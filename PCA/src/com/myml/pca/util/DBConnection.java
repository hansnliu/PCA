package com.myml.pca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	public static Connection getConnection(){
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/ml";
			conn = DriverManager.getConnection(url, "root", "1234");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	/**
	 * 关闭数据库连接
	 * @param conn Connection对象
	 */
	public static void closeConnection(Connection conn){
		if(conn != null){
			try {
				conn.close();	
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
