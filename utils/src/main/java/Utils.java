import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static String parseUrl (String input) {
//        int firstSpace = input.indexOf(' ') + 1;
//        int secondSpace = input.indexOf(' ', firstSpace);
//
//        return input.substring(firstSpace, secondSpace);

        String[] result = input.split(" ");
        return result[1];

    }

    public static HTTPType parseHttpRequestType(String input) {
        if (input.startsWith("G"))
            return HTTPType.GET;
        else if (input.startsWith("H"))
            return HTTPType.HEAD;
        else if (input.startsWith("PO"))
            return HTTPType.POST;

        throw new RuntimeException("Invalid type");
    }

    public Request parseHttpHeader(String input) {
        var request = new Request();
        request.type = parseHttpRequestType(input);
        request.url = parseUrl(input);
        return request;
    }

    public static void handleRequest(Request request) {
        switch (request.type) {
            case GET -> System.out.println("Handle GET request");
            case HEAD -> System.out.println("Handle HEAD request");
            case POST -> System.out.println("Handle POST request");
        }
    }

    public String message(){
        return "Hello from Utils";
    }

}
