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

import java.io.NotActiveException;

import de.cesr.parma.core.PmAbstractParameterReader;


/**
 * PARameter MAnager
 * 
 * Fetches Parameters by GUI
 * 
 * @author Holzhauer
 * @date 08.01.2009
 * 
 */
public class PmGuiParameterReader extends PmAbstractParameterReader {


	/**
	 * @see de.cesr.parma.core.PmAbstractParameterReader#initParameters()
	 */
	public void initParameters() {
		try {
			throw new NotActiveException("Not yet implemented");
		} catch (NotActiveException e) {
			e.printStackTrace();
		}
		// TODO
	}
}
