import java.util.Map;

public class HttpStatus {
    public static final Map<Integer, String> STATUS_MESSAGES = Map.ofEntries(
            Map.entry(100, "Continue"),
            Map.entry(200, "OK"),
            Map.entry(201, "Created"),
            Map.entry(400, "Bad Request"),
            Map.entry(401, "Unauthorized"),
            Map.entry(403, "Forbidden"),
            Map.entry(404, "Not Found"),
            Map.entry(405, "Method Not Allowed"),
            Map.entry(500, "Internal Server Error")
            );

    public static String getMessage(int statusCode) {
        String message =  STATUS_MESSAGES.get(statusCode);
        if (message == null){
            throw new IllegalArgumentException("Unknown status code: " + statusCode);
        }
        return message;
    }
}
