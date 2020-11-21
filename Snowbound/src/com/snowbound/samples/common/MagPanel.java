/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import Snow.Snowbnd;

/**
 * This is the Magnifier Panel Component class.
 */
public class MagPanel extends Component
{
    private static final long serialVersionUID = -576285103477480986L;
    private final static long MAX_PIXEL_BUFFER = 7000000;
    private Snowbnd Simage = null;
    private AbstractImagePanel parent;
    private Image paintImage;
    private int cx, cy;
    private int crop_xe, crop_ye;
    private int ecx, ecy;
    private int xs_off;
    private int bimg_xs;
    private int crop_xs, crop_ys;
    private int mag_size;
    private int paintWidth, paintHeight;
    private int actMagWidth;
    private int actMagHeight;
    private int magDispSize = 12;     // adjustment ratio to keep mag zoom the
                                      // same across any size display/crop


    /**
     * Constructor.
     *
     * @param parent Parent panel
     * @param size pixel size of zoom panel
     */
    public MagPanel(AbstractImagePanel parent, int size)
    {
        this.parent = parent;
        mag_size = size;
        setSize(mag_size, mag_size);
    }

    public MagPanel(int psize, Snowbnd Si, AbstractImagePanel sbp)
    {
        mag_size = psize;
        if (Si.getBitsPerPixel() == 1)
        {
            setBackground(new Color(0xff, 0xff, 0xff));
        }
        else
        {
            setBackground(new Color(0, 0, 0));
        }
        setSize(psize, psize);
        Simage = Si;

        parent = sbp;

        crop_xe = (100 * Si.dis_crop_xe) / (Si.dis_xsize * 4);
        crop_ye = crop_xe;
    }

    /**
     * Reinitialize object
     *
     * @throws Exception
     */
    public void reinit() throws Exception
    {
        Snowbnd Si = parent.getImage();
        if (Si == null)
        {
            return;
        }
        // this.setSize(mag_size, mag_size);
        try
        {
            if (Si.getWidth() * Si.getHeight() > MAX_PIXEL_BUFFER)
            {
                System.out
                    .println("Too many pixels in original for magnification.");
                throw new Exception("Image too large to buffer.");
            }
            Simage = new Snowbnd(Si);
            paintImage = parent.createImage(Simage.getWidth(), Simage
                .getHeight());
        }
        catch (java.lang.OutOfMemoryError oom)
        {
            System.out
                .println("Byte size of image is too large for magnification.");
            throw new Exception("Image too large to buffer.");
        }
        if (Simage.getBitsPerPixel() == 1)
        {
            setBackground(new Color(0xff, 0xff, 0xff));
        }
        else
        {
            setBackground(new Color(0, 0, 0));
        }
        Simage.fit_to_height(0);
        Simage.fit_to_width(0);
        paintWidth = paintImage.getWidth(this);
        paintHeight = paintImage.getHeight(this);
        Graphics g = paintImage.getGraphics();
        Simage.set_croprect(0, 0, Simage.getWidth(), Simage.getHeight());
        Simage.IMG_display_bitmap(g, 0, 0, Simage.getWidth(), Simage
            .getHeight());
        int width = getSize().width;
        int height = getSize().height;
        actMagWidth = (int) ((magDispSize / parent.getZoomRatio()) * ((double) width / 100));
        actMagHeight = (int) ((magDispSize / parent.getZoomRatio()) * ((double) height / 100));
        g.dispose();
    }

    /**
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    public void setBounds(int a, int b, int c, int d)
    {
        // this is a mild hack to prevent auto-resizing of the mag
        Rectangle currentBounds = getBounds();
        int currentX = currentBounds.x;
        if ((a == 0 && b == 0) && currentX != 0)
        {
            return;
        }
        super.setBounds(a, b, c, d);
    }

    /**
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
        int width = getSize().width;
        int height = getSize().height;
        g.drawImage(paintImage, 0, 0, width, height, cx, cy, ecx, ecy, this);
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
    }

    /**
     *
     */
    public void resetSize()
    {
        int width = getSize().width;
        int height = getSize().height;
        actMagWidth = (int) ((magDispSize / parent.getZoomRatio()) * ((double) width / 100));
        actMagHeight = (int) ((magDispSize / parent.getZoomRatio()) * ((double) height / 100));
        repaintMag();
    }

    /**
     *
     */
    private void repaintMag()
    {
        cx = crop_xs - actMagWidth;
        cy = crop_ys - actMagHeight;
        ecx = crop_xs + actMagWidth;
        ecy = crop_ys + actMagHeight;
        if (cx < 0)
        {
            cx = 0;
            ecx = 2 * actMagWidth;
        }
        if (cy < 0)
        {
            cy = 0;
            ecy = 2 * actMagHeight;
        }
        if (ecx >= paintWidth)
        {
            ecx = paintWidth;
            cx = ecx - (2 * actMagWidth);
        }
        if (ecy >= paintHeight)
        {
            ecy = paintHeight;
            cy = ecy - (2 * actMagHeight);
        }
        repaint();
    }

    /**
     * @param xs
     * @param ys
     * @param imagexs
     * @param imageys
     */
    public void move_mag(int xs, int ys, int imagexs, int imageys)
    {
        int xdiff = bimg_xs - xs;
        int ydiff = ys;
        if (xdiff < 0)
        {
            xdiff *= -1;
        }
        if (ydiff < 0)
        {
            ydiff *= -1;
        }
        if (xdiff + ydiff < 3)
        {
            return;
        }
        bimg_xs = xs - xs_off;
        crop_xs = imagexs;
        crop_ys = imageys;
        cx = crop_xs - actMagWidth;
        cy = crop_ys - actMagHeight;
        ecx = crop_xs + actMagWidth;
        ecy = crop_ys + actMagHeight;
        if (cx < 0)
        {
            cx = 0;
            ecx = 2 * actMagWidth;
        }
        if (cy < 0)
        {
            cy = 0;
            ecy = 2 * actMagHeight;
        }
        if (ecx >= paintWidth)
        {
            ecx = paintWidth;
            cx = ecx - (2 * actMagWidth);
        }
        if (ecy >= paintHeight)
        {
            ecy = paintHeight;
            cy = ecy - (2 * actMagHeight);
        }

        setLocation(xs, ys);
        repaint();
    }
}