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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrintDialog extends Dialog
{
    private static final long serialVersionUID = 8391843582765118355L;
    Button OKbutton = new Button("OK");
    Button cancelButton = new Button("Cancel");
    Label dotsLabel = new Label("Select a dots per inch value: ");
    Choice dotsChoice = new Choice();
    GridBagLayout gbl = new GridBagLayout();
    GridBagAdder gba = new GridBagAdder();

    public PrintDialog(Frame owner, String dialogTitle, boolean modal)
    {
        super(owner, dialogTitle, modal);
        this.setLayout(gbl);
        this.setSize(200, 150);
        this.setResizable(false);
        this.setBackground(Color.lightGray);

        dotsChoice.add("150");
        dotsChoice.add("300");
        dotsChoice.add("600");
        dotsChoice.add("1200");

        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        });

        OKbutton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        });

        // Add Components to the Dialog
        gba.add(this,
                dotsLabel,
                0,
                0,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                dotsChoice,
                0,
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
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER);
        gba.add(this,
                cancelButton,
                0,
                3,
                1,
                1,
                1,
                1,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER);
        this.setVisible(true);
    }

    public int getDots()
    {
        return Integer.parseInt(dotsChoice.getSelectedItem());
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
    }
}



