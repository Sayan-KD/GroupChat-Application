import java.io.*;
import java.net.Socket;

public class Client {
    static void SendMessage(BufferedReader bufferedReader, DataOutputStream dataOutputStream, String clientName){   //Thread To SendMessages to Another Clients.
        Thread sendMessage= new Thread(() -> {
            System.out.println("Start Typing your Messages "+ clientName +"...  ");
            while (true){
                try {
                    String sendmsg=bufferedReader.readLine();
                    dataOutputStream.writeUTF(sendmsg);
                } catch (Exception e) {
                    System.out.println(e);
                    break;
                }
            }
        });
        sendMessage.start();
    }
    static void RecieveMessage(DataInputStream dataInputStream){    //Thread To RecieveMessages from Another Clients.
        Thread recieveMessage= new Thread(() -> {
            while (true){
                try {
                    String rcvmsg=dataInputStream.readUTF();
                    System.out.println(rcvmsg);
                }catch (Exception e){
                    System.out.println(e);
                    break;
                }
            }
        });
        recieveMessage.start();
    }
    static String newClient(BufferedReader bufferedReader,DataInputStream dataInputStream,DataOutputStream dataOutputStream,String choice) throws IOException{  /*Helps to Create new Clients and Groups.*/
        String clientName = null,groupName;
        if(choice.equals("2")){
            dataOutputStream.writeUTF(null +"," + null + ","+ choice);
            System.out.println("Available Groups: ");
            int groupSize = Integer.parseInt(dataInputStream.readUTF());
            for (int i = 0; i < groupSize; i++) {
                System.out.println(dataInputStream.readUTF());
            }
        }
        System.out.print("Enter Client Name: ");
        clientName=bufferedReader.readLine();            //Taking Client Name
        System.out.print("Enter Group Name: ");
        groupName = bufferedReader.readLine();        //Taking Group Name
        System.out.println("----------------------- "+clientName+" -----------------------");
        dataOutputStream.writeUTF(clientName + "," + groupName + ","+ null);
        return clientName;
    }
    public static void main(String[] args) throws IOException {     //Main Method, Program Execution Starts From Here.
        String clientName = null;
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(System.in));
        Socket socket= new Socket("192.168.1.2",8055);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        System.out.println("Connected to Server");
        System.out.println("1. Create New Group\n2. Join New Group\n3. Exit");
        System.out.print("Enter Choice: ");
        String choice= bufferedReader.readLine();
        switch (choice) {
            case "1", "2" -> clientName=newClient(bufferedReader,dataInputStream,dataOutputStream,choice);
            case "3"-> {
                System.out.println("Thank You");
                System.exit(0);
            }
            default -> System.out.println("Wrong Input");
        }
        SendMessage(bufferedReader,dataOutputStream,clientName);
        RecieveMessage(dataInputStream);
    }
}
