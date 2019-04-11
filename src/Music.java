import java.util.ArrayList;
import java.util.List;

public class Music {
	String title;
	List<Pair<String, String>> notes;
	
	public Music(String title) {
		this.title = title;
		this.notes = new ArrayList<>();
	}
	
	public void addNote(String note, String length) {
		this.notes.add(new Pair<String, String>(note, length));
		
	}
}
