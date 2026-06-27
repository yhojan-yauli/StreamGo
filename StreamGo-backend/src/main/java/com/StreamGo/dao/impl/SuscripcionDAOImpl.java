package com.StreamGo.dao.impl;

import com.StreamGo.dao.SuscripcionDAO;
import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Plan;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class SuscripcionDAOImpl
        extends AbstractGenericJdbcDAO<Suscripcion, Long>
        implements SuscripcionDAO {

    public SuscripcionDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "suscripciones";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(Suscripcion suscripcion) {

        String sql = """
                INSERT INTO suscripciones (
                    usuario_id, plan_id, fecha_inicio, fecha_fin,
                    horas_restantes, estado
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(suscripcion));
                    preparedStatement.setObject(2, idPlan(suscripcion));
                    preparedStatement.setObject(3, suscripcion.getFechaInicio());
                    preparedStatement.setObject(4, suscripcion.getFechaFin());
                    preparedStatement.setObject(5, suscripcion.getHorasRestantes());
                    preparedStatement.setString(6, enumName(suscripcion.getEstado()));
                }
        );

        suscripcion.setId(id);
    }

    @Override
    public void update(Suscripcion suscripcion) {

        String sql = """
                UPDATE suscripciones
                SET usuario_id = ?, plan_id = ?, fecha_inicio = ?,
                    fecha_fin = ?, horas_restantes = ?, estado = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(suscripcion));
                    preparedStatement.setObject(2, idPlan(suscripcion));
                    preparedStatement.setObject(3, suscripcion.getFechaInicio());
                    preparedStatement.setObject(4, suscripcion.getFechaFin());
                    preparedStatement.setObject(5, suscripcion.getHorasRestantes());
                    preparedStatement.setString(6, enumName(suscripcion.getEstado()));
                    preparedStatement.setLong(7, suscripcion.getId());
                }
        );
    }

    public List<Suscripcion> findByUsuarioId(Long usuarioId) {

        return queryForList(
                "SELECT * FROM suscripciones WHERE usuario_id = ?",
                preparedStatement -> preparedStatement.setLong(1, usuarioId),
                this::mapResultSet
        );
    }

    public List<Suscripcion> findByUsuario(Usuario usuario) {

        return findByUsuarioId(usuario.getId());
    }

    public List<Suscripcion> findByUsuarioIdAndEstado(
            Long usuarioId,
            EstadoSuscripcion estado
    ) {

        return queryForList(
                "SELECT * FROM suscripciones WHERE usuario_id = ? AND estado = ?",
                preparedStatement -> {
                    preparedStatement.setLong(1, usuarioId);
                    preparedStatement.setString(2, enumName(estado));
                },
                this::mapResultSet
        );
    }

    public List<Suscripcion> findByUsuarioAndEstado(
            Usuario usuario,
            EstadoSuscripcion estado
    ) {

        return findByUsuarioIdAndEstado(usuario.getId(), estado);
    }

    public Optional<Suscripcion> findTopByUsuarioIdOrderByFechaFinDesc(
            Long usuarioId
    ) {

        return queryForOptional(
                """
                        SELECT * FROM suscripciones
                        WHERE usuario_id = ?
                        ORDER BY fecha_fin DESC
                        LIMIT 1
                        """,
                preparedStatement -> preparedStatement.setLong(1, usuarioId),
                this::mapResultSet
        );
    }

    public Optional<Suscripcion> findTopByUsuarioIdAndEstadoOrderByFechaFinDesc(
            Long usuarioId,
            EstadoSuscripcion estado
    ) {

        return queryForOptional(
                """
                        SELECT * FROM suscripciones
                        WHERE usuario_id = ? AND estado = ?
                        ORDER BY fecha_fin DESC
                        LIMIT 1
                        """,
                preparedStatement -> {
                    preparedStatement.setLong(1, usuarioId);
                    preparedStatement.setString(2, enumName(estado));
                },
                this::mapResultSet
        );
    }

    public List<Suscripcion> findByEstado(EstadoSuscripcion estado) {

        return queryForList(
                "SELECT * FROM suscripciones WHERE estado = ?",
                preparedStatement -> preparedStatement.setString(
                        1,
                        enumName(estado)
                ),
                this::mapResultSet
        );
    }

    @Override
    protected Suscripcion mapResultSet(ResultSet resultSet)
            throws SQLException {

        return Suscripcion.builder()
                .id(resultSet.getLong("id"))
                .usuario(Usuario.builder()
                        .id(getLongOrNull(resultSet, "usuario_id"))
                        .build())
                .plan(Plan.builder()
                        .id(getLongOrNull(resultSet, "plan_id"))
                        .build())
                .fechaInicio(getLocalDateTimeOrNull(
                        resultSet,
                        "fecha_inicio"
                ))
                .fechaFin(getLocalDateTimeOrNull(
                        resultSet,
                        "fecha_fin"
                ))
                .horasRestantes(getIntegerOrNull(
                        resultSet,
                        "horas_restantes"
                ))
                .estado(enumValue(
                        EstadoSuscripcion.class,
                        resultSet.getString("estado")
                ))
                .build();
    }

    private Long idUsuario(Suscripcion suscripcion) {
        return suscripcion.getUsuario() == null
                ? null
                : suscripcion.getUsuario().getId();
    }

    private Long idPlan(Suscripcion suscripcion) {
        return suscripcion.getPlan() == null
                ? null
                : suscripcion.getPlan().getId();
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
