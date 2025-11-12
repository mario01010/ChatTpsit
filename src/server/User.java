import java.util.List;

public abstract class Chat {
    private String ID;
    private List<Message> messaggi;

    public Chat(String ID) {
        this.ID = ID;
    }

    public abstract List<User> getParticipants();

    public List<Message> getMessaggi() {
        return messaggi;
    }

    public void sendMessage(Message message) {
       //TODO
    }

    public String getID() {
        return ID;
    }
}