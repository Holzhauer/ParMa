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
package de.cesr.parma.definition;

import de.cesr.parma.core.PmParameterDefinition;


/**
 * PARameter MAnager
 * 
 * @author Sascha Holzhauer
 * @date 29.06.2010
 * 
 */
public enum PmFrameworkPa implements PmParameterDefinition {

	/**
	 * MySQL server
	 */
	LOCATION(String.class, "localhost"),
	
	/**
	 * 
	 */
	DBNAME(String.class, "database"),
	
	/**
	 * MySQl user name
	 */
	USER(String.class, "user"),
	
	/**
	 * Password
	 */
	PASSWORD(String.class, "password"),

	/**
	 * the parameter definition that specifies the parameter set id
	 * for which parameter definitions shall be fetched from DB
	 */
	PARAM_SET_ID(Integer.class, 1),
	
	/**
	 * Tablename of parameter table
	 */
	TBLNAME_PARAMS(String.class, "parameterTable"),
	
	/**
	 * Location of XML file that specifies parameters
	 */
	XML_PARAMETER_FILE(String.class, "./Parameter.xml"),
	
	/**
	 *  Location of XML file that specifies database settings:
	 */
	DB_SETTINGS_FILE(String.class, "./DbSettings.xml");
	

	private Class < ? >	type;
	private Object		defaultValue;

	PmFrameworkPa(Class<?> type) {
		this(type, null);
	}

	PmFrameworkPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public Class < ? > getType() {
		return type;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
}
