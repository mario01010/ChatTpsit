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
            //createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Creazione tabelle
/*    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();

        // User
        stmt.executeUpdate("CREATE TABLE Utente (" +
            "id_utente INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL, " +
            "status INTEGER NOT NULL DEFAULT 0" +
        ");");


        // Chat
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Chat (" +
            "id_chat INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "tipo TEXT NOT NULL" +
        ");");

        // ChatUtente
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ChatUtente (" +
            "id_chat INTEGER NOT NULL, " +
            "id_utente INTEGER NOT NULL, " +
            "PRIMARY KEY (id_chat, id_utente), " +
            "FOREIGN KEY (id_chat) REFERENCES Chat(id_chat) ON DELETE CASCADE, " +
            "FOREIGN KEY (id_utente) REFERENCES Utente(id_utente) ON DELETE CASCADE" +
        ");");

        // Messaggio
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Messaggio (" +
            "id_messaggio INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
            "content TEXT NOT NULL, " +
            "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "id_utente INTEGER NOT NULL, " +
            "id_chat INTEGER NOT NULL, " +
            "FOREIGN KEY (id_utente) REFERENCES Utente(id_utente), " +
            "FOREIGN KEY (id_chat) REFERENCES Chat(id_chat)" +
        ");");

        // Gruppo
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Gruppo (" +
            "id_chat INTEGER PRIMARY KEY, " +
            "nome TEXT NOT NULL, " +
            "FOREIGN KEY (id_chat) REFERENCES Chat(id_chat) ON DELETE CASCADE" +
        ");");

        stmt.close();
    }*/

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
}
