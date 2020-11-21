package com.snowbound.samples.redaction;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * This sample provides a GUI that allows the user to redact selected areas
 * of the PDF file. Begin by selecting the type of Redaction: Single-Page if
 * redacting on a single page or Multi-Page if redaction on multiple pages.
 * (*Note that redacting on a single page is possible in Multi-Page redaction
 * mode.) The Select button can be toggled to start and stop selection. Use
 * the Deselect button to remove all your selections. Use the Redact button
 * when you are ready to save the file with the selected areas redacted.
 * 
 * Note that for Single-Page redaction, if the page is changed or zoomed or
 * if the window is resized or minimized, all selections are lost.
 * For Multi-Page redaction, the same is true except when the page is
 * changed, the selections are preserved so that multiple pages can be
 * redacted.
 */
public class RedactionApp extends JFrame
{
	private static final long serialVersionUID = 3917711356550192732L;
	
	private JMenuBar mainMenuBar;
	
	private JMenu menuFile;
	private JMenu menuEdit;

	private JMenuItem menuItemOpen;
	private JMenuItem menuItemNextPage;
	private JMenuItem menuItemPrevPage;
	private JMenuItem menuItemExit;
	private JMenuItem menuItemZoomIn;
	private JMenuItem menuItemZoomOut;
	
	private JToggleButton selectTButton;
	private JButton deselectButton;
	private JButton redactButton;
	
	private SnowboundRedactJPanel snowPanel;
	
	/**
	 * Constructor for the RedactionUI JFrame.
	 * 
	 * @param snowp - The SnowboundJPanel object
	 * @param frameTitle - The title of the frame
	 */
	public RedactionApp(SnowboundRedactJPanel snowp, String frameTitle)
	{
		snowPanel = snowp;
		
		// Create UI Components
		mainMenuBar = new JMenuBar();
		
        menuFile = new JMenu("File");
        menuEdit = new JMenu("Edit");
     
        menuItemOpen = new JMenuItem("Open");
        menuItemNextPage = new JMenuItem("Next Page");
        menuItemPrevPage = new JMenuItem("Previous Page");
        menuItemExit = new JMenuItem("Exit");
        menuItemZoomIn = new JMenuItem("Zoom In");
        menuItemZoomOut = new JMenuItem("Zoom Out");
        
        selectTButton = new JToggleButton("Select");
        deselectButton = new JButton("Deselect All");
        redactButton = new JButton("Redact");
        
        // Initially disabled buttons
        menuItemNextPage.setEnabled(false);
        menuItemPrevPage.setEnabled(false);
        menuEdit.setEnabled(false);
        selectTButton.setEnabled(false);
        deselectButton.setEnabled(false);
        redactButton.setEnabled(false);
        
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemNextPage);
        menuFile.add(menuItemPrevPage);
        menuFile.add(menuItemExit);
        
        menuEdit.add(menuItemZoomIn);
        menuEdit.add(menuItemZoomOut);
        
        mainMenuBar.add(menuFile);
        mainMenuBar.add(menuEdit);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectTButton);
        buttonPanel.add(deselectButton);
        buttonPanel.add(redactButton);
        // End of Components
		
        // Place Components on the window
        this.setJMenuBar(mainMenuBar);
        this.getContentPane().setLayout(new BorderLayout());
        this.add(snowPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        // Asks the user to select the type of Redaction
        Object[] options = {"Single-Page", "Multi-Page"};
        int n = JOptionPane.showOptionDialog(this,
        									 "Select the type of redaction:",
        									 "Redaction Type",
        									 JOptionPane.DEFAULT_OPTION,
        									 JOptionPane.QUESTION_MESSAGE,
        									 null,
        									 options,
        									 null);

        // Create the RedactionController based on the specified type
        if (n == 0)
        {
        	new RedactionController(this);
        }
        else if (n == 1)
        {
        	new RedactionMultiPageController(this);
        }	
        else
        {
        	System.exit(0);
        }
        
        // Initialize Frame
        this.setTitle(frameTitle);
        this.setSize(600, 800);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Gets the SnowboundRedactJPanel where the Snowbound Image is displayed
	 * 
	 * @return The SnowboundRedactJPanel object
	 */
	public SnowboundRedactJPanel getSnowPanel()
	{
		return snowPanel;
	}
	
	/**
	 * Adds ActionListeners to the corresponding Menu Items.
	 * 
	 * @param openActionListener - ActionListener for the Open Menu Item
	 * @param pageActionListener - ActionListener for the Next Page/Previous Page
     *                             Menu Items
	 * @param exitActionListener - ActionListener for the Exit Menu Item
	 * @param zoomActionListener - ActionListener for Zoom In/Zoom Out Menu Items
	 */
	public void addMenuActionListeners(ActionListener openActionListener,
			                           ActionListener pageActionListener,
			                           ActionListener exitActionListener,
			                           ActionListener zoomActionListener)
	{
		menuItemOpen.addActionListener(openActionListener);
		menuItemNextPage.addActionListener(pageActionListener);
		menuItemPrevPage.addActionListener(pageActionListener);
		menuItemExit.addActionListener(exitActionListener);
		menuItemZoomIn.addActionListener(zoomActionListener);
		menuItemZoomOut.addActionListener(zoomActionListener);
	}
	
	/**
	 * Adds ActionListeners to the corresponding Buttons.
	 * 
	 * @param selectActionListener - ActionListener for the Select Button
	 * @param deselectActionListener - ActionListener for the Deselect All Button
	 * @param redactActionListener - ActionListener for the Redact Button
	 */
	public void addButtonActionListeners(ActionListener selectActionListener,
			                             ActionListener deselectActionListener,
			                             ActionListener redactActionListener)
	{
		selectTButton.addActionListener(selectActionListener);
		deselectButton.addActionListener(deselectActionListener);
		redactButton.addActionListener(redactActionListener);
	}
	
	/**
	 * Adds a MouseListener to the snowPanel.
	 * 
	 * @param panelMouseListener - MouseListener for the snowPanel
	 */
	public void addPanelMouseListener(MouseListener panelMouseListener,
			                          MouseMotionListener panelMMListener)
	{
		snowPanel.addMouseListener(panelMouseListener);
		snowPanel.addMouseMotionListener(panelMMListener);
	}
	
	/**
	 * Adds a ComponentListener to the Window Frame.
	 * 
	 * @param windowComponentListener - The ComponentListener for the window
	 */
	public void addWindowComponentListener(ComponentListener windowComponentListener)
	{
		this.addComponentListener(windowComponentListener);
	}
	
	/**
	 * Returns whether the Select button is selected or not.
	 * 
	 * @return - true if button is selected, false if not
	 */
	public boolean getIsSelected()
	{
		return selectTButton.isSelected();
	}
	
	/**
	 * Enables Next Page, Previous Page, Edit, and Select after a file is opened.
	 */
	public void enableAfterOpen()
	{
		menuItemNextPage.setEnabled(true);
		menuItemPrevPage.setEnabled(true);
		menuEdit.setEnabled(true);
		selectTButton.setEnabled(true);
	}
	
	/**
	 * Enables Deselect All and Redact after a selection has been made.
	 */
	public void enableAfterSelect()
	{
		deselectButton.setEnabled(true);
		redactButton.setEnabled(true);
	}
	
	/**
	 * Untoggles Select and disables Deselect All and Redact after the
	 * Frame is reset.
	 */
	public void disableAfterReset()
	{
		selectTButton.setSelected(false);
		deselectButton.setEnabled(false);
		redactButton.setEnabled(false);
	}
	
	/**
	 * Main method executes the Redaction App.
	 * 
	 * @param args - unused
	 */
	public static void main(String[] args)
	{
		SnowboundRedactJPanel snowPanel = new SnowboundRedactJPanel();
		String frameTitle = "Snowbound Software :: Redaction";
		
		new RedactionApp(snowPanel, frameTitle);
	}
}
