import java.io.*;
import java.net.Socket;
import java.util.*;


public class Worker implements Runnable{
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    Worker(Socket socket) throws Exception {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
    }

    @Override
    public void run(){
        try {
            String requestLine = reader.readLine();
            String []parts;
            HashMap<String, String> headers = new HashMap<>();
            String line;
            while (true){
                line = reader.readLine();
                if (line == null || line.isEmpty()) break;
                parts = line.split(":(\\s++)?", 2);
                headers.put(parts[0], parts[1]);
            }
            RequestHandler handler = new RequestHandler(requestLine, headers, socket.getOutputStream());
            handler.handleRequest();
        } catch (Exception e){
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
