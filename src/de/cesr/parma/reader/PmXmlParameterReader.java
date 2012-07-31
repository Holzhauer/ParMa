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
	
	/**
	 * @param settingsFile parameter definition of location of XML file that specifies general settings
	 */
	public PmXmlParameterReader(PmParameterDefinition settingsFile) {
		this.settingsFile = settingsFile;
	}
	
	/**
	 * Uses {@link PmFrameworkPa#XML_PARAMETER_FILE as source.}
	 */
	public PmXmlParameterReader() {
		this.settingsFile = PmFrameworkPa.XML_PARAMETER_FILE;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void initParameters() {
		try {
			
			logger.info("Read settings from XML-File " + settingsFile); 
					
			File file = new File((String) PmParameterManager.getParameter(settingsFile));
			
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
							String param_class = tagName.split(":")[0];
							String param_name = tagName.split(":")[1];
							
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
										setParameter(definition,
												Class.forName(value));
									}
								} else {
									// handle all other parameter types:
									setParameter(definition, value);
								}
								
							} catch (ClassNotFoundException e1) {
								logger.error("No such parameter definition in classpath: " + tagName);
								e1.printStackTrace();
							}
						}
						
					}
				}
				else {
					logger.warn("Parameter XML file " + PmParameterManager.getParameter(settingsFile) + " does not contain" +
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
