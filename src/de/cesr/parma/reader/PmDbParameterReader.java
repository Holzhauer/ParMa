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



import static de.cesr.parma.core.PmParameterManager.getParameter;
import static de.cesr.parma.core.PmParameterManager.setParameter;

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
 * Parameter retrieval from MySQL databases
 * PARameter MAnager
 * 
 * This reader enables the use of other db settings and tables than defined in PmFrameworkPa
 * via setter methods for theses settings. However, defaults are the values of {@link PmFrameworkPa}.
 * 
 * @author Sascha Holzhauer
 * @date 29.06.2010
 * 
 */
public class PmDbParameterReader extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(PmDbParameterReader.class);

	private Connection		con;
	private PmParameterDefinition paramSetId;

	
	protected PmParameterDefinition dbTable 	= PmFrameworkPa.TBLNAME_PARAMS;
	protected PmParameterDefinition dbLocation	= PmFrameworkPa.LOCATION;
	protected PmParameterDefinition dbName		= PmFrameworkPa.DBNAME	;
	protected PmParameterDefinition dbUser		= PmFrameworkPa.USER;
	protected PmParameterDefinition dbPassword	= PmFrameworkPa.PASSWORD;
	
	/**
	 * @param paramSetId the parameter definition that specifies the parameter set id for which parameter definitions shall be fetched from DB,
	 */
	public PmDbParameterReader(PmParameterDefinition paramSetId) {
		this.paramSetId = paramSetId;
	}

	/**
	 * 
	 */
	public PmDbParameterReader() {
		this.paramSetId = PmFrameworkPa.PARAM_SET_ID;
	}
	/**
	 * @see de.cesr.parma.core.PmAbstractParameterReader#initParameters()
	 */
	@SuppressWarnings("unchecked")
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
		
		String t1 = (String) getParameter(dbTable);

		String sql = "SELECT * FROM `" + t1 + "` AS t1 WHERE t1.id = " + getParameter(this.paramSetId) + ";";
		logger.debug("SQL-statement to fetch params: " + sql);

		
		ResultSet result = connect(sql);
		try {
			String param_class = "";
			String param_name = "";

			if (!result.next()) {
				logger.error("No parameter set in table for paramID "
						+ PmParameterManager.getParameter(this.paramSetId));
				throw new IllegalStateException("No parameter set in table for paramID "
						+ PmParameterManager.getParameter(this.paramSetId));
			}

			for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
				try {
					if (logger.isDebugEnabled()) {
						logger.debug("metaData for " + i + ": " + result.getMetaData().getColumnName(i));
					}
					if (result.getMetaData().getColumnName(i).contains(":")) {
						// !!! difficulty! >
						param_class = result.getMetaData().getColumnName(i).split(":")[0];
						param_name = result.getMetaData().getColumnName(i).split(":")[1];

						PmParameterDefinition definition = (PmParameterDefinition) Enum.valueOf((Class<Enum>) Class
								.forName(param_class), param_name);
						if (definition.getType() == Class.class) {
							// handle Class.class parameter types:
							if (result.getObject(i).toString().length() > 0) {
								setParameter(definition, Class.forName((String) result.getObject(i)));
							}
						} else {
							// handle all other parameter types:
							setParameter(definition, result.getObject(i));
						}

						logger.info("Parameter " + Enum.valueOf((Class<Enum>) Class.forName(param_class), param_name)
								+ " read from" + " database. Value: " + result.getObject(i));
					} else {
						if (!result.getMetaData().getColumnName(i).equals("id")) {
							logger.warn("The column " + result.getMetaData().getColumnName(i) + " of table " +
									t1 + " is not in proper parameter format (CLASS:PARAMETER_NAME)");
						}
					}

				} catch (IllegalArgumentException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}
			result.close();
			disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
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
	 * Tries to establish a JDBC Connection to the MySQL database given the settings provided to the constructor.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			SQLException {

		Properties properties = new Properties();
		properties.put("user", getParameter(dbUser));
		properties.put("password", getParameter(dbPassword));
		String connectTo = "jdbc:mysql://" + getParameter(dbLocation) 
			+ (((String) getParameter(dbLocation)).endsWith("/") ? "" : "/")+ getParameter(dbName);

		// error handling:
		if (getParameter(dbLocation) == null) {
			throw new IllegalArgumentException("Invalid database settings: Invalid database location!");
		}
		if (getParameter(dbName) == null) {
			throw new IllegalArgumentException("Invalid database settings: Invalid database name!");
		}
		if (getParameter(dbUser) == null) {
			throw new IllegalArgumentException("Invalid database settings: Invalid user name!");
		}
		if (getParameter(dbPassword) == null) {
			throw new IllegalArgumentException("Invalid database settings: Invalid password!");
		}

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		logger.info("Connect to DB: " + connectTo);
		return DriverManager.getConnection(connectTo, properties);
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 29.03.2010
	 */
	protected void disconnect() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the dbTable
	 */
	public PmParameterDefinition getDbTable() {
		return dbTable;
	}

	/**
	 * @param dbTable the dbTable to set
	 */
	public void setDbTable(PmParameterDefinition dbTable) {
		this.dbTable = dbTable;
	}

	/**
	 * @return the dbLocation
	 */
	public PmParameterDefinition getDbLocation() {
		return dbLocation;
	}

	/**
	 * @param dbLocation the dbLocation to set
	 */
	public void setDbLocation(PmParameterDefinition dbLocation) {
		this.dbLocation = dbLocation;
	}

	/**
	 * @return the dbName
	 */
	public PmParameterDefinition getDbName() {
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(PmParameterDefinition dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the dbUser
	 */
	public PmParameterDefinition getDbUser() {
		return dbUser;
	}

	/**
	 * @param dbUser the dbUser to set
	 */
	public void setDbUser(PmParameterDefinition dbUser) {
		this.dbUser = dbUser;
	}

	/**
	 * @return the dbPassword
	 */
	public PmParameterDefinition getDbPassword() {
		return dbPassword;
	}

	/**
	 * @param dbPassword the dbPassword to set
	 */
	public void setDbPassword(PmParameterDefinition dbPassword) {
		this.dbPassword = dbPassword;
	}
}
