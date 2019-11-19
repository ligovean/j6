import java.util.UUID;

public class FilesListRequest extends AbstractMessage {


    public FilesListRequest(UUID clientId) {
        super.clientId = clientId;
    }

    public UUID getClientId() {
        return super.clientId;
    }

    public void setClientId(UUID clientId) {
        super.clientId = clientId;
    }
}
