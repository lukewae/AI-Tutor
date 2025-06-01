package com.example.bugs;

import com.example.bugs.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private static final String FIRST_NAME = "Jimmy";
    private static final String FIRST_NAME_TWO = "Jack";
    private static final String LAST_NAME = "Doe";
    private static final String LAST_NAME_TWO = "Doe";
    private static final String EMAIL = "jimmy@gmail.com";
    private static final String EMAIL_TWO = "jack@gmail.com";
    private static final String PHONE = "123457";
    private static final String PHONE_TWO = "644321";

    private Contact contact;
    private Contact contactTwo;

    @BeforeEach
    public void setUp() {
        contact = new Contact(FIRST_NAME, LAST_NAME, EMAIL, PHONE);
        contactTwo = new Contact(FIRST_NAME_TWO, LAST_NAME_TWO, EMAIL_TWO, PHONE_TWO);
    }

    @Test
    public void testSetId() {
        contact.setId(1);
        assertEquals(1, contact.getId());
    }

    @Test
    public void testGetFirstName() {
        assertEquals(FIRST_NAME, contact.getFirstName());
    }
    @Test
    public void testSetFirstName() {
        contact.setFirstName(FIRST_NAME_TWO);
        assertEquals(FIRST_NAME_TWO, contact.getFirstName());
    }
    @Test
    public void testGetLastName() {
        assertEquals(LAST_NAME, contact.getLastName());
    }
    @Test
    public void testSetLastName() {
        contact.setLastName(LAST_NAME_TWO);
        assertEquals(LAST_NAME_TWO, contact.getLastName());
    }
    @Test
    public void testGetEmail() {
        assertEquals(EMAIL, contact.getEmail());
    }
    @Test
    public void testSetEmail() {
        contact.setEmail(EMAIL_TWO);
        assertEquals(EMAIL_TWO, contact.getEmail());
    }
    @Test
    public void testGetPhone() {
        assertEquals(PHONE, contact.getPassword());
    }
    @Test
    public void testSetPhone() {
        contact.setPassword(PHONE_TWO);
        assertEquals( PHONE_TWO, contact.getPassword());
    }
    @Test
    public void testGetFullName() {
        String[] firstContact = {FIRST_NAME, LAST_NAME};
        String[] secondContact = {FIRST_NAME_TWO, LAST_NAME_TWO};
        assertEquals(String.join(" ", firstContact),  contact.getFullName());
        assertEquals(String.join(" ", secondContact),  contactTwo.getFullName());
    }
}
