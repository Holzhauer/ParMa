/**
 * This file is part of
 * 
 * Parameter Manager (Parma) 0.9
 *
 * Copyright (C) 2010 Center for Environmental Systems Research, Kassel, Germany
 * 
 * ReSolEvo is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ReSolEvo is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Created by Sascha Holzhauer on 19.05.2011
 */
package de.cesr.parma.reader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;

/**
 * 
 * Parameter retrieval from MySQL databases PARameter MAnager
 * 
 * Connects to a MySQL database using parameter values of
 * {@link PmFrameworkPa#LOCATION}, {@link PmFrameworkPa#DBNAME},
 * {@link PmFrameworkPa#USER}, and {@link PmFrameworkPa#PASSWORD} and reads in
 * parameter values from {@link PmFrameworkPa#TBLNAME_PARAMS} (the row that is
 * defined by parameter set id - see
 * {@link #PmDbParameterReader(PmParameterDefinition)}). This reader enables the
 * use of other db settings and tables than defined in PmFrameworkPa via setter
 * methods for theses settings. However, defaults are the values of
 * {@link PmFrameworkPa}. See documentation for further instructions.
 * 
 * @author Sascha Holzhauer
 * @date 29.06.2010
 * 
 */
public class PmDbParameterReader extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(PmDbParameterReader.class);

	public static final String NOT_DEFINED = "NOT DEFINED";
	public static final String NULL_EXPRESSION = "null";

	private Connection con;
	private final PmParameterDefinition paramSetId;

	protected PmParameterManager pm;

	protected PmParameterDefinition dbTable = PmFrameworkPa.TBLNAME_PARAMS;
	protected PmParameterDefinition dbLocation = PmFrameworkPa.LOCATION;
	protected PmParameterDefinition dbName = PmFrameworkPa.DBNAME;
	protected PmParameterDefinition dbUser = PmFrameworkPa.USER;
	protected PmParameterDefinition dbPassword = PmFrameworkPa.PASSWORD;

	/**
	 * Uses the given parameter definition as parameter set ID.
	 * 
	 * @param pm
	 *            the parameter manager to store read parameter at
	 * @param paramSetId
	 *            the parameter definition that specifies the parameter set id
	 *            for which parameter definitions shall be fetched from DB,
	 */
	public PmDbParameterReader(PmParameterManager pm,
			PmParameterDefinition paramSetId) {
		this.pm = pm;
		this.paramSetId = paramSetId;
	}

	/**
	 * Uses the given parameter definition as parameter set ID.
	 * 
	 * @param paramSetId
	 *            the parameter definition that specifies the parameter set id
	 *            for which parameter definitions shall be fetched from DB,
	 */
	public PmDbParameterReader(PmParameterDefinition paramSetId) {
		this(PmParameterManager.getInstance(null), paramSetId);
	}

	/**
	 * Uses {@link PmFrameworkPa#PARAM_SET_ID} as parameter set ID.
	 */
	public PmDbParameterReader() {
		this(PmFrameworkPa.PARAM_SET_ID);
	}

	/**
	 * @see de.cesr.parma.core.PmAbstractParameterReader#initParameters()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	// type of Enum is clear
	@Override
	public void initParameters() {

		try {
			if (con == null || con.isClosed()) {
				con = getConnection();
			}
		} catch (SQLException e1) {
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String t1 = (String) pm.getParam(dbTable);

		if (t1.equals(NOT_DEFINED)) {
			// <- LOGGING
			logger.warn("Table is set to " + NOT_DEFINED
					+ ". Skipping reading parameters from database.");
			// LOGGING ->
		} else {

			String sql = "SELECT * FROM `" + t1 + "` AS t1 WHERE t1.id = "
					+ pm.getParam(this.paramSetId) + ";";
			logger.debug("SQL-statement to fetch params: " + sql);

			ResultSet result = connect(sql);
			try {
				String param_class = "";
				String param_name = "";

				if (!result.next()) {
					logger.error("No parameter set in table for paramID "
							+ pm.getParam(this.paramSetId));
					throw new IllegalStateException(
							"No parameter set in table " + t1 + " for paramID "
									+ pm.getParam(this.paramSetId));
				}

				for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
					try {
						if (logger.isDebugEnabled()) {
							logger.debug("metaData for " + i + ": "
									+ result.getMetaData().getColumnName(i));
						}
						if (result.getMetaData().getColumnName(i).contains(":")) {
							// !!! difficulty! >
							param_class = result.getMetaData().getColumnName(i)
									.split(":")[0];
							param_name = result.getMetaData().getColumnName(i)
									.split(":")[1];

							PmParameterDefinition definition = (PmParameterDefinition) Enum
									.valueOf((Class<Enum>) Class
											.forName(param_class), param_name);
							if (definition.getType() == Class.class) {
								// handle Class.class parameter types:
								if (result.getObject(i).toString()
										.contains(" ")) {
									logger.warn("The class for parameter "
											+ definition
											+ " contains withspaces. Maybe it is some hint (" + result.getObject(i).toString() + ").");
								} else if (result.getObject(i).toString()
										.equals(NULL_EXPRESSION)) {
									pm.setParam(definition, null);
								} else if (result.getObject(i).toString()
										.length() > 0) {
									pm.setParam(definition, Class
											.forName((String) result
													.getObject(i)));
								}
							} else {
								// handle all other parameter types:
								pm.setParam(definition, result.getObject(i));
							}

							logger.info("Parameter "
									+ Enum.valueOf((Class<Enum>) Class
											.forName(param_class), param_name)
									+ " read from" + " database. Value: "
									+ result.getObject(i));
						} else {
							if (!result.getMetaData().getColumnName(i)
									.equals("id")
									&& !result.getMetaData().getColumnName(i)
											.equals("description")) {
								logger.warn("The column '"
										+ result.getMetaData().getColumnName(i)
										+ "' of table "
										+ t1
										+ " is not in proper parameter format (CLASS:PARAMETER_NAME)");
							}
						}

					} catch (IllegalArgumentException e) {
						logger.error(e.getMessage());
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						logger.error("Class " + result.getObject(i)
								+ " not found!" + " (for parameter "
								+ param_class + ":" + param_name + ")");
						e.printStackTrace();
					}
				}
				result.close();
				disconnect();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param sql
	 * @return Created by Sascha Holzhauer on 29.03.2010
	 */
	protected ResultSet connect(String sql) {
		try {
			if (con == null || con.isClosed()) {
				con = getConnection();
			}
			if (sql.startsWith("update:")) {
				con.createStatement().executeUpdate(sql.substring(7));
				return null;
			} else {
				return con.createStatement().executeQuery(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Tries to establish a JDBC Connection to the MySQL database given the
	 * settings provided to the constructor.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

		Properties properties = new Properties();
		properties.put("user", pm.getParam(dbUser));
		properties.put("password", pm.getParam(dbPassword));
		String connectTo = "jdbc:mysql://" + pm.getParam(dbLocation)
				+ (((String) pm.getParam(dbLocation)).endsWith("/") ? "" : "/")
				+ pm.getParam(dbName);

		// error handling:
		if (pm.getParam(dbLocation) == null) {
			throw new IllegalArgumentException(
					"Invalid database settings: Invalid database location!");
		}
		if (pm.getParam(dbName) == null) {
			throw new IllegalArgumentException(
					"Invalid database settings: Invalid database name!");
		}
		if (pm.getParam(dbUser) == null) {
			throw new IllegalArgumentException(
					"Invalid database settings: Invalid user name!");
		}
		if (pm.getParam(dbPassword) == null) {
			throw new IllegalArgumentException(
					"Invalid database settings: Invalid password!");
		}

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		logger.info("Connect to DB: " + connectTo);
		return DriverManager.getConnection(connectTo, properties);
	}

	/**
	 *
	 */
	protected void disconnect() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
