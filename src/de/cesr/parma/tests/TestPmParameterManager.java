/**
 * 
 */
package de.cesr.parma.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;
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
	}
	
	/**
	 * 
	 */
	@Test
	public void defaultParameterValueTest() {
		PmParameterManager.init();
		
		assertEquals(10, PmParameterManager.getParameter(PaBasicPa.NUM_AGENTS));
		assertEquals(10, PmParameterManager.getParameter(PaBasicPa.SPECIAL_NUM_AGENTS));
		
		PmParameterManager.setParameter(PaBasicPa.NUM_AGENTS, 20);
		
		assertEquals(20, PmParameterManager.getParameter(PaBasicPa.NUM_AGENTS));
		assertEquals(20, PmParameterManager.getParameter(PaBasicPa.SPECIAL_NUM_AGENTS));		
	}
	
	
	/**
	 * 
	 */
	@Test
	public void copyParameterValueTest() {
		PmParameterManager.init();
		
		assertEquals( 0, PmParameterManager.getParameter(PaBasicPa.COPY_NUM_AGENTS));
		assertEquals(10, PmParameterManager.getParameter(PaBasicPa.NUM_AGENTS));
		
		PmParameterManager.copyParameterValue(PaBasicPa.NUM_AGENTS, PaBasicPa.COPY_NUM_AGENTS);
		
		assertEquals(10, PmParameterManager.getParameter(PaBasicPa.COPY_NUM_AGENTS));
		assertEquals(10, PmParameterManager.getParameter(PaBasicPa.NUM_AGENTS));		
	}
	

	@Test
	public void testLogValues() {
		PmParameterManager.logParameterValues((PmParameterDefinition[])PmFrameworkPa.values());
	}
	
	/**
	 * Test method for {@link de.cesr.parma.core.PmParameterManager#reset()}.
	 */
	@Test
	public void testReset() {
		PmXmlParameterReader xmlReader = new PmXmlParameterReader(new PmParameterDefinition() {
			public Class<?> getType() {
				return String.class;
			}
			
			public Object getDefaultValue() {
				return "./src/de/cesr/parma/tests/res/TestParameter.xml";
			}

			public Class<?> getDeclaringClass() {
				return this.getClass();
			}
		});
		PmParameterManager.registerReader(xmlReader);
		assertEquals("", 10, PmParameterManager.getParameter(PaBasicPa.NUM_AGENTS));
		PmParameterManager.init();
		assertEquals("", 100, ((Integer) PmParameterManager.getParameter(PaBasicPa.NUM_AGENTS)).intValue());
		PmParameterManager.reset();
		assertEquals("", 10, PmParameterManager.getParameter(PaBasicPa.NUM_AGENTS));
	}

}
