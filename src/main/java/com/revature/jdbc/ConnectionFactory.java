package com.revature.jdbc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import java.util.Properties;

public class ConnectionFactory {
	
	final static Logger logger = Logger.getLogger(ConnectionFactory.class);
	
	private static ConnectionFactory cf = null;
	private ConnectionFactory() {}
	
	public static synchronized ConnectionFactory getInstance()
	{
		if (cf == null)
		{
			cf = new ConnectionFactory();
			logger.info("Returning cf " + cf.getClass());			
		}
		return cf;
	}
	
	//5 key interface with jdbc manages our connection to (session with) the db
	
	public Connection getConnection()
	{
		Connection conn = null;
		Properties prop = new Properties();
		try {
			InputStream is = ConnectionFactory.class.getResourceAsStream("/db.properties");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			prop.load(reader);
			Class.forName(prop.getProperty("driver"));
			conn = DriverManager.getConnection(prop.getProperty("url"), prop.getProperty("username"), prop.getProperty("pwd"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
}
