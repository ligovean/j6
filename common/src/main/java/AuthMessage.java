public class AuthMessage extends AbstractMessage{
    private String loginFiled;

    private String passwordField;

    public AuthMessage(String loginFiled, String passwordField) {
        this.loginFiled = loginFiled;
        this.passwordField = passwordField;
    }


    public String getLoginFiled() {
        return loginFiled;
    }

    public String getPasswordField() {
        return passwordField;
    }
}
