/** ******************************************************************
 *
 *	CreateThumbnail
 *
 *	Demonstrates how to create a thumbnail of a file.
 *
 *	Snowbnd Methods Demonstrated:
 *		IMG_decompress_bitmap()
 *		IMG_create_thumbnail()
 *		IMG_save_bitmap()
 *
 *	Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 *
 *******************************************************************/
package com.snowbound.samples.snippets;

import com.snowbound.samples.common.ErrorCodes;

import Snow.Snowbnd;

public class CreateThumbnail
{

	/**
	 * This method demonstrates how to create a thumbnail.
	 *
	 * @param inFileName Name of file to convert.
	 * @param outFileName Name of thumbnail file.
	 * @param nWidth Width of thumbnail.
	 * @param nHeight Height of thumbnail.
	 * @param fileType Type of file to save (See Appendix B)
	 * @return Integer. Status of file conversion method.
	 */
	public static int createThumbnail
	(
		String[]	inFileName,				/* name of file to convert */
		String	outFileName,			/* name of thumbnail file */
		int		nWidth,					/* width of thumbnail */
		int		nHeight,				/* height of thumbbail */
		int		fileType				/* type of file to save (Appendix B) */
	)
	{
		Snowbnd	sImage = new Snowbnd();	/* Snowbound object */
		int		nStatus = 0;			/* initialize return status */
		int		nImageHandle = 0;		/* Snowbound image handle */
		int 	count = 0;

		for(int i = 0; i < count; i++)
		{
			//inFileName = inFileList[i];
			/* load (read in) file for conversion */
			/* NOTE: Snowbound will determine the file type and open accordingly */
			nImageHandle = sImage.IMG_decompress_bitmap( inFileName[i], 0 );
			/* check if valid image handle */
			if ( nImageHandle < 0 )
			{
				/* if nImageHandle less than 0 there was an error decompressing file */
				/* do something clever here for error */
				System.out.println("Error decompressing image " + ErrorCodes.getErrorMessage(nImageHandle));
				return nImageHandle;
			}
			else
			{
				System.out.println("Decompression successful: " + nImageHandle);
			}
		}
		
		try
		{
			/* create thumbnail */
			nStatus = sImage.IMG_create_thumbnail( nWidth, nHeight );
			if(nStatus >= 0){
				sImage.IMG_save_bitmap(outFileName, fileType);
			}
			/* check for error */
			if ( nStatus < 0 )
			{
				/* error occurred while trying to save image to desired type */

				/* do something clever here for error */
				System.out.println("Failed to creat thumbnail " + ErrorCodes.getErrorMessage(nStatus));

				return nStatus;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		


		try
		{
			/* save file to desired file type */
			/* NOTE: See Appendix B of manual for a complete list of file type constants */
			nStatus = sImage.IMG_save_bitmap( outFileName, fileType );
			/* check for error */
			if ( nStatus < 0 )
			{
				
				/* error occurred while trying to save image to desired type */
				System.out.println("Error saving page" + ErrorCodes.getErrorMessage(nStatus));
                        
				/* do something clever here for error */

				return nStatus;
			}
			else
			{
				System.out.println("Saving image: " + nStatus);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		/* return status */
		return nStatus;

	}

}

