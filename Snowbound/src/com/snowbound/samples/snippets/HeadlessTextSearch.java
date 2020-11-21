/** ******************************************************************
 *
 *  HeadlessTextSearch
 *
 *  Demonstrates search for a text string within a searchable document.
 *
 *  Snowbound Functions Demonstrated:
 *      IMG_decompress_bitmap()
 *      IMG_promote_24()
 *      IMGLOW_extract_text()
 *      IMGLOW_search_text()
 *      IMG_save_bitmap()
 *      SANN_add_object()
 *      SANN_set_croprect()
 *      SANN_merge_annotations()
 *
 *  Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 *
 *******************************************************************/

package com.snowbound.samples.snippets;

import Snow.ErrorCodes;
import Snow.IMG_RECT;
import Snow.SANN_RECT;
import Snow.SNBD_SEARCH_RESULT;
import Snow.SnowAnn;
import Snow.Snowbnd;

public class HeadlessTextSearch {

    /**
     * This method demonstrates search for text, highlight resulting
     * search terms, and merge highlighted result onto output image
     *
     * @param inputFilePath Path to input file.
     * @param searchTerm Search term to locate in file at inputFilePath
     * @param outputFilePath Patch to save resulting output file to.
     */

	public static int headlessTextSearchAndMerge( String inputFilePath,
										  		  String searchTerm,
										          String outputFilePath )
	{
		int result;
        int[] length = new int[1];
        int[] extractError = new int[1];
        int[] searchError = new int[1];
        int nStatus = -1;
        Snowbnd s = new Snowbnd();
        SnowAnn annLayer = new SnowAnn(0, 0);

        nStatus = s.IMG_decompress_bitmap(inputFilePath, 0);
        nStatus = s.IMG_promote_24();

        if( nStatus < 0 ) {
        	result = nStatus;
        	System.out.println("Error decompressing" + nStatus + ErrorCodes.getErrorMessage(nStatus));
        	return nStatus;
        }
        else{
        	System.out.println("Decompression: " + nStatus);
        }
        byte[] text = s.IMGLOW_extract_text(inputFilePath, length, extractError, 0);

        SNBD_SEARCH_RESULT[] results = s.IMGLOW_search_text(text,
                                                            searchTerm,
                                                            0,
                                                            searchError);

        if (results != null)
        {
            for (int nIx = 0; nIx < results.length; nIx++)
            {
                IMG_RECT[] recta = results[nIx].rc;

                for (int r = 0; r < recta.length; r++)
                {
                    IMG_RECT imgRect = recta[r];
                    SANN_RECT sannRect = new SANN_RECT();

                    sannRect.top = imgRect.top;
                    sannRect.bottom = imgRect.bottom;
                    sannRect.left = imgRect.left;
                    sannRect.right = imgRect.right;
                    annLayer.SANN_add_object(SnowAnn.SANN_HIGHLIGHT_RECT,
                                             sannRect,
                                             null,
                                             null,
                                             0);
                }
            }
        }

        annLayer.SANN_set_croprect(0, 0, s.getWidth(), s.getHeight());
        annLayer.SANN_merge_annotations(s, null);
        nStatus = s.IMG_save_bitmap(outputFilePath, 0);

		return nStatus;
	}

    /**
     * This method demonstrates search for text in a searchable document.
     *
     * @param inputFilePath Path to input file.
     * @param searchTerm Search term to locate in file at inputFilePath.
     */

	public static int headlessTextSearch( String inputFilePath,
										  String searchTerm)
	{
		int[] length = new int[1];
		int[] error = new int[1];
		int[] errorA = new int[1];
		length[0] = 0;
		error[0] = 0;
		errorA[0] = 0;

		Snowbnd s = new Snowbnd();
		SNBD_SEARCH_RESULT mSearchResults[] = null;

		try {
			byte extractedText[] = s.IMGLOW_extract_text(inputFilePath, length, error, 0);

			System.out.println("extractedText.length: " + extractedText.length);

			mSearchResults = s.IMGLOW_search_text(extractedText, searchTerm, 0, errorA);
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		if( mSearchResults != null ) {
			System.out.println(" Empty search " + mSearchResults.toString());
		}
		else {
			System.out.println("String not found: " + searchTerm);
			System.out.println("errorA[0].toString(): " + Integer.toString(errorA[0]));
		}

		return 0;
	}

}
