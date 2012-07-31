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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.NotActiveException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import de.cesr.parma.core.PmAbstractParameterReader;
import de.cesr.parma.core.PmParameterDefinition;



/**
 * PARameter MAnager
 * 
 * Fetches Parameters by GUI
 * 
 * TODO type check
 * TODO choose from registered parameter definitions
 * TODO make wait for dialog
 * 
 * @author Holzhauer
 * @date 08.01.2009
 * 
 */
public class PmGuiParameterReader extends PmAbstractParameterReader {

	/**
	 * Logger
	 */
	static Logger	logger	= Logger.getLogger(PmGuiParameterReader.class);

	/**
	 * @see de.cesr.parma.core.PmAbstractParameterReader#initParameters()
	 */
	@Override
	public void initParameters() {

		try {
			throw new NotActiveException("Not yet implemented");
		} catch (NotActiveException e) {
			e.printStackTrace();
		}
		// TODO this.askForParameter();
	}

	void askForParameter() {

		ParameterDialog paramDialog = new ParameterDialog(this);
		paramDialog.setVisible(true);
	}

	public static void main(String[] a) {
		PmGuiParameterReader reader = new PmGuiParameterReader();
		reader.askForParameter();
	}
}



/**
 * ParMa
 * 
 * @author Sascha Holzhauer
 * @date 30.06.2011
 * 
 */
class ParameterDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	PmGuiParameterReader		reader;

	JLabel						labelName			= new JLabel("Parameter name");
	JLabel						labelValue			= new JLabel("Value");

	JTextField					name				= new JTextField();
	JTextField					value				= new JTextField();

	boolean						next				= false;

	public ParameterDialog(PmGuiParameterReader reader) {
		super(new JFrame());
		System.out.println("New Dialog");
		this.reader = reader;
		init();
		pack();
	}

	private void init() {
		this.setTitle("Parameter Dialog");
		this.setLayout(new GridLayout(3, 2));
		this.add(labelName);
		this.add(name);
		this.add(labelValue);
		this.add(value);

		JButton buttonOk = new JButton("OK");
		buttonOk.addActionListener(this);
		this.add(buttonOk);

		JButton buttonNext = new JButton("Next..");
		buttonNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				next = true;
				setParameterValue();
				setVisible(false);
				dispose();
			}
		});
		this.add(buttonNext);
	}

	public boolean isNext() {
		return next;
	}

	@Override
	public String getName() {
		return name.getText();
	}

	public String getValue() {
		return value.getText();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		setParameterValue();
		setVisible(false);
		System.out.println("Dispose");
		dispose();
	}

	private void setParameterValue() {

		String tagName = getName();
		String param_class = tagName.split(":")[0];
		String param_name = tagName.split(":")[1];

		String value = getValue();

		PmParameterDefinition definition;

		try {
			definition = (PmParameterDefinition) Enum.valueOf((Class<Enum>) Class.forName(param_class), param_name);
			setParameter(definition, value);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		PmGuiParameterReader.logger.debug("Set parameter " + tagName + " to " + value);

		if (isNext()) {
			reader.askForParameter();
			next = false;
		}
	}
}
