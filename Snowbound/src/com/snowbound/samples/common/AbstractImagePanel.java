/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */
 
package com.snowbound.samples.common;

import java.awt.Panel;
import java.awt.Rectangle;

import Snow.Snowbnd;

public abstract class AbstractImagePanel extends Panel
{

    public abstract int watermarkMerge(Rectangle selrect);
    
    public abstract Snowbnd getImage();
    
    public abstract MagPanel getMagPanel();
    
    public abstract void drawBackImage();

    public abstract double getZoomRatio();
    
    public abstract void setBackImageActive(boolean boolval);
    
    public abstract void zoomRubberBand(Rectangle selRect);
}
