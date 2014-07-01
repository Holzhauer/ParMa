/**
 * 
 */
package de.cesr.parma.tests;

import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;

/**
 * @author sholzhau
 *
 */
public enum PmComplexPa implements PmParameterDefinition {
	/**
	 * Number of households a single agents represents in simulation
	 */
	NUM_AGENTS(Integer.class, 10),
	
	SPECIAL_NUM_AGENTS(Integer.class, PmComplexPa.NUM_AGENTS),
	
	NEW_NUM_AGENTS(Integer.class, 12);

	private Class < ? >	type;
	private Object		defaultValue;

	PmComplexPa(Class<?> type) {
		this(type, null);
	}

	PmComplexPa(Class<?> type, Object defaultValue) {
		this.type = type;
		this.defaultValue = defaultValue;
	}

	PmComplexPa(Class<?> type, PmParameterDefinition defaultDefinition) {
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
