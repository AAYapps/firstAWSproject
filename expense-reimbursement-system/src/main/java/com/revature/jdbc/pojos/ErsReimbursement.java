package com.revature.jdbc.pojos;

import java.awt.image.BufferedImage;
import java.sql.Date;

public class ErsReimbursement {
	long Reimb_ID;
	public long getReimb_ID() {
		return Reimb_ID;
	}
	public void setReimb_ID(long reimb_ID) {
		Reimb_ID = reimb_ID;
	}
	long Reimb_amount;
    Date Reimb_submitted;
	Date ReimbResolved;
    String ReimbDescription = "";
    BufferedImage ReimbReceipt;
    ErsUser ReimbAuthor;
    ErsUser ReimbResolver;
	String Reimb_Type;
	String Reimb_Status;
	public ErsReimbursement() { }
	public ErsReimbursement(long reimb_amount,
			ErsUser reimbAuthor, String reimb_Type) {
		Reimb_amount = reimb_amount;
		ReimbAuthor = reimbAuthor;
		Reimb_Type = reimb_Type;
	}
	public long getReimb_amount() {
		return Reimb_amount;
	}
	public void setReimb_amount(long reimb_amount) {
		Reimb_amount = reimb_amount;
	}
	public Date getReimb_submitted() {
		return Reimb_submitted;
	}
	public void setReimb_submitted(Date reimb_submitted) {
		Reimb_submitted = reimb_submitted;
	}
	public Date getReimbResolved() {
		return ReimbResolved;
	}
	public void setReimbResolved(Date reimbResolved) {
		ReimbResolved = reimbResolved;
	}
	public String getReimbDescription() {
		return ReimbDescription;
	}
	public void setReimbDescription(String reimbDescription) {
		ReimbDescription = reimbDescription;
	}
	public BufferedImage getReimbReceipt() {
		return ReimbReceipt;
	}
	public void setReimbReceipt(BufferedImage reimbReceipt) {
		ReimbReceipt = reimbReceipt;
	}
	public ErsUser getReimbAuthor() {
		return ReimbAuthor;
	}
	public void setReimbAuthor(ErsUser reimbAuthor) {
		ReimbAuthor = reimbAuthor;
	}
	public ErsUser getReimbResolver() {
		return ReimbResolver;
	}
	public void setReimbResolver(ErsUser reimbResolver) {
		ReimbResolver = reimbResolver;
	}
    public String getReimb_Type() {
		return Reimb_Type;
	}
	public void setReimb_Type(String reimb_Type) {
		Reimb_Type = reimb_Type;
	}
	public String getReimb_Status() {
		return Reimb_Status;
	}
	public void setReimb_Status(String reimb_Status) {
		Reimb_Status = reimb_Status;
	}
	@Override
	public String toString() {
		return "ErsReimbursement [Reimb_ID=" + Reimb_ID + ", Reimb_amount=" + Reimb_amount + ", Reimb_submitted="
				+ Reimb_submitted + ", ReimbResolved=" + ReimbResolved + ", ReimbDescription=" + ReimbDescription
				+ ", ReimbReceipt=" + ReimbReceipt + ", ReimbAuthor=" + ReimbAuthor + ", ReimbResolver=" + ReimbResolver
				+ ", Reimb_Type=" + Reimb_Type + ", Reimb_Status=" + Reimb_Status + "]";
	}
	
}
