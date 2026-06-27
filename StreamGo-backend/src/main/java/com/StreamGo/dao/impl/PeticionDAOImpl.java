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
    public void save(Peticion peticion) {

        String sql = """
                INSERT INTO peticiones (
                    usuario_id, contenido_votable_id, fecha_peticion
                ) VALUES (?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(peticion));
                    preparedStatement.setObject(2, idContenidoVotable(peticion));
                    preparedStatement.setObject(3, peticion.getFechaPeticion());
                }
        );

        peticion.setId(id);
    }

    @Override
    public void update(Peticion peticion) {

        String sql = """
                UPDATE peticiones
                SET usuario_id = ?, contenido_votable_id = ?,
                    fecha_peticion = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(peticion));
                    preparedStatement.setObject(2, idContenidoVotable(peticion));
                    preparedStatement.setObject(3, peticion.getFechaPeticion());
                    preparedStatement.setLong(4, peticion.getId());
                }
        );
    }

    public Optional<Peticion> findByUsuarioId(Long usuarioId) {

        return queryForOptional(
                "SELECT * FROM peticiones WHERE usuario_id = ?",
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
                        .build())
                .fechaPeticion(getLocalDateTimeOrNull(
                        resultSet,
                        "fecha_peticion"
                ))
                .build();
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
}
