import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends Thread {
	static HashMap<String, Receiver> client = new HashMap<String, Receiver>();
	static HashMap<String, byte[]> recentData = new HashMap<String, byte[]>();
	static LinkedList<Data> queue = new LinkedList<Data>();
	static int monitor = 0;
	ServerSocket serverSo = null;
	Socket socket = null;
	Server startThread;
	
	Server() {
		startThread = this;
	}

	public void Start() {
		startThread.start();
	}

	public void run() {
		try {
			Collections.synchronizedMap(client);
			serverSo = new ServerSocket(9000);
			ServerWorker thread = new ServerWorker();
			thread.start();
			while (true) {
				socket = serverSo.accept();
				Receiver receiver = new Receiver(socket);
				receiver.start();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public void sendData(byte[] data, String id) {
		try {
			client.get(id).out.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}