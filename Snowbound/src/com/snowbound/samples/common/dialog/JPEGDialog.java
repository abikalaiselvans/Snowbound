/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common.dialog;

import java.awt.Button;
import java.awt.Choice;
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

public class JPEGDialog extends Dialog
{
    private static final long serialVersionUID = 7131024545263742588L;

    Button OKbutton = new Button("OK");
    Label qualityLabel = new Label("JPEG Quality (0-100): ");
    Label interleaveLabel = new Label("JPEG Interleave: ");
    Label horizLabel = new Label("Horizontal");
    Label vertiLabel = new Label("Vertical");
    TextField qualField = new TextField(3);
    Choice horizChoice = new Choice();
    Choice vertChoice = new Choice();
    GridBagLayout gbl = new GridBagLayout();
    GridBagAdder gba = new GridBagAdder();

    public JPEGDialog(Frame owner, String dialogTitle, boolean modal)
    {
        super(owner, dialogTitle, modal);
        this.setLayout(gbl);
        this.setSize(200, 300);
        this.setResizable(false);
        this.setBackground(Color.lightGray);

        horizChoice.add("1");
        horizChoice.add("2");
        horizChoice.add("3");
        horizChoice.add("4");

        vertChoice.add("1");
        vertChoice.add("2");
        vertChoice.add("3");
        vertChoice.add("4");

        OKbutton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        });

        gba.add(this,
                qualityLabel,
                0,
                0,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.EAST);
        gba.add(this,
                qualField,
                1,
                0,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                interleaveLabel,
                0,
                1,
                1,
                1,
                1,
                1,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER);
        gba.add(this,
                horizLabel,
                1,
                2,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                horizChoice,
                0,
                2,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                vertChoice,
                0,
                3,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                vertiLabel,
                1,
                3,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                OKbutton,
                1,
                4,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        this.setVisible(true);
    }

    public int getQuality()
    {
        return Integer.parseInt(qualField.getText());
    }

    public int getHorizontalInterleave()
    {
        return Integer.parseInt(horizChoice.getSelectedItem());
    }

    public int getVerticalInterleave()
    {
        return Integer.parseInt(vertChoice.getSelectedItem());
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
            cont.add(comp, cons);
        }
    }
}




