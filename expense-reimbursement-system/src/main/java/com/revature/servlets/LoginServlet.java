package com.revature.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.jdbc.pojos.Ers;
import com.revature.jdbc.pojos.ErsUser;
import com.revature.services.ErsService;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	
	Logger log = Logger.getLogger(LoginServlet.class);
	
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		log.trace("login was reached");
//		ObjectMapper mapper = new ObjectMapper();
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
//		String json = mapper.writeValueAsString(ers) + "\n";
//		for (int i = 0; i <= ers.lastReimbursement(); i++) {
//			json += mapper.writeValueAsString(ers.getReimbursementByID(i)) + "\n";
//		}
//		PrintWriter writer = resp.getWriter();
//		//resp.setContentType("application/html");
//		writer.write(json);
//	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect("login.view");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ErsService es = new ErsService();
		ErsUser u = mapper.readValue(req.getInputStream(), ErsUser.class);
		try {
			Ers ers = es.login(u);
			resp.setStatus(200);
			HttpSession session = req.getSession();
			session.setAttribute("sessionUser", ers);
			resp.sendRedirect("home");
		} catch (Exception e) {
			resp.setStatus(406);
			PrintWriter writer = resp.getWriter();
			resp.setContentType("application/html");
			writer.write(e.getMessage());
		}
	}
	

}