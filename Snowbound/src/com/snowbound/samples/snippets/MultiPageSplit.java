/** ******************************************************************
 *
 *	MultiPageSplit
 *
 *	Demonstrates how to split a multi-page image into multiple single
 *	page images.
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


public class MultiPageSplit
{

	/**
	 * This method demonstrates how to convert a file from one type to another.
	 *
	 * @param inFileName Name of file to convert.
	 * @param outBaseName Base name of converted file.
	 * @param outExt Output file extenstion.
	 * @param fileType Type of file to save (See Appendix B)
	 * @return Integer. Status of file conversion method.
	 */
	public static int multiPageSplit
	(
		String	inFileName,
		String	outBaseName,
		String	outExt,
		int		fileType
	)
	{
		Snowbnd	sImage = new Snowbnd();		/* Snowbound object */
		int		nStatus = 0;				/* initialize return status */
		int		nPage = 0;					/* current page number */
		int		nImageHandle = 0;			/* Snowbound image handle */
		String	outFileName;				/* output file name */

		/* load (read in) each page of multi-page input file */
		/* NOTE: Snowbound will determine the file type and open accordingly */
		while ( nImageHandle >= 0 )
		{
			try
			{
				//nPage++;
				nImageHandle = sImage.IMG_decompress_bitmap( inFileName, nPage );
				/* check if valid image handle */
				if ( 0 <= nImageHandle )
				{
					/* output filename = szOutBaseName + nPage + szOutExt */
					outFileName = outBaseName + nPage + outExt;

					/* save file to desired file type */
					/* NOTE: See Appendix B of manual for a complete list of file type constants */
					nStatus = sImage.IMG_save_bitmap( outFileName, fileType );

					/* check for error */
					if ( nStatus < 0 )
					{
						/* error occurred while trying to save image to desired type */
						/* set image handle to error returned from save */
						System.out.println("Failed to save file type " + ErrorCodes.getErrorMessage(nPage));
						nImageHandle = nStatus;

						/* do something clever here for error */
					}
					else
					{
						System.out.println("MultipageJoin status: " + nImageHandle);
						/* increment page number */
						nPage++;
					}

				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
					
		}

		/* return status */
		return nImageHandle;

	}

}