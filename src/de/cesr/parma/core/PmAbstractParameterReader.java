/**
 * KUBUS_Proto01
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 29.06.2010
 */
package de.cesr.parma.core;



import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;



/**
 * KUBUS_Proto01
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
	 * @see PmParameterReader.framework.ParameterReader#initParameters()
	 */
	@Override
	public void initParameters() {

		for (PmParameterReader reader : readers) {
			reader.initParameters();

			// <- LOGGING
			if (logger.isDebugEnabled()) {
				logger.debug("Init parameter reader " + reader);
			}
			// LOGGING ->

		}
	}

	/**
	 * @see PmParameterReader.framework.ParameterReader#registerParameterReader(PmParameterReader.framework.ParameterReader)
	 */
	@Override
	public void registerParameterReader(PmParameterReader reader) {
		readers.add(reader);
	}

}
