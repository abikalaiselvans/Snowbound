/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.thumbnails;

import java.applet.Applet;
import java.awt.event.FocusListener;

/**
 * Applet version of the thumbnail sample
 */
public class ThumbnailsApplet extends Applet
{
    private static final long serialVersionUID = 4593499279705851851L;
    private static final String frameTitle = new String("Snowbound Software :: Thumbnails");

    boolean isStandalone = false;

    /**
     * Get a parameter value
     *
     * @param key the key to retrieve
     * @param def a default value
     *
     * @return the string value of the system property, or the default value if
     *         there is no property with that key.
     */
    public String getParameter(String key, String def)
    {
        if (isStandalone == true)
        {
            return System.getProperty(key, def);
        }
        else
        {
            if (getParameter(key) != null)
            {
                return getParameter(key);
            }
            else
            {
                return def;
            }
        }
    }

    /**
     * Construct the applet
     */
    public ThumbnailsApplet()
    {
    }

    /**
     * @see java.applet.Applet#init()
     */
    public void init()
    {
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Component initialization
     *
     * @throws Exception
     */
    private void jbInit() throws Exception
    {
        ThumbnailsUI UI = new ThumbnailsUI(frameTitle);
    }

    /**
     * @see java.applet.Applet#start()
     */
    public void start()
    {
    }

    /**
     * @see java.applet.Applet#stop()
     */
    public void stop()
    {
    }

    /**
     * @see java.applet.Applet#destroy()
     */
    public void destroy()
    {
    	
    }

    /**
     * @see java.applet.Applet#getAppletInfo()
     */
    public String getAppletInfo()
    {
        return "Applet Information";
    }

    /**
     * @see java.applet.Applet#getParameterInfo()
     */
    public String[][] getParameterInfo()
    {
        return null;
    }
}
