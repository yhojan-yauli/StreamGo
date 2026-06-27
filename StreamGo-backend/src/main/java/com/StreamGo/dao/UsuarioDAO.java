package com.StreamGo.dao;

import com.StreamGo.dao.base.AbstractGenericJdbcDAO;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDAO extends AbstractGenericJdbcDAO<Usuario, Long> {

    public UsuarioDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "usuarios";
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    public void save(Usuario usuario) {

        String sql = """
                INSERT INTO usuarios (
                    nombre, email, password, telefono, avatar, rol,
                    estado, fecha_registro, ultimo_acceso
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        Long id = executeInsertAndReturnLongId(
                sql,
                preparedStatement -> {
                    preparedStatement.setString(1, usuario.getNombre());
                    preparedStatement.setString(2, usuario.getEmail());
                    preparedStatement.setString(3, usuario.getPassword());
                    preparedStatement.setString(4, usuario.getTelefono());
                    preparedStatement.setString(5, usuario.getAvatar());
                    preparedStatement.setString(6, enumName(usuario.getRol()));
                    preparedStatement.setString(7, enumName(usuario.getEstado()));
                    preparedStatement.setObject(8, usuario.getFechaRegistro());
                    preparedStatement.setObject(9, usuario.getUltimoAcceso());
                }
        );

        usuario.setId(id);
    }

    @Override
    public void update(Usuario usuario) {

        String sql = """
                UPDATE usuarios
                SET nombre = ?, email = ?, password = ?, telefono = ?,
                    avatar = ?, rol = ?, estado = ?, fecha_registro = ?,
                    ultimo_acceso = ?
                WHERE id = ?
                """;

        executeUpdate(
                sql,
                preparedStatement -> {
                    preparedStatement.setString(1, usuario.getNombre());
                    preparedStatement.setString(2, usuario.getEmail());
                    preparedStatement.setString(3, usuario.getPassword());
                    preparedStatement.setString(4, usuario.getTelefono());
                    preparedStatement.setString(5, usuario.getAvatar());
                    preparedStatement.setString(6, enumName(usuario.getRol()));
                    preparedStatement.setString(7, enumName(usuario.getEstado()));
                    preparedStatement.setObject(8, usuario.getFechaRegistro());
                    preparedStatement.setObject(9, usuario.getUltimoAcceso());
                    preparedStatement.setLong(10, usuario.getId());
                }
        );
    }

    public Optional<Usuario> findByEmail(String email) {

        return queryForOptional(
                "SELECT * FROM usuarios WHERE email = ?",
                preparedStatement -> preparedStatement.setString(1, email),
                this::mapResultSet
        );
    }

    public boolean existsByEmail(String email) {

        return queryForOptional(
                "SELECT id FROM usuarios WHERE email = ?",
                preparedStatement -> preparedStatement.setString(1, email),
                resultSet -> resultSet.getLong("id")
        ).isPresent();
    }

    public List<Usuario> findByRol(Rol rol) {

        return queryForList(
                "SELECT * FROM usuarios WHERE rol = ?",
                preparedStatement -> preparedStatement.setString(1, enumName(rol)),
                this::mapResultSet
        );
    }

    @Override
    protected Usuario mapResultSet(ResultSet resultSet) throws SQLException {

        return Usuario.builder()
                .id(resultSet.getLong("id"))
                .nombre(resultSet.getString("nombre"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .telefono(resultSet.getString("telefono"))
                .avatar(resultSet.getString("avatar"))
                .rol(enumValue(Rol.class, resultSet.getString("rol")))
                .estado(enumValue(
                        EstadoUsuario.class,
                        resultSet.getString("estado")
                ))
                .fechaRegistro(getLocalDateTimeOrNull(
                        resultSet,
                        "fecha_registro"
                ))
                .ultimoAcceso(getLocalDateTimeOrNull(
                        resultSet,
                        "ultimo_acceso"
                ))
                .build();
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
