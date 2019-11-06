import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Draw extends JFrame {
	String id;
	int x, y;
	Client obj;

	public Draw(String id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public void setLoc(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void paint(Graphics g, int color) {
		super.paint(g);
		if(color == 0)
			g.setColor(Color.blue);
		else g.setColor(Color.red);
		g.fillRect(x, y, 15, 15);
	}

	public void update(Graphics g) {
		super.update(g);
		repaint();
	}
}

public class Client extends Thread {
	String id;
	Socket socket;
	InputStream in;
	OutputStream out;
	Thread receiveThread;
	Monitor_GUI g;
	Terminal_GUI gui;
	CliGUI GUI;
	CliGUI2 cliGUI;
	CliGUI3 MsgGUI;
	ArrayList<String> ClientList = new ArrayList<String>();
	HashMap<String, Draw> saveDraw = new HashMap<String, Draw>();
	LinkedList<byte[]>queue = new LinkedList<byte[]>();
	byte state;
	byte normal = (byte) 0x00, danger = (byte) 0xFF;

	Client(String ip, int port, String id) {
		this.id = id;
		state = normal;
		receiveThread = this;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port));
			out = socket.getOutputStream();
			in = socket.getInputStream();
			byte[] login_protocol = Protocol.Set_LoginProtocol(id);
			out.write(login_protocol);
			start();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void run() {
		byte[] arr = new byte[1024];
		Worker worker = new Worker(this);
		worker.start();
		while (in != null) {
			try {				
				in.read(arr);				
				queue.offer(arr);				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	void GetMsgGUI(CliGUI3 a) {
		this.MsgGUI = a;
	}

	void setMonGUI(Monitor_GUI a) {
		this.g = a;
	}

	void setTerGUI(Terminal_GUI a) {
		this.gui = a;
	}

	void setGUI(CliGUI2 a) {
		this.cliGUI = a;
	}

	void Gui(CliGUI a) {
		this.GUI = a;
	}

	public void sendMessage(String msg, String send_id, String recv_id) throws UnsupportedEncodingException {
		byte[] MsgProtocol = Protocol.Set_MsgProtocol(msg, send_id, recv_id);
		try {
			out.write(MsgProtocol);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
