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

package com.snowbound.samples.snippets;

import Snow.Snowbnd;
import Snow.ErrorCodes;



public class MemoryReq {
	public static void main(String[] args) {


		// Returns the memory requirements for specified file
		
		String inputFile = "C:\\Test\\Input\\18627-persuasion00austuoft.pdf";
		if (args.length > 0) {
			inputFile = args[0];
		}
		
		int BPP = 0;
		int heightPixels = 0;
		int widthPixels = 0;
		int highMem = 0;
		
		int memoryNeeded = 0;
		

		// Create the Snowbound image object
		Snowbnd snowImage = new Snowbnd();

		// Create the input and output file extensions
		int fileType = snowImage.IMGLOW_get_filetype(inputFile);

        // Check for errors
		if (0 > fileType) {
			System.out.println("Please edit the 'inputFile' variable to specify a good filename. ["
                        + ErrorCodes.getErrorMessage(fileType) + "] processing input file " + inputFile);
			return;
		   }

		// List the file name & location
		System.out.println("Determing memory requirements for " + inputFile);

		// Print out 
		int totalPages = snowImage.IMGLOW_get_pages(inputFile);
		System.out.println("Total number of pages = " + totalPages);
		System.out.println();

		
		// Loop through all pages to determine memory requirements for each page.
		for (int page = 0; page < totalPages; page++) {
			int status = 0;
			
			// Need to decompress to get file information
			status = snowImage.IMG_decompress_bitmap(inputFile, page);
			if (0 > status) {
				System.out.println("Decompress error: " + status);
				}
			
			
			// Get the BPP, height, & width
			BPP = snowImage.getBitsPerPixel();
	
			heightPixels = snowImage.getHeight();
			widthPixels = snowImage.getWidth();
			
		
			// Perform calculations
			// memoryNeeded is converted to bytes
			memoryNeeded = (((heightPixels * widthPixels * BPP) / 8) / 1000);
		
		
			// Print out results
			// Add 1 to Page for ease of reading
			System.out.println("Memory needed for Page #: " + (page + 1));
			System.out.println("Bytes: " + memoryNeeded);
			System.out.println("MB: " + (memoryNeeded / 1000.0));
			System.out.println();
		
			if (highMem < memoryNeeded){
				highMem = memoryNeeded;
			}
		
		}  // End for loop
		
		// Setting highMem to 1MB if working with small file
		if (highMem < 1000){
			highMem = 1000;
		}
		
		System.out.println("Largest RAM needed: " + (highMem / 1000.0) + "MB");
	}
	}