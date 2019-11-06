import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class Receiver extends Thread {
	InputStream in;
	OutputStream out;
	int packetSize;
	byte data[] = new byte[1024];

	public Receiver(Socket socket) {
		try {
			out = socket.getOutputStream();
			in = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (in != null) {
				packetSize = in.read(data);
				Data d = new Data(this, data, packetSize);
				Server.queue.offer(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
