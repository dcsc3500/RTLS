import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.*;

public class Terminal_GUI extends JFrame {

	Client obj;
	static int x = 435, y = 335;
	JPanel contentPane;
	JPanel pane;
	List_Display pancurr;

	public Terminal_GUI(List_Display panel_list) {		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		
		Border lineBorder = BorderFactory.createLineBorder(Color.black, 2);
		
		pane = new JPanel();
		pane.setBounds(700, 10, 70, 340);
		contentPane.add(pane);
		
		panel_list.setBackground(Color.WHITE);
		panel_list.setBounds(700, 10, 70, 340);
		pancurr = panel_list;
		pane.add(panel_list);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 680, 340);	 
		contentPane.add(panel);
		panel.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 0, 680, 140);
		panel.add(panel_2);
		panel_2.setLayout(null);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(0, 0, 190, 140);
		panel_5.setBorder(lineBorder);
		panel_2.add(panel_5);
		panel_5.setLayout(null);
		
		JLabel lblRoom = new JLabel("Room1");
		lblRoom.setBounds(66, 41, 55, 24);
		panel_5.add(lblRoom);
		lblRoom.setFont(new Font("Consolas", Font.BOLD, 20));
		
		JPanel panel_6 = new JPanel();
		panel_6.setBackground(Color.WHITE);
		panel_6.setBounds(190, 0, 300, 140);
		panel_6.setBorder(lineBorder);
		panel_2.add(panel_6);
		panel_6.setLayout(null);
		
		JLabel lblLobby = new JLabel("Lobby");
		lblLobby.setBounds(121, 44, 102, 18);
		panel_6.add(lblLobby);
		lblLobby.setFont(new Font("Consolas", Font.BOLD, 20));
		
		JPanel panel_7 = new JPanel();
		panel_7.setBackground(Color.WHITE);
		panel_7.setBounds(490, 0, 190, 140);
		panel_7.setBorder(lineBorder);
		panel_2.add(panel_7);
		panel_7.setLayout(null);
		
		JLabel lblRoom_1 = new JLabel("Room2");
		lblRoom_1.setBounds(67, 42, 75, 18);
		panel_7.add(lblRoom_1);
		lblRoom_1.setFont(new Font("Consolas", Font.BOLD, 20));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 200, 680, 140);
		panel.add(panel_3);
		panel_3.setLayout(null);
		
		JPanel panel_9 = new JPanel();
		panel_9.setBackground(Color.WHITE);
		panel_9.setBounds(0, 0, 190, 140);
		panel_9.setBorder(lineBorder);
		panel_3.add(panel_9);
		panel_9.setLayout(null);
		
		JLabel lblRoom_2 = new JLabel("Room3");
		lblRoom_2.setBounds(65, 74, 102, 18);
		panel_9.add(lblRoom_2);
		lblRoom_2.setFont(new Font("Consolas", Font.BOLD, 20));
		
		JPanel panel_10 = new JPanel();
		panel_10.setBackground(Color.WHITE);
		panel_10.setBounds(190, 0, 190, 140);
		panel_10.setBorder(lineBorder);
		panel_3.add(panel_10);
		panel_10.setLayout(null);
		
		JLabel lblRoom_3 = new JLabel("Room4");
		lblRoom_3.setBounds(68, 75, 76, 18);
		panel_10.add(lblRoom_3);
		lblRoom_3.setFont(new Font("Consolas", Font.BOLD, 20));
		
		JPanel panel_11 = new JPanel();
		panel_11.setBackground(Color.WHITE);
		panel_11.setBounds(380, 0, 85, 140);
		panel_11.setBorder(lineBorder);
		panel_3.add(panel_11);
		panel_11.setLayout(null);
		
		JLabel lblExit = new JLabel("EXIT");
		lblExit.setBounds(21, 75, 49, 18);
		panel_11.add(lblExit);
		lblExit.setFont(new Font("Consolas", Font.BOLD, 20));
		
		JPanel panel_12 = new JPanel();
		panel_12.setBackground(Color.WHITE);
		panel_12.setBounds(465, 0, 215, 140);
		panel_12.setBorder(lineBorder);
		panel_3.add(panel_12);
		panel_12.setLayout(null);
		
		JLabel lblWarehouse = new JLabel("Warehouse");
		lblWarehouse.setBounds(61, 75, 102, 18);
		panel_12.add(lblWarehouse);
		lblWarehouse.setFont(new Font("Consolas", Font.BOLD, 20));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.PINK);
		panel_1.setBounds(2, 2, 115, 80);
		panel_12.add(panel_1);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 140, 680, 60);
		panel.add(panel_4);
		panel_4.setLayout(null);
		
		JPanel panel_8 = new JPanel();
		panel_8.setBackground(Color.WHITE);
		panel_8.setBounds(0, 0, 680, 60);
		panel_8.setBorder(lineBorder);
		panel_4.add(panel_8);
		panel_8.setLayout(null);
		
		JLabel lblCorridor = new JLabel("Corridor");
		lblCorridor.setBounds(297, 21, 102, 18);
		panel_8.add(lblCorridor);
		lblCorridor.setFont(new Font("Consolas", Font.BOLD, 20));
		
		//drawRect(this.getGraphics(), 485, 240, 115, 80);
		
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				switch(keycode) {
				case KeyEvent.VK_LEFT : 
					x -= 5; if(x < 20) x += 5; repaint(); 
					if(!checkState(x, y)) obj.state = obj.danger;
					else obj.state = obj.normal;
					break;
				case KeyEvent.VK_RIGHT : 
					x += 5; if(x > 680) x -= 5; repaint(); 
					if(!checkState(x, y)) obj.state = obj.danger;
					else obj.state = obj.normal;
					break;
				case KeyEvent.VK_UP : 
					y -= 5; if(y < 45) y += 5;repaint(); 
					if(!checkState(x, y)) obj.state = obj.danger;
					else obj.state = obj.normal;
					break;
				case KeyEvent.VK_DOWN : 
					y += 5; if(y > 365) y -= 5; repaint(); 
					if(!checkState(x, y)) obj.state = obj.danger;
					else obj.state = obj.normal;
					break;
				}				
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
			
		});
		panel.requestFocus();
		setFocusable(true);
		setContentPane(contentPane);
	}
	
	public JPanel getPane() {
		return pane;
	}
	
	public List_Display getPanCurr() {
		return pancurr;
	}
	
	public void setobj(Client obj) {
		this.obj = obj;
	}
	
	public void drawRect(Graphics g, int x1, int y1, int x2, int y2) {
		g.drawRect(x1, y1, x2, y2);
	}
	
	public boolean checkState(int x, int y) {
		if(x >= 485 && x <= 600 && y >= 240 && y <= 320) {
			return false;
		}
		return true;
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.blue);
		g.fillRect(x, y, 15, 15);
	}
}
