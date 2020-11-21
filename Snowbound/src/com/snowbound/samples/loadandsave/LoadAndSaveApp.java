/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.loadandsave;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class LoadAndSaveApp
{
    private static final String frameTitle = new String("Snowbound Software :: Load And Save");

    public LoadAndSaveApp()
    {
        SnowboundPanelWithAnnotations SPanel = new SnowboundPanelWithAnnotations();
        LoadAndSaveUI UI = new LoadAndSaveUI(SPanel, frameTitle);
    }

    public static void main(String[] args)
    {
        LoadAndSaveApp loadAndSaveApp1 = new LoadAndSaveApp();
    }
}
