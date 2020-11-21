/*
 * Copyright (c) 2011-2017, Snowbound Software
 *
 * This is example code for all SnowBound customers to freely copy and use
 * however they wish.
 *
 * Sample SingleConversion.java
 *   Convert an input file to a different format. 
 *   The input file name is supplied on the command line.
 *   To change the output format from TIFF_LZW, edit "outputFormat" initial value below.
 *   See the code comments for additional details.
 *
 *  If you need any help please reference the manual at RasterMaster.com 
 *  or contact http://support.snowbound.com.
 *  
 * RasterMaster library functions used:
 *  Snowbnd() - RasterMaster library constructor
 *  IMGLOW_get_filetype
 *  IMGLOW_get_pages
 *  IMGLOW_set_ooxml_license
 *  decomp_vect
 *  IMGLOW_set_document_input
 *  IMGLOW_set_fontmap_path
 *  IMGLOW_set_overlay_path
 *  getXdpi, getYdpi
 *  getHeight, getWidth
 *  IMG_decompress_bitmap
 
 *
 *  IMG_diffusion_mono
 *  IMG_color_gray
 *  IMG_promote_24
 *  setXdpi, setYdpi
 *  IMG_resize_bitmap
 *  snow.IMG_save_bitmap
 * 
 */

/**
* This is a simple conversion sample that converts an input file to a
* specified output format.  The user must provide values for the
* 'inputFile' and 'outputFormat' variables.  See additional code comments
* for more details.
*/

package com.mkm.test;

import Snow.Defines;
import Snow.ErrorCodes;
import Snow.FormatHash;
import Snow.Format;
import Snow.Snowbnd;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleConversion {
	public static void main(String[] args) {


		/* Specify the input file path and the desired output format.
		   For multi-page color and/or grayscale output, PDF or TIFF_LZW
		   is recommended. NOTE: You may see an increase in the converted
		   file size when using TIFF_LZW. To reduce the output file size,
		   try TIFF_JPEG7 or reduce the image to 1-bit and use TIFF_G4_FAX.
	    */
		String inputFile = "C:\\Test\\Input\\test.pdf";
		if (args.length > 0) {
			inputFile = args[0];
		}
		int outputFormat = Defines.PDF;
		
		//Allow output as SVG
		boolean SVGOutput = true;
		
		// Following variables can be used to set a page range. 0 is page 1
		int SVGPageStart = 0;
		int SVGPageEnd = 10;

		// create the Snowbound image object
		Snowbnd snowImage = new Snowbnd();

		// create the input and output file extensions
		int fileType = snowImage.IMGLOW_get_filetype(inputFile);

            // always check for errors
		if (0 > fileType) {
			System.out.println("Make sure license is in classpath or edit the 'inputFile' variable to specify a good filename. ["
                        + ErrorCodes.getErrorMessage(fileType) + "] processing input file " + inputFile);
			return;
		   }

		FormatHash formatHash = FormatHash.getInstance();
		Format inputExt = formatHash.getFormat(fileType);
		Format outputExt = formatHash.getFormat(outputFormat);

		// print the file type of the input & output files
		System.out.println("converting " + inputExt.getFormatName() + " to "
				+ outputExt.getFormatName() + "...");

		// print the total number of pages in the input document
		int totalPages = snowImage.IMGLOW_get_pages(inputFile);
		System.out.println("total number of pages = " + totalPages);

	    /* begin loop to decompress the input document, and save to
		   the specified output format
		*/

		//get the start time
		//Date startTime = new Date();
		Date startTime;
		System.out.println(" SingleConversion started at:" + new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(startTime = new Date()));

		for (int page = 0; page < totalPages; page++) {
		int status = 0;

		/* PRE-DECOMPRESSION OPTIONS:
		   -For vector PDFs "searchable PDF" output:
		    status = snowImage.decomp_vect = true;

		   -To specify the Office 2007 (OOXML)license file:
		    status = snowImage.IMGLOW_set_ooxml_license("C:\\aspose\\Aspose.Words.lic");

		   -To specify the input DPI and bit-depth for PDF, MS Office, RTF, PCL, and AFP files:
		    status = snowImage.IMGLOW_set_document_input(300, 24, Defines.PDF);

		   -To specify the font-map & overlay path for AFP files:
		    status = snowImage.IMGLOW_set_fontmap_path("C:\\path\\to\\font-map");
		    status = snowImage.IMGLOW_set_overlay_path("C:\\path\\to\\overlay");

		   -To get the horizontal and vertical DPI of the current page:
		    status = snowImage.getXdpi();
		    status = snowImage.getYdpi();

		   -To get the height and width of the current page:
		    status = snowImage.getHeight();
		    status = snowImage.getWidth();

		   + Please remember to check the status is successful (0 or greater) before continuing   
		*/

		// decompress the current page and print the resulting status code
		status = snowImage.IMG_decompress_bitmap(inputFile, page);
		System.out.println("decompression status = " + status);

		// print the error code details if decompression fails
		if (0 > status) {
			System.out.println("ERROR: " + status +
		    " [" + ErrorCodes.getErrorMessage(status) + "] on p. {" + page + "}");
			continue;
		   }

		/* POST-DECOMPRESSION OPTIONS:
		   -To reduce the bit-depth to 1-bit black & white:
		    status = snowImage.IMG_diffusion_mono();

		   -To reduce a color image to 8-bit gray scale:
		    status = snowImage.IMG_color_gray();

		   -To promote 1, 4, or 8-bit images to 24-bit:
		    status = snowImage.IMG_promote_24();

		   -To change the output DPI while maintaining image size:
		    int newXdpi = 300;
			int newYdpi = 300;
			double newXsize = (double)newXdpi/(double)snowImage.getXdpi()
		                  	  *(double)snowImage.getWidth();
			double newYsize = (double)newYdpi/(double)snowImage.getYdpi()
		                      *(double)snowImage.getHeight();
			status = snowImage.setXdpi(newXdpi);
			status = snowImage.setYdpi(newYdpi);
	    	      status = snowImage.IMG_resize_bitmap((int)newXsize, (int)newYsize);
        */

		// save the current page and print the resulting status code (Raster)
		status = snowImage.IMG_save_bitmap(inputFile + "_out."
				+ outputExt.getExtension(), outputFormat);
		System.out.println("save Raster status = " + status);
		

		
		
		// print the error code details if save fails
		if (0 > status) {
		   System.out.println("ERROR on save: " + status
		   + " [" + ErrorCodes.getErrorMessage(status) + "] on p. {" + page + "}");
		   continue;
		  }

		System.out.println("Done p. " + page);
		
		}
		
		//Iterating through each page and converting to SVG
		if (SVGOutput) {
			String outputFile = inputFile;
			for (int currentPage = SVGPageStart, x=1; (currentPage < SVGPageEnd) && (currentPage < totalPages); currentPage++,x++) {
			int status = 0;
			//Adding number after each individual page (page1.svg, page2.svg, etc)
			status = snowImage.IMG_vector_to_svg(inputFile, outputFile + x +"_out.svg", currentPage);
			System.out.println("SVG decompression status = " + status);
			}
		}

		System.out.println("conversion completed!");

		//get the end time
		Date endTime;
		System.out.println(" SingleConversion ended at: " + new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(endTime = new Date()));

		//print the total conversion time
		long totalTime = endTime.getTime() - startTime.getTime();
		System.out.println("total conversion time = " + (double)totalTime/1000 + " seconds");

		if (true) return;

		}
	}