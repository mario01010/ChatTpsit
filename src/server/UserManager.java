import java.util.Map;
import java.sql.SQLException;
import java.util.HashMap;

public class UserManager{
    private Map<String, User> userList;
    private Map<String, User> online;
    private DBManager dbManager;

    public UserManager(){
        userList = new HashMap<>();
        online = new HashMap<>();
        dbManager = new DBManager();
    }

    public boolean register(User u){
        if(userList.containsKey(u.getUsername())){
            return false;
        } else {
            userList.put(u.getUsername(), u);
            try{
                dbManager.addUser(u.getUsername(), u.getPassword(), u.getStatus() ? 1 : 0);
            } catch (SQLException e){
                e.printStackTrace();
                System.out.println("Errore durante la registrazione dell'utente nel database.");
            }
            return true;
        }
    }

    public User login(String username, String password){
        User u = userList.get(username);
        if(u != null && u.getPassword().equals(password)){
            u.setStatus(true);
            online.put(username, u);
            return u;
        } else{
            return null;
        }
    }

    public boolean logout(User u){
        if(u != null && u.getStatus()){
            u.setStatus(false);
            online.remove(u.getUsername());
            return true;
        } else {
            return false;
        }
    }

    public User getUser(String username){
        return userList.get(username);
    }

    public User getOnline(String username){
        return online.get(username);
    }
}