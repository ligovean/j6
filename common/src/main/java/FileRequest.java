import java.util.UUID;

public class FileRequest extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRequest(UUID clientId, String filename) {
        this.clientId = clientId;
        this.filename = filename;
    }
}
