import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    TextField tfFileName;

    @FXML
    ListView<String> filesListClient;

    @FXML
    ListView<String> filesListServer;

    ObservableList selectedItemsClient;

    ObservableList selectedItemsServer;

    private final UUID clientId = UUID.fromString("7dc53df5-703e-49b3-8670-b1c468f47f1f");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
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


        Network.sendMsg(new FilesListRequest(clientId));
    }

    //Скачать с Сервера
    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        System.out.println(selectedItemsServer);
        Iterator it = selectedItemsServer.iterator();
        while (it.hasNext()) {
            Network.sendMsg(new FileRequest(clientId, it.next().toString()));
        }
    }

    //Отправить на сервер
    public void pressOnUploadBtn(ActionEvent actionEvent) throws IOException {
        //System.out.println(selectedItems);
        Iterator it = selectedItemsClient.iterator();
        while (it.hasNext()){
            FileMessage fm = new FileMessage(clientId,Paths.get("client_storage/" + it.next()));
            Network.sendMsg(fm);
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



    public static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }
}
