/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.printing;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class PrintingUI extends Frame
{
    private static final long serialVersionUID = -3236659147852714184L;
    private MenuBar mainMenuBar = null;
    private Menu menuFile = null;
    private Menu menuManip = null;

    public MenuItem menuItemOpen = null;
    public MenuItem menuItemPrint = null;
    public MenuItem menuItemPrintPS = null;
    public MenuItem menuItemExit = null;
    public MenuItem menuItemSave = null;
    public MenuItem menuItemZoomIn = null;
    public MenuItem menuItemZoomOut = null;
    public SnowboundPanelWithAnnotations snowPanel = null;

    public PrintingUI(SnowboundPanelWithAnnotations snowp, String frameTitle)
    {
        snowPanel = snowp;

        // Add Menu Components to the Frame
        mainMenuBar = new MenuBar();
        menuFile = new Menu("File");
        menuManip = new Menu("Manipulate");
        menuItemOpen = new MenuItem("Open");
        menuItemSave = new MenuItem("Save");
        menuItemPrint = new MenuItem("Print");
        menuItemPrintPS = new MenuItem("Print PS/PCL");
        menuItemExit = new MenuItem("Exit");
        menuItemZoomIn = new MenuItem("Zoom In");
        menuItemZoomOut = new MenuItem("Zoom Out");
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemSave);
        menuFile.add(menuItemPrint);
        menuFile.add(menuItemPrintPS);
        menuFile.add(menuItemExit);
        menuManip.add(menuItemZoomIn);
        menuManip.add(menuItemZoomOut);
        mainMenuBar.add(menuFile);
        mainMenuBar.add(menuManip);
        // End of Menu Components

        // Place components on the window
        this.setMenuBar(mainMenuBar);
        this.add(snowPanel);

        PrintingController ctrl = new PrintingController(this);

        // Initialize window
        this.setTitle(frameTitle);
        this.setSize(800, 600);
        this.setVisible(true);
    } // end LoadUI constructor
} // end LoadUI Class


