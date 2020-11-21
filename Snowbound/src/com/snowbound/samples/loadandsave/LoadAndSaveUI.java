/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.loadandsave;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class LoadAndSaveUI extends Frame
{
    private static final long serialVersionUID = 2706858754155157049L;
    private MenuBar mainMenuBar = null;
    private Menu menuFile = null;
    private Menu menuManip = null;
    public MenuItem menuItemOpen = null;
    public MenuItem menuItemExit = null;
    public MenuItem menuItemSave = null;
    public MenuItem menuItemZoomIn = null;
    public MenuItem menuItemZoomOut = null;
    public SnowboundPanelWithAnnotations snowPanel = null;

    public LoadAndSaveUI(SnowboundPanelWithAnnotations snowp, String frameTitle)
    {
        snowPanel = snowp;

        // Add Menu Components to the Frame
        mainMenuBar = new MenuBar();
        menuFile = new Menu("File");
        menuManip = new Menu("Manipulate");
        menuItemOpen = new MenuItem("Open");
        menuItemSave = new MenuItem("Save");
        menuItemExit = new MenuItem("Exit");
        menuItemZoomIn = new MenuItem("Zoom In");
        menuItemZoomOut = new MenuItem("Zoom Out");
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemSave);
        menuFile.add(menuItemExit);
        menuManip.add(menuItemZoomIn);
        menuManip.add(menuItemZoomOut);
        mainMenuBar.add(menuFile);
        mainMenuBar.add(menuManip);
        // End of Menu Components

        // Place components on the window
        this.setMenuBar(mainMenuBar);
        this.add(snowPanel);

        LoadAndSaveController ctrl = new LoadAndSaveController(this);

        // Initialize window
        this.setTitle(frameTitle);
        this.setSize(800, 600);
        this.setVisible(true);
    }

}


