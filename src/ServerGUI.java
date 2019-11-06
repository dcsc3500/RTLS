import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

class SerGUI extends JFrame{
	JPanel one = new JPanel();
	JPanel panCurr = null;
	JPanel panNew = null;
	boolean checkPan;
	Server thread1 = new Server();
	JButton Start;
	JButton Stop;
	Server myServer;
	
	SerGUI(){
		checkPan = true;
		myServer = new Server();
		one.setLayout(new BoxLayout(one, BoxLayout.Y_AXIS));
		
		setLocation(500, 400);
		setPreferredSize(new Dimension(225,220));
		Start = new JButton("Server Start");
		Stop = new JButton("Server Stop");

		Start.setPreferredSize(new Dimension(300, 150));
		Start.setFont(new Font("¸¼Àº°íµñ", Font.BOLD, 30));
		Stop.setPreferredSize(new Dimension(300, 150));
		Stop.setFont(new Font("¸¼Àº°íµñ", Font.BOLD, 30));

		one.add(Start);
		one.add(Stop);
		
		Start.setEnabled(true);
		Stop.setEnabled(false);
		
		Start.addActionListener(new ChangePan());
		Stop.addActionListener(new ChangePan());
		
		add(one);
		
		setVisible(true);
		pack();
	}
	
	class ChangePan implements ActionListener
	{
		Container pane = getContentPane();
		

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if(checkPan == true) {
				Start.setEnabled(false);
				Stop.setEnabled(true);
				myServer.Start();
				checkPan = false;
			}
			else {
				Start.setEnabled(true);
				Stop.setEnabled(false);
				checkPan = true;
			}			
		}
	}
}

public class ServerGUI {
	public static void main(String args[]) {
		SerGUI a = new SerGUI();
	}
}
