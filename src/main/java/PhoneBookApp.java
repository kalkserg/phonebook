import dao.*;
import domain.Address;
import domain.Contact;

import java.util.List;
import java.util.Scanner;

public class PhoneBookApp {

    public static void main(String[] args) {

        ContactDAO contactDAO = new JdbcContactDAO(Sources.INSTANCE.dataSource());
        AddressDAO addressDAO = new JdbcAddressDAO(Sources.INSTANCE.dataSource());
        System.out.println("Commands: new, update, delete, all, by");
        System.out.print("Enter phone or name or command: ");

        List<Contact> contacts = null;
        Scanner myScanner = new Scanner(System.in);

        while (true) {
            String str = myScanner.nextLine();
            if (str.length() == 0) continue;
            else if (str.equals("by")) {
                by();
                break;
            } else if (!isCommand(str, myScanner, contactDAO, addressDAO)) {
                contacts = contactDAO.findByPhone(str);
                if (contacts.size() != 0) {
                    printContacts(contacts);
                } else {
                    contacts = contactDAO.findByName(str);
                    if (contacts.size() != 0) {
                        printContacts(contacts);
                    } else {
                        System.out.print("Not found. Do you want create new contact? [Y|N]: ");
                        String answ = myScanner.nextLine().toUpperCase();
                        if (answ.contains("Y")) {
                            System.out.println(createNewContact(myScanner, contactDAO, addressDAO) ? "Success" : "");
                        }
                    }
                }
            }
            System.out.print("Enter phone or name or command: ");
        }
    }

    private static boolean isCommand(String str, Scanner myScanner, ContactDAO contactDAO, AddressDAO addressDAO) {
        if (str.equalsIgnoreCase("update")) {
            System.out.println(updateContact(myScanner, contactDAO, addressDAO) ? "Success" : "Error");
            return true;
        } else if (str.equalsIgnoreCase("delete")) {
            System.out.println(delContact(myScanner, contactDAO, addressDAO) ? "Success" : "Error");
            return true;
        } else if (str.equalsIgnoreCase("all")) {
            allContacts(contactDAO);
            return true;
        } else if (str.equalsIgnoreCase("new")) {
            createNewContact(myScanner, contactDAO, addressDAO);
            return true;
        }
        return false;
    }

    private static void printContacts(List<Contact> contacts) {
        System.out.println("Find " + contacts.size() + " contacts:");
        contacts.forEach(System.out::println);
        System.out.println();
    }

    private static void allContacts(ContactDAO contactDAO) {
        List<Contact> contacts = contactDAO.getAll();
        printContacts(contacts);
    }

    private static boolean updateContact(Scanner myScanner, ContactDAO contactDAO, AddressDAO addressDAO) {
        System.out.print("Enter id for update: ");
        Integer id = Integer.valueOf(myScanner.nextLine());
        Contact contact = null;
        try {
            contact = contactDAO.findById(id);
            System.out.println("Enter new or press enter ");

            System.out.print("Name [" + contact.getName() + "]: ");
            String name = myScanner.nextLine();
            if (name.length() > 0) contact.setName(name);

            System.out.print("Phone [" + contact.getPhone() + "]: ");
            String phone = myScanner.nextLine();
            if (name.length() > 0) contact.setPhone(phone);

            System.out.print("Address [" + contact.getAddress().getPlace() + "]: ");
            String newAddress = myScanner.nextLine();
            int idAddress = contactDAO.findIdAddressById(id);

            if (newAddress.length() > 0) {
                if (idAddress != 0) {
                    addressDAO.update(contactDAO.findIdAddressById(id), newAddress);
                }
                Address address = new Address();
                address.setId(addressDAO.insert(newAddress));
                contact.setAddress(address);
            } else {
                Address address = new Address();
                address.setId(idAddress);
                contact.setAddress(address);
            }
        } catch (Exception ex) {
            System.out.print("Invalid id! ");
            return false;
        }
        return contactDAO.update(contact);
    }

    private static boolean delContact(Scanner myScanner, ContactDAO contactDAO, AddressDAO addressDAO) {
        System.out.print("Enter id for delete: ");
        Integer id = Integer.valueOf(myScanner.nextLine());
        addressDAO.delete(contactDAO.findIdAddressById(id));
        return contactDAO.delete(id);
    }

    private static boolean createNewContact(Scanner myScanner, ContactDAO contactDAO, AddressDAO addressDAO) {
        System.out.print("Enter name: ");
        String name = myScanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = myScanner.nextLine();
        System.out.print("Enter address: ");
        String place = myScanner.nextLine();
        return contactDAO.insert(name, phone, addressDAO.insert(place));
    }

    private static void by() {
        System.out.println("Good bye!");
    }
}