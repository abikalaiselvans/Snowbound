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

public class ContrastDialog extends Dialog
{
    private static final long serialVersionUID = -6924085316803625893L;
    private Label contrastLabel = new Label("Contrast (-127 to 127): ");
    private TextField contrastField = new TextField(4);
    private Button OKbutton = new Button("  OK  ");
    private Button CancelButton = new Button("Cancel");
    private GridBagLayout gbl = new GridBagLayout();
    private GridBagAdder gba = new GridBagAdder();
    private static int contrast;

    public ContrastDialog(Frame owner, String dialogTitle, boolean modal)
    {
        super(owner, dialogTitle, modal);
        this.setLayout(gbl);
        this.setSize(300, 100);
        this.setResizable(false);
        this.setBackground(Color.lightGray);
        addComponents();
        this.setVisible(true);
    }

    public int getContrast()
    {
        return contrast;
    }

    public void setContrast(int contr)
    {
        contrast = contr; //instance method writes to static field,
						 // and tricky if multiple instance are being manipulated
    }

    private void addComponents()
    {
        gba.add(this,
                contrastLabel,
                1,
                0,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                contrastField,
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
        	if(contrastField != null){
        		 setContrast(Integer.parseInt(contrastField.getText()));
                 setVisible(false);
        	}
        	else{
        		contrastField = null;
        		System.out.println("no contrast");
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



