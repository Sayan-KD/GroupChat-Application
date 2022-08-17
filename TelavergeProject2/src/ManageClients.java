import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
public class ManageClients implements Runnable{
    Socket s;
    private String clientName=null;
    private String groupName=null;
    private String choice=null;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    ManageClients(Socket s, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream=dataOutputStream;
        this.s=s;
    }
    public void clientInfo(){       //Takes All the Clients' Information(clientName, groupName) after a Client Successfully Connected
        try {
            while(true){
                String[] clientInfos=dataInputStream.readUTF().split(",");
                clientName=clientInfos[0];
                groupName=clientInfos[1];
                choice=clientInfos[2];
                if (choice.equals("2")){
                    dataOutputStream.writeUTF(String.valueOf(Server.groups.size()));
                    for (String g:Server.groups.keySet()) {
                        dataOutputStream.writeUTF(g);
                    }
                    clientName=null;
                    groupName=null;
                }
                if(clientName!=null && groupName!=null){
                    System.out.println("New Client ["+clientName+"] is Connected.");
                    Server.groups.computeIfAbsent(groupName, k -> new ArrayList<>());
                    Server.groups.get(groupName).add(this);
                    break;
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void broadCastMessage(String recieveMessage) throws IOException {        //Broadcast Messages of one User among other Users Present in the Same Group.
        for(int i=0;i<Server.groups.get(groupName).size();i++){
            String otherClients=Server.groups.get(groupName).get(i).clientName;
            if(!otherClients.equals(clientName)){
                Server.groups.get(groupName).get(i).dataOutputStream.writeUTF(clientName.toUpperCase()+" : "+recieveMessage);
            }
        }
    }
    public void exitClient() throws IOException {       //This Method Used to Exit a Client From the Group.
        int clientIndex;
        dataInputStream.close();
        dataOutputStream.close();
        clientIndex=Server.groups.get(groupName).indexOf(this);
        System.out.println("Client ["+clientName+"] Exited From the Group ["+groupName+"].");
        broadCastMessage("Client ["+clientName+"] Exited From the Group.");
        Server.groups.get(groupName).remove(clientIndex);
    }
    public  void  closeAll(){       //All the Connections Will be Closed Through this Method.
        try {
            s.close();
            dataInputStream.close();
            dataOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {     //Overrides the run Method present in Runnable Interface.
        String recieveMessage;
        clientInfo();
        while (true){
            try {
                recieveMessage=dataInputStream.readUTF();
                if(recieveMessage.equalsIgnoreCase("exit")){
                    exitClient();
                    break;
                }
                broadCastMessage(recieveMessage);
            }catch (Exception e){
                closeAll();
                System.out.println(e);
                break;
            }
        }
    }
}
