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

import com.revature.jdbc.pojos.Ers;
import com.revature.services.ErsService;

@WebServlet("/resolve")
public class ReimbursementServlet extends HttpServlet {
	
	Logger log = Logger.getLogger(ReimbursementServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Ers user = (Ers)session.getAttribute("sessionUser");
		String result = "";
		if (user == null)
		{
			resp.setStatus(409);
		}
		else
		{
			String[] resolve = req.getReader().readLine().split(",");
			try {
				ErsService es = new ErsService();
				if (resolve[0].equals("Approve")) {
					log.info("Approved");
					es.resolveReimb(Integer.parseInt(resolve[1]), user.getUser(), 2);
					result = "Approved";
				}
				else if (resolve[0].equals("Deny"))
				{
					
					log.info("Denied");
					es.resolveReimb(Integer.parseInt(resolve[1]), user.getUser(), 3);
					result = "Denied";
				}
			} catch (Exception e) {
				log.info(e.getMessage());
				//e.printStackTrace();
				resp.setStatus(500);
				result = e.getMessage();
			}	
		}
		PrintWriter writer = resp.getWriter();
		writer.println(result);
	}
}
