package com.snowbound.samples.scanning;

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Displays a list of available Twain Devices on the System.
 */
public class ScanSourceListApp
{
	/**
	 * A String Array of the names of the available Twain Devices on the System.
	 */
	private String[] sourceList;
	
	/**
	 * Constructor for the ScanSourceListApp that creates a frame to display
	 * a list of the available Twain Devices.
	 */
	public ScanSourceListApp()
	{		
		JPanel labelPane = new JPanel();
		labelPane.add(new JLabel("Available Twain Devices"));
		
		JTextArea textArea = new JTextArea();
		textArea.setMargin(new Insets(8, 8, 8, 0));
		textArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		ScanInterface sli = new ScanInterface();
		
		
		// Call native SDK method from ScanInterface
		sourceList = sli.IMG_scan_get_source_list();
		
		for (int i = 0; i < sourceList.length; i++)
		{
			textArea.append(sourceList[i] + "\n");
		}
		
		
		JFrame frame = new JFrame();
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.add(labelPane, BorderLayout.NORTH);
		frame.add(scrollPane, BorderLayout.CENTER);
		
		frame.setTitle("Snowbound Software :: Scanning");
		frame.setSize(300, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * main method executes the Scan Source List App.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		new ScanSourceListApp();
	}
}
