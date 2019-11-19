import java.util.UUID;

public class AuthMessageReq  extends AbstractMessage {
    private boolean auth;

    public AuthMessageReq(boolean auth, UUID clientId) {
        this.auth = auth;
        this.clientId = clientId;
    }

    public boolean isAuth() {
        return auth;
    }

}
