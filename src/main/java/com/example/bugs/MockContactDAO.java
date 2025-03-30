package com.example.bugs;

import java.util.ArrayList;
import java.util.List;

public class MockContactDAO implements IContactDAO {
    public static final ArrayList<Contact> contacts = new ArrayList<>();
    private static int nextId = 0;

    public MockContactDAO() {
        // Initialize with sample data
        addContact(new Contact("John", "Doe", "john@example.com", "123-456-7890"));
        addContact(new Contact("Jane", "Smith", "jane@example.com", "987-654-3210"));
    }

    @Override
    public void addContact(Contact contact) {  // Must match IContactDAO
        contact.setId(nextId++);
        contacts.add(contact);
    }

    @Override
    public void updateContact(Contact contact) {  // Must match IContactDAO
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId() == contact.getId()) {
                contacts.set(i, contact);
                break;
            }
        }
    }

    @Override
    public void deleteContact(Contact contact) {  // Must match IContactDAO
        contacts.remove(contact);
    }

    @Override
    public Contact getContact(int id) {
        return contacts.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }
}