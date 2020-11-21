/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common.dialog;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Snow.FormatHash;


/**
 * Displays a save file dialog to the user
 */
public class SaveDialog extends Dialog implements ActionListener{
    private static final long serialVersionUID = -3641906057349537388L;
    
    public static final int NO_FORMAT_SELECTED = -1;
    List formatList;
    String selectedFormat = null;
    Button actionButton;
    Button buttonOK;
    Button buttonCancel;


    /**
     * Constructor.
     *
     * @param owner The frame which requested this dialog
     * @param st The Titlebar String
     * @param modal Whether or not this dialog should be modal
     */
    public SaveDialog(Frame owner, String st, boolean modal)
    {
        super(owner, st, modal);
        setSize(400, 400);
        setBackground(Color.lightGray);
        setResizable(false);
        setLayout(null);
        addWidgets();
        setVisible(true);
        pack();
    }

    void initializeFormatsList() {
        FormatHash formatHash = FormatHash.getInstance();
        String[] saveFormats = formatHash.getAvailibleSaveFormats();
        for (int index = 0; index < saveFormats.length; index++)
        {
            formatList.add(saveFormats[index]);
        }
    }

    void addWidgets() {
    	formatList = new List();
        formatList.setMultipleMode(false);
        formatList.setBounds(0, 20, 400, 330);
        initializeFormatsList();

        buttonOK = new Button(" OK ");
        buttonCancel = new Button(" Cancel ");
        buttonOK.setBounds(10, 360, 110 , 25);
        buttonCancel.setBounds(280, 360, 110, 25);

        formatList.addActionListener(this);
        buttonOK.addActionListener(this);
        buttonCancel.addActionListener(this);

        add(formatList);
        add(buttonOK);
        add(buttonCancel);
    }

    /**
     * Gets the hash value for the current format
     *
     * @return The hash value, -1 for an error
     */
    public int getFormat()
    {
        if (selectedFormat != null)
        {
            return FormatHash.getInstance().getFormatCode(selectedFormat);
        }
        else
            return NO_FORMAT_SELECTED;
    }

    /**
     * Sets the current format
     *
     * @param format the new format
     *
     * @return Status
     */
    void setFormat(String format)
    {
        selectedFormat = format;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == buttonOK) {
        	selectedFormat = formatList.getSelectedItem();
            setVisible(false);
        }
        else if (e.getSource() == formatList) {
        	selectedFormat = e.getActionCommand();
            return;
        }
        else if( e.getSource() == buttonCancel) {
        	setVisible(false);
        	dispose();
        }
    }
}



