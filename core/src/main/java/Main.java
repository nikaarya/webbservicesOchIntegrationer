import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    public static List<String> board = new ArrayList<>();

    public static void main(String[] args) {

        //Smidigare sätt att starta tråd då man återanvänder trådarna
        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            System.out.println(Thread.currentThread().getName());
            while (true) {
                //Raden ServerSocket öppnar servern och .accept gör att vi väntar på att någon ska ansluta
                Socket client = serverSocket.accept();

                //Starta tråd
//                Thread thread = new Thread(() -> handleConnection(client));
//                thread.start();
                executorService.submit(() -> handleConnection(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleConnection(Socket client) {
        try {
            //getInetAddress visar oss IP numret till klienten som har ansutit sig
//            System.out.println(client.getInetAddress());
//            System.out.println(Thread.currentThread().getName());
//            Thread.sleep(2000);

            //Hämtar och skriver ut info som klienten skickar till oss, Läser in en textrad åt gången
            var inputFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            var url = readRequest(inputFromClient);

            var outputToClient = client.getOutputStream();
            //Routing
            if (url.equals("/dog.jpeg")) {
                sendImageResponse(outputToClient);
            } else {
                sendJsonResponse(outputToClient);
            }

            inputFromClient.close();
            outputToClient.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendImageResponse (OutputStream outputToClient) throws IOException {
        String header = "";
        byte[] data = new byte[0];

        File file = Path.of("core", "target", "web", "dog.jpeg").toFile();
        if (!(file.exists() && !file.isDirectory())) {
            header = "HTTP/1.1 404 Not Found\r\nContent-length: 0\r\n\r\n";
        } else {
            try(FileInputStream fileInputStream = new FileInputStream(file)){
                data = new byte[(int) file.length()];
                fileInputStream.read(data);
                var contentType = Files.probeContentType(file.toPath());
                header = "HTTP/1.1 200 OK\r\nContent-Type: " + contentType + "\r\nContent-length: " + data.length + "\r\n\r\n";

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        outputToClient.write(header.getBytes());
        outputToClient.write(data);

        outputToClient.flush();
    }

    private static void sendJsonResponse(OutputStream outputToClient) throws IOException {
        //Return json info:
        //I Labben ska man ta denna info från en databas
//        List<Person> personss = new ArrayList<>();
//
//            personss.add(new Person("Martin", 43, true));
//            personss.add(new Person("Kalle", 23, false));
//            personss.add(new Person("Anna", 11, true));
//
        var persons = List.of(new Person("Martin", 43, true), new Person("Kalle", 23, false), new Person("Anna", 11, true));

        Gson gson = new Gson();
        String json = gson.toJson(persons);
        System.out.println(json);

        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-length: " + data.length + "\r\n\r\n";
        outputToClient.write(header.getBytes());
        outputToClient.write(data);

        outputToClient.flush();
    }

    private static String readRequest(BufferedReader inputFromClient) throws IOException {

        var url = "";

        while (true) {
            var line = inputFromClient.readLine();
            if (line.startsWith("GET"))
                url = line.split(" ")[1];

            if (line == null || line.isEmpty()) {
                break;
            }
            System.out.println(line);
        }
        return url;
    }
}
