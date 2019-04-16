import java.util.List;

abstract class MusicPlayer {
    private int index;
    private int transponse;
    private int tempo;
    private boolean playing;
    private Music music;


    MusicPlayer(Music music, int tempo, int transponse) {
        this.music = music;
        this.tempo = tempo;
        this.transponse = transponse;
    }

    void playMusic() {
        this.playing = true;
        MidiConverter midiConverter = new MidiConverter();
        List<Pair<String, String>> notes = generateNotes(music.getNotes());
        int lyricsIndex = 0;
        int i = 0;
        while (this.playing && i < notes.size()) {
            String partion = notes.get(i).getP1();

            if (!notes.get(i).getP1().equals("R")) {
                partion = midiConverter.transponseNote(partion, transponse);
                partion += String.format(" %s", music.getLyricsAt(lyricsIndex));
                lyricsIndex++;
            }
            sendToClient(partion);
            try {
                Thread.sleep((long) tempo * Integer.parseInt(notes.get(i).getP2()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        sendToClient("FIN");
    }

    private List<Pair<String, String>> generateNotes(List<Pair<String, String>> notes) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getP1().equals("REP")) {
                Pair<String, String> rep = notes.remove(i);
                String[] secondPair = rep.getP2().split(";");
                int start = i - Integer.parseInt(secondPair[0]);
                int iteration = Integer.parseInt((secondPair[1]));

                for (int j = 0; j < iteration; j++) {
                    for (int k = i - 1; k >= start; k--) {
                        notes.add(i, notes.get(k));
                    }
                }
            }
        }
        return notes;
    }

    abstract void sendToClient(String message);

    void stopMusic() {
        synchronized (this) {
            playing = false;
        }
    }

    void changeAttributes(int tempo, int transponse) {
        synchronized (this) {
            this.tempo = tempo;
            this.transponse = transponse;
        }
    }
}
