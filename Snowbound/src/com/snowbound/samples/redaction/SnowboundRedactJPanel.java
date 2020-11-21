/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.redaction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import Snow.Defines;

import com.snowbound.samples.common.SnowboundJPanel;

/**
 * A SnowboundJPanel that keeps track of the Redact Rectangles on each page.
 */
public class SnowboundRedactJPanel extends SnowboundJPanel
{
	private static final long serialVersionUID = -2912598021946878835L;
	
	public static int PDF_DPI = 300;
	public static int PDF_BIT_DEPTH = 24;

    private HashMap<Integer, ArrayList<Rectangle>> rectMap;
    
    /**
     * Constructs a SnowJPanel object with the BorderLayout
     */
	public SnowboundRedactJPanel()
	{
		super();
		
		rectMap = new HashMap<Integer, ArrayList<Rectangle>>();
		rectMap.put(0, new ArrayList<Rectangle>());
	}
	
	/**
	 * Decompress the specified image file and displays it on the panel.
	 * 
	 * @param fileName - The name of the image file to decompress
	 * @return The status of decompression
	 */
    public int decompressImage(String st)
	{
    	Simage.IMGLOW_set_document_input(PDF_DPI, PDF_BIT_DEPTH, Defines.PDF);
    	Simage.alias = 4;

        if (filename != null && filename != st)
        {
        	page = 0;
        }

        Simage.setFrame((JPanel)getParent());
        
	    stat = Simage.IMG_decompress_bitmap(st, page);

		if (stat >= 0)
		{
			int	xsize, ysize;

		    filename = st;
			zoom = 0;
            cropped = 0;
            
		    totalPages = Simage.IMGLOW_get_pages(filename);
		    
			xsize = Simage.getWidth();
			ysize = Simage.getHeight();
		    
			for (int page = 0; page < totalPages; page++)
			{
				rectMap.put(page, new ArrayList<Rectangle>());
			}
		    
            if (Simage.getBitsPerPixel() == 16)
            {
            	Simage.IMG_window_level(0, 0, 1);
            }
                
			d = getSize();
			if (ysize > d.height || xsize > d.width)
			{
                d.width  -= 100;
                d.height -= 100;
                
				xsize = (xsize * d.height) / ysize;
				
    			if (xsize > d.width)
                {
                    ysize = (ysize * d.width) / Simage.getWidth();
    			    xsize = d.width;
                }
                else
                {
                    ysize = d.height;
                }
			}

            d = getSize();
            if (d.width == xsize && d.height == ysize)
            {
            	repaint();
            }              
	    }
		else
		{      
            new Snow.MessageBox(null,"Error decompressing Image", true);
		}
		
		updateUI();
		
        return stat;
	}
    
	/*!
	****************************************************************************
	****************************************************************************
	****************************************************************************

		NOTE THAT THE UPDATE METHOD FOR JCOMPONENTS DOES NOT CLEAR THE
		BACKGROUND.  IN OUR EXPERIENCE, IT IS NOT EVEN CALLED.
		IF YOU NEED TO USE THE UPDATE METHOD, BE SURE TO USE THE
		COMPONENTUI UPDATE METHOD WHICH TAKES TWO ARGUMENTS, GRAPHICS G AND
		JCOMPONENT C.  ALSO, BE SURE TO CALL UPDATE BEFORE DOING A PAINT.
		BE SURE TO ERASE THE BACKGROUND IN YOUR PAINT METHOD.  SEE THE JDK1.2.1
		DOCUMENTATION FOR CLARIFICATION OF THE UPDATE METHOD.

	****************************************************************************
	****************************************************************************
	****************************************************************************
	*/
	public void paint(Graphics g)
	{
		if (Simage != null)
		{
			//!****************************************************************************

			//ERASE BACKGROUND HERE
			//OR, CALL UPDATE HERE, BEFORE YOU DO THE PAINT.
	
			// The whole if statement below is needed if your not forcing the
	        // scroll-bars to show using a fit_to_? snow method
		
			if((zoom == 0) || (Simage.hsb == null && Simage.vsb == null))
			{
				d = getSize();
				g.setColor(getBackground());
				g.fillRect(0, 0, d.width, d.height);
				g.setColor(getForeground());
			}
	
			//!****************************************************************************
	
		    d = getSize();
		    Insets in = getInsets();
		    d.width -= (in.right + in.left);
		    d.height -= (in.top + in.bottom);
		
		    int hsbheight_vsbwidth = 17;
		    int forcevsb = (((zoom * Simage.dis_width)/100) + hsbheight_vsbwidth);
		    int forcehsb = (((zoom * Simage.dis_height)/100) + hsbheight_vsbwidth);
		
		    if (cropped == 0)
		    {
		    	// Below if statement forces the scroll-bars to display when image
                // reaches its limit
		    	if ((forcevsb <= d.width) && (forcevsb != hsbheight_vsbwidth))
		    	{
					Simage.fit_to_height(0);
					Simage.fit_to_width(1);
		    	}
		    	else if ((forcehsb <= d.height) && (forcehsb != hsbheight_vsbwidth))
		    	{
			      	Simage.fit_to_width(0);
			      	Simage.fit_to_height(1);
		    	}
		    	else
		    	{
					Simage.fit_to_width(0);
					Simage.fit_to_height(0);
		    	}
		    	stat = Simage.IMG_display_bitmap_aspect(g,this,0,0,d.width,d.height,zoom);
		    }
		    else
		    {
		    	Simage.IMG_display_bitmap(g,0,0,d.width,d.height);
		    }
		}
        //!****************************************************************************

		//ERASE BACKGROUND HERE
		//OR, CALL UPDATE HERE, BEFORE YOU DO THE PAINT.
		//Needed to Properly Use Swing Scrollbars
		
		super.paintChildren(g);
		
        //!****************************************************************************
		
		for (Rectangle rect : rectMap.get(page))
		{
			g.setXORMode(Color.WHITE);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
			g.setPaintMode();
		}
	}
	
	/**
	 * Adds an ArrayList of Rectangles on the current page to the rectMap.
	 * 
	 * @param rects - An ArrayList of Rectangles on the current page
	 */
	public void addRectsToRectMap(ArrayList<Rectangle> rects)
	{
		for (Rectangle rect : rects)
		{
			rectMap.get(page).add(rect);
		}
	}
	
	/**
	 * Clears the ArrayList of Rectangles for each page.
	 */
	public void clearRectMap()
	{
		for (ArrayList<Rectangle> ral : rectMap.values())
		{
			ral.clear();
		}
	}
}
