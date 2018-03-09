package vn.axonactive.authentication.login;

public class LoginAlreadyException extends Exception {
    private static final long serialVersionUID = 1L;

    public LoginAlreadyException() {
        super("This IP alreay have an account logined");
    }

    public LoginAlreadyException(Exception variable) {
        super("This IP alreay have an account logined. " + variable.toString());
    }

}
