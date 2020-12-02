package member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OracleConnection {
	private DBConnectionMgr pool;
	
	public OracleConnection() {
		pool = DBConnectionMgr.getInstance();
	}
	public void getList() {
		//java.sql 패키지 에 있는걸 써야한다.
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		try {
			con = pool.getConnection();
			sql="select * from tblMember"; // 자바 코드에 DB 첫시간에 배웠던 쿼리문 삽입하는 과정
			pstmt = con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				String msg = rs.getString(1) + "\t";
				msg +=rs.getString(2) + "\t";
				msg +=rs.getString(3) + "\t";
				msg +=rs.getString(4) ;
				System.out.println(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
	}
	
	
	public static void main(String[] args) {
		OracleConnection ora = new OracleConnection();
		ora.getList();

	}
}
