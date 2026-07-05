package com.StreamGo.dao.impl;

import com.StreamGo.dao.NoticiaDAO;
import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.dto.query.NoticiaQuery;
import com.StreamGo.dto.response.PageResponse;
import com.StreamGo.entity.Noticia;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public List<Noticia> findAll() {

        return queryForList(
                selectNoticiasConUsuarios(),
                preparedStatement -> {
                },
                this::mapResultSet
        );
    }

    @Override
    public Noticia findById(Long idPost) {

        return queryForOptional(
                selectNoticiasConUsuarios() + " WHERE n.id_post = ?",
                preparedStatement -> preparedStatement.setLong(1, idPost),
                this::mapResultSet
        ).orElseThrow(() -> new RuntimeException("noticias no encontrado"));
    }

    @Override
    public PageResponse<Noticia> findAll(NoticiaQuery query) {
        return buscarPaginado(query);
    }

    @Override
    public PageResponse<Noticia> findAdminAll(NoticiaQuery query) {
        return buscarPaginado(query);
    }

    @Override
    public long count(NoticiaQuery query) {
        SqlFiltros filtros = construirFiltros(query);
        String sql = """
                SELECT COUNT(*)
                FROM noticias n
                LEFT JOIN usuarios autor ON autor.id = n.id_autor
                %s
                """.formatted(filtros.where());

        return queryForOptional(
                sql,
                preparedStatement -> asignarParametros(preparedStatement, filtros.parametros()),
                resultSet -> resultSet.getLong(1)
        ).orElse(0L);
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
                selectNoticiasConUsuarios() + " WHERE n.id_autor = ?",
                preparedStatement -> preparedStatement.setLong(1, idAutor),
                this::mapResultSet
        );
    }

    public List<Noticia> findByUsuarioId(Long idUsuario) {

        return queryForList(
                selectNoticiasConUsuarios() + " WHERE n.id_usuario = ?",
                preparedStatement -> preparedStatement.setLong(1, idUsuario),
                this::mapResultSet
        );
    }

    public List<Noticia> findAllByOrderByFijadoDescReaccionesDesc() {

        return queryForList(
                """
                        %s
                        ORDER BY n.fijado DESC, n.reacciones DESC
                        """.formatted(selectNoticiasConUsuarios()),
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
                        .nombre(resultSet.getString("autor_nombre"))
                        .build())
                .usuario(Usuario.builder()
                        .id(getLongOrNull(resultSet, "id_usuario"))
                        .nombre(resultSet.getString("usuario_nombre"))
                        .build())
                .titulo(resultSet.getString("titulo"))
                .reacciones(getIntegerOrNull(resultSet, "reacciones"))
                .trailer(resultSet.getString("trailer"))
                .contenido(resultSet.getString("contenido"))
                .fijado(resultSet.getBoolean("fijado"))
                .build();
    }

    private String selectNoticiasConUsuarios() {

        return """
                SELECT n.id_post, n.id_autor, n.id_usuario, n.titulo,
                       n.reacciones, n.trailer, n.contenido, n.fijado,
                       autor.nombre AS autor_nombre,
                       usuario.nombre AS usuario_nombre
                FROM noticias n
                LEFT JOIN usuarios autor ON autor.id = n.id_autor
                LEFT JOIN usuarios usuario ON usuario.id = n.id_usuario
                """;
    }

    private PageResponse<Noticia> buscarPaginado(NoticiaQuery query) {
        SqlFiltros filtros = construirFiltros(query);
        List<Object> parametros = new ArrayList<>(filtros.parametros());
        parametros.add(query.getSize());
        parametros.add(query.getOffset());

        String sql = """
                %s
                %s
                %s
                LIMIT ? OFFSET ?
                """.formatted(
                selectNoticiasConUsuarios(),
                filtros.where(),
                construirOrderBy(query)
        );

        List<Noticia> noticias = queryForList(
                sql,
                preparedStatement -> asignarParametros(preparedStatement, parametros),
                this::mapResultSet
        );

        return PageResponse.of(
                noticias,
                query.getPage(),
                query.getSize(),
                count(query)
        );
    }

    private SqlFiltros construirFiltros(NoticiaQuery query) {
        List<String> condiciones = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();

        if (query.tieneBusqueda()) {
            condiciones.add("""
                    (
                        LOWER(n.titulo) LIKE ?
                        OR LOWER(n.contenido) LIKE ?
                        OR LOWER(autor.nombre) LIKE ?
                    )
                    """);

            String busqueda = "%" + query.getSearch().toLowerCase() + "%";
            parametros.add(busqueda);
            parametros.add(busqueda);
            parametros.add(busqueda);
        }

        if (query.esEstadoFijadas()) {
            condiciones.add("n.fijado = ?");
            parametros.add(true);
        }

        if (query.esEstadoNormales()) {
            condiciones.add("n.fijado = ?");
            parametros.add(false);
        }

        String where = condiciones.isEmpty()
                ? ""
                : "WHERE " + String.join(" AND ", condiciones);

        return new SqlFiltros(where, parametros);
    }

    private String construirOrderBy(NoticiaQuery query) {
        return switch (query.getSort()) {
            case NoticiaQuery.SORT_REACCIONES -> "ORDER BY n.reacciones DESC, n.id_post DESC";
            case NoticiaQuery.SORT_TITULO -> "ORDER BY n.titulo ASC";
            default -> "ORDER BY n.id_post DESC";
        };
    }

    private void asignarParametros(
            java.sql.PreparedStatement preparedStatement,
            List<Object> parametros
    ) throws SQLException {

        for (int index = 0; index < parametros.size(); index++) {
            preparedStatement.setObject(index + 1, parametros.get(index));
        }
    }

    private record SqlFiltros(
            String where,
            List<Object> parametros
    ) {
    }

    private Long idAutor(Noticia noticia) {
        return noticia.getAutor() == null ? null : noticia.getAutor().getId();
    }

    private Long idUsuario(Noticia noticia) {
        return noticia.getUsuario() == null ? null : noticia.getUsuario().getId();
    }
}
