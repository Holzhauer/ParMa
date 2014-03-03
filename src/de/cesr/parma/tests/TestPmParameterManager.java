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
		assertEquals(PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS), new Integer(10));
		
		PmParameterManager pm = PmParameterManager.getNewInstance();
		pm.setParam(PmBasicPa.NUM_AGENTS, new Integer(20));

		assertEquals(pm.getParam(PmBasicPa.NUM_AGENTS), new Integer(20));
		assertEquals(PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS),
				new Integer(10));
	}

	@Test
	public void testInstanceRetrieval() {
		Object id = new Object();
		PmParameterManager pm = PmParameterManager.getNewInstance(id);

		PmParameterManager.setParameter(id, PmBasicPa.NUM_AGENTS, new Integer(
				15));

		assertEquals(PmParameterManager.getParameter(id, PmBasicPa.NUM_AGENTS),
				new Integer(15));
		assertEquals(PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS),
				new Integer(10));

		assertEquals(pm.getParam(PmBasicPa.NUM_AGENTS), new Integer(15));
		
		pm.setParam(PmBasicPa.NUM_AGENTS, new Integer(42));
		assertEquals(pm.getParam(PmBasicPa.NUM_AGENTS), new Integer(42));
		assertEquals(PmParameterManager.getParameter(id, PmBasicPa.NUM_AGENTS),
				new Integer(42));
		assertEquals(PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS),
				new Integer(10));
	}

	@Test
	public void testDefaultPm() {
		PmParameterManager pm = PmParameterManager.getNewInstance();

		// no default pm defined:
		assertEquals(
				((Integer) PmBasicPa.NUM_AGENTS.getDefaultValue()).intValue(),
				((Integer) pm.getParam(PmBasicPa.NUM_AGENTS)).intValue());

		// default pm defined:
		PmParameterManager.setParameter(PmBasicPa.NUM_AGENTS, new Integer(42));

		PmParameterManager defaultPm = PmParameterManager.getNewInstance();
		defaultPm.setParam(PmBasicPa.NUM_AGENTS, new Integer(34));

		pm.setDefaultPm(defaultPm);

		assertEquals(34,
				((Integer) pm.getParam(PmBasicPa.NUM_AGENTS)).intValue());
	}

	@Test
	public void testInstanceReset() {
		PmParameterManager.setParameter(PmBasicPa.NUM_AGENTS, new Integer(10));

		PmParameterManager pm = PmParameterManager.getNewInstance();
		pm.setParam(PmBasicPa.NUM_AGENTS, new Integer(15));

		assertEquals(pm.getParam(PmBasicPa.NUM_AGENTS), new Integer(15));

		pm.resetInstance();
		assertEquals(pm.getParam(PmBasicPa.NUM_AGENTS),
				PmBasicPa.NUM_AGENTS.getDefaultValue());

		assertEquals(PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS),
				new Integer(10));
	}
}
