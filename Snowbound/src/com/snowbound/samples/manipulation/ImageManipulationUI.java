/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.manipulation;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class ImageManipulationUI extends Frame {
    private static final long serialVersionUID = 6624932439411717727L;
    private MenuBar mainMenuBar = null;
    private Menu menuFile = null;
    private Menu menuEdit = null;
    private Menu menuZoom = null;
    public MenuItem menuItemOpen = null;
    public MenuItem menuItemExit = null;
    public MenuItem menuItemSave = null;
    public MenuItem menuItemZoomIn = null;
    public MenuItem menuItemZoomOut = null;
    public MenuItem menuItemMagBox = null;
    public MenuItem menuItemZoomBox = null;
    public MenuItem menuItemFitWidth = null;
    public MenuItem menuItemFitHeight = null;
    public MenuItem menuItemDeskew = null;
    public MenuItem menuItemDespeckle = null;
    public MenuItem menuItemRotate = null;
    public MenuItem menuItemColorGray = null;
    public MenuItem menuItemContrast = null;
    public MenuItem menuItemBrightness = null;
    public MenuItem menuItemFlipX = null;
    public MenuItem menuItemFlipY = null;
    public MenuItem menuItemInvert = null;


    public SnowboundPanelWithAnnotations snowPanel = null;

    public ImageManipulationUI(SnowboundPanelWithAnnotations snowp, String frameTitle)
    {
        snowPanel = snowp;

        /* initialize menu bar and menu items */
        mainMenuBar = new MenuBar();
        menuFile = new Menu("File");
        menuEdit = new Menu("Edit");
        menuZoom = new Menu("Zoom");
        menuItemOpen = new MenuItem("Open...");
        menuItemSave = new MenuItem("Save...");
        menuItemSave.setEnabled(false);
        menuItemExit = new MenuItem("Exit");
        menuItemZoomIn = new MenuItem("Zoom In");
        menuItemZoomOut = new MenuItem("Zoom Out");
        menuItemMagBox = new MenuItem("Magnifier Box");
        menuItemZoomBox = new MenuItem("Zoom Selection");
        menuItemFitHeight = new MenuItem("Fit To Height");
        menuItemFitWidth = new MenuItem("Fit To Width");
        menuItemDeskew = new MenuItem("Deskew");
        menuItemDespeckle = new MenuItem("Despeckle");
        menuItemRotate = new MenuItem("Rotate...");
        menuItemColorGray = new MenuItem("Color Gray");
        menuItemContrast = new MenuItem("Contrast...");
        menuItemBrightness = new MenuItem("Brightness...");
        menuItemFlipX = new MenuItem("Flip X Axis");
        menuItemFlipY = new MenuItem("Flip Y Axis");
        menuItemInvert = new MenuItem("Invert");

        /* add menu items to the menus */
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemSave);
        menuFile.add(menuItemExit);
        menuEdit.add(menuItemDeskew);
        menuEdit.add(menuItemDespeckle);
        menuEdit.add(menuItemRotate);
        menuEdit.add(menuItemBrightness);
        menuEdit.add(menuItemContrast);
        menuEdit.add(menuItemColorGray);
        menuEdit.add(menuItemFlipX);
        menuEdit.add(menuItemFlipY);
        menuEdit.add(menuItemInvert);

        menuZoom.add(menuItemZoomIn);
        menuZoom.add(menuItemZoomOut);
        menuZoom.add(menuItemZoomBox);
        menuZoom.add(menuItemMagBox);
        menuZoom.add(menuItemFitHeight);
        menuZoom.add(menuItemFitWidth);
        mainMenuBar.add(menuFile);
        mainMenuBar.add(menuEdit);
        mainMenuBar.add(menuZoom);

        /* add menu bar to frame */
        this.setMenuBar(mainMenuBar);
        this.add(snowPanel);

        ImageManipulationController ctrl = new ImageManipulationController(this);

        // Initialize window
        this.setTitle(frameTitle);
        this.setSize(800, 600);
        this.setVisible(true);
    }

}
