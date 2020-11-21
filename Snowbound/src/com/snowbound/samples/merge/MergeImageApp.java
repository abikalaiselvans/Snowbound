package com.snowbound.samples.merge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/**
 * This sample provides a UI that allows the user to merge two images
 * into one with the specified top, bottom, middle, left, and right
 * margins. 
 */
public class MergeImageApp extends JFrame
{
	private static final long serialVersionUID = -4305626481422874021L;
	
	private Color snowColor;
	
	private JTextField img1TextField;
	private JTextField img2TextField;
	
	private JButton img1BrowseButton;
	private JButton img2BrowseButton;
	
	private JPanel img1Panel;
	private JPanel img2Panel;
	
	private JButton mergeButton;

	/**
	 * Constructs the UI.
	 */
	public MergeImageApp()
	{
		snowColor = new Color(21, 149, 211);
		
		// Create Load Panel and Components for Image 1 and 2
		JLabel img1Label = new JLabel("Image 1: ");
		img1Label.setForeground(snowColor);
		
		img1TextField = new JTextField("Select an image...", 30);
		img1TextField.setEditable(false);
		
		img1BrowseButton = new JButton("Browse");
		img1BrowseButton.setName("img1Button");
		
		JPanel load1Pane = new JPanel();
		load1Pane.add(img1Label);
		load1Pane.add(img1TextField);
		load1Pane.add(img1BrowseButton);
		
		JLabel img2Label = new JLabel("Image 2: ");
		img2Label.setForeground(snowColor);
		
		img2TextField = new JTextField("Select an image...", 30);
		img2TextField.setEditable(false);
		
		img2BrowseButton = new JButton("Browse");
		img2BrowseButton.setName("img2Button");
		
		JPanel load2Pane = new JPanel();
		load2Pane.add(img2Label);
		load2Pane.add(img2TextField);
		load2Pane.add(img2BrowseButton);
		
		JPanel loadPane = new JPanel();
		loadPane.setLayout(new BoxLayout(loadPane, BoxLayout.Y_AXIS));
		loadPane.add(load1Pane);
		loadPane.add(load2Pane);
		
		// Create Logo Label
		ImageIcon logo = new ImageIcon("Snowbound_Software_logo_full_2012.png");
		JLabel logoLabel = new JLabel();
		logoLabel.setIcon(logo);
		
		// Add Logo and Load Panel to Top Panel
		JPanel topPane = new JPanel();
		topPane.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
		topPane.setLayout(new BoxLayout(topPane, BoxLayout.X_AXIS));
		topPane.add(logoLabel);
		topPane.add(Box.createHorizontalStrut(10));
		topPane.add(new JSeparator(JSeparator.VERTICAL));
		topPane.add(Box.createHorizontalStrut(10));
		topPane.add(loadPane);
		
		// Create Titled Preview Panels for Image 1 and 2
		img1Panel = new JPanel(new BorderLayout());
		TitledBorder tBorder1 = BorderFactory.createTitledBorder("Image 1 Preview");
		tBorder1.setTitleColor(snowColor);
		img1Panel.setBorder(tBorder1);
		
		img2Panel = new JPanel(new BorderLayout());
		TitledBorder tBorder2 = BorderFactory.createTitledBorder("Image 2 Preview");
		tBorder2.setTitleColor(snowColor);
		img2Panel.setBorder(tBorder2);
		
		// Add Preview Panels to Main Preview Panel
		JPanel previewPane = new JPanel();
		previewPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
		previewPane.setLayout(new BoxLayout(previewPane, BoxLayout.X_AXIS));
		
		previewPane.add(img1Panel);
		previewPane.add(Box.createHorizontalStrut(10));
		previewPane.add(img2Panel);
		
		// Create Merge Button and Panel
		mergeButton = new JButton("Merge");
		
		JPanel mergePane = new JPanel();
		mergePane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		mergePane.add(mergeButton);
		
		// Create the UI Controller
		new MergeImageController(this);
		
		// Add Panels to Frame
		this.getContentPane().setLayout(new BorderLayout());
		this.add(topPane, BorderLayout.NORTH);
		this.add(previewPane, BorderLayout.CENTER);
		this.add(mergePane, BorderLayout.SOUTH);
		
		// Initialize Frame
		this.setTitle("Snowbound Software :: Merge Images");
		this.setSize(750, 600);
		this.setMinimumSize(new Dimension(750, 400));
		this.setLocationByPlatform(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Sets the text of the TextField for Image 1
	 * 
	 * @param text - The given text
	 */
	public void setTextField1Text(String text)
	{
		img1TextField.setText(text);
	}
	
	/**
	 * Sets the text of the TextField for Image 2
	 * 
	 * @param text - The given text
	 */
	public void setTextField2Text(String text)
	{
		img2TextField.setText(text);
	}
	
	/**
	 * Adds ActionListeners to the corresponding Buttons
	 * 
	 * @param browseActionListener - A BrowseActionListener object
	 * @param mergeAction Listener - A MergeActionListener object
	 */
	public void addActionListeners(ActionListener browseActionListener,
			                       ActionListener mergeActionListener)
	{
		img1BrowseButton.addActionListener(browseActionListener);
		img2BrowseButton.addActionListener(browseActionListener);
		
		mergeButton.addActionListener(mergeActionListener);
	}
	
	/**
	 * Adds a PreviewPanel to display Image 1 in img1Panel
	 * 
	 * @param previewPanel - A PreviewPanel object that displays Image 1
	 */
	public void addPreviewPanel1(PreviewPanel previewPanel)
	{
		img1Panel.removeAll();
		img1Panel.add(previewPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Adds a PreviewPanel to display Image 2 in img2Panel
	 * 
	 * @param previewPanel - A PreviewPanel object that displays Image 2
	 */
	public void addPreviewPanel2(PreviewPanel previewPanel)
	{
		img2Panel.removeAll();
		img2Panel.add(previewPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Gets the JPanel for Image 2
	 * 
	 * @return - img2Panel
	 */
	public JPanel getImg2Panel()
	{
		return img2Panel;
	}

	/**
	 * Runs the Merge Image Application.
	 * 
	 * @param args - unused
	 */
	public static void main(String[] args)
	{
		new MergeImageApp();
	}
}
