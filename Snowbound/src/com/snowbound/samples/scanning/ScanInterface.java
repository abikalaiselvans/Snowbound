/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.scanning;

import java.io.DataInputStream;
import java.io.ByteArrayInputStream;

import Snow.MessageBox;
import Snow.Snowbnd;

public class ScanInterface 
{
	/* these correspond to functions in the RasterMaster native SDK */
	public native int IMG_scan_acquire(int showui);
	public native int IMG_scan_acquire_feeder(int showui);
	public native int IMG_scan_feeder_close();
	public native int IMG_scan_open_source();
	
	public native String[] IMG_scan_get_source_list();
	
	public native int IMG_scan_pages(String filename, int filetype, int showui);
	public native int IMG_scan_get_cap(int cap);
	public native int IMG_scan_set_cap(int cap, int value);
	public native int IMG_scan_set_caps(int bitspix, double left, double top, double right, double bottom, int hres, int vres);
	public native int IMG_delete_bitmap(int imghandle);
	
	private native byte[] getImage(int imghandle);

	public Snowbnd Simage;

	public ScanInterface() 
	{	
		/* you can load the scandll.dll two different ways */
		
		/* load DLL by specify absolute directory */
		/* NOTE: assumes scandll.dll in same directory as ScanInterface.java */
		
		String dllPath = System.getProperty("user.dir");
		dllPath += System.getProperty("file.separator") + "com";
		dllPath += System.getProperty("file.separator") + "snowbound";
		dllPath += System.getProperty("file.separator") + "samples";
		dllPath += System.getProperty("file.separator") + "scanning";
		dllPath += System.getProperty("file.separator") + "scandll.dll";
		
	    System.load(dllPath);
	    
	    /* load DLL with specifying absolute path */
	    /* NOTE: scandll.dll will need to be on your PATH */
	    // String dllPath = "scandll";
	    // System.loadLibrary("scandll");
	}
	
	public DataInputStream IMG_get_scanned_image(int imghandle)
	{
		byte[] buff;
		buff = getImage(imghandle);
		DataInputStream di = new DataInputStream(new ByteArrayInputStream(buff));

		return di;
	}

	public byte[] IMG_get_scanned_image_bytes(int imghandle)
	{
		byte[] buff = getImage(imghandle);
		
		return buff;
	}
	
	public static void main(String[] args) 
	{
		ScanInterface scanTest1 = new ScanInterface();
	}

}


