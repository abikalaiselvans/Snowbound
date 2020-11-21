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
 * This sample provides a UI that allows the user to convert PDF
 * files to other writable file formats. 
 * 
 * This program uses the IMGLOW_set_pdf_input(int dpi, int bits_pix)
 * method to convert PDf files into bitmaps when decompressed. The
 * user is able to specify the bitmap size and pixel depth of the
 * resulting bitmap before loading the file.
 *
 * Note that some file conversions are not supported because the
 * output file format does not support the specified DPI or bitmap
 * pixel depth. See Appendix A of the Java SDK Manual on "Supported
 * File Formats" if you receive the "Format not supported" error.
 */
public class PdfToFileApp extends JFrame
{
	private static final long serialVersionUID = 3867021982754652852L;
	
	private SnowboundJPanel snowPanel;
	
	private JMenuItem menuItemNext;
	private JMenuItem menuItemPrev;
	private JMenuItem menuItemZoomIn;
	private JMenuItem menuItemZoomOut;
	
	private JButton loadButton;
	private JButton saveButton;
	
	/**
	 * Constructor for the PdfToFileUI frame
	 * 
	 * @param sPanel - The SnowboundJPanel object
	 * @param frameTitle - The title of the frame
	 */
	public PdfToFileApp(SnowboundJPanel sPanel, String frameTitle)
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
		
		loadButton = new JButton("Load PDF File");
		saveButton = new JButton("Save File As");
		
		// Initially disabled buttons
		saveButton.setEnabled(false);
		menuItemNext.setEnabled(false);
		menuItemPrev.setEnabled(false);
		menuItemZoomIn.setEnabled(false);
		menuItemZoomOut.setEnabled(false);
		
		JPanel buttonPane = new JPanel(new GridLayout(1, 2, 2, 2));
		buttonPane.setPreferredSize(new Dimension(600, 40));
		buttonPane.add(loadButton);
		buttonPane.add(saveButton);
		
		// Create the Controller
		new PdfToFileController(this);
		
		// Place Components on Frame
		this.getContentPane().setLayout(new BorderLayout(4, 4));
		this.add(buttonPane, BorderLayout.NORTH);
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
	 * The main method that runs the PDF to File Conversion App
	 * 
	 * @param args - unused
	 */
	public static void main(String[] args)
	{
		SnowboundJPanel snowPanel = new SnowboundJPanel();
		String frameTitle = "Snowbound Software :: " + "PDF to File Conversion";
		
		new PdfToFileApp(snowPanel, frameTitle);
	}
}
