/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.thumbnails;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.ScrollPane;

public class ThumbnailsUI extends Frame
{
    private static final long serialVersionUID = 4351078790498564315L;
    private FlowLayout fl = new FlowLayout();
    private MenuBar mainMenuBar = null;
    private Menu menuFile = null;
    public MenuItem menuItemOpen = null;
    public MenuItem menuItemExit = null;
    private ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    public Panel p = new Panel();

    /**
     * Constructor.
     *
     * @param frameTitle The title of the window
     */
    public ThumbnailsUI(String frameTitle)
    {
        super(frameTitle);

        mainMenuBar = new MenuBar();
        menuFile = new Menu("File");
        menuItemOpen = new MenuItem("Open");
        menuItemExit = new MenuItem("Exit");
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemExit);
        mainMenuBar.add(menuFile);
        this.setMenuBar(mainMenuBar);
        ThumbnailsController ctrl = new ThumbnailsController(this);

        sp.add(p);
        fl.setAlignment(FlowLayout.LEFT);
        p.setLayout(fl);
        this.add(sp);

        // Initialize window
        this.setSize(500, 200);
        this.setVisible(true);
    }

}