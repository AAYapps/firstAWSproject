package com.revature.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.jdbc.pojos.ErsUser;
import com.revature.services.ErsService;

@WebServlet("/createuser")
public class NewUserServlet extends HttpServlet {
	Logger log = Logger.getLogger(NewUserServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		UserData user = mapper.readValue(req.getInputStream(), UserData.class);
		ErsService es = new ErsService();
		log.info(user.toString());
		if (user.passCheck())
		{
			ErsUser newUser = new ErsUser(user.getUsername(), user.getFirstname(), user.getLastname(), user.getEmail());
			newUser.setErsPassword(user.getPassword());
			newUser.setUser_Role("Employee");
			log.info(newUser.toString());
			try {
				es.createUser(newUser);
			} catch (Exception e) {
				resp.setStatus(406);
				//e.printStackTrace();
				PrintWriter writer = resp.getWriter();
				writer.write(e.getMessage());
			}
		}
		else
		{
			resp.setStatus(406);
			PrintWriter writer = resp.getWriter();
			writer.write("Passwords don't match");
		}
	}
}

class UserData {
	private String username;
	private	String password;
	private	String confirm;
	private	String firstname;
	private	String lastname;
	private	String email;
	public UserData() { }
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean passCheck() {
		return password.equals(confirm);
	}
	@Override
	public String toString() {
		return "UserData [username=" + username + ", password=" + password + ", confirm=" + confirm + ", firstname="
				+ firstname + ", lastname=" + lastname + ", email=" + email + "]";
	}
	
}