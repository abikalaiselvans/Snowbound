/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.manipulation;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.snowbound.samples.common.dialog.BrightnessDialog;
import com.snowbound.samples.common.dialog.ContrastDialog;
import com.snowbound.samples.common.dialog.JPEGDialog;
import com.snowbound.samples.common.dialog.RotateDialog;
import com.snowbound.samples.common.dialog.SaveDialog;

public class ImageManipulationController {
	private String fileName = null;
    private String openDir = null;

    public ImageManipulationUI UI = null;

    /**
     * Constructor.
     *
     * @param UI object (the window frame)
     */
    public ImageManipulationController(ImageManipulationUI lu)
    {
        UI = lu;

        /* give the UI widgets all action listeners */
        UI.menuItemExit.addActionListener(new menuItemExitActionListener());
        UI.menuItemSave.addActionListener(new menuItemSaveActionListener());
        UI.menuItemOpen.addActionListener(new menuItemOpenActionListener());
        UI.menuItemZoomIn.addActionListener(new zoomInActionListener());
        UI.menuItemZoomOut.addActionListener(new zoomOutActionListener());
        UI.menuItemMagBox.addActionListener(new magBoxActionListener());
        UI.menuItemZoomBox.addActionListener(new zoomBoxActionListener());
        UI.menuItemFitHeight.addActionListener( new fitToHeightActionListener());
        UI.menuItemFitWidth.addActionListener( new fitToWidthActionListener());
        UI.menuItemDeskew.addActionListener(new deskewActionListener());
        UI.menuItemBrightness.addActionListener(new brightnessActionListener());
        UI.menuItemContrast.addActionListener(new contrastActionListener());
        UI.menuItemRotate.addActionListener(new rotateActionListener());
        UI.menuItemColorGray.addActionListener(new colorGrayActionListener());
        UI.menuItemFlipX.addActionListener(new flipXActionListener());
        UI.menuItemFlipY.addActionListener(new flipYActionListener());
        UI.menuItemInvert.addActionListener(new invertActionListener());

        UI.addWindowListener(new UIWindowAdapter());
    }

    /* create the various listeners attached to UI widgets */

    class zoomBoxActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // Alert the Panel that The Zoom Rectangle is now Active
           // UI.snowPanel.setZoomRect(true);
        	UI.snowPanel.disableFitWidthHeight();
        	UI.snowPanel.activateRubberBandListener();
        }
    }

    class menuItemOpenActionListener implements ActionListener
    {
        private int status = 0;

        public void actionPerformed(ActionEvent e)
        {
        	/* make sure mag panel is off before we open another image */
            if (UI.snowPanel.getDrawMag() == true)
                UI.snowPanel.toggleMagnifyRectangle();

            FileDialog fd = new FileDialog(UI, "Open a File", FileDialog.LOAD);

            if (openDir != null)
            {
                fd.setDirectory(openDir);
            }
            fd.setVisible(true);

            openDir = fd.getDirectory();
            fileName = new String(fd.getDirectory() + fd.getFile());

            /* make sure we have the file */
            if (fd.getFile() != null)
            {
                status = UI.snowPanel.decompressImage(fileName);
                if (status == 0)
                {
                    UI.menuItemSave.setEnabled(true);
                }
            }
            else
            {
                fileName = null;
            }
        }
    }

    class magBoxActionListener implements ActionListener
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            if (UI.snowPanel.getDrawMag() == false)
            {
                // Turns off the Zoom Rectangle if it has been enabled
                if (UI.snowPanel.getZoomRect() == true)
                {
                    UI.snowPanel.setZoomRect(false);
                }

                UI.snowPanel.toggleMagnifier();
            }
            else
            {
                UI.snowPanel.toggleMagnifier();
            }
        }
    }

    class menuItemSaveActionListener implements ActionListener
    {
        int quality = 100;
        int verticalInterleave = 0;
        int horizontalInterleave = 0;
        int fileType;
        int status;

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            SaveDialog sd = new SaveDialog(UI, "Select Output Format", true);
            fileType = sd.getFormat();

            /* make sure we have a legit format selected */
            if( fileType != SaveDialog.NO_FORMAT_SELECTED ) {
                if (fileType == 13)
                {
                    JPEGDialog jd = new JPEGDialog(UI, "JPEG Parameters", true);
                    quality = jd.getQuality();
                    verticalInterleave = jd.getHorizontalInterleave();
                    horizontalInterleave = jd.getVerticalInterleave();
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

    class menuItemExitActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }

    class zoomInActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	/* we need to toggle to boolean fit width/height before
        	 * image will be repainted
        	 */
        	UI.snowPanel.disableFitWidthHeight();
            UI.snowPanel.zoomIn(20);
            UI.snowPanel.repaint();
        }
    }

    class zoomOutActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	/* we need to toggle to boolean fit width/height before
        	 * image will be repainted
        	 */
        	UI.snowPanel.disableFitWidthHeight();
            UI.snowPanel.zoomOut(20);
            UI.snowPanel.repaint();
        }
    }

    class deskewActionListener implements ActionListener
    {
	    public void actionPerformed(ActionEvent e)
	    {
	        UI.snowPanel.deskewImage();
	        UI.snowPanel.repaint();
	    }
    }

    class brightnessActionListener implements ActionListener
    {
	    BrightnessDialog bd = null;

	    public void actionPerformed(ActionEvent e)
	    {
	        bd = new BrightnessDialog(UI, "Adjust Brightness", true);
	        UI.snowPanel.setBrightness(bd.getBrightness());
	        UI.snowPanel.repaint();
	    }
	}

    class contrastActionListener implements ActionListener
	{
	    ContrastDialog cd = null;

	    public void actionPerformed(ActionEvent e)
	    {
	        cd = new ContrastDialog(UI, "Adjust Contrast", true);
	        UI.snowPanel.setContrast(cd.getContrast());
	        UI.snowPanel.repaint();
	    }
	}

    class colorGrayActionListener implements ActionListener
	{
	    RotateDialog rd = null;

	    public void actionPerformed(ActionEvent e)
	    {
	    	UI.snowPanel.colorGray();
	    	UI.snowPanel.repaint();
	    }
	}

    class rotateActionListener implements ActionListener
	{
	    RotateDialog rd = null;

	    public void actionPerformed(ActionEvent e)
	    {
	        rd = new RotateDialog(UI, "Rotate Image", true);
	        UI.snowPanel.rotateImage(rd.getRotateAngle() * 100);
	        UI.snowPanel.repaint();
	    }
	}

    class flipXActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent e)
	    {
	        UI.snowPanel.flipX();
	        UI.snowPanel.repaint();
	    }
	}

	class flipYActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent e)
	    {
	        UI.snowPanel.flipY();
	        UI.snowPanel.repaint();
	    }
	}

    class fitToHeightActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent e)
	    {
	        UI.snowPanel.fitToHeight();
	    }
	}

	class fitToWidthActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent e)
	    {
	        UI.snowPanel.fitToWidth();
	    }
	}

	class invertActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent e)
	    {
	        UI.snowPanel.invert();
	        UI.snowPanel.repaint();
	    }
	}

	/* handle window closing events */
    class UIWindowAdapter extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }
}
