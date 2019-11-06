import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class MariaDB {
	Connection conn;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;
	LinkedList<Integer> posX = new LinkedList<Integer>();
	LinkedList<Integer> posY = new LinkedList<Integer>();
   
   MariaDB(){
         try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/client","root","maria");         
            System.out.println(conn);
            stmt = conn.createStatement();   
            System.out.println("마리아 생성");
         } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
            
   }
   
   public void makeTable(String id) {
      try {
         String id2 = "c" + id;
         String sql = "create table "+ id2
                  +"("
                  +"state tinyint(1),"
                  +"x int(4),"
                  +"y int(4)"
                  +")";
         stmt.executeQuery(sql);
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public void insertData(String id, byte state,int x,int y) {
      try {
         String sql = "insert into c" + id +" values("+ state +","+ x +","+ y +")";
         String client = "client";
         stmt.executeUpdate(sql);
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
	public void select(String id) {
		try {
			String sql = "select * from c" + id;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				posX.add(rs.getInt("X"));
				posY.add(rs.getInt("Y"));
				System.out.println("x = " + rs.getInt("X"));
				System.out.println("y = " + rs.getInt("Y"));
			}
		}
		catch(SQLException e) {			
			e.printStackTrace();
		}
	}
}