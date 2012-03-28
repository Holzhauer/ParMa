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
package de.cesr.parma.core;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;



/**
 * 
 * Defines an interface for classes that provide parameter values. Also defines all parameters used throughout the
 * model. See ParametrFramework_Documentation.doc for further information!
 * 
 * @author Holzhauer
 * @date 08.01.2009
 * 
 */
public class PmParameterManager extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger						logger		= Logger.getLogger(PmParameterManager.class);

	static Map<PmParameterDefinition, Object>	params;
	static Map<PmParameterDefinition, PmParameterDefinition>
												defaultParams;
	static PmParameterManager					paraManager;


	static {
		initCollections();
		defaultParams	= new HashMap<PmParameterDefinition,PmParameterDefinition>();
	}
	
	/**
	 * Initialise collections for params and defualtParams. 
	 */
	private static void initCollections() {
		params			= new HashMap<PmParameterDefinition, Object>();
	}
	
	/**
	 * Get any registered parameter.
	 * If no value is registered for the given parameter
	 * it request the default parameter and return it if defined.
	 * 
	 * @param parameter the {@link PmParameterDefinition} whose value is requested
	 * @return the parameter's current value
	 */
	public static Object getParameter(PmParameterDefinition parameter) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Requested parameter: " + getFullName(parameter));
		}
		// LOGGING ->
		if (params.containsKey(parameter)) {
			return params.get(parameter);
		} else {
			if (defaultParams.containsKey(parameter)) {
				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Request default parameter for " + getFullName(parameter) + 
							"(" + defaultParams.get(parameter) + ")");
				}
				// LOGGING ->
				return getParameter(defaultParams.get(parameter));
			} else {
				return parameter.getDefaultValue();
			}
		}
	}

	/**
	 * Checks whether the given definition has been set by
	 * {@link PmParameterManager#setParameter(PmParameterDefinition, Object)}.
	 * 
	 * @param definiiton
	 *            the definition to check
	 * @return true if the parameter has been set
	 */
	public static boolean isCustomised(PmParameterDefinition definiiton) {
		return params.containsKey(definiiton);
	}

	/**
	 * @param definition
	 * @param value
	 */
	public static void setParameter(PmParameterDefinition definition, Object value) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set parameter " + getFullName(definition) + " (" + value + ")");
		}
		// LOGGING ->

		// TODO extend conversion
		if (definition.getType() == Integer.class && value instanceof String) {
			value = Integer.parseInt((String) value);
		} else if (definition.getType() == Double.class && value instanceof String) {
			value = Double.parseDouble((String) value);
		} else if (definition.getType() == Float.class && value instanceof String) {
			value = Float.parseFloat((String) value);
		} else if (definition.getType() == Long.class && value instanceof String) {
			value = Long.parseLong((String) value);
		} else if (definition.getType() == Short.class && value instanceof String) {
			value = Short.parseShort((String) value);
		} else if (definition.getType() == Boolean.class && value instanceof String) {
			value = Boolean.parseBoolean((String) value);
		}
		
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value after conversion: " + value);
		}

		if (value != null && value != null
				&& !definition.getType().isAssignableFrom(value.getClass())) {

			logger.warn(getFullName(definition) + ": The given value (" + value + ") of type " + value.getClass() + " is not assignable to the "
					+ "type specified in the parameter definition (" + definition.getType() + ")!");
		}
		params.put(definition, value);
	}
	
	/**
	 * Registers another parameter definition that is
	 * used if there is no value set for the given parameter.
	 * 
	 * @param definition
	 * @param defaultDefinition
	 */
	public static void setDefaultParameterDef(PmParameterDefinition definition, PmParameterDefinition defaultDefinition) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set parameter default definition for " + getFullName(definition) + " (" + getFullName(defaultDefinition) + ")");
		}
		// LOGGING ->
		defaultParams.put(definition, defaultDefinition);
	}


	/**
	 * Copies the current parameter value of source to the parameter
	 * definition target.
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyParameterValue(PmParameterDefinition source, PmParameterDefinition target) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Copy parameter value from " + getFullName(source) + " to " + getFullName(target));
		}
		// LOGGING ->
		setParameter(target, getParameter(source));
	}
	
	/**
	 * Let registered {@link PmParameterReader}s read parameters.
	 */
	public static void init() {
		if (paraManager == null) {
			paraManager = new PmParameterManager();
		}
		paraManager.initParameters();
	}

	/**
	 * Register a parameter reader.
	 * 
	 * @param reader the reader to register
	 */
	public static void registerReader(PmParameterReader reader) {
		if (paraManager == null) {
			paraManager = new PmParameterManager();
		}
		paraManager.registerParameterReader(reader);
	}

	
	/**
	 * Logs the current parameter values for parameters that were read into this
	 * parameter manager by {@link PmParameterReader}s before. The logger name is
	 * <code><de.cesr.parma.core.PmParameterManager.values</code>.
	 * To log all parameter values including the default values for those 
	 * that have not been read use {@link #logParameterValues(PmParameterDefinition[]...)}. 
	 */
	public static void logParameterValues() {
		Logger valueLogger = Logger.getLogger(PmParameterManager.class.getName() + ".values");
		StringBuffer buffer = new StringBuffer();
		buffer.append("Current parameter values: " + System.getProperty("line.separator"));
		for (Entry<PmParameterDefinition, Object> entry : params.entrySet()) {
			buffer.append("\t" + entry.getKey().getDeclaringClass().getName()+ "." + entry.getKey() + 
					System.getProperty("line.separator") + "\t\t" + entry.getValue());
		}
		valueLogger.info(buffer.toString());
	}
	
	/**
	 * Logs the current parameter values for the parameters defined
	 * in the given arrays of type {@link PmParameterDefinition}s. These
	 * can be obtained by <code>(PmParameterDefinition[])PmFrameworkPa.values()</code>.
	 * The logger name is <code><de.cesr.parma.core.PmParameterManager.values</code>.
	 * @param params
	 */
	public static void logParameterValues(PmParameterDefinition[] ... params) {
		Collection<PmParameterDefinition> paramDefs = new ArrayList<PmParameterDefinition>();
		for (PmParameterDefinition[] item : params) {
			paramDefs.addAll(Arrays.asList(item));
		}
		Logger valueLogger = Logger.getLogger(PmParameterManager.class.getName() + ".values");
		StringBuffer buffer = new StringBuffer();
		buffer.append("Current parameter values: " + System.getProperty("line.separator"));
		
		for (PmParameterDefinition parameterDef : paramDefs) {
			buffer.append("\t" + parameterDef.getDeclaringClass().getName() + "." + parameterDef.toString() + 
					System.getProperty("line.separator") + "\t -> " + 
					getParameter(parameterDef) + System.getProperty("line.separator"));
		}
		valueLogger.info(buffer.toString());
	}
	
	/**
	 * Set every field to null
	 */
	public static void reset() {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Reset Parma Parameter Manager...");
		}
		// LOGGING ->
		paraManager = null;
		params = null;
		initCollections();
	}
	
	/**
	 * Returns the full parameter definition name containing
	 * the name of the declaring class.
	 * @param definition
	 * @return
	 */
	public static String getFullName(PmParameterDefinition definition) {
		return definition.getDeclaringClass().getName() + "."+ definition.toString();
	}
}