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

    public static void main(String... strings) {
        new MusicBox();
    }

    public MusicBox() {
        musicMap = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
        clients = new ArrayList<>();
        try (ServerSocket ss = new ServerSocket(40000)) {
            for (; ; ) {
                acceptNewClient(ss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptNewClient(ServerSocket ss) throws IOException {
        ClientHandler musicClient = new ClientHandler(ss.accept(), musicMap);
        clients.add(musicClient);
        this.executorService.submit(musicClient);
    }
}

class ClientHandler implements Runnable, AutoCloseable {

    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private boolean running;
    private Map<String, Music> musicMap;

    ClientHandler(Socket s, Map<String, Music> musicMap) throws IOException {
        super();
        this.musicMap = musicMap;
        this.socket = s;
        this.input = new Scanner(s.getInputStream());
        this.output = new PrintWriter(s.getOutputStream());
        this.running = true;
    }

    @Override
    public void run() {
        while (this.running && this.input.hasNextLine()) {
            String input = this.input.nextLine();

            if (input.startsWith("addlyrics")) {
                String title = input.replace("addlyrics", "").trim();
                String lyrics = this.input.nextLine();
                addLyrics(title, lyrics);
            } else if (input.startsWith("add")) {
                String title = input.replace("add", "").trim();
                String notes = this.input.nextLine();
                addMusic(title, notes);
            } else {
                System.out.println(input);
            }
        }
        try {
            this.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addMusic(String title, String notes) {
        Music music = this.musicMap.getOrDefault(title, new Music());
        String[] splittedNotes = notes.split(" ");
        if (splittedNotes.length % 2 == 0) {
            music.clearNotes();
            for (int i = 0; i < splittedNotes.length; i += 2) {
                music.addNote(splittedNotes[i], splittedNotes[i + 1]);
            }
            System.out.println(music);
            this.musicMap.put(title, music);
        } else {
            System.out.println("Bad notes format!");
        }
    }

    private void addLyrics(String title, String lyrics) {
        Music music = this.musicMap.getOrDefault(title, new Music());
        music.addLyrics(lyrics);
    }

    @Override
    public void close() throws Exception {
        this.input.close();
        this.output.close();
        this.socket.close();
    }

    void dropClient() {
        this.running = false;
    }
}
