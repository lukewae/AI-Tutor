package com.example.bugs.model;

import com.example.bugs.Contact;
import com.example.bugs.IContactDAO;
import com.example.bugs.MockContactDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.util.List;

public class SignUp {
    @FXML
    private ListView<com.example.bugs.Contact> contactsListView;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private VBox contactContainer;

    private IContactDAO contactDAO;

    public SignUp() {
        contactDAO = new MockContactDAO();
    }

    private void selectContact(com.example.bugs.Contact contact) {
        contactsListView.getSelectionModel().select(contact);
        firstNameTextField.setText(contact.getFirstName());
        lastNameTextField.setText(contact.getLastName());
        emailTextField.setText(contact.getEmail());
        passwordTextField.setText(contact.getPassword());
    }

    private ListCell<com.example.bugs.Contact> renderCell(ListView<com.example.bugs.Contact> contactListView) {
        return new ListCell<>() {
            private void onContactSelected(MouseEvent mouseEvent) {
                ListCell<com.example.bugs.Contact> clickedCell = (ListCell<com.example.bugs.Contact>) mouseEvent.getSource();
                com.example.bugs.Contact selectedContact = clickedCell.getItem();
                if (selectedContact != null) selectContact(selectedContact);
            }

            @Override
            protected void updateItem(com.example.bugs.Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null || contact.getFullName() == null) {
                    setText(null);
                } else {
                    setText(contact.getFullName() + " (" + contact.getPassword() + ")");
                }
                super.setOnMouseClicked(this::onContactSelected);
            }
        };
    }

    private void syncContacts() {
        contactsListView.getItems().clear();
        List<com.example.bugs.Contact> contacts = contactDAO.getAllContacts();
        boolean hasContact = !contacts.isEmpty();
        if (hasContact) {
            contactsListView.getItems().addAll(contacts);
        }
        contactContainer.setVisible(hasContact);
    }

    @FXML
    private void onEditConfirm() {
        com.example.bugs.Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            selectedContact.setFirstName(firstNameTextField.getText());
            selectedContact.setLastName(lastNameTextField.getText());
            selectedContact.setEmail(emailTextField.getText());
            selectedContact.setPassword(passwordTextField.getText());
            contactDAO.updateContact(selectedContact);
            syncContacts();
        }
    }

    @FXML
    private void onDelete() {
        com.example.bugs.Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            contactDAO.deleteContact(selectedContact);
            syncContacts();
        }
    }

    @FXML
    private void onAdd() {
        final String DEFAULT_FIRST_NAME = "New";
        final String DEFAULT_LAST_NAME = "Contact";
        final String DEFAULT_EMAIL = "";
        final String DEFAULT_PHONE = "";
        com.example.bugs.Contact newContact = new com.example.bugs.Contact(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_EMAIL, DEFAULT_PHONE);
        contactDAO.addContact(newContact);
        syncContacts();
        selectContact(newContact);
        firstNameTextField.requestFocus();
    }

    @FXML
    private void onCancel() {
        com.example.bugs.Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            selectContact(selectedContact);
        }
    }

    @FXML
    public void initialize() {
        contactsListView.setCellFactory(this::renderCell);
        syncContacts();
        contactsListView.getSelectionModel().selectFirst();
        Contact firstContact = contactsListView.getSelectionModel().getSelectedItem();
        if (firstContact != null) {
            selectContact(firstContact);
        }
    }
}