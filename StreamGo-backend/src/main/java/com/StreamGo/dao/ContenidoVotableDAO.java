package com.StreamGo.dao;

import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.ContenidoVotable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ContenidoVotableDAO
        extends AbstractGenericJdbcDAO<ContenidoVotable, Long> {

    public ContenidoVotableDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "contenidos_votables";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(ContenidoVotable contenidoVotable) {

        String sql = """
                INSERT INTO contenidos_votables (
                    titulo, descripcion, poster_url, imagen_url, activo
                ) VALUES (?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setString(1, contenidoVotable.getTitulo());
                    preparedStatement.setString(2, contenidoVotable.getDescripcion());
                    preparedStatement.setString(3, contenidoVotable.getPosterUrl());
                    preparedStatement.setString(4, contenidoVotable.getImagenUrl());
                    preparedStatement.setObject(5, contenidoVotable.getActivo());
                }
        );

        contenidoVotable.setId(id);
    }

    @Override
    public void update(ContenidoVotable contenidoVotable) {

        String sql = """
                UPDATE contenidos_votables
                SET titulo = ?, descripcion = ?, poster_url = ?,
                    imagen_url = ?, activo = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setString(1, contenidoVotable.getTitulo());
                    preparedStatement.setString(2, contenidoVotable.getDescripcion());
                    preparedStatement.setString(3, contenidoVotable.getPosterUrl());
                    preparedStatement.setString(4, contenidoVotable.getImagenUrl());
                    preparedStatement.setObject(5, contenidoVotable.getActivo());
                    preparedStatement.setLong(6, contenidoVotable.getId());
                }
        );
    }

    public List<ContenidoVotable> findByActivoTrue() {

        return queryForList(
                "SELECT * FROM contenidos_votables WHERE activo = true",
                preparedStatement -> {
                },
                this::mapResultSet
        );
    }

    @Override
    protected ContenidoVotable mapResultSet(ResultSet resultSet)
            throws SQLException {

        return ContenidoVotable.builder()
                .id(resultSet.getLong("id"))
                .titulo(resultSet.getString("titulo"))
                .descripcion(resultSet.getString("descripcion"))
                .posterUrl(resultSet.getString("poster_url"))
                .imagenUrl(resultSet.getString("imagen_url"))
                .activo(getBooleanOrNull(resultSet, "activo"))
                .build();
    }
}
