import java.util.List;

public class DM extends Chat {
    private User participant;

    public DM(String ID, User participant) {
        super(ID);
        this.participant = participant;
    }

    @Override
    public List<User> getParticipants() {
        return List.of(participant);
    }

    @Override
    public String getChatType() {
        return "DirectMessage";
    }
}