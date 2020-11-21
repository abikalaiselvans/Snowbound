/**
 * Copyright (C) 2007-2018 by Snowbound Software Corp. All rights reserved.
 * This is example code for all SnowBound customers to freely copy and use however they wish.
 * 
 * Authors: Luke Miranda, Jiero Coste
 * 
 */

/**
 * Sample shows how to convert a file to SVG format by inputting a filepath in the inputFile variable
 */

package com.snowbound.samples.convert;

import java.text.SimpleDateFormat;
import java.util.Date;
import Snow.Defines;
import Snow.ErrorCodes;
import Snow.Format;
import Snow.FormatHash;
import Snow.Snowbnd;

public class SVGConversion {

	public static void main(String[] args) {

		String inputFile = "C:\\Test\\Input\\test.pdf";
		int x = 1;
		if (args.length > 0) {
			inputFile = args[0];
		}
		// Snowbound object created
		Snowbnd snow = new Snowbnd();

		// Get the original filetype from inputted file
		int fileType = snow.IMGLOW_get_filetype(inputFile);

		// Negative filetype integer is an error code (Find in manual)
		if (0 > fileType) {
			System.out.println("Please edit the 'inputFile' variable to specify a good filename. ["
					+ ErrorCodes.getErrorMessage(fileType) + "] processing input file " + inputFile);
			return;
		}

		FormatHash formatHash = FormatHash.getInstance();
		Format inputExt = formatHash.getFormat(fileType);
		Format outputExt = formatHash.getFormat(Defines.SVG);

		// print the file type of the input & output files
		System.out.println("converting " + inputExt.getFormatName() + " to " + outputExt.getFormatName() + "...");

		// print the total number of pages in the input document
		int totalPages = snow.IMGLOW_get_pages(inputFile);
		System.out.println("total number of pages = " + totalPages);

		/*
		 * begin loop to decompress the input document, and save to the specified output
		 * format (SVG in this case)
		 */

		// get the start time
		Date startTime;
		System.out.println(" SingleConversion started at:"
				+ new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(startTime = new Date()));

		// Iterating through each page and converting to SVG
		for (int page = 0; page < totalPages; page++) {
			int status = 0;
			// Adding number after each individual page (page1.svg, page2.svg, etc)
			status = snow.IMG_vector_to_svg(inputFile, inputFile + x + "_out.svg", page);
			x++;
			System.out.println("decompression status = " + status);
			// print the error code details if save fails
			if (0 > status) {
				System.out.println(
						"ERROR: " + status + " [" + ErrorCodes.getErrorMessage(status) + "] on p. {" + page + "}");
			}

			System.out.println("Done p. " + page);

		}

		System.out.println("conversion completed!");

		// get the end time
		Date endTime;
		System.out.println(" SingleConversion ended at: "
				+ new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(endTime = new Date()));

		// print the total conversion time
		long totalTime = endTime.getTime() - startTime.getTime();
		System.out.println("total conversion time = " + (double) totalTime / 1000 + " seconds");

		if (true)
			return;
	}
}
