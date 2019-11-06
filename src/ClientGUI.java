import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class Task extends TimerTask {
	Client obj;
	byte[] data;

	public void setClient(Client c) {
		obj = c;
	}

	public void run() {
		data = Protocol.makeRTDataProtocol(obj.id, obj.state, Terminal_GUI.x, Terminal_GUI.y);
		System.out.println("x = " + Terminal_GUI.x);
		System.out.println("y = " + Terminal_GUI.y);
		System.out.println("state = " + obj.state);
		try {
			obj.out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

class Drawline extends JFrame {
	int x1, y1;
	int x2, y2;
	
	public Drawline(int x, int y) {
		this.x1 = this.x2 = x;
		this.y1 = this.y2 = y;
	}
	
	public void setNewPos(int x, int y) {
		this.x1 = this.x2;
		this.y1 = this.y2;
		this.x2 = x;
		this.y2 = y;
	}
	
	public void drawLine(Graphics g) {
		g.drawLine(x1, y1, x2, y2);
	}
}

class CliGUI extends JFrame {
	Client obj;
	List_Display pancurr;

	CliGUI() {
		setLocation(100, 400);
		setPreferredSize(new Dimension(600, 600));
		JPanel info = new JPanel();
		JPanel btn = new JPanel();
		JButton contact = new JButton("접속");
		JTextField infoText[] = new JTextField[3];
		JLabel infoLabel[] = new JLabel[3];

		for (int i = 0; i < 3; i++) {
			infoText[i] = new JTextField();
		}

		infoLabel[0] = new JLabel("IP");
		infoLabel[1] = new JLabel("PORT");
		infoLabel[2] = new JLabel("ID");

		infoText[0].setText("LocalHost");
		infoText[1].setText("9000");

		for (int i = 0; i < 3; i++) {
			infoLabel[i].setHorizontalAlignment(0);
			infoLabel[i].setFont(new Font("맑은 고딕", Font.BOLD, 30));
			infoText[i].setFont(new Font("맑은 고딕", Font.BOLD, 30));
		}

		btn.add(contact);
		info.setLayout(new GridLayout(3, 2, 0, 0));

		for (int i = 0; i < 3; i++) {
			info.add(infoLabel[i]);
			info.add(infoText[i]);
		}

		this.add(info, BorderLayout.CENTER);
		this.add(btn, BorderLayout.SOUTH);

		contact.addActionListener(new contactSuc(infoText[0], infoText[1], infoText[2]));

		setVisible(true);
		pack();
	}

	void setGUI() {
		obj.Gui(this);
	}

	class contactSuc implements ActionListener {
		Container pane = getContentPane();
		JTextField ip = null, port = null, id = null;

		public contactSuc(JTextField ip, JTextField port, JTextField id) {
			this.ip = ip;
			this.port = port;
			this.id = id;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String ip1 = ip.getText();
			int port1 = Integer.parseInt(port.getText());
			String id1 = id.getText();
			obj = new Client(ip1, port1, id1);// 스레드 시작
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setGUI();
			dispose();
			List_Display panel = new List_Display(obj);
			if (id1.equals("0")) {
				Monitor_GUI frame_monitor = new Monitor_GUI(panel);
				obj.setMonGUI(frame_monitor);
				frame_monitor.setTitle("Monitor #" + id1);
				frame_monitor.setVisible(true);
			} else {
				Terminal_GUI frame = new Terminal_GUI(panel);
				obj.setTerGUI(frame);
				frame.setobj(obj);
				frame.setTitle("Client #" + id1);
				frame.setVisible(true);
				Timer m_timer = new Timer(true);
				Task m_task = new Task();
				m_task.setClient(obj);
				m_timer.schedule(m_task, 0, 5000);
			}
		}
	}
}

class List_Display extends JPanel {
	Client obj;
	ArrayList<JLabel> list = new ArrayList<JLabel>();
	String recv_name;
	String recv_id;
	PopupMenu pm;
	MariaDB maria = new MariaDB();

	List_Display(Client obj) {
		System.out.println("Listpanel 생성자 실행");
		this.obj = obj;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel("List"));
		list.clear();
		Iterator<String> e = obj.ClientList.iterator();

		while (e.hasNext()) {
			System.out.println("iterator 내부루프");
			String client = e.next();
			System.out.println("Listpanel 생성자 client: " + client);
			if (client.equals(obj.id))
				continue;
			if (client.equals("0"))
				list.add(new JLabel("Monitor#" + client));
			else
				list.add(new JLabel("Client#" + client));
		}

		pm = new PopupMenu();
		MenuItem pm_item1 = new MenuItem("message");
		MenuItem pm_item2 = new MenuItem("path");

		pm.add(pm_item1);
		pm.add(pm_item2);

		add(pm);

		for (int i = 0; i < list.size(); i++) {
			JLabel label = list.get(i);
			add(label);
			label.addMouseListener(new MouseListener() {
				Font original;

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (!obj.id.equals("0") && arg0.getClickCount() == 2) {
						String recv_name = label.getText();
						String recv_id = null;
						StringTokenizer stok = new StringTokenizer(recv_name, "#");
						while (stok.hasMoreTokens()) {
							recv_id = stok.nextToken();
						}
						CliGUI2 C_Gui = new CliGUI2(obj, recv_id);
						C_Gui.setTitle("Send To " + recv_name);
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					original = e.getComponent().getFont();
					Map attributes = original.getAttributes();
					attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
					e.getComponent().setFont(original.deriveFont(attributes));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					e.getComponent().setFont(original);

				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					if (obj.id.equals("0")) {
						if (arg0.getButton() == MouseEvent.BUTTON3) {
							pm.show(List_Display.this, arg0.getX(), arg0.getY());
							recv_name = label.getText();
						}
					}
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
				}

			});
		}
		pm_item1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				StringTokenizer stok = new StringTokenizer(recv_name, "#");
				while (stok.hasMoreTokens()) {
					recv_id = stok.nextToken();
				}
				CliGUI2 C_Gui = new CliGUI2(obj, recv_id);
				C_Gui.setTitle("Send To " + recv_name);

			}
		});
		// path 버튼 클릭시 액션 추가 
		pm_item2.addActionListener(new ActionListener() {
				
			public void actionPerformed(ActionEvent arg0) {
				StringTokenizer stok = new StringTokenizer(recv_name, "#");
				while (stok.hasMoreTokens()) {
					recv_id = stok.nextToken();
				}
				maria.select(recv_id);
				Drawline draw = new Drawline(maria.posX.poll(), maria.posY.poll());
				Iterator e = maria.posX.iterator();
				while(e.hasNext()) {
					draw.setNewPos(maria.posX.poll(), maria.posY.poll());
					draw.drawLine(obj.g.getGraphics());
				}
			}
		});
	}

	public void Adjust() {

		ArrayList<String> newlist = new ArrayList<String>();
		newlist.clear();
		String newclient;
		JLabel lbl;
		
		for(int a = 0;a<obj.ClientList.size();a++) {

			newclient = obj.ClientList.get(a);
			boolean already_there = false;
			for(int b = 0;b<list.size();b++)
			{
				lbl = list.get(b);
				String label = lbl.getText();
				StringTokenizer stok = new StringTokenizer(label, "#");
				while (stok.hasMoreTokens()) {
					label = stok.nextToken();
										
				}
				if (newclient.equals(label)) {
					already_there = true;
					break;
				}

			}
			if (already_there == false) {
				newlist.add(newclient);
			}
		}
		
		Iterator<String> i = newlist.iterator();

		while (i.hasNext()) {
			String clnt = i.next();
			JLabel newlabel;
			if (clnt.equals(obj.id))
				continue;
			else if (clnt.equals("0"))
				newlabel = new JLabel("Monitor#" + clnt);
			else
				newlabel = new JLabel("Client#" + clnt);
			list.add(newlabel);
			newlabel.addMouseListener(new MouseListener() {
				Font original;
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (!obj.id.equals("0") && arg0.getClickCount() == 2) {
						String recv_name = newlabel.getText();
						String recv_id = null;
						StringTokenizer stok = new StringTokenizer(recv_name, "#");
						while (stok.hasMoreTokens()) {
							recv_id = stok.nextToken();
						}
						CliGUI2 C_Gui = new CliGUI2(obj, recv_id);
						C_Gui.setTitle("Send To " + recv_name);
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					original = e.getComponent().getFont();
					Map attributes = original.getAttributes();
					attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
					e.getComponent().setFont(original.deriveFont(attributes));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					e.getComponent().setFont(original);

				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					if (obj.id.equals("0")) {
						if (arg0.getButton() == MouseEvent.BUTTON3) {
							pm.show(List_Display.this, arg0.getX(), arg0.getY());
							recv_name = newlabel.getText();
						}
					}
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

			});
			add(newlabel);
			

		}
		updateUI();
	}

}

class CliGUI2 extends JFrame {
	JTextField txtInput;
	JButton btnSend;
	JPanel pane;
	Client obj;
	JTextArea txtDisp;
	String recv_id;

	CliGUI2(Client obj, String recv_id) {
		this.obj = obj;
		this.recv_id = recv_id;
		obj.setGUI(this);
		setLocation(100, 400);
		setPreferredSize(new Dimension(600, 200));

		pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		JPanel sendPan = new JPanel();
		sendPan.setLayout(new BoxLayout(sendPan, BoxLayout.X_AXIS));

		txtDisp = new JTextArea();
		txtDisp.setEditable(false);
		txtDisp.setPreferredSize(new Dimension(520, 500));
		JScrollPane scrollPane = new JScrollPane(txtDisp);
		txtDisp.setLineWrap(true);

		pane.add(scrollPane);

		txtInput = new JTextField();
		txtInput.setPreferredSize(new Dimension(100, 30));
		sendPan.add(txtInput);

		btnSend = new JButton("send");
		btnSend.setPreferredSize(new Dimension(100, 30));
		btnSend.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		btnSend.addActionListener(new ActionSend(txtInput, txtDisp));
		sendPan.add(btnSend);
		pane.add(sendPan);

		add(pane);
		pack();
		setVisible(true);
	}

	class ActionSend implements ActionListener {
		JTextField txtInput = null;
		JTextArea txtDisp = null;

		public ActionSend(JTextField txtInput, JTextArea txtDisp) {
			this.txtInput = txtInput;
			this.txtDisp = txtDisp;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String message = txtInput.getText();
			try {
				obj.sendMessage(message, obj.id, recv_id);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			txtInput.setText("");
			dispose();
		}
	}

	void displayMsg(String msg) {
		txtDisp.setText(txtDisp.getText() + '\n' + msg);
	}
}

class CliGUI3 extends JFrame {
	JTextField txtInput;
	JButton btnSend;
	JPanel pane;
	Client obj;
	JTextArea txtDisp;
	String send_id;

	CliGUI3(Client obj, String send_id) {
		this.obj = obj;
		this.send_id = send_id;
		obj.GetMsgGUI(this);
		setLocation(300, 400);
		setPreferredSize(new Dimension(600, 200));

		pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		JPanel sendPan = new JPanel();
		sendPan.setLayout(new BoxLayout(sendPan, BoxLayout.X_AXIS));

		txtDisp = new JTextArea();
		txtDisp.setEditable(false);
		txtDisp.setPreferredSize(new Dimension(520, 500));
		JScrollPane scrollPane = new JScrollPane(txtDisp);
		txtDisp.setLineWrap(true);

		pane.add(scrollPane);

		txtInput = new JTextField();
		txtInput.setPreferredSize(new Dimension(100, 30));
		sendPan.add(txtInput);

		btnSend = new JButton("send");
		btnSend.setPreferredSize(new Dimension(100, 30));
		btnSend.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		btnSend.addActionListener(new ActionSend(txtInput, txtDisp));
		sendPan.add(btnSend);
		pane.add(sendPan);

		add(pane);
		pack();
		setVisible(true);
	}

	class ActionSend implements ActionListener {
		JTextField txtInput = null;
		JTextArea txtDisp = null;

		public ActionSend(JTextField txtInput, JTextArea txtDisp) {
			this.txtInput = txtInput;
			this.txtDisp = txtDisp;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String message = txtInput.getText();
			try {
				obj.sendMessage(message, obj.id, send_id);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			txtInput.setText("");
			dispose();
		}
	}

	void displayMsg(String msg) {
		txtDisp.setText(txtDisp.getText() + '\n' + msg);
	}
}

public class ClientGUI {
	public static void main(String args[]) {
		CliGUI a = new CliGUI();
	}
}