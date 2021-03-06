/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JPanel;

import Snow.FormatHash;
import Snow.Snowbnd;

/**
 * A Panel with methods that display and manipulate the Snowbound image.
 */
public class SnowboundJPanel extends JPanel
{
	private static final long serialVersionUID = -2912598021946878835L;
	
    protected Snowbnd Simage;
    
    protected FormatHash formatHash;
    
    protected int zoom = 0;
    protected int stat = 0;
    protected int flag = 0;
    protected int cropped = 0;
    protected Dimension d;
    
	protected int page = 0;
	protected int totalPages = 0;
	
	protected byte textbuff[];
	
	protected Component cp[];
	
    protected String filename;
    
    /**
     * Constructs a SnowJPanel object with the BorderLayout
     */
	public SnowboundJPanel()
	{
    	Simage = new Snowbnd();
		
		formatHash = FormatHash.getInstance();
		
		setLayout(new BorderLayout());
	}

	/**
	 * Decompress the specified image file and displays it on the panel
	 * 
	 * @param fileName - The name of the image file to decompress
	 * @return The status of decompression
	 */
    public int decompressImage(String st)
	{
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
                    ysize = d.height;
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

			// ERASE BACKGROUND HERE
			// OR, CALL UPDATE HERE, BEFORE YOU DO THE PAINT.
	
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
		    	// Below if statement forces the scroll-bars to display when
                // image reaches its limit
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
		    	Simage.IMG_display_bitmap(g, 0, 0, d.width, d.height);
		    }
		}
        //!****************************************************************************

		//ERASE BACKGROUND HERE
		//OR, CALL UPDATE HERE, BEFORE YOU DO THE PAINT.
		//Needed to Properly Use Swing Scrollbars
		
		super.paintChildren(g);
		
        //!****************************************************************************
	}

	/**
	 * Zooms in the image by the specified increment
	 * 
	 * @param inc - The increment to zoom in the image
	 */
	public void zoomIn(int inc)
	{
		flag = 1;
		cropped = 0;
		d = getSize();
		
		if (zoom == 0)
		{
			zoom = (int)(Simage.getInitialZoom(d.width, d.height));
		}
	 	else
	 	{
	 		zoom += inc;
	 	}
	}
	
	/**
	 * Zooms out the image by the specified increment
	 * 
	 * @param inc - The increment to zoom out the image
	 */	
	public void zoomOut(int inc)
	{
		flag = 0;
		cropped = 0;
		d = getSize();

		if (zoom > (int)(Simage.getInitialZoom(d.width, d.height)))
		{
			zoom -= inc;
		}
		else
		{
			zoom = 0;
		}
	}
	
	/**
	 * Displays the image rotated by the specified angle
	 * 
	 * @param angle - The angle to rotate the image
	 */
	public void displayAngle(int angle)
	{
        int xsize,ysize;

		Simage.display_angle(angle);
        if (angle == 90 || angle == 270)
        {
            ysize = Simage.getWidth();
		    xsize = Simage.getHeight();
        }
        else
        {
            xsize = Simage.getWidth();
		    ysize = Simage.getHeight();
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
                ysize = d.height;
        }
		
    	d = getSize();
        if (xsize == d.width && ysize == d.height)
		{
            repaint();
		}
        
		repaint();
    }

	/**
	 * Displays the next page in the document or an error message
	 * if there is no next page.
	 */
    public void nextPage()
    {
    	if (page < totalPages)
    	{
    		page++;
    	}
        	
        decompressImage(filename);
    }

    /**
     * Displays the previous page in the document or an error message
     * if there is no previous page.
     */
    public void prevPage()
    {
        if (page > 0)
        {
        	page--;
        }

        decompressImage(filename);
    }

    /**
     * Maps the point's window coordinates to image coordinates.
     * 
     * @param pt - The Point being mapped
     */
    public void mapWindowToImage(Point pt)
    {
    	Simage.map_wnd_to_image(this, pt);
    }
    
    /**
     * Maps the point's image coordinates to window coordinates.
     * 
     * @param pt - The Point being mapped
     */
    public void mapImageToWindow(Point pt)
    {
    	Simage.map_image_to_wnd(this, pt);
    }
    
    /**
     * Gets the current page number.
     * 
     * @return - The current page number
     */
    public int getPage()
    {
    	return page;
    }
    
    /**
     * Gets the total number of pages in the file
     * 
     * @return - The total number of pages
     */
    public int getTotalPages()
    {
    	return totalPages;
    }
    
    /**
     * Returns the Snowbnd image object.
     * 
     * @return The Snowbnd image object
     */
    public Snowbnd getSimage()
    {
    	return Simage;
    }
    
    /**
     * Returns the FormatHash object.
     * 
     * @return - The FormatHash object
     */
    public FormatHash getFormatHash()
    {
    	return formatHash;
    }
}
