package com.revature.jdbc.pojos;

import java.util.ArrayList;
import java.util.List;

public final class Ers {
	private ErsUser user;
	private List<ErsReimbursement> reimbursement = new ArrayList<ErsReimbursement>();
	private boolean isFinancialManager = false;
	public ErsUser getUser() {
		return user;
	}
	public Ers(ErsUser user) {
		super();
		if (user != null) {
			if (user.getUser_Role().equals("Finance"))
				isFinancialManager = true;
		}
		this.user = user;
	}
	
	public boolean isFinancialManager() {
		return isFinancialManager;
	}
	public void AddReimbursement(long reimb_amount,
		ErsUser reimbAuthor, String reimb_Type) {
		reimbursement.add(new ErsReimbursement(reimb_amount, reimbAuthor, reimb_Type));
	}
	
	public ErsReimbursement getReimbursementByID(int ID) {
		return reimbursement.get(ID);
	}
	
	public int lastReimbursement() {
		return reimbursement.size() - 1;
	}
	
	public List<ErsReimbursement> getReimbursement() {
		return reimbursement;
	}
}
