/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.thumbnails;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;

import Snow.MessageBox;
import Snow.Snowbnd;

/**
 * This class...
 */
public class Thumbnail extends Panel
{
    private static final long serialVersionUID = 8748394101461570648L;
    private static final String DecompressErrorMessage = new String("Error decompressing Image");
    private Label imageName = new Label();
    private Snowbnd Simage = null;
    private Insets in = null;
    private Dimension d = null;
    private int stat = 0;
    private int horiz;
    private int vert;

    /**
     * The preferred Size Method needs to be overriden it was causing
     * a problem with component sizing using FlowLayout.
     *
     * @see java.awt.Container#preferredSize()
     */
    public Dimension preferredSize()
    {
        setDimensionsWidthHeight();
        return d;
    }

    /**
     * @see java.awt.Container#getMinimumSize()
     */
    public Dimension getMinimumSize()
    {
        setDimensionsWidthHeight();
        return d;
    }

    /**
     * Returns the filename of the current image
     *
     * @return filename
     */
    public String getFileName()
    {
        return imageName.getText();
    }

    /**
     * Constructor.
     *
     * @param fileName filename of image to open
     */
    public Thumbnail(String fileName)
    {
    	
        imageName.setText(fileName);
        Simage = new Snowbnd();
        try
        {
        	stat = Simage.IMG_decompress_bitmap(fileName, 0);
            if (stat < 0)
            {
                // Snowbound's message box routine
                new MessageBox(null, DecompressErrorMessage, true);
                System.out.println(" Decompressing failed");
            }
            else {
                makeThumbnail();
            }
            repaint();
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        
       

       
    }

    /**
     * Reduces the with and height of the component by the insets.
     */
    private void setDimensionsWidthHeight()
    {
        d = getSize();
        in = getInsets();
        d.width = horiz;
        d.height = vert;
    }

    /**
     * @see java.awt.Container#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
        if (Simage != null)
        {
            setDimensionsWidthHeight();
            Simage.setFrame(this);
            g.translate(in.left, in.top);
            stat = Simage.IMG_display_bitmap(g, 0, 0, d.width, d.height);
        }
    }

    /**
     * @see java.awt.Container#update(java.awt.Graphics)
     */
    public void update(Graphics g)
    {
        setDimensionsWidthHeight();

        g.setColor(getBackground());
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(getForeground());

        paint(g);
    }

    /**
     * Create a thumbnail from the current image
     */
    private void makeThumbnail()
    {
        int bits = Simage.getBitsPerPixel();
        horiz = 120;

        vert = (int) (((double) horiz / (double) Simage.getWidth()) * (double) Simage
            .getHeight());

        if (bits == 1) {
            Simage.IMG_resize_to_gray(horiz, vert);
        }
        else {
            Simage.IMG_resize_bitmap_bicubic(horiz, vert);
        }

        this.setSize(horiz, vert);
        
    }

    /**
     * Sets the current image name
     *
     * @param name the new image name
     */
    public void setImageName(String name)
    {
        imageName.setText(name);
    }
}

