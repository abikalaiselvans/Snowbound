/**
 * Copyright (C) 2009-2017 by Snowbound Software Corp. All rights reserved.
 *
 * This is example code for all Snowbound Software customers to freely copy and use
 * however they wish.
 *
 * This is the save to memory sample for RasterMaster Java. It loads an image from a document and
 * saves it in the desired format to a byte array.
 * 
 * Input:
 *     Document name defined in variable inputFilename and defaults to "C:/images/mysource.pdf"
 * Output:
 *     A byte array containing the image data of the page
 *
 *  This sample makes use of the following RasterMaster methods:
 *  
 *    Snowbnd.IMGLOW_get_pages(String filename);
 *    Snowbnd.IMG_decompress_bitmap(String filename, int pageIndex);
 *    Snowbnd.IMG_save_bitmap(int initialByteSize, int byteIncrement, int fileType, int[] saveStatus);
 *    Snowbnd.IMG_save_bitmap(byte[] convertedBytes, int byteIncrement, int fileType, int[] saveStatus);
 *    Snowbnd.IMG_decompress_bitmap(DataInputStream di, 0);
 *    ErrorCodes.getErrorMessage(int errorCode);
 *    
 * See  "Java Samples" in the "RasterMaster Java Manual"  at RasterMaster.com for more information.
 */
package com.snowbound.samples.snippets;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import Snow.Defines;
import Snow.ErrorCodes;
import Snow.Snowbnd;

public class SavePageToMemory
{
    /**
     * This method demonstrates how to read in an image, convert
     * to a specified image type and then save to a byte array.
     *
     * @param inputFilename Name of file to convert.
     * @param fileType Type of file to save (See Appendix B)
     * @return byte[]. byte array containing converted file.
     */
    public static byte[] convertToByteArray(String inputFilename, int fileType)
    {
        Snowbnd sImage = new Snowbnd(); /* Snowbound object */
        int status = 0; /* initialize return status */
        int saveSize = 0; /* This will hold the length of the saved content, returned by the save method */
        /* get the page count for the document */
        int pageCount = sImage.IMGLOW_get_pages(inputFilename);
        /* First, convert the first page to determine the baseLine byte array. */
        /* NOTE: Snowbound will determine the file type and open accordingly */
        int pageIndex = 0;
        status = sImage.IMG_decompress_bitmap(inputFilename, pageIndex);
        if (status < 0)
        {
        	//pageIndex = status;
            System.out.println("Error decompressing Image: "
                + ErrorCodes.getErrorMessage(status));
        }
        /* For the first save, we will have to estimate an initial size for the array. */
        int initialByteSize = 50000;
        int byteIncrement = 20000;
        /* the saveStatus array will contain any error codes at array index 0 */
        int[] saveStatus = new int[1];
        byte[] convertedBytes = sImage.IMG_save_bitmap(initialByteSize,
                                                       byteIncrement,
                                                       fileType,
                                                       saveStatus);
        saveSize = saveStatus[0];
        if (saveSize < 0)
        {
            System.out.println("Error saving Image: "
                + ErrorCodes.getErrorMessage(saveSize));
        }
        /* Loop through each additional page  and save it to the byte array */
        for (pageIndex = 0; pageIndex < pageCount; pageIndex++)
        {
        	System.out.println("Processing pages: " + pageIndex);
        	try
        	{
        		
        		/* NOTE: Snowbound will determine the file type and open accordingly */
                status = sImage.IMG_decompress_bitmap(inputFilename, pageIndex);
                if (status < 0)
                {
                    System.out.println("Error Decompressing Image: "
                        + ErrorCodes.getErrorMessage(status));
                }
                convertedBytes = sImage.IMG_save_bitmap(convertedBytes,
                                                        byteIncrement,
                                                        fileType,
                                                        saveStatus);
                saveSize = saveStatus[0];
                if (saveSize < 0)
                {
                    System.out.println("Error saving Image: "
                        + ErrorCodes.getErrorMessage(saveSize));
                }
                else
				{
					System.out.println("pageIndex: " + saveSize + " bytes saved successfully");

				}
                
        	}
        	catch(Exception ex)
        	{
        		ex.printStackTrace();
        	}
            
           
            
        }
        /* At this point we are done saving. If convertedBytes.length is greater than the saveSize,
         * you may consider trimming the byte array
         */
        if (convertedBytes.length > saveSize)
        {
            byte[] trimmedBytes = new byte[saveSize];
            System.arraycopy(convertedBytes, 0, trimmedBytes, 0, saveSize);
            convertedBytes = trimmedBytes;
        }
        /* return byte array containing converted image */
        return convertedBytes;
    }

    /**
     * This method demonstrates how to read in an image from a byte array.
     *
     * @param buff Byte array containing image data.
     * @return Integer. Status of file conversion method.
     */
    public static int clientConvert(byte[] buff)
    {
        Snowbnd sImage = new Snowbnd(); /* Snowbound object */
        int status = 0; /* initialize return status */
        java.io.DataInputStream di;
        /* create DataInputStream from byte array */
        di = new java.io.DataInputStream(new java.io.ByteArrayInputStream(buff));
        /* */
        status = sImage.IMG_decompress_bitmap(di, 0);
        /* do something with decompressed image (i.e. display) */
        return status;
    }

    /**
     * Executes the Save Page To Memory sample.
     * 
     * If running this sample from a command line, you could also optionally pass
     * the inputFilename and outputFilename as command line arguments:
     * 
     * For example: 
     * 
     * java com.snowbound.samples.snippets.SavePageToMemory C:/images/mysource.pdf 
     * @throws IOException 
     */
    public static void main(String args[]) throws IOException
    {
    	String inputFilename = "C:/images/mysource.pdf";
    	if (args.length > 0)
    	{
    		inputFilename = args[0];
    	}
    	byte[] convertedBytes = convertToByteArray(inputFilename, Defines.TIFF_G4_FAX);
    	BufferedOutputStream bufferStream = new BufferedOutputStream(new FileOutputStream("C:/images/mysource.tiff"));
    	bufferStream.write(convertedBytes);
    	bufferStream.flush();
    	bufferStream.close(); 	
    }
    
}