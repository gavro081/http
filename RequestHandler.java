import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

enum METHODS {
    GET,
    HEAD,
    PUT,
    POST,
    PATCH,
    DELETE
}

public class RequestHandler {
    private final String method;
    private final File requestedResource;
    private HashMap<String, String> requestHeaders;
    private final OutputStream outputStream;
    private final BufferedWriter writer;

    RequestHandler(String requestLine, HashMap<String, String> headers, OutputStream outputStream){
        // TODO: replace exceptions with relevant error codes
        System.out.println(requestLine);
        String []parts = requestLine.split(" ");
        if (parts.length != 3) throw new RuntimeException("Invalid params.");

        this.method = parts[0];

        String httpVersion = parts[2];
        if (!httpVersion.startsWith("HTTP/")) throw new RuntimeException("Invalid HTTP version.");

        this.requestedResource = getFile(parts[1]);
        this.requestHeaders = headers;

        // stream is for bytes, writer is for text
        this.outputStream = outputStream;
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    private static File getFile(String requestTarget) {
        Path base = Path.of("reactapp/dist").toAbsolutePath().normalize();
        String relativePath = requestTarget.equals("/") ? "index.html" : requestTarget.substring(1);
        Path resolved = base.resolve(relativePath).normalize();
        if (!resolved.startsWith(base)) throw new RuntimeException("Forbidden: Path escape attempt");

        File target = resolved.toFile();
        if (!target.exists())
            // resort to react client-side routing
            return base.resolve("index.html").toFile();
        return target;
    }


    private String getContentType(File file){
        String []parts = file.getName().split("\\.");
        String extension = parts[parts.length - 1];
        return switch (extension){
            // TODO: add others
            case "jpg" -> "image/jpeg";
            case "jpeg", "gif", "png" -> "image/" + extension;
            case "css", "html" -> "text/" + extension;
            case "js" -> "text/javascript";
            case "json" -> "application/json";
            case "svg" -> "image/svg+xml";
            // this exception will only be thrown if the file exists, but its type is not supported
            default -> throw new RuntimeException("Extension " + extension + " not supported");
        };
    }

    private LinkedHashMap<String, String> writeHeaders(int contentLength, String contentType){
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", Integer.toString(contentLength));
        return headers;
    }

    public void handleRequest() throws IOException {
        // currently only handles successful get requests :)
        if (this.method.equals("GET")){
            // if we make it here the requested resource must exist
            String contentType = getContentType(requestedResource);
            if (contentType.isBlank()) throw new RuntimeException();
            byte[] fileContentsBytes;
            if (contentType.startsWith("image")) {
                fileContentsBytes = fileToByteArray(requestedResource);
            } else {
                fileContentsBytes = fileToStringBuilder(this.requestedResource)
                        .toString()
                        .getBytes(StandardCharsets.UTF_8);
            }
            LinkedHashMap<String, String> responseHeaders = this.writeHeaders(fileContentsBytes.length, contentType);
            writer.write("HTTP/1.1 200 OK\r\n");
            responseHeaders.forEach((key, val) -> {
                try {
                    writer.write(key + ": " + val + "\r\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writer.write("\r\n");
            writer.flush();
            outputStream.write(fileContentsBytes);
            outputStream.flush();
        }
        // implement other methods ...
    }
    static private StringBuilder fileToStringBuilder(File file) throws FileNotFoundException {
        try (Scanner sc = new Scanner(file)) {
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append('\n');
            }
            return sb;
        }
    }

    static private byte[] fileToByteArray(File file) throws IOException{
        return Files.readAllBytes(file.toPath());
    }
}