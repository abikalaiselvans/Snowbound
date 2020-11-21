/** ******************************************************************
 *
 *	ConvertFromFile
 *
 *	Demonstrates how to convert a file from one type to another.
 *
 *	Snowbnd Methods Demonstrated:
 *		IMG_decompress_bitmap()
 *		IMG_save_bitmap()
 *
 *	Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 *
 *******************************************************************/

package com.snowbound.samples.snippets;

import com.snowbound.samples.common.ErrorCodes;

import Snow.Snowbnd;


public class ConvertFromFile
{

	/**
	 * This method demonstrates how to convert a file from one type to another.
	 *
	 * @param inFileName Name of file to convert.
	 * @param outFileName Name of converted file.
	 * @param fileType Type of file to save (See Appendix B)
	 * @return Integer. Status of file conversion method.
	 */
	public static int convertFromFile
	(
		String	inFileName,
		String	outFileName,
		int		fileType
	)
	{
		Snowbnd	sImage = new Snowbnd();	/* Snowbound object */
		int		nStatus = 0;			/* initialize return status */
		
		try
		{
			/* load (read in) file for conversion */
			/* NOTE: Snowbound will determine the file type and open accordingly */
			nStatus = sImage.IMG_decompress_bitmap( inFileName, 0 );
			/* check for valid return status */
			if ( nStatus >= 0 )
			{
				/* save file to desired file type */
				/* NOTE: See Appendix B of manual for a complete list of file type constants */
				System.out.println("Decompressing bitmap status: " + nStatus);
				nStatus = sImage.IMG_save_bitmap( outFileName, fileType );
				/* check for error */
				if ( nStatus < 0 )
				{
					/* error occurred while trying to save image to desired type */
					/* do something clever here for error */
					System.out.println("Failed to save " + nStatus + ErrorCodes.getErrorMessage(nStatus));
				}
				else
				{
					System.out.println("Saving Bitmap status: " + nStatus);
				}
			}
			else
			{
				System.out.println("Decompression Error: " + ErrorCodes.getErrorMessage(nStatus));
			}

			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		

		return nStatus;

	}

}