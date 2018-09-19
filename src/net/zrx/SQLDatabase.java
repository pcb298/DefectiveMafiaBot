package net.zrx;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

public class SQLDatabase {
	private Connection connection;
	
	public SQLDatabase(Connection conn) {
		this.connection = conn;
	}
	public static SQLDatabase connect(String uri, String user, String pass) throws SQLException {
		Connection conn = connection(uri, user, pass);
		return new SQLDatabase(conn);
	}
	private static Connection connection(String uri, String user, String pass) throws SQLException {
		return DriverManager.getConnection(uri, user, pass);
	}
	
	public int update(String sql) throws SQLException {
		Statement stmt = connection.createStatement();
		return stmt.executeUpdate(sql);
	}
	
	public ResultSet query(String sql) throws SQLException {
		Statement stmt = connection.createStatement();
		return stmt.executeQuery(sql);
	}
	
	public PreparedStatementBuilder prepare(String sql, boolean returnIndexes) throws SQLException {
		if (returnIndexes) return new PreparedStatementBuilder(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
		else return new PreparedStatementBuilder(connection.prepareStatement(sql));
	}
	
	public class PreparedStatementBuilder {
		private PreparedStatement pstmt;
		public PreparedStatementBuilder(PreparedStatement pstmt) {
			this.pstmt = pstmt;
		}
		public PreparedStatementBuilder bind(Object[] values) throws SQLException {
			for (int i = 1; i <= values.length; i++) {
				Object o = values[i - 1];
				if (o instanceof String) {
					pstmt.setString(i, (String) o);
				} else if (o instanceof Integer) {
					pstmt.setInt(i, (int) o);
				} else if (o instanceof Double) {
					pstmt.setDouble(i, (double) o);
				} else if (o instanceof Boolean) {
					pstmt.setBoolean(i, (boolean) o);
				} else if (o instanceof Float) {
					pstmt.setFloat(i, (float) o);
				} else if (o instanceof Byte) {
					pstmt.setByte(i, (byte) o);
				} else if (o instanceof Short) {
					pstmt.setShort(i, (short) o);
				} else if (o instanceof Long) {
					pstmt.setLong(i, (long) o);
				} else if (o instanceof Date) {
					pstmt.setDate(i, (Date) o);
				} else if (o instanceof Time) {
					pstmt.setTime(i, (Time) o);
				} else if (o instanceof Timestamp) {
					pstmt.setTimestamp(i, (Timestamp) o);
				} else {		
					pstmt.setNull(i, 0);
				}
			}
			return this;
		}
		public ResultSet query() throws SQLException {
			return pstmt.executeQuery();
		}
		public int update() throws SQLException {
			return pstmt.executeUpdate();
		}
		public ResultSet updateWithKeys() throws SQLException {
			pstmt.executeUpdate();
			return pstmt.getGeneratedKeys();
		}
	}
}
