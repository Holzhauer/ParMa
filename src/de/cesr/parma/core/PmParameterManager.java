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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * 
 * Defines an interface for classes that provide parameter values. Also defines all parameters used throughout the model. See
 * ParametrFramework_Documentation.doc for further information!
 * 
 * @author Holzhauer
 * @date 08.01.2009
 * 
 */
public class PmParameterManager extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger							logger		= Logger.getLogger(PmParameterManager.class);

	static Map < PmParameterDefinition , Object >	params		= new HashMap < PmParameterDefinition , Object >();
	static ArrayList < PmParameterDefinition >		definitions	= new ArrayList < PmParameterDefinition >();
	static PmParameterManager						paraManager;

	/**
	 * Registers {@link PmParameterDefinition}s to this manager. Usually, parameters are defined by an enumerations that extends
	 * {@link PmParameterDefinition}
	 * 
	 * Created by Holzhauer on 08.01.2009
	 * 
	 * @param definitions
	 */
	public static void registerParametersDefinitions(Collection < ? extends PmParameterDefinition > definitions) {
		for (PmParameterDefinition definition : definitions) {
			PmParameterManager.definitions.add(definition);

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Add defintion " + definition.toString() + " (" + definition.getDefaultValue() + ")");
			}
			// LOGGING ->

		}
	}

	/**
	 * Registers {@link PmParameterDefinition}s to this manager. Usually, parameters are defined by an enumerations that extends
	 * {@link PmParameterDefinition}
	 * 
	 * @param definitions Created by Sascha Holzhauer on 30.07.2010
	 */
	public static void registerParametersDefinitions(PmParameterDefinition[] definitions) {
		for (PmParameterDefinition definition : definitions) {
			PmParameterManager.definitions.add(definition);

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Add defintion " + definition.toString() + " (" + definition.getDefaultValue() + ")");
			}
			// LOGGING ->
		}
	}

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

		if (!definition.getType().isInstance(value)) {
			logger.warn("The given value is not assignable to the type specified in the parameter definition!");
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
