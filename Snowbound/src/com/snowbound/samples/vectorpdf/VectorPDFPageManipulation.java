/**
 * Copyright (C) 2009-2013 by Snowbound Software Corp. All rights reserved.
 *
 * This is example code for all Snowbound Software customers to freely copy and use
 * however they wish.
 *
 * This is the vector PDF page manipulation sample for RasterMaster Java.  It allows one to
 * delete, extract and append pages to and from a vector PDF file without causing additional
 * pages in that vector PDF to change into non-vector pages because of those manipulations.
 * Pages being appended to vector PDFs can be vector or non vector pages.
 *
 *  Command-line Parameters:
 *  Required:
 *  args[0] = Input vector PDF
 *  args[1] = Input image
 *  Optional:
 *  args[2] = Initial size of buffer used by IMG_save_bitmap()
 *  args[3] = Amount the buffer used by IMG_save_bitmap() will grow by if needed
 *
 *  This sample makes use of the following RasterMaster methods:
 *
 *  Snowbnd.IMGLOW_delete_page(String bm_name,int page,int error[])
 *  Snowbnd.IMGLOW_extract_page(String filename, int page, int error[])
 *  Snowbnd.IMGLOW_append_page(String filename,byte buff[],int format)
 *  Snowbnd.IMG_decompress_bitmap(String bm_name, int page)
 *  Snowbnd.IMG_save_bitmap(int initial_size, int buff_inc, int comp_type, int[] error)
 *
 * See  "Java Samples" in the "RasterMaster Java Manual"  at RasterMaster.com for more information.
 *
 * Author: GG
 */

package com.snowbound.samples.vectorpdf;

import java.io.*;

import Snow.Defines;
import Snow.Snowbnd;
import com.snowbound.common.utils.Logger;

public class VectorPDFPageManipulation {

	private static int vectorPdfPageIndex = 0;
	private static int bitMapPageIndex = 0;
	//Snowbound error code
	private static int nstat;
	//Initial size of buffer used by IMG_save_bitmap()
	private static int initialBufferSize = 60000;
	//Amount the buffer used by IMG_save_bitmap() will grow by if needed
	private static int expandBufferSize = 40000;

	private static boolean accessDirectly;

/***************************************************************************
 * Delete vector PDF page
 **************************************************************************/
	public static void deleteVectorPage(Snowbnd snowObject, String startFile, int pageIndex, int [] error)
	{
		//A byte array containing all pages not deleted
		byte deletedVectorPage[] = snowObject.IMGLOW_delete_page(startFile,1,error);
		//Create a new file to save non-deleted pages to
		File outputFile = new File(startFile+"_deletedPage"+pageIndex+".pdf");
		//Save data stored in byte array to new document
		saveFileBytes(deletedVectorPage,outputFile);
		System.out.println("Page "+(pageIndex + 1)+" deleted from "+startFile);
		System.out.println("Document without deleted vectorpage created here: " +outputFile);
	}

/***************************************************************************
 * Extract a vector PDF page
 **************************************************************************/
	public static byte[] extractVectorPage(Snowbnd snowObject, String startFile, int pageIndex, int [] error, boolean access)
	{
		//A byte array containing the extracted vector PDF page
		byte extractedPage[] = snowObject.IMGLOW_extract_page(startFile, pageIndex, error);
		//Create a new file to save non-deleted pages to
		File outputFile = new File(startFile+"_extractedPage"+pageIndex+".pdf");
		//Save data stored in byte array to new document
		saveFileBytes(extractedPage,outputFile);
		if(access == true)
		{
			System.out.println("Page "+(pageIndex + 1)+" extracted from "+startFile+" and saved here: " +outputFile);
		}
		return extractedPage;
	}

/***************************************************************************
 * Append a vector PDF page
 **************************************************************************/
	public static void appendVectorPage(Snowbnd snowObject, String startFile, int pageIndex, int [] error, boolean access)
	{
		//Append a vector PDF page extracted from the same vector PDF
		snowObject.IMGLOW_append_page(startFile,extractVectorPage(snowObject,startFile,pageIndex,error,accessDirectly),Defines.PDF );
		System.out.println("Page "+(pageIndex + 1)+" appeneded to "+startFile);
	}

/***************************************************************************
 * Append a non-vector PDF page
 **************************************************************************/
	public static void appendNonVectorPage(Snowbnd snowObject, String bitMap, String startFile, int pageIndex,int bitMapPageIndex, int [] error)
	{
		//Decompress image
		nstat = snowObject.IMG_decompress_bitmap(bitMap,bitMapPageIndex);
		if(nstat >= 0)
		{
			//Save decompressed image to byte array as PDF
			System.out.println(bitMap+" Successfully decompressed.");
			byte[] savedPdfBytes = snowObject.IMG_save_bitmap(initialBufferSize, expandBufferSize, Defines.PDF, error);
			//Append non-vector PDF page to vector PDF
			snowObject.IMGLOW_append_page(startFile,savedPdfBytes,Defines.PDF);
			System.out.println(bitMap+" appended to "+startFile);
		}
		else
		{
			System.out.println("Error decompressing image. Error code "+nstat);
			System.exit(0);
		}
	}

/***************************************************************************
 * Save byte array to file
 **************************************************************************/
	public static void saveFileBytes(byte[] data, File file)
	{
		if (data == null)
		{
			return;
		}
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
		}
		catch (Exception e)
		{
			Logger.getInstance().printStackTrace(e);
		}
	}

/***************************************************************************
* Main
**************************************************************************/

	public static void main(String[] args) throws IOException {

		String inputSearchablePDF = "C:\\savedTags\\ConversionError.pdf";
		//String inputSearchablePDF = args[0];
		String inputNonSearchableImage = "C:\\temp\\image.tif";
		//String inputNonSearchableImage = args[1];
        if (args.length > 2)
        {
        	initialBufferSize = Integer.parseInt(args[2]);
        }
        if (args.length > 3)
        {
        	expandBufferSize = Integer.parseInt(args[3]);
        }

		Snowbnd snow = new Snowbnd();
		int[] error = new int[1];
		error[0] = 0;

		if((new File(inputSearchablePDF).exists()) == true && (new File(inputNonSearchableImage).exists()) == true)
		{
			deleteVectorPage(snow,inputSearchablePDF,vectorPdfPageIndex,error);
			accessDirectly = true;
			extractVectorPage(snow,inputSearchablePDF,vectorPdfPageIndex,error,accessDirectly);
			accessDirectly = false;
			//Warning: appendVectorPage() and appendNonVectorPage() will modify original document
			appendVectorPage(snow,inputSearchablePDF,vectorPdfPageIndex,error,accessDirectly);
			appendNonVectorPage(snow,inputNonSearchableImage,inputSearchablePDF,vectorPdfPageIndex,bitMapPageIndex,error);
			System.out.println("Done");
		}
		else
		{
			System.out.println("Error: File does not exist");
			System.out.println("Exiting program");
			System.exit(0);
		}
	}

}