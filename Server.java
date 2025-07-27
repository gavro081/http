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
            while (true){
                try {
                    Socket clientSocket = socket.accept();
                    new Thread(new Worker(clientSocket)).start();
                } catch (Exception e){
                    System.out.println(e.getMessage());
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
