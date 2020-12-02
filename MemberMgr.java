package member;

import java.sql.Connection; // java.sql 로 import 시켜야한다. 중요************
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import sql.DBConnectionMgr;

//DB와 연동에 필요한 모든 기능을 객체화 시키기
public class MemberMgr {
	
	//DB연결 객체 10개 만들어 놓음. 누가? pool이
	private DBConnectionMgr pool; // 사용하고 반납하고 사용하고 반납하고 하는 것처럼..
	
	public MemberMgr() {
		pool = DBConnectionMgr.getInstance();//객체 생성하기
	}
	
	
	
	/*배열은 일단 1. 동일한 타입이어야 한다. 2. 크기가 한번 지정이 되면 고정이 된다. 
	 * 크기가 고정이지 않고 유동적이며(늘어났다 줄어들었다), 변환이 가능하다(동일하지 않아도 된다), 오브젝트 타입 + 자바 기본형 타입 8가지 전부를 저장할 수 있음. //  wrapper  class -> string과 관련*/
	//리스트 : Victor에 저장되는 타입을 MemberBean으로 지정했다.(그냥 옵션이다, 에러는 안남) -> 이걸 문법적으로 Generic이라고 함. (제네릭)
	
	
	
	public Vector<MemberBean> getListMember() {
		/*DB연결 실행 공식*//* java.sql 로 import 시켜야한다*/
		Connection con = null;//DB 연결 객체 (외워야함)
		PreparedStatement pstmt = null; //sql 문 만드는 객체
		ResultSet rs = null;//select 문 실행 결과값 리턴 객체
		String sql = null; // 변수명을 왜 sql로 했는가?
		Vector<MemberBean>vlist = new Vector<MemberBean>();
		
		try {
			//pool 객체에서 빌려옴(이미10개 만들어 옴)
			con = pool.getConnection();
			//sql문 선언한 것
			sql="select * from tblMember order by idx";
			//DB에 실행하기 위해서 pstmt 문 생성
			pstmt = con.prepareStatement(sql); // String sql = null; // 변수명을 왜 sql로 했는가? 의 답변
			
			rs= pstmt.executeQuery();//DB 실행후 java에 결과값 리턴
			while(rs.next() /*next() 에대한 설명? 현재 cursor 커서에서 다음 cursor로 이동했는데 그 다음 레코드가 또 있을경우 true, 없으면 falses*/) {
				/*현재 while문은 2번만 실행된다.*/
				// 현재 콩 2개 -> 홍길동 레코드 1줄, 강호동 레코드 1줄
			
				
				//길게 코드 쓰는 방법
				/*MemberBean bean = new MemberBean();
				int idx = rs.getInt("idx"); // 컬럼명 idx, 현재 값은 1
				String name = rs.getString("name");
				String phone = rs.getString("phone");
				String team = rs.getString("team");
				
				bean.setIdx(idx); // bean 객체에 레코드에 들어가있던 값을 넣기 
				bean.setName(name);;
				bean.setPhone(phone);;
				bean.setTeam(team);;
				vlist.addElement(bean); // vector에 add시켜야함, 벡타를 던져주는 메소드*/
				
				
				//축약해서 코드 짧게 쓰는 방법
				MemberBean bean = new MemberBean();
				bean.setIdx(rs.getInt("idx"));
				bean.setName(rs.getString("name"));
				bean.setPhone(rs.getString("phone"));
				bean.setTeam(rs.getString("team"));
				//레코드를 저장시킨 beans를 Vector에 저장,
				vlist.addElement(bean);
				
				
			} //----while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//con은 반납, pstmt 이랑 rs는 close 해야한다. 
			pool.freeConnection(con,pstmt,rs);
		}
		return vlist;
	}
	
	//레코드 한개 가져오기<- 만들었던 beans 이용해야함. 
	public MemberBean getMember(int idx) {
	Connection con = null;//DB 연결 객체 (외워야함)
	PreparedStatement pstmt = null; //sql 문 만드는 객체
	ResultSet rs = null;//select 문 실행 결과값 리턴 객체
	String sql = null; // 변수명을 왜 sql로 했는가?
	MemberBean bean = new MemberBean();
	try {
		con = pool.getConnection();
		sql = "select * from tblMember where idx=?";
		//매개변수 idx를 첫번째  물음표 (?) 세팅
		pstmt.setInt(1, idx); // 1은 현재 홍길동, 즉 레코드 1개
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery();
		if(rs.next()) {
			bean.setIdx(rs.getInt(1)); // 테이블 스키마 index 번호(순서번호)
			bean.setName(rs.getString(2));
			bean.setPhone(rs.getString(3));
			bean.setTeam(rs.getString(4));
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		pool.freeConnection(con, pstmt, rs);
	}
	return bean;
	}
	
	//입력
	public boolean insertMember(MemberBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			sql = "insert into tblMember(idx,name,phone,team)"
					+ "values(seqmember.nextval,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bean.getName());
			pstmt.setString(2, bean.getPhone());
			pstmt.setString(3, bean.getTeam());
			int cnt = pstmt.executeUpdate();//insert,update,delete
			if(cnt==1) flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	//수정
	public boolean updateMember(MemberBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			sql = "update tblMember set name =?, phone=?, team=? " + "where idx=?"; // team하고 where하고 띄어쓰기 해야한다
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bean.getName());
			pstmt.setString(2, bean.getPhone());
			pstmt.setString(3, bean.getTeam());
			pstmt.setInt(4, bean.getIdx());
			int cnt = pstmt.executeUpdate();//insert,update,delete
			if(cnt==1) flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}	
	//삭제
	public boolean deleteMember(int idx) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			sql = "delete from tblMember where idx=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, idx);
			int cnt = pstmt.executeUpdate();
			if(cnt==1) flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
	
	
	
	
}
 