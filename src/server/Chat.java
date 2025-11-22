import java.util.List;

public abstract class Chat {
    private int ID;
    private List<Message> messaggi;
    private List<User> participants;

    public Chat(int ID) {
        this.ID = ID;
    }

    public abstract List<User> getParticipants();

    public List<Message> getMessaggi() {
        return messaggi;
    }

    public void addMessage(Message message) {
        messaggi.add(message);
    }

    public void addParticipant(User u) {
        participants.add(u);
    }

    public void removeParticipant(User u) {
        participants.remove(u);
    }


    public abstract String getChatType();


    public int getID() {
        return ID;
    }
}