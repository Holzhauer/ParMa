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
		
		assertEquals(10, PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
		assertEquals(10,
				PmParameterManager.getParameter(PmBasicPa.SPECIAL_NUM_AGENTS));
		
		PmParameterManager.setParameter(PmBasicPa.NUM_AGENTS, 20);
		
		assertEquals(20, PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
		assertEquals(20,
				PmParameterManager.getParameter(PmBasicPa.SPECIAL_NUM_AGENTS));
	}
	
	@Test
	public void staticDefaultParameterValueTest() {
		PmParameterManager.init();

		Object id = new Object();
		PmParameterManager pm = PmParameterManager.getNewInstance(id);
		
		PmParameterManager defaultPm = PmParameterManager.getNewInstance();
		pm.setDefaultPm(defaultPm);

		
		pm.setParam(PmComplexPa.NUM_AGENTS, 22);
				
		assertEquals(22, pm.getParam(PmComplexPa.NUM_AGENTS));
		assertEquals(22, pm.getParam(PmComplexPa.SPECIAL_NUM_AGENTS));
	
		assertEquals(10, defaultPm.getParam(PmComplexPa.SPECIAL_NUM_AGENTS));
		
		pm.setDefaultParamDef(PmComplexPa.SPECIAL_NUM_AGENTS, PmComplexPa.NEW_NUM_AGENTS);
		assertEquals(12, pm.getParam(PmComplexPa.SPECIAL_NUM_AGENTS));
	}
	
	/**
	 * 
	 */
	@Test
	public void copyParameterValueTest() {
		PmParameterManager.init();
		
		assertEquals(0,
				PmParameterManager.getParameter(PmBasicPa.COPY_NUM_AGENTS));
		assertEquals(10, PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
		
		PmParameterManager.copyParameterValue(PmBasicPa.NUM_AGENTS,
				PmBasicPa.COPY_NUM_AGENTS);
		
		assertEquals(10,
				PmParameterManager.getParameter(PmBasicPa.COPY_NUM_AGENTS));
		assertEquals(10, PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
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
		assertEquals("", 10,
				PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
		PmParameterManager.init();
		assertEquals("", 100,
				((Integer) PmParameterManager
						.getParameter(PmBasicPa.NUM_AGENTS)).intValue());
		PmParameterManager.reset();
		assertEquals("", 10,
				PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
	}

	@Test
	public void testInstanceCreation() {
		PmParameterManager.setParameter(PmBasicPa.NUM_AGENTS, new Integer(10));
		assertEquals(new Integer(10), PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
		
		PmParameterManager pm = PmParameterManager.getNewInstance();
		pm.setParam(PmBasicPa.NUM_AGENTS, new Integer(20));

		assertEquals(new Integer(20), pm.getParam(PmBasicPa.NUM_AGENTS));
		assertEquals(new Integer(10), PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
	}

	@Test
	public void testInstanceRetrieval() {
		Object id = new Object();
		PmParameterManager pm = PmParameterManager.getNewInstance(id);

		PmParameterManager.setParameter(id, PmBasicPa.NUM_AGENTS, new Integer(
				15));

		assertEquals(new Integer(15), PmParameterManager.getParameter(id, PmBasicPa.NUM_AGENTS));
		assertEquals(new Integer(10), PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));

		assertEquals(new Integer(15), pm.getParam(PmBasicPa.NUM_AGENTS));
		
		pm.setParam(PmBasicPa.NUM_AGENTS, new Integer(42));
		assertEquals(new Integer(42), pm.getParam(PmBasicPa.NUM_AGENTS));
		assertEquals(new Integer(42), PmParameterManager.getParameter(id, PmBasicPa.NUM_AGENTS));
		assertEquals(new Integer(10), PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
	}

	/**
	 * 
	 */
	@Test
	public void defaultParameterByInstanceValueTest() {
		PmParameterManager.init();
		
		Object id = new Object();
		PmParameterManager pm = PmParameterManager.getNewInstance(id);
		
		assertEquals(10, PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
		assertEquals(10,
				PmParameterManager.getParameter(PmBasicPa.SPECIAL_NUM_AGENTS));
		
		PmParameterManager.setParameter(PmBasicPa.NUM_AGENTS, 20);
		
		pm.setParam(PmBasicPa.NUM_AGENTS, 23);
				
		assertEquals(23, pm.getParam(PmBasicPa.SPECIAL_NUM_AGENTS));
	}
	
	@Test
	public void testDefaultPm() {
		PmParameterManager pm = PmParameterManager.getNewInstance();

		// no default pm defined:
		assertEquals(
				((Integer) pm.getParam(PmBasicPa.NUM_AGENTS)).intValue(),
				((Integer) PmBasicPa.NUM_AGENTS.getDefaultValue()).intValue());

		// default pm defined:
		PmParameterManager.setParameter(PmBasicPa.NUM_AGENTS, new Integer(42));

		PmParameterManager defaultPm = PmParameterManager.getNewInstance();
		defaultPm.setParam(PmBasicPa.NUM_AGENTS, new Integer(34));

		pm.setDefaultPm(defaultPm);

		assertEquals(((Integer) pm.getParam(PmBasicPa.NUM_AGENTS)).intValue(), 34);
	}

	@Test
	public void testInstanceReset() {
		PmParameterManager.setParameter(PmBasicPa.NUM_AGENTS, new Integer(10));

		PmParameterManager pm = PmParameterManager.getNewInstance();
		pm.setParam(PmBasicPa.NUM_AGENTS, new Integer(15));

		assertEquals(new Integer(15), pm.getParam(PmBasicPa.NUM_AGENTS));

		pm.resetInstance();
		assertEquals(PmBasicPa.NUM_AGENTS.getDefaultValue(), pm.getParam(PmBasicPa.NUM_AGENTS));

		assertEquals(new Integer(10), PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS));
	}

	@Test
	public void testInstanceParamCopy() {
		PmParameterManager.setParameter(PmBasicPa.NUM_AGENTS, new Integer(10));
		PmParameterManager.setParameter(PmBasicPa.COPY_NUM_AGENTS, new Integer(
				20));

		PmParameterManager pm = PmParameterManager.getNewInstance();
		pm.setDefaultPm(PmParameterManager.getInstance(null));
		pm.setParam(PmBasicPa.NUM_AGENTS, new Integer(30));

		pm.copyParamValue(PmBasicPa.COPY_NUM_AGENTS,
				PmBasicPa.SPECIAL_NUM_AGENTS);

		assertEquals(new Integer(20), pm.getParam(PmBasicPa.SPECIAL_NUM_AGENTS));
	}
}
