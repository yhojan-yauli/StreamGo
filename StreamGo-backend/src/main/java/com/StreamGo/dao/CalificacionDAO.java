package com.StreamGo.dao;

import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.CalificacionContenido;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CalificacionDAO
        extends AbstractGenericJdbcDAO<CalificacionContenido, Long> {

    public CalificacionDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "calificaciones_contenido";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(CalificacionContenido calificacion) {

        String sql = """
                INSERT INTO calificaciones_contenido (
                    usuario_id, contenido_id, puntaje, comentario,
                    fecha_calificacion
                ) VALUES (?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(calificacion));
                    preparedStatement.setObject(2, idContenido(calificacion));
                    preparedStatement.setObject(3, calificacion.getPuntaje());
                    preparedStatement.setString(4, calificacion.getComentario());
                    preparedStatement.setObject(5, calificacion.getFechaCalificacion());
                }
        );

        calificacion.setId(id);
    }

    @Override
    public void update(CalificacionContenido calificacion) {

        String sql = """
                UPDATE calificaciones_contenido
                SET usuario_id = ?, contenido_id = ?, puntaje = ?,
                    comentario = ?, fecha_calificacion = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(calificacion));
                    preparedStatement.setObject(2, idContenido(calificacion));
                    preparedStatement.setObject(3, calificacion.getPuntaje());
                    preparedStatement.setString(4, calificacion.getComentario());
                    preparedStatement.setObject(5, calificacion.getFechaCalificacion());
                    preparedStatement.setLong(6, calificacion.getId());
                }
        );
    }

    public Optional<CalificacionContenido> findByUsuarioAndContenido(
            Usuario usuario,
            Contenido contenido
    ) {

        return queryForOptional(
                """
                        SELECT * FROM calificaciones_contenido
                        WHERE usuario_id = ? AND contenido_id = ?
                        """,
                preparedStatement -> {
                    preparedStatement.setLong(1, usuario.getId());
                    preparedStatement.setLong(2, contenido.getId());
                },
                this::mapResultSet
        );
    }

    public List<CalificacionContenido> findByContenido(
            Contenido contenido
    ) {

        return queryForList(
                "SELECT * FROM calificaciones_contenido WHERE contenido_id = ?",
                preparedStatement -> preparedStatement.setLong(
                        1,
                        contenido.getId()
                ),
                this::mapResultSet
        );
    }

    @Override
    protected CalificacionContenido mapResultSet(ResultSet resultSet)
            throws SQLException {

        return CalificacionContenido.builder()
                .id(resultSet.getLong("id"))
                .usuario(Usuario.builder()
                        .id(getLongOrNull(resultSet, "usuario_id"))
                        .build())
                .contenido(Contenido.builder()
                        .id(getLongOrNull(resultSet, "contenido_id"))
                        .build())
                .puntaje(getIntegerOrNull(resultSet, "puntaje"))
                .comentario(resultSet.getString("comentario"))
                .fechaCalificacion(getLocalDateTimeOrNull(
                        resultSet,
                        "fecha_calificacion"
                ))
                .build();
    }

    private Long idUsuario(CalificacionContenido calificacion) {
        return calificacion.getUsuario() == null
                ? null
                : calificacion.getUsuario().getId();
    }

    private Long idContenido(CalificacionContenido calificacion) {
        return calificacion.getContenido() == null
                ? null
                : calificacion.getContenido().getId();
    }
}
