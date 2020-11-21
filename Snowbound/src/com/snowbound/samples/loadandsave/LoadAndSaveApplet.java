/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.loadandsave;

import java.applet.Applet;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class LoadAndSaveApplet extends Applet
{
    private static final long serialVersionUID = -1002679961484449876L;
    private static final String frameTitle = new String("Snowbound Software :: Load And Save");

    boolean isStandalone = false;

    public String getParameter(String key, String def)
    {
        if (isStandalone == true)
        {
            return System.getProperty(key, def);
        }
        else
        {
            if(getParameter(key) != null) {
                return getParameter(key);
            }
            else {
                return def;
            }
        }
    }

    // Construct the applet
    public LoadAndSaveApplet()
    {
    }

    // Initialize the applet
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

    // Component initialization
    private void jbInit() throws Exception
    {
        SnowboundPanelWithAnnotations SPanel = new SnowboundPanelWithAnnotations();
        LoadAndSaveUI UI = new LoadAndSaveUI(SPanel, frameTitle);
    }

    // Start the applet
    public void start()
    {
    }

    // Stop the applet
    public void stop()
    {
    }

    // Destroy the applet
    public void destroy()
    {
    }

    // Get Applet information
    public String getAppletInfo()
    {
        return "Applet Information";
    }

    // Get parameter info
    public String[][] getParameterInfo()
    {
        return null;
    }
}
