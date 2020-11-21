/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.scanning;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.FileDialog;

import Snow.ErrorCodes;

import com.snowbound.samples.common.dialog.JPEGDialog;
import com.snowbound.samples.common.dialog.PathDialog;
import com.snowbound.samples.common.dialog.SaveDialog;

public class ScanController
{
	  private int showui = 0;
	  private int duplex = 0;
	  private String fileName = null;
	  private String openDir = null;
	  public ScanUI UI = null;

	  /* attach reference to the UI and wire up the menu listeners */
	  public ScanController(ScanUI lu)
	  {
		   UI = lu;
		
		   UI.menuItemExit.addActionListener(new menuItemExitActionListener());
		   UI.menuItemSave.addActionListener(new menuItemSaveActionListener());
		   UI.menuItemZoomIn.addActionListener(new zoomInActionListener());
		   UI.menuItemZoomOut.addActionListener(new zoomOutActionListener());
		   UI.menuItemSelectSource.addActionListener(new selectSourceActionListener());
		   UI.menuItemScanAcquire.addActionListener(new menuItemScanAcquireActionListener());
		   UI.menuItemScanFeeder.addActionListener(new scanFeederActionListener());
		   UI.menuItemShowUI.addItemListener(new menuItemShowUIListener());
		   UI.menuItemDuplex.addItemListener(new menuItemDuplexListener());
		   UI.menuItemScanPages.addActionListener(new scanPagesListener());
		   UI.addWindowListener(new UIWindowAdapter());
	  }
	  
	  /* all of these inner classes below are triggered from menu events */
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
			  if (fileType == 13)
			  {
				  JPEGDialog jd = new JPEGDialog(UI,"JPEG Parameters", true);
				  quality = jd.getQuality();
				  verticalInterleave = jd.getVerticalInterleave();
				  horizontalInterleave = jd.getHorizontalInterleave();

				  UI.snowPanel.setJPEGparameters(quality,horizontalInterleave,verticalInterleave);
			  }

			  FileDialog fd = new FileDialog(UI,"Save File", FileDialog.SAVE);
			  
			  if (openDir != null)
			  {
				  fd.setDirectory(openDir);
			  }
			  fd.setVisible(true);
			  fileName = new String(fd.getDirectory() + fd.getFile());
			  
			  if (fd.getFile() != null )
			  {
				  UI.snowPanel.saveImage(fileName,fileType);
			  }
		  }	
	  }

	  class scanPagesListener implements ActionListener
	  {
		  public void actionPerformed(ActionEvent e)
		  {
			  ScanInterface st = new ScanInterface();
			  int result = 0;
			  int fileType =0;
			  String path;

			  SaveDialog sd = new SaveDialog(UI, "Select Output Format", true);
			  fileType = sd.getFormat();

			  PathDialog pd = new PathDialog(UI, "Specify File Path:",true);
			  path = pd.getPath();
			  UI.repaint();
			  if (path != "")
			  {
				  //result = st.IMG_scan_pages(path, fileType, showui);
				  result = 1;
				  while (result > 0)
				  {
					  result = st.IMG_scan_acquire_feeder(showui);
					  
					  /* uncomment the following for duplex scanning mode */
					  //result = st.IMG_scan_acquire_feeder_fast(showui,duplex);
    
					  System.out.println("IMG_scan_acquire_feeder(): " + result);
					  try
					  {
						  if (result  >= 0)
						  {  			
							  byte[] di = st.IMG_get_scanned_image_bytes(result);
							  int status = UI.snowPanel.decompressImage(di);
							  UI.snowPanel.saveImage(path,fileType);    
							  st.IMG_delete_bitmap(result);
						  }
						  else{
							  System.out.println("Image scan failed: " + ErrorCodes.getErrorMessage(result));
						  }
					  }
					  catch(Exception ex){
						  ex.printStackTrace();
					  }
					  
				  }
			  }

			  st = null;
		  }
	  }
 
	  class menuItemDuplexListener implements ItemListener
	  {
		  public void itemStateChanged(ItemEvent e)
		  {
			  if ( UI.menuItemDuplex.getState() == true)
				  duplex = 1;
			  else
				  duplex = 0;
		  }
	  }

	  class menuItemShowUIListener implements ItemListener
	  {
		  public void itemStateChanged(ItemEvent e)
		  {
			  if ( UI.menuItemShowUI.getState() == true)
				  showui = 1;
			  else
				  showui = 0;
		  }
	  }

	  class scanFeederActionListener implements ActionListener
	  {
		  public void actionPerformed(ActionEvent e)
		  {
			  ScanInterface st = new ScanInterface();
			  int result = 1;

			  while (result > 0)
			  {
				  result = st.IMG_scan_acquire_feeder(showui);
				  
				  /* uncommment the following for duplex scanning mode */
				  //result = st.IMG_scan_acquire_feeder_fast(showui,duplex);
				  
				  System.out.println("IMG_scan_acquire_feeder(): "+ result);
				  try{
					  if (result  >= 0)
					  {
						  byte[] di = st.IMG_get_scanned_image_bytes(result);
						  int status = UI.snowPanel.decompressImage(di);
						  st.IMG_delete_bitmap(result);
						  
						  UI.menuItemSave.setEnabled(true);
					  }
					  else
					  {
						  System.out.println("Image scan failed : " + ErrorCodes.getErrorMessage(result));
					  }
				  }
				  catch(Exception ex)
				  {
					  ex.printStackTrace();
				  }
				  
			  }
			  
			  st.IMG_scan_feeder_close();
			  st = null;
		  }
	  }
	  
	  class selectSourceActionListener implements ActionListener
	  {
		  public void actionPerformed(ActionEvent e)
		  {
			  ScanInterface  st = new ScanInterface();

			  st.IMG_scan_open_source();
			  st = null;
		  }
	  }
	  
	  class menuItemScanAcquireActionListener implements ActionListener
	  {
		  public void actionPerformed(ActionEvent e)
		  {
			  int imageHandle = -1;

			  ScanInterface  st = new ScanInterface();
			  imageHandle = st.IMG_scan_acquire(showui);
			  
			  System.out.println("IMG_scan_aquire(): " + imageHandle);
			 try
			 {
				 if ( imageHandle >= 0)
				  {
					  byte[] di = st.IMG_get_scanned_image_bytes( imageHandle );
					  int status = UI.snowPanel.decompressImage(di);
					  st.IMG_delete_bitmap( imageHandle );
					  
					  UI.menuItemSave.setEnabled(true);
				  }
				  st = null;
			 }
			 catch(Exception ex)
			 {
				 ex.printStackTrace();
			 }		  
		  }
	  }

	  class zoomInActionListener implements ActionListener
	  {
		  public void actionPerformed(ActionEvent e)
		  {
			  UI.snowPanel.disableFitWidthHeight();
			  UI.snowPanel.zoomIn(20);
			  UI.snowPanel.repaint();
		  }
	  }

	  class zoomOutActionListener implements ActionListener
	  {
		  public void actionPerformed(ActionEvent e)
		  {
			  UI.snowPanel.disableFitWidthHeight();
			  UI.snowPanel.zoomOut(20);
			  UI.snowPanel.repaint();
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


