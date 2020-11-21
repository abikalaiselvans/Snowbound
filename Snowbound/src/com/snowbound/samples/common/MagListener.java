/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import Snow.Snowbnd;

/**
 * This class is a MouseListener and MouseMotionListener that handles events to
 * implement the Magnifier
 */
public class MagListener
    implements MouseListener, MouseMotionListener
{
    private AbstractImagePanel imagePanel;
    private Snowbnd snowImage;
    private MagPanel magPanel;
    protected boolean active = false;

    /**
     * @param mainApp
     */
    protected MagListener(AbstractImagePanel imagePanel)
    {
        this.imagePanel = imagePanel;
    }

    /**
     * @see com.snowbound.snapplet.applets.listeners.imagepanel.ListenerInterface#update()
     */
    public void update()
    {
        snowImage = imagePanel.getImage();
        magPanel = imagePanel.getMagPanel();
        active = false;
        imagePanel.setCursor(Cursor
            .getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     *
     */
    public void start()
    {
        /**
         * When the magnifier is activated, simulate a mouse click to get the
         * magnigier on the screen.
         */
        Rectangle ipBounds = imagePanel.getBounds();
        int centerX = ipBounds.x + ipBounds.width / 2;
        int centerY = ipBounds.y + ipBounds.height / 2;
        mousePressed(new MouseEvent(imagePanel, // the Component that originated
                                    // the event
                                    MouseEvent.MOUSE_PRESSED, // the integer
                                    // that identifies
                                    // the event
                                    System.currentTimeMillis(), // the time the
                                    // event
                                    // occurred
                                    InputEvent.BUTTON1_MASK, // the modifier
                                    // keys
                                    centerX, // x coordinate
                                    centerY, // y coordinate
                                    1, // number of clicks
                                    false)); // boolean for pop-up (false)
    }

    /**
     * @see com.snowbound.snapplet.applets.listeners.imagepanel.ListenerInterface#done()
     */
    public void done()
    {
        imagePanel.remove(magPanel);
        active = false;
        imagePanel.repaint();
        imagePanel.setBackImageActive(false);
        imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e)
    {
        if (magPanel != null)
        {
            Point pt = new Point(e.getX(), e.getY());
            Point near = new Point(0, 0);
            Point far = new Point(snowImage.getWidth(), snowImage.getHeight());
            snowImage.map_image_to_wnd(imagePanel, near);
            snowImage.map_image_to_wnd(imagePanel, far);
            Point new_evt = new Point(e.getX(), e.getY());

            if (new_evt.x < near.x) {
                new_evt.x = near.x;
            }

            if (new_evt.x > far.x) {
                new_evt.x = far.x;
            }

            if (new_evt.y < near.y) {
                new_evt.y = near.y;
            }

            if (new_evt.y > far.y) {
                new_evt.y = far.y;
            }

            snowImage.map_wnd_to_image(imagePanel, pt);
            int magXpos = new_evt.x - (magPanel.getSize().width / 2);
            int magYpos = new_evt.y - (magPanel.getSize().height / 2);
            // magpanel.move_mag(magXpos, magYpos, pt.x, pt.y);
            magPanel.move_mag(magXpos, magYpos, pt.x, pt.y);
            return;
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(final MouseEvent e)
    {
        imagePanel.repaint();
        imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(final MouseEvent e)
    {
        if (magPanel != null)
        {
            active = true;
            Point near = new Point(0, 0);
            Point far = new Point(snowImage.getWidth(), snowImage.getHeight());
            snowImage.map_image_to_wnd(imagePanel, near);
            snowImage.map_image_to_wnd(imagePanel, far);
            Point new_evt = new Point(e.getX(), e.getY());

            if (new_evt.x < near.x) {
                new_evt.x = near.x;
            }

            if (new_evt.x > far.x) {
                new_evt.x = far.x;
            }

            if (new_evt.y < near.y) {
                new_evt.y = near.y;
            }

            if (new_evt.y > far.y) {
                new_evt.y = far.y;
            }

            imagePanel.setCursor(Cursor
                .getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            Point pt = new Point();
            pt.x = new_evt.x;
            pt.y = new_evt.y;
            snowImage.map_wnd_to_image(imagePanel, pt);
            int magXpos = new_evt.x - (magPanel.getSize().width / 2);
            int magYpos = new_evt.y - (magPanel.getSize().height / 2);
            magPanel.move_mag(magXpos, magYpos, pt.x, pt.y);
            try
            {
                magPanel.reinit();
            }
            catch (Exception me)
            {

            }
            imagePanel.add(magPanel);
            imagePanel.drawBackImage();
            imagePanel.setBackImageActive(true);
            mouseDragged(e);
            magPanel.setVisible(true);
            return;
        }
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e)
    {
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
        imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
        if (!active) {
            imagePanel.setCursor(Cursor
                .getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
    }
}