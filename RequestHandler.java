import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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
    private final HashMap<String, String> requestHeaders;
    private final OutputStream outputStream;
    private final BufferedWriter writer;

    RequestHandler(String requestLine, HashMap<String, String> headers, OutputStream outputStream)
            throws IllegalArgumentException {
        // TODO: replace exceptions with relevant error codes
        System.out.println(requestLine);
        String []parts = requestLine.split(" ");
        if (parts.length != 3) throw new RuntimeException("Invalid params.");

        METHODS.valueOf(parts[0]);
        this.method = parts[0];

        String httpVersion = parts[2].strip();
        if (!httpVersion.equals("HTTP/1.1")) throw new RuntimeException("Invalid HTTP version.");

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
        // handle error404
        if (!target.exists())
            return base.resolve("index.html").toFile();
        return target;
    }

    private String getContentType(){
        String []parts = this.requestedResource.getName().split("\\.");
        String extension = parts[parts.length - 1];
        return switch (extension){
            case "jpg" -> "image/jpeg";
            case "jpeg", "gif", "png" -> "image/" + extension;
            case "svg" -> "image/svg+xml";
            case "css", "html" -> "text/" + extension;
            case "js" -> "text/javascript";
            case "json" -> "application/json";
            // this exception will only be thrown if the file exists, but its type is not supported
            default -> throw new RuntimeException("Extension " + extension + " not supported");
        };
    }

    private String getDate() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    private void writeHeaders (int statusCode, int contentLength, String contentType, Map<String, String> extraHeaders)
    throws IOException {
        writer.write("HTTP/1.1 " + statusCode + " " + HttpStatus.getMessage(statusCode) + "\r\n");
        writer.write("Content-Type: " + contentType + "\r\n");
        writer.write("Content-Length: " + contentLength + "\r\n");
        writer.write("Server: GavroHTTP\r\n");
        writer.write("Date: " + getDate() + "\r\n");

        if (extraHeaders != null){
            for (Map.Entry<String, String> header: extraHeaders.entrySet()){
                writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
            }
        }

        writer.write("\r\n");
        writer.flush();
    }

    public void handleRequest() throws IOException {
        switch (method){
            case "GET", "HEAD" -> handleSuccessRequest();
            default -> handleUnsupportedMethod();
        }
    }

    private void handleSuccessRequest() throws IOException{
        String contentType = getContentType();
        if (contentType.isBlank())
            throw new RuntimeException("Unsupported or unknown content type");
        byte[] body = contentType.startsWith("image") ?
                fileToByteArray() :
                textFileToByteArray();

        writeHeaders(200, body.length, contentType, null);

        if (!method.equals("HEAD")) {
            outputStream.write(body);
            outputStream.flush();
        }
    }

    private void handleUnsupportedMethod() throws IOException{
        String contentType = "application/json";
        byte[] message = "{\"detail\": \"Method not allowed.\"}"
                .getBytes(StandardCharsets.UTF_8);

        Map<String, String> extraHeaders = Map.ofEntries(
                Map.entry("Allow", "GET, HEAD")
        );
        writeHeaders(405, message.length, contentType, extraHeaders);
        outputStream.write(message);
        outputStream.flush();
    }


    private StringBuilder fileToStringBuilder() throws FileNotFoundException{
        try (Scanner sc = new Scanner(this.requestedResource)) {
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append('\n');
            }
            return sb;
        }
    }

    private byte[] fileToByteArray() throws IOException{
        return Files.readAllBytes(this.requestedResource.toPath());
    }

    private byte[] textFileToByteArray() throws FileNotFoundException{
        return fileToStringBuilder().toString().getBytes(StandardCharsets.UTF_8);
    }
}
