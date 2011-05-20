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
import java.util.HashMap;
import java.util.Map;

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

	static Map<PmParameterDefinition, Object>	params		= new HashMap<PmParameterDefinition, Object>();
	static ArrayList<PmParameterDefinition>		definitions	= new ArrayList<PmParameterDefinition>();
	static PmParameterManager					paraManager;

	/**
	 * Get any registered parameter
	 * 
	 * @param parameter the {@link PmParameterDefinition} whose value is requested
	 * @return the parameter's current value
	 * 
	 *         Created by Holzhauer on 08.01.2009
	 */
	public static Object getParameter(PmParameterDefinition parameter) {
		if (params.containsKey(parameter)) {
			return params.get(parameter);
		} else {
			return parameter.getDefaultValue();
		}
	}

	/**
	 * @param definition
	 * @param value Created by Sascha Holzhauer on 29.06.2010
	 */
	public static void setParameter(PmParameterDefinition definition, Object value) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Set parameter " + definition + "(" + value + ")");
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
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Value after conversion: " + value);
		}

		if (!definition.getType().isInstance(value)) {

			logger.warn("The given value (" + value + ") of type " + value.getClass() + " is not assignable to the "
					+ "type specified in the parameter definition (" + definition.getType() + ")!");
		}
		params.put(definition, value);
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 15.09.2010
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
	 * @param reader the reader to register Created by Sascha Holzhauer on 20.05.2011
	 */
	public static void registerReader(PmParameterReader reader) {
		if (paraManager == null) {
			paraManager = new PmParameterManager();
		}
		paraManager.registerParameterReader(reader);
	}

	/**
	 * Set every field to null Created by Sascha Holzhauer on 30.06.2010
	 */
	public static void reset() {
		paraManager = null;
		params = null;
		definitions = null;
	}
}
