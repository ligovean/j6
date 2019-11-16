import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class FilesListMessage extends AbstractMessage {
    ArrayList<String> filesList = new ArrayList<>();
    public FilesListMessage(UUID clientId) {
        try {
            this.clientId =  clientId;
            //TODO Отправка списка файлов из пользовательской папку clientId

            Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> filesList.add(o));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getFilesList() {
        return filesList;
    }

    public void setFilesList(ArrayList<String> filesList) {
        this.filesList = filesList;
    }
}
