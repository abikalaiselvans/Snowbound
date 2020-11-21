package com.snowbound.samples.pdfconversion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.snowbound.samples.common.SnowboundJPanel;
import com.snowbound.samples.common.dialog.SaveJDialog;

import Snow.ErrorCodes;
import Snow.Snowbnd;

/**
 * The Controller for the PDF to File Conversion Application
 */
public class PdfToFileController
{
	private PdfToFileApp UI;
	private SnowboundJPanel snowPanel;
	
	private String fileName;
	private String openDir;
	
	/**
	 * Constructs a Controller which handles events that
	 * occur in the UI.
	 * 
	 * @param ui - The UI of the PDFToFileApp
	 */
	public PdfToFileController(PdfToFileApp ui)
	{
		UI = ui;
		snowPanel = UI.getSnowPanel();
		
		// Add ActionListeners to UI components
		UI.addActionListeners(new LoadActionListener(), new SaveActionListener(),
                              new PageActionListener(), new ZoomActionListener());
	}
	
	class LoadActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// Open a Dialog that allows the user to set the PDF input
			// DPI and bit depth
			SetPdfInputDialog spid = new SetPdfInputDialog(UI, snowPanel);
			
			Integer dpi = spid.getDPI();
			Integer bpd = spid.getBPD();
			
			if (dpi == null || bpd == null)
			{
				return;
			}
				
			if (dpi < 1)
			{
				dpi = 1;
			}
			
			// Open a Dialog from the JFileChooser that allows the user
			// to select the input file to decompress
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));

			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
			
			int option = chooser.showOpenDialog(UI);
			
			// If file selection approved, decompress the image and set
			// PDF input
			if (option == JFileChooser.APPROVE_OPTION && 
				chooser.getSelectedFile() != null)
			{
				UI.enableAfterOpen();
				
				openDir = chooser.getCurrentDirectory().getPath();
				fileName = chooser.getSelectedFile().getPath();
				
				int status = 0;
				
				while (status > -1)
				{
					// Set DPI and bitmap pixel depth
					status = snowPanel.getSimage().IMGLOW_set_pdf_input(dpi, bpd);
					
					// Decompress image
					status = snowPanel.decompressImage(fileName);
					
					UI.setTitle("Snowbound Software :: PDF to File Conversion - " +
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
						
					break;
				}
				
				if (status < 0)
				{
					System.out.println("Error loading file: " +
					                    ErrorCodes.getErrorMessage(status));
				}
			}
		}
	}
	
	class SaveActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// Open a SaveJDialog that allows the user to select the
			// output file format.
			SaveJDialog sjd = new SaveJDialog(UI, snowPanel);
			
			String format = sjd.getSelectedValue();
			
			if (format == null)
			{
				return;
			}
				
			// Get the extension and format code of the selected format
			String formatExt = sjd.getFormatExtension(format);
			int formatCode = sjd.getFormatCode(format);
			
			// Open a Save Dialog from the JFileChooser for the user to
			// specify the output file name.
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter(
					              format + " (*" + formatExt + ")",
                                  formatExt.replace(".", "")));
			
			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
			
			int option = chooser.showSaveDialog(UI);
			
			if (option == JFileChooser.APPROVE_OPTION && 
					chooser.getSelectedFile() != null)
			{
				Snowbnd Simage = snowPanel.getSimage();
				String outputFileName = chooser.getSelectedFile().getPath();
				
				int status = 0;
				
				// Decompress and save each page of the file.
				if (snowPanel.getTotalPages() != 1)
				{
					for (int page = 0; page < snowPanel.getTotalPages(); page++)
					{
						status = Simage.IMG_decompress_bitmap(fileName, page);
						
						if (status < 0)
						{
							break;
						}
							
						// Determine whether the output file is one file or
						// multiple numbered files
						if (snowPanel.getFormatHash().isValidMultiFormat(formatCode))
						{
							status = Simage.IMG_save_bitmap(
									 outputFileName + formatExt, formatCode);
						}
						else
						{
							status = Simage.IMG_save_bitmap(
									 outputFileName + page + formatExt, formatCode);
						}
						
						if (status < 0)
						{
							break;
						}
							
					}
				}
				else
				{				
					status = Simage.IMG_save_bitmap(
							 outputFileName + formatExt, formatCode);					
				}
				
				// Print conversion status messages for multi-page and single
				// page conversions
				if (status > -1)
				{
					if (snowPanel.getTotalPages() != 1 &&
		                !(snowPanel.getFormatHash().isValidMultiFormat(formatCode)))
					{
						snowPanel.decompressImage(fileName);
						
						System.out.println("PDF Conversion successful." +
								           "Converted file saved to:");
						
						for (int page = 0; page < snowPanel.getTotalPages(); page++)
						{
							System.out.println(outputFileName + page + formatExt);
						}
					}
					else
					{
						System.out.println("PDF Conversion successful: Converted file" +
						                   "saved to " + outputFileName + formatExt);
					}
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