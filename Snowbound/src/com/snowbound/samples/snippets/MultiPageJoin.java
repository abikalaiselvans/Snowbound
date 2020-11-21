/** ******************************************************************
 *
 *	MultiPageJoin
 *
 *	Demonstrates how to 'join' multiple single and multi-page images
 *	into one multi-page image.
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


public class MultiPageJoin
{

	/**
	 * This method demonstrates how to join multiple single and
	 * multi-page image into one multi-page image.
	 *
	 * @param inFileList List of input files to convert.
	 * @param nCount Number of files in list.
	 * @param outFileName Name of joined file.
	 * @param fileType Type of file to save (See Appendix B)
	 * @return Integer. Status of file conversion method.
	 */
	public static int multiPageJoin
	(
		String[]	inFileList,				/* list of input file names */
		int			nCount,					/* number of files in list */
		String		outFileName,			/* name of output file */
		int			fileType				/* save image file format */
	)
	{
		Snowbnd	sImage = new Snowbnd();		/* Snowbound object */
		int		nImageHandle = 0;			/* Snowbound image handle */
		int		nPage = 0;					/* current page number */
		int		nStatus = 0;				/* initialize return status */
		String	inFileName;					/* pointer to current input file name */
		int		i;


		for ( i = 0; i < nCount; i++ )
		{
			/* point to next file name in the list */
			inFileName = inFileList[i];

			/* load (read in) each page of input file */
			/* NOTE: Snowbound will determine the file type and open accordingly */
			while ( nImageHandle >= 0 )
			{
				nImageHandle = sImage.IMG_decompress_bitmap( inFileName, nPage );

				/* check if valid image handle */
				if ( 0 <= nImageHandle )
				{
					try
					{
						/* save file to desired file type */
						/* NOTE: See Appendix B of manual for a complete list of file type constants */
						/* NOTE: if nFileType is a valid multi-page format this image will be appended */
						nStatus = sImage.IMG_save_bitmap( outFileName, fileType );
						/* check for error */
						if ( nStatus < 0 )
						{
							/* error occurred while trying to save image to desired type */
							System.out.println(" Error saving page = " + i + ": " + ErrorCodes.getErrorMessage(nStatus));
							//continue; //continue to the next file in the list

							/* set image handle to error returned from save */
							nImageHandle = nStatus;

							/* do something clever here for error */
						}
						else
						{
							/* increment page number */
							nPage++;
						}

					}
					catch(Exception ex)
					{
						System.out.println("Error thrown on file " + i);
					}

				}

			}

			/* reset image handle for each file */
			nImageHandle = 0;

			/* reset page count to zero for each file */
			nPage = 0;

		}

		/* return status */
		return nImageHandle;

	}

}
