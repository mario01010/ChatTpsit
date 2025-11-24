import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String DB_URL = "jdbc:sqlite:Database/chat.db";
    private Connection conn;

    public DBManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Inserimento utente
    public int addUser(String username, String password, int status) throws SQLException {
        String sql = "INSERT INTO Utente (username, password, status) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, username);
        ps.setString(2, password);
        ps.setInt(3, status);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        int id = -1;
        if (rs.next()) id = rs.getInt(1);
        
        rs.close();
        ps.close();
        return id;
    }

    // Inserimento chat (tipo "DM" o "Gruppo")
    public int addChat(String tipo) throws SQLException {
        String sql = "INSERT INTO Chat (tipo) VALUES (?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, tipo);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = -1;
        if (rs.next()) id = rs.getInt(1);
        rs.close();
        ps.close();
        return id;
    }

    // Aggiungi utente a chat
    public void addUserToChat(int idChat, int idUtente) throws SQLException {
        String sql = "INSERT OR IGNORE INTO ChatUtente (id_chat, id_utente) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idChat);
        ps.setInt(2, idUtente);
        ps.executeUpdate();
        ps.close();
    }

    // Inserimento messaggio
    public int addMessage(int idChat, int idUtente, String content) throws SQLException {
        String sql = "INSERT INTO Messaggio (content, id_chat, id_utente) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, content);
        ps.setInt(2, idChat);
        ps.setInt(3, idUtente);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = -1;
        if (rs.next()) id = rs.getInt(1);
        rs.close();
        ps.close();
        return id;
    }

    // Inserimento gruppo (chat di tipo Gruppo)
    public void addGroup(int idChat, String nome) throws SQLException {
        String sql = "INSERT INTO Gruppo (id_chat, nome) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idChat);
        ps.setString(2, nome);
        ps.executeUpdate();
        ps.close();
    }

    // Lettura messaggi di una chat
    public List<String> getMessages(int idChat) throws SQLException {
        String sql = "SELECT m.id_messaggio, m.content, u.username, m.time " +
                     "FROM Messaggio m " +
                     "JOIN Utente u ON m.id_utente = u.id_utente " +
                     "WHERE m.id_chat = ? " +
                     "ORDER BY m.time ASC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idChat);
        ResultSet rs = ps.executeQuery();

        List<String> messages = new ArrayList<>();
        while(rs.next()) {
            messages.add(rs.getInt("id_messaggio") + " | " +
                         rs.getString("username") + ": " +
                         rs.getString("content") + " (" +
                         rs.getString("time") + ")");
        }
        rs.close();
        ps.close();
        return messages;
    }

    // Chiudi connessione
    public void close() throws SQLException {
        if (conn != null) conn.close();
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT id_utente, username, password, status FROM Utente";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        List<User> users = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id_utente");
            String username = rs.getString("username");
            String password = rs.getString("password");
            int status = rs.getInt("status");
            users.add(new User(id, username, password, status == 1));
        }
        rs.close();
        stmt.close();
        return users;
    }

    public List<Chat> getAllChats() throws SQLException {
        List<Chat> chats = new ArrayList<>();
        
        String chatSql = "SELECT id_chat, tipo FROM Chat";
        Statement chatStmt = conn.createStatement();
        ResultSet chatRs = chatStmt.executeQuery(chatSql);

        while (chatRs.next()) {
            int chatId = chatRs.getInt("id_chat");
            String tipo = chatRs.getString("tipo");
            
            if (tipo.equals("DirectMessage")) {
                List<User> dmUsers = getUsersInChat(chatId);
                if (dmUsers.size() == 2) {
                    DM dm = new DM(chatId, dmUsers.get(0), dmUsers.get(1));
                    
                    // CARICA MESSAGGI
                    List<Message> messages = getMessagesForChat(chatId);
                    for (Message msg : messages) {
                        dm.addMessage(msg);
                    }
                    
                    chats.add(dm);
                }
            } else if (tipo.equals("Gruppo")) {
                String groupSql = "SELECT nome FROM Gruppo WHERE id_chat = ?";
                PreparedStatement groupPs = conn.prepareStatement(groupSql);
                groupPs.setInt(1, chatId);
                ResultSet groupRs = groupPs.executeQuery();
                
                if (groupRs.next()) {
                    String nomeGruppo = groupRs.getString("nome");
                    Gruppo gruppo = new Gruppo(chatId, nomeGruppo);
                    
                    // CARICA PARTECIPANTI
                    List<User> participants = getUsersInChat(chatId);
                    for (User user : participants) {
                        gruppo.addParticipant(user);
                    }
                    
                    // CARICA MESSAGGI
                    List<Message> messages = getMessagesForChat(chatId);
                    for (Message msg : messages) {
                        gruppo.addMessage(msg);
                    }
                    
                    chats.add(gruppo);
                }
                groupRs.close();
                groupPs.close();
            }
        }
        
        chatRs.close();
        chatStmt.close();
        
        // DEBUG
        System.out.println("DBManager: Caricate " + chats.size() + " chat dal database");
        for (Chat chat : chats) {
            System.out.println("DBManager - Chat ID: " + chat.getID() + 
                              ", Tipo: " + chat.getChatType() + 
                              ", Partecipanti: " + chat.getParticipants().size());
        }
        
        return chats;
    }

    // METODO MANCANTE - AGGIUNGI QUESTO
    private List<User> getUsersInChat(int chatId) throws SQLException {
        String sql = "SELECT u.id_utente, u.username, u.password, u.status " +
                    "FROM Utente u " +
                    "JOIN ChatUtente cu ON u.id_utente = cu.id_utente " +
                    "WHERE cu.id_chat = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, chatId);
        ResultSet rs = ps.executeQuery();
        
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id_utente");
            String username = rs.getString("username");
            String password = rs.getString("password");
            boolean status = rs.getInt("status") == 1;
            
            users.add(new User(id, username, password, status) {
                // implementazione anonima
            });
        }
        
        rs.close();
        ps.close();
        
        System.out.println("DBManager - Trovati " + users.size() + " utenti nella chat " + chatId);
        for (User user : users) {
            System.out.println("  - " + user.getUsername() + " (ID: " + user.getID() + ")");
        }
        
        return users;
    }

    // METODO PER CARICARE I MESSAGGI
    private List<Message> getMessagesForChat(int chatId) throws SQLException {
        String sql = "SELECT id_messaggio, content, id_utente, time " +
                 "FROM Messaggio WHERE id_chat = ? ORDER BY time ASC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, chatId);
        ResultSet rs = ps.executeQuery();
        
        List<Message> messages = new ArrayList<>();
        while (rs.next()) {
            String messageId = String.valueOf(rs.getInt("id_messaggio"));
            String content = rs.getString("content");
            String senderId = String.valueOf(rs.getInt("id_utente"));
            String chatIdStr = String.valueOf(chatId);
            
            Message message = new Message(senderId, content, messageId, chatIdStr);
            messages.add(message);
        }
        
        rs.close();
        ps.close();
        
        System.out.println("DBManager - Caricati " + messages.size() + " messaggi per chat " + chatId);
        return messages;
    }
}