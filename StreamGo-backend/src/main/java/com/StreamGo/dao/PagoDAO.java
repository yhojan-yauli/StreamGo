package com.StreamGo.dao;

import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.Enum.EstadoPago;
import com.StreamGo.entity.Pago;
import com.StreamGo.entity.Plan;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PagoDAO extends AbstractGenericJdbcDAO<Pago, Long> {

    public PagoDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "pagos";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(Pago pago) {

        String sql = """
                INSERT INTO pagos (
                    usuario_id, plan_id, monto, estado_pago,
                    transaction_id, metodo_pago, fecha_pago,
                    mercado_pago_payment_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(pago));
                    preparedStatement.setObject(2, idPlan(pago));
                    preparedStatement.setBigDecimal(3, pago.getMonto());
                    preparedStatement.setString(4, enumName(pago.getEstadoPago()));
                    preparedStatement.setString(5, pago.getTransactionId());
                    preparedStatement.setString(6, pago.getMetodoPago());
                    preparedStatement.setObject(7, pago.getFechaPago());
                    preparedStatement.setString(8, pago.getMercadoPagoPaymentId());
                }
        );

        pago.setId(id);
    }

    @Override
    public void update(Pago pago) {

        String sql = """
                UPDATE pagos
                SET usuario_id = ?, plan_id = ?, monto = ?,
                    estado_pago = ?, transaction_id = ?, metodo_pago = ?,
                    fecha_pago = ?, mercado_pago_payment_id = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(pago));
                    preparedStatement.setObject(2, idPlan(pago));
                    preparedStatement.setBigDecimal(3, pago.getMonto());
                    preparedStatement.setString(4, enumName(pago.getEstadoPago()));
                    preparedStatement.setString(5, pago.getTransactionId());
                    preparedStatement.setString(6, pago.getMetodoPago());
                    preparedStatement.setObject(7, pago.getFechaPago());
                    preparedStatement.setString(8, pago.getMercadoPagoPaymentId());
                    preparedStatement.setLong(9, pago.getId());
                }
        );
    }

    @Override
    protected Pago mapResultSet(ResultSet resultSet) throws SQLException {

        return Pago.builder()
                .id(resultSet.getLong("id"))
                .usuario(Usuario.builder()
                        .id(getLongOrNull(resultSet, "usuario_id"))
                        .build())
                .plan(Plan.builder()
                        .id(getLongOrNull(resultSet, "plan_id"))
                        .build())
                .monto(resultSet.getBigDecimal("monto"))
                .estadoPago(enumValue(
                        EstadoPago.class,
                        resultSet.getString("estado_pago")
                ))
                .transactionId(resultSet.getString("transaction_id"))
                .metodoPago(resultSet.getString("metodo_pago"))
                .fechaPago(getLocalDateTimeOrNull(
                        resultSet,
                        "fecha_pago"
                ))
                .mercadoPagoPaymentId(resultSet.getString(
                        "mercado_pago_payment_id"
                ))
                .build();
    }

    private Long idUsuario(Pago pago) {
        return pago.getUsuario() == null ? null : pago.getUsuario().getId();
    }

    private Long idPlan(Pago pago) {
        return pago.getPlan() == null ? null : pago.getPlan().getId();
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
