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
 * Defines an interface for classes that provide parameter values. Also defines
 * all parameters used throughout the model.
 * 
 * {@link PmParameterManager}s can be associated with an {@link Object} and referenced by that
 * from this class ({@link this#getInstance(Object)}. However, this is not mandatory and basic 
 * operations (e.g. parameter reading) work with {@link PmParameterManager}s directly.
 * 
 * TODO incorporate to site doc
 * See ParametrFramework_Documentation.doc for further information!
 * 
 * @author Holzhauer
 * @date 08.01.2009
 * 
 */
public class PmParameterManager extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(PmParameterManager.class);

	static PmParameterManager paraManager = null;
	static Map<Object, PmParameterManager> paraManagers = new HashMap<Object, PmParameterManager>();

	static protected Map<PmParameterDefinition, PmParameterDefinition> staticDefaultParams = 
			new HashMap<PmParameterDefinition, PmParameterDefinition>();
	 
	protected Map<PmParameterDefinition, Object> params;
	protected Object identifier;
	protected PmParameterManager defaultPm;
	protected Map<PmParameterDefinition, PmParameterDefinition> defaultParams;

	/**
	 * Returns the full parameter definition name containing the name of the
	 * declaring class.
	 * 
	 * @param definition
	 * @return
	 */
	public static String getFullName(PmParameterDefinition definition) {
		return definition.getDeclaringClass().getName() + "."
				+ definition.toString();
	}

	/**
	 * Checks whether the main instance exists and instantiates if not.
	 */
	protected static boolean checkMainInstance() {
		if (paraManager == null) {
			paraManager = new PmParameterManager("values");
		}
		return true;
	}

	/**
	 * @param param
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static PmParameterDefinition parse(String param) {
		String param_class, param_name;
		try {
			param_class = param.split(":")[0];
			param_name = param.split(":")[1];
		} catch (ArrayIndexOutOfBoundsException exception) {
			// <- LOGGING
			logger.error("You probabliy have an error in your parameter XML/CSV file (near "
					+ param
					+ "):"
					+ "Please, stick to the form PACKAGE.CLASS:PARAMETER");
			// LOGGING ->
			throw new RuntimeException(
					"You probabliy have an error in your parameter XML/CSV file (near "
							+ param
							+ "):"
							+ "Please, stick to the form PACKAGE.CLASS:PARAMETER");
		}

		PmParameterDefinition definition = null;

		try {
			definition = (PmParameterDefinition) Enum.valueOf(
					(Class<Enum>) Class.forName(param_class), param_name);
		} catch (ClassNotFoundException exception) {
			logger.error("Error while parsing parameter " + param);
			exception.printStackTrace();
		}
		return definition;
	}

	/**
	 * Set every field to null
	 */
	public static void reset() {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Reset Parma Parameter Managers");
		}
		// LOGGING ->

		paraManager = null;
		paraManagers = new HashMap<Object, PmParameterManager>();
	}

	/**
	 * The identifier's toString() is used to mark parameter value loggings for
	 * this parameter manager.
	 * 
	 * @param identifier
	 */
	private PmParameterManager(Object identifier) {
		this.identifier = identifier;
		initInstance();
	}

	/* ********************************************************
	 * Static methods for MAIN instance (compatibility reasons)
	 */

	/**
	 * Let registered {@link PmParameterReader}s read parameters.
	 */
	public static void init() {
		checkMainInstance();
		paraManager.initParameters();
	}

	/**
	 * Provides the requested parameter value from the main parameter manager.
	 * 
	 * @param parameter
	 * @return value
	 */
	public static Object getParameter(PmParameterDefinition definition) {
		checkMainInstance();
		return paraManager.getParam(definition);
	}

	/**
	 * Checks whether the given definition has been set at the main parameter
	 * manager.
	 * 
	 * @param definition
	 *            the definition to check
	 * @return true if the parameter has been set
	 */
	public static boolean isCustomised(PmParameterDefinition definition) {
		checkMainInstance();
		return paraManager.isParamCustomised(definition);
	}

	/**
	 * Sets the given value for the given parameter definition at the main
	 * parameter manager.
	 * 
	 * @param definition
	 * @param value
	 */
	public static void setParameter(PmParameterDefinition definition,
			Object value) {
		checkMainInstance();
		paraManager.setParam(definition, value);
	}

	/**
	 * Registers another parameter definition that is used if there is no value
	 * set for the given parameter at the main parameter manager.
	 * NOTE: This should only be used by {@link PmParameterDefinition} constructors
	 * (The reason is that this method sets a static default parameter information that is restored
	 * at calls to {@link this#resetInstance()}).
	 * 
	 * Use {@link this#getInstance(null)#setDefaultParamDef(PmParameterDefinition, PmParameterDefinition)} to store custom
	 * default parameter definitions at the main instance.
	 * 
	 * @param definition
	 * @param defaultDefinition
	 */
	public static void setDefaultParameterDef(PmParameterDefinition definition,
			PmParameterDefinition defaultDefinition) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set default param definition for " + definition + ": " + defaultDefinition);
		}
		// LOGGING ->

		staticDefaultParams.put(definition, defaultDefinition);
		checkMainInstance();
		paraManager.setDefaultParamDef(definition, defaultDefinition);
	}

	/**
	 * Copies the current parameter value of source to the parameter definition
	 * target at the main parameter manager.
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyParameterValue(PmParameterDefinition source,
			PmParameterDefinition target) {
		checkMainInstance();
		paraManager.copyParamValue(source, target);
	}

	/**
	 * Register a parameter reader at the main parameter manager.
	 * 
	 * @param reader
	 *            the reader to register
	 */
	public static void registerReader(PmParameterReader reader) {
		checkMainInstance();
		paraManager.registerParameterReader(reader);
	}

	/**
	 * De-register a parameter reader from the main parameter manager.
	 * 
	 * @param reader
	 *            the reader to register
	 */
	public static void deregisterReader(PmParameterReader reader) {
		checkMainInstance();
		paraManager.deregisterParameterReader(reader);
	}

	/**
	 * Logs the current parameter values for parameters that were read into the
	 * main parameter manager by {@link PmParameterReader}s before. The logger
	 * name is <code><de.cesr.parma.core.PmParameterManager.values</code>. To
	 * log all parameter values including the default values for those that have
	 * not been read use {@link #logParameterValues(PmParameterDefinition[]...)}
	 * .
	 */
	public static void logParameterValues() {
		checkMainInstance();
		paraManager.logParamValues();
	}

	/**
	 * Logs the current parameter values for the parameters defined in the given
	 * arrays of type {@link PmParameterDefinition}s. These can be obtained by
	 * <code>(PmParameterDefinition[])PmFrameworkPa.values()</code>. The logger
	 * name is <code><de.cesr.parma.core.PmParameterManager.values</code>.
	 * 
	 * @param params
	 */
	public static void logParameterValues(PmParameterDefinition[]... params) {
		checkMainInstance();
		paraManager.logParamValues(params);
	}

	/* ********************************************************
	 * ******************************** Instance methods
	 * ***************************************************
	 */

	/**
	 * Initialises maps.
	 */
	protected void initInstance() {
		defaultParams = new HashMap<PmParameterDefinition, PmParameterDefinition>();
		for (Map.Entry<PmParameterDefinition, PmParameterDefinition> entry : staticDefaultParams.entrySet()) {
			defaultParams.put(entry.getKey(), entry.getValue());
		}
		params = new HashMap<PmParameterDefinition, Object>();
	}

	/**
	 * Get any registered parameter. If no value is registered for the given
	 * parameter at this PM it requests the default parameter of this PM and
	 * return it if defined. Otherwise, the request is delegated to the default
	 * PM.
	 * 
	 * @param parameter
	 *            the {@link PmParameterDefinition} whose value is requested
	 * @return the parameter's current value
	 */
	public Object getParam(PmParameterDefinition parameter) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Requested parameter: " + getFullName(parameter));
		}
		// LOGGING ->

		if (params.containsKey(parameter)) {
			return params.get(parameter);
		} else if (defaultParams.containsKey(parameter)) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Request default parameter for "
						+ getFullName(parameter) + " ("
						+ defaultParams.get(parameter) + ")");
			}
			// LOGGING ->

			return getParam(defaultParams.get(parameter));
		
		} else if (staticDefaultParams.containsKey(parameter)) {
			return this.getParam(staticDefaultParams.get(parameter));
		
		} else if (defaultPm != null) {
			return defaultPm.getParam(parameter);
			
		} else {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Request default value for "
						+ getFullName(parameter) + " ("
						+ parameter.getDefaultValue() + ")");
			}
			// LOGGING ->
			
			return parameter.getDefaultValue();
		}
	}

	/**
	 * Checks whether the given definition has been set.
	 * 
	 * @param definition
	 *            the definition to check
	 * @return true if the parameter has been set
	 */
	public boolean isParamCustomised(PmParameterDefinition definition) {
		return params.containsKey(definition);
	}

	/**
	 * @param definition
	 * @param value
	 */
	public void setParam(PmParameterDefinition definition, Object value) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set parameter " + getFullName(definition) + " ("
					+ value + ")");
		}
		// LOGGING ->

		Object result = value;

		// TODO extend conversion
		if (definition.getType() == Integer.class && value instanceof String) {
			result = Integer.parseInt((String) value);
		} else if (definition.getType() == Double.class
				&& value instanceof String) {
			result = Double.parseDouble((String) value);
		} else if (definition.getType() == Float.class
				&& value instanceof String) {
			result = Float.parseFloat((String) value);
		} else if (definition.getType() == Long.class
				&& value instanceof String) {
			result = Long.parseLong((String) value);
		} else if (definition.getType() == Short.class
				&& value instanceof String) {
			result = Short.parseShort((String) value);
		} else if (definition.getType() == Boolean.class
				&& value instanceof String) {
			result = Boolean.parseBoolean((String) value);
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value after conversion: " + result);
		}

		if (result != null
				&& !definition.getType().isAssignableFrom(result.getClass())) {

			logger.warn(getFullName(definition) + ": The given value ("
					+ result + ") of type " + result.getClass()
					+ " is not assignable to the "
					+ "type specified in the parameter definition ("
					+ definition.getType() + ")!");
		}
		params.put(definition, result);
	}

	/**
	 * Registers another parameter definition that is used if there is no value
	 * set for the given parameter.
	 * 
	 * @param definition
	 * @param defaultDefinition
	 */
	public void setDefaultParamDef(PmParameterDefinition definition,
			PmParameterDefinition defaultDefinition) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set parameter default definition for "
					+ getFullName(definition) + " ("
					+ getFullName(defaultDefinition) + ")");
		}
		// LOGGING ->
		defaultParams.put(definition, defaultDefinition);
	}

	/**
	 * Sets the default {@link PmParameterManager} that is consulted in case
	 * this {@link PmParameterManager} has no value assigned for a certain
	 * parameter (and no default parameter defined).
	 * 
	 * @param defaultPm
	 */
	public void setDefaultPm(PmParameterManager defaultPm) {
		this.defaultPm = defaultPm;
	}

	/**
	 * Copies the current parameter value of source to the parameter definition
	 * target.
	 * 
	 * @param source
	 * @param target
	 */
	public void copyParamValue(PmParameterDefinition source,
			PmParameterDefinition target) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Copy parameter value from " + getFullName(source)
					+ " to " + getFullName(target));
		}
		// LOGGING ->
		setParameter(target, getParameter(source));
	}

	/**
	 * Logs the current parameter values for parameters that were read into this
	 * parameter manager by {@link PmParameterReader}s before. The logger name
	 * is <code><de.cesr.parma.core.PmParameterManager.values</code>. To log all
	 * parameter values including the default values for those that have not
	 * been read use {@link #logParameterValues(PmParameterDefinition[]...)}.
	 */
	public void logParamValues() {
		Logger valueLogger = Logger.getLogger(PmParameterManager.class
				.getName() + "." + this);
		StringBuffer buffer = new StringBuffer();
		buffer.append("Current parameter values: "
				+ System.getProperty("line.separator"));
		for (Entry<PmParameterDefinition, Object> entry : params.entrySet()) {
			buffer.append("\t" + entry.getKey().getDeclaringClass().getName()
					+ "." + entry.getKey()
					+ System.getProperty("line.separator") + "\t\t"
					+ entry.getValue() + System.getProperty("line.separator"));
		}
		valueLogger.info(buffer.toString());
	}

	/**
	 * Logs the current parameter values for the parameters defined in the given
	 * arrays of type {@link PmParameterDefinition}s in this parameter manager.
	 * These can be obtained by
	 * <code>(PmParameterDefinition[])PmFrameworkPa.values()</code>. The logger
	 * name is <code><de.cesr.parma.core.PmParameterManager.values</code>.
	 * 
	 * @param params
	 */
	public void logParamValues(PmParameterDefinition[]... params) {
		Collection<PmParameterDefinition> paramDefs = new ArrayList<PmParameterDefinition>();
		for (PmParameterDefinition[] item : params) {
			paramDefs.addAll(Arrays.asList(item));
		}
		Logger valueLogger = Logger.getLogger(PmParameterManager.class
				.getName() + "." + this);
		StringBuffer buffer = new StringBuffer();
		buffer.append("Current parameter values: "
				+ System.getProperty("line.separator"));

		for (PmParameterDefinition parameterDef : paramDefs) {
			buffer.append("\t" + parameterDef.getDeclaringClass().getName()
					+ "." + parameterDef.toString()
					+ System.getProperty("line.separator") + "\t -> "
					+ getParam(parameterDef)
					+ System.getProperty("line.separator"));
		}
		valueLogger.info(buffer.toString());
	}

	/**
	 * Set every field to null
	 */
	public void resetInstance() {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Reset Parma Parameter Manager " + this);
		}
		// LOGGING ->
		this.initInstance();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return identifier.toString();
	}

	/* *********************************************************************
	 * INSTANCE MANAGEMENT
	 * ********************************************************************
	 */

	/**
	 * Return new instance of parameter manager that is NOT registered.
	 * 
	 * @return parameter manager
	 */
	public static PmParameterManager getNewInstance() {
		return new PmParameterManager("Unnamed");
	}

	/**
	 * Creates a new parameter manager and stores it with key identifier.
	 * 
	 * @param identifier
	 * @return
	 */
	public static PmParameterManager getNewInstance(Object identifier) {
		PmParameterManager pm = new PmParameterManager(identifier);
		if (paraManagers.containsKey(identifier)) {
			logger.warn("There is still an PmParameterManager registered by identifier " + identifier + 
					" which is now replaced. Consider using resetInstance() instead!");
		}
		paraManagers.put(identifier, pm);
		return pm;
	}

	/**
	 * If identifier is null, it returns the main static instance of
	 * {@link PmParameterManager}.
	 * 
	 * @param identifier
	 * @return instance of {@link PmParameterManager}
	 */
	public static PmParameterManager getInstance(Object identifier) {
		if (identifier == null) {
			checkMainInstance();
			return paraManager;
		} else {
			if (!paraManagers.containsKey(identifier)) {
				logger.warn("No PmParameterManager registered for identifier "
						+ identifier
						+ "! Maybe you want to call getNewInstance(identifier).");
				return null;
			} else {
				return paraManagers.get(identifier);
			}
		}
	}

	/* *********************************************************************
	 * STATIC ACCESS METHODS
	 * ********************************************************************
	 */

	/**
	 * @param identifier
	 * @param definition
	 * @param value
	 */
	public static void setParameter(Object identifier,
			PmParameterDefinition definition, Object value) {
		getInstance(identifier).setParam(definition, value);
	}

	/**
	 * Provides the current parameter value for the given
	 * {@link PmParameterDefinition} in the parameter manager identified by
	 * identifier.
	 * 
	 * @param identifier
	 * @param definition
	 * @return
	 */
	public static Object getParameter(Object identifier,
			PmParameterDefinition definition) {
		return getInstance(identifier).getParam(definition);
	}

	/**
	 * Checks whether the given definition has been set.
	 * 
	 * @param definition
	 *            the definition to check
	 * @return true if the parameter has been set
	 */
	public static boolean isParamCustomised(Object identifier,
			PmParameterDefinition definition) {
		return getInstance(identifier).isParamCustomised(definition);
	}

	/**
	 * Copies the current parameter value of source to the parameter definition
	 * target.
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyParamValue(Object identifier,
			PmParameterDefinition source, PmParameterDefinition target) {
		getInstance(identifier).copyParamValue(source, target);
	}

	/**
	 * @param identifier
	 */
	public static void resetInstance(Object identifier) {
		getInstance(identifier).resetInstance();
	}

	/**
	 * @param identifier
	 */
	public static void initParams(Object identifier) {
		getInstance(identifier).initParameters();
	}

	/**
	 * @param identifier
	 * @param reader
	 */
	public static void registerParamReader(Object identifier,
			PmParameterReader reader) {
		getInstance(identifier).registerParameterReader(reader);
	}

	/**
	 * @param identifier
	 * @param reader
	 */
	public static void deregisterParamReader(Object identifier,
			PmParameterReader reader) {
		getInstance(identifier).deregisterParameterReader(reader);
	}
}