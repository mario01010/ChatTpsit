import java.util.List;

public abstract class User {
    private String ID;
    private String username;
    private String password;
    private List<Chat> chat;
    private boolean status;

    public User(String ID, String username, String password, boolean status) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public String getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getStatus(){
        return status;
    }

    public void setStatus(Boolean status){
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}