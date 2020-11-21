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
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DespeckleDialog extends Dialog
{
    private static final long serialVersionUID = 8776822021841163936L;
    private Label despeckleLabel = new Label("Despeckle Quality (1 to 100): ");
    private TextField despeckleField = new TextField(4);
    private Button OKbutton = new Button("  OK  ");
    private Button CancelButton = new Button("Cancel");
    private GridBagLayout gbl = new GridBagLayout();
    private GridBagAdder gba = new GridBagAdder();
    private static int despeckle;

    public DespeckleDialog(Frame owner, String dialogTitle, boolean modal)
    {
        super(owner, dialogTitle, modal);
        this.setLayout(gbl);
        this.setSize(300, 100);
        this.setResizable(false);
        this.setBackground(Color.lightGray);
        addComponents();
        this.setVisible(true);
    }

    public int getDespeckleQuality()
    {
        return despeckle;
    }

    public void setDespeckleQuality(int desp)
    {
        despeckle = desp; //instance method writes to static field,
		                  // very tricky if multiple instance are being manipulated
    }

    private void addComponents()
    {
        gba.add(this,
                despeckleLabel,
                1,
                0,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                despeckleField,
                1,
                1,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                OKbutton,
                0,
                2,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                CancelButton,
                2,
                2,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);

        OKbutton.addActionListener(new okActionListener());
        CancelButton.addActionListener(new cancelActionListener());
    }

    class okActionListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	if(despeckleField != null){
        		setDespeckleQuality(Integer.parseInt(despeckleField.getText()));
                setVisible(false);
        	}
        	else{
        		despeckleField = null;
        		System.out.println("no despeckle");
        	}
            
        }
    }

    class cancelActionListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            setVisible(false);
        }
    }

    static class GridBagAdder
    {
        static GridBagConstraints cons = new GridBagConstraints();

        public void add(Container cont,
                        Component comp,
                        int x,
                        int y,
                        int width,
                        int height,
                        int weightx,
                        int weighty,
                        int fill,
                        int anchor)
        {
            cons.gridx = x;
            cons.gridy = y;
            cons.gridwidth = width;
            cons.gridheight = height;
            cons.weightx = weightx;
            cons.weighty = weighty;
            cons.fill = fill;
            cons.anchor = anchor;
            cont.add(comp, cons);
        }
    } // end GridBagAdderClass
} // end Brightness Dialog class




