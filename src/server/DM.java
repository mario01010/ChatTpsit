public class DirectMessage extends Chat {
    private User participant;

    public DirectMessage(String ID, User participant) {
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