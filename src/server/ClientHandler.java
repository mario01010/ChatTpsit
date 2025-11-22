import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatManager chatManager;
    private UserManager userManager;
    private User user;
    private BufferedReader in;
    private PrintWriter out;

    // Contatore semplice per generare ID utente e messaggio temporanei
    private static AtomicInteger userCounter = new AtomicInteger(1);
    private static AtomicInteger messageCounter = new AtomicInteger(1);

    public ClientHandler(Socket socket, ChatManager chatManager, UserManager userManager) {
        this.socket = socket;
        this.chatManager = chatManager;
        this.userManager = userManager;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ciclo login / register
            while (true) {
                out.println("Benvenuto! Digita 'login' o 'register':");
                String command = in.readLine();
                if (command == null) continue;

                if (command.equalsIgnoreCase("login")) {
                    out.println("Username:");
                    String username = in.readLine();
                    out.println("Password:");
                    String password = in.readLine();

                    User u = userManager.getUser(username);
                    if (u != null && u.getPassword().equals(password)) {
                        user = u;
                        user.setStatus(true);
                        break;
                    } else {
                        out.println("Login fallito. Prova di nuovo.");
                    }

                } else if (command.equalsIgnoreCase("register")) {
                    out.println("Scegli un username:");
                    String username = in.readLine();
                    out.println("Scegli una password:");
                    String password = in.readLine();

                    if (userManager.getUser(username) == null) {
                        int id = userCounter.getAndIncrement();
                        // crea User con tutti i parametri richiesti
                        user = new User(id, username, password, true) {
                            // classe astratta anonima
                        };
                        userManager.register(user);
                        break;
                    } else {
                        out.println("Username già esistente. Prova di nuovo.");
                    }

                } else {
                    out.println("Comando non valido.");
                }
            }

            userManager.setClientHandler(user, this);
            out.println("Benvenuto, " + user.getUsername() + "!");

            // Loop per ricevere messaggi
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length < 2) continue;

                int chatId = Integer.parseInt(parts[0]);
                String text = parts[1];

                Chat chat = chatManager.getChatByID(chatId);
                if (chat == null) {
                    out.println("Chat non trovata.");
                    continue;
                }

                String msgId = String.valueOf(messageCounter.getAndIncrement());
                Message msg = new Message(
                        String.valueOf(user.getID()), // senderID
                        text,                        // contenuto
                        msgId,                       // ID messaggio
                        String.valueOf(chat.getID()) // chatID
                );
                chat.addMessage(msg);

                // inoltra a tutti i partecipanti
                for (User u : chat.getParticipants()) {
                    ClientHandler ch = userManager.getClientHandler(u);
                    if (ch != null) {
                        ch.sendMessage(chat, msg);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
            if (user != null) {
                user.setStatus(false);
                userManager.removeClientHandler(user);
            }
        }
    }

    public void sendMessage(Chat chat, Message msg) {
        out.println("[" + chat.getChatType() + " " + chat.getID() + "] " +
                msg.getSenderID() + ": " + msg.getContent());
    }
}
