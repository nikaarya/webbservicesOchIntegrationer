import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        var bytes = System.lineSeparator().getBytes();
        for (var b: bytes) {
            System.out.println(b);
        }

        try {
            Socket socket = new Socket("127.0.0.1", 3000);

            var output = new PrintWriter(socket.getOutputStream());
            output.println("Hello from client\r\n\r\n");
            output.flush();
            //Läs svaret från servern
            var inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                var line = inputFromServer.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                System.out.println(line);
            }
            inputFromServer.close();
            output.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
