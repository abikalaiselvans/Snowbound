/** ******************************************************************
 *
 *	MergeAnnotaionWithImage
 *
 *	Demonstrates how to merge an annotaion with an image.
 *
 *	Snowbound Functions Demonstrated:
 *		IMG_decompress_bitmap()
 *		IMG_promote_24()
 *		IMG_display_bitmap()
 *		IMG_save_bitmap()
 *		SANN_read_ann()
 *		SANN_set_croprect()
 *		SANN_display_annotations()
 *		SANN_delete_all_objects()
 *
 *	Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 *
 *******************************************************************/
package com.snowbound.samples.snippets;

import Snow.ErrorCodes;
import Snow.Snowbnd;
import Snow.SnowAnn;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;


public class MergeAnnotationWithImage
{

	/**
	 * This method demonstrates how to convert a file from one type to another.
	 *
	 * @param inFileName Name of file to convert.
	 * @param annFileName Name of file to convert.
	 * @param outFileName Name of converted file.
	 * @param xPos X position (in pixels) to put annotaion.
	 * @param yPos Y position (in pixels) to put annotaion.
	 * @param fileType Type of file to save (See Appendix B)
	 * @return Integer. Status of file conversion method.
	 */
	public static int mergeAnnotationWithImage
	(
		String	inFileName,
		String	annFileName,
		String	outFileName,
		int		xPos,
		int		yPos,
		int		fileType
	)
	{
		Snowbnd	sImage = new Snowbnd();	/* Snowbound object */
		SnowAnn sAnn = null;			/* Snowbound Annotation object */
		int		nStatus = 0;			/* initialize return status */
		int     statusPage = 0;
		int		width = 0;					/* width of image */
		int		height = 0;					/* height of image */
		int		depth = 0;					/* bits depth of image */
		Graphics g = null;						/* graphics object used for merging */
		Image image = null;			/* bitmap used for merging */


		/* load (read in) file for conversion */
		/* NOTE: Snowbound will determine the file type and open accordingly */
		try
		{
			nStatus = sImage.IMG_decompress_bitmap( inFileName, 0 );
			/* check if valid image handle */
			if ( nStatus < 0 )
			{
				/* if nImageHandle less than 0 there was an error decompressing file */
				/* do something clever here for error */
				statusPage = nStatus;
				System.out.println("Error decompressing file " + ErrorCodes.getErrorMessage(nStatus));
				return nStatus;
			}
			else{
				System.out.println(" Decompression status: " + nStatus);
		              
			}
			/* get image information */
			width = sImage.getWidth();
			height = sImage.getHeight();
			depth = sImage.getBitsPerPixel();

			/* load (read in) annotation file */
			sAnn = new SnowAnn( width, height );
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		

		/* check if valid handle */
		if ( sAnn == null  )
		{
			/* error creating Snowbound Annotation object */
			System.out.println("Caught error in annotation object");
			return -1;
		}

		try{
			nStatus = sAnn.SANN_read_ann( annFileName, 0 );
			if ( nStatus < 0 )
			{
				/* there was an error reading file */
				/* do something clever here for error */
				statusPage = nStatus;
				System.out.println("Error during read annotation " + ErrorCodes.getErrorMessage(nStatus));
				return nStatus;
			}
			else{
				System.out.println("Annotations was read " + nStatus);	
			}
			/* create graphics device context, bitmap for merging */
			image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
	        g = image.getGraphics();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		/* make image bit depth = annotation bit depth */
		if ( depth < 24 )
		{
			nStatus = sImage.IMG_promote_24( );
		}

		/* set crop rectangle for image and annotation */
        sImage.set_croprect( 0, 0, width, height );
        sAnn.SANN_set_croprect( 0, 0, width, height );

        /* copy image data to device context */
		nStatus = sImage.IMG_display_bitmap( g, 0, 0, width, height );

		/* copy annotation data to device context */
		nStatus = sAnn.SANN_display_annotations( g, null, 0, 0, width, height );

		/* create new Snowbound image handle with merged data */
		nStatus = sImage.IMG_decompress_bitmap( image, 24 );

		try
		{
			/* save file to desired file type */
			/* NOTE: See Appendix B of manual for a complete list of file type constants */
			nStatus = sImage.IMG_save_bitmap( outFileName, fileType );
			int totalPageCount = sImage.IMGLOW_get_pages(inFileName);
			for(int pageCount = 0; pageCount < totalPageCount; pageCount++)
			{
				nStatus = sImage.IMG_save_bitmap( outFileName, pageCount );
				/* check for error */
				if ( nStatus < 0 )
				{
					/* error occurred while trying to save image to desired type */
					/* free up image handles in Snowbound library */
					sAnn.SANN_delete_all_objects( );

					/* do something clever here for error */
					statusPage = nStatus;
					System.out.println("Error saving file type" + ErrorCodes.getErrorMessage(nStatus));
					return nStatus;
				}
				else
				{
					System.out.println("Current" + nStatus + "saved successfully " );
				}
			}	
			/* free up image handles in Snowbound library */
			nStatus = sAnn.SANN_delete_all_objects( );
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		/* return status */		
		return nStatus;

	}

}