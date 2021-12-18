package dao;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcAddressDAO implements AddressDAO {
    private static final Logger log = LoggerFactory.getLogger(JdbcContactDAO.class);
    private final DataSource dataSource;

    public JdbcAddressDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String findById(Integer id) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM addresses WHERE id = ?");
            preparedStatement.setString(1, id.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getString("address");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (id != null) {
            try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM addresses WHERE id = ?");
                preparedStatement.setString(1, id.toString());
                return preparedStatement.execute();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return false;
    }

    @Override
    public boolean update(Integer id, String place) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE addresses SET address = ? WHERE id = ?");
            preparedStatement.setString(1, place);
            preparedStatement.setString(2, id.toString());
            return preparedStatement.execute();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public Integer insert(String address) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO addresses (address) values (?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, address);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            int key = 0;
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }
}
