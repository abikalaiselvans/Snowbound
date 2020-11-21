/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common.dialog;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PathDialog extends Dialog
{
	private static final long serialVersionUID = 7131024545263742588L;
	
	String format;
    TextField formatField;
	Button actionButton;
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
    GridBagAdder gba = new GridBagAdder();

	public PathDialog(Frame owner, String st, boolean modal)
	{
		super(owner, st, modal);
		
		setSize(380, 150);
		setBackground(Color.lightGray);
		setResizable(false);
        setLayout(gbl);
		addButtons();
		show();
	}

	private void addButtons()
	{
		formatField = new TextField(30);
		actionButton = new Button(" OK ");
        actionButton.addActionListener(new buttonActionListener());
        gba.add(this,
        		formatField,
        		0,
        		0,
        		1,
        		1,
        		1,
        		1,
        		GridBagConstraints.NONE,
        		GridBagConstraints.CENTER);
        gba.add(this,
        		actionButton,
        		0,
        		1,
        		1,
        		1,
        		1,
        		1,
        		GridBagConstraints.NONE,
        		GridBagConstraints.CENTER);
	}


	public String getPath()
	{
          return format;
  	}
    public void setFormat(String format)
	{
         this.format = format;
	}

	class buttonActionListener implements ActionListener
    {
		public void actionPerformed(ActionEvent e)
		{
			hide();
            format = formatField.getText();
        }
	}

	static class GridBagAdder 
	{
		static GridBagConstraints cons = new GridBagConstraints();
		
		public void add(Container cont,Component comp, int x, int y, int width,
						int height, int weightx, int weighty, int fill, int anchor)
		{
			cons.gridx = x;
			cons.gridy = y;
			cons.gridwidth = width;
			cons.gridheight = height;
			cons.weightx = weightx;
			cons.weighty = weighty;
			cons.fill = fill;
			cons.anchor = anchor;
			cont.add(comp,cons);
		}
	}
}
