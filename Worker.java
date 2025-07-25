import java.io.*;
import java.net.Socket;
import java.util.*;

public class Worker implements Runnable{
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private HashMap<String, String> headers = new HashMap<>();

    Worker(Socket socket) throws Exception {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new Exception("cannot open reader/writer!");
        }
    }

    static private StringBuilder fileToStringBuilder(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()){
            sb.append(sc.nextLine()).append('\n');
        }
        return sb;
    }

    static private void embedCss(StringBuilder html){
        String []lines = html.toString().split("\n");
        Arrays.stream(lines)
                .filter(line -> line.strip().startsWith("<link") && line.contains("rel=\"stylesheet\""))
                .forEach(line -> {
                    String link = line.strip().split("href=\"")[1].split("\"")[0];
                    File file = new File("static/" + link);
                    try {
                        StringBuilder cssContents = fileToStringBuilder(file);
                        cssContents.insert(0, "<style>\n");
                        cssContents.append("</style>");
                        int offset = html.indexOf(line);
                        html.replace(offset, offset + line.length(), cssContents.toString());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void run(){
        String line = "";
        try {
            line = reader.readLine();
            String[]parts = line.split(" ");
            String method = parts[0];
//            TODO: handle methods other than GET
            String path = parts[1];
            String version = parts[2].split("/")[1];
            while (true){
                line = reader.readLine();
                if (line == null || line.isEmpty()) break;
                parts = line.split(":(\\s++)?", 2);
                headers.put(parts[0], parts[1]);
            }
            File file = new File("static/index.html");
            StringBuilder html = fileToStringBuilder(file);
            embedCss(html);

            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Connection: close\r\n");
            writer.write("Content-Length: " + html.length() + "\r\n");
            writer.write("\r\n");
            writer.write(html.toString());
            writer.flush();
        } catch (Exception e){
            System.out.println(line);
            System.out.println(e.getMessage());
        } finally {
            try {
                writer.close();
                reader.close();
                socket.close();
            }
            catch (Exception e){
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
