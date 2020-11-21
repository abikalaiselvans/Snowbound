package com.snowbound.samples.pdfconversion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.snowbound.samples.common.SnowboundJPanel;

/**
 * This sample provides a UI that allows the user to convert other
 * file formats to the PDF file format. Use the "Load File" button
 * to select the file to be decompressed. Press the "Save As PDF"
 * button to convert the file to PDF format.
 * 
 * This program uses IMGLOW_set_pdf_output(int xsize, int ysize) to
 * set the output size of PDF file in points.
 * The dimensions of the image are converted from pixels to points
 * by multiplying by a factor of 72 and dividing by the DPI of the
 * file.
 */
public class FileToPdfApp extends JFrame
{
	private static final long serialVersionUID = 8594411445935937485L;
	
	private SnowboundJPanel snowPanel;
	
	private JMenuItem menuItemNext;
	private JMenuItem menuItemPrev;
	private JMenuItem menuItemZoomIn;
	private JMenuItem menuItemZoomOut;
	
	private JButton loadButton;
	private JButton saveButton;
	
	/**
	 * The Constructor for the FileToPdfUI frame
	 * 
	 * @param sPanel - The SnowboundJPanel object
	 * @param frameTitle - The title of the frame
	 */
	public FileToPdfApp(SnowboundJPanel sPanel, String frameTitle)
	{
		snowPanel = sPanel;
		
		// Create UI Components
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuView = new JMenu("View");
		
		menuItemNext = new JMenuItem("Next Page");
		menuItemPrev = new JMenuItem("Previous Page");
		menuItemZoomIn = new JMenuItem("Zoom In");
		menuItemZoomOut = new JMenuItem("Zoom Out");
		
		menuView.add(menuItemNext);
		menuView.add(menuItemPrev);
		menuView.add(new JSeparator());
		menuView.add(menuItemZoomIn);
		menuView.add(menuItemZoomOut);
		
		menuBar.add(menuView);
		
		loadButton = new JButton("Load File");
		saveButton = new JButton("Save As PDF");
		
		// Initially disabled buttons
		saveButton.setEnabled(false);
		menuItemNext.setEnabled(false);
		menuItemPrev.setEnabled(false);
		menuItemZoomIn.setEnabled(false);
		menuItemZoomOut.setEnabled(false);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 2, 2));
		buttonPanel.setPreferredSize(new Dimension(600, 40));
		buttonPanel.add(loadButton);
		buttonPanel.add(saveButton);
		
		// Create the Controller
		new FileToPdfController(this);

		// Place Components on Frame
		this.getContentPane().setLayout(new BorderLayout(4, 4));
		this.add(buttonPanel, BorderLayout.NORTH);
		this.add(snowPanel, BorderLayout.CENTER);
		
		// Initialize Frame
		this.setJMenuBar(menuBar);
		this.setTitle(frameTitle);
		this.setSize(600, 800);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Gets the SnowboundJPanel that displays the Snowbound Image
	 * 
	 * @return - The SnowboundJPanel object
	 */
	public SnowboundJPanel getSnowPanel()
	{
		return snowPanel;
	}
	
	/**
	 * Adds ActionListeners for Buttons and Menu Items
	 * 
	 * @param loadActionListener - ActionListener for the Load Button
	 * @param saveActionListener - ActionListener for the Save Button
	 * @param pageActionListener - ActionListener for the Next/Previous Page
	 *                             Menu Items
	 * @param zoomActionListener - ActionListener for the Zoom In/Zoom Out
	 *                             Menu Items
	 */
	public void addActionListeners(ActionListener loadActionListener,
			                       ActionListener saveActionListener,
			                       ActionListener pageActionListener,
			                       ActionListener zoomActionListener)
	{
		loadButton.addActionListener(loadActionListener);
		saveButton.addActionListener(saveActionListener);
		menuItemNext.addActionListener(pageActionListener);
		menuItemPrev.addActionListener(pageActionListener);
		menuItemZoomIn.addActionListener(zoomActionListener);
		menuItemZoomOut.addActionListener(zoomActionListener);
	}
	
	/**
	 * Enables Save, Next Page, Previous Page, Zoom In, and Zoom Out Buttons
	 * after a file has been opened.
	 */
	public void enableAfterOpen()
	{
		saveButton.setEnabled(true);
		menuItemNext.setEnabled(true);
		menuItemPrev.setEnabled(true);
		menuItemZoomIn.setEnabled(true);
		menuItemZoomOut.setEnabled(true);
	}
	
	/**
	 * The main method that runs the File to PDF Conversion App
	 * 
	 * @param args - unused
	 */
	public static void main(String[] args)
	{
		SnowboundJPanel snowPanel = new SnowboundJPanel();
		String frameTitle = "Snowbound Software :: " + "File to PDF Conversion";
		
		new FileToPdfApp(snowPanel, frameTitle);
	}
}

