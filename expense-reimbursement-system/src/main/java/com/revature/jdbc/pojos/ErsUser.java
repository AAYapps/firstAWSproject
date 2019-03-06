package com.revature.jdbc.pojos;

public class ErsUser {
	private String ersUsername;
	private String ersPassword;
	private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String user_Role;
    
    public ErsUser() { }
    
    public ErsUser(String ersUsername, String ersPassword) {
		super();
		this.ersUsername = ersUsername;
		this.ersPassword = ersPassword;
	}
    
    public ErsUser(String UserFirstName, String UserLastName, String UserEmail) {
		super();
		this.userFirstName = UserFirstName;
		this.userLastName = UserLastName;
		this.userEmail = UserEmail;
	}

	public ErsUser(String ErsUsername, String UserFirstName, String UserLastName,
			String UserEmail) {
		super();
		this.ersUsername = ErsUsername;
		this.userFirstName = UserFirstName;
		this.userLastName = UserLastName;
		this.userEmail = UserEmail;
	}
    
    public String getErsUsername() {
		return ersUsername;
	}
	public void setErsUsername(String ErsUsername) {
		this.ersUsername = ErsUsername;
	}
	public String getErsPassword() {
		return ersPassword;
	}
	public void setErsPassword(String ersPassword) {
		this.ersPassword = ersPassword;
	}
	public String getUserFirstName() {
		return userFirstName;
	}
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	public String getUserLastName() {
		return userLastName;
	}
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public String getUser_Role() {
		return user_Role;
	}

	public void setUser_Role(String user_Role) {
		this.user_Role = user_Role;
	}

	@Override
	public String toString() {
		return "First Name: " + userFirstName + " Last Name: " + userLastName + " Email: " + userEmail;
	}
}
