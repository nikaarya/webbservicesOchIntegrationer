import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static List<String> board = new ArrayList<>();

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            System.out.println(Thread.currentThread().getName());
            while (true) {

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
        } catch (IOException | SQLException e) {
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

    private static void databaseConnection() {
        try {
            Class.forName("jdbc:sqlserver://");
            Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost;username=sa;password=chicagobulls4;database=Everyloop");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from users");
            while(rs.next())
                System.out.println(rs.getString(1) + " " + rs.getString(2));
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    private static void sendJsonResponse(OutputStream outputToClient) throws IOException, SQLException {

        var users = List.of(new User("51", "niar", "losen", "Nika", "Arya", "nika@gmail.com", "0709998877"), new User("52", "annand", "password", "Anna", "Andersson", "anna@gmail.com", "0707771122"));

        Gson gson = new Gson();
        String json = gson.toJson(users);
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
