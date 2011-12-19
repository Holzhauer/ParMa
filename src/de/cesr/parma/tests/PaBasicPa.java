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
package de.cesr.parma.tests;

import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;



/**
 * 
 * @author Sascha Holzhauer
 * @date 29.06.2010
 * 
 */
public enum PaBasicPa implements PmParameterDefinition {

	/**
	 * Number of households a single agents represents in simulation
	 */
	NUM_AGENTS(Integer.class, 10),
	
	SPECIAL_NUM_AGENTS(Integer.class, PaBasicPa.NUM_AGENTS),
	
	COPY_NUM_AGENTS(Integer.class, 0),
	
	/**
	 * 
	 */
	MUE(Double.class, 0.5);

	private Class < ? >	type;
	private Object		defaultValue;

	PaBasicPa(Class<?> type) {
		this(type, null);
	}

	PaBasicPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	PaBasicPa(Class<?> type, PmParameterDefinition defaultDefinition) {
		this.type = type;
		this.defaultValue = defaultDefinition.getDefaultValue();
		PmParameterManager.setDefaultParameterDef(this, defaultDefinition);
	}
	
	public Class < ? > getType() {
		return type;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
}
