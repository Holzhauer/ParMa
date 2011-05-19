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



/**
 * 
 * @author Sascha Holzhauer
 * @date 28.06.2010
 * 
 */
public interface PmParameterDefinition {

	/**
	 * Return the type of this parameter
	 * 
	 * @return the type of this parameter Created by Sascha Holzhauer on 28.06.2010
	 */
	public Class < ? > getType();

	/**
	 * Returns the default value that is assigned to this parameter at definition
	 * 
	 * @return the parameter's default value Created by Sascha Holzhauer on 28.06.2010
	 */
	public Object getDefaultValue();

}
