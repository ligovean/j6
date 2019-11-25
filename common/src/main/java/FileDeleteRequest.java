import java.util.UUID;

public class FileDeleteRequest extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileDeleteRequest(UUID clientId, String filename) {
        super.clientId = clientId;
        this.filename = filename;
    }
}