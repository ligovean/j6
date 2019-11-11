import java.util.UUID;

public class FilesListRequest extends AbstractMessage {
    UUID clientId;

    public FilesListRequest(UUID clientId) {
        this.clientId = clientId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }
}
