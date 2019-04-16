import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicBox {

    private Map<String, Music> musicMap;
    private List<ClientHandler> clients;
    private ExecutorService executorService;
    private List<MusicPlayer> musicPlayers;

    public static void main(String... strings) {
        new MusicBox();
    }

    public MusicBox() {
        musicMap = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
        clients = new ArrayList<>();
        musicPlayers = new ArrayList<>();
        try (ServerSocket ss = new ServerSocket(40000)) {
            for (; ; ) {
                acceptNewClient(ss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptNewClient(ServerSocket ss) throws IOException {
        ClientHandler musicClient = new ClientHandler(ss.accept(), musicMap, musicPlayers);
        clients.add(musicClient);
        this.executorService.submit(musicClient);
    }
}
