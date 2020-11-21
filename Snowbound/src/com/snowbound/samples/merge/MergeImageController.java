package com.snowbound.samples.merge;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import Snow.ErrorCodes;
import Snow.Snowbnd;

/**
 * The Merge Image Controller that handles events that occur in the UI.
 */
public class MergeImageController
{
	private MergeImageApp UI;
	
	private String openDir;
	
	private String path1;
	private String path2;
	
	private Snowbnd simage1;
	private Snowbnd simage2;
	
	private int bp1;
	private int bp2;
	
	/**
	 * Constructs a Controller for the Merge Image UI.
	 * 
	 * @param mi - The Merge Image UI
	 */
	public MergeImageController(MergeImageApp mi)
	{
		UI = mi;
		
		UI.addActionListeners(new BrowseActionListener(), new MergeActionListener());
	}
	
	class BrowseActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG (*.jpg)", "jpg"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("TIFF (*.tif)", "tif"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("Windows Bitmap (*.bmp)", "bmp"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("Compuserve GIF (*.gif)", "gif"));
			
			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
			
			int option = chooser.showOpenDialog(UI);
			
			if (option == JFileChooser.APPROVE_OPTION &&
				chooser.getSelectedFile() != null)
			{
				openDir = chooser.getCurrentDirectory().getPath();
				
				JButton buttonClicked = (JButton)(e.getSource());
				
				if (buttonClicked.getName().equals("img1Button"))
				{
					path1 = chooser.getSelectedFile().getPath();
					UI.setTextField1Text(path1);
					
					simage1 = new Snowbnd();

					PreviewPanel prev1 = new PreviewPanel(simage1);
					UI.addPreviewPanel1(prev1);
					
					prev1.decompressImage(path1);
					
					bp1 = simage1.getBitsPerPixel();
				}
				else
				{
					path2 = chooser.getSelectedFile().getPath();
					UI.setTextField2Text(path2);
					
					simage2 = new Snowbnd();
					
					PreviewPanel prev2 = new PreviewPanel(simage2);
					UI.addPreviewPanel2(prev2);
					
					prev2.decompressImage(path2);
					
					bp2 = simage2.getBitsPerPixel();
				}
			}
		}
	}
	
	class MergeActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (path1 != null && path2 != null)
			{
				Snowbnd simage = merge(path1, path2, 20, 20, 20, 20, 20);
				
				new MergedDialog(UI, UI.getImg2Panel(), simage, openDir);
			}
		}
	}
	
	/**
	 * Merges the two specified images and returns a Snowbnd object of
	 * the merged image.
	 * 
	 * @param path1 - The file path of image 1
	 * @param path2 - The file path of image 2
	 * @param top - The top margin
	 * @param bot - The bottom margin
	 * @param mid - The margin between the two images
	 * @param lt - The left margin
	 * @param rt - The right margin
	 * 
	 * @return - The Snowbnd object of the merged image
	 */
	private Snowbnd merge(String path1, String path2,
                          int top, int bot, int mid, int lt, int rt)
	{
		Snowbnd simage3 = null;
		
		int status = 0;
		while (status > -1)
		{
			// Initialize Snowbnd objects for the two images
			simage1 = new Snowbnd();
			status = simage1.IMG_decompress_bitmap(path1, 0);
			
			simage2 = new Snowbnd();
			status = simage2.IMG_decompress_bitmap(path2, 0);
			
			// Promote bitmap pixel depth if the two image's are not equal
			int bp = bp1;
			if (bp1 != bp2)
			{
				bp = promoteBP();
			}
			
			// Get image dimensions
			int width1 = simage1.getWidth();
			int height1 = simage1.getHeight();
			
			int width2 = simage2.getWidth();
			int height2 = simage2.getHeight();
			
			int maxWidth = (width1 > width2) ? width1 : width2;
			
			// Initialize a Snowbnd object for the merged image
			int xsize = maxWidth + lt + rt;
			int ysize = top + height1 + mid + height2 + bot;
			
			simage3 = new Snowbnd(xsize, ysize, bp);
			
			// Change background from black to white
			status = simage3.IMG_invert_bitmap();
			
			// Initialize buffers of white pixels to store image data
			byte[] buffer1 = new byte[maxWidth * 4];
			byte[] buffer2 = new byte[maxWidth * 4];
			
			for (int i = 0; i < buffer1.length; i++)
			{
				buffer1[i] = (byte) 0xff;
				buffer2[i] = (byte) 0xff;
			}
			
			// Get raster from the two images and put onto the merged image
			for (int y1 = 0; y1 <= height1; y1++)
			{
				status = simage1.IMGLOW_get_raster(y1, buffer1);
				status = simage3.IMGLOW_put_raster(y1 + top, buffer1);
			}
			
			for (int y2 = 0; y2 <= height2; y2++)
			{
				status = simage2.IMGLOW_get_raster(y2, buffer2);
				status = simage3.IMGLOW_put_raster(y2 + top + height1 + mid, buffer2);
			}
			
			// Shift merged image by left margin
			status = simage3.IMG_shift_bitmap(lt, 0);
			
			break;
		}
		
		if (status < 0)
		{
			System.out.println("Merging Failed: " + 
                               ErrorCodes.getErrorMessage(status));
		}
		
		return simage3;
	}
	
	/**
	 * Promotes the bitmap pixel depth of the image with the lower bitmap
	 * pixel depth so that the bitmap pixel depths of the two images are
	 * equal.
	 */
	private int promoteBP()
	{
		int status = 0;
		int bp = 0;
		
		if (bp1 > bp2)
		{
			if (bp1 == 24)
			{
				status = simage2.IMG_promote_24();
				bp = 24;
			}
			else if (bp1 == 8)
			{
				status = simage2.IMG_promote_8();
				bp = 8;
			}
			else
			{
				status = simage1.IMG_promote_8();
				status = simage2.IMG_promote_8();
				bp = 8;
			}
		}
		else
		{
			if (bp2 == 24)
			{
				status = simage1.IMG_promote_24();
				bp = 24;
			}
			else if (bp2 == 8)
			{
				status = simage1.IMG_promote_8();
				bp = 8;
			}
			else
			{
				status = simage1.IMG_promote_8();
				status = simage2.IMG_promote_8();
				bp = 8;
			}
		}
		
		if (status < 0)
		{
			System.out.println("Error promoting bitmap pixel depth:");
			System.out.println(ErrorCodes.getErrorMessage(status));
		}
		
		return bp;
	}
}
