/*
 * Copyright (c) 2011-2018, Snowbound Software
 *
 * This is example code for all SnowBound customers to freely copy and use
 * however they wish.
 *
 * Sample HangDetector.java
 *   If a document does not convert within set amount of time, then halt the conversion and print a timeout message.
 *   Please note that this is more of a timeout detector then a hang detector. If file gets completely stuck inside a
 *   Snowbound API, it will never get kicked out.
 *   Update inputPath with file location that you want to send through conversion
 *   If you don't want output location to be the same as input, update that as well
 *  
 *  Updated by: Luke Miranda and Jeiro Coste
 *  
 * RasterMaster library functions used:
 *  none directly. References are in accompanying SampleConvertThread.java
 */

package com.snowbound.samples.convert;

import Snow.Defines;
import Snow.Snowbnd;

/**
 * This class serves to demonstrate how to implement a timeout within a
 * RasterMaster conversion routine.
 */
public class HangDetector {

	public static long topTime = 0;

	/**
	 * This method will perform a conversion from inputFile to outputStem in PNG
	 * format. If the conversion takes more than the specified timeout value in
	 * milliseconds, the method will return after the most recent conversion.
	 * 
	 */
	public static void convertPages(String inputPath, String outputPath, long timeout, int pages) {
		long duration = 0;
		for (int pageIndex = 0; pageIndex < pages; pageIndex++) {
			if (duration <= timeout) {
				long before = System.currentTimeMillis();
				convertWithTimeout(inputPath, outputPath, pageIndex, timeout);
				long after = System.currentTimeMillis();
				duration = after - before;
			} else if (duration >= timeout) {
				System.out.println(
						"Conversion took longer then "+timeout+ " ms and ended at: " + duration + " ms on page " + pageIndex);
			}

		}
	}

	/**
	 * 
	 * @param inputFile
	 *            file path for input file
	 * @param outputStem
	 *            file path for output file location
	 * @param pageIndex
	 *            page number being converted
	 * @param timeout
	 *            time set and causes a timeout message to be returned after the
	 *            most recent conversion finishes
	 */
	public static void convertWithTimeout(String inputFile, String outputStem, int pageIndex, long timeout) {
		String outputFile = outputStem + "pageIndex" + pageIndex + ".png";
		SampleConvertThread convertThread = new SampleConvertThread(inputFile, outputFile, pageIndex, Defines.PNG);

		try {
			
				convertThread.startTime();
				topTime = convertThread.getTime();
//				if(topTime<=timeout) {
				convertThread.start();
//				}
//				else {
	//				System.out.println("Timeout at "+convertThread.getTime()+" on page "+pageIndex);
		//		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		Snowbnd simage = new Snowbnd();
		// Enter the input path of a document you want to send through hang detector
		// and also an output path with a name for the outputted document.
		String inputPath = "C:\\Test\\Input\\15177_netx.pdf";
		String outputPath = inputPath + "-output";
		long timeout = 1;
		int pages = simage.IMGLOW_get_pages(inputPath);
		convertPages(inputPath, outputPath, timeout, pages);
	}

}