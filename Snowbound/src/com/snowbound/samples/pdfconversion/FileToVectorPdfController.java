package com.snowbound.samples.pdfconversion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.snowbound.samples.common.SnowboundJPanel;

import Snow.ErrorCodes;
import Snow.Snowbnd;

/**
 * The Controller for the File to Vector PDF Conversion App
 */
public class FileToVectorPdfController
{
	private FileToVectorPdfApp UI;
	private SnowboundJPanel snowPanel;
	
	private String fileName;
	private String openDir;
	
	/**
	 * Constructor for FileToVectorPdfController
	 * 
	 * @param ui - The UI for the FileToVectorPdfApp
	 */
	public FileToVectorPdfController(FileToVectorPdfApp ui)
	{
		UI = ui;
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
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter("PCL (*.pcl)", "pcl"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("ATP/MO:DCA (*.mca)", "mca"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("PTOCA (*.mca)", "mca"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("RTF (*.rtf)", "rtf"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("MS Word (*.doc)", "doc"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("MS Excel (*.xls)", "xls"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
			
			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
			
			int option = chooser.showOpenDialog(UI);
			
			// If selected file approved, decompress the file
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
						UI.setSize(800, (int)(900 * (height / width)));
					}
					else
					{
						UI.setSize((int)(710 * (width/height)), 800);
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
			Snowbnd Simage = snowPanel.getSimage();
			
			// Open a Save Dialog that allows the user to specify the
			// output file name
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
			
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
				
				
				
				
				String outputFileName = chooser.getSelectedFile().getPath() + ".pdf";
				
				int[] length = new int[2];
				int[] error = new int[2];
				
				int status =0;
				if (status  > -1)
				{
					// Decompress and save each page of the file to the outputFileName
					for (int page = 0; page < snowPanel.getTotalPages(); page++)
					{
						
						// Extract the text on the page to a byte array
						byte[] extractedText = Simage.IMGLOW_extract_text(
								               fileName, length, error, page);
						
						// Save extracted text as a searchable PDF document
						status = Simage.IMG_save_document(
								 outputFileName, extractedText, 59);
							
						if (status < 0)
						{
							break;
						}					
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

