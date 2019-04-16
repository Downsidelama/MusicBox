import java.util.ArrayList;
import java.util.List;

public class Music {
    private final List<Pair<String, String>> notes;
    private String lyrics = "";

    Music() {
        this.notes = new ArrayList<>();
    }

    void addNote(String note, String length) {
        synchronized (this.notes) {
            this.notes.add(new Pair<>(note, length));
        }
    }

    void addLyrics(String lyrics) {
        synchronized (this) {
            this.lyrics = lyrics;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringOfMusic = new StringBuilder("Notes: ");
        for (Pair<String, String> note : notes) {
            stringOfMusic.append(note.getP1()).append(" ").append(note.getP2()).append(" ");
        }
        stringOfMusic = new StringBuilder(stringOfMusic.toString().trim());
        stringOfMusic.append(",\r\nLyrics: ").append(lyrics);
        return stringOfMusic.toString();
    }

    void clearNotes() {
        this.notes.clear();
    }
}
