/** ******************************************************************
 *
 *	MergeTextWithImage
 *
 *	Demonstrates how to merge a text string with an image.
 *
 *	Snowbound Functions Demonstrated:
 *		IMG_decompress_bitmap()
 *		IMG_promote_24()
 *		IMG_merge_block()
 *		IMG_save_bitmap()
 *		IMG_delete_bitmap()
 *		IMGLOW_set_ascii_attributes()
 *
 *	Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 *
 *******************************************************************/
package com.snowbound.samples.snippets;

import com.snowbound.samples.common.ErrorCodes;

import Snow.Snowbnd;

public class MergeTextWithImage
{

	/**
	 * This method demonstrates how to merge a text string with an image.
	 *
	 * @param inFileName Name of file to add text string.
	 * @param outFileName Name of merged file.
	 * @param text Text string to add.
	 * @param xPos X position in image to place text.
	 * @param yPos Y position in image to place text.
	 * @param fileType Type of file to save (See Appendix B)
	 * @return Integer. Status of file conversion method.
	 */
	public static int mergeTextWithImage
	(
		String	inFileName,
		String	outFileName,
		String	text,
		int		xPos,
		int		yPos,
		int		fileType
	)
	{
		Snowbnd	sImage = new Snowbnd();		/* Snowbound object for image */
		Snowbnd sText = new Snowbnd();		/* Snowbound for text */
		int statusPage = 0;
		int				nStatus = 0;		/* initialize return status */
		int				nImageHandle = 0;	/* Snowbound image handle */
		int				nTextHandle = 0;	/* Snowbound image hanle for text string */
		int				imageDepth;			/* bits depth of image */


		/* load (read in) file for merge */
		/* NOTE: Snowbound will determine the file type and open accordingly */
		nImageHandle = sImage.IMG_decompress_bitmap( inFileName, 0 );

		/* check if valid image handle */
		if ( nImageHandle < 0 )
		{
			/* if nImageHandle less than 0 there was an error decompressing file */
			/* do something clever here for error */
			statusPage = nImageHandle;
			System.out.println("Image decompression failed " + ErrorCodes.getErrorMessage(nStatus));
			return nImageHandle;
		}
		else
		{
			System.out.println("File handle status: " + nImageHandle);
		}

		/* get image bit depth */
		imageDepth = sImage.getBitsPerPixel();

		/* set text attributes */
		sText.IMGLOW_set_ascii_attributes(	200,		// xdpi
											200,		// ydpi
											0,			// left margin (1/1000 inch)
											0,			// right margin (1/1000 inch)
											0,			// top margin (1/1000 inch)
											0,			// bottom margin (1/1000 inch)
											0,			// number of spaces for a tab
											8500,		// page width (1/1000 inch)
											11000,		// page height (1/1000 inch)
											50,			// font height in pixels
											72,			// characters per line
											66,			// lines per page
											false,		// bold
											false,		// italic
											"Arial",	// font name
											0,			// line spacing
											0 );		// character spacing


		/* create image from text string */
		byte[] buff = text.getBytes();
		java.io.DataInputStream di = new java.io.DataInputStream( new java.io.ByteArrayInputStream( buff ) );

		/* forcing UTF8 decompression is required for some text files */
		sText.IMGLOW_set_UTF_8(1);

		try
		{
			nTextHandle = sText.IMG_decompress_bitmap( di , 0 );
			/* check if valid handle */
			if ( nTextHandle < 0 )
			{
				/* turn off UTF8 decompression */
				sText.IMGLOW_set_UTF_8(0);

				/* if nImageHandle less than 0 there was an error decompressing file */
				/* do something clever here for error */
				System.out.println("Error decompressing file " + ErrorCodes.getErrorMessage(nStatus));
				return nTextHandle;
			}
			else
			{
				System.out.println("Decompression status: " + nTextHandle);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		


		/* make text image bit depth = image bit depth */
		if ( imageDepth == 24 )
		{
			sText.IMG_promote_24();
		}

		try
		{
			/* merge the text bitmap onto the image */
			nStatus = sImage.IMG_merge_block( sText, xPos, yPos, 2 );
			if ( nStatus < 0 )
			{
				/* error occurred while trying to merge  */
				statusPage = nStatus;
				System.out.println("Error occured while trying to merge" + ErrorCodes.getErrorMessage(nStatus));

				/* do something clever here for error */

				return nStatus;
			}
			else{
				System.out.println("Merge successful");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		

		try{
			/* save file to desired file type */
			/* NOTE: See Appendix B of manual for a complete list of file type constants */
			nStatus = sImage.IMG_save_bitmap( outFileName, fileType );
			/* check for error */
			if ( nStatus < 0 )
			{
				/* error occurred while trying to save image to desired type */
				statusPage = nStatus;
				
			/* do something clever here for error */
				System.out.println("Failed to save " + ErrorCodes.getErrorMessage(nStatus));

				return nStatus;
			}
			else
			{
				System.out.println("Saving File: " + nStatus);
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

