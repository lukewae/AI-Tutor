package com.example.bugs.model;

public class SignUp {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Constructor
    public SignUp(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Validation methods
    public boolean isValidEmail() {
        // Basic email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    public boolean isValidPassword() {
        // Password must be at least 8 characters long and contain at least one number
        return password != null && 
               password.length() >= 8 && 
               password.matches(".*\\d.*");
    }

    public boolean isValidName() {
        // Names should not be empty and should only contain letters
        return firstName != null && 
               lastName != null && 
               !firstName.trim().isEmpty() && 
               !lastName.trim().isEmpty() && 
               firstName.matches("[A-Za-z]+") && 
               lastName.matches("[A-Za-z]+");
    }

    // Method to validate all fields
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