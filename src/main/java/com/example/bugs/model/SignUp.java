package com.example.bugs.model;

import java.util.regex.Pattern;

public class SignUp {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Regex for email validation.
    // Allows single char TLD for cases like a@b.c
    // Ensures no leading/trailing whitespace in the email string itself for validation.
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{1,}$" // Changed {2,} to {1,}
    );

    // Regex for name validation (letters only, at least one character, no leading/trailing spaces)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    // Regex for password validation (>=8 chars, >=1 number)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9]).{8,}$");


    // Constructor
    public SignUp(String firstName, String lastName, String email, String password) {
        // Store them as they are given; validation methods will handle trimming if necessary
        // or reject if spaces are not allowed by the validation logic itself.
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }


    // Validation methods
    public boolean isValidEmail() {
        if (this.email == null) { // Null is invalid
            return false;
        }
        // The test " test@example.com " expects false. This means the email string itself
        // must not contain leading/trailing spaces for the validation to pass.
        // The EMAIL_PATTERN implicitly disallows leading/trailing spaces
        // because ^ and $ anchor the match to the beginning and end of the string.
        // We also need to ensure the string isn't just empty.
        if (this.email.trim().isEmpty()) { // An email that's all spaces is invalid
            return false;
        }
        return EMAIL_PATTERN.matcher(this.email).matches();
    }

    public boolean isValidPassword() {
        if (this.password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(this.password).matches();
    }

    public boolean isValidName() {
        // Names should not be null, not empty, and contain only letters.
        // The NAME_PATTERN ("^[a-zA-Z]+$") already ensures no leading/trailing spaces
        // and that it's not empty. We just need to check for null.
        boolean isFirstNameValid = this.firstName != null && NAME_PATTERN.matcher(this.firstName).matches();
        boolean isLastNameValid = this.lastName != null && NAME_PATTERN.matcher(this.lastName).matches();
        return isFirstNameValid && isLastNameValid;
    }

    public boolean isValidUser() {
        return isValidName() && isValidEmail() && isValidPassword();
    }

    @Override
    public String toString() {
        return "SignUp{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}