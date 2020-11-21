package com.snowbound.samples.redaction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import Snow.ErrorCodes;

/**
 * The Redaction Controller for Multi-Page Redaction
 */
public class RedactionMultiPageController
{
	private RedactionApp UI;
	private SnowboundRedactJPanel snowPanel;
	
	private String fileName;
	private String openDir;
	
	private HashMap<Integer, ArrayList<Rectangle>> pageRects;
	private boolean select;
	private Rectangle selrect;
	
	/**
	 * Constructor for the RedactionController which handles events
	 * that occur in the UI
	 * 
	 * @param ru - The RedactionUI object
	 */
	public RedactionMultiPageController(RedactionApp ru)
	{
		UI = ru;
		snowPanel = UI.getSnowPanel();
		
		select = false;
		
		// Initialize pageRects: A HashMap of the page number to an ArrayList
		//                       of the Rectangles on that page
		pageRects = new HashMap<Integer, ArrayList<Rectangle>>();
		pageRects.put(0, new ArrayList<Rectangle>());
		
        // Add Listeners to UI Components
		UI.addMenuActionListeners(new OpenActionListener(), new PageActionListener(),
				                  new ExitActionListener(), new ZoomActionListener());
		
		UI.addButtonActionListeners(new SelectActionListener(),
                                    new DeselectActionListener(),
                                    new RedactActionListener());

		UI.addPanelMouseListener(new PanelMouseListener(), new PanelMouseListener());
        
        UI.addWindowComponentListener(new WindowComponentListener());
	}
	
	class OpenActionListener implements ActionListener
	{	
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter("PDF file (*.pdf)", "pdf"));
			
			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
			
			int option = chooser.showOpenDialog(UI);
			
			if (option == JFileChooser.APPROVE_OPTION && 
				chooser.getSelectedFile() != null)
			{
				UI.enableAfterOpen();
				
				openDir = new String(chooser.getCurrentDirectory().getPath());
				fileName = new String(chooser.getSelectedFile().getPath());
				
				snowPanel.decompressImage(fileName);
				UI.setTitle("Snowbound Software :: Redaction - " +
				            chooser.getSelectedFile().getName());
				
				// Initialize the ArrayList for each page of the file
				for (int page = 0; page < snowPanel.getTotalPages(); page++)
				{
					pageRects.put(page, new ArrayList<Rectangle>());
				}
			}
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
	        	
	    	// Add Rectangles to rectMap in snowPanel so they are drawn
	    	snowPanel.addRectsToRectMap(pageRects.get(snowPanel.getPage()));
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
			reset();
	    }
	}
	
	class SelectActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (UI.getIsSelected())
			{
				select = true;
			}
			else
			{
				select = false;
			}
		}	
	}
	
	class PanelMouseListener implements MouseListener, MouseMotionListener
	{
		// MouseMotionListener methods
		public void mouseDragged(MouseEvent e)
		{
			if (select)
			{
				if (e.getX() > selrect.x && e.getY() > selrect.y)
				{
					drawPen();
					selrect.width = e.getX() - selrect.x;
					selrect.height = e.getY() - selrect.y;
					drawPen();
				}
			}
		}

		public void mouseMoved(MouseEvent e)
		{
		}

		// MouseListener methods
		public void mouseClicked(MouseEvent e)
		{
			if (select)
			{
				selrect = new Rectangle();
				selrect.x = e.getX();
				selrect.y = e.getY();
				selrect.width = 1;
				selrect.height = 1;
				drawPen();
			}
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}

		public void mousePressed(MouseEvent e)
		{
			if (select)
			{
				selrect = new Rectangle();
				selrect.x = e.getX();
				selrect.y = e.getY();
				selrect.width = 0;
				selrect.height = 0;
				drawPen();
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			if (select)
			{
				drawPen();
				
				Point pt1 = new Point(selrect.x, selrect.y);
				Point pt2 = new Point(selrect.x + selrect.width,
							          selrect.y + selrect.height);
				
				if (pt1.x > pt2.x)
				{
					selrect.x = pt1.x;
					pt1.x = pt2.x;
					pt2.x = selrect.x;
				}
				
				if (pt1.y > pt2.y)
				{
					selrect.y = pt1.y;
					pt1.y = pt2.y;
					pt2.y = selrect.y;
				}
				
				drawPen();
				
				UI.enableAfterSelect();
				
				pageRects.get(snowPanel.getPage()).add(selrect);
				
				selrect = null;
			}
		}
	}
	
	class DeselectActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			snowPanel.repaint();
			reset();
		}
	}
	
	class RedactActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// Save Output File Dialog
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter("PDF file (*.pdf)", "pdf"));
			
			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
			
			int option = chooser.showSaveDialog(UI);
			
			// If file selected, save redacted file as the selected file
			if (option == JFileChooser.APPROVE_OPTION &&
				chooser.getSelectedFile() != null)
			{
				openDir = chooser.getCurrentDirectory().getPath();
				
				String outputFileName = chooser.getSelectedFile().getPath() + ".pdf";
				
				redact(fileName, outputFileName);
				
				System.gc();
			}
			
			snowPanel.repaint();
			reset();
		}
	}
	
	class ExitActionListener implements ActionListener
	{
	    public void actionPerformed(ActionEvent e)
	    {
	        UI.dispose();
	        System.exit(0);
	    }
	}
	
	class WindowComponentListener implements ComponentListener
	{
		public void componentHidden(ComponentEvent e)
		{
			reset();
		}

		public void componentMoved(ComponentEvent e)
		{
		}

		public void componentResized(ComponentEvent e)
		{
			reset();
		}

		public void componentShown(ComponentEvent e)
		{
		}
	}
	
	/**
	 * Draws a rectangle with the dimensions of the selection
	 */
	private void drawPen()
	{
	    Graphics g;
        
        if (selrect != null)
        {
        	g = snowPanel.getGraphics();	
        	g.setXORMode(Color.WHITE);
        	g.drawRect(selrect.x, selrect.y, selrect.width, selrect.height);
        	g.setPaintMode();
        }
	}
	
	/**
	 * Redacts the Rectangles on each page of the pageRects HashMap from
	 * the input file and saves the redacted file to the specified output
	 * file.
	 * 
	 * @param inputFile - The name of the file being redacted
	 * @param outputFile - The name of the redacted file
	 */
	private void redact(String inputFile, String outputFile)
	{
		int status = 0;
		
		// Loop through each page of the file 
		for (int page = 0; page < snowPanel.getTotalPages(); page++) 
		{
			ArrayList<Rectangle> rArrayList = pageRects.get(page);

			Rectangle2D.Double[] rArray = new Rectangle2D.Double[rArrayList.size()];
			
			// Loop through each Rectangle in the ArrayList of Rectangles on that page
			// and add it to rArray (An Array of Rectangle2D.Double) so that it can be
			// used by the redact method
			for (int i = 0; i < rArrayList.size(); i++)
			{
				Rectangle rect = rArrayList.get(i);
				
				convertRectDimensions(rect);
				
				// Add a new Rectangle2D.Double with the selected rectangle's
				// coordinates to rArrayList
				Rectangle2D.Double newRect = new Rectangle2D.Double(rect.x,
																    rect.y,
																    rect.width,
																    rect.height);
				// Add the new Rectangle2D.Double to the Array
				rArray[i] = newRect;
			}
			
			// Call to IMGLOW_redact_page method for each page of the file
			status = snowPanel.getSimage().IMGLOW_redact_page(inputFile,
											                  outputFile,
											                  rArray,
											                  page,
											                  0);
			if (status != 0)
			{
				break;
			}
				
			// Set fileName equal to outputFileName so previous redactions are saved
			inputFile = outputFile;
		}
		
		if (status == 0)
		{
			System.out.println("Redaction successful. Redacted file saved to " + 
		                       outputFile);
		}
		else
		{
			System.out.println("Redaction failed: "+ 
		                        ErrorCodes.getErrorMessage(status));
		}
	}
	
	/**
	 * Converts the Dimensions of the Rectangle so it is drawn in the correct
	 * location on the PDF file.
	 * 
	 * @param rect - The given Rectangle object
	 */
	private void convertRectDimensions(Rectangle rect)
	{
		Point pt1 = new Point(rect.x, rect.y);
		Point pt2 = new Point(rect.x + rect.width,
							  rect.y + rect.height);
		
		// Map point window coordinates to image coordinates
		snowPanel.mapWindowToImage(pt1);
		snowPanel.mapWindowToImage(pt2);
		
		// Resize Rectangle point coordinates to PDF point coordinates.
		// The dimensions of the redacted Rectangle must be in points.
		// A point is 1/72 of an inch. 
		rect.setRect(pt1.x * 72 / SnowboundRedactJPanel.PDF_DPI,
				     pt1.y * 72 / SnowboundRedactJPanel.PDF_DPI,
				     (pt2.x - pt1.x) * 72 / SnowboundRedactJPanel.PDF_DPI,
				     (pt2.y - pt1.y) * 72 / SnowboundRedactJPanel.PDF_DPI);
		
		// Change rectangle's y coordinate so the rectangle is drawn
		// from the lower left corner because the redact method draws
		// redacted rectangles from the lower left corner instead of the
		// upper left corner.
		int ysize = snowPanel.getSimage().getHeight() * 72 / SnowboundRedactJPanel.PDF_DPI;
		rect.y = ysize - (rect.y + rect.height);
	}
	
	/**
	 * Resets the Frame so that all selected rectangles are deselected.
	 */
	private void reset()
	{
		// Clear Rectangles from pageRects and rectMap HashMaps
		for (ArrayList<Rectangle> ralCtrl : pageRects.values())
		{
			ralCtrl.clear();
		}
		
		select = false;
		
		snowPanel.clearRectMap();
		
		UI.disableAfterReset();
	}
	
}
