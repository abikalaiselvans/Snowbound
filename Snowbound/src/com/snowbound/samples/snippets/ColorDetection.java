package com.snowbound.samples.snippets;

import Snow.Defines;
import Snow.ErrorCodes;
import Snow.Snowbnd;

/**
 * Copyright (C) 2009-2017 by Snowbound Software Corp. All rights reserved.
 *
 * This is example code for all Snowbound Software customers to freely copy and use
 * however they wish.
 *
 * This is the color detection sample for RasterMaster Java. It converts a document to tiff
 * and selects the appropriate tiff format (Tiff Group 4, or Tiff LZW) based on the color 
 * profile of each source page.
 * 
 * Input:
 *     Document name defined in variable inputFilename and defaults to "C:/images/mysource.pdf"
 * Output:
 *     Document name defined in variable outputFilename and defaults to "C:/images/myoutput.tif"
 *
 *  This sample makes use of the following RasterMaster methods:
 *  
 *  Snowbnd.IMGLOW_set_document_input(int dpi, int bitDepth, int format); 
 *  Snowbnd.IMGLOW_get_pages(String inputFilename);
 *  Snowbnd.IMG_decompress_bitmap(String inputFilename, int pageIndex);
 *  Snowbnd.IMGLOW_detect_color();
 *  Snowbnd.IMG_thresh_mono();
 *  Snowbnd.IMG_save_bitmap(String outputFilename, int outputFormat);
 *
 * See  "Java Samples" in the "RasterMaster Java Manual"  at RasterMaster.com for more information.
 */
public class ColorDetection
{
    public static void convertDocumentByDetectingColor(String inputFilename,
                                                       String outputFilename)
    {
        System.out.println("convertDocumentByDetectingColor");
        System.out.println("inputFilename: " + inputFilename);
        System.out.println("outputFilename: " + outputFilename);
        /* Create a Snowbnd object that will be used to perform the image routines. */
        Snowbnd snowImage = new Snowbnd();

        /*
         * Set the desired parameters for electronic/ vector filetypes
         */
        int dpi = 300; /* Higher values yield higher quality / larger documents, but take up more memory and time */
        int bitDepth = 24; /* Use a value of 1 if you only want to process pdfs in black and white */
        snowImage.IMGLOW_set_document_input(dpi, bitDepth, Defines.PDF);
        
        /** call IMGLOW_set_document_input for each filetype that you want to process... */ 
        
        // snowImage.IMGLOW_set_document_input(dpi, bitDepth, Defines.DOC); 
        // snowImage.IMGLOW_set_document_input(dpi, bitDepth, Defines.EXCEL); 
        
        /* Get the page count of the source image */
        int pageCount = snowImage.IMGLOW_get_pages(inputFilename);
        /* Check the page count return value for an error */
        if (pageCount < 0)
        {
            /* Return values less than zero indicate an error */
            System.out.println("Error in counting pages: "
                + ErrorCodes.getErrorMessage(pageCount));
            return;
        }
        else
        {
            System.out.println("The input document has " + pageCount
                + " pages.");
        }
        /* Otherwise we have a valid page count */
        /* Loop through each page and perform the conversion */
        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++)
        {
            /* pageIndex is zero-based */
            System.out.println("Processing page at pageIndex " + pageIndex);
            try
            {
                int decompressStatus = snowImage
                    .IMG_decompress_bitmap(inputFilename, pageIndex);
                /* Check the decompressStatus for an error */
                if (decompressStatus < 0)
                {
                    /* Return values less than zero indicate an error */
                    System.out.println("Error decompressing page at pageIndex="
                        + pageIndex + " : "
                        + ErrorCodes.getErrorMessage(decompressStatus));
                    /* 
                     * Depending on your needs, you may wish to continue with the other pages, or break out of 
                     * the loop 
                     */
                    continue; /* 'continue' means to continue on to the next pageIndex */
                    // break; /* 'break' means to exit out of the loop */
                }
                int pageBitsPerPixel = snowImage.IMGLOW_detect_color();
                System.out.println("The bit depth of the page at pageIndex "
                    + pageIndex + " is " + pageBitsPerPixel);
                int outputFormat; /* This variable will indicate the output format to save to */
                if (pageBitsPerPixel == 24)
                {
                    /* For 24-bit color pages, we will save as TIFF_LZW */
                    outputFormat = Snow.Defines.TIFF_LZW;
                    /* You might also choose Snow.Defines.TIFF_JPEG */
                }
                else
                {
                    /* For all other bit depths, we will save as TIFF Group 4 */
                    outputFormat = Snow.Defines.TIFF_G4_FAX;
                    if (pageBitsPerPixel == 8)
                    {
                        /* If the page is 8 bits, then calling thresh_mono will convert it to one bit
                         * for optimal TIFF Group 4 saving */
                        snowImage.IMG_thresh_mono();
                    }
                }
                /* 
                 * Now we can save the page to our output file.
                 * When the output file is an existing tiff file, then the save method
                 * will *append* this page to the end of the existing document. 
                 */
                int saveStatus = snowImage.IMG_save_bitmap(outputFilename,
                                                           outputFormat);
                System.out.println("Save Success");
                if (saveStatus < 0)
                {
                    /* Return values less than zero indicate an error */
                    System.out.println("Error saving page at pageIndex="
                        + pageIndex + " : "
                        + ErrorCodes.getErrorMessage(saveStatus));
                }
                else
                {
                    System.out.println("pageIndex " + pageIndex
                        + " processed successfully");
                }
            }
            catch (Exception e)
            {
                System.out
                    .println("Exception thrown at pageIndex=" + pageIndex);
                e.printStackTrace();
            }
        }
    }

    /**
     * Executes the Color Detection sample.
     * 
     * If running this sample from a command line, you could also optionally pass
     * the inputFilename and outputFilename as command line arguments:
     * 
     * For example: 
     * 
     * java com.snowbound.samples.snippets.ColorDetection C:/images/mysource.pdf C:/images/myoutput.tif
     */
    public static void main(String args[])
    {
        String inputFilename = "C:/images/mysource.pdf";
        String outputFilename = "C:/images/myoutput.tif";
        if (args.length > 0)
        {
            inputFilename = args[0];
        }
        if (args.length > 1)
        {
            outputFilename = args[1];
        }
        convertDocumentByDetectingColor(inputFilename, outputFilename);
    }
}
