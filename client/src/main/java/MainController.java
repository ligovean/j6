import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

//public class MainController implements Initializable {
public class MainController {
    @FXML
    Parent root;

    @FXML
    TextField tfFileName;
    @FXML
    ListView<String> filesListClient;
    @FXML
    ListView<String> filesListServer;
    @FXML
    HBox topPanel;
    @FXML
    TextField loginFiled;
    @FXML
    PasswordField passwordField;

    @FXML
    HBox bottomPanel;

    @FXML
    TextField textField;
    @FXML
    Button btn1;
    @FXML
    ComboBox comboBox;

    //private final UUID clientId = UUID.fromString("7dc53df5-703e-49b3-8670-b1c468f47f1f");
    private UUID clientId;

    ObservableList selectedItemsClient;

    ObservableList selectedItemsServer;

    private boolean isAuthorized;

    private Stage primaryStage;

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
    public void connect() {
        Thread t = new Thread(() -> {
            try {
                //Получение сообщения об Авторизации

                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof AuthMessageReq) {
                        AuthMessageReq amr = (AuthMessageReq) am;
                        if(amr.isAuth()){
                            setAuthorized(true, amr.getClientId());

                            setTitle(amr.getClientName());
                            break;
                        }else {
                            System.out.println("НЕ КОРРЕКТНЫЙ ЛОГИН ИЛИ ПАРОЛЬ!");
                        }
                    }
                }

/*
                AbstractMessage amr = new AuthMessageReq(true,UUID.fromString("7dc53df5-703e-49b3-8670-b1c468f47f1f"));
                setAuthorized(true, amr.getClientId());
*/

                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                    if (am instanceof FilesListMessage){
                        System.out.println("Пришел список фаЙлов от сервера ");
                        FilesListMessage rfm = (FilesListMessage) am;
                        ArrayList<String> fls = rfm.getFilesList();
                        refreshServerFilesList(fls);
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        filesListClient.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        filesListServer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        refreshLocalFilesList();
        filesListClient.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            selectedItemsClient = filesListClient.getSelectionModel().getSelectedItems();
        });

        filesListServer.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            selectedItemsServer = filesListServer.getSelectionModel().getSelectedItems();
        });
    }

    //Скачать с Сервера
    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        System.out.println(selectedItemsServer);
        if (selectedItemsServer != null) {
            Iterator it = selectedItemsServer.iterator();
            while (it.hasNext()) {
                Network.sendMsg(new FileRequest(clientId, it.next().toString()));
            }
        } else refreshLocalFilesList();
    }

    //Отправить на сервер
    public void pressOnUploadBtn(ActionEvent actionEvent) throws IOException {
        if (selectedItemsClient != null) {
            Iterator it = selectedItemsClient.iterator();

            while (it.hasNext()) {
                FileMessage fm = new FileMessage(clientId, Paths.get("client_storage/" + it.next()));
                System.out.println("clientId: " + clientId + ", fm.getClientId: " + fm.getClientId());
                Network.sendMsg(fm);
            }
        }
    }

    //Удалить на Сервере
    public void pressOnServerDelete(ActionEvent actionEvent) {
        System.out.println(selectedItemsServer);
        if (selectedItemsServer != null) {
            Iterator it = selectedItemsServer.iterator();
            while (it.hasNext()) {
                Network.sendMsg(new FileDeleteRequest(clientId, it.next().toString()));
            }
        } else refreshLocalFilesList();
    }

    public void pressOnLocalRenameBtn(){
        //TODO Переименование файла
    }
    public void pressOnLocalDelBtn() throws IOException {
        if (selectedItemsClient != null) {
            Iterator it = selectedItemsClient.iterator();

            while (it.hasNext()) {
                Files.deleteIfExists(Paths.get("client_storage/" + it.next()));
            }
            refreshLocalFilesList();
        }
    }

    public void refreshLocalFilesList() {
        updateUI(() -> {
            try {
                filesListClient.getItems().clear();
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> filesListClient.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void refreshServerFilesList(ArrayList<String> fls) {
        updateUI(() -> {
                filesListServer.getItems().clear();
                fls.forEach(o -> filesListServer.getItems().add(o));
        });
    }

    public void setTitle(String title){
        updateUI(() -> {
            primaryStage.setTitle("My Cloud " + title);
        });
    };


    public void setAuthorized(boolean isAuthorized, UUID clientId) {
        this.clientId = clientId;
        updateUI(() -> {
            this.isAuthorized = isAuthorized;
            if(!isAuthorized) {
                topPanel.setVisible(true);
                topPanel.setManaged(true);
                bottomPanel.setVisible(false);
                bottomPanel.setManaged(false);
                Network.sendMsg(new FilesListRequest(clientId));
            } else {
                topPanel.setVisible(false);
                topPanel.setManaged(false);
                bottomPanel.setVisible(true);
                bottomPanel.setManaged(true);
            }
        });
    }



    public static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    //Отправка сообщения об Авторизации
    public void auth() {
        Network.start();
            connect();
        System.out.println("Отправить запрос на авторизацию! login: " + loginFiled.getText() + ", psw: " + passwordField.getText());
            Network.sendMsg(new AuthMessage(loginFiled.getText(),passwordField.getText()));
    }
}
