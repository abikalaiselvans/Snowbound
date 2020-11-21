/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class WatermarkListener implements MouseListener, MouseMotionListener
{
    Rectangle selrect;
    AbstractImagePanel imagePanel;

    public WatermarkListener(AbstractImagePanel panel)
    {
        imagePanel = panel;
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mouseMoved(MouseEvent arg0) {
    }

    /**
     * This creates a start point for the Rectangle.
     */
    public void mousePressed(final MouseEvent e)
    {
        imagePanel.requestFocus();
        selrect = new Rectangle();
        selrect.x = e.getX();
        selrect.y = e.getY();
        selrect.width = 5;
        selrect.height = 5;
        snbd_draw_pen();
    }

    /**
     * This defines the Rectangle according to where the user drags the mouse.
     */
    public void mouseDragged(MouseEvent e)
    {
        if (e.getX() > selrect.x && e.getY() > selrect.y)
        {
            snbd_draw_pen();
            selrect.width = e.getX() - selrect.x;
            selrect.height = e.getY() - selrect.y;
            snbd_draw_pen();
        }
    }

    /**
     * The user has defined The rectangle. The postCommand method associated
     * with our Zoom_Rubber_Band Menu will be executed.
     */
    public void mouseReleased(final MouseEvent e)
    {
        snbd_draw_pen();
        imagePanel.watermarkMerge(selrect);
        return;
    }

    /**
     * This performs the graphics calls to show the outline of the Rectangle.
     */
    private void snbd_draw_pen()
    {
        Graphics g;
        int xsize, ysize;
        Insets in;
        Dimension d;
        if (selrect == null)
        {
            return;
        }
        g = imagePanel.getGraphics();
        in = imagePanel.getInsets();
        d = imagePanel.getSize();
        d.width -= (in.right + in.left);
        d.height -= (in.top + in.bottom);
        g.setXORMode(Color.white);
        xsize = selrect.width;
        ysize = selrect.height;
        g.drawRect(selrect.x, selrect.y, xsize, ysize);
        g.setPaintMode();
        g.dispose();
    }
}
