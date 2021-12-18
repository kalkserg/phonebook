package dao;

import domain.Contact;

import java.util.List;

public interface ContactDAO {

    List<Contact> getAll();

    List<Contact> findByPhone(String phone);

    List<Contact> findByName(String name);

    Contact findById(Integer id);

    Integer findIdAddressById(Integer id);

    boolean delete(String phone);

    boolean delete(Integer id);

    boolean update(Contact contact);

    boolean insert(String name, String phone, Integer address);
}
