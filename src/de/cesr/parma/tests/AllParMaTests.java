/**
 * 
 */
package de.cesr.parma.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * @author holzhauer
 *
 */
@RunWith(Suite.class)
@SuiteClasses( {
TestPmParameterManager.class, TestDBParameterReader.class, TestXmlParameterReader.class
 })

public class AllParMaTests {

}
