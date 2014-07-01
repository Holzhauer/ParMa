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


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterDefinition;
import de.cesr.parma.core.PmParameterManager;
import de.cesr.parma.definition.PmFrameworkPa;

/**
 * ParMa
 *
 * @author Sascha Holzhauer
 * @date 20.05.2011 
 *
 */
public class PmXmlParameterReader extends PmAbstractParameterReader {
	
	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(PmXmlParameterReader.class);

	PmParameterDefinition settingsFile;
	
	PmParameterManager pm;
	
	/**
	 * @param pm
	 * @param settingsFile
	 */
	public PmXmlParameterReader(PmParameterManager pm, PmParameterDefinition settingsFile) {
		this.pm = pm;
		this.settingsFile = settingsFile;
	}
	
	/**
	 * @param settingsFile parameter definition of location of XML file that specifies general settings
	 */
	public PmXmlParameterReader(PmParameterDefinition settingsFile) {
		this(PmParameterManager.getInstance(null), settingsFile);
	}
	
	/**
	 * Uses {@link PmFrameworkPa#XML_PARAMETER_FILE as source.}
	 */
	public PmXmlParameterReader() {
		this(PmFrameworkPa.XML_PARAMETER_FILE);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" }) // Enum type is clear
	public void initParameters() {
		try {
			
			logger.info("Read settings from XML-File " + settingsFile); 
					
			File file = new File((String) pm.getParam(settingsFile));
			
			if (!file.exists()) {
				logger.warn("Settings XML file (" + file + ") does not exist!");
			} else {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				doc.getDocumentElement().normalize();
				
				// get first "parameter" element
				Element parameterElem = (Element) doc.getElementsByTagName("parameters").item(0);
				if (parameterElem.hasChildNodes()) {
					NodeList parameterDefinitions = parameterElem.getChildNodes();
					for (int i = 0; i < parameterDefinitions.getLength(); i++) {					
						if (parameterDefinitions.item(i).getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) parameterDefinitions.item(i);
							String tagName = e.getTagName();
							String param_class, param_name;
							try {
								param_class = tagName.split(":")[0];
								param_name = tagName.split(":")[1];
							} catch (ArrayIndexOutOfBoundsException exception) {
								// <- LOGGING
								logger.error("You probabliy have an error in your parameter XML file:"
										+ "Please, stick to the form <PACKAGE.CLASS:PARAMETER>VALUE</PACKAGE.CLASS:PARAMETER>");
								// LOGGING ->
								throw new RuntimeException(
										"You probabliy have an error in your parameter XML file:"
												+ "Please, stick to the form <PACKAGE.CLASS:PARAMETER>VALUE</PACKAGE.CLASS:PARAMETER>");
							}
							
							String value = e.getFirstChild().getNodeValue();
							
							PmParameterDefinition definition;
							
							try {
								definition = (PmParameterDefinition) Enum.valueOf((Class<Enum>) Class
										.forName(param_class), param_name);
								
								logger.debug("Set parameter " + tagName
										+ " to " + value);
								
								if (definition.getType() == Class.class) {
									// handle Class.class parameter types:
									if (value.toString().length() > 0) {
										pm.setParam(definition,
												Class.forName(value));
									}
								} else {
									// handle all other parameter types:
									pm.setParam(definition, value);
								}
								
							} catch (ClassNotFoundException e1) {
								logger.error("No such parameter definition in classpath: " + tagName);
								e1.printStackTrace();
							}
						}
						
					}
				}
				else {
					logger.warn("Parameter XML file " + pm.getParam(settingsFile) + " does not contain" +
							" any parameter defualtParams");
				}
			}
		} catch (ParserConfigurationException e) {
			logger.error("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			logger.error("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
