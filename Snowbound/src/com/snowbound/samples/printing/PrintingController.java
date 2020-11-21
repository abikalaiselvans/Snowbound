/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.printing;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;
import com.snowbound.samples.common.dialog.*;

public class PrintingController
{
    private String fileName = null;
    private String openDir = null;

    public PrintingUI UI = null;

    public PrintingController(PrintingUI lu)
    {
        UI = lu;

        // Add ActionListeners
        UI.menuItemExit.addActionListener(new menuItemExitActionListener());
        UI.menuItemOpen.addActionListener(new menuItemOpenActionListener());
        UI.menuItemSave.addActionListener(new menuItemSaveActionListener());
        UI.menuItemPrint.addActionListener(new menuItemPrintActionListener());
        UI.menuItemPrintPS.addActionListener(new menuItemPrintPSActionListener());
        UI.menuItemZoomIn.addActionListener(new zoomInActionListener());
        UI.menuItemZoomOut.addActionListener(new zoomOutActionListener());
        UI.addWindowListener(new UIWindowAdapter());
    }

    class menuItemPrintPSActionListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            int fileType = 0;
            String destination;
            PrintPSDialog psd = new PrintPSDialog(UI, "Print Info", true);
            fileType = psd.getFormat();
            destination = psd.getPort();
            UI.snowPanel.printPSPCL(fileType, destination);
        }
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

            if (openDir != null) {
                fd.setDirectory(openDir);
            }

            fd.setVisible(true);

            fileName = new String(fd.getDirectory() + fd.getFile());

            if (fd.getFile() != null) {
                UI.snowPanel.saveImage(fileName, fileType);
            }
        }
    }

    class menuItemOpenActionListener
        implements ActionListener
    {
        private String openDir = null;
        private String fileName = null;

        public void actionPerformed(ActionEvent e)
        {
            FileDialog fd = new FileDialog(UI, "Open a File", FileDialog.LOAD);

            if (openDir != null) {
                fd.setDirectory(openDir);
            }

            fd.setVisible(true);

            openDir = fd.getDirectory();
            fileName = new String(fd.getDirectory() + fd.getFile());

            if (fd.getFile() != null)
            {
                UI.snowPanel.decompressImage(fileName);
            }
            else {
                fileName = null;
            }
        } // end actionPerformed
    }// end inner class

    class menuItemPrintActionListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            int dpi;
            PrintDialog pd = new PrintDialog(UI, "Print Settings", true);
            dpi = pd.getDots();
            UI.snowPanel.printImage(UI, dpi);
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
    } // end innerclass

    class zoomInActionListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            UI.snowPanel.zoomIn(20);
            UI.snowPanel.repaint();
        }
    } // end inner class

    class zoomOutActionListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            UI.snowPanel.zoomOut(20);
            UI.snowPanel.repaint();
        }
    } // end inner class

    class UIWindowAdapter extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            UI.dispose();
            System.exit(0);
        }
    }
} // end controller class
