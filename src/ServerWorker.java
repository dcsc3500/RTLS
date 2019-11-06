import java.util.Iterator;

public class ServerWorker extends Thread {
   int cmdSwitch;
   String id;
   byte temp[];
   MariaDB maria = new MariaDB();

   public void run() {
      while (true) {
         try {
            Thread.sleep(10);
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         Data d = Server.queue.poll();

         if (d != null) {
            byte[] data = d.p;
            int packetsize = d.packetSize;
            Receiver receiver = d.r;
            if (Protocol.isPacket(data, packetsize))
               continue;
            cmdSwitch = checkCMD(data);
            switch (cmdSwitch) {
            case 1: // RTDATA ������
               id = Byte.toString(data[2]);
               updateData(data, id);
               updateDB(data,id);
               if (Server.monitor == 1)// ����� �α��� �Ǿ�������
               {
                  temp = Protocol.makeAllstatProtocol(Server.recentData);
                  Server.sendData(temp, "0");// Monitor�� AllStat������
               }
               break;
            case 3: // �޼��� �������� ������
               id = Byte.toString(data[3]);
               Server.sendData(data, id);
               break;
            case 4:// �α��� ��������
               id = Byte.toString(data[2]);
               Server.client.put(id, receiver);
               if (id.equals("0")) {// ����Ͱ� �α����� ���
                  Server.monitor = 1;
                  temp = Protocol.makeAllstatProtocol(Server.recentData);
                  Server.sendData(temp, id);
               }
               else {
                  maria.makeTable(id);
               }
               // �α������������� ������ ���ÿ� List���������� Ŭ���̾�Ʈ���� ����
               temp = Protocol.makeListProtocol(Server.client);
               Iterator<String> iterator = Server.client.keySet().iterator();
               while (iterator.hasNext()) {
                  id = iterator.next();
                  Server.sendData(temp, id);
               }
               break;
            }
         }
      }

   }
   
   public void updateDB(byte[] data,String id) {
      int x,y;
      byte[] temp = new byte[4];
      int i;
      for(i = 0;i <4;i++){
         temp[i] = data[i+4];
      }
      x = Protocol.byteToInt(temp);
      
      for(i = 0;i <4;i++){
         temp[i] = data[i+8];
      }
      y = Protocol.byteToInt(temp);
      
      maria.insertData(id, data[3], x, y);
   }
   
   public void updateData(byte[] data, String id) {
      byte[] recentRT = new byte[10];
      int i;
      for (i = 0; i < 10; i++) {
         recentRT[i] = data[i + 2];
      }

      Server.recentData.put(id, recentRT);
   }

   int checkCMD(byte[] proto) {
      byte cmd = proto[1];
      if ((byte) 0x00 == cmd)
         return 1;
      else if ((byte) 0x01 == cmd)
         return 2;
      else if ((byte) 0x02 == cmd)
         return 3;
      else if ((byte) 0x10 == cmd)
         return 4;
      else if ((byte) 0x11 == cmd)
         return 5;
      return 0;
   }
}