/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.thumbnails;

import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ThumbnailsController
{
    protected GridBagConstraints cons = new GridBagConstraints();
    private String fileName = null;
    protected ThumbnailsUI UI = null;

    /**
     * Constructor.
     *
     * @param tu Reference to the view
     */
    public ThumbnailsController(ThumbnailsUI tu)
    {
        UI = tu;
        UI.addWindowListener(new UIWindowAdapter());
        UI.menuItemExit.addActionListener(new menuItemExitActionListener());
        UI.menuItemOpen.addActionListener(new menuItemOpenActionListener());
    }

    /**
     * Handles windowClosing events
     */
    class UIWindowAdapter extends WindowAdapter
    {
        /**
         * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
         */
        public void windowClosing(WindowEvent e)
        {
            UI.dispose();
            System.exit(0);
        }
    }

    /**
     * Handles the "Exit" menu item
     */
    class menuItemExitActionListener
        implements ActionListener
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            UI.dispose();
            System.exit(0);
        }
    }

    /**
     * Handles the "Open" menu item
     */
    class menuItemOpenActionListener
        implements ActionListener
    {
        private String openDir = null;
        private Thumbnail th = null;

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            FileDialog fd = new FileDialog(UI, "Open a File", FileDialog.LOAD);

            if (openDir != null)
            {
                fd.setDirectory(openDir);
            }

            fd.setVisible(true);

            openDir = fd.getDirectory();
            fileName = new String(fd.getDirectory() + fd.getFile());

            if (fd.getFile() != null)
            {
                th = new Thumbnail(fileName);
                th.addMouseListener(new thumbMouseAdapter());

                UI.p.add(th);
                UI.validate();
            }
            else
            {
                fileName = null;
            }
        }
    }

    /**
     * Handles MouseEvents
     */
    class thumbMouseAdapter extends MouseAdapter
    {
        private ThumbFrame tf = null;
        private Thumbnail th1 = null;

        /**
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e)
        {
            // We know that the Source of the event will always be a thumbnail
            // object we cast the Object returned by getSource to a thumbnail
            // so that we can retrieve the image name for hires display.
            th1 = (Thumbnail) e.getSource();
            tf = new ThumbFrame(th1.getFileName());
            UI.dispose();
        }
    }
}

