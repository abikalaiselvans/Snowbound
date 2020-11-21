package com.snowbound.samples.pdfconversion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.snowbound.samples.common.SnowboundJPanel;

import Snow.ErrorCodes;

/**
 * The Controller for the File to PDF Conversion App
 */
public class FileToPdfController
{
	private FileToPdfApp UI;
	private SnowboundJPanel snowPanel;
	
	private String fileName;
	private String openDir;
	
	/**
	 * Constructor for FileToPdfController which handles events
	 * that occur in the UI
	 * 
	 * @param ui - The UI for the FileToPdfApp
	 */
	public FileToPdfController(FileToPdfApp ftpa)
	{
		UI = ftpa;
		snowPanel = UI.getSnowPanel();
		
		UI.addActionListeners(new LoadActionListener(), new SaveActionListener(),
				              new PageActionListener(), new ZoomActionListener());
	}
	
	class LoadActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// Open a Dialog that allows the user to select the input file
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("TIFF (*.tif)", "tif"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("PCL (*.pcl)", "pcl"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("MO:DCA (*.mca)", "mca"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("IOCA (*.ica)", "ica"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("CALS (*.cal)", "cal"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG (*.jpg)", "jpg"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("Windows Bitmap (*.bmp)", "bmp"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("Compuserve GIF (*.gif)", "gif"));
			
			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
			
			int option = chooser.showOpenDialog(UI);
			
			// If the selection is approved, decompress the image
			if (option == JFileChooser.APPROVE_OPTION && 
				chooser.getSelectedFile() != null)
			{
				UI.enableAfterOpen();
				
				openDir = chooser.getCurrentDirectory().getPath();
				fileName = chooser.getSelectedFile().getPath();
				
				int status = snowPanel.decompressImage(fileName);
				
				if (status == 0)
				{
					UI.setTitle("Snowbound Software :: File to PDF Conversion - " +
				                chooser.getSelectedFile().getName());
					
					// Resize frame to scale to file dimensions
					double width = snowPanel.getSimage().getWidth();
					double height = snowPanel.getSimage().getHeight();

					if (width > height)
					{
						UI.setSize(800, (int)(900 * (height/width)));
					}
					else
					{
						UI.setSize((int)(725 * (width/height)), 800);
					}	
				}
				else
				{
					System.out.println("Error decompressing file: " +
							           ErrorCodes.getErrorMessage(status));
					
					snowPanel.repaint();
				}
			}
		}
	}
	
	class SaveActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			// Open a Save Dialog that allows the user to enter the output
			// file name.
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter("PDF file (*.pdf)", "pdf"));
			
			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
			
			int option = chooser.showSaveDialog(UI);
			
			// If selection approved, set the PDF destination size for the
			// saved PDF file
			if (option == JFileChooser.APPROVE_OPTION && 
				chooser.getSelectedFile() != null)
			{
				// Convert size of the file in pixels to points. Note that
				// a point is 1/72 of an inch. Since the width and height
				// of the original image are in pixels, the dimensions are
				// divided by the DPI to get the units in inches and then
				// multiplied by 72 to get the units in points.
				int xdpi = snowPanel.getSimage().getXdpi();
				int ydpi = snowPanel.getSimage().getYdpi();
				int width = snowPanel.getSimage().getWidth();
				int height = snowPanel.getSimage().getHeight();
				
				int xsize = width;
				int ysize = height;
				
				if (xdpi != 0 && ydpi != 0)
				{
					xsize = width * 72 / xdpi;
					ysize = height * 72 / ydpi;
				}
				
				// Set PDF output size (Enter different values for xsize and
				// ysize if you want the output size to be different from that
				// of the original image. Note that xsize and ysize are output
				// sizes in points.)
				snowPanel.getSimage().IMGLOW_set_pdf_output(xsize, ysize);
				
				// Decompress and save each page of the file to the outputFileName
				int status = 0;
				String outputFileName = chooser.getSelectedFile().getPath() + ".pdf";
				
				for (int page = 0; page < snowPanel.getTotalPages(); page++)
				{
					status = snowPanel.getSimage().IMG_decompress_bitmap(fileName, page);
					
					if (status < 0)
					{
						break;
					}	
					
					status = snowPanel.getSimage().IMG_save_bitmap(outputFileName, 59);
						
					if (status < 0)
					{
						break;
					}					
				}
				
				// Display the status of the conversion
				if (status > -1)
				{
					System.out.println("PDF Conversion successful: Converted file" +
							           "saved as " + outputFileName);
				}
				else
				{
					System.out.println("PDF Conversion failed: " +
									   ErrorCodes.getErrorMessage(status));
				}
			}
			
			snowPanel.decompressImage(fileName);
			System.gc();
		}
	}
	
	class PageActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	if (e.getActionCommand().equals("Next Page"))
	    	{
	    		snowPanel.nextPage();
	    	}
	        else
	        {
	        	snowPanel.prevPage();
	        }        	
	    }
	}
	
	class ZoomActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent e)
	    {
	    	if (e.getActionCommand().equals("Zoom In"))
	    	{
	    		snowPanel.zoomIn(20);
	    		snowPanel.repaint();
	    	}
	    	else
	    	{
	    		snowPanel.zoomOut(20);
	    		snowPanel.repaint();
	    	}
	    }
	}
}
