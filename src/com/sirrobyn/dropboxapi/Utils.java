package com.sirrobyn.dropboxapi;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

public class Utils {
	
	public CLOB stringToCLOB (String str) {
		Connection conn = null;
		
		try { 
			conn = DriverManager.getConnection("jdbc:default:connection:");
			CLOB clob = CLOB.createTemporary(conn, false, CLOB.DURATION_CALL);
			//clob.putString(1, str);  
			clob.putChars(1, str.toCharArray());
			return clob;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	public BLOB stringToBLOB (String str) {
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection("jdbc:default:connection:");
			BLOB blob = BLOB.createTemporary(conn, false, BLOB.DURATION_CALL);
			blob.putBytes(0, str.getBytes());
			return blob;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	public InputStream blobToInputstream (BLOB blob) {
		
		try {
			return blob.getBinaryStream();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getRandomName () {
		Date date = new Date();
		Random random = new Random(100);
		return Math.abs(date.getTime() + random.nextLong()) + "";
	}
	
	public static void main(String[] args) {
		System.out.println(getRandomName());
	}

}
