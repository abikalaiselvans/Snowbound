/**
 * FontDisplay
 *
 * This handy utility from Snowbound Software will display the fonts that are available
 * in your Java environment.  These are the same fonts that will be available to
 * RasterMaster Java and VirtualViewer.
 *
 * If you do not see the font your documents use, you should install the font.
 * Snowbound's font configuration guide provides more information:
 *
 * http://www.snowbound.com/sites/snowbound.com/files/PDFs/Support/RM_Manuals/SnowboundSofwareConfiguringNonStandardFonts.pdf
 *
 * Copyright (C) 2013 by Snowbound Software Corp. All rights reserved.
 *
 * SnowBound customers may use this code as is or with their own modifications
 * to customize their use of a Snowbound product within their company.
 *
 * @author: Cailin Li
 * @version: 1.0
 */
package com.snowbound.samples.snippets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * Displays all available Fonts on the System
 */
public class FontDisplay extends JFrame
{
	private static final long serialVersionUID = 7775058902413831737L;

	/**
	 * Constructor for the Font Display
	 */
	public FontDisplay()
	{
		// Get all Fonts from the System
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = ge.getAllFonts();

		// Build a list of styled Font Names
		StringBuilder sb = new StringBuilder("<html><body style='font-size: 18px'>");
		for (Font font : fonts)
		{
			sb.append("<p style='font-family: ");
			sb.append(font.getFontName());
			sb.append("'>");
			sb.append(font.getFontName());
			sb.append("</p>");
		}

		// Add built list to EditorPane
		JEditorPane fontEP = new JEditorPane();
		fontEP.setMargin(new Insets(10, 10, 10, 10));
		fontEP.setContentType("text/html");
		fontEP.setText(sb.toString());
		fontEP.setEditable(false);

		// Add EditorPane to ScrollPane
		JScrollPane scrollPane = new JScrollPane(fontEP);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		// Create a Label for the EditorPane
		JLabel titleLabel = new JLabel("Available Fonts on System",
                                       SwingConstants.CENTER);
		titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 17));
		titleLabel.setForeground(new Color(21, 149, 211));

		ImageIcon logo = new ImageIcon("Snowbound_Software_logo_full_2012.png");
		JLabel logoLabel = new JLabel();
		logoLabel.setIcon(logo);
		JPanel logoPane = new JPanel();
		logoPane.setLayout(new BoxLayout(logoPane, BoxLayout.X_AXIS));
		logoPane.add(logoLabel);
		logoPane.add(Box.createHorizontalStrut(12));
		logoPane.add(new JSeparator(JSeparator.VERTICAL));
		logoPane.add(Box.createHorizontalStrut(6));
		// Add Label to Panel
		JPanel labelPane = new JPanel(new BorderLayout(0, 10));
		labelPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
		labelPane.add(logoPane, BorderLayout.WEST);
		labelPane.add(titleLabel, BorderLayout.CENTER);
		labelPane.add(new JSeparator(), BorderLayout.SOUTH);

		// Add Panels to Frame
		this.getContentPane().setLayout(new BorderLayout());
		this.add(labelPane, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);

		// Initialize Frame
		this.setTitle("Snowbound Software :: Font Display");
		this.setSize(500, 650);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Runs the FontDisplay program.
	 *
	 * @param args - unused
	 */
	public static void main(String[] args)
	{
		new FontDisplay();
	}
}
