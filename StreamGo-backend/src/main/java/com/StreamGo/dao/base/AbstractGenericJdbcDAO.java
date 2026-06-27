package com.StreamGo.dao.base;

import com.StreamGo.dao.IGenericDAO;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractGenericJdbcDAO<T, ID> implements IGenericDAO<T, ID> {

    private final DataSource dataSource;

    protected AbstractGenericJdbcDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected abstract String getTableName();

    protected abstract String getIdColumnName();

    protected abstract T mapResultSet(ResultSet resultSet) throws SQLException;

    @Override
    public List<T> findAll() {

        String sql = "SELECT * FROM " + getTableName();

        return queryForList(
                sql,
                preparedStatement -> {
                },
                this::mapResultSet
        );
    }

    @Override
    public T findById(ID id) {

        String sql = "SELECT * FROM " + getTableName()
                + " WHERE " + getIdColumnName() + " = ?";

        return queryForOptional(
                sql,
                preparedStatement -> preparedStatement.setObject(1, id),
                this::mapResultSet
        ).orElseThrow(() ->
                new RuntimeException(getTableName() + " no encontrado"));
    }

    @Override
    public void delete(ID id) {

        String sql = "DELETE FROM " + getTableName()
                + " WHERE " + getIdColumnName() + " = ?";

        int filasAfectadas = executeUpdate(
                sql,
                preparedStatement -> preparedStatement.setObject(1, id)
        );

        if (filasAfectadas == 0) {
            throw new RuntimeException(getTableName() + " no encontrado");
        }
    }

    protected List<T> queryForList(
            String sql,
            PreparedStatementBinder binder,
            ResultSetMapper<T> mapper
    ) {

        Connection connection = getConnection();

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql)) {

            binder.bind(preparedStatement);

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                List<T> resultados = new ArrayList<>();

                while (resultSet.next()) {
                    resultados.add(mapper.map(resultSet));
                }

                return resultados;
            }

        } catch (SQLException exception) {
            throw new DataAccessResourceFailureException(
                    "Error ejecutando consulta JDBC",
                    exception
            );
        } finally {
            releaseConnection(connection);
        }
    }

    protected Optional<T> queryForOptional(
            String sql,
            PreparedStatementBinder binder,
            ResultSetMapper<T> mapper
    ) {

        Connection connection = getConnection();

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql)) {

            binder.bind(preparedStatement);

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(mapper.map(resultSet));
            }

        } catch (SQLException exception) {
            throw new DataAccessResourceFailureException(
                    "Error ejecutando consulta JDBC",
                    exception
            );
        } finally {
            releaseConnection(connection);
        }
    }

    protected int executeUpdate(
            String sql,
            PreparedStatementBinder binder
    ) {

        Connection connection = getConnection();

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql)) {

            binder.bind(preparedStatement);
            return preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            throw new DataAccessResourceFailureException(
                    "Error ejecutando actualizacion JDBC",
                    exception
            );
        } finally {
            releaseConnection(connection);
        }
    }

    protected Long executeInsertAndReturnLongId(
            String sql,
            PreparedStatementBinder binder
    ) {

        Connection connection = getConnection();

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            binder.bind(preparedStatement);

            int filasAfectadas = preparedStatement.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("No se pudo insertar el registro");
            }

            try (ResultSet generatedKeys =
                         preparedStatement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }

                throw new RuntimeException(
                        "No se obtuvo el ID generado"
                );
            }

        } catch (SQLException exception) {
            throw new DataAccessResourceFailureException(
                    "Error ejecutando insercion JDBC",
                    exception
            );
        } finally {
            releaseConnection(connection);
        }
    }

    private Connection getConnection() {

        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection connection) {

        DataSourceUtils.releaseConnection(
                connection,
                dataSource
        );
    }

    @FunctionalInterface
    protected interface PreparedStatementBinder {

        void bind(PreparedStatement preparedStatement)
                throws SQLException;
    }

    @FunctionalInterface
    protected interface ResultSetMapper<R> {

        R map(ResultSet resultSet) throws SQLException;
    }
}
