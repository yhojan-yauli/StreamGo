package com.StreamGo.dao.impl;

import com.StreamGo.dao.NoticiaDAO;
import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.Noticia;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class NoticiaDAOImpl extends AbstractGenericJdbcDAO<Noticia, Long>
        implements NoticiaDAO {

    public NoticiaDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "noticias";
    }

    @Override
    protected String getIdColumnName() {
        return "id_post";
    }

    @Override
    public void save(Noticia noticia) {

        String sql = """
                INSERT INTO noticias (
                    id_autor, id_usuario, titulo, reacciones,
                    trailer, contenido, fijado
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idAutor(noticia));
                    preparedStatement.setObject(2, idUsuario(noticia));
                    preparedStatement.setString(3, noticia.getTitulo());
                    preparedStatement.setObject(4, noticia.getReacciones());
                    preparedStatement.setString(5, noticia.getTrailer());
                    preparedStatement.setString(6, noticia.getContenido());
                    preparedStatement.setBoolean(7, noticia.isFijado());
                }
        );

        noticia.setIdPost(id);
    }

    @Override
    public void update(Noticia noticia) {

        String sql = """
                UPDATE noticias
                SET id_autor = ?, id_usuario = ?, titulo = ?,
                    reacciones = ?, trailer = ?, contenido = ?, fijado = ?
                WHERE id_post = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idAutor(noticia));
                    preparedStatement.setObject(2, idUsuario(noticia));
                    preparedStatement.setString(3, noticia.getTitulo());
                    preparedStatement.setObject(4, noticia.getReacciones());
                    preparedStatement.setString(5, noticia.getTrailer());
                    preparedStatement.setString(6, noticia.getContenido());
                    preparedStatement.setBoolean(7, noticia.isFijado());
                    preparedStatement.setLong(8, noticia.getIdPost());
                }
        );
    }

    public List<Noticia> findByAutorId(Long idAutor) {

        return queryForList(
                "SELECT * FROM noticias WHERE id_autor = ?",
                preparedStatement -> preparedStatement.setLong(1, idAutor),
                this::mapResultSet
        );
    }

    public List<Noticia> findByUsuarioId(Long idUsuario) {

        return queryForList(
                "SELECT * FROM noticias WHERE id_usuario = ?",
                preparedStatement -> preparedStatement.setLong(1, idUsuario),
                this::mapResultSet
        );
    }

    public List<Noticia> findAllByOrderByFijadoDescReaccionesDesc() {

        return queryForList(
                """
                        SELECT * FROM noticias
                        ORDER BY fijado DESC, reacciones DESC
                        """,
                preparedStatement -> {
                },
                this::mapResultSet
        );
    }

    @Override
    protected Noticia mapResultSet(ResultSet resultSet) throws SQLException {

        return Noticia.builder()
                .idPost(resultSet.getLong("id_post"))
                .autor(Usuario.builder()
                        .id(getLongOrNull(resultSet, "id_autor"))
                        .build())
                .usuario(Usuario.builder()
                        .id(getLongOrNull(resultSet, "id_usuario"))
                        .build())
                .titulo(resultSet.getString("titulo"))
                .reacciones(getIntegerOrNull(resultSet, "reacciones"))
                .trailer(resultSet.getString("trailer"))
                .contenido(resultSet.getString("contenido"))
                .fijado(resultSet.getBoolean("fijado"))
                .build();
    }

    private Long idAutor(Noticia noticia) {
        return noticia.getAutor() == null ? null : noticia.getAutor().getId();
    }

    private Long idUsuario(Noticia noticia) {
        return noticia.getUsuario() == null ? null : noticia.getUsuario().getId();
    }
}
