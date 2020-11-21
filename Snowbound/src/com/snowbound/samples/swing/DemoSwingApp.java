/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.swing;

/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!
!!	This is a Swing demo using JDK 1.2.1.  Please update your compiler and Java
!!	virtual machine if you receive errors.  Also note that Swing slows down the
!!	toolkit considerably.  This is because of the overhead of the Swing components.
!!	Printing as well is affected:  the highest resolution for printing is 72 dpi.
!!
!!	This demo has been optimized for 800 x 600 screen resolution.
!!
!!
!!
!!
!!
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.PrintJob;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;

public class DemoSwingApp extends JFrame implements ActionListener, MouseMotionListener, MouseListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4803777242677479736L;

	static String[][] sButton =

										{
											{"Open", "Save", "Print", "Zoom In", "Zoom Out", "Next Page", "Prev Page"},
											{"0\u00ba Screen", "90\u00ba Screen", "180\u00ba Screen", "270\u00ba Screen", "Rotate 90\u00ba", "Rotate 180\u00ba", "Rotate 270\u00ba"},
											{"Rotate Any Angle", "Resize Image", "Deskew", "Despeckle", "Border Removal", "Merge", "Flip X"},
											{"Flip Y", "Invert", "Preserve Black", "Scale to Gray",  "Alias Off", "Zoom Rect", "Exit"}
										};

	static String[][] sActionCmd =

										{

											{"open", "save", "print", "zin", "zout", "next", "prev"},
											{"r0s", "r90s", "r180s", "r270s", "r90", "r180", "r270"},
											{"rany", "resize", "deskew", "despeck", "bord_rmv",	"merge", "flipx"},
											{"flipy", "invert", "pres_blck", "scale", "alias_off", "zrect", "exit"}

										};

	static int angle;
	static int x_resize,y_resize;
	int stat,i;
	static Insets in;
	static int zoom = 0;
	Dimension d;
	static JFrame jf = new JFrame();
	static Container content;
	static JRootPane root = new JRootPane();
	static GridBagLayout gridbag = new GridBagLayout();
	static GridBagConstraints constr = new GridBagConstraints();
	static SwingSnowFrame ssb = new SwingSnowFrame();
	static String FileName = new String();
	static JPanel jp1 = new JPanel(gridbag);

	//constructor
	DemoSwingApp()
	{
	}

	public JRootPane createRootPane()
	{
		content = root.getContentPane();
		content.setLayout(gridbag);

		jp1.setBackground(Color.blue);
		constr.insets = new Insets(5, 5, 5, 5);
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.NORTHWEST;
		constr.gridx = 0;
		constr.gridy = 0;
		constr.weightx = 1.0;
		constr.weighty = 1.0;
		content.add(jp1);
		gridbag.setConstraints(jp1, constr);

		/*

			Add the buttons to a JPanel which sits on top of the content pane.

		*/

			for(int i = 0; i < 4; i++)
			{

				for(int j = 0; j < 7; j++)
				{

					if(j < 7)
					{
						constr.fill = GridBagConstraints.HORIZONTAL;
						constr.anchor = GridBagConstraints.NORTHWEST;
						constr.weightx = 1.0;
						constr.weighty = 0.25;

						makebutton(sButton[i][j], gridbag, constr, j, i, sActionCmd[i][j]);
					}
					else
					{
						constr.fill = GridBagConstraints.HORIZONTAL;
						constr.anchor = GridBagConstraints.NORTHWEST;
						constr.weightx = 1.5;
						constr.weighty = 0.25;
						constr.gridwidth = GridBagConstraints.REMAINDER;


						makebutton(sButton[i][j], gridbag, constr, j, i, sActionCmd[i][j]);
					}


				}

			}


		constr.fill = GridBagConstraints.BOTH;
		constr.anchor = GridBagConstraints.CENTER;
		constr.weightx = 1;
		constr.weighty = 15;
		constr.gridx = 0;
		constr.gridy = 1;
		constr.gridwidth = GridBagConstraints.REMAINDER;
		content.add(ssb);
		gridbag.setConstraints(ssb, constr);
		ssb.addMouseMotionListener(this);
		ssb.addMouseListener(this);
                addWindowListener(new WindowAdapter()
                {
                  public void windowClosing(WindowEvent e)
                  {System.exit(0);}
                });

		return(root);
	}//end createRootPane

    protected void makebutton(String name, GridBagLayout gridbag, GridBagConstraints c, int x, int y, String act_cmd)
	{
    	JButton button = new JButton(name);
		constr.gridx = x;
		constr.gridy = y;
		button.setFont(new Font("Sans Serif", Font.PLAIN, 10));


		button.setActionCommand(act_cmd);
		button.addActionListener(this);
		jp1.add(button);
		gridbag.setConstraints(button, c);

    }//end makebutton


	public static void main(String args[])
	{

		jf = new DemoSwingApp();
        jf.setTitle("Snowbound Software :: Swing");
		jf.setBackground(Color.lightGray);
		jf.pack();
		jf.setSize(800, 600);
        jf.setVisible(true);



	}//end main

	public void actionPerformed(ActionEvent e)
	{

		if(e.getActionCommand().equals("open"))
		{

			String tif = new String ("tif");
			String jpg = new String ("jpg");
			String bmp = new String ("bmp");
			String gif = new String ("gif");
			String mod = new String ("mod");
			String ica = new String ("ica");
			String cal = new String ("cal");
			String dcm = new String ("dcm");
			String ann = new String ("ann");
			String all = new String ("*");

			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);

			chooser.setFileFilter(new SimpleFileFilter(tif, "TIFF (*.tif)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(jpg, "JPEG (*.jpg)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(bmp, "Windows Bitmap (*.bmp)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(gif, "Compuserve GIF (*.gif)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(mod, "MO:DCA (*.mod)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(ica, "IOCA (*.ica)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(cal, "CALS (*.cal)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(dcm, "DICOM (*.dcm)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(ann, "RM Annotation File (*.ann)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(all, "All Files (*.*)"));

			int option = chooser.showOpenDialog(this);
			if(option == JFileChooser.APPROVE_OPTION)
			{

				if(chooser.getSelectedFile() != null)
				{
					FileName = chooser.getCurrentDirectory() + System.getProperty("file.separator") + chooser.getSelectedFile().getName();
					setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
					stat = ssb.Decompress(chooser.getCurrentDirectory() + System.getProperty("file.separator") + chooser.getSelectedFile().getName());
					System.out.println(stat);

					if(ssb.Simage != null)
					{
						setTitle("Snowbound Software :: Swing - [" + FileName + "]");
						System.gc();
						repaint();

					}
					setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));

				}

			}

		}//end open
		else if(e.getActionCommand().equals("save"))
		{

			String frmt					= new String();
			String cur_filter 			= new String();
			String tmp_fn				= new String();
			String cur_dir				= new String();
			String tok_filename			= new String();
			File tmp_file				= new File(FileName);

			//HASHTABLE TO STORE SAVE FORMATS (INTEGER)
			Hashtable<String, Integer> format 			= new Hashtable<String, Integer>();

				format.put("TIFF Group 3 (*.tif)", 		new Integer(8));
				format.put("TIFF Group 3-2d (*.tif)", 	new Integer(17));
				format.put("TIFF Group 4 (*.tif)",		new Integer(10));
				format.put("TIFF-Jpeg (*.tif)", 		new Integer(40));
				format.put("TIFF Packbits (*.tif)", 	new Integer(16));
				format.put("TIFF Uncompressed (*.tif)", new Integer(0));
				format.put("JPEG (*.jpg)", 				new Integer(13));
				format.put("Windows Bitmap (*.bmp)", 	new Integer(1));
				format.put("CALS (*.cal)", 				new Integer(18));
				format.put("Compuserve GIF (*.gif)", 	new Integer(4));
				format.put("MO:DCA (*.mod)", 			new Integer(49));


			//STRINGS FOR THE FILE EXTENSIONS
			String tifg3 				= new String ("tif");
			String tifg32d 				= new String ("tif");
			String tifg4 				= new String ("tif");
			String tifjpeg				= new String ("tif");
			String tifpackbits 			= new String ("tif");
			String tifuncomp 			= new String ("tif");
			String jpg 					= new String ("jpg");
			String bmp 					= new String ("bmp");
			String gif 					= new String ("gif");
			String mod 					= new String ("mod");
			String cal 					= new String ("cal");

			JFileChooser chooser 		= new JFileChooser();

			chooser.setMultiSelectionEnabled(false);
			chooser.addChoosableFileFilter(new SimpleFileFilter(tifg3, "TIFF Group 3 (*.tif)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(tifg32d, "TIFF Group 3-2d (*.tif)"));
			chooser.setFileFilter(new SimpleFileFilter(tifg4, "TIFF Group 4 (*.tif)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(tifjpeg, "TIFF-Jpeg (*.tif)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(tifpackbits, "TIFF Packbits (*.tif)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(tifuncomp, "TIFF Uncompressed (*.tif)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(jpg, "JPEG (*.jpg)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(bmp, "Windows Bitmap (*.bmp)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(gif, "Compuserve GIF (*.gif)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(mod, "MO:DCA (*.mod)"));
			chooser.addChoosableFileFilter(new SimpleFileFilter(cal, "CALS (*.cal)"));


			chooser.setSelectedFile(new File(FileName));

			int option = chooser.showSaveDialog(this);

			if(option == JFileChooser.APPROVE_OPTION)
			{
				if(chooser.getSelectedFile() != null)
				{

					setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));

					tmp_file = chooser.getSelectedFile();

					if(tmp_file != null)
					{

						//extract
						for(StringTokenizer str_tok = new StringTokenizer(FileName); str_tok.hasMoreTokens();)
						{
							tok_filename = str_tok.nextToken(System.getProperty("file.separator"));
						}

						//extract the filename minus the extension
						tmp_fn = tok_filename.substring(0, tok_filename.length() - 3);

						cur_filter = chooser.getFileFilter().getDescription();  		//this will be the key into the hashtable

						if(!(cur_dir.endsWith("\\")))
						{

							cur_dir = chooser.getCurrentDirectory().toString() + System.getProperty("file.separator");
						}


						frmt = cur_filter.substring(cur_filter.length() - 4, cur_filter.length() - 1);

						stat = ssb.Simage.IMG_save_bitmap( (String)(cur_dir + tmp_fn + frmt), ( (Integer)format.get(cur_filter) ).intValue() );


						if(stat == -7)
						{
							System.out.println("Format not allowed.");
						}
						else
						{
							setTitle("Snowbound Sofware :: Swing - [" + cur_dir + tmp_fn + frmt + "]");
							System.gc();
							repaint();
						}


					}

					setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));

				}

			}

		}//end save

		else if(e.getActionCommand().equals("exit"))
		{
			System.exit(0);

		}
		else if(e.getActionCommand().equals("zin"))
		{
			ssb.zoomin(20);
		}

		else if(e.getActionCommand().equals("zout"))
		{
			ssb.zoomout(20);

		}

		else if(e.getActionCommand().equals("next"))
		{
			ssb.NextPage();

		}
		else if(e.getActionCommand().equals("prev"))
		{
			ssb.PrevPage();

		}
		else if(e.getActionCommand().equals("r0s"))
		{
			ssb.display_angle(0);

		}
		else if(e.getActionCommand().equals("r90s"))
		{
        	ssb.display_angle(90);

		}
		else if(e.getActionCommand().equals("r180s"))
		{
			ssb.display_angle(180);

		}
		else if(e.getActionCommand().equals("r270s"))
		{
			ssb.display_angle(270);

		}
		else if(e.getActionCommand().equals("r90"))
		{
			if (ssb.Simage != null)
				ssb.Simage.IMG_rotate_bitmap(9000);
			repaint();

		}
		else if(e.getActionCommand().equals("r180"))
		{
			if (ssb.Simage != null)
				ssb.Simage.IMG_rotate_bitmap(18000);
			repaint();


		}
		else if(e.getActionCommand().equals("r270"))
		{
			if (ssb.Simage != null)
				ssb.Simage.IMG_rotate_bitmap(27000);
			repaint();

		}
		else if(e.getActionCommand().equals("rany"))
		{
	        angle = 0;
			JDialog Angle = new AngleDialog(this);
			Angle.setVisible(true);

			if (angle != 0)
			{
				if (ssb.Simage != null)
					ssb.Simage.IMG_rotate_bitmap(angle);
				repaint();
			}

		}
		else if(e.getActionCommand().equals("resize"))
		{
			x_resize = 0;
			y_resize = 0;
			JDialog Resize = new ResizeDialog(this);
			Resize.setVisible(true);

			if (x_resize != 0 && y_resize != 0)
			{
				if (ssb.Simage != null)
					ssb.Simage.IMG_resize_bitmap(x_resize,y_resize);
				repaint();
			}

		}
		else if(e.getActionCommand().equals("deskew"))
		{
			int pangle[] = new int[1];
			int stat = 0;

			if (ssb.Simage != null)
				stat = ssb.Simage.IMG_get_deskew_angle(pangle,-15,15);

			if (stat >= 0 && pangle[0] != 0)
			{
				ssb.Simage.IMG_deskew_bitmap(pangle[0]);
				repaint();
			}

		}
		else if(e.getActionCommand().equals("despeck"))
		{

			if (ssb.Simage != null)
				ssb.Simage.IMG_despeckel_bitmap(30);
			repaint();

		}
		else if(e.getActionCommand().equals("bord_rmv"))
		{
			if (ssb.Simage != null)
			{
				int width = ssb.Simage.getWidth();
				int height = ssb.Simage.getHeight();

				ssb.Simage.IMG_erase_rect(width / 20,height / 20,width - (width / 10), height - (height / 10),0xffffffff,0,0);
			}
			repaint();

		}
		else if(e.getActionCommand().equals("merge"))
		{
			if (ssb.Simage != null)
			{

				FileDialog fd = new FileDialog(this,"Open Bitmap",FileDialog.LOAD);
				fd.setVisible(true);

				FileName = fd.getDirectory() + fd.getFile();

				if (FileName != null)
				{
					ssb.tSimage = new Snow.Snowbnd();
					ssb.tSimage.IMG_decompress_bitmap(FileName,0);
					ssb.Simage.IMG_merge_block(ssb.tSimage,31,31,0);
					ssb.tSimage = null;
					System.gc();
				}

				repaint();

			}

		}
		else if(e.getActionCommand().equals("flipx"))
		{
			if (ssb.Simage != null)
				ssb.Simage.IMG_flip_bitmapx();
			repaint();

		}
		else if(e.getActionCommand().equals("flipy"))
		{
			if (ssb.Simage != null)
				ssb.Simage.IMG_flip_bitmapy();
			repaint();


		}
		else if(e.getActionCommand().equals("invert"))
		{
			if (ssb.Simage != null)
				ssb.Simage.IMG_invert_bitmap();
			repaint();


		}
		else if(e.getActionCommand().equals("pres_blck"))
		{
			if(ssb.Simage != null)
				ssb.Simage.alias = 1;
			repaint();

		}
		else if(e.getActionCommand().equals("scale"))
		{
			if (ssb.Simage != null)
				ssb.Simage.alias = 2;
			repaint();


		}

		else if(e.getActionCommand().equals("alias_off"))
		{
			ssb.Simage.alias = 0;
			repaint();


		}
		else if(e.getActionCommand().equals("zrect"))
		{
			ssb.ZoomRect();

		}
		else if(e.getActionCommand().equals("print"))
		{
			int xs,ys;
			int pd_height,pd_width;

			PrintJob pjob = getToolkit().getPrintJob(this,"Snowbound",null);
			if (pjob != null && ssb.Simage != null)
			{

				Graphics pg = pjob.getGraphics();
				if (pg != null)
				{
					int  width;
					int  height;
					int getHeight,getWidth;

					int alias = ssb.Simage.alias;
					Dimension pd = pjob.getPageDimension();

					int res = pjob.getPageResolution();

					if (res == 72)
						res = 300;

					if (ssb.Simage.dis_rotate == 90 || ssb.Simage.dis_rotate == 270)
					{
						getWidth = ssb.Simage.getHeight();
						getHeight = ssb.Simage.getWidth();
					}
					else
					{
						getWidth = ssb.Simage.getWidth();
						getHeight = ssb.Simage.getHeight();

					}

					width  = (pd.width / 72) * res;
					pd_width = (pd.width / 72) * res;
					pd_height = (pd.height / 72) * res;
					height = (getHeight * width) / getWidth;


					ssb.Simage.alias = 0;
					if (height > pd_height)
					{
						xs = 0;
						height  = pd_height;
						width = (getWidth * width) / getHeight;
						ys = 0;
						xs = (pd_width - width) / 2;
					}
					else
					{
						xs = 0;
						ys = (pd_height - height) / 2;

					}

					ssb.Simage.IMG_print_bitmap(pg,xs,ys,width,height,res);

					ssb.Simage.alias = alias;
					pg.dispose();
				}

				pjob.end();

			}
		}



	}//end actionPerformed

	public void mouseMoved(MouseEvent e)
	{

	}

	public void mouseDragged(MouseEvent e)
	{

	}

	public void mouseClicked(MouseEvent e)
	{

	}

	public void mousePressed(MouseEvent e)
	{

	}

	public void mouseReleased(MouseEvent e)
	{

	}

	public void mouseEntered(MouseEvent e)
	{

	}


	public void mouseExited(MouseEvent e)
	{

	}


}
class AngleDialog extends JDialog implements ActionListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField af;

    AngleDialog(Frame parent)
    {
        super(parent,"Rotate Angle",true);
        JPanel p1 = new JPanel();
        p1.add(new JLabel("Input Rotation angle in hundreds of degrees"));
        getContentPane().add(p1,"North");

        JPanel p2 = new JPanel();
        JButton ok = new JButton ("Ok");
		ok.addActionListener(this);
        p2.add(ok);
        JButton Cancel = new JButton ("Cancel");
		Cancel.addActionListener(this);
        p2.add(Cancel);
        getContentPane().add(p2,"South");

        JPanel p3 = new JPanel();
        af = new JTextField("",10);
        p3.add(af);
        getContentPane().add(p3,"Center");

        setSize(400,150);

		isModal();



    }

	public void actionPerformed(ActionEvent e)
	{

		if(e.getActionCommand().equals("Ok"))
	    {
       		DemoSwingApp.angle = Integer.parseInt(af.getText());
            dispose();
        }
        else if (e.getActionCommand().equals("Cancel"))
		{
            DemoSwingApp.angle = 0;
            dispose();
        }


	}


}

class ResizeDialog extends JDialog implements ActionListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6944689824847205347L;
	JTextField af_x;
    JTextField af_y;

    ResizeDialog(JFrame parent)
    {
        super(parent,"Resize Image",true);
        JPanel p1 = new JPanel();
        p1.add(new JLabel("Input new x and y resize values"));
        getContentPane().add(p1,"North");

        JPanel p2 = new JPanel();
        JButton ok = new JButton ("Ok");
		ok.addActionListener(this);
        p2.add(ok);
        JButton Cancel = new JButton ("Cancel");
		Cancel.addActionListener(this);
        p2.add(Cancel);
        getContentPane().add(p2,"South");

        JPanel p3 = new JPanel();
        af_x = new JTextField("",10);
        p3.add(af_x);
        af_y = new JTextField("",10);
        p3.add(af_y);
        getContentPane().add(p3,"Center");

        setSize(400,150);
		isModal();


    }

	public void actionPerformed(ActionEvent e)
	{


		if(e.getActionCommand().equals("Ok"))
	    {

       		DemoSwingApp.x_resize = Integer.parseInt(af_x.getText());
       		DemoSwingApp.y_resize = Integer.parseInt(af_y.getText());
            dispose();

        }
        else if (e.getActionCommand().equals("Cancel"));
		{
            DemoSwingApp.angle = 0;
            dispose();

        }

	}

}

class SimpleFileFilter extends javax.swing.filechooser.FileFilter
{

	String extension;
	String description;

	public SimpleFileFilter(String ext)
	{
		extension = ext.toLowerCase();

	}

	public SimpleFileFilter(String ext, String descr)
	{


		extension = ext.toLowerCase();
		description = (descr == null ? ext + " files" : descr);

	}

	public boolean accept(File f)
	{

		if (f.isDirectory())
		{

			return true;

		}

		String name = f.getName().toLowerCase();


		if(name.endsWith(extension))
		{

			return true;

		}
		else
		{

			return false;
		}


	}


	public String getDescription()
	{

		return description;

	}



}