package com.example.bugs;

import com.example.bugs.model.SignUp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SignUpTest {

    private static final String VALID_FIRST_NAME = "John";
    private static final String VALID_LAST_NAME = "Doe";
    private static final String VALID_EMAIL = "john.doe@example.com";
    private static final String VALID_PASSWORD = "password123";

    private SignUp signUp;

    @BeforeEach
    void setUp() {
        // Create a valid SignUp object before each test
        signUp = new SignUp(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD);
    }

    @Test
    @DisplayName("Constructor should initialize fields correctly")
    void testConstructorAndGetters() {
        assertEquals(VALID_FIRST_NAME, signUp.getFirstName());
        assertEquals(VALID_LAST_NAME, signUp.getLastName());
        assertEquals(VALID_EMAIL, signUp.getEmail());
        assertEquals(VALID_PASSWORD, signUp.getPassword());
    }

    @Test
    @DisplayName("setFirstName should update the first name")
    void testSetFirstName() {
        String newFirstName = "Jane";
        signUp.setFirstName(newFirstName);
        assertEquals(newFirstName, signUp.getFirstName());
    }

    @Test
    @DisplayName("setLastName should update the last name")
    void testSetLastName() {
        String newLastName = "Smith";
        signUp.setLastName(newLastName);
        assertEquals(newLastName, signUp.getLastName());
    }

    @Test
    @DisplayName("setEmail should update the email")
    void testSetEmail() {
        String newEmail = "jane.smith@domain.net";
        signUp.setEmail(newEmail);
        assertEquals(newEmail, signUp.getEmail());
    }

    @Test
    @DisplayName("setPassword should update the password")
    void testSetPassword() {
        String newPassword = "newSecurePassword9";
        signUp.setPassword(newPassword);
        assertEquals(newPassword, signUp.getPassword());
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidation {

        @ParameterizedTest
        @ValueSource(strings = {"test@example.com", "test.name@domain.co.uk", "test+alias@host.net", "123@numbers.com", "a@b.c"})
        @DisplayName("should return true for valid email formats")
        void isValidEmail_ValidFormats(String validEmail) {
            signUp.setEmail(validEmail);
            assertTrue(signUp.isValidEmail(), "Expected true for email: " + validEmail);
        }

        @ParameterizedTest
        @ValueSource(strings = {"plainaddress", "@missinglocalpart.com", "test@missingdomain", "test@domain.", "test@.com", "", " test@example.com "})
        @DisplayName("should return false for invalid email formats")
        void isValidEmail_InvalidFormats(String invalidEmail) {
            signUp.setEmail(invalidEmail);
            assertFalse(signUp.isValidEmail(), "Expected false for email: " + invalidEmail);
        }

        @Test
        @DisplayName("should return false for null email")
        void isValidEmail_Null() {
            signUp.setEmail(null);
            assertFalse(signUp.isValidEmail(), "Expected false for null email");
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidation {

        @ParameterizedTest
        @ValueSource(strings = {"password1", "12345678", "longPasswordWith1Number", "numb3rsM1ddle", "9startNumber"})
        @DisplayName("should return true for valid passwords (>=8 chars, >=1 number)")
        void isValidPassword_Valid(String validPassword) {
            signUp.setPassword(validPassword);
            assertTrue(signUp.isValidPassword(), "Expected true for password: " + validPassword);
        }

        @ParameterizedTest
        @ValueSource(strings = {"pass1", // Too short
                "short",   // Too short, no number
                "longpasswordnonumber", // Long enough, no number
                "1234567", // Too short
                ""})       // Empty
        @DisplayName("should return false for invalid passwords")
        void isValidPassword_Invalid(String invalidPassword) {
            signUp.setPassword(invalidPassword);
            assertFalse(signUp.isValidPassword(), "Expected false for password: " + invalidPassword);
        }

        @Test
        @DisplayName("should return false for null password")
        void isValidPassword_Null() {
            signUp.setPassword(null);
            assertFalse(signUp.isValidPassword(), "Expected false for null password");
        }
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidation {

        @Test
        @DisplayName("should return true for valid first and last names (letters only)")
        void isValidName_Valid() {
            signUp.setFirstName("Valid");
            signUp.setLastName("Name");
            assertTrue(signUp.isValidName());
        }

        @ParameterizedTest
        @ValueSource(strings = {"Name1", "Name!", " Name", "Name ", ""})
        @DisplayName("should return false if first name is invalid")
        void isValidName_InvalidFirstName(String invalidFirstName) {
            signUp.setFirstName(invalidFirstName);
            // Keep last name valid
            signUp.setLastName("ValidLastName");
            assertFalse(signUp.isValidName(), "Expected false for first name: " + invalidFirstName);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Name1", "Name!", " Name", "Name ", ""})
        @DisplayName("should return false if last name is invalid")
        void isValidName_InvalidLastName(String invalidLastName) {
            // Keep first name valid
            signUp.setFirstName("ValidFirstName");
            signUp.setLastName(invalidLastName);
            assertFalse(signUp.isValidName(), "Expected false for last name: " + invalidLastName);
        }


        @Test
        @DisplayName("should return false if first name is null")
        void isValidName_NullFirstName() {
            signUp.setFirstName(null);
            signUp.setLastName("ValidLastName");
            assertFalse(signUp.isValidName());
        }

        @Test
        @DisplayName("should return false if last name is null")
        void isValidName_NullLastName() {
            signUp.setFirstName("ValidFirstName");
            signUp.setLastName(null);
            assertFalse(signUp.isValidName());
        }

        @Test
        @DisplayName("should return false if both names are null")
        void isValidName_NullBothNames() {
            signUp.setFirstName(null);
            signUp.setLastName(null);
            assertFalse(signUp.isValidName());
        }

        @Test
        @DisplayName("should return false if first name is empty")
        void isValidName_EmptyFirstName() {
            signUp.setFirstName("");
            signUp.setLastName("ValidLastName");
            assertFalse(signUp.isValidName());
        }

        @Test
        @DisplayName("should return false if last name is empty")
        void isValidName_EmptyLastName() {
            signUp.setFirstName("ValidFirstName");
            signUp.setLastName("");
            assertFalse(signUp.isValidName());
        }
    }

    @Nested
    @DisplayName("Overall User Validation (isValidUser)")
    class OverallValidation {

        @Test
        @DisplayName("should return true when all fields are valid")
        void isValidUser_AllValid() {
            // Use the object created in setUp, which is valid
            assertTrue(signUp.isValidUser());
        }

        @Test
        @DisplayName("should return false if name is invalid")
        void isValidUser_InvalidName() {
            signUp.setFirstName("Invalid1");
            assertFalse(signUp.isValidUser());
        }

        @Test
        @DisplayName("should return false if email is invalid")
        void isValidUser_InvalidEmail() {
            signUp.setEmail("invalid-email");
            assertFalse(signUp.isValidUser());
        }

        @Test
        @DisplayName("should return false if password is invalid")
        void isValidUser_InvalidPassword() {
            signUp.setPassword("short");
            assertFalse(signUp.isValidUser());
        }

        @Test
        @DisplayName("should return false if multiple fields are invalid")
        void isValidUser_MultipleInvalid() {
            signUp.setFirstName("Inv@lid");
            signUp.setEmail("invalid-email");
            signUp.setPassword("short");
            assertFalse(signUp.isValidUser());
        }
    }

    @Test
    @DisplayName("toString should return formatted string with protected password")
    void testToString() {
        String expectedStringPart1 = "SignUp{firstName='" + VALID_FIRST_NAME + "'";
        String expectedStringPart2 = "lastName='" + VALID_LAST_NAME + "'";
        String expectedStringPart3 = "email='" + VALID_EMAIL + "'";
        String expectedProtectedPassword = "password='[PROTECTED]'"; // Exact match expected

        String actualString = signUp.toString();

        assertTrue(actualString.contains(expectedStringPart1), "toString output missing first name part");
        assertTrue(actualString.contains(expectedStringPart2), "toString output missing last name part");
        assertTrue(actualString.contains(expectedStringPart3), "toString output missing email part");
        assertTrue(actualString.contains(expectedProtectedPassword), "toString output missing or incorrect protected password");
        assertFalse(actualString.contains(VALID_PASSWORD), "toString output should not contain the actual password");
    }
}