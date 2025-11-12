public class Chat{
    private String ID;
    private List<Message> messaggi;
    private List<User> utenti;

    public Chat(String ID){
        this.ID = ID;
    }

    public List<User> getUtenti() {
        return utenti;
    }

    public List<Message> getMessaggi() {
        return messaggi;
    }

    public void setMessaggi(List<Message> messaggi) {
        this.messaggi = messaggi;
    }

    public void setUtenti(List<User> utenti) {
        this.utenti = utenti;
    }
}