package com.defectivemafia.bot.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.defectivemafia.bot.App;

import net.zrx.Logger;
import net.zrx.SQLDatabase;
import net.zrx.SQLDatabase.PreparedStatementBuilder;

public class Database {
	private final String TAG = this.getClass().getSimpleName();
	private SQLDatabase db;
	private Logger Log = App.Log;
	
	public Database(String uri, String user, String pass) {
		try {
			this.db = SQLDatabase.connect(uri, user, pass);
			Log.i(TAG, "connected to database server and table at " + uri);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			// e.printStackTrace();
			System.exit(2);
		}
	}
	public int update(String sql) {
		try {
			return this.db.update(sql);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return -1;
	}
	public ResultSet query(String sql) {
		try {
			return this.db.query(sql);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	public PreparedStatementBuilder prepare(String sql) {
		return prepare(sql, false);
	}
	public PreparedStatementBuilder prepare(String sql, boolean returnIndexes) {
		try {
			return this.db.prepare(sql, returnIndexes);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
