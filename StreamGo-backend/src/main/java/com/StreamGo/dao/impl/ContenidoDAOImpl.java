package com.StreamGo.dao.impl;

import com.StreamGo.dao.ContenidoDAO;
import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.TipoContenido;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ContenidoDAOImpl extends AbstractGenericJdbcDAO<Contenido, Long>
        implements ContenidoDAO {

    public ContenidoDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "contenidos";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(Contenido contenido) {

        String sql = """
                INSERT INTO contenidos (
                    titulo, descripcion, categoria, tipo_contenido,
                    imagen_url, banner_url, video_url, fecha_estreno,
                    duracion_minutos, gratuito, recomendado, tendencia,
                    promedio_calificacion, total_calificaciones,
                    total_reproducciones, estado
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> bindContenido(
                        preparedStatement,
                        contenido,
                        false
                )
        );

        contenido.setId(id);
    }

    @Override
    public void update(Contenido contenido) {

        String sql = """
                UPDATE contenidos
                SET titulo = ?, descripcion = ?, categoria = ?,
                    tipo_contenido = ?, imagen_url = ?, banner_url = ?,
                    video_url = ?, fecha_estreno = ?, duracion_minutos = ?,
                    gratuito = ?, recomendado = ?, tendencia = ?,
                    promedio_calificacion = ?, total_calificaciones = ?,
                    total_reproducciones = ?, estado = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> bindContenido(
                        preparedStatement,
                        contenido,
                        true
                )
        );
    }

    public List<Contenido> findByEstado(EstadoContenido estado) {

        return queryForList(
                "SELECT * FROM contenidos WHERE estado = ?",
                preparedStatement -> preparedStatement.setString(
                        1,
                        enumName(estado)
                ),
                this::mapResultSet
        );
    }

    public List<Contenido> findByCategoriaAndEstado(
            String categoria,
            EstadoContenido estado
    ) {

        return queryForList(
                "SELECT * FROM contenidos WHERE categoria = ? AND estado = ?",
                preparedStatement -> {
                    preparedStatement.setString(1, categoria);
                    preparedStatement.setString(2, enumName(estado));
                },
                this::mapResultSet
        );
    }

    public List<Contenido> findByRecomendadoTrueAndEstado(
            EstadoContenido estado
    ) {

        return queryForList(
                "SELECT * FROM contenidos WHERE recomendado = true AND estado = ?",
                preparedStatement -> preparedStatement.setString(
                        1,
                        enumName(estado)
                ),
                this::mapResultSet
        );
    }

    public List<Contenido> findByTendenciaTrueAndEstado(
            EstadoContenido estado
    ) {

        return queryForList(
                "SELECT * FROM contenidos WHERE tendencia = true AND estado = ?",
                preparedStatement -> preparedStatement.setString(
                        1,
                        enumName(estado)
                ),
                this::mapResultSet
        );
    }

    public List<Contenido> findByTituloContainingIgnoreCaseAndEstado(
            String titulo,
            EstadoContenido estado
    ) {

        return queryForList(
                """
                        SELECT * FROM contenidos
                        WHERE LOWER(titulo) LIKE LOWER(?)
                          AND estado = ?
                        """,
                preparedStatement -> {
                    preparedStatement.setString(1, "%" + titulo + "%");
                    preparedStatement.setString(2, enumName(estado));
                },
                this::mapResultSet
        );
    }

    @Override
    protected Contenido mapResultSet(ResultSet resultSet)
            throws SQLException {

        return Contenido.builder()
                .id(resultSet.getLong("id"))
                .titulo(resultSet.getString("titulo"))
                .descripcion(resultSet.getString("descripcion"))
                .categoria(resultSet.getString("categoria"))
                .tipoContenido(enumValue(
                        TipoContenido.class,
                        resultSet.getString("tipo_contenido")
                ))
                .imagenUrl(resultSet.getString("imagen_url"))
                .bannerUrl(resultSet.getString("banner_url"))
                .videoUrl(resultSet.getString("video_url"))
                .fechaEstreno(getLocalDateOrNull(
                        resultSet,
                        "fecha_estreno"
                ))
                .duracionMinutos(getIntegerOrNull(
                        resultSet,
                        "duracion_minutos"
                ))
                .gratuito(getBooleanOrNull(resultSet, "gratuito"))
                .recomendado(getBooleanOrNull(resultSet, "recomendado"))
                .tendencia(getBooleanOrNull(resultSet, "tendencia"))
                .promedioCalificacion(getDoubleOrNull(
                        resultSet,
                        "promedio_calificacion"
                ))
                .totalCalificaciones(getIntegerOrNull(
                        resultSet,
                        "total_calificaciones"
                ))
                .totalReproducciones(getIntegerOrNull(
                        resultSet,
                        "total_reproducciones"
                ))
                .estado(enumValue(
                        EstadoContenido.class,
                        resultSet.getString("estado")
                ))
                .build();
    }

    private void bindContenido(
            java.sql.PreparedStatement preparedStatement,
            Contenido contenido,
            boolean includeId
    ) throws SQLException {

        preparedStatement.setString(1, contenido.getTitulo());
        preparedStatement.setString(2, contenido.getDescripcion());
        preparedStatement.setString(3, contenido.getCategoria());
        preparedStatement.setString(4, enumName(contenido.getTipoContenido()));
        preparedStatement.setString(5, contenido.getImagenUrl());
        preparedStatement.setString(6, contenido.getBannerUrl());
        preparedStatement.setString(7, contenido.getVideoUrl());
        preparedStatement.setObject(8, contenido.getFechaEstreno());
        preparedStatement.setObject(9, contenido.getDuracionMinutos());
        preparedStatement.setObject(10, contenido.getGratuito());
        preparedStatement.setObject(11, contenido.getRecomendado());
        preparedStatement.setObject(12, contenido.getTendencia());
        preparedStatement.setObject(13, contenido.getPromedioCalificacion());
        preparedStatement.setObject(14, contenido.getTotalCalificaciones());
        preparedStatement.setObject(15, contenido.getTotalReproducciones());
        preparedStatement.setString(16, enumName(contenido.getEstado()));

        if (includeId) {
            preparedStatement.setLong(17, contenido.getId());
        }
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
