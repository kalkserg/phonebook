package dao;

public interface AddressDAO {

    String findById(Integer id);

    boolean delete(Integer id);

    boolean update(Integer id, String addr);

    Integer insert(String place);
}
