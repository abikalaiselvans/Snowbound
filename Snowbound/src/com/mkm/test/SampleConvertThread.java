/*
 * Copyright (c) 2011-2018, Snowbound Software
 *
 * This is example code for all SnowBound customers to freely copy and use
 * however they wish.
 *
 * Sample HangDetector.java
 *   See accompanying HangDetector.java for sample description and main.
 *  
 * RasterMaster library functions used:
 *  Snowbnd() - RasterMaster library constructor
 *  IMG_decompress_bitmap
 *  snow.IMG_save_bitmap
 * 
 */

package com.mkm.test;

import Snow.Snowbnd;

/**
 * This is a Thread class to perform a simple RasterMaster conversion.
 */
public class SampleConvertThread extends Thread {
	String mInputFile;
	String mOutputFile;
	int mPageIndex;
	int mOutputType;
	boolean flag = true;
	volatile long startTime;
	volatile long endTime;

	public void stopRunning() {
		flag = false;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getTime() {
		endTime = System.currentTimeMillis();
		return startTime-endTime;
	}
	public void startTime() {
		startTime = System.currentTimeMillis();
	}

	public SampleConvertThread(String inputFile, String outputFile, int pageIndex, int outputType) {
		mInputFile = inputFile;
		mOutputFile = outputFile;
		mPageIndex = pageIndex;
		mOutputType = outputType;
	}

	public void run() {
		while (flag) {
			Snowbnd snow = new Snowbnd();
			//System.out.println("About to Decompress " + mInputFile + ", pageIndex= " + mPageIndex);

			/* Read in input file automatically detecting the document's format */
			while (flag) {
				int decompStat = snow.IMG_decompress_bitmap(mInputFile, mPageIndex);
				if (decompStat >= 0) {
					try {
						while (flag) {
							/* Save the document in the requested output format */
							int result = 0;
							int saveStat = snow.IMG_save_bitmap(mOutputFile, mOutputType);
							if (saveStat < 0) {

								
								
								System.out.println("Error (" + saveStat + ") saving " + mOutputFile + ", pageIndex= "
										+ mPageIndex);
							}else {
							System.out.println("Page "+mPageIndex+" decompressed and saved successfully");
							flag = false;
							}
							
						}
					} catch (Exception ex) {
									ex.getMessage();
					}

				}else {
					System.out.println("Error (" + decompStat + ") decompressing " + mInputFile
							+ ", pageIndex= " + mPageIndex);
				}

			}
		}
	}
}