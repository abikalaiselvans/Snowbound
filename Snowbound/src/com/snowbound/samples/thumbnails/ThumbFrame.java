/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.thumbnails;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import Snow.MessageBox;
import Snow.Snowbnd;

/**
 * Main application window
 */
public class ThumbFrame extends Frame
{
    private static final long serialVersionUID = -4666043198839539943L;
    private static final String DecompressErrorMessage = new String("Error decompressing Image");
    private String fileName = null;
    private Snowbnd Simage = null;
    private Insets in = null;
    private Dimension d = null;
    private int stat = 0;

    /**
     * Constructor.
     *
     * @param file name of file to open
     */
    public ThumbFrame(String file)
    {
        super(file);
        fileName = file;
        Simage = new Snowbnd();
        decompressImage(fileName);
        this.setSize(Simage.getWidth(), Simage.getHeight());
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            public void windowClosing(WindowEvent e)
            {
            	System.out.println("closing big windows");
                fileName = null;
                Simage = null;
            }
        });
    }

    /**
     * Open and decompress the specified file
     *
     * @param fileName file to decompress
     */
    private void decompressImage(String fileName)
    {
        //System.out.println(fileName);
        stat = Simage.IMG_decompress_bitmap(fileName, 0);

        if (stat < 0)
        {
            // Snowbound's message box routine
            new MessageBox(null, DecompressErrorMessage, true);
        }
        repaint();
    }

    /**
     * @see java.awt.Container#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
        if (Simage != null)
        {
            setDimensionsWidthHeight();

            g.translate(in.left, in.top);

            Simage.IMG_display_bitmap(g, 0, 0, d.width, d.height);
        }

    }

    /**
     * @see java.awt.Container#update(java.awt.Graphics)
     */
    public void update(Graphics g)
    {
        if (Simage == null)
        {
            setDimensionsWidthHeight();

            g.setColor(getBackground());
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(getForeground());
        }
        paint(g);
    }

    /**
     * Reduces the with and height of the component by the insets.
     */
    private void setDimensionsWidthHeight()
    {
        d = getSize();
        in = getInsets();
        d.width -= (in.right + in.left);
        d.height -= (in.top + in.bottom);
    }

}
