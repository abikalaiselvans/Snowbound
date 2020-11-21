/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.watermark;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import Snow.Defines;

import com.snowbound.samples.common.dialog.JPEGDialog;
import com.snowbound.samples.common.dialog.SaveDialog;

public class WaterMarkController
{
	private String fileName = null;
	private String openDir = null;
	public WaterMarkUI UI = null;
	
	/* connect back to the view and wire up the action listeners */
	public WaterMarkController(WaterMarkUI watermarkUI)
	{	
	   UI = watermarkUI;
	   
	   UI.menuItemExit.addActionListener(new menuItemExitActionListener());
	   UI.menuItemOpen.addActionListener(new menuItemOpenActionListener());
	   UI.menuItemSave.addActionListener(new menuItemSaveActionListener());
	   UI.menuItemZoomIn.addActionListener(new zoomInActionListener());
	   UI.menuItemZoomOut.addActionListener(new zoomOutActionListener());
	   UI.menuItemSelectWatermark.addActionListener( new waterMarkSelect());
	   UI.menuItemGray.addActionListener(new waterMarkGray());
	   UI.menuItemBlack.addActionListener(new waterMarkBlack());
	   UI.menuItemInvert.addActionListener(new waterMarkInvert());
	   UI.menuItemAlpha.addActionListener(new waterMarkAlpha());
	   UI.addWindowListener(new UIWindowAdapter());
	}
	
	/* inner classes below are triggered by menu events */

	class menuItemSaveActionListener implements ActionListener
	{
		int quality = 100;
		int verticalInterleave = 0;
		int horizontalInterleave = 0;
		int fileType;
		int status;

		public void actionPerformed(ActionEvent e)
		{
			SaveDialog sd = new SaveDialog(UI, "Select Output Format", true);
			fileType = sd.getFormat();
			if (fileType == Defines.JPEG)
			{
				JPEGDialog jd = new JPEGDialog(UI,"JPEG Parameters", true);
				quality = jd.getQuality();
				verticalInterleave = jd.getVerticalInterleave();
				horizontalInterleave = jd.getHorizontalInterleave();

				UI.snowPanel.setJPEGparameters(quality,horizontalInterleave,verticalInterleave);
			}

			FileDialog fd = new FileDialog(UI,"Save Image As", FileDialog.SAVE);
			if (openDir != null)
				fd.setDirectory(openDir);
			fd.setVisible(true);
			fileName = new String(fd.getDirectory() + fd.getFile());
			if (fd.getFile() !=null)
				UI.snowPanel.saveImage(fileName,fileType);
		}
	}

	class menuItemOpenActionListener implements ActionListener
	{
		private String openDir = null;
		private String currFile = null;
		private int status = 0;

		public void actionPerformed(ActionEvent e)
		{
			FileDialog fd = new FileDialog(UI,"Select Image", FileDialog.LOAD);
			if (openDir != null)
				fd.setDirectory(openDir);
			fd.setVisible(true);

			openDir = fd.getDirectory();
			currFile = fd.getFile();
			fileName = new String (fd.getDirectory() + fd.getFile());

			if (fd.getFile() != null)
			{
				status = UI.snowPanel.decompressImage(fileName);
				
				if( status == 0 ) { 
					UI.menuWm.setEnabled(true);
					UI.menuItemInvert.setEnabled(false);
					UI.menuItemBlack.setEnabled(false);
					UI.menuItemGray.setEnabled(false);
					UI.menuItemAlpha.setEnabled(false);
					UI.menuEdit.setEnabled(true);
					UI.menuItemSave.setEnabled(true);
				}
				
			}
			else 
			{
				fileName = null;
			}
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

	class waterMarkGray implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			UI.snowPanel.setWatermarkMergeFlag(0);
        	UI.snowPanel.disableFitWidthHeight();
        	UI.snowPanel.activateWatermarkListener();
		}
		
	}
	
	class waterMarkBlack implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			UI.snowPanel.setWatermarkMergeFlag(1);
        	UI.snowPanel.disableFitWidthHeight();
        	UI.snowPanel.activateWatermarkListener();
		}
	}

	class waterMarkInvert implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			UI.snowPanel.setWatermarkMergeFlag(2);
        	UI.snowPanel.disableFitWidthHeight();
        	UI.snowPanel.activateWatermarkListener();
		}
	}
	
	class waterMarkAlpha implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{
			UI.snowPanel.setWatermarkMergeFlag(3);
        	UI.snowPanel.disableFitWidthHeight();
        	UI.snowPanel.activateWatermarkListener();
		}
	}
	
	/* Select a watermark and then draw on the image with the mouse to place
	 * the watermark.  Similar behavior to zoom rubber band.
	 */
  
	class waterMarkSelect implements ActionListener
	{
		public void actionPerformed( ActionEvent e )
		{
			FileDialog fd = new FileDialog(UI,"Select Watermark Image...", FileDialog.LOAD);
			if (openDir != null) {
				fd.setDirectory(openDir);
			}
			fd.setVisible(true);

			openDir = fd.getDirectory();
			String watermark = fd.getDirectory() + fd.getFile();
			
			UI.snowPanel.setWatermarkImagePath(watermark);
			UI.menuItemInvert.setEnabled(true);
			UI.menuItemBlack.setEnabled(true);
			UI.menuItemGray.setEnabled(true);
			UI.menuItemAlpha.setEnabled(true);
		}
	}
	
	class menuItemExitActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			UI.dispose();
			System.exit(0);
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
