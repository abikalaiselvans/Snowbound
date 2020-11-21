/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.loadandsave;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.snowbound.samples.common.dialog.*;

public class LoadAndSaveController
{
    private String fileName = null;
    private String openDir = null;

    public LoadAndSaveUI UI = null;

    public LoadAndSaveController(LoadAndSaveUI lu)
    {
        UI = lu;

        // Add ActionListeners
        UI.menuItemExit.addActionListener(new menuItemExitActionListener());
        UI.menuItemOpen.addActionListener(new menuItemOpenActionListener());
        UI.menuItemSave.addActionListener(new menuItemSaveActionListener());
        UI.menuItemZoomIn.addActionListener(new zoomInActionListener());
        UI.menuItemZoomOut.addActionListener(new zoomOutActionListener());
        UI.addWindowListener(new UIWindowAdapter());
    }

    class menuItemSaveActionListener
        implements ActionListener
    {
        int quality = 100;
        int verticalInterleave = 0;
        int horizontalInterleave = 0;
        int fileType;
        int status;

        public void actionPerformed(ActionEvent e)
        {
            SaveDialog sd = new SaveDialog(UI, "Select and Output Format", true);
            fileType = sd.getFormat();

            /* make sure we have a legit format selected */
            if( fileType != SaveDialog.NO_FORMAT_SELECTED ) {
	            if (fileType == 13)
	            {
	                JPEGDialog jd = new JPEGDialog(UI, "JPEG Parameters", true);
	                quality = jd.getQuality();
	                verticalInterleave = jd.getVerticalInterleave();
	                horizontalInterleave = jd.getHorizontalInterleave();

	                UI.snowPanel.setJPEGparameters(quality,
	                                               horizontalInterleave,
	                                               verticalInterleave);
	            }

	            FileDialog fd = new FileDialog(UI, "Save File", FileDialog.SAVE);

	            if (openDir != null)
	            {
	                fd.setDirectory(openDir);
	            }

	            fd.setVisible(true);

	            fileName = new String(fd.getDirectory() + fd.getFile());

	            if (fd.getFile() != null)
	            {
	                UI.snowPanel.saveImage(fileName, fileType);
	            }
        	}
        }
    }

    class menuItemOpenActionListener
        implements ActionListener
    {
        private String openDir = null;

        public void actionPerformed(ActionEvent e)
        {
            FileDialog fd = new FileDialog(UI, "Open a File", FileDialog.LOAD);

            if (openDir != null)
            {
                fd.setDirectory(openDir);
            }

            fd.setVisible(true);

            openDir = fd.getDirectory();
            fileName = new String(fd.getDirectory() + fd.getFile());

            if (fd.getFile() != null)
            {
                UI.snowPanel.decompressImage(fileName);
            }
            else
            {
                fileName = null;
            }
        }
    }

    class menuItemExitActionListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            UI.dispose();
            System.exit(0);
        }
    }

    class zoomInActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            UI.snowPanel.zoomIn(20);
            UI.snowPanel.repaint();
        }
    }

    class zoomOutActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            UI.snowPanel.zoomOut(20);
            UI.snowPanel.repaint();
        }
    }

    class UIWindowAdapter extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            UI.dispose();
            System.exit(0);
        }
    }
}
