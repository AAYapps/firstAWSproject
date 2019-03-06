package com.revature.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet("*.view")
public class LoadViewsServlet extends HttpServlet {

	private static Logger log = Logger.getLogger(LoadViewsServlet.class);
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri[] = req.getRequestURI().split("/");
		String resource = uri[uri.length - 1];
		log.info(req.getRequestURI());
		
		String resourcePath = "/partials/" + resource.substring(0, resource.length() - 5) + ".html";
		//String absoluteFilePath = getServletContext().getResource(req.getRequestURI().substring(0, req.getRequestURI().length() - 5) + ".html").getFile();
		//log.info(absoluteFilePath);
		try {
			if (req.getRequestDispatcher(resourcePath) != null) {
				req.getRequestDispatcher(resourcePath).forward(req, resp);
			}
			else
			{
				req.getRequestDispatcher("/partials/error.html").forward(req, resp);	
			}
		} catch (Exception e) {
			log.error(e.getMessage().split("\n")[0]);
			log.error(e.getMessage().split("\n")[1]);
			log.error(e.getMessage().split("\n")[2]);
			log.error(e.getMessage().split("\n")[3]);
			log.error(e.getMessage().split("\n")[4]);
			log.error(e.getMessage().split("\n")[5]);
			log.error(e.getMessage().split("\n")[6]);
		}
		log.info(resourcePath);
	}
}
