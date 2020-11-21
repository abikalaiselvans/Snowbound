/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.watermark;

import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class WaterMarkApp
{
	private static final String frameTitle = new String("Snowbound Software :: Watermark");

	public WaterMarkApp()
	{
		SnowboundPanelWithAnnotations SPanel = new SnowboundPanelWithAnnotations();
		WaterMarkUI UI = new WaterMarkUI(SPanel,frameTitle);
	}

	public static void main(String[] args)
	{
		WaterMarkApp watermarkApp = new WaterMarkApp();
	}
}
