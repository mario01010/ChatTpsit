import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;

public class Server {
    private DataOutputStream out;
    private BufferedReader in;
    private Socket client;
    private ServerSocket server;

    private List<Message> messages = new ArrayList<>();

    private int port = 2000;

    public Server() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server online sulla porta " + port);
        client = server.accept();

        out = new DataOutputStream(client.getOutputStream());
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    public void sendMessage(Message message) throws IOException {
        String line;
        while( (line = in.readLine()) != null) {
            String[] parts = line.split(";", 5);
            Message msg = new Message(parts[0], parts[1], parts[2], parts[3]);
            messages.add(msg);
            
            System.out.println("Messaggio ricevuto: " + msg.getContent());
        }
    }
}