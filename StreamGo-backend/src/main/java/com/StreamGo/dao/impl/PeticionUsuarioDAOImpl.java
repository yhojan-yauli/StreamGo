package com.StreamGo.dao.impl;

import com.StreamGo.dao.PeticionUsuarioDAO;
import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.Peticion;
import com.StreamGo.entity.PeticionUsuario;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class PeticionUsuarioDAOImpl
        extends AbstractGenericJdbcDAO<PeticionUsuario, Long>
        implements PeticionUsuarioDAO {

    public PeticionUsuarioDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "peticiones_usuario";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(PeticionUsuario peticionUsuario) {

        String sql = """
                INSERT INTO peticiones_usuario (usuario_id, peticion_id)
                VALUES (?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(peticionUsuario));
                    preparedStatement.setObject(2, idPeticion(peticionUsuario));
                }
        );

        peticionUsuario.setId(id);
    }

    @Override
    public void update(PeticionUsuario peticionUsuario) {

        String sql = """
                UPDATE peticiones_usuario
                SET usuario_id = ?, peticion_id = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setObject(1, idUsuario(peticionUsuario));
                    preparedStatement.setObject(2, idPeticion(peticionUsuario));
                    preparedStatement.setLong(3, peticionUsuario.getId());
                }
        );
    }

    public boolean existsByUsuarioAndPeticion(
            Usuario usuario,
            Peticion peticion
    ) {

        return findByUsuarioAndPeticion(usuario, peticion).isPresent();
    }

    public Optional<PeticionUsuario> findByUsuarioAndPeticion(
            Usuario usuario,
            Peticion peticion
    ) {

        return queryForOptional(
                """
                        SELECT * FROM peticiones_usuario
                        WHERE usuario_id = ? AND peticion_id = ?
                        """,
                preparedStatement -> {
                    preparedStatement.setLong(1, usuario.getId());
                    preparedStatement.setLong(2, peticion.getId());
                },
                this::mapResultSet
        );
    }

    @Override
    protected PeticionUsuario mapResultSet(ResultSet resultSet)
            throws SQLException {

        return PeticionUsuario.builder()
                .id(resultSet.getLong("id"))
                .usuario(Usuario.builder()
                        .id(getLongOrNull(resultSet, "usuario_id"))
                        .build())
                .peticion(Peticion.builder()
                        .id(getLongOrNull(resultSet, "peticion_id"))
                        .build())
                .build();
    }

    private Long idUsuario(PeticionUsuario peticionUsuario) {
        return peticionUsuario.getUsuario() == null
                ? null
                : peticionUsuario.getUsuario().getId();
    }

    private Long idPeticion(PeticionUsuario peticionUsuario) {
        return peticionUsuario.getPeticion() == null
                ? null
                : peticionUsuario.getPeticion().getId();
    }
}
