public class Gruppo extends Chat {
    private List<User> participants;

    public Gruppo(String ID) {
        super(ID);
    }

    @Override
    public List<User> getParticipants() {
        return participants;
    }

    @Override
    public String getChatType() {
        return "Gruppo";
    }
}