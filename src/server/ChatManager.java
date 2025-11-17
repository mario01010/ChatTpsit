import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatManager{
    private Map<String, Chat> chats;
    private Map<String, DM> direct;
    private Map<String, Gruppo> groups;

    public ChatManager(){
        chats = new HashMap<>();
        direct = new HashMap<>();
        groups = new HashMap<>();
    }
    
    public Chat createDM(User u1, User u2) {
        if (u1 != null && u2 != null) {
            DM dm = new DM(UUID.randomUUID().toString(), u1, u2);
            chats.put(dm.getID(), dm);
            direct.put(dm.getID(), dm);
            return dm;
        }
        return null;
    }

    public Chat createGroup(String nome){
        Gruppo g = new Gruppo(UUID.randomUUID().toString(), nome);
        chats.put(g.getID(), g);
        groups.put(g.getID(), g);

        return g;
    }

    public Chat getChatByID(String id){
        return chats.get(id);
    }
    
   public List<Chat> getChatsForUser(User u) {
        List<Chat> result = new ArrayList<>();
        for (Chat c : chats.values()) {
            if (c.getParticipants().contains(u)) {
                result.add(c);
            }
        }
        return result;
    }
}