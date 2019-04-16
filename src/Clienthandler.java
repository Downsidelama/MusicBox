import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class ClientHandler implements Runnable, AutoCloseable {

    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private boolean running;
    private Map<String, Music> musicMap;
    private List<MusicPlayer> musicPlayers;

    ClientHandler(Socket s, Map<String, Music> musicMap, List<MusicPlayer> musicPlayers) throws IOException {
        super();
        this.musicMap = musicMap;
        this.socket = s;
        this.input = new Scanner(s.getInputStream());
        this.output = new PrintWriter(s.getOutputStream(), true);
        this.running = true;
        this.musicPlayers = musicPlayers;
    }

    @Override
    public void run() {
        try {
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
                } else if (input.startsWith("play")) {
                    String[] parameters = input.replace("play", "").trim().split(" ");
                    if (parameters.length == 3) {
                        try {
                            play(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), parameters[2]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Not enough parameters!");
                    }

                } else if (input.startsWith("stop")) {
                    int index = Integer.parseInt(input.replace("stop", "").trim());
                    stopPlaying(index);
                } else if (input.startsWith("change")) {
                    String[] parameters = input.replace("change", "").trim().split(" ");
                    if (parameters.length == 3) {
                        changeAttributes(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2]));
                    } else {
                        System.out.println("Not enough parameters!");
                    }
                } else {
                    System.out.println(input);
                }
            }
        } catch (Exception e) {
            // Needed for debug, because of the Executor, exceptions are being raised silently(?).
            e.printStackTrace();
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
        System.out.println(music);
    }

    private void play(int tempo, int transponse, String title) {
        if (musicMap.containsKey(title)) {
            Music music = musicMap.get(title);
            MusicPlayer musicPlayer = new MusicPlayer(music, tempo, transponse) {
                @Override
                void sendToClient(String message) {
                    output.println(message);
                }
            };
            musicPlayers.add(musicPlayer);
            output.println(String.format("playing %d", musicPlayers.indexOf(musicPlayer)));
            musicPlayer.playMusic();
        } else {
            output.println("FIN");
        }
    }

    private void stopPlaying(int index) {
        musicPlayers.get(index).stopMusic();
    }

    private void changeAttributes(int index, int tempo, int transponse) {
        musicPlayers.get(index).changeAttributes(tempo, transponse);
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
