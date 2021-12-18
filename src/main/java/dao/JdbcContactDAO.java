package dao;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import domain.Address;
import domain.Contact;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcContactDAO implements ContactDAO {
    private static final Logger log = LoggerFactory.getLogger(JdbcContactDAO.class);
    private final DataSource dataSource;

    public JdbcContactDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Contact> getAll() {
        try (Connection connection = dataSource.getConnection();
            Statement selectStatement = connection.createStatement()) {
            ResultSet resultSet = selectStatement.executeQuery("SELECT contacts.id, name, phone, addresses.address FROM contacts INNER JOIN addresses WHERE contacts.address = addresses.id UNION ALL SELECT id, name, phone, address FROM contacts WHERE address IS NULL");
            return getList(resultSet);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public List<Contact> findByName(String name) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT contacts.id, name, phone, addresses.address FROM contacts INNER JOIN addresses WHERE name = ? AND contacts.address = addresses.id UNION ALL SELECT id, name, phone, address FROM contacts WHERE name = ? AND address IS NULL");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public Contact findById(Integer id) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT contacts.id, name, phone, addresses.address FROM contacts inner join addresses where contacts.id = ? and contacts.address = addresses.id UNION ALL SELECT id, name, phone, address FROM contacts WHERE id = ? AND address IS NULL");
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, id.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return getOne(resultSet);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public Integer findIdAddressById(Integer id) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT address FROM contacts WHERE id = ?");
            preparedStatement.setString(1, id.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            int key = 0;
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public List<Contact> findByPhone(String phone) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT contacts.id, name, phone, addresses.address FROM contacts INNER JOIN addresses WHERE phone = ? AND contacts.address = addresses.id UNION ALL SELECT id, name, phone, address FROM contacts WHERE phone = ? AND address IS NULL ");
            preparedStatement.setString(1, phone);
            preparedStatement.setString(2, phone);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public boolean delete(String phone) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM addresses WHERE id IN (SELECT id FROM contacts WHERE phone = ?)");
            preparedStatement.setString(1, phone);
            ResultSet resultSet = preparedStatement.executeQuery();
            return preparedStatement.executeUpdate()==1;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM contacts WHERE id = ?");
            preparedStatement.setString(1, id.toString());
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean update(Contact contact) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE contacts SET name = ?, phone = ?, address = ? WHERE id = ?");
            preparedStatement.setString(1, contact.getName());
            preparedStatement.setString(2, contact.getPhone());
            preparedStatement.setString(3, contact.getAddress().getId().toString());
            preparedStatement.setInt(4, contact.getId());
            return preparedStatement.executeUpdate()==1;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean insert(String name, String phone, Integer address) {
        try (Connection connection = Sources.INSTANCE.dataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO contacts (name, phone, address) VALUE (?, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, address.toString());
            return preparedStatement.execute();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    private List<Contact> getList(ResultSet resultSet) throws SQLException {
        List<Contact> contacts = new ArrayList();
        while (resultSet.next()) {
            Contact contact = new Contact();
            contact.setId(resultSet.getInt("id"));
            contact.setName(resultSet.getString("name"));
            contact.setPhone(resultSet.getString("phone"));
            Address address = new Address();
            address.setPlace(resultSet.getString("address"));
            contact.setAddress(address);
            contacts.add(contact);
        }
        return contacts;
    }

    private Contact getOne(ResultSet resultSet) throws SQLException {
        Contact contact = null;
        while (resultSet.next()) {
            contact = new Contact();
            contact.setId(resultSet.getInt("id"));
            contact.setName(resultSet.getString("name"));
            contact.setPhone(resultSet.getString("phone"));
            Address address = new Address();
            address.setPlace(resultSet.getString("address"));
            contact.setAddress(address);
        }
        return contact;
    }

}
