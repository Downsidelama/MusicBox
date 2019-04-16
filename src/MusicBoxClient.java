import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MusicBoxClient {

    public static void main(String... args) throws MidiUnavailableException, InterruptedException {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        MidiChannel channel = synthesizer.getChannels()[0];
        MidiConverter midiConverter = new MidiConverter();

        try (Socket socket = new Socket("localhost", 40000);
             Scanner input = new Scanner(socket.getInputStream());
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            output.println(String.format("play %d %d %s", Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]));

            int lastNote = -1;
            while (input.hasNextLine()) {
                if (lastNote != -1) {
                    channel.noteOff(lastNote);
                }
                String response = input.nextLine();
                if(response.equals("FIN")) {
                    break;
                }
                System.out.println(response);
                String note = response.split(" ")[0];
                if ("CDEFGAB".contains(Character.toString(note.charAt(0)))) {
                    int noteInMidi = midiConverter.convertNoteToMidiValue(note);
                    channel.noteOn(noteInMidi, 80);
                    lastNote = noteInMidi;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
