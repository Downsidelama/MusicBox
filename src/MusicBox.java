import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MusicBox {

	List<ClientHandler> clients;
	
	public static void main(String... strings) {
		new MusicBox();
	}

	public MusicBox() {
		clients = new ArrayList<>();
		try (ServerSocket ss = new ServerSocket(40000)) {
			while (true) {
				acceptNewClient(ss);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void acceptNewClient(ServerSocket ss) throws IOException {
		ClientHandler musicClient = new ClientHandler(ss.accept());
		clients.add(musicClient);
		Thread t = new Thread(musicClient);
		t.start();
	}
}

class ClientHandler implements Runnable {

	Socket s;

	public ClientHandler(Socket s) {
		super();
		this.s = s;
	}

	@Override
	public void run() {

	}
}
