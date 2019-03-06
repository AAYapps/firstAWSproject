package com.revature.jdbc.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.revature.jdbc.ConnectionFactory;
import com.revature.jdbc.pojos.Ers;
import com.revature.jdbc.pojos.ErsReimbursement;
import com.revature.jdbc.pojos.ErsUser;

public final class ErsFactory {
	
	final static Logger logger = Logger.getLogger(ErsFactory.class);

	public String hashString(String str) {
		String saltedhash = "n0w" + str.hashCode() + "$@1ted";
		String CompleteHash = saltedhash.hashCode() + "";
		return CompleteHash;
	}
	
	public boolean CreateReimbursement(Ers ers) throws Exception {
		ErsReimbursement reimb = ers.getReimbursementByID(ers.lastReimbursement());
		if (reimb.getReimb_amount() > 0) {
			try (Connection conn = ConnectionFactory.getInstance().getConnection()){
				conn.setAutoCommit(false);
				String sql = "{call ERS_Create_REIMBURSEMENT(?, ?, ?, ?, ?, ?)}";
				CallableStatement ps = conn.prepareCall(sql);
				
				ps.setLong(1, reimb.getReimb_amount());
				ps.setString(2, reimb.getReimbDescription());
				ps.setNull(3, java.sql.Types.BLOB);
				ps.setString(4, ers.getUser().getErsUsername());
				ps.setString(5, reimb.getReimb_Type());
				ps.registerOutParameter(6, java.sql.Types.NUMERIC);
				ps.executeUpdate();
				System.out.println(ps.getInt(6));
				if (ps.getInt(6) == 1) {
					conn.commit();
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			throw new Exception("Amount cannot be less than or equal to 0");
		}
		return false;
	}
	
	public void resolveReimb(long id, String rUsername, int status) throws Exception {
		logger.info("in the Dao resolveReimb");
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			conn.setAutoCommit(false);
			logger.info("auto commit false");
			String sql = "{call Ers_Resolve_REIMBURSEMENT(?, ?, ?, ?)}";
			logger.info(sql);
			CallableStatement cs = conn.prepareCall(sql);
			logger.info(id);
			cs.setLong(1, id);
			logger.info(rUsername);
			cs.setString(2, rUsername);
			logger.info(status);
			cs.setInt(3, status);
			logger.info("setting up param 4");
			cs.registerOutParameter(4, java.sql.Types.NUMERIC);
			logger.info("running update");
			cs.executeUpdate();
			logger.info(cs.getInt(4));
			if (cs.getInt(4) == 1) {
				conn.commit();
			} 
			else {
				throw new Exception("Request could not be resolved");
			}
		} catch (SQLException e) {
			logger.error("Error with query");
			e.printStackTrace();
			throw new Exception("Request could not be resolved");
		}
	}
	
	public void findUserReimbursements(Ers ers, int page)
	{
		ers.getReimbursement().clear();
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			String query = "select r.reimb_ID, r.reimb_amount, r.reimb_submitted, r.reimb_resolved, r.reimb_description, " //1-5
			/*6-8*/	+ "u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail, "
			/*9-11*/+ "a.USER_FIRST_NAME as resolverfirst, a.USER_LAST_NAME as resolverlast, a.USER_Email as resolveremail, "
		   /*12-13*/+ "rs.reimb_status as status, rt.reimb_type as type " +
						"from ers_users u " + 
						"inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author " + 
						"inner join ERS_REIMBURSEMENT_STATUS rs on rs.reimb_status_id = r.reimb_status_id " + 
						"inner join ERS_REIMBURSEMENT_TYPE rt on rt.reimb_type_id = r.reimb_type_id " + 
						"left join ers_users a on a.ers_users_id = r.reimb_resolver " + 
						"where u.ers_username = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, ers.getUser().getErsUsername());
			ResultSet rs = statement.executeQuery();
			System.out.println("rs query");
			int count = 0;
			int begin = (page - 1) * 10;
			int end = begin + 10;
			while(rs.next())
			{
				if (count >= begin && count < end)
				{
					ers.AddReimbursement(rs.getLong(2), 
							new ErsUser(rs.getString(6), rs.getString(7), rs.getString(8)), 
							rs.getString(13));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_ID(rs.getLong(1));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_submitted(rs.getDate(3));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolved(rs.getDate(4));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbDescription(rs.getString(5));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolver(new ErsUser(rs.getString(9), rs.getString(10), rs.getString(11)));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_Status(rs.getString(12));
				}
			}
		} catch (SQLException e) {
			logger.error("Error with query");
			e.printStackTrace();
			//return new ArrayList<Account>();
		}
	}
	
	public void findUserPendingReimbursements(Ers ers, int page)
	{
		ers.getReimbursement().clear();
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			String query = "select r.reimb_ID, r.reimb_amount, r.reimb_submitted, r.reimb_resolved, r.reimb_description, " //1-5
			/*6-8*/	+ "u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail, "
			/*9-11*/+ "a.USER_FIRST_NAME as resolverfirst, a.USER_LAST_NAME as resolverlast, a.USER_Email as resolveremail, "
		   /*12-13*/+ "rs.reimb_status as status, rt.reimb_type as type " +
						"from ers_users u " + 
						"inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author " + 
						"inner join ERS_REIMBURSEMENT_STATUS rs on rs.reimb_status_id = r.reimb_status_id " + 
						"inner join ERS_REIMBURSEMENT_TYPE rt on rt.reimb_type_id = r.reimb_type_id " + 
						"left join ers_users a on a.ers_users_id = r.reimb_resolver " + 
						"where u.ers_username = ? and r.reimb_status_id = 1";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, ers.getUser().getErsUsername());
			ResultSet rs = statement.executeQuery();
			System.out.println("rs query");
			int count = 0;
			int begin = (page - 1) * 10;
			int end = begin + 10;
			while(rs.next())
			{
				if (count >= begin && count < end)
				{
					ers.AddReimbursement(rs.getLong(2), 
							new ErsUser(rs.getString(6), rs.getString(7), rs.getString(8)), 
							rs.getString(13));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_ID(rs.getLong(1));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_submitted(rs.getDate(3));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolved(rs.getDate(4));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbDescription(rs.getString(5));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolver(new ErsUser(rs.getString(9), rs.getString(10), rs.getString(11)));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_Status(rs.getString(12));
				}
			}
		} catch (SQLException e) {
			logger.error("Error with query");
			e.printStackTrace();
			//return new ArrayList<Account>();
		}
	}
	
	public void findUserResolvedReimbursements(Ers ers, int page)
	{
		ers.getReimbursement().clear();
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			String query = "select r.reimb_ID, r.reimb_amount, r.reimb_submitted, r.reimb_resolved, r.reimb_description, " //1-5
			/*6-8*/	+ "u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail, "
			/*9-11*/+ "a.USER_FIRST_NAME as resolverfirst, a.USER_LAST_NAME as resolverlast, a.USER_Email as resolveremail, "
		   /*12-13*/+ "rs.reimb_status as status, rt.reimb_type as type " +
						"from ers_users u " + 
						"inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author " + 
						"inner join ERS_REIMBURSEMENT_STATUS rs on rs.reimb_status_id = r.reimb_status_id " + 
						"inner join ERS_REIMBURSEMENT_TYPE rt on rt.reimb_type_id = r.reimb_type_id " + 
						"left join ers_users a on a.ers_users_id = r.reimb_resolver " + 
						"where u.ers_username = ? and r.reimb_status_id != 1";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, ers.getUser().getErsUsername());
			ResultSet rs = statement.executeQuery();
			System.out.println("rs query");
			int count = 0;
			int begin = (page - 1) * 10;
			int end = begin + 10;
			while(rs.next())
			{
				if (count >= begin && count < end)
				{
					ers.AddReimbursement(rs.getLong(2), 
							new ErsUser(rs.getString(6), rs.getString(7), rs.getString(8)), 
							rs.getString(13));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_ID(rs.getLong(1));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_submitted(rs.getDate(3));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolved(rs.getDate(4));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbDescription(rs.getString(5));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolver(new ErsUser(rs.getString(9), rs.getString(10), rs.getString(11)));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_Status(rs.getString(12));
				}
			}
		} catch (SQLException e) {
			logger.error("Error with query");
			e.printStackTrace();
			//return new ArrayList<Account>();
		}
	}
	
	public void getAllReimbursements(Ers ers, int page) {
		ers.getReimbursement().clear();
		if (ers.getUser() == null) {
			return;
		}
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			String query = "select r.reimb_ID, r.reimb_amount, r.reimb_submitted, r.reimb_resolved, r.reimb_description, " //1-4
			/*5-7*/	+ "u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail, "
			/*8-10*/+ "a.USER_FIRST_NAME as resolverfirst, a.USER_LAST_NAME as resolverlast, a.USER_Email as resolveremail, "
		   /*11-12*/+ "rs.reimb_status as status, rt.reimb_type as type " +
						"from ers_users u " + 
						"inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author " + 
						"inner join ERS_REIMBURSEMENT_STATUS rs on rs.reimb_status_id = r.reimb_status_id " + 
						"inner join ERS_REIMBURSEMENT_TYPE rt on rt.reimb_type_id = r.reimb_type_id " + 
						"left join ers_users a on a.ers_users_id = r.reimb_resolver ";
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet rs = statement.executeQuery();
			logger.info("Getting items");
			//int count = 0;
//			for (ErsReimbursement er : ers.getReimbursement()) {
//				logger.info("ID: " + er.getReimb_ID() + " count: " + ++count);
//			}
			int count = 0;
			int begin = (page - 1) * 10;
			int end = begin + 10;
			while(rs.next())
			{
				if (count >= begin && count < end)
				{
					ers.AddReimbursement(rs.getLong(2), 
							new ErsUser(rs.getString(6), rs.getString(7), rs.getString(8)), 
							rs.getString(13));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_ID(rs.getLong(1));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_submitted(rs.getDate(3));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolved(rs.getDate(4));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbDescription(rs.getString(5));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolver(new ErsUser(rs.getString(9), rs.getString(10), rs.getString(11)));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_Status(rs.getString(12));
				}
			}
		} catch (SQLException e) {
			logger.error("Error with query");
			e.printStackTrace();
			//return new ArrayList<Account>();
		}
	}
	
	public void getAllPendingReimbursements(Ers ers, int page) {
		ers.getReimbursement().clear();
		if (ers.getUser() == null) {
			return;
		}
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			String query = "select r.reimb_ID, r.reimb_amount, r.reimb_submitted, r.reimb_resolved, r.reimb_description, " //1-4
			/*5-7*/	+ "u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail, "
			/*8-10*/+ "a.USER_FIRST_NAME as resolverfirst, a.USER_LAST_NAME as resolverlast, a.USER_Email as resolveremail, "
		   /*11-12*/+ "rs.reimb_status as status, rt.reimb_type as type " +
						"from ers_users u " + 
						"inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author " + 
						"inner join ERS_REIMBURSEMENT_STATUS rs on rs.reimb_status_id = r.reimb_status_id " + 
						"inner join ERS_REIMBURSEMENT_TYPE rt on rt.reimb_type_id = r.reimb_type_id " + 
						"left join ers_users a on a.ers_users_id = r.reimb_resolver " + 
						"where r.REIMB_status_ID = 1";
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet rs = statement.executeQuery();
			int count = 0;
			int begin = (page - 1) * 10;
			int end = begin + 10;
			while(rs.next())
			{
				if (count >= begin && count < end)
				{
					ers.AddReimbursement(rs.getLong(2), 
							new ErsUser(rs.getString(6), rs.getString(7), rs.getString(8)), 
							rs.getString(13));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_ID(rs.getLong(1));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_submitted(rs.getDate(3));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolved(rs.getDate(4));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbDescription(rs.getString(5));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolver(new ErsUser(rs.getString(9), rs.getString(10), rs.getString(11)));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_Status(rs.getString(12));
				}
			}
		} catch (SQLException e) {
			logger.error("Error with query");
			e.printStackTrace();
			//return new ArrayList<Account>();
		}
	}
	
	public void getAllResolvedReimbursements(Ers ers, int page) {
		ers.getReimbursement().clear();
		if (ers.getUser() == null) {
			return;
		}
		try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
			String query = "select r.reimb_ID, r.reimb_amount, r.reimb_submitted, r.reimb_resolved, r.reimb_description, " //1-4
			/*5-7*/	+ "u.USER_FIRST_NAME as authorfirst, u.USER_LAST_NAME as authorlast, u.USER_Email as authoremail, "
			/*8-10*/+ "a.USER_FIRST_NAME as resolverfirst, a.USER_LAST_NAME as resolverlast, a.USER_Email as resolveremail, "
		   /*11-12*/+ "rs.reimb_status as status, rt.reimb_type as type " +
						"from ers_users u " + 
						"inner join ers_reimbursement r on u.ers_users_id = r.REIMB_author " + 
						"inner join ERS_REIMBURSEMENT_STATUS rs on rs.reimb_status_id = r.reimb_status_id " + 
						"inner join ERS_REIMBURSEMENT_TYPE rt on rt.reimb_type_id = r.reimb_type_id " + 
						"left join ers_users a on a.ers_users_id = r.reimb_resolver " + 
						"where r.REIMB_status_ID != 1";
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet rs = statement.executeQuery();
			int count = 0;
			int begin = (page - 1) * 10;
			int end = begin + 10;
			while(rs.next())
			{
				if (count >= begin && count < end)
				{
					ers.AddReimbursement(rs.getLong(2), 
							new ErsUser(rs.getString(6), rs.getString(7), rs.getString(8)), 
							rs.getString(13));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_ID(rs.getLong(1));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_submitted(rs.getDate(3));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolved(rs.getDate(4));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbDescription(rs.getString(5));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimbResolver(new ErsUser(rs.getString(9), rs.getString(10), rs.getString(11)));
					ers.getReimbursementByID(ers.lastReimbursement()).setReimb_Status(rs.getString(12));
				}
			}
			//return accounts;
		} catch (SQLException e) {
			logger.error("Error with query");
			e.printStackTrace();
			//return new ArrayList<Account>();
		}
	}
	
	public ErsUser Login(String username, String password) throws Exception {
		try (Connection conn = ConnectionFactory.getInstance().getConnection()){
			conn.setAutoCommit(false);
			String sql = "{call Ers_login(?, ?, ?, ?)}";
			CallableStatement ps = conn.prepareCall(sql);
			ps.setString(1, username);
			ps.setString(2, hashString(password));
			ps.registerOutParameter(3, java.sql.Types.NUMERIC);
			ps.registerOutParameter(4, java.sql.Types.VARCHAR);
			ps.executeUpdate();
			long output = ps.getInt(3);
			logger.info(output);
			switch ((int)output) {
				case 0:
					throw new Exception("Email or password was incorrect");
				case -1 :
					throw new Exception("Email or password was incorrect");
				default:
			     	return getUserInfo(output);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
     	return null;
	}
	
	private ErsUser getUserInfo(long id) {
		if (id > 0) {
			try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
				String query = "select u.ERS_USERNAME, u.user_first_name, u.user_last_name, u.user_email, r.user_role from ers_users u " + 
						"inner join ERS_USER_ROLES r on u.user_role_id = r.ers_user_role_id " + 
						"where u.ers_users_id = ?";
				PreparedStatement statement = conn.prepareStatement(query);
				statement.setLong(1, id);
				ResultSet rs = statement.executeQuery();
				if (rs.next())
				{
					ErsUser user = new ErsUser(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
					user.setUser_Role(rs.getString(5));
					return user;
				}
				//return accounts;
			} catch (SQLException e) {
				logger.error("Error with query");
				e.printStackTrace();
				//return new ArrayList<Account>();
			}
		}
		
		return null;
	}

	public boolean CreateUser(Ers user) throws Exception {
		try (Connection conn = ConnectionFactory.getInstance().getConnection()){
			if (user.getUser().getErsUsername().equals(""))
			{
				throw new Exception("User Required");
			}
			if (user.getUser().getUserEmail().equals(""))
			{
				throw new Exception("Email Required");
			}
			if (user.getUser().getErsPassword().equals(""))
			{
				throw new Exception("Password Required");
			}
			if (user.getUser().getUserFirstName().equals(""))
			{
				throw new Exception("First Name Required");
			}
			if (user.getUser().getUserLastName().equals(""))
			{
				throw new Exception("Last Name Required");
			}
			
			conn.setAutoCommit(false);
			String query = "select * from ers_users u where u.ers_username = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, user.getUser().getErsUsername());
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				throw new Exception("User exists");
			}
			
			query = "select * from ers_users u where u.user_email = ?";
			statement = conn.prepareStatement(query);
			statement.setString(1, user.getUser().getUserEmail());
			rs = statement.executeQuery();
			if (rs.next()) {
				throw new Exception("Email was already used");
			}
			
			String sql = "{call ErsCreateAccount(?, ?, ?, ?, ?, ?)}";
			CallableStatement ps = conn.prepareCall(sql);
			
			ps.setString(1, user.getUser().getErsUsername());
			ps.setString(2, hashString(user.getUser().getErsPassword()));
			ps.setString(3, user.getUser().getUserFirstName());
			ps.setString(4, user.getUser().getUserLastName());
			ps.setString(5, user.getUser().getUserEmail());
			ps.registerOutParameter(6, java.sql.Types.NUMERIC);
			ps.executeUpdate();
			if (ps.getInt(6) == 1)
			{
				conn.commit();
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
}
