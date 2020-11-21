/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import Snow.Snowbnd;
/*  Only for testing decompress from a datainputstream  */

public class SwingSnowFrame extends JPanel implements MouseListener, MouseMotionListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2912598021946878835L;
	Component cp[];
    int flag = 0;
    public Snowbnd Simage = null;
    public Snowbnd tSimage = null;

	int stat = 0;
	//Insets in;
	byte textbuff[];
    int zoom = 0;
	Dimension d;
    int page = 0;
    String filename = null;
    static boolean    select = false;
    static Rectangle selrect = null;
    int cropped = 0;


	public SwingSnowFrame()
	{

		setLayout(new BorderLayout());
		addMouseListener(this);
		addMouseMotionListener(this);

	}

    public int Decompress(String st)
	{

    	 tSimage = new Snowbnd();


        if (filename != null)
            if (filename != st)
                page = 0;


//The following is for reading from a DataInputStream

/*
		RandomAccessFile ras;
        int length;
        byte buff[];

        try
        {
            ras = new RandomAccessFile (st,"r");
            length = (int)ras.length();
            buff = new byte[length];
            ras.readFully(buff,0,length);
        } catch (IOException ioe)
        {
            return -2;
        }

	DataInputStream di;
        di = new DataInputStream(new ByteArrayInputStream(buff));

        stat = tSimage.IMG_decompress_bitmap(di,page);

*/

        tSimage.setFrame((JPanel)getParent());

	    stat = tSimage.IMG_decompress_bitmap(st,page);

		if (stat >= 0)
		{
			int	xsize,ysize;

            if (Simage != null)
				Simage.finalize();



            Simage = tSimage;

		    filename = st;
			zoom = 0;
            cropped = 0;
            select = false;
            selrect = null;
			xsize = Simage.getWidth();
			ysize = Simage.getHeight();
            if (Simage.getBitsPerPixel() == 16)
                Simage.IMG_window_level(0,0,1);

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
                repaint();
	    }
		else
		{
	        Snow.MessageBox msg;

		    if (page != 0)
		        page--;

            msg = new Snow.MessageBox(null,"Error decompressing Image",true);

		}
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
	public void paint (Graphics g)
	{
		if (Simage != null)
		{


	//!****************************************************************************

			//ERASE BACKGROUND HERE
			//OR, CALL UPDATE HERE, BEFORE YOU DO THE PAINT.

                        // The whole if statement below is needed if your not forcing the
                        // scroll-bars to show using a fit_to_? snow method
                        if((zoom == 0) || (Simage.hsb == null && Simage.vsb ==null))
                        {

                                d = getSize();
				g.setColor(getBackground());
				g.fillRect(0,0,d.width,d.height);
				g.setColor(getForeground());

			}

	//!****************************************************************************


            d = getSize();
            Insets in = getInsets();
            d.width -= (in.right + in.left);
            d.height -= (in.top + in.bottom);

            int hsbheight_vsbwidth = 17;
            int forcevsb = (((zoom * Simage.dis_width)/100)+hsbheight_vsbwidth);
            int forcehsb = (((zoom * Simage.dis_height)/100)+hsbheight_vsbwidth);

            if (cropped == 0)
            {
              //Below if statement forces the scroll-bars to display when image reaches its limit
              if((forcevsb <= d.width) && (forcevsb != hsbheight_vsbwidth))
              {
                Simage.fit_to_height(0);
                Simage.fit_to_width(1);
              }
              else if((forcehsb <= d.height) && (forcehsb != hsbheight_vsbwidth))
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
                 Simage.IMG_display_bitmap(g,0,0,d.width,d.height);

            if (select)
                snbd_draw_pen();

        }
        //!****************************************************************************

			//ERASE BACKGROUND HERE
			//OR, CALL UPDATE HERE, BEFORE YOU DO THE PAINT.

                        //Needed to Properly Use Swing Scrollbars
            super.paintChildren(g);
        //!****************************************************************************



	}



	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ END OLD EVENT MODEL $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$


	//######################################################################################################
	//########################## THE FOLLOWING CODE USES THE JDK1.1 EVENT MODEL. ###########################
	//######################################################################################################


	public void mouseMoved(MouseEvent e)
	{
	}


	public void mouseDragged(MouseEvent e)
	{
		if (select)
		{
			if (e.getX() > selrect.x && e.getY() > selrect.y)
			{
				snbd_draw_pen();
				selrect.width  = e.getX() - selrect.x;
				selrect.height = e.getY() - selrect.y;
				snbd_draw_pen();
			}
		}

	}

	public void mousePressed(MouseEvent e)
	{

		if (select)
		{

			selrect = new Rectangle();
			selrect.x       = e.getX();
			selrect.y       = e.getY();
			selrect.width   = 5;
			selrect.height  = 5;
			snbd_draw_pen();
		}
	}

	public void mouseReleased(MouseEvent e)
	{

		Point pt,pt2;

		if (select)
		{
			snbd_draw_pen();
			pt = new Point();
			pt2 = new Point();
			pt.x = selrect.x;
			pt.y = selrect.y;
			Simage.map_wnd_to_image(this,pt);

			pt2.x = selrect.x + selrect.width;
			pt2.y = selrect.y + selrect.height;
			Simage.map_wnd_to_image(this,pt2);

			if(pt.x > pt2.x)
			{
				selrect.x = pt.x;
				pt.x = pt2.x;
				pt2.x = selrect.x;
			}

			if(pt.y > pt2.y)
			{
				selrect.y = pt.y;
				pt.y = pt2.y;
				pt2.y = selrect.y;
			}

			zoom = Simage.set_croprect_scroll(this,pt.x,pt.y,pt2.x - pt.x,pt2.y - pt.y,1);
			repaint();
			if (zoom <0)
				zoom = 0;
			cropped = 1;
			select = false;
			selrect = null;
			validate();

		}

	}

	public void mouseClicked(MouseEvent e)
	{

		if (select)
		{

			selrect = new Rectangle();
			selrect.x       = e.getX();
			selrect.y       = e.getY();
			selrect.width   = 5;
			selrect.height  = 5;
			snbd_draw_pen();
		}

	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	//######################################################################################################
	//##################################### END JDK1.1 EVENT MODEL. ########################################
	//######################################################################################################


	private void snbd_draw_pen()
	{



	    Graphics	g;
  		int		    xsize,ysize;
        Dimension   d;

      if (selrect !=null)
      {
        g = getGraphics();
	    d = getSize();
        g.setXORMode(Color.white);
	    xsize = selrect.width;
		ysize = (d.height * xsize) / d.width;
		if (ysize > d.height)
		{
			ysize = selrect.height;
			xsize = (d.width * ysize) / d.height;
		}
        g.drawRect(selrect.x,selrect.y,xsize,ysize);
        g.setPaintMode();
	  }
	}

	public int zoomin(int inc)
	{
		flag = 1;
                double	zoom_inc;

        cropped = 0;
		d = getSize();
		zoom_inc = (double)(d.width * 100) / (double)Simage.getWidth();


		if (zoom == 0)
			zoom = (int)(zoom_inc + inc);
	 	else
			zoom += inc;

		repaint();
		return 0;

	}

	public int zoomout(int inc)
	{
		flag = 0;
                double	zoom_inc;

                cropped = 0;
		d = getSize();
		zoom_inc = (double)(d.width * 100) / (double)Simage.getWidth();

		if (zoom >= (int)(zoom_inc + inc))
			zoom -= inc;
		else
			zoom = 0;
		repaint();
		return 0;

	}

	public void display_angle(int angle)
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

    public void NextPage()
    {
        page++;
        Decompress(filename);

    }

    public void PrevPage()
    {
        if (page != 0)
            page--;

        Decompress(filename);

    }

    public void ZoomRect()
    {

        if (Simage != null)
            select = true;

    }

}
