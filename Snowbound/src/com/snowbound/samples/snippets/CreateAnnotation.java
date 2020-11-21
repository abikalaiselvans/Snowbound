/** ******************************************************************
 *
 *	CreateAnnotation
 *
 *	Demonstrates how to create an annotation.
 *
 *	Snowbound Functions Demonstrated:
 *		SANN_create_ann()
 *		SANN_add_object()
 *		SANN_write_ann()
 *		SANN_delete_all_objects()
 *
 *	Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 *
 *******************************************************************/
package com.snowbound.samples.snippets;

import com.snowbound.samples.common.ErrorCodes;

import Snow.SnowAnn;
import Snow.SANN_RECT;


public class CreateAnnotation
{

	/**
	 * This method demonstrates how to create an annotation.
	 *
	 * @param outFileName Name of annotation file.
	 * @param width Width (in inches) of annotation.
	 * @param height Height (in inches) of annotation.
	 * @return Integer. Status of annotation  method.
	 */
	public static int createAnnotation
	(
		String	outFileName,
		double	width,
		double	height
	)
	{
		SnowAnn 	sAnn = null ;	/* Snowbound Annotation object */
		SANN_RECT	rect;				/* bounding rectangle of annotation */
		int			dpi = 200;			/* annotation resolution */
		int			nStatus = 0;		/* return status */

		/* convert inches to pixles at 200 dpi */
		width *= dpi;
		height *= dpi;

		/* create annotation */
		sAnn = new SnowAnn( (int)width, (int)height );

		/* check if valid handle */
		if ( sAnn == null )
		{
			/* there was an error creating annotation */
			System.out.println("Error creating annotation objects");
			/* do something clever here for error */
			return -1;
		}

		/* set desired annotation object fields */
		/* create a red 1" x 8" rectangle and place 10" from top of page */
		sAnn.ann_width = 8 * dpi;
		sAnn.ann_height = 1 * dpi;
		sAnn.ann_rc = new SANN_RECT();
		sAnn.ann_rc.top = 10 * dpi;
		sAnn.ann_rc.left = (int)(0.25 * dpi);
		sAnn.ann_rc.bottom = 11 * dpi;
		sAnn.ann_rc.right = (int)(7.75 * dpi);
		sAnn.ann_fred = 255;
		sAnn.ann_fgreen = 0;
		sAnn.ann_fblue = 0;
		sAnn.ann_bred = 255;
		sAnn.ann_bgreen = 255;
		sAnn.ann_bblue = 255;

		/* set bounding rectangle of annotation */
		rect = new SANN_RECT();
		rect.top = sAnn.ann_rc.top;
		rect.left = sAnn.ann_rc.left;
		rect.bottom = sAnn.ann_rc.bottom;
		rect.right = sAnn.ann_rc.right;

		/* add annotation object to annotation */
		sAnn.SANN_add_object( SnowAnn.SANN_HIGHLIGHT_RECT, rect, null, null, 0 );


		try
		{
			/* write annotation to file */
			nStatus = sAnn.SANN_write_ann( outFileName, 0, null );
			/* check for error */
			if ( nStatus < 0 )
			{
				/* error occurred while trying to save image to desired type */
				System.out.println("Failed to write " + nStatus + ErrorCodes.getErrorMessage(nStatus));
				/* free up annotation object in Snowbound library */
				sAnn.SANN_delete_all_objects( );

				/* do something clever here for error */

				return nStatus;
			}
			else
			{
				System.out.println("File annotation was written");
			}
			/* free up annotation handle in Snowbound library */
			nStatus = sAnn.SANN_delete_all_objects( );

			/* return status */
			//return nStatus;

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		/* return status */
		return nStatus;
		

	}

}