/** ******************************************************************
 *
 *	MergeImages
 *
 *	Demonstrates how to merge two images.
 *
 *	Snowbound Functions Demonstrated:
 *		IMG_decompress_bitmap()
 *		IMG_promote_24()
 *		IMG_merge_block()
 *		IMG_save_bitmap()
 *
 *	Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 *
 *******************************************************************/
package com.snowbound.samples.snippets;

import Snow.ErrorCodes;
import Snow.Snowbnd;

public class MergeImages
{

	/**
	 * This method demonstrates how to merge tow images.
	 *
	 * @param inFile1 Name of first file to merge.
	 * @param inFile2 Name of second file to merge.
	 * @param outFileName Name of output file.
	 * @param fileType Type of file to save (See Appendix B)
	 * @return Integer. Status of file conversion method.
	 */
	public static int mergeImages
	(
		String	inFile1,				/* name of first file to merge */
		String	inFile2,				/* name of second file to merge */
		String	outFileName,			/* name of merged file */
		int		fileType				/* type of file to save (Appendix B) */
	)
	{
		Snowbnd	sImage1 = new Snowbnd();	/* Snowbound object for image */
		Snowbnd	sImage2 = new Snowbnd();	/* Snowbound object for image */
		int				nStatus = 0;		/* initialize return status */
		int				imageHandle1 = 0;	/* Snowbound image handle */
		int				imageHandle2 = 0;	/* Snowbound image handle */
		int				imageDepth1 = 0;		/* bits depth of image */
		int				imageDepth2;		/* bits depth of image */


		/* load (read in) first file for merge */
		/* NOTE: Snowbound will determine the file type and open accordingly */
		try
		{
			imageHandle1 = sImage1.IMG_decompress_bitmap( inFile1, 0 );
			/* check if valid image handle */
			if ( imageHandle1 < 0 )
			{
				/* if nImageHandle less than 0 there was an error decompressing file */
				/* do something clever here for error */
				System.out.println("Error decompressing file " + ErrorCodes.getErrorMessage(imageHandle1));
				
				return imageHandle1;
				
			}
			else
			{
				System.out.println(" decompressed status: " + imageHandle1);
			}
			/* get image bit depth */
			imageDepth1 = sImage1.getBitsPerPixel();

			/* load (read in) second file for merge */
			/* NOTE: Snowbound will determine the file type and open accordingly */
			imageHandle2 = sImage2.IMG_decompress_bitmap( inFile2, 0 );
			if(imageHandle2 < 0)
			{
				System.out.println("Error decompressing file " + ErrorCodes.getErrorMessage(imageHandle2));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		
		/* check if valid image handle */
		if ( imageHandle2 < 0 )
		{
			/* if nImageHandle less than 0 there was an error decompressing file */
			/* do something clever here for error */
			System.out.println("Error decompressing" + imageHandle2 + ErrorCodes.getErrorMessage(imageHandle2));
			
			return imageHandle2;
		}
		else
		{
			System.out.println("Decompression status: " + imageHandle2);
		}

		/* get image bit depth */
		imageDepth2 = sImage2.getBitsPerPixel();

		/* make bit depths compatible */
		if ( imageDepth1 != imageDepth2 )
		{
			if ( imageDepth1 < 24 )
			{
				sImage1.IMG_promote_24();
			}

			if ( imageDepth2 < 24 )
			{
				sImage2.IMG_promote_24();
			}
		}
		
		try
		{
			/* merge the text bitmap onto the image */
			nStatus = sImage1.IMG_merge_block( sImage2, 0, 0, 2 );

			if ( nStatus < 0 )
			{
				/* error occurred while trying to merge  */

				/* do something clever here for error */
				System.out.println("Error caught during merge " + ErrorCodes.getErrorMessage(nStatus));

				return nStatus;
			}
			else
			{
				System.out.println("Merging image");
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
			nStatus = sImage1.IMG_save_bitmap( outFileName, fileType );
			/* check for error */
			if ( nStatus < 0 )
			{
				/* error occurred while trying to save image to desired type */

				/* do something clever here for error */
				System.out.println("Error saving current file" + ErrorCodes.getErrorMessage(nStatus));

				return nStatus;
			}
			else
			{
				System.out.println("Saving file status: " + nStatus );
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

