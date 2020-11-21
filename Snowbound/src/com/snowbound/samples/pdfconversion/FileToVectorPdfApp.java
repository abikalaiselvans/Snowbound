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
 * This sample provides a UI that allows users to convert supported
 * document formats to searchable (vector) PDF format. 
 * 
 * This program uses the IMGLOW_extract_text method to extract text
 * from PTOCA, PCL, PDF, Ms Word, MS Excel, AFP, and RTF files.
 * Then the IMG_save_document method is used to save the extracted
 * text to a searchable PDF file.
 */
public class FileToVectorPdfApp extends JFrame
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
	 * The Constructor for the FileToPdfVectorUI frame
	 * 
	 * @param sPanel - The SnowboundJPanel object
	 * @param frameTitle - The title of the frame
	 */
	public FileToVectorPdfApp(SnowboundJPanel sPanel, String frameTitle)
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
		saveButton = new JButton("Save As Vector PDF");
		
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
		new FileToVectorPdfController(this);

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
	 * The main method that runs the File to Vector PDF Conversion App
	 * 
	 * @param args - unused
	 */
	public static void main(String[] args)
	{
		SnowboundJPanel snowPanel = new SnowboundJPanel();
		String frameTitle = "Snowbound Software :: " + 
		                    "File to Vector PDF Conversion";
		
		new FileToVectorPdfApp(snowPanel, frameTitle);
	}
}