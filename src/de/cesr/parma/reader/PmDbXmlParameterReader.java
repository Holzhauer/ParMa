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
package de.cesr.parma.reader;


import static de.cesr.parma.core.PmParameterManager.setParameter;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;


/**
 * Reads the database settings from an XML-File (specified by PmFrameworkPa.DB_SETTINGS_FILE)
 * 
 * @author Sascha Holzhauer
 * @date 29.03.2010
 * 
 */
public class PmDbXmlParameterReader extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(PmDbXmlParameterReader.class);
	
	PmParameterDefinition settingsFile;
	
	/**
	 * @param settingsFile parameter definition of location of XML file that specifies database settings
	 */
	public PmDbXmlParameterReader(PmParameterDefinition settingsFile) {
		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Init PmDbXmlParameterReader with " + PmParameterManager.getFullName(settingsFile));
		}
		// LOGGING ->

		this.settingsFile = settingsFile;
	}
	
	/**
	 * 
	 */
	public PmDbXmlParameterReader() {
		this.settingsFile = PmFrameworkPa.DB_SETTINGS_FILE;
		// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Init PmDbXmlParameterReader with " + PmParameterManager.getFullName(PmFrameworkPa.DB_SETTINGS_FILE));
				}
				// LOGGING ->
	}
	
	
	/**
	 * Read parameters from the XML file
	 */
	@Override
	public void initParameters() {
		
		logger.info("Read Database settings from XML-File " + PmParameterManager.getParameter(settingsFile));
		File file = new File((String) PmParameterManager.getParameter(settingsFile));
		
		if (!file.exists()) {
			logger.warn("DB Settings XML file ("
					+ PmParameterManager.getParameter(this.settingsFile)
					+ ") does not exist! "
					+ "This might have impacts on further DB parameter initialisatons (e.g. mixing up parameter ID "
					+ "with wrong DB configuration)!");
		} else {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				doc.getDocumentElement().normalize();
		
				NodeList nodeLst = doc.getElementsByTagName("db");
				Node fstNode = nodeLst.item(0);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("SQL_LOCATION");
					Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					NodeList fstNm = fstNmElmnt.getChildNodes();
					setParameter(PmFrameworkPa.LOCATION, (fstNm.item(0)).getNodeValue());
		
					NodeList dbnameNmElmntLst = fstElmnt.getElementsByTagName("SQL_DBNAME");
					Element dbnameNmElmnt = (Element) dbnameNmElmntLst.item(0);
					NodeList dbnameNm = dbnameNmElmnt.getChildNodes();
					setParameter(PmFrameworkPa.DBNAME, (dbnameNm.item(0)).getNodeValue());
		
					NodeList userNmElmntLst = fstElmnt.getElementsByTagName("SQL_USER");
					Element userNmElmnt = (Element) userNmElmntLst.item(0);
					NodeList userNm = userNmElmnt.getChildNodes();
					setParameter(PmFrameworkPa.USER, (userNm.item(0)).getNodeValue());
		
					NodeList pwNmElmntLst = fstElmnt.getElementsByTagName("SQL_PASSWORD");
					Element pwNmElmnt = (Element) pwNmElmntLst.item(0);
					NodeList pwNm = pwNmElmnt.getChildNodes();
					setParameter(PmFrameworkPa.PASSWORD, (pwNm.item(0)).getNodeValue());
		
					NodeList tbNmElmntLst = fstElmnt.getElementsByTagName("SQL_TBLNAME_PARAMS");
					Element tbNmElmnt = (Element) tbNmElmntLst.item(0);
					NodeList tbNm = tbNmElmnt.getChildNodes();
					setParameter(PmFrameworkPa.TBLNAME_PARAMS, (tbNm.item(0)).getNodeValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
