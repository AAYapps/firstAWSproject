package com.revature.services;

import org.apache.log4j.Logger;

import com.revature.jdbc.dao.ErsFactory;
import com.revature.jdbc.pojos.Ers;
import com.revature.jdbc.pojos.ErsReimbursement;
import com.revature.jdbc.pojos.ErsUser;

public class ErsService {
	ErsFactory ersDao = new ErsFactory();
	
	Logger log = Logger.getLogger(ErsService.class);
	
	public boolean createReimbursement(ErsReimbursement er, Ers user) throws Exception {
		user.AddReimbursement(er.getReimb_amount(), 
				new ErsUser(user.getUser().getUserFirstName(), user.getUser().getUserLastName(), user.getUser().getUserEmail()), 
				er.getReimb_Type());
		user.getReimbursementByID(user.lastReimbursement()).setReimbDescription(er.getReimbDescription());
		return ersDao.CreateReimbursement(user);
	}
	
	public Ers login(ErsUser eu) throws Exception{
		//mthompson
		return new Ers(ersDao.Login(eu.getErsUsername(), eu.getErsPassword()));
	}

	public void getAllReimbursements(Ers user, status stat, int page) {
		switch(stat)
		{
			case pending:
				ersDao.getAllPendingReimbursements(user, page);
				break;
			case resolved:
				ersDao.getAllResolvedReimbursements(user, page);
				break;
			case all:
				ersDao.getAllReimbursements(user, page);
				break;
		}	
	}
	
	public void findAllReimbursements(Ers user, status stat, int page) {
		switch(stat)
		{
			case pending:
				ersDao.findUserPendingReimbursements(user, page);
				break;
			case resolved:
				ersDao.findUserResolvedReimbursements(user, page);
				break;
			case all:
				ersDao.findUserReimbursements(user, page);
				break;
		}	
	}
	
	public void resolveReimb(int ersReimb, ErsUser rUser, int status) throws Exception {
		log.info("in the service resolveReimb");
		ersDao.resolveReimb(ersReimb, rUser.getErsUsername(), status);
	}
	
	public void createUser(ErsUser user) throws Exception {
		log.info(user.toString());
		Ers ers = new Ers(user);
		ersDao.CreateUser(ers);
	}
}
