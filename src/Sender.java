import java.io.*;
import java.net.*;
import java.util.*;
import javax.json.*;
import javax.json.stream.JsonGenerator;

// Student name: Teodor Tebeica
// UCID: 30046038


public class Sender {

    private static ArrayList<Object> objList;

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8000;
        objList = new ArrayList<>();
        //loop on object creation until user decides to quit or send objects over socket
        boolean quit = false;
        while (!quit) {
            int objChoice = promptObjectSelection();
            switch (objChoice) {
                case 0:
                    System.out.println("Quitting program...");
                    System.exit(0);
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    createObject(objChoice);
                    break;
                case 7:
                    quit = true;
                    break;
                case 8:
                    //print list of created objects
                    System.out.println(objList.toString());
                    break;
                default:
                    System.out.println("Integer out of range");
                    break;
            }
        }
        //quit loop when done creating objects and want to serialize/send
        serializeObjects(host, port);

    }

    private static int promptObjectSelection() {

        int objChoice;
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println("LIST OF OBJECTS: \n" +
                "1 - SimpleObject\t\t\t -- object with primitives (int and double) \n" +
                "2 - ReferenceObject\t\t\t -- object with field referencing SimpleObject\n" +
                "3 - CircularReference\t\t -- creates two circular reference objects, with an int and partner reference\n" +
                "4 - SimpleArrayObject\t\t -- object with primitive int array\n" +
                "5 - ReferenceArrayObject\t -- object with array of SimpleObject references\n" +
                "6 - CollectionObject\t\t -- object with references using Java's Collection class (ArrayList) \n" +
                "\nEnter 0 to QUIT" +
                "\nEnter 7 to SERIALIZE and SEND object list to Receiver" +
                "\nEnter 8 to SHOW LIST of created objects");
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.print("Enter your choice: ");
        Scanner input = new Scanner(System.in);
        //check valid int input
        while (!input.hasNextInt()) {
            input.next();
            System.out.println("Enter a valid integer:");
        }
        objChoice = input.nextInt();
        return objChoice;

    }

    private static void createObject(int objChoice) {
        switch (objChoice) {
            case 1:
                objList.add(ObjectBuilder.createSimpleObject());
                break;
            case 2:
                objList.add(ObjectBuilder.createReferenceObject());
                break;
            case 3:
                objList.add(ObjectBuilder.createCircularReference());
                break;
            case 4:
                objList.add(ObjectBuilder.createSimpleArrayObject());
                break;
            case 5:
                objList.add(ObjectBuilder.createRefArrayObject());
                break;
            case 6:
                objList.add(ObjectBuilder.createCollectionObject());
                break;
            default:
                break;
        }
    }

    private static File createJsonFile(JsonObject document) {
        File file = new File("sentFile.json");
        try {
            JsonWriter writer = Json.createWriter(new FileOutputStream(file));
            writer.writeObject(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;

    }

    private static void sendFile(String host, int port, File file) {
        try {
            System.out.println("Connecting to " + host + " on port: " + port);
            //create socket to send file
            Socket socket = new Socket(host, port);
            System.out.println("Sender connected to " + socket.getRemoteSocketAddress());
            //open io streams
            OutputStream outputStream = socket.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(file);
            //send file as byte array stream
            byte[] fileBytes = new byte[1024 * 1024];
            int bytesRead = 0;
            while ((bytesRead = fileInputStream.read(fileBytes)) > 0) {
                outputStream.write(fileBytes, 0, bytesRead);
            }
            //close streams/sockets
            fileInputStream.close();
            outputStream.close();
            socket.close();
            System.out.println("File sent!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void prettyPrintJSON(JsonObject document) {
        System.out.println("------------------------------------------------------------------------");
        String jsonString = document.toString();
        StringWriter sw = new StringWriter();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObj = jsonReader.readObject();
        Map<String, Object> map = new HashMap<>();
        map.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(map);
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(jsonObj);
        jsonWriter.close();
        System.out.println(sw.toString());
        System.out.println("------------------------------------------------------------------------");
    }


    private static void serializeObjects(String host, int port) {
        //serialize and send created objects list
        for (Object obj : objList) {
            try {
                Class objClass = obj.getClass();
                System.out.println("*************************************************************************\n");
                System.out.printf("Press ENTER to serialize %s...\n", objClass.getName());
                Scanner input = new Scanner(System.in);
                input.nextLine();
                System.out.println("Serializing object...");
                JsonObject document = Serializer.serializeObject(obj);
                System.out.println("Printing JSON form of the object...");
                prettyPrintJSON(document);
                System.out.println("Creating file...");
                File file = createJsonFile(document);
                System.out.println("Sending file...");
                sendFile(host, port, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
