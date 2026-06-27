package com.StreamGo.dao;

import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.Enum.TipoPlan;
import com.StreamGo.entity.Plan;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class PlanDAO extends AbstractGenericJdbcDAO<Plan, Long> {

    public PlanDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "planes";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(Plan plan) {

        String sql = """
                INSERT INTO planes (
                    tipo_plan, nombre, precio, duracion_horas,
                    descripcion, activo, personalizado
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setString(1, enumName(plan.getTipoPlan()));
                    preparedStatement.setString(2, plan.getNombre());
                    preparedStatement.setBigDecimal(3, plan.getPrecio());
                    preparedStatement.setObject(4, plan.getDuracionHoras());
                    preparedStatement.setString(5, plan.getDescripcion());
                    preparedStatement.setObject(6, plan.getActivo());
                    preparedStatement.setObject(7, plan.getPersonalizado());
                }
        );

        plan.setId(id);
    }

    @Override
    public void update(Plan plan) {

        String sql = """
                UPDATE planes
                SET tipo_plan = ?, nombre = ?, precio = ?,
                    duracion_horas = ?, descripcion = ?, activo = ?,
                    personalizado = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setString(1, enumName(plan.getTipoPlan()));
                    preparedStatement.setString(2, plan.getNombre());
                    preparedStatement.setBigDecimal(3, plan.getPrecio());
                    preparedStatement.setObject(4, plan.getDuracionHoras());
                    preparedStatement.setString(5, plan.getDescripcion());
                    preparedStatement.setObject(6, plan.getActivo());
                    preparedStatement.setObject(7, plan.getPersonalizado());
                    preparedStatement.setLong(8, plan.getId());
                }
        );
    }

    public Optional<Plan> findByPrecioAndPersonalizadoTrue(Double precio) {

        return queryForOptional(
                "SELECT * FROM planes WHERE precio = ? AND personalizado = true",
                preparedStatement -> preparedStatement.setBigDecimal(
                        1,
                        BigDecimal.valueOf(precio)
                ),
                this::mapResultSet
        );
    }

    @Override
    protected Plan mapResultSet(ResultSet resultSet) throws SQLException {

        return Plan.builder()
                .id(resultSet.getLong("id"))
                .tipoPlan(enumValue(
                        TipoPlan.class,
                        resultSet.getString("tipo_plan")
                ))
                .nombre(resultSet.getString("nombre"))
                .precio(resultSet.getBigDecimal("precio"))
                .duracionHoras(getIntegerOrNull(
                        resultSet,
                        "duracion_horas"
                ))
                .descripcion(resultSet.getString("descripcion"))
                .activo(getBooleanOrNull(resultSet, "activo"))
                .personalizado(getBooleanOrNull(resultSet, "personalizado"))
                .build();
    }

    private String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private <E extends Enum<E>> E enumValue(
            Class<E> enumType,
            String value
    ) {

        return value == null ? null : Enum.valueOf(enumType, value);
    }
}
