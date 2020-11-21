/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 * This is example code for all SnowBound customers to freely copy and use however they wish.
 */

package com.snowbound.samples.annapp;

import java.awt.*;
import java.io.*;

import Snow.*;

//import java.applet.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.net.*;

import com.snowbound.samples.common.dialog.SaveDialog;

//import com.ms.awt.*;

//import netscape.security.*;
//import Snow.*;
//import sun.awt.print.PrintDialog;

class AngleDialog extends Dialog
{
    TextField af;

    public AngleDialog(Frame parent)
    {
        super(parent,"Rotate Angle",true);
        Panel p1 = new Panel();
        p1.add(new Label("Input Rotation angle in hundreds of degrees"));
        add(p1,"North");

        Panel p2 = new Panel();
        Button ok = new Button ("Ok");
        p2.add(ok);
        Button Cancel = new Button ("Cancel");
        p2.add(Cancel);
        add(p2,"South");

        Panel p3 = new Panel();
        af = new TextField("",10);
        p3.add(af);
        add(p3,"Center");

        setSize(400,150);
    }

    public boolean handleEvent (Event evt)
	{
        if ("Ok".equals(evt.arg))
	    {
       		DemoAnnApp.angle = Integer.parseInt(af.getText());
            dispose();
                return true;
        }
        else if ("Cancel".equals(evt.arg))
		{
            DemoAnnApp.angle = 0;
            dispose();
            return true;
        }
		return false;
	}

}

class ResizeDialog extends Dialog
{
    TextField af_x;
    TextField af_y;

    public ResizeDialog(Frame parent)
    {
        super(parent,"Resize Image",true);
        Panel p1 = new Panel();
        p1.add(new Label("Input new x and y resize values"));
        add(p1,"North");

        Panel p2 = new Panel();
        Button ok = new Button ("Ok");
        p2.add(ok);
        Button Cancel = new Button ("Cancel");
        p2.add(Cancel);
        add(p2,"South");

        Panel p3 = new Panel();
        af_x = new TextField("",10);
        p3.add(af_x);
        af_y = new TextField("",10);
        p3.add(af_y);
        add(p3,"Center");

        setSize(400,150);
    }

    public boolean handleEvent (Event evt)
    {
        if ("Ok".equals(evt.arg))
	    {

       		DemoAnnApp.x_resize = Integer.parseInt(af_x.getText());
       		DemoAnnApp.y_resize = Integer.parseInt(af_y.getText());
            dispose();
            return true;
        }
        else if ("Cancel".equals(evt.arg))
		{
            DemoAnnApp.angle = 0;
            dispose();
            return true;
        }
		return false;
	}

}



public class DemoAnnApp extends SnowFrame
{

	static int angle;
	static int x_resize,y_resize;
	int stat,i;
	static String FileName;
	static DemoAnnApp myFrame;
	static Insets in;
	static int zoom = 0;
	Dimension d;
	MenuBar mb;

	DemoAnnApp(String st)
	{
		setTitle(st);
		mb = new MenuBar();
		setMenuBar(mb);
		Menu FileMenu = new Menu ("File");
		FileMenu.add(new MenuItem("Open"));
        FileMenu.add(new MenuItem("Save"));

        /* see the printing sample */
        //FileMenu.add(new MenuItem("Print"));
		FileMenu.add(new MenuItem("Zoom in"));
		FileMenu.add(new MenuItem("Zoom out"));
		FileMenu.add(new MenuItem("Rotate 0 Screen"));
		FileMenu.add(new MenuItem("Rotate 90 Screen"));
		FileMenu.add(new MenuItem("Rotate 180 Screen"));
		FileMenu.add(new MenuItem("Rotate 270 Screen"));
        FileMenu.add(new MenuItem("Rotate 90"));
		FileMenu.add(new MenuItem("Rotate 180"));
		FileMenu.add(new MenuItem("Rotate 270"));
        FileMenu.add(new MenuItem("Rotate Any Angle"));
        FileMenu.add(new MenuItem("Resize Image"));
        FileMenu.add(new MenuItem("Deskew"));
        FileMenu.add(new MenuItem("Despeckle"));
        FileMenu.add(new MenuItem("Border Removal"));
        FileMenu.add(new MenuItem("Merge"));

        FileMenu.add(new MenuItem("Flip X"));
        FileMenu.add(new MenuItem("Flip Y"));
        FileMenu.add(new MenuItem("Invert"));
		FileMenu.add(new MenuItem("Preserve Black"));
        FileMenu.add(new MenuItem("Scale to Gray"));
		FileMenu.add(new MenuItem("Alias off"));
        FileMenu.add(new MenuItem("Next Page"));
        FileMenu.add(new MenuItem("Previous Page"));
        FileMenu.add(new MenuItem("Zoom Rectangle"));
		FileMenu.add(new MenuItem("Exit"));
        mb.add(FileMenu);

        FileMenu = new Menu ("Annotate");
        FileMenu.add(new MenuItem("Create Annotations"));
        FileMenu.add(new MenuItem("Activate Text"));
        FileMenu.add(new MenuItem("Deactivate Text"));
        Menu ObjectMenu = new Menu("Objects");
       	ObjectMenu.add(new MenuItem("Filled Rect"));
        ObjectMenu.add(new MenuItem("Highlight Rect"));
        ObjectMenu.add(new MenuItem("Rectangle"));
        ObjectMenu.add(new MenuItem("Line"));
        ObjectMenu.add(new MenuItem("Ellipse"));
        ObjectMenu.add(new MenuItem("Filled Ellipse"));
        ObjectMenu.add(new MenuItem("Freehand"));
        ObjectMenu.add(new MenuItem("Bitmap"));
        ObjectMenu.add(new MenuItem("Sticky Note"));
        ObjectMenu.add(new MenuItem("Polygon"));
        ObjectMenu.add(new MenuItem("Filled Polygon"));
        ObjectMenu.add(new MenuItem("Arrow"));
        ObjectMenu.add(new MenuItem("Edit"));
    	ObjectMenu.add(new MenuItem("Bubble"));

        FileMenu.add(ObjectMenu);
        mb.add(FileMenu);


	}


	public static void main(String args[])
	{

		myFrame = new DemoAnnApp ("Snowbound Java Annotation Demo");
		myFrame.setSize(640,480);
		myFrame.setVisible(true);
	}


	public boolean handleEvent (Event evt)
	{

		switch(evt.id)
		{
			case Event.ACTION_EVENT:
				if ("Exit".equals(evt.arg))
					System.exit(0);
				else if ("Zoom in".equals(evt.arg))
				{
					zoomin(20);
					return true;
			    }
				else if ("Zoom out".equals(evt.arg))
				{
					zoomout(20);
					return true;
				}
                else if ("Flip X".equals(evt.arg))
				{
				    if (Simage != null)
    					Simage.IMG_flip_bitmapx();
                    repaint();
					return true;
				}
                else if ("Invert".equals(evt.arg))
				{
				    if (Simage != null)
					    Simage.IMG_invert_bitmap();
                    repaint();
					return true;
				}
                else if ("Flip Y".equals(evt.arg))
				{
				    if (Simage != null)
					    Simage.IMG_flip_bitmapy();
					repaint();
					return true;
				}
				else if ("Rotate 0 Screen".equals(evt.arg))
				{
					display_angle(0);
					return true;
				}
				else if ("Rotate 90 Screen".equals(evt.arg))
				{
                    display_angle(90);
					return true;
				}
				else if ("Rotate 180 Screen".equals(evt.arg))
				{
                    display_angle(180);
					return true;
				}
				else if ("Rotate 270 Screen".equals(evt.arg))
				{
					display_angle(270);
					return true;
				}
                else if ("Rotate Any Angle".equals(evt.arg))
				{
                    angle = 0;
                    Dialog Angle = new AngleDialog(this);
                    Angle.setVisible(true);;

					if (angle != 0)
					{
				        if (Simage != null)
					        Simage.IMG_rotate_bitmap(angle);
                        repaint();
                    }
					return true;
				}
                else if ("Resize Image".equals(evt.arg))
				{
                    x_resize = 0;
                    y_resize = 0;
                    Dialog Resize = new ResizeDialog(this);
                    Resize.setVisible(true);;

					if (x_resize != 0 && y_resize != 0)
					{
				        if (Simage != null)
					        Simage.IMG_resize_bitmap(x_resize,y_resize);
                        repaint();
                    }
					return true;
				}
                else if ("Rotate 90".equals(evt.arg))
				{
                    if (Simage != null)
					    Simage.IMG_rotate_bitmap(9000);
                    repaint();
					return true;
				}
                else if ("Deskew".equals(evt.arg))
				{
                    int pangle[] = new int[1];
                    int stat = 0;

                    if (Simage != null)
					    stat = Simage.IMG_get_deskew_angle(pangle,-15,15);

                    if (stat >= 0 && pangle[0] != 0)
                    {
                        Simage.IMG_deskew_bitmap(pangle[0]);
                        repaint();
                    }
					return true;
				}
                else if ("Despeckle".equals(evt.arg))
				{
                    if (Simage != null)
					    Simage.IMG_despeckel_bitmap(30);
					repaint();
					return true;
				}
                else if ("Border Removal".equals(evt.arg))
				{
                    if (Simage != null)
					{
                        int width = Simage.getWidth();
                        int height = Simage.getHeight();

					    Simage.IMG_erase_rect(width / 20,height / 20,width - (width / 10),
							height - (height / 10),0xffffffff,0,0);
                    }
					repaint();
					return true;
				}
                else if ("Rotate 180".equals(evt.arg))
				{
                    if (Simage != null)
					    Simage.IMG_rotate_bitmap(18000);
					repaint();
					return true;
				}
                else if ("Rotate 270".equals(evt.arg))
				{
                    if (Simage != null)
					    Simage.IMG_rotate_bitmap(27000);
                    repaint();
					return true;
				}
				else if ("Scale to Gray".equals(evt.arg))
				{
                    if (Simage != null)
					    Simage.alias = 2;
					repaint();
					return true;
				}
	            else if ("Preserve Black".equals(evt.arg))
				{
					Simage.alias = 1;
					repaint();
					return true;
				}
                else if ("Next Page".equals(evt.arg))
				{
                    myFrame.NextPage();
					return true;
				}
                else if ("Previous Page".equals(evt.arg))
				{
					myFrame.PrevPage();
					return true;
				}
				else if ("Alias off".equals(evt.arg))
				{
					Simage.alias = 0;
					repaint();
					return true;
				}
                else if ("Zoom Rectangle".equals(evt.arg))
				{
					myFrame.ZoomRect();
					return true;
				}
                else if ("Save".equals(evt.arg))
				{
                	System.out.println("Save Called");
//                	int status = 0;
//			        status = Simage.IMG_save_bitmap("c:\\temp\\out.tif",0);
//			        System.out.println("IMG_save_bitmap(): " + status );
//                    if (Sann != null)
//                        Sann.SANN_write_ann("c:\\temp\\out.ann",0,null);
//					return true;

                	SaveDialog sd = new SaveDialog(this,"Choose Format",true);

                	int format = sd.getFormat();

                	System.out.println("format: " + format);

					FileDialog fd = new FileDialog(this,"Save Bitmap",FileDialog.SAVE);
                    fd.setVisible(true);;

					FileName = fd.getDirectory() + fd.getFile();

					if (FileName != null)
					{

                        //myFrame.Decompress(FileName);

						//System.out.println("FileName: " + FileName);
						myFrame.SaveAs(FileName,format);

                        System.gc();
					}

					return true;

				}
				else if ("Open".equals(evt.arg))
				{

					FileDialog fd = new FileDialog(this,"Open Bitmap",FileDialog.LOAD);
                    fd.setVisible(true);;

					FileName = fd.getDirectory() + fd.getFile();

					if (FileName != null)
					{
						myFrame.Decompress(FileName);
                        System.gc();
					}

					return true;
				}
				else if ("Merge".equals(evt.arg))
				{
					if (Simage == null)
					    break;

					FileDialog fd = new FileDialog(this,"Open Bitmap",FileDialog.LOAD);
                    fd.setVisible(true);;

					FileName = fd.getDirectory() + fd.getFile();

					if (FileName != null)
					{
                        tSimage = new Snow.Snowbnd();
                        tSimage.IMG_decompress_bitmap(FileName,0);
                        Simage.IMG_merge_block(tSimage,31,31,0);
                        tSimage = null;
                        System.gc();
					}

					return true;
				}
                else if ("Print".equals(evt.arg))
				{


                  int xs,ys;
      int pd_height,pd_width;
      int xs_offset,ys_offset;
      Dimension pd;
      int sres;

    //  (new PrintDialog(this, true)).setVisible(true);
                    int Dpi = 300;



      PrintJob pjob = getToolkit().getPrintJob(this, "Snowbound", null);
      if (pjob != null && this.Simage != null) {

          Graphics pg = pjob.getGraphics();
          if (pg != null) {
              int width;
              int height;
              int getHeight,getWidth;

              int alias = this.Simage.alias;


              int res = Dpi;
              if ( System.getProperty("java.vendor").startsWith("Microsoft") == true) {
                  pd = new Dimension();
                  pd.width = (int) (8.0 * res);
                  pd.height = (int) (10.5 * res);
                  PrintJob wpjob = (PrintJob) pjob;
                  wpjob.getPageDimension().setSize(pd.width, pd.height);
                  sres = res;
              } else {
                  pd = pjob.getPageDimension();
                  sres = 72;
              }

              if (this.Simage.dis_rotate == 90 || this.Simage.dis_rotate == 270) {
                  getWidth = this.Simage.getHeight();
                  getHeight = this.Simage.getWidth();
              } else {
                  getWidth = this.Simage.getWidth();
                  getHeight = this.Simage.getHeight();
              }

/*
System.out.println("width  - " + pd.width);
System.out.println("height - " + pd.height);
System.out.println("res    - " + pjob.getPageResolution());
*/



              if (pd.width != 612)
                  xs_offset = 0;
              else {
                  xs_offset = (int) (pd.width * 0.025);
                  pd.width *= 0.95;
              }

              if (pd.height != 792)
                  ys_offset = 0;
              else {
                  ys_offset = (int) (pd.height * 0.025);
                  pd.height *= 0.95;
              }
              width = (pd.width * res) / sres;
              pd_width = (pd.width * res) / sres;
              pd_height = (pd.height * res) / sres;
              height = (getHeight * width) / getWidth;


              this.Simage.alias = 0;
              if (height > pd_height) {
                  height = pd_height;
                  width = (getWidth * height) / getHeight;
                  ys = 0;
                  xs = (pd_width - width) / 2;
              } else {
                  xs = 0;
                  ys = (pd_height - height) / 2;
              }

              setCursor(Cursor.getPredefinedCursor(WAIT_CURSOR));
              this.Simage.IMG_print_bitmap(pg, xs + (int) ((xs_offset * res) / 72), ys + (int) ((ys_offset * res) / 72),
                      width, height, (res * 72) / sres);
              if (this.Sann != null) {
                  width = pd.width;
                  height = (getHeight * width) / getWidth;


                  if (height > pd.height) {
                      height = pd.height;
                      width = (getWidth * height) / getHeight;
                      ys = 0;
                      xs = (pd.width - width) / 2;
                  } else {
                      xs = 0;
                      ys = (pd.height - height) / 2;
                  }

                  this.Sann.SANN_set_croprect(this.Simage.dis_crop_xs, this.Simage.dis_crop_ys, this.Simage.dis_crop_xe, this.Simage.dis_crop_ye);
                  this.Sann.SANN_display_annotations(pg, this, xs + xs_offset, ys + ys_offset, width, height);
              }


              this.Simage.alias = alias;
              pg.dispose();
          }
          pjob.end();

      }
        setCursor(Cursor.getPredefinedCursor(DEFAULT_CURSOR));
                  /*
                    int xs,ys;
                    int pd_height,pd_width;

                    PrintJob pjob = getToolkit().getPrintJob(this,"Snowbound",null);
                    if (pjob != null && Simage != null)
                    {

                        Graphics pg = pjob.getGraphics();

                        if (pg != null)
                        {
                            int  width;
                            int  height;
                    		int getHeight,getWidth;


                            int alias = Simage.alias;
                            Dimension pd = pjob.getPageDimension();

                            int res = pjob.getPageResolution();


                            if (res == 72)
                                res = 300;


                            if (Simage.dis_rotate == 90 || Simage.dis_rotate == 270)
                            {
                                getWidth = Simage.getHeight();
                                getHeight = Simage.getWidth();
                            }
                            else
                            {
                                getWidth = Simage.getWidth();
                                getHeight = Simage.getHeight();

                            }

                           width  = (pd.width / 72) * res;
                           pd_width = (pd.width / 72) * res;
                           pd_height = (pd.height / 72) * res;
                           height = (getHeight * width) / getWidth;

                           Simage.alias = 0;
                            if (height > pd_height)
                            {
                                xs = 0;
                                height  = pd_height;
                                width = (getWidth * height) / getHeight;
                                ys = 0;
                                xs = (pd_width - width) / 2;
                            }
                            else
                            {
                                xs = 0;
                                ys = (pd_height - height) / 2;
                            }

                            Simage.IMG_print_bitmap(pg,xs,ys,width,height,res);

                            if (Sann != null)
                            {

                            width  = pd.width;
                            pd_width = pd.width;
                            pd_height = pd.height;
                            height = (getHeight * width) / getWidth;


                            if (height > pd_height)
                            {
                                xs = 0;
                                height  = pd_height;
                                width = (getWidth * height) / getHeight;
                                ys = 0;
                                xs = (pd_width - width) / 2;
                            }
                            else
                            {
                                xs = 0;
                                ys = (pd_height - height) / 2;
                            }

                            Sann.SANN_set_croprect(Simage.dis_crop_xs,Simage.dis_crop_ys,Simage.dis_crop_xe,Simage.dis_crop_ye);
			                Sann.SANN_display_annotations(pg,this,xs,ys,width,height);
                    }


                         Simage.alias = alias;
                         pg.dispose();
                        }
                        pjob.end();

                    }








*/

                    return true;
                }
                else if ("Activate Text".equals(evt.arg))
		        {
			        Sann.SANN_activate_all_objects(this);
                    return true;
                }
                else if ("Create Annotations".equals(evt.arg))
		        {
                    if (Simage != null)
                    {
                      Sann = new SnowAnn(Simage.getWidth(),Simage.getHeight());
					  Sann.double_byte=true;
                      this.enablePopupMenuItems();
                    }
                    return true;
                }
                else if ("Deactivate Text".equals(evt.arg))
		        {
			        Sann.SANN_deactivate_all_objects();
                    return true;
                }
                else if ("Filled Rect".equals(evt.arg))
		        {
			        Sann.graphic_id = 1;
                    return true;
                }
                else if ("Highlight Rect".equals(evt.arg))
		        {
			        Sann.graphic_id = 2;
                    return true;
                }
                else if ("Rectangle".equals(evt.arg))
		        {
			        Sann.graphic_id = 3;
                    return true;
                }
                else if ("Line".equals(evt.arg))
		        {
			        Sann.graphic_id = 4;
                    return true;
                }
                else if ("Ellipse".equals(evt.arg))
		        {
			        Sann.graphic_id = 5;
                    return true;
                }
                else if ("Filled Ellipse".equals(evt.arg))
		        {
			        Sann.graphic_id = 6;
                    return true;
                }
                else if ("Freehand".equals(evt.arg))
		        {
			        Sann.graphic_id = 7;
                    return true;
                }
                else if ("Bitmap".equals(evt.arg))
		        {
                    FileDialog fd = new FileDialog(this,"Open Bitmap",FileDialog.LOAD);
                    fd.setVisible(true);;

					String st = fd.getDirectory() + fd.getFile();
                    textbuff = new byte[st.length() + 48];
                    st.getBytes(0,st.length(),textbuff,0);
			        if (fd.getFile() != null)
			        {
				try{
                        RandomAccessFile ras = new RandomAccessFile (st,"r");
                        textbuff = new byte[(int)ras.length()];
                        ras.read(textbuff,0,(int)ras.length());
                        ras.close();
                        Sann.graphic_id = 8;
                      }catch (IOException ioe){return false;}
				}
                    return true;
                }
                else if ("Sticky Note".equals(evt.arg))
		        {
			        Sann.graphic_id = 9;
                    return true;
                }
                else if ("Polygon".equals(evt.arg))
		        {
			        Sann.graphic_id = 10;
                    return true;
                }
                else if ("Filled Polygon".equals(evt.arg))
		        {
			        Sann.graphic_id = 11;
                    return true;
                }
                else if ("Arrow".equals(evt.arg))
		        {
			        Sann.graphic_id = 12;
                    return true;
                }
                else if ("Edit".equals(evt.arg))
		        {
        			Sann.graphic_id = 13;
                    return true;
                }
                else if ("Bubble".equals(evt.arg))
                {
                	Sann.graphic_id = 15;
                	return true;
                }
               break;

			case Event.WINDOW_DESTROY:
				System.exit(0);
				break;


		}
        return super.handleEvent(evt);

	}

}



