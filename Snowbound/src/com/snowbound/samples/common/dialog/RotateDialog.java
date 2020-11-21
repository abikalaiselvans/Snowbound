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

public class RotateDialog extends Dialog
{
    private static final long serialVersionUID = 6815160098305378000L;
    private Label rotateLabel = new Label("Enter Number of Degrees to Rotate: ");
    private TextField rotateField = new TextField(4);
    private Button OKbutton = new Button("  OK  ");
    private Button CancelButton = new Button("Cancel");
    private GridBagLayout gbl = new GridBagLayout();
    private GridBagAdder gba = new GridBagAdder();
    private static int rotate;
    private int validEntry;

    public RotateDialog(Frame owner, String dialogTitle, boolean modal)
    {
        super(owner, dialogTitle, modal);
        this.setLayout(gbl);
        this.setSize(300, 100);
        this.setResizable(false);
        this.setBackground(Color.lightGray);
        addComponents();
        this.setVisible(true);
    }

    public int getRotateAngle()
    {
        return rotate;
    }

    public void setRotateAngle(int rot)
    {
        rotate = rot; //instance method writes to static field,
		              // and tricky if multiple instance are being manipulated(remove static modifier)
    }

    private void addComponents()
    {
        gba.add(this,
                rotateLabel,
                1,
                0,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                rotateField,
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
        	if(rotateField != null){
        		setRotateAngle(Integer.parseInt(rotateField.getText()));
                //validEntry = Integer.parseInt(rotateField.getText());
                setVisible(false);
        	}
        	else{
        		rotateField = null;
        		System.out.println("no rotation");
        	}
            
        }
    }

    class cancelActionListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            setRotateAngle(0);
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


