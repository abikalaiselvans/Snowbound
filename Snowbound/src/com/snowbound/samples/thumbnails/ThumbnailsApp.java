/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.thumbnails;

public class ThumbnailsApp
{
    private static final String frameTitle = new String("Snowbound Software :: Thumbnails");

    /**
     * Default constructor
     */
    public ThumbnailsApp()
    {
        ThumbnailsUI UI = new ThumbnailsUI(frameTitle);
    }

    /**
     * Main method starts things up
     *
     * @param args Command line arguments
     */
    public static void main(String[] args)
    {
        ThumbnailsApp thumbnailsApp1 = new ThumbnailsApp();
    }
}
