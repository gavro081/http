import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

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
            String message = "<p>hello from my http server!</p>";
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Connection: close\r\n");
            writer.write("Content-Length: " + message.getBytes().length + "\r\n");
            writer.write("\r\n");
            writer.write(message);
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
