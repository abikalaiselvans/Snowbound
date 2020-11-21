/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.watermark;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class WaterMarkUI extends Frame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5073768249968134015L;
	private MenuBar mainMenuBar = null;
	public Menu menuFile = null;
	public Menu menuEdit = null;
	public Menu menuWm = null;
	public MenuItem menuItemOpen = null;
	public MenuItem menuItemExit = null;
	public MenuItem menuItemSave = null;
	public MenuItem menuItemZoomIn = null;
	public MenuItem menuItemZoomOut = null;
	public MenuItem menuItemBlack = null;
	public MenuItem menuItemInvert = null;
	public MenuItem menuItemGray = null;
	public MenuItem menuItemAlpha = null;
	public MenuItem menuItemSelectWatermark = null;
	public SnowboundPanelWithAnnotations snowPanel = null;

	public WaterMarkUI(SnowboundPanelWithAnnotations snowPanel, String frameTitle)
	{
		this.snowPanel = snowPanel;

		mainMenuBar = new MenuBar();
		menuFile = new Menu("File");
		menuEdit = new Menu("Edit");
		menuWm = new Menu("Watermark");
		menuItemOpen = new MenuItem("Open...");
		menuItemSave = new MenuItem("Save...");
		menuItemExit = new MenuItem("Exit");
		menuItemZoomIn = new MenuItem("Zoom In");
		menuItemZoomOut = new MenuItem("Zoom Out");
		menuItemSelectWatermark = new MenuItem("Select Watermark...");
		menuItemBlack = new MenuItem("Watermark Black");
		menuItemInvert = new MenuItem("Watermark Invert");
		menuItemGray = new MenuItem("Watermark Gray");
		menuItemAlpha = new MenuItem("Watermark Alpha");
   
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemSave);
		menuFile.add(menuItemExit);
		menuEdit.add(menuItemZoomIn);
		menuEdit.add(menuItemZoomOut);
		menuWm.add(menuItemSelectWatermark);
		menuWm.addSeparator();
		menuWm.add(menuItemInvert);
		menuWm.add(menuItemBlack);
		menuWm.add(menuItemGray);
		menuWm.add(menuItemAlpha);
   
		menuWm.setEnabled(false);
		menuItemSave.setEnabled(false);
		menuEdit.setEnabled(false);

		mainMenuBar.add(menuFile);
		mainMenuBar.add(menuEdit);
		mainMenuBar.add(menuWm);

		this.setMenuBar(mainMenuBar);
		this.add(snowPanel);

		WaterMarkController ctrl = new WaterMarkController(this);

		this.setTitle(frameTitle);
		this.setSize(800,600);
		this.setVisible(true);
	}
}
