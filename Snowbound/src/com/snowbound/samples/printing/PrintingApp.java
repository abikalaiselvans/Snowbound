/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.printing;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class PrintingApp
{
    private static final String frameTitle = new String("Snowbound Software :: Printing");

    public PrintingApp()
    {
        SnowboundPanelWithAnnotations SPanel = new SnowboundPanelWithAnnotations();
        PrintingUI UI = new PrintingUI(SPanel, frameTitle);
    }

    public static void main(String[] args)
    {
        PrintingApp printingApp1 = new PrintingApp();
    }
}
