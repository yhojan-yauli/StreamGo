package com.StreamGo.dao.impl;

import com.StreamGo.dao.PeticionDAO;
import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.ContenidoVotable;
import com.StreamGo.entity.Peticion;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PeticionDAOImpl extends AbstractGenericJdbcDAO<Peticion, Long>
        implements PeticionDAO {

    public PeticionDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "peticiones";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public List<Peticion> findAll() {

        return queryForList(
                selectPeticionesConVotable(),
                preparedStatement -> {
                },
                this::mapResultSet
        );
    }

    @Override
    public Peticion findById(Long id) {

        return queryForOptional(
                selectPeticionesConVotable() + " WHERE p.id = ?",
                preparedStatement -> preparedStatement.setLong(1, id),
                this::mapResultSet
        ).orElseThrow(() -> new RuntimeException("peticiones no encontrado"));
    }

    @Override
    public void save(Peticion peticion) {

        String sql = """
                INSERT INTO peticiones (
                    usuario_id, contenido_votable_id, titulo,
                    descripcion, imagen_url, fecha_peticion
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(peticion));
                    preparedStatement.setObject(2, idContenidoVotable(peticion));
                    preparedStatement.setString(3, tituloContenidoVotable(peticion));
                    preparedStatement.setString(4, descripcionContenidoVotable(peticion));
                    preparedStatement.setString(5, imagenUrlContenidoVotable(peticion));
                    preparedStatement.setObject(6, peticion.getFechaPeticion());
                }
        );

        peticion.setId(id);
    }

    @Override
    public void update(Peticion peticion) {

        String sql = """
                UPDATE peticiones
                SET usuario_id = ?, contenido_votable_id = ?,
                    titulo = ?, descripcion = ?, imagen_url = ?,
                    fecha_peticion = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(peticion));
                    preparedStatement.setObject(2, idContenidoVotable(peticion));
                    preparedStatement.setString(3, tituloContenidoVotable(peticion));
                    preparedStatement.setString(4, descripcionContenidoVotable(peticion));
                    preparedStatement.setString(5, imagenUrlContenidoVotable(peticion));
                    preparedStatement.setObject(6, peticion.getFechaPeticion());
                    preparedStatement.setLong(7, peticion.getId());
                }
        );
    }

    public Optional<Peticion> findByUsuarioId(Long usuarioId) {

        return queryForOptional(
                selectPeticionesConVotable() + " WHERE p.usuario_id = ?",
                preparedStatement -> preparedStatement.setLong(1, usuarioId),
                this::mapResultSet
        );
    }

    public List<Object[]> contarVotosPorContenido() {

        return queryForList(
                """
                        SELECT cv.id AS contenido_id,
                               cv.titulo AS titulo,
                               COUNT(p.id) AS total
                        FROM peticiones p
                        INNER JOIN contenidos_votables cv
                                ON cv.id = p.contenido_votable_id
                        GROUP BY cv.id, cv.titulo
                        ORDER BY COUNT(p.id) DESC
                        """,
                preparedStatement -> {
                },
                resultSet -> new Object[]{
                        resultSet.getLong("contenido_id"),
                        resultSet.getString("titulo"),
                        resultSet.getLong("total")
                }
        );
    }

    public long countByContenidoVotableId(Long contenidoVotableId) {

        return queryForOptional(
                """
                        SELECT COUNT(*) AS total
                        FROM peticiones
                        WHERE contenido_votable_id = ?
                        """,
                preparedStatement -> preparedStatement.setLong(
                        1,
                        contenidoVotableId
                ),
                resultSet -> resultSet.getLong("total")
        ).orElse(0L);
    }

    @Override
    protected Peticion mapResultSet(ResultSet resultSet) throws SQLException {

        return Peticion.builder()
                .id(resultSet.getLong("id"))
                .usuario(Usuario.builder()
                        .id(getLongOrNull(resultSet, "usuario_id"))
                        .build())
                .contenidoVotable(ContenidoVotable.builder()
                        .id(getLongOrNull(
                                resultSet,
                                "contenido_votable_id"
                        ))
                        .titulo(getStringOrNull(
                                resultSet,
                                "contenido_votable_titulo"
                        ))
                        .descripcion(getStringOrNull(
                                resultSet,
                                "contenido_votable_descripcion"
                        ))
                        .posterUrl(getStringOrNull(
                                resultSet,
                                "contenido_votable_poster_url"
                        ))
                        .imagenUrl(getStringOrNull(
                                resultSet,
                                "contenido_votable_imagen_url"
                        ))
                        .activo(getBooleanOrNull(
                                resultSet,
                                "contenido_votable_activo"
                        ))
                        .build())
                .fechaPeticion(getLocalDateTimeOrNull(
                        resultSet,
                        "fecha_peticion"
                ))
                .build();
    }

    private String selectPeticionesConVotable() {

        return """
                SELECT p.id, p.usuario_id, p.contenido_votable_id,
                       p.fecha_peticion,
                       cv.titulo AS contenido_votable_titulo,
                       cv.descripcion AS contenido_votable_descripcion,
                       cv.poster_url AS contenido_votable_poster_url,
                       cv.imagen_url AS contenido_votable_imagen_url,
                       cv.activo AS contenido_votable_activo
                FROM peticiones p
                LEFT JOIN contenidos_votables cv
                       ON cv.id = p.contenido_votable_id
                """;
    }

    private Long idUsuario(Peticion peticion) {
        return peticion.getUsuario() == null
                ? null
                : peticion.getUsuario().getId();
    }

    private Long idContenidoVotable(Peticion peticion) {
        return peticion.getContenidoVotable() == null
                ? null
                : peticion.getContenidoVotable().getId();
    }

    private String tituloContenidoVotable(Peticion peticion) {
        return peticion.getContenidoVotable() == null
                ? null
                : peticion.getContenidoVotable().getTitulo();
    }

    private String descripcionContenidoVotable(Peticion peticion) {
        return peticion.getContenidoVotable() == null
                ? null
                : peticion.getContenidoVotable().getDescripcion();
    }

    private String imagenUrlContenidoVotable(Peticion peticion) {
        return peticion.getContenidoVotable() == null
                ? null
                : peticion.getContenidoVotable().getImagenUrl();
    }
}
