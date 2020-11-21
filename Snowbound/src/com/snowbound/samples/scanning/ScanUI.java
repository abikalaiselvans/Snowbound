/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.scanning;

import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class ScanUI extends Frame
{
	  private MenuBar mainMenuBar = null;
	  private Menu menuFile = null;
	  private Menu menuManip = null;
	  private Menu menuScan = null;
	  public MenuItem menuItemSave = null;
	  public MenuItem menuItemExit = null;
	  public MenuItem menuItemZoomIn = null;
	  public MenuItem menuItemZoomOut = null;
	  public MenuItem menuItemScanAcquire = null;
	  public MenuItem menuItemSelectSource = null;
	  public MenuItem menuItemScanFeeder = null;
	  public MenuItem menuItemScanPages = null;
	  public CheckboxMenuItem menuItemShowUI = null;
	  public CheckboxMenuItem menuItemDuplex = null;
	  public ScanController ctrl = null;
	  public SnowboundPanelWithAnnotations snowPanel = null;

	  /* set up the basic ui listener and create the controller */
	  public ScanUI(SnowboundPanelWithAnnotations snowp, String frameTitle)
	  {
		  snowPanel = snowp;
		  mainMenuBar = new MenuBar();
	
		  menuFile = new Menu("File");
		  menuManip = new Menu("Edit");
		  menuScan = new Menu("Scan");
		  menuItemSave = new MenuItem("Save...");
		  menuItemExit = new MenuItem("Exit");
		  menuItemZoomIn = new MenuItem("Zoom In");
		  menuItemZoomOut = new MenuItem("Zoom Out");
		  menuItemScanAcquire = new MenuItem("Acquire Image...");
		  menuItemSelectSource = new MenuItem("Select Scanner Source...");
		  menuItemScanFeeder = new MenuItem("Acquire From Feeder...");
		  menuItemShowUI = new CheckboxMenuItem("Show Scanner UI");
		  menuItemDuplex = new CheckboxMenuItem("Duplex Scanning Mode");
		  menuItemScanPages = new MenuItem("Scan Pages to File");
		  menuFile.add(menuItemSave);
		  menuFile.add(menuItemExit);
		  menuManip.add(menuItemZoomIn);
		  menuManip.add(menuItemZoomOut);
		  menuScan.add(menuItemShowUI);
		  menuScan.add(menuItemDuplex);
		  menuScan.add(menuItemSelectSource);
		  menuScan.add(menuItemScanAcquire);
		  menuScan.add(menuItemScanFeeder);
		  menuScan.add(menuItemScanPages);
		  mainMenuBar.add(menuFile);
		  mainMenuBar.add(menuManip);
		  mainMenuBar.add(menuScan);
		  
		  menuItemSave.setEnabled(false);
		  this.setMenuBar(mainMenuBar);
		  this.add(snowPanel);
		  this.setTitle(frameTitle);
		  this.setSize(800, 600);

		  ctrl = new ScanController(this);

		  /* position the window in center of screen */
		  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		  int windowX = Math.max(0, (screenSize.width - this.getWidth()) / 2);
		  int windowY = Math.max(0, (screenSize.height - this.getHeight()) / 2);

		  this.setLocation(windowX, windowY);
		  this.show();
	  }
}