package com.example.bugs.controller;

import com.example.bugs.HelloApplication;
import com.example.bugs.model.Contact;
import com.example.bugs.model.IContactDAO;
import com.example.bugs.model.SqliteContactDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.List;

public class SignUpController {
    @FXML
    private ListView<Contact> contactsListView;
    private IContactDAO contactDAO;
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
    @FXML
    private Button returnButton;

    public SignUpController() {
        contactDAO = new SqliteContactDAO();
    }

    /**
     * Programmatically selects a contact in the list view and
     * updates the text fields with the contact's information.
     * @param contact The contact to select.
     */
    private void selectContact(Contact contact) {
        contactsListView.getSelectionModel().select(contact);
        firstNameTextField.setText(contact.getFirstName());
        lastNameTextField.setText(contact.getLastName());
        emailTextField.setText(contact.getEmail());
        passwordTextField.setText(contact.getPassword());
    }

    /**
     * Renders a cell in the contacts list view by setting the text to the contact's full name.
     * @param contactListView The list view to render the cell for.
     * @return The rendered cell.
     */
    private ListCell<Contact> renderCell(ListView<Contact> contactListView) {
        return new ListCell<>() {
            /**
             * Handles the event when a contact is selected in the list view.
             * @param mouseEvent The event to handle.
             */
            private void onContactSelected(MouseEvent mouseEvent) {
                ListCell<Contact> clickedCell = (ListCell<Contact>) mouseEvent.getSource();
                // Get the selected contact from the list view
                Contact selectedContact = clickedCell.getItem();
                if (selectedContact != null) selectContact(selectedContact);
            }

            /**
             * Updates the item in the cell by setting the text to the contact's full name.
             * @param contact The contact to update the cell with.
             * @param empty Whether the cell is empty.
             */
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                // If the cell is empty, set the text to null, otherwise set it to the contact's full name
                if (empty || contact == null || contact.getFullName() == null) {
                    setText(null);
                    super.setOnMouseClicked(this::onContactSelected);
                } else {
                    setText(contact.getFullName());
                }
            }
        };
    }

    /**
     * Synchronizes the contacts list view with the contacts in the database.
     */
    private void syncContacts() {
        contactsListView.getItems().clear();
        List<Contact> contacts = contactDAO.getAllContacts();
        boolean hasContact = !contacts.isEmpty();
        if (hasContact) {
            contactsListView.getItems().addAll(contacts);
        }
        // Show / hide based on whether there are contacts
        contactContainer.setVisible(hasContact);
    }

    @FXML
    public void initialize() {
        contactsListView.setCellFactory(this::renderCell);
        syncContacts();
        // Select the first contact and display its information
        contactsListView.getSelectionModel().selectFirst();
        Contact firstContact = contactsListView.getSelectionModel().getSelectedItem();
        if (firstContact != null) {
            selectContact(firstContact);
        }
    }

    @FXML
    private void onEditConfirm() {
        // Get the selected contact from the list view
        Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
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
        // Get the selected contact from the list view
        Contact selectedContact = contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            contactDAO.deleteContact(selectedContact);
            syncContacts();
        }
    }

    @FXML
    private void onAdd() {
        // Default values for a new contact
        final String DEFAULT_FIRST_NAME = "New";
        final String DEFAULT_LAST_NAME = "Contact";
        final String DEFAULT_EMAIL = "";
        final String DEFAULT_PASSWORD = "";
        Contact newContact = new Contact(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
        // Add the new contact to the database
        contactDAO.addContact(newContact);
        syncContacts();
        // Select the new contact in the list view
        // and focus the first name text field
        selectContact(newContact);
        firstNameTextField.requestFocus();
    }

    @FXML
    private void onCancel() throws IOException {
        // Returns to the Log-In Screen
        Stage stage = (Stage) returnButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

}