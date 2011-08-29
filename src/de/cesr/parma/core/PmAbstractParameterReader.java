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

import org.apache.log4j.Logger;



/**
 * PARameter MAnager
 * 
 * @author Sascha Holzhauer
 * @date 29.06.2010
 * 
 */
public abstract class PmAbstractParameterReader implements PmParameterReader {

	/**
	 * Logger
	 */
	static private Logger			logger	= Logger.getLogger(PmAbstractParameterReader.class);

	Collection < PmParameterReader >	readers	= new ArrayList < PmParameterReader >();

	/**
	 * @see de.cesr.parma.core.PmParameterReader#initParameters()
	 */
	@Override
	public void initParameters() {

		for (PmParameterReader reader : readers) {
			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Initialisation of parameter reader " + reader + " started");
			}
			// LOGGING ->
			
			reader.initParameters();

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Initialisation of parameter reader " + reader + " finished");
			}
			// LOGGING ->

		}
	}

	/**
	 * @see de.cesr.parma.core.PmParameterReader#registerParameterReader(de.cesr.parma.core.PmParameterReader)
	 */
	@Override
	public void registerParameterReader(PmParameterReader reader) {
		readers.add(reader);
	}
	
	/**
	 * Cancels registration of the given reader, i.e. it is no longer used to initialise parameters.
	 * @param reader reader to deregisters
	 */
	public void deregisterParameterReader(PmParameterReader reader) {
		readers.remove(reader);
	}

	/**
	 * Cancels registration of all registered readers.
	 */
	public void derigsterAll() {
		readers.clear();
	}
}
