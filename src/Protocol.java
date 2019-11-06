import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class Protocol {
	static byte[] Set_LoginProtocol(String id) {
		byte tmp = Byte.parseByte(id);
		byte[]LoginProtocol = new byte[4];
		LoginProtocol[0] = (byte) 0x02;
		LoginProtocol[1] = (byte) 0x10;
		LoginProtocol[2] = tmp;
		LoginProtocol[3] = (byte) 0x03;
		return LoginProtocol;
	}

	static String Decode_LoginProtocol(byte[] p) throws Exception {
		if (p[0] != (byte) 0x02 || p[1] != (byte) 0x10 || p[3] != (byte) 0x03)
			throw new Exception("메세지 전송 오류");
		String id = Byte.toString(p[2]);
		return id;

	}
	static String Decode_MsgProtocol(byte[] p) throws Exception {

		if ((p[0] != (byte) 0x02))
			throw new Exception("메세지 전송 오류");
		int i;
		for (i = 4;; i++) {
			if (p[i] == (byte) 0x03)
				break;
		}
		if (p.length == i)
			throw new Exception("메세지 전송 오류");

		String msg = null;
		msg = new String(p, 4, i - 4, "UTF-8");

		return msg;

	}

	static byte[] Set_MsgProtocol(String msg, String send_id, String recv_id) throws UnsupportedEncodingException {
		byte[] tmp = msg.getBytes();
		byte[]msg_protocol = new byte[1024];

		msg_protocol[0] = (byte) 0x02;
		msg_protocol[1] = (byte) 0x02;
		msg_protocol[2] = Byte.parseByte(send_id);
		msg_protocol[3] = Byte.parseByte(recv_id);
		int i = 4;
		int j = 0;
		int cnt = 0;
		for (cnt = 0; cnt < tmp.length; cnt++) {//tmp의 길이만큼
			msg_protocol[i++] = tmp[j++];
		}
		msg_protocol[i] = (byte) 0x03;
		byte[]MsgProtocol = new byte[i+1];
		for(int k = 0;k<=i;k++)
		{
			MsgProtocol[k] = msg_protocol[k];
		}
		return MsgProtocol;

	}

	static byte[] Decode_ListProtocol(byte[] p) throws Exception {
		if (p[0] != (byte) 0x02)
			throw new Exception("메세지 전송 오류");
		int n = p[2];
		if (p[n + 2 + 1] != (byte) 0x03)
			throw new Exception("메세지 전송 오류");
		byte[] tmp = new byte[n];
		int i = 0;
		int j = 3;
		for (i = 0; i < n; i++) {
			tmp[i] = p[j++];
		}
		return tmp;

	}

	static byte[] makeRTDataProtocol(String id, byte state, int x, int y) {
		byte[] protocol = new byte[13];
		byte[] tmp;
		protocol[0] = (byte) 0x02;
		protocol[1] = (byte) 0x00;
		protocol[2] = Byte.parseByte(id);
		protocol[3] = state;
		tmp = intToByte(x);
		for (int i = 0; i <= 3; i++)
			protocol[i + 4] = tmp[i];
		tmp = intToByte(y);
		for (int i = 0; i <= 3; i++)
			protocol[i + 8] = tmp[i];
		protocol[12] = (byte) 0x03;
		return protocol;
	}

	static byte[] makeListProtocol(HashMap<String, Receiver> client) {
		int getNum = client.size();
		int i;
		byte[] data = new byte[getNum + 4];
		byte[] tmp;
		String id;
		Iterator<String> iterator = client.keySet().iterator();

		data[0] = (byte) 0x02;
		data[1] = (byte) 0x11;
		tmp = intToByte(getNum);
		data[2] = tmp[3];
		i = 3;
		while (iterator.hasNext()) {
			id = iterator.next();
			data[i++] = Byte.parseByte(id);
		}
		data[i] = (byte) 0x03;
		return data;
	}

	static byte[] makeAllstatProtocol(HashMap<String, byte[]> recentData) {
		int getNum = recentData.size();
		int dataSize = getNum * 10 + 4;
		byte[] data = new byte[dataSize];
		byte[] tmp;
		int i, j;
		String key;
		Iterator<String> iterator = recentData.keySet().iterator();

		data[0] = (byte) 0x02;
		data[1] = (byte) 0x01;
		tmp = intToByte(getNum);
		data[2] = tmp[3]; // size 넣기
		for (i = 0; i < getNum; i++) {
			key = iterator.next();
			tmp = recentData.get(key);
			for (j = 0; j < 10; j++) {
				data[i * 10 + 3 + j] = tmp[j];
			}
		}
		data[dataSize - 1] = (byte) 0x03;
		return data;
	}

	static byte[] intToByte(int num) {
		byte[] data = new byte[4];
		data[0] |= (byte) ((num & 0xFF000000) >> 24);
		data[1] |= (byte) ((num & 0xFF0000) >> 16);
		data[2] |= (byte) ((num & 0xFF00) >> 8);
		data[3] |= (byte) (num & 0xFF);
		return data;
	}

	static int byteToInt(byte[] arr) {
		return (arr[0] & 0xff) << 24 | (arr[1] & 0xff) << 16 | (arr[2] & 0xff) << 8 | (arr[3] & 0xff);
	}

	public static boolean isPacket(byte[] temp, int dataSize) {
		if ((byte) 0x02 == temp[0] && (byte) 0x03 == temp[dataSize - 1]) {
			return false;
		}
		return true;
	}
}
