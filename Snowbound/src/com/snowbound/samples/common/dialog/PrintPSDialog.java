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

public class PrintPSDialog extends Dialog
{
    private static final long serialVersionUID = 8799300259031366751L;
    Button OKbutton = new Button("OK");
    Button cancelButton = new Button("Cancel");
    Label psLabel = new Label("Choose PCL or Postscript: ");
    Label portLabel = new Label("Specify the printer location (port):");
    Choice psChoice = new Choice();
    TextField portField = new TextField(15);
    GridBagLayout gbl = new GridBagLayout();
    GridBagAdder gba = new GridBagAdder();

    public PrintPSDialog(Frame owner, String dialogTitle, boolean modal)
    {
        super(owner, dialogTitle, modal);
        this.setLayout(gbl);
        this.setSize(200, 220);
        this.setResizable(false);
        this.setBackground(Color.lightGray);

        psChoice.add("Postscript");
        psChoice.add("PCL");

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
                psLabel,
                0,
                0,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                psChoice,
                0,
                1,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                portLabel,
                0,
                2,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                portField,
                0,
                3,
                1,
                1,
                1,
                1,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER);
        gba.add(this,
                OKbutton,
                0,
                4,
                1,
                1,
                1,
                1,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER);
        gba.add(this,
                cancelButton,
                0,
                5,
                1,
                1,
                1,
                1,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER);
        this.setVisible(true);
    }

    public int getFormat()
    {
        if (psChoice.getSelectedItem() == "Postscript") {
            return 14;
        }
        else if (psChoice.getSelectedItem() == "PCL") {
            return 57;
        }

        return 14;
    }

    public String getPort()
    {
        return portField.getText();
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
}// end JPEGDialog class



