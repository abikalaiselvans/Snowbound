/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.scanning;

import com.snowbound.samples.scanning.ScanUI;
import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

public class ScanApp
{
	private static final String frameTitle = new String("Snowbound Software :: Scanning");

	public ScanApp()
	{
		SnowboundPanelWithAnnotations SPanel = new SnowboundPanelWithAnnotations();
		ScanUI UI = new ScanUI(SPanel,frameTitle);
	}

	public static void main(String[] args)
	{
		ScanApp scanApp1 = new ScanApp();
	}
}