import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatManager{
    private Map<Integer, Chat> chats;
    private Map<Integer, DM> direct;
    private Map<Integer, Gruppo> groups;
    private DBManager dbManager;

    public ChatManager(){
        chats = new HashMap<>();
        direct = new HashMap<>();
        groups = new HashMap<>();
        dbManager = new DBManager();
    }

    public Chat createDM(User u1, User u2) {
        if (u1 != null && u2 != null) {
            try {
                int chatId = dbManager.addChat("DirectMessage");

                DM dm = new DM(chatId, u1, u2);

                dbManager.addUserToChat(chatId, u1.getID());
                dbManager.addUserToChat(chatId, u2.getID());

                chats.put(dm.getID(), dm);
                direct.put(dm.getID(), dm);

                return dm;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    public Chat createGroup(String nome){
        try{
            int chatId = dbManager.addChat("Gruppo");
            dbManager.addGroup(chatId, nome);

            Gruppo g = new Gruppo(chatId, nome);

            chats.put(g.getID(), g);
            groups.put(g.getID(), g);

            return g;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Chat getChatByID(int id){
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