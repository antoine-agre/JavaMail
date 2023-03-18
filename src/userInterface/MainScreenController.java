package userInterface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import mail.EMail;
import mail.MailHandler;

import javax.mail.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MainScreenController {

    protected static Client client;
    @FXML protected TableView<EMail> tableView;
    @FXML protected TableColumn<EMail, Date> dateColumn;
    @FXML protected TableColumn<EMail, String> fromColumn;
    @FXML protected TableColumn<EMail, String> subjectColumn;
    @FXML protected TableColumn<EMail, String> previewColumn;
    @FXML protected TableColumn<EMail, Boolean> attachmentColumn;
    @FXML protected Label focusSubject;
    @FXML protected Text focusFrom;
    @FXML protected Text focusTo;
    @FXML protected Label focusDate;
    @FXML protected Label focusContent;
    @FXML protected Label attachmentName;

    @FXML
    public void initialize() {
        /**
         * Executée à la création du controller, mais après que les attributs @FXML aient été remplis (contrairement
         * au constructeur).
         */

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("fromAddress"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        previewColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        attachmentColumn.setCellValueFactory(new PropertyValueFactory<>("hasAttachment"));

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showMail(newSelection);
            }
        });
    }

    public void refreshMails() {
        client.mailHandler.checkMails();
        tableView.setItems(FXCollections.observableArrayList());
        ObservableList<EMail> mailList = client.mailHandler.getEMailList();
        for (EMail eMail : mailList) {
            tableView.getItems().add(eMail);
        }
    }

    public void emptyList() {
        tableView.setItems(FXCollections.observableArrayList());
    }

    public void showMail(EMail eMail) {
        this.focusSubject.setText(eMail.getSubject());
        this.focusFrom.setText(eMail.getFromAddress());
        this.focusTo.setText(client.mailHandler.getUser());
        this.focusDate.setText(eMail.getDate().toString());
        this.focusContent.setText(eMail.getContent());
        this.attachmentName.setText(eMail.getFileName());
    }

    public void composeMail() {
        client.composeMail();
    }

    public void addTestLine() {

//        try {
//            Parent testMailLine = FXMLLoader.load(
//                    getClass().getResource("/resources/fxml/mailLine.fxml"));
//            mailListVBox.getChildren().add(testMailLine);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        tableView.

    }

}
