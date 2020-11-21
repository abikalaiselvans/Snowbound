/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.manipulation;

import com.snowbound.samples.common.*;

public class ImageManipulationApp {

    private static final String frameTitle = new String("Snowbound Software :: Image Manipulation");

    public ImageManipulationApp()
    {
        SnowboundPanelWithAnnotations SPanel = new SnowboundPanelWithAnnotations();
        ImageManipulationUI UI = new ImageManipulationUI(SPanel, frameTitle);
    }

    public static void main(String[] args)
    {
        ImageManipulationApp imageManipApp = new ImageManipulationApp();
    }
}
