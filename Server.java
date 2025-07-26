import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    private final int port;
    Server(int port){
        this.port = port;
    }

    @Override
    public void run(){
        try (ServerSocket socket = new ServerSocket(port)){
            System.out.println("server listening on port: " + port);
            Socket clientSocket;
            while (true){
                try {
                    clientSocket = socket.accept();
                } catch (Exception e){
                    System.out.println(e.getMessage());
                    continue;
                }
                try {
                    Worker worker = new Worker(clientSocket);
                    new Thread(worker).start();
                    System.out.println("Starting worker for " + socket.getInetAddress());
                } catch (Exception e){
                    System.out.println("Could not start worker: " + e.getMessage());
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server(1234);
        new Thread(server).start();
    }
}
