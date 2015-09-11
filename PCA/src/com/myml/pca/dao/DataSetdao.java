package com.myml.pca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.myml.pca.util.DBConnection;

public class DataSetdao {
	private final static int featureNum = 13;
	@SuppressWarnings({ "static-access", "finally" })
	public double[][] getAllData(String dataset) {
		DBConnection dbcon = new DBConnection();
		String sql = "select * from "+dataset;
		Connection conn = dbcon.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		double[][] array=null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			// 确定有多少个样本（行数）,特征值（列数）自己定义
			int size = 0;
			while (rs.next()) {
				size++;
			}
			array = new double[size][featureNum];
			//将结果存入数组
			rs = ps.executeQuery();
			int k = 0;
			while (rs.next()) {
				for (int j = 0; j < featureNum; j++) {
					array[k][j] = rs.getDouble(j + 2);
					//System.out.print(array[k][j]+"\t");
				}
				k++;
				//System.out.println();
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return array;
		}

	}
}
