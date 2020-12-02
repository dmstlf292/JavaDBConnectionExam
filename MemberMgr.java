package member;

import java.sql.Connection; // java.sql �� import ���Ѿ��Ѵ�. �߿�************
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import sql.DBConnectionMgr;

//DB�� ������ �ʿ��� ��� ����� ��üȭ ��Ű��
public class MemberMgr {
	
	//DB���� ��ü 10�� ����� ����. ����? pool��
	private DBConnectionMgr pool; // ����ϰ� �ݳ��ϰ� ����ϰ� �ݳ��ϰ� �ϴ� ��ó��..
	
	public MemberMgr() {
		pool = DBConnectionMgr.getInstance();//��ü �����ϱ�
	}
	
	
	
	/*�迭�� �ϴ� 1. ������ Ÿ���̾�� �Ѵ�. 2. ũ�Ⱑ �ѹ� ������ �Ǹ� ������ �ȴ�. 
	 * ũ�Ⱑ �������� �ʰ� �������̸�(�þ�� �پ�����), ��ȯ�� �����ϴ�(�������� �ʾƵ� �ȴ�), ������Ʈ Ÿ�� + �ڹ� �⺻�� Ÿ�� 8���� ���θ� ������ �� ����. //  wrapper  class -> string�� ����*/
	//����Ʈ : Victor�� ����Ǵ� Ÿ���� MemberBean���� �����ߴ�.(�׳� �ɼ��̴�, ������ �ȳ�) -> �̰� ���������� Generic�̶�� ��. (���׸�)
	
	
	
	public Vector<MemberBean> getListMember() {
		/*DB���� ���� ����*//* java.sql �� import ���Ѿ��Ѵ�*/
		Connection con = null;//DB ���� ��ü (�ܿ�����)
		PreparedStatement pstmt = null; //sql �� ����� ��ü
		ResultSet rs = null;//select �� ���� ����� ���� ��ü
		String sql = null; // �������� �� sql�� �ߴ°�?
		Vector<MemberBean>vlist = new Vector<MemberBean>();
		
		try {
			//pool ��ü���� ������(�̹�10�� ����� ��)
			con = pool.getConnection();
			//sql�� ������ ��
			sql="select * from tblMember order by idx";
			//DB�� �����ϱ� ���ؼ� pstmt �� ����
			pstmt = con.prepareStatement(sql); // String sql = null; // �������� �� sql�� �ߴ°�? �� �亯
			
			rs= pstmt.executeQuery();//DB ������ java�� ����� ����
			while(rs.next() /*next() ������ ����? ���� cursor Ŀ������ ���� cursor�� �̵��ߴµ� �� ���� ���ڵ尡 �� ������� true, ������ falses*/) {
				/*���� while���� 2���� ����ȴ�.*/
				// ���� �� 2�� -> ȫ�浿 ���ڵ� 1��, ��ȣ�� ���ڵ� 1��
			
				
				//��� �ڵ� ���� ���
				/*MemberBean bean = new MemberBean();
				int idx = rs.getInt("idx"); // �÷��� idx, ���� ���� 1
				String name = rs.getString("name");
				String phone = rs.getString("phone");
				String team = rs.getString("team");
				
				bean.setIdx(idx); // bean ��ü�� ���ڵ忡 ���ִ� ���� �ֱ� 
				bean.setName(name);;
				bean.setPhone(phone);;
				bean.setTeam(team);;
				vlist.addElement(bean); // vector�� add���Ѿ���, ��Ÿ�� �����ִ� �޼ҵ�*/
				
				
				//����ؼ� �ڵ� ª�� ���� ���
				MemberBean bean = new MemberBean();
				bean.setIdx(rs.getInt("idx"));
				bean.setName(rs.getString("name"));
				bean.setPhone(rs.getString("phone"));
				bean.setTeam(rs.getString("team"));
				//���ڵ带 �����Ų beans�� Vector�� ����,
				vlist.addElement(bean);
				
				
			} //----while
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//con�� �ݳ�, pstmt �̶� rs�� close �ؾ��Ѵ�. 
			pool.freeConnection(con,pstmt,rs);
		}
		return vlist;
	}
	
	//���ڵ� �Ѱ� ��������<- ������� beans �̿��ؾ���. 
	public MemberBean getMember(int idx) {
	Connection con = null;//DB ���� ��ü (�ܿ�����)
	PreparedStatement pstmt = null; //sql �� ����� ��ü
	ResultSet rs = null;//select �� ���� ����� ���� ��ü
	String sql = null; // �������� �� sql�� �ߴ°�?
	MemberBean bean = new MemberBean();
	try {
		con = pool.getConnection();
		sql = "select * from tblMember where idx=?";
		//�Ű����� idx�� ù��°  ����ǥ (?) ����
		pstmt.setInt(1, idx); // 1�� ���� ȫ�浿, �� ���ڵ� 1��
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery();
		if(rs.next()) {
			bean.setIdx(rs.getInt(1)); // ���̺� ��Ű�� index ��ȣ(������ȣ)
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
	
	//�Է�
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

	//����
	public boolean updateMember(MemberBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			sql = "update tblMember set name =?, phone=?, team=? " + "where idx=?"; // team�ϰ� where�ϰ� ���� �ؾ��Ѵ�
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
	//����
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
 