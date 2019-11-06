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
            case 1: // RTDATA 받으면
               id = Byte.toString(data[2]);
               updateData(data, id);
               updateDB(data,id);
               if (Server.monitor == 1)// 모니터 로그인 되어있으면
               {
                  temp = Protocol.makeAllstatProtocol(Server.recentData);
                  Server.sendData(temp, "0");// Monitor로 AllStat보내기
               }
               break;
            case 3: // 메세지 프로토콜 받으면
               id = Byte.toString(data[3]);
               Server.sendData(data, id);
               break;
            case 4:// 로그인 프로토콜
               id = Byte.toString(data[2]);
               Server.client.put(id, receiver);
               if (id.equals("0")) {// 모니터가 로그인할 경우
                  Server.monitor = 1;
                  temp = Protocol.makeAllstatProtocol(Server.recentData);
                  Server.sendData(temp, id);
               }
               else {
                  maria.makeTable(id);
               }
               // 로그인프로토콜을 받음과 동시에 List프로토콜을 클라이언트에게 쏴줌
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