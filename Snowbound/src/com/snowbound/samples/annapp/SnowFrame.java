/**
 * Copyright (C) 2002-20013 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.annapp;

import java.awt.*;
import java.awt.event.*;
import java.awt.Toolkit;

import Snow.*;
import Snow.Snowbnd;
import Snow.SnowAnn;
import Snow.SANN_RECT;

/*  Only for testing decompress from a datainputstream
*/
import java.io.DataInputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import com.snowbound.common.components.annotationdialog.AnnotationPropertiesDialog;


public class SnowFrame extends Frame
{
	public Snowbnd Simage = null;
	public SnowAnn Sann = null;

    public Snowbnd tSimage = null;
	public SnowAnn tSann = null;
	int stat = 0;
	Insets in;
	public byte textbuff[];
    int zoom = 0;
	Dimension d;
    int page = 0;
    String filename = null;
    String decompressedFilename = null;
    String savedFilename = null;
    int totalPages = 0;
    boolean    select = false;
    Rectangle selrect = null;
    int cropped = 0;
    int scroll = 0;
    int curobj = -1;
    AnnotationPropertiesDialog props = null;

    public int Decompress(String st)
	{


    	 tSimage = new Snowbnd();

        if (filename != null)
            if (filename != st)
                page = 0;

        stat = tSimage.IMG_decompress_bitmap(st,page);
        if (stat >= 0)
		{
        	decompressedFilename = st;
        	totalPages = tSimage.IMGLOW_get_pages(st);
			int	xsize,ysize;
            if (Simage != null)
                Simage.finalize();

            Simage = tSimage;


            Sann = new SnowAnn(Simage.getWidth(),Simage.getHeight());
            enablePopupMenuItems();
            char buf[] = new char[200];

            st.getChars(0,st.length(),buf,0);
            buf[st.length() - 3] = 'a';
            buf[st.length() - 2] = 'n';
            buf[st.length() - 1] = 'n';

            String st2 = new String(buf);

            stat = Sann.SANN_read_ann(st2);
            if (stat < 0)
                Sann = null;

            if(Sann!=null)
            {
                Sann.double_byte=true;
            }


		    filename = st;
            setTitle(st);
			zoom = 0;
            cropped = 0;
            select = false;
            selrect = null;
			xsize = Simage.getWidth();
			ysize = Simage.getHeight();
            if (Simage.getBitsPerPixel() == 16)
                Simage.IMG_window_level(0,0,1);

            d = Toolkit.getDefaultToolkit().getScreenSize();
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
			in = insets();

            d = size();
			d.width -= (in.right + in.left);
			d.height -= (in.top + in.bottom);
            if (d.width == xsize && d.height == ysize)
                repaint();
            else
                resize(xsize + in.right + in.left,ysize + in.top + in.bottom);

	    }
		else
		{
	        Snow.MessageBox msg;

		    if (page != 0)
		        page--;

            msg = new Snow.MessageBox(null,"Error Decompressing Image",true);

		}
        return stat;
	}

    public int SaveAs( String saveAs , int format ) {
    	int stat = 0;

    	if( tSimage != null ) {
    		System.out.println("tSimage NOT null ");
    	}

    	if( decompressedFilename != null && saveAs != null ) {
    		for( int i = 0 ; i < totalPages ; i++ ) {
    			stat = tSimage.IMG_save_bitmap(saveAs, format);
    		}

    	}

    	if( stat < 0 ) {
    		MessageBox msg = new MessageBox(null,"Error Saving Image",true);
    	}

    	return stat;
    }

	public void paint (Graphics g)
	{

		if (Simage != null)
		{
            in = insets();
/* NOTE THAT Microsoft JVIEW.EXE has a bug and does not start     */
/* the origin at 0,0 under the title bar.  Comment this next line */
/* to fix Jview.exe.  All other Java Machines work fine.          */
            g.translate(in.left,in.top);

			d = size();
			d.width -= (in.right + in.left);
			d.height -= (in.top + in.bottom);
            if (scroll == 0)
            {
                if (cropped == 0)
    	    	    stat = Simage.IMG_display_bitmap_aspect(g,this,0,0,d.width,d.height,zoom);
                else
                     Simage.IMG_display_bitmap(g,0,0,d.width,d.height);
            }
            scroll = 0;
            if (select)
                snbd_draw_pen();

            if (Sann != null)
            {
                Sann.SANN_set_croprect(Simage.dis_crop_xs,Simage.dis_crop_ys,
                        Simage.dis_crop_xe,Simage.dis_crop_ye);
			    Sann.SANN_display_annotations(g,this,0,0,d.width,d.height);
            }

        }

	}

	public void update(Graphics g)
	{
		if ((Simage == null || zoom == 0) && scroll == 0)
		{
			d = size();
			in = insets();
			d.width -= (in.right + in.left);
			d.height -= (in.top + in.bottom);
			g.setColor(getBackground());
			g.fillRect(0,0,d.width,d.height);
			g.setColor(getForeground());
		}
		paint(g);
	}

    public void enablePopupMenuItems()
  {
    //This enable the Popup menu Properties option to bring up the Annotation Properties Dialog Box
    //and the Enable Text option for Postit and Edit Annotations
    if (Sann != null)
    {
      Sann.EnableProperties = true;
      Sann.EnableEditText = true;
    }
  }

    public boolean handleEvent (Event evt)
	{

		switch(evt.id)
		{
            case Event.ACTION_EVENT:
			{
			    if ("Cancel".equals(evt.arg))
				{
                    remove(Sann.p);
                    SANN_RECT rc = new SANN_RECT();
                    Sann.GetClientRect(this,rc);
	                Sann.SANN_highlight_object(this,rc,Sann.graphic_num);
                }
                else if ("Delete".equals(evt.arg))
                {
                    remove(Sann.p);
                    SANN_RECT rc = new SANN_RECT();
                    Sann.GetClientRect(this,rc);
                    Sann.SANN_highlight_object(this,rc,Sann.graphic_num);
                    Sann.ui_delete(this);
                    this.repaint();
                }
			    else if ("Move".equals(evt.arg))
                {
                    remove(Sann.p);
                    SANN_RECT rc = new SANN_RECT();
                    Sann.GetClientRect(this,rc);
                    Sann.SANN_highlight_object(this,rc,Sann.graphic_num);
                    Sann.ui_startmove(this);
               	}
			    else if ("Resize".equals(evt.arg))
                {
                    remove(Sann.p);
                    Snow.SANN_RECT rc = new Snow.SANN_RECT();
                    Sann.GetClientRect(this,rc);
                    Sann.SANN_highlight_object(this,rc,Sann.graphic_num);
                    Sann.ui_startresize(this);

                }else if ("Edit text".equals(evt.arg)) {
                    remove(Sann.p);
                    Sann.SANN_activate_object(this,curobj);
                    repaint();

                }else if ("Properties".equals(evt.arg)) {

                    if (props == null)
                        props = new AnnotationPropertiesDialog(this, this, false,Sann);

                    props.updateValues();
                    props.setVisible(true);

                    remove(Sann.p);
                    repaint();

                }
                return super.handleEvent(evt);
            }

			case Event.SCROLL_LINE_UP:
			case Event.SCROLL_LINE_DOWN:
			case Event.SCROLL_PAGE_UP:
			case Event.SCROLL_PAGE_DOWN:
			case Event.SCROLL_ABSOLUTE:
			    {
       				Simage.IMG_scroll_bitmap(this,evt);

                    if (Sann != null)
                    {
                        scroll = 1;
                        repaint();
                    }
  					return true;
			    }

            case Event.MOUSE_DOWN:
			    {
                    if (!select)
                    {
                         if (Sann == null)
                            return super.handleEvent(evt);
                          if (Sann != null)
                          {
                            curobj = -1;
                            int obj = Sann.SANN_get_object_num(this,evt.x,evt.y);
                            if (obj >= 0)
                              curobj = obj;
                          }

                        Sann.ui_wm_lbuttondown(this,evt.x,evt.y,textbuff);
                        return true;
                    }

                    selrect = new Rectangle();
                    selrect.x       = evt.x;
					selrect.y       = evt.y;
					selrect.width   = 5;
					selrect.height  = 5;
					snbd_draw_pen();
    				return true;
			    }

			case Event.MOUSE_DRAG:
			    {
                     if (!select || selrect == null)
                     {
                        if (Sann == null)
                            return super.handleEvent(evt);

                         Sann.ui_wm_mousemove(this,evt.x,evt.y,textbuff);
                         return true;

                     }


                    if (evt.x > selrect.x && evt.y > selrect.y)
					{
		   	    	    snbd_draw_pen();
    	    			selrect.width  = evt.x - selrect.x;
                        selrect.height = evt.y - selrect.y;
    					snbd_draw_pen();
					}
					return true;
			    }
                case Event.MOUSE_MOVE:
			    {
                     if (!select || selrect == null)
                     {
                        if (Sann == null)
                           return super.handleEvent(evt);
                        else
                        {
                            Sann.ui_wm_mousemove(this,evt.x,evt.y,textbuff);

                        }
                     }
                     return super.handleEvent(evt);
                }
                case Event.MOUSE_UP:
			    {
                    Point pt,pt2;

				    if (!select || selrect == null)
                    {
                        if (Sann != null)
                            Sann.ui_wm_lbuttonup(this,evt.x,evt.y,textbuff);

                        return super.handleEvent(evt);

                    }

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
                    if (zoom < 0)
                        zoom = 0;
                    cropped = 1;
                    select = false;
                    selrect = null;
					repaint();
					return true;
			    }


			case Event.WINDOW_DESTROY:
				dispose();
				return true;


		}
		return super.handleEvent(evt);

	}


	private void snbd_draw_pen()
	{
	    Graphics	g;
		int		    xsize,ysize;
        Insets      in;
        Dimension   d;


        g = getGraphics();
        in = insets();

	    d = size();
        d.width -= (in.right + in.left);
	    d.height -= (in.top + in.bottom);

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

	public int zoomin(int inc)
	{
    double	zoom_inc;

        cropped = 0;
		d = size();
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
		double	zoom_inc;

        cropped = 0;
		d = size();
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

        d = Toolkit.getDefaultToolkit().getScreenSize();
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
        in = insets();
        xsize += in.right + in.left;
        ysize += in.top + in.bottom;
    	d = size();
        if (xsize == d.width && ysize == d.height)
            repaint();
        else
            resize(xsize,ysize);
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
