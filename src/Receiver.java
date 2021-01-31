import java.io.*;
import java.net.*;
import javax.json.*;

public class Receiver extends Thread {

    private ServerSocket serverSocket;

    public Receiver(int port){
        try{
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(300000);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        while (true){
            try{
                System.out.println("Receiver (Server) running on " + serverSocket.getLocalPort());
                Socket socket = serverSocket.accept();
                System.out.println("Receiver (Server) connected to " + socket.getRemoteSocketAddress());
                File file =  new File("receivedFile.json");
                //io streams
                InputStream inputStream = socket.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                //receive file from Sender client as byte array
                byte[] fileBytes = new byte[1024 * 1024];
                int bytesRead = 0;
                while((bytesRead =  inputStream.read(fileBytes)) > 0){
                    fileOutputStream.write(fileBytes, 0, bytesRead);
                    break;
                }
                System.out.println("received file!");
                JsonReader reader = Json.createReader(new FileInputStream(file));
                JsonObject jsonObject = reader.readObject();
                Object obj = Deserializer.deserialize(jsonObject);
                //visualize object (via reflective inspection)
                System.out.println("\n======================================================");
                Inspector inspectorGadget = new Inspector();
                inspectorGadget.inspect(obj, true);
                System.out.println("\n======================================================");
                socket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        int port = 8000;
        try{
            Thread serverThread = new Receiver(port);
            serverThread.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



}
