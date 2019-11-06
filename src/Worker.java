import java.util.Iterator;
import javax.swing.JPanel;

public class Worker extends Thread {
	JPanel pane;
	List_Display pancurr;
	Client obj;

	Worker(Client obj) {
		this.obj = obj;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			byte[] arr = null;		
			arr = obj.queue.poll();
			if (arr != null) {
				System.out.println("Worker Thread start");
				switch (arr[1]) {
				case (byte) 0x01: // ALLSTAT 패킷
					System.out.println("Allstat 패킷");
					byte[] tmp = new byte[4];
					int record_num = arr[2];
					int color = 0;
					obj.g.repaint();
					for (int i = 0; i < record_num; i++) {
						for (int j = 0; j < 4; j++)
							tmp[j] = arr[(10 * i) + 5 + j];
						int x = Protocol.byteToInt(tmp);
						for (int j = 0; j < 4; j++)
							tmp[j] = arr[(10 * i) + 9 + j];
						int y = Protocol.byteToInt(tmp);
						String id = Byte.toString(arr[3 + 10 * i]);
						byte state = arr[4 + 10 * i];
						System.out.println(state);
						if(state == obj.normal) color = 0;
						else color = 1;
						Draw d = new Draw(id, x, y);
						d.paint(obj.g.getGraphics(), color);
						obj.saveDraw.put(id, d);
					}
					break;

				case (byte) 0x02:// 메세지 패킷 받았을때
					String msg;
					try {
						System.out.println("메세지 패킷");
						msg = Protocol.Decode_MsgProtocol(arr);
						String sender = Byte.toString(arr[2]);
						CliGUI3 MsgView = new CliGUI3(obj, sender);
						MsgView.setTitle("Receive From C#" + sender);
						obj.MsgGUI.displayMsg(msg);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case (byte) 0x11:// 리스트 패킷 받았을때
					obj.ClientList.clear();
					byte[] CList;
					try {
						System.out.println("리스트 패킷");
						CList = Protocol.Decode_ListProtocol(arr);
						for (int i = 0; i < CList.length; i++) {
							String tmp1 = Byte.toString(CList[i]);
							obj.ClientList.add(tmp1);
						}
						Thread.sleep(100);
						if (obj.id.equals("0")) {
							this.pancurr = obj.g.getPanCurr();
						} else {
							this.pancurr = obj.gui.getPanCurr();
						}
						pancurr.Adjust();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			} 

		}
	}



}
