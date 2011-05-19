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



import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;
import de.cesr.parma.reader.PmDbParameterReader;
import de.cesr.parma.reader.PmDbXmlParameterReader;




/**
 * Note: Correct DB settings need to be specified in SqlPa!
 * 
 * @author Sascha Holzhauer
 * @date 29.06.2010
 * 
 */
public class TestDBParameterReader {

	/**
	 * @throws java.lang.Exception Created by Sascha Holzhauer on 29.06.2010
	 */
	@Before
	public void setUp() throws Exception {
		PmParameterManager.registerParametersDefinitions(Arrays.asList(PmFrameworkPa.values()));
		PmParameterManager.setParameter(PmFrameworkPa.PARAM_SET_ID, 1);
		PmParameterManager.setParameter(PmFrameworkPa.DB_SETTINGS_FILE, "./src/de/cesr/parma/tests/res/DBSettingsMysql3.xml");
		PmDbXmlParameterReader dbXmlReader = new PmDbXmlParameterReader();
		PmDbParameterReader dbReader = new PmDbParameterReader();
		PmParameterManager.registerReader(dbXmlReader);
		PmParameterManager.registerReader(dbReader);
	}

	static int	order	= 0;
	static String	first	= "", second = "", third = "";

	/**
	 * @throws java.lang.Exception Created by Sascha Holzhauer on 29.06.2010
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link param.framework.DBParameterReader#initParameters()}.
	 */
	@Test
	public final void testInitParameters() {
		assertTrue("", 0 < (Integer) PmParameterManager.getParameter(PmTestBasicPa.NUM_AGENTS));
		PmParameterManager.init();
		assertEquals("", 10, PmParameterManager.getParameter(PmTestBasicPa.NUM_AGENTS));
	}

	/**
	 * Test method for {@link param.framework.AbstractParameterReader#registerParameterReader(param.ParameterReader)}.
	 */
	@Test
	public final void testRegisterParameterReader() {
		PmParameterManager.registerReader(new PmAbstractParameterReader() {
			@Override
			public void initParameters() {
				TestDBParameterReader.this.orderTest("A");
			}
		});
		PmParameterManager.registerReader(new PmAbstractParameterReader() {
			@Override
			public void initParameters() {
				TestDBParameterReader.this.orderTest("B");
			}
		});
		PmParameterManager.registerReader(new PmAbstractParameterReader() {
			@Override
			public void initParameters() {
				TestDBParameterReader.this.orderTest("C");
			}
		});
		PmParameterManager.init();
		assertEquals("first should be assigned A", "A", first);
		assertEquals("seconds should be assigned B", "B", second);
		assertEquals("third should be assigned C", "C", third);
	}

	/**
	 * @param reader Created by Sascha Holzhauer on 30.06.2010
	 */
	public void orderTest(String reader) {
		order++;
		switch (order) {
			case 1:
				first = reader;
				break;
			case 2:
				second = reader;
				break;
			case 3:
				third = reader;
				break;
		}
	}
}
