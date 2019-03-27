package com.revature.jdbc.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
//import java.util.Scanner;

import org.junit.Test;

import com.revature.jdbc.pojos.Ers;
import com.revature.jdbc.pojos.ErsReimbursement;
import com.revature.jdbc.pojos.ErsUser;

public class ErsFactoryTest {

	ErsUser user = new ErsUser("mthompson", "Manuel", "Thompson", "manuel.thompson@gmail.com");
	
//	@Test
//	void testHash() {
//		Scanner c = new Scanner(System.in);
//		System.out.println(ErsFactory.hashString(c.nextLine()));
//	}
	
	public ErsFactoryTest() {
		
	}
	
	@Test
	public void testFindAllReimbursements() {
		Ers ers = new Ers(user);
		ErsFactory dao = new ErsFactory();
		dao.findUserReimbursements(ers, 1);
		for (int i = 0; i <= ers.lastReimbursement(); i++) {
			ErsReimbursement er = ers.getReimbursementByID(i);
			DateFormat df = DateFormat.getInstance();
			String date = df.format(er.getReimb_submitted());
			String dater = "Not yet Resolved";
			if (er.getReimbResolved() != null)
				dater = df.format(er.getReimbResolved());
			System.out.println("Amount: " + er.getReimb_amount() + " Submitted: " + date + 
					" Resolved: " + dater + " Desc " + er.getReimbDescription() + 
					" Author: " + er.getReimbAuthor().toString() + 
					" Resolver: " + er.getReimbResolver().toString() + 
					" Type: " + er.getReimb_Type() + " Status: " + er.getReimb_Status());
		}
	}
	
//	@Test
//	void testGetAllReimbursements() {
//		Ers ers = new Ers(ErsFactory.Login("kswan", "asdf"));
//		ErsFactory.getAllReimbursements(ers);
//		for (int i = 0; i <= ers.lastReimbursement(); i++) {
//			ErsReimbursement er = ers.getReimbursementByID(i);
//			DateFormat df = DateFormat.getInstance();
//			String date = df.format(er.getReimb_submitted());
//			String dater = "Not yet Resolved";
//			if (er.getReimbResolved() != null)
//				dater = df.format(er.getReimbResolved());
//			System.out.println("Amount: " + er.getReimb_amount() + " Submitted: " + date + 
//					" Resolved: " + dater + " Desc " + er.getReimbDescription() + 
//					" Author: " + er.getReimbAuthor().toString() + 
//					" Resolver: " + er.getReimbResolver().toString() + 
//					" Type: " + er.getReimb_Type() + " Status: " + er.getReimb_Status());
//		}
//		
//		System.out.println("pending");
//		ers = new Ers(ErsFactory.Login("kswan", "asdf"));
//		ErsFactory.getAllPendingReimbursements(ers);
//		for (int i = 0; i <= ers.lastReimbursement(); i++) {
//			ErsReimbursement er = ers.getReimbursementByID(i);
//			DateFormat df = DateFormat.getInstance();
//			String date = df.format(er.getReimb_submitted());
//			String dater = "Not yet Resolved";
//			if (er.getReimbResolved() != null)
//				dater = df.format(er.getReimbResolved());
//			System.out.println("Amount: " + er.getReimb_amount() + " Submitted: " + date + 
//					" Resolved: " + dater + " Desc " + er.getReimbDescription() + 
//					" Author: " + er.getReimbAuthor().toString() + 
//					" Resolver: " + er.getReimbResolver().toString() + 
//					" Type: " + er.getReimb_Type() + " Status: " + er.getReimb_Status());
//		}
//		
//		System.out.println("resolved");
//		ers = new Ers(ErsFactory.Login("kswan", "asdf"));
//		ErsFactory.getAllResolvedReimbursements(ers);
//		for (int i = 0; i <= ers.lastReimbursement(); i++) {
//			ErsReimbursement er = ers.getReimbursementByID(i);
//			DateFormat df = DateFormat.getInstance();
//			String date = df.format(er.getReimb_submitted());
//			String dater = "Not yet Resolved";
//			if (er.getReimbResolved() != null)
//				dater = df.format(er.getReimbResolved());
//			System.out.println("Amount: " + er.getReimb_amount() + " Submitted: " + date + 
//					" Resolved: " + dater + " Desc " + er.getReimbDescription() + 
//					" Author: " + er.getReimbAuthor().toString() + 
//					" Resolver: " + er.getReimbResolver().toString() + 
//					" Type: " + er.getReimb_Type() + " Status: " + er.getReimb_Status());
//		}
//	}

//	@Test
//	void testCreateReimbursement() {
//		Ers ers = new Ers(user);
//		ers.AddReimbursement(100, ers.getUser(), "OTHER");
//		ErsFactory.CreateReimbursement(ers);
//	}
	
	@Test
	public void testLogin() {
		ErsFactory dao = new ErsFactory();
		try {
			assertTrue(dao.Login("mthompson", "password") != null);
			assertFalse(dao.Login("mthompson", "passwor") != null);
			assertFalse(dao.Login("mthompso", "password") != null);
			assertFalse(dao.Login("mthompso", "passwor") != null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
//	void testCreateUser() {
//		Ers ers = new Ers(user);
//		ErsFactory.CreateUser(ers);
//	}

}
