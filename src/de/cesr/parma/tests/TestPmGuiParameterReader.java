package de.cesr.parma.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.reader.PmGuiParameterReader;

/**
 * ParMa
 * 
 * @author Sascha Holzhauer
 * @date 30.06.2011 
 *
 */
public class TestPmGuiParameterReader {

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		PmParameterManager.registerReader(new PmGuiParameterReader());
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	@Ignore // see PmGuiParameterReader
	public final void testInitParameters() {
		PmParameterManager.init();
		assertEquals("", 100, ((Integer) PmParameterManager.getParameter(PmBasicPa.NUM_AGENTS)).intValue());
	}

}
