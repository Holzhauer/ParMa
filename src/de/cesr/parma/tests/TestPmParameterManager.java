/**
 * 
 */
package de.cesr.parma.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.reader.PmXmlParameterReader;

/**
 * @author holzhauer
 *
 */
public class TestPmParameterManager {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		PmParameterManager.reset();
		PmXmlParameterReader xmlReader = new PmXmlParameterReader(new PmParameterDefinition() {
			@Override
			public Class<?> getType() {
				return String.class;
			}
			
			@Override
			public Object getDefaultValue() {
				return "./src/de/cesr/parma/tests/res/TestParameter.xml";
			}
		});
		PmParameterManager.registerReader(xmlReader);
	}

	/**
	 * Test method for {@link de.cesr.parma.core.PmParameterManager#reset()}.
	 */
	@Test
	public void testReset() {
		assertEquals("", 10, PmParameterManager.getParameter(BasicPa.NUM_AGENTS));
		PmParameterManager.init();
		assertEquals("", 100, ((Integer) PmParameterManager.getParameter(BasicPa.NUM_AGENTS)).intValue());
		PmParameterManager.reset();
		assertEquals("", 10, PmParameterManager.getParameter(BasicPa.NUM_AGENTS));
	}

}
