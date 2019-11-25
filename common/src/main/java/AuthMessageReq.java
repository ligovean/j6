import java.util.UUID;

public class AuthMessageReq  extends AbstractMessage {
    private boolean auth;
    private String clientName;

    public AuthMessageReq(boolean auth, UUID clientId, String clientName) {
        this.auth = auth;
        this.clientId = clientId;
        this.clientName = clientName;
    }

    public boolean isAuth() {
        return auth;
    }

    public String getClientName(){
        return clientName;
    }

}
