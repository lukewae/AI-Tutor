package com.example.bugs;

import java.util.List;

public interface IContactDAO {
    void addContact(Contact contact);      // Was "addContent"?
    void updateContact(Contact contact);  // Was "updateContent"?
    void deleteContact(Contact contact);  // Was "deleteContent"?
    Contact getContact(int id);
    List<Contact> getAllContacts();
}