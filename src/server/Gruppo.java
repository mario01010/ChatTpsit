import java.util.ArrayList;
import java.util.List;

public class Gruppo extends Chat {
    private String nome;
    private List<User> participants;

    public Gruppo(String ID, String nome) {
        super(ID);
        this.nome = nome;
        this.participants = new ArrayList<>();
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
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