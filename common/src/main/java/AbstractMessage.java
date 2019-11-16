import java.io.Serializable;
import java.util.UUID;

public abstract class AbstractMessage implements Serializable {
    protected UUID clientId;

    public UUID getClientId() {
        return clientId;
    }
}