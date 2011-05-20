package de.cesr.parma.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;
import de.cesr.parma.reader.PmXmlParameterReader;

/**
 * ParMa
 *
 * @author Sascha Holzhauer
 * @date 20.05.2011 
 *
 */
public class TestXmlParameterReader {

	/**
	 * @throws Exception
	 * Created by Sascha Holzhauer on 20.05.2011
	 */
	@Before
	public void setUp() throws Exception {
		PmParameterManager.registerReader(new PmXmlParameterReader());
		PmParameterManager.setParameter(PmFrameworkPa.XM_PARAMETER_FILE, "./src/de/cesr/parma/tests/res/TestParameter.xml");
	}

	/**
	 * @throws Exception
	 * Created by Sascha Holzhauer on 20.05.2011
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 20.05.2011
	 */
	@Test
	public final void testInitParameters() {
		assertEquals("", 10, PmParameterManager.getParameter(BasicPa.NUM_AGENTS));
		PmParameterManager.init();
		assertEquals("", 100, ((Integer) PmParameterManager.getParameter(BasicPa.NUM_AGENTS)).intValue());
	}

}
