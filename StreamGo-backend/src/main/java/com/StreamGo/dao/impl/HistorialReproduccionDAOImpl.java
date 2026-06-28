package com.StreamGo.dao.impl;

import com.StreamGo.dao.HistorialReproduccionDAO;
import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class HistorialReproduccionDAOImpl
        extends AbstractGenericJdbcDAO<HistorialReproduccion, Long>
        implements HistorialReproduccionDAO {

    public HistorialReproduccionDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "historial_reproducciones";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(HistorialReproduccion historial) {

        String sql = """
                INSERT INTO historial_reproducciones (
                    usuario_id, contenido_id, fecha_reproduccion,
                    progreso_segundos, completado
                ) VALUES (?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(historial));
                    preparedStatement.setObject(2, idContenido(historial));
                    preparedStatement.setObject(3, historial.getFechaReproduccion());
                    preparedStatement.setObject(4, historial.getProgresoSegundos());
                    preparedStatement.setObject(5, historial.getCompletado());
                }
        );

        historial.setId(id);
    }

    @Override
    public void update(HistorialReproduccion historial) {

        String sql = """
                UPDATE historial_reproducciones
                SET usuario_id = ?, contenido_id = ?,
                    fecha_reproduccion = ?, progreso_segundos = ?,
                    completado = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(historial));
                    preparedStatement.setObject(2, idContenido(historial));
                    preparedStatement.setObject(3, historial.getFechaReproduccion());
                    preparedStatement.setObject(4, historial.getProgresoSegundos());
                    preparedStatement.setObject(5, historial.getCompletado());
                    preparedStatement.setLong(6, historial.getId());
                }
        );
    }

    public List<HistorialReproduccion> findByUsuarioOrderByFechaReproduccionDesc(
            Usuario usuario
    ) {

        return queryForList(
                """
                        SELECT h.id, h.usuario_id, h.contenido_id,
                               h.fecha_reproduccion, h.progreso_segundos,
                               h.completado, c.titulo AS contenido_titulo,
                               c.imagen_url AS contenido_imagen_url,
                               c.categoria AS contenido_categoria
                        FROM historial_reproducciones h
                        LEFT JOIN contenidos c ON c.id = h.contenido_id
                        WHERE h.usuario_id = ?
                        ORDER BY h.fecha_reproduccion DESC
                        """,
                preparedStatement -> preparedStatement.setLong(
                        1,
                        usuario.getId()
                ),
                this::mapResultSet
        );
    }

    @Override
    protected HistorialReproduccion mapResultSet(ResultSet resultSet)
            throws SQLException {

        return HistorialReproduccion.builder()
                .id(resultSet.getLong("id"))
                .usuario(Usuario.builder()
                        .id(getLongOrNull(resultSet, "usuario_id"))
                        .build())
                .contenido(Contenido.builder()
                        .id(getLongOrNull(resultSet, "contenido_id"))
                        .titulo(getStringOrNull(
                                resultSet,
                                "contenido_titulo"
                        ))
                        .imagenUrl(getStringOrNull(
                                resultSet,
                                "contenido_imagen_url"
                        ))
                        .categoria(getStringOrNull(
                                resultSet,
                                "contenido_categoria"
                        ))
                        .build())
                .fechaReproduccion(getLocalDateTimeOrNull(
                        resultSet,
                        "fecha_reproduccion"
                ))
                .progresoSegundos(getIntegerOrNull(
                        resultSet,
                        "progreso_segundos"
                ))
                .completado(getBooleanOrNull(resultSet, "completado"))
                .build();
    }

    private Long idUsuario(HistorialReproduccion historial) {
        return historial.getUsuario() == null
                ? null
                : historial.getUsuario().getId();
    }

    private Long idContenido(HistorialReproduccion historial) {
        return historial.getContenido() == null
                ? null
                : historial.getContenido().getId();
    }
}
