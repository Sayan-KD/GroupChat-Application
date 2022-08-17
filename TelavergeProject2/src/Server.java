import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    static Map<String, ArrayList<ManageClients>> groups=new HashMap<>();
    public static void main(String[] args) {
        try {
                ServerSocket serversocket= new ServerSocket(8055);
                Socket socket;
                while (true){           //always ready to accept clients
                    socket=serversocket.accept();
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    ManageClients manageClients=new ManageClients(socket,dataInputStream,dataOutputStream);
                    Thread thread= new Thread(manageClients);
                    thread.start();
                }
        }catch (Exception e) {
            System.out.println("SERVER: "+ e);
        }
    }
}
