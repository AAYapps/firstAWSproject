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
import com.revature.jdbc.pojos.ErsReimbursement;
import com.revature.services.ErsService;

@WebServlet("/create")
public class createServlet extends HttpServlet {
	
	private static Logger log = Logger.getLogger(createServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ErsService es = new ErsService();
		HttpSession session = req.getSession();
		Ers user = (Ers)session.getAttribute("sessionUser");
		if (user != null)
		{
			ErsReimbursement er = mapper.readValue(req.getInputStream(), ErsReimbursement.class);
			try {
				if (es.createReimbursement(er, user))
				{
					//log.info(user.getReimbursementByID(user.lastReimbursement()).toString());	
					PrintWriter writer = resp.getWriter();
					writer.write("Complete");
				}
				else
				{
					resp.setStatus(500);
					PrintWriter writer = resp.getWriter();
					writer.write("Could not create a new request.");
				}
			} catch (Exception e) {
				resp.setStatus(406);
				PrintWriter writer = resp.getWriter();
				writer.write("Could not create a new request.\n" + e.getMessage());
			}
		}
		else
		{
			resp.setStatus(406);
			PrintWriter writer = resp.getWriter();
			writer.write("Not logged in");
		}
	}
}
