/**
 * Copyright (C) 2007-2017 by Snowbound Software Corp. All rights reserved.
 * This is example code for all SnowBound customers to freely copy and use however they wish.
 * 
 * Authors: Alan Shepard, Barbara Bazemore, Bismark Frimpong
 * 
 * Requires Java 1.6x and later
 */
package com.snowbound.samples.convert;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import com.snowbound.samples.common.FileProcessor;

import Snow.ErrorCodes;
import Snow.Format;
import Snow.FormatHash;
import Snow.MessageBox;
import Snow.Snowbnd;

//import com.snowbound.common.utils.Logger;

/**
 * This class will represent all pages which need to be converted for all documents in the input directory. This class allows us to know the output name and format for each file
 * ahead of time, and lets us know how many pages will need to processed in total. We use this information to update the progress bar accurately.
 */
class SplashWindow extends JWindow {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5280658118880208238L;
	private java.awt.Image splashImage;
	private ImageIcon splashIcon;
	private JProgressBar progress;
	JPanel panelLayout = new JPanel();
	int maxProgress = 100;
	int startProgress = 0;

	/**
	 * Create the application.
	 */
	public SplashWindow() {
		super();
		if (splashIcon != null) {
			return;
		}
		/*
		 * progresPanel = new JPanel(); getContentPane().add(progresPanel,BorderLayout.SOUTH); this.pack(); progresPanel.setVisible(true);
		 */

	}

	public void showSplash() {
		splashImage = Toolkit.getDefaultToolkit().getImage(SplashWindow.class.getResource("/com/resource/splashScreen.png"));

		splashIcon = new ImageIcon(splashImage);
		setSize(splashIcon.getIconWidth(), splashIcon.getIconHeight());
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		java.awt.Rectangle frame = getBounds();
		int x_size = (int) (screenDimension.width - frame.getWidth() - 490);
		int y_size = (int) (screenDimension.height - frame.getHeight() - 380);
		setLocation(x_size, y_size);
		setVisible(true);
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(splashImage, 0, 0, this);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	}

	public void showProgressBar(int counter) {

		if (progress != null) {
			this.remove(progress);
			this.setSize(this.getWidth(), this.getHeight() - 16);
		}
		progress = new JProgressBar();
		progress.setBorderPainted(true);
		progress.setStringPainted(true);
		progress.setSize(this.getWidth(), 50);
		progress.setLocation(0, this.getHeight());
		progress.setSize(this.getWidth(), this.getHeight() + 16);
		progress.setMinimum(startProgress);
		progress.setMaximum(maxProgress);
		progress.setVisible(true);
		this.getContentPane().add(progress);

	}
}

class Job {
	// Identifier for the job
	private int jobNumber;
	// Estimated remaining CPU usage time
	private int maxTimeRemaining;
	// Current status of the process
	private int status;
	// Time at which the job entered the system
	private int timeEntered;
	// Amount of time spent on the ready queue waiting for the CPU
	private double waitTime;
	// Time from entrance to completion
	private double turnAroundTime;
	// Number of times this process had the CPU allocated to it
	private int cpuCount;
	// Actual CPU usage time
	// private int cpuTime;
	private long cpuTime = System.nanoTime();
	// The identifier for the next job to enter the system
	private static int nextJob = 1;
	private int timeStamp = 0;
	private double totalTurnaroundTime;

	/**
	 * Create a new process with the estimated CPU usage, and entrance time
	 * 
	 * @param est
	 *            estimated CPU usage for the new job
	 * @param timeIn
	 *            time the new job entered the system
	 */
	public Job(int est, int timeIn) {
		// Assign this job an id
		this.jobNumber = Job.nextJob;
		// Increment the job counter
		Job.nextJob++;
		this.maxTimeRemaining = est;
		this.timeEntered = timeIn;
		// this.status = Scheduler.READY;

		// Initialize the statistics for this job
		this.initializeStatistics();
	}

	/**
	 * Sets all the statistics for this job to zero at its entrance into the simulation.
	 */
	private void initializeStatistics() {
		this.waitTime = 0.0;
		this.turnAroundTime = 0.0;
		this.totalTurnaroundTime = 0.0;
		this.cpuCount = 0;
		// this.cpuTime = System.nanoTime();
	}

	/**
	 * Changes the current status of the this job as it is moved between the CPU and the ready and waiting queues of the simulation
	 * 
	 * @param newStatus
	 *            the new status for the job
	 */
	private void setStatus(int newStatus) {
		this.setStatus(status);
	}

	/**
	 * Retrieves the identifier for this job
	 * 
	 * @return the job number for this process
	 */
	public int getJobNum() {
		return this.jobNumber;

	}

	/**
	 * Retrieves the maximum remaining CPU usage time for this job
	 * 
	 * @return the max remaining CPU usage
	 */
	/*
	 * public long getRemainingCPUTime() { //double elapsedTimeInSec = (System.nanoTime() - cpuTime) * 1.0e-9; //System.out.println(elapsedTimeInSec); return (int) this.cpuTime;
	 * 
	 * 
	 * }
	 */

	/**
	 * Retrieves the total wait time for this job
	 * 
	 * @return the total time this job spent waiting
	 */
	public int getWaitTime() {
		return (int) this.waitTime;

	}

	/**
	 * Retrieves the turnaround time for this job
	 * 
	 * @return the turnaround time for this job
	 */
	public int getTurnAroundTime() {
		return (int) this.turnAroundTime;

	}

	/**
	 * Update the statistics for this job after it waits for a tick on the I/O queue
	 */
	public void waitTick() {
		// waitTime = (turnAroundTime - maxTimeRemaining - timeEntered) ;
		waitTime = (turnAroundTime + maxTimeRemaining + timeEntered);
		// turnAroundTime = (int) (waitTime + maxTimeRemaining);
		turnAroundTime = (int) (waitTime + maxTimeRemaining);
	}

	/**
	 * Update the statistics for this job after it sits for a tick on the ready queue
	 */
	public void readyTick() {
		status = (int) (waitTime - cpuTime);
	}

	/**
	 * Update the statistics for this job after it executes for a tick on the CPU
	 */
	public void cpuTick() {
		cpuTime = jobNumber + cpuCount;
	}

	/**
	 * Mark this job as successfully completed
	 */
	public void complete() {
		System.out.println("This Job was Successfully Completed!!!");
	}

	/**
	 * Mark this job as terminated
	 */
	public void terminate() {
		// System.exit(0);
		System.out.println("This Job was Terminated!!!");
	}

	@Override
	public String toString() {
		return (" Job Number: " + jobNumber + "\n" + "CPU Time: " + (double) (System.nanoTime() - cpuTime) * 1.0e-9 + "\n" + " Wait Time: "
				+ (double) waitTime / 1000 + "\n" + " Turn Around Time: " + (double) turnAroundTime / 1000);
	}

	/**
	 * Retrieves the running statistics for this job
	 * 
	 * @return a string containing the statistics for this job
	 */
	public String getStatistics() {
		return (toString());
	}
}

class DocumentInput extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3557766450223844186L;
	private final JPanel contentPanel = new JPanel();
	private JLabel dpiLabel;
	private JLabel bitsPerPixelLabel;
	private JLabel descriptionLabel;
	public static JComboBox dpiComboBox;
	public static JComboBox bitsPerPixelCombo;
	private JButton cancelButton;
	private JButton okButton;
	public static int numDPI;
	public static int numBits;

	public DocumentInput() {
		setResizable(false);
		setModal(true);
		// setType(Type.POPUP);
		setTitle("Document Input Settings");
		setBounds(100, 100, 216, 160);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			dpiLabel = new JLabel("Dots Per Inch");
			dpiLabel.setBounds(10, 29, 74, 14);
			contentPanel.add(dpiLabel);
		}
		{
			bitsPerPixelLabel = new JLabel("Bits Per Pixel");
			bitsPerPixelLabel.setBounds(10, 57, 84, 14);
			contentPanel.add(bitsPerPixelLabel);
		}
		{
			descriptionLabel = new JLabel("Change Document Settings");
			descriptionLabel.setBounds(30, 5, 160, 14);
			contentPanel.add(descriptionLabel);
		}

		String[] numValues = { "1", "24" };
		bitsPerPixelCombo = new JComboBox();
		for (int i = 0; i < numValues.length; i++) {
			bitsPerPixelCombo.addItem(new Integer(numValues[i]));
		}
		bitsPerPixelCombo.setBounds(83, 51, 74, 26);
		bitsPerPixelCombo.setSelectedItem(null);
		/*
		 * int[] numValuesConvert = new int[2]; for(int i = 0; i < numValues.length; i++) { try { numValuesConvert[i] = Integer.parseInt(numValues[i]); } catch(Exception ex) {
		 * System.out.println(" String parse Error " + ex.getMessage()); } }
		 */
		contentPanel.add(bitsPerPixelCombo);

		{
			// Convert String items to Integer Objects
			String[] numItems = { "100", "200", "300", "400" };
			dpiComboBox = new JComboBox();
			for (int i = 0; i < numItems.length; i++) {
				dpiComboBox.addItem(new Integer(numItems[i])); // add String
																// items as
																// Integer to
																// ComboBox
			}
			dpiComboBox.setBounds(83, 23, 74, 26);
			dpiComboBox.setSelectedItem(null);
			/*
			 * int[] numItemsConvert = new int[4]; for(int i = 0; i < numItems.length; i++) { try { numItemsConvert[i] = Integer.parseInt(numItems[i]); } catch(Exception ex) {
			 * System.out.println(" String Parse Error " + ex.getMessage()); } }
			 */

			contentPanel.add(dpiComboBox);
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}

			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			// get the selected item as Integer
			Integer intDpi = (Integer) dpiComboBox.getSelectedItem();
			numDPI = intDpi.intValue(); // store selected item to numDPI, so we
										// can use it later
			Integer intBitpixel = (Integer) bitsPerPixelCombo.getSelectedItem();
			numBits = intBitpixel.intValue(); // store selected item to numBits,
												// so we can use it later
			System.out.println(" DPI: " + numDPI + " Bits Pixel: " + numBits);
			dispose();
		} else if (e.getActionCommand().equals("Cancel"))
			;
		{
			dispose();
		}
	}

}

// PDF DialogBox class
class PDFInputDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4908938677437902257L;
	private final JPanel contentPanel = new JPanel();
	private JLabel dpiLabel;
	private JLabel bits_pix_Label;
	private JButton okButton;
	private JButton cancelButton;
	public static JComboBox dpiComboBox;
	public static JComboBox bits_pix_ComboBox;
	Snowbnd s = new Snowbnd();
	public static int numDPI;
	public static int numBits_pix;

	public PDFInputDialog() {
		setResizable(false);
		setModal(true);
		// setType(Type.POPUP);
		setTitle("PDF Input Settings");
		setBounds(100, 100, 216, 160);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		{
			dpiLabel = new JLabel("Dots Per Inch");
			dpiLabel.setBounds(10, 29, 74, 14);
			contentPanel.add(dpiLabel);
		}
		{
			bits_pix_Label = new JLabel("Bits Per Pixel");
			bits_pix_Label.setBounds(10, 57, 84, 14);
			contentPanel.add(bits_pix_Label);
		}

		// Convert String items to Integer Objects
		String[] numValues = { "1", "8", "24" };
		bits_pix_ComboBox = new JComboBox();
		for (int i = 0; i < numValues.length; i++) {
			bits_pix_ComboBox.addItem(new Integer(numValues[i]));
		}
		bits_pix_ComboBox.setBounds(83, 51, 74, 26);
		bits_pix_ComboBox.setSelectedItem(null);
		/*
		 * int[] numValuesConvert = new int[3]; for(int i = 0; i < numValues.length; i++) { try { numValuesConvert[i] = Integer.parseInt(numValues[i]); } catch(Exception ex) {
		 * System.out.println("Could not parse String" + ex.getMessage()); } }
		 */
		contentPanel.add(bits_pix_ComboBox);

		{
			String[] numItems = { "100", "200", "300", "400" };
			dpiComboBox = new JComboBox();
			for (int i = 0; i < numItems.length; i++) {
				dpiComboBox.addItem(new Integer(numItems[i]));
			}
			dpiComboBox.setBounds(83, 23, 74, 26);
			dpiComboBox.setSelectedItem(null);
			/*
			 * int[] numItemsConvert = new int[4]; for(int i = 0; i < numItems.length; i++) { try { numItemsConvert[i] = Integer.parseInt(numItems[i]); } catch(Exception ex) {
			 * System.out.println("String Parse Error" + ex.getMessage()); } }
			 */
			contentPanel.add(dpiComboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}

			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			Integer intDpi = (Integer) dpiComboBox.getSelectedItem();
			numDPI = intDpi.intValue();
			Integer intBits_pix = (Integer) bits_pix_ComboBox.getSelectedItem();
			numBits_pix = intBits_pix.intValue();
			System.out.println(" DPI: " + numDPI + " Bits Pixel: " + numBits_pix);
			dispose();
		} else if (e.getActionCommand().equals("Cancel"))
			;
		{
			dispose();
		}
	}

}

class PDFOptionsDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4258459505024979024L;
	private final JPanel contentPanel = new JPanel();
	private JLabel wLabel;
	private JLabel hLabel;
	private JButton okButton;
	private JButton cancelButton;
	private JTextField wTextField;
	private JTextField hTextField;
	private JLabel descriptionLabel;
	Snowbnd s = new Snowbnd();
	public static int pdf_x_status, pdf_y_status;
	public static int x_resize_pdf, y_resize_pdf;

	public PDFOptionsDialog() {
		setTitle("PDF Output");
		setResizable(false);
		// setType(Type.POPUP);
		setBounds(100, 100, 216, 160);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			wLabel = new JLabel("Width");
			wLabel.setBounds(37, 38, 36, 14);
			contentPanel.add(wLabel);
		}
		{
			hLabel = new JLabel("Height");
			hLabel.setBounds(37, 71, 36, 14);
			contentPanel.add(hLabel);
		}
		{
			wTextField = new JTextField();
			wTextField.setBounds(75, 32, 65, 27);
			contentPanel.add(wTextField);
			wTextField.setColumns(10);
		}
		{
			hTextField = new JTextField();
			hTextField.setColumns(10);
			hTextField.setBounds(75, 65, 65, 27);
			contentPanel.add(hTextField);
		}
		{
			descriptionLabel = new JLabel("Set PDF Width and Height");
			descriptionLabel.setBounds(37, 7, 140, 14);
			contentPanel.add(descriptionLabel);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			x_resize_pdf = Integer.parseInt(wTextField.getText()); // convert
																	// textfield
																	// to int
			y_resize_pdf = Integer.parseInt(hTextField.getText());

			pdf_x_status = Integer.valueOf(x_resize_pdf).intValue(); // store
																		// the
																		// result
																		// as an
																		// Integer
			pdf_y_status = Integer.valueOf(y_resize_pdf).intValue();
			System.out.println(" Height " + pdf_x_status + "\n" + " Widght " + pdf_y_status);
			dispose();
		} else if (e.getActionCommand().equals("Cancel"))
			;
		{
			dispose();
		}
	}

}

class ImageResizeDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3941387627380688908L;
	private final JPanel contentPanel = new JPanel();
	private JTextField hTextfield;
	private JTextField wTextfield;
	private JLabel wLabel;
	private JLabel hLabel;
	private JLabel descriptionLabel;
	private JButton cancelButton;
	private JButton okButton;
	Snowbnd s = new Snowbnd();
	public static int xstatus, ystatus;
	public static int x_resize, y_resize;

	// static int x_resize,y_resize;

	public ImageResizeDialog() {
		setResizable(false);
		setModal(true);
		setTitle("Resize Image");
		setBounds(100, 100, 216, 160);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		contentPanel.setLayout(null);
		{
			hLabel = new JLabel("Height");
			hLabel.setBounds(48, 26, 46, 14);
			contentPanel.add(hLabel);
		}
		{
			wLabel = new JLabel("Width");
			wLabel.setBounds(48, 51, 46, 14);
			contentPanel.add(wLabel);
		}
		{
			hTextfield = new JTextField();
			hTextfield.setBounds(87, 21, 66, 25);
			contentPanel.add(hTextfield);
			hTextfield.setColumns(10);
		}
		{
			wTextfield = new JTextField();
			wTextfield.setColumns(10);
			wTextfield.setBounds(87, 46, 66, 25);
			contentPanel.add(wTextfield);
		}
		{
			descriptionLabel = new JLabel("Set Height and Width of Image");
			descriptionLabel.setBounds(30, 0, 150, 14);
			contentPanel.add(descriptionLabel);
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			x_resize = Integer.parseInt(hTextfield.getText());
			y_resize = Integer.parseInt(wTextfield.getText());

			xstatus = Integer.valueOf(x_resize).intValue();
			ystatus = Integer.valueOf(y_resize).intValue();
			System.out.println(" Height " + xstatus + "\n" + " Widght " + ystatus);
			dispose();
		} else if (e.getActionCommand().equals("Cancel"))
			;
		{
			dispose();

		}
	}

	public int getxValue() {

		return xstatus = Integer.valueOf(BatchConvert7.x_resize).intValue();
	}

	public int getyValue() {
		return ystatus = Integer.valueOf(BatchConvert7.y_resize).intValue();
	}
}

class About extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5139561066976306637L;
	private JPanel contentPane;
	private JLabel snowLogoLabel;
	private JPanel contentArea;
	private JSeparator separator;
	private JEditorPane batchConvert;
	private JEditorPane versionNumber;
	private JEditorPane copyRight;
	private JLabel rasterLabel;
	private JEditorPane web;
	private JButton okButton;

	/**
	 * Create the frame.
	 */
	public About() {
		// setType(Type.UTILITY);
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("About BatchConvert");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 546, 262);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		snowLogoLabel = new JLabel("");
		snowLogoLabel.setBounds(332, 133, 206, 69);
		snowLogoLabel.setIcon(new ImageIcon(About.class.getResource("/com/resource/Snowbound_Software_logo_full_2012.png")));

		contentArea = new JPanel();
		contentArea.setBounds(0, 0, 538, 131);
		contentArea.setBackground(Color.WHITE);

		separator = new JSeparator();
		separator.setBounds(0, 206, 543, 9);
		contentPane.setLayout(null);
		contentPane.add(contentArea);

		batchConvert = new JEditorPane();
		batchConvert.setEditable(false);
		batchConvert.setBounds(142, 11, 231, 31);
		batchConvert.setFont(new Font("Tahoma", Font.PLAIN, 14));
		batchConvert.setText("BatchConvert Using RasterMaster");
		contentArea.setLayout(null);

		versionNumber = new JEditorPane();
		versionNumber.setEditable(false);
		versionNumber.setBounds(142, 38, 78, 20);
		versionNumber.setText("Version 1.7");
		contentArea.add(versionNumber);
		contentArea.add(batchConvert);

		copyRight = new JEditorPane();
		copyRight.setEditable(false);
		copyRight.setText("(c) 2007-2012 Snowbound Software. All Right Reserved");
		copyRight.setBounds(142, 69, 285, 31);
		contentArea.add(copyRight);

		rasterLabel = new JLabel("");
		rasterLabel.setIcon(new ImageIcon(About.class.getResource("/com/resource/RasterMaster 100x100.png")));
		rasterLabel.setBounds(10, 0, 100, 120);
		contentArea.add(rasterLabel);
		contentPane.add(snowLogoLabel);
		contentPane.add(separator);

		web = new JEditorPane();
		web.setEditable(false);
		web.setFont(new Font("Tahoma", Font.PLAIN, 13));
		web.setBackground(UIManager.getColor("Button.background"));
		web.setText("www.snowbound.com");
		web.setBounds(0, 208, 161, 20);
		contentPane.add(web);

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		okButton.setBounds(465, 208, 73, 23);
		contentPane.add(okButton);
	}
}

class LicenseExtension {
	public final static String ooxml = "lic";

	public static String fileExt(File afile) {
		String ext = null;
		String s = afile.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}

/**
 * The main class for our BatchConvert sample extends java.awt.Frame
 */
public class BatchConvert7 extends Frame // / main panel
{
	Snowbnd s;
	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
	private static String userHome = System.getProperty("user.home", ".");
	private static String FILE_SEPARATOR = System.getProperty("file.separator", "/");
	private static String logFileName = userHome + FILE_SEPARATOR + "ErrorOuput.log";
	private static BatchConvert7 logStream;
	static SplashWindow splash;
	private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
	private java.util.Date currentDate = new java.util.Date();
	private String simpleDate = formatter.format(currentDate);
	private JLabel dateLabel = new JLabel(simpleDate + " ");
	private static PrintStream log;
	private int numItems;
	private String[] listItems;
	static SortedList myList;
	private static final long serialVersionUID = -2257537939627604263L;
	private static BatchConvert7 mBatchConvertFrame;
	public String mInputDirPath = null;
	public String mOutputDirPath = null;
	private int totalPageCount = 0;
	FileProcessor[] myFileProcessor;
	LicenseExtension licExt;
	ImageResizeDialog imageResize;
	DocumentInput docInputSettings;
	PDFOptionsDialog pdfOptions;
	PDFInputDialog pdfInputDialog;
	About about;
	Job jobOutput;
	PreviewPanel mPreviewPanel;
	JButton mInputDirButton = null;
	JButton mOutputDirButton = null;
	JButton mConvertButton = null;
	JButton mStopButton = null;
	JButton mCancelButton = null;
	JButton clearButton;
	JButton openError;
	JButton addFile;
	JButton removeItem;
	JButton mProblemFilesButton;
	JButton mPdfaFontmapButton;
	JButton mAfpFontmapButton;
	JComboBox comboBox = new JComboBox();
	JCheckBox openFolderCheckbox, removeFiles;
	JCheckBox histogramCheckbox;
	JCheckBox documentCheck;
	JCheckBox convertCMYKCheckbox;
	JCheckBox selectgrayScale;
	JCheckBox selectPromote;
	JCheckBox checkResizeImage;
	JCheckBox pdfResizeCheckbox;
	JCheckBox pdfInputCheckbox;
	JCheckBox fastConversionCheckbox;
	JCheckBox problemFilesCheckbox;
	JCheckBox extractTextCheckbox;
	JCheckBox detectBitDepthCheckbox;
	JCheckBox afpFontmapCheckbox;
	JCheckBox pdfaFontmapCheckbox;
	JCheckBox showConsoleOutput;
	JPanel mMainPanel;
	Panel mWidgetPanel;
	JPanel textOutputPanel;
	JPanel fileOptions;
	JPanel convePanel, filePreviewPanel, verPanel, mProgressPanel;
	JPanel comboPanel, mPreviewBorder, radioPanel, statusPanel, listPanel;
	JPanel firstTab;
	JPanel secondTab;
	JLabel mInputDirLabel = null;
	JLabel mOutputDirLabel = null;
	JLabel mOutputFormatLabel = null;
	JLabel mInputStatusLabel = null;
	JLabel mOutputStatusLabel = null;
	JLabel convLabel, logoArea, verLabel, sendFiles, selectedFolder, footerImage, headerImage, deleteFiles, resizeOptions;
	JLabel inputDocument;
	JLabel grayScale;
	JLabel promoteImage;
	JLabel pdfResizeLabel;
	JLabel pdfInputLabel;
	JLabel cmykLabel;
	JLabel fasConversionLabel;
	JLabel problemFilesLabel;
	JLabel histogramLabel;
	JLabel textExtractLabel;
	JLabel detectBitDepthLabel;
	JLabel pdfaFontmapLabel;
	JLabel afpFontmapLabel;
	JLabel consoleOutputLabel;
	JMenuBar menuBar;
	JMenu menuFile;
	JMenuItem menu_itemExit;
	JMenu menuOptions;
	JMenuItem menuItems_imageSettings;
	JMenuItem menuItems_documentInput;
	JMenuItem menu_deleteItem;
	JMenuItem menuItem_grayScale;
	JMenuItem menuItem_promte;
	JMenuItem menuItem_pdfOutput;
	JMenu menuHelp;
	JMenuItem menu_itemAbout;
	JMenuItem timerTextArea;
	JSeparator separator, middleSeparator;
	JTextField mInputDirField = null;
	JTextField mOutputDirField = null;
	JTextField mProblemFilesField = null;
	JTextField pdfaFontmapField = null;
	JTextField afpFontmapField = null;
	JProgressBar mProgressBar = null;
	JFileChooser mFileBrowser = null;
	JTextArea textOutput;
	static JTextArea fileOuput;
	JScrollPane scroll, fileScroll;
	JScrollBar sBar;
	JDialog dialog = null;
	// DefaultListModel listModel;
	List mOutputFormatList;
	// JList outputList;
	Timer myTimer = null;
	int counter = 0;
	int minValue = 0;
	int complete = 100;
	int result = 0;
	int[] length = new int[2];
	int[] error = new int[2];
	int[] errorA = new int[1];
	int convertToFormat = 0;
	int status = -1;
	int formatCode;
	static int x_resize, y_resize;
	long elaspedTime = 0;
	boolean gDone = false;
	boolean checkSelected = true;
	File[] totalFiles = null;
	static String[] listFiles = null;
	String[] inputFileList = null;
	String conversionFormatExtension = null;
	String mInputDirectoryName = null;
	String mOutputDirectoryName = null;
	String mProblemDirectoryName = null;
	String mPdfaFontmapDirectoryName = null;
	String mAfpFontmapDirectoryName = null;
	static Date startTime;
	static Date endTime;
	
	Color color = new Color(236, 233, 216);
	Color newColor = new Color(240, 240, 240);
	Color UI = new Color(245, 245, 220);
	Color paleOrange = new Color(255, 235, 200);
	Color lightBrown = new Color(224, 209, 194);
	
	Color mainWidgetPanelColor = lightBrown;

	int mainPanelWidth = 900;
	int mainPanelHeight = 660;
	
	boolean savedAsSearchablePdf = false;
	boolean settingPdfaFontDirectory = true;

	// private so no class can make a new instance
	private BatchConvert7(FileOutputStream fileOutputStream, boolean logOutFile, PrintStream log) {
		super();
		s = new Snowbnd();
		setIconImage(Toolkit.getDefaultToolkit().getImage(BatchConvert7.class.getResource("/com/resource/RasterMaster 100x100.png")));
		mFileBrowser = new JFileChooser();
		// mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		mBatchConvertFrame = this;

		// Set RaterMaster version
		int majorVersion[] = { 0 };
		int minorVersion[] = { 0 };
		s.IMG_get_version(majorVersion, minorVersion);

		// License stuff
		int nStat = s.IMGLOW_set_ooxml_license();
		String licensePath = s.IMGLOW_get_ooxml_license_path();

		// Log output to console
		BatchConvert7.log = log;
		if (log != null) {
			System.out.println("License File status = " + nStat + ", " + ErrorCodes.getErrorMessage(nStat));
			System.out.println("RasterMaster Java version: " + majorVersion[0] + "." + minorVersion[0]);
			System.out.println("License File Path = " + licensePath);
			System.out.println("--------------------------------------");
			log.println("Starting BatchConvert | Using RasterMaster");
			log.println("--------------------------------------");
		}
	}

	/**
	 * PreviewPanel is used for drawing a preview thumbnail of the current image being processed. PreviewPanel should update with each step of the progress bar.
	 */
	class PreviewPanel extends JPanel {
		private static final long serialVersionUID = 5544297606459224866L;

		public PreviewPanel() {
			super();
		}

		public Snowbnd getImage() {
			return new Snowbnd();
		}

		public void paint(Graphics g) {
			if (s != null) {
				Insets in = getInsets();
				Dimension dimension = getSize();
				dimension.width -= (in.right + in.left);
				dimension.height -= (in.top + in.bottom);
				g.translate(in.left, in.top);
				/* draw a white background over previous image */
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, dimension.width, dimension.height);
				s.IMG_display_bitmap_aspect(g, this, 0, 0, dimension.width, dimension.height, 0);
			}
		}

		public void update(Graphics g) {
			paint(g);

		}

	}

	class SortedList extends List {
		/**
		 * Sorted List class
		 */
		private static final long serialVersionUID = 1L;

		public SortedList(int outputList) {
			super(outputList);
		}

		public SortedList() {
			super();
		}

		public int sortList() {
			int first;
			int midpoint;
			int last;
			String temp;
			for (first = 0; first < numItems - 1; first++) {
				midpoint = first;// store first value -> midpoint
				// find the index of the smallest item
				for (last = first + 1; last < numItems; last++)
					if (listItems[last].compareTo(listItems[midpoint]) < 0)
						midpoint = last;
				// swap the listItems of the middle and listItems of first
				temp = listItems[midpoint];
				listItems[midpoint] = listItems[first];
				listItems[first] = temp;
			}
			return counter;

		}
	}

	// @SuppressWarnings("unchecked")
	public void initializeFormatsList() {
		// mOutputFormatList = new List();
		myList = new SortedList();

		FormatHash formatHash = FormatHash.getInstance();
		String[] saveFormats = formatHash.getAvailibleSaveFormats();
		java.util.Arrays.sort(saveFormats);
		
		comboBox = new JComboBox(saveFormats);
		String format = prefs.get("format", "");
		comboBox.setSelectedItem(format);

		comboBox.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent arg0)	{
				String selectedItem = (String)comboBox.getSelectedItem();
				if(selectedItem != null){
					prefs.put("format", selectedItem);
					if(selectedItem.equals("PDFA"))	{
						textExtractLabel.setForeground(SystemColor.black);
						extractTextCheckbox.setSelected(true);
					}	
					else	{
						textExtractLabel.setForeground(SystemColor.black);						
						extractTextCheckbox.setSelected(false);
					}
				}
			}		
		});
		
		comboPanel = new JPanel();
		comboPanel.setBounds(0, 135, 265, 50);
		TitledBorder titledBorder = new TitledBorder(null, "Output Format", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.blue);		
		comboPanel.setBorder(titledBorder);
		
		//comboPanel.setBorder(new TitledBorder(null, "Output Format", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.red));
		comboPanel.setBackground(this.mainWidgetPanelColor);

		for (int index = 0; index < saveFormats.length; index++) {
			// comboPanel = new JPanel();
			// comboPanel.setBounds(5, 155, 255, 50);
			// comboPanel.setBorder(new TitledBorder(null, "Output Format",
			// TitledBorder.LEADING,
			// TitledBorder.TOP, null, SystemColor.red));
			// comboPanel.setBackground(newColor);
			comboPanel.add(comboBox, saveFormats[index]);
			comboBox.setPreferredSize(new Dimension(255, 25));
		}

	}

	/*
	 * This method has the potential to cause alot of overhead if the page count is large
	 */
	/*
	 * For a speed increase, consider improving the threading model or running headless
	 */
	public void preprocessDirectory() throws NegativeArraySizeException {
		File inputDirectory = new File(mInputDirectoryName);
		File outputDirectory = new File(mOutputDirectoryName);
		Snowbnd s = new Snowbnd();

		if (true)
		// if (inputDirectory.isDirectory() && outputDirectory.isDirectory())
		{

			inputFileList = inputDirectory.list(); // dirs + files
			totalFiles = inputDirectory.listFiles(); // files

			// recursiveDirectory(appendFiles, inputDirectory,
			// mOutputDirectoryPath, true, mOutputFormatList);
			scanDirectoryFiles(inputDirectory);

			System.out.println(" Total Files: " + totalFiles.length);
			if (totalFiles.length >= 10) {
				System.out.println("Processing Large Number of Files: " + totalFiles.length);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			// Begin timer task
			System.out.println("------------------------------------------------------------------------");
			System.out.println(" Current TestThread started: " + Thread.currentThread().getName() + " BatchConvert "
					+ new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(startTime = new Date()));
			System.out.println("------------------------------------------------------------------------");

			for (int i = 0; i < inputFileList.length; i++) {

				fileOuput.append(inputFileList[i].toString() + "\n");// only
																		// executes
																		// when
																		// start
																		// is
																		// pressed

				String currentFile = mInputDirectoryName + System.getProperty("file.separator") + inputFileList[i];

				// Skip directories. Get page count of each file
				try {
					int pageCount = s.IMGLOW_get_pages(currentFile);
					if (pageCount < 0) {
						if (mProblemFilesField.isEnabled() && mProblemFilesField.getText().length() > 0) {
							File sourceDir = new File(currentFile);
							File desDir = new File(mProblemDirectoryName);
							try {
								boolean moveSuccessful = sourceDir.renameTo(new File(desDir, sourceDir.getName()));
								System.out.println(" Error, within: " + sourceDir.getName() + " was moved to: " + desDir.getAbsolutePath());
								if (!moveSuccessful) {
									System.out.println(" Error, file was not moved");
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							/* Return values less than zero indicate an error */
							System.out.println("Error in counting pages: " + ErrorCodes.getErrorMessage(pageCount) + "," + " ProblemFileName: "
									+ currentFile);
							System.out.println(" Corrupted file was moved");
						} else {
							/* Return values less than zero indicate an error */
							System.out.println("Error in counting pages: " + ErrorCodes.getErrorMessage(pageCount) + "," + " CorruptedFileName: "
									+ currentFile);
						}	// problem files
						
					}
					if (pageCount >= 0) {
						totalPageCount += pageCount;
					} else {
						// creating new objects..need to make one instance
						new MessageBox(mBatchConvertFrame, " Error converting file: "
								+ " ' Please check the file or classpath for proper jar file ' ", true);
						System.out.println(" Caught error in get pages for file ".toString() + i + " '" + currentFile + "': " + pageCount + "\n"
								+ ErrorCodes.getErrorMessage(pageCount).toUpperCase() + "\n");
					}
					/*
					 * if(totalPageCount > 1) { System.out.println("Saving as multipage document"); }
					 */

					// totalPageCount += s.IMGLOW_get_pages(currentFile);
				} catch (Exception ex) {
					System.out.println("Caught error in get pages for file " + i + " '" + currentFile + "' : " + ex.getMessage());

					ex.printStackTrace();
				}
				mMainPanel.repaint();

			}
			myFileProcessor = new FileProcessor[totalPageCount]; // / updates
																	// the
																	// progress
																	// bar
			int globalImageCount = 0;
			for (int i = 0; i < inputFileList.length; i++) {
				String currentFile = mInputDirectoryName + System.getProperty("file.separator") + inputFileList[i];
				String currentFileSaveAs = mOutputDirectoryName + System.getProperty("file.separator") + inputFileList[i] + "."
						+ conversionFormatExtension;
				try {
					int currentPageCount = s.IMGLOW_get_pages(currentFile);
					if (currentPageCount > 0) {
						for (int j = 0; j < currentPageCount; j++) {
							myFileProcessor[globalImageCount] = new FileProcessor();
							myFileProcessor[globalImageCount].setCurrentFile(currentFile);
							myFileProcessor[globalImageCount].setCurrentFileSaveAs(currentFileSaveAs);
							myFileProcessor[globalImageCount].setCurrentPage(j);
							globalImageCount++;
						}
					} else {
						totalPageCount += s.IMGLOW_get_pages(currentFile);
						System.out.println(" Error in currentImage/file format: Please check your file: "
								+ ErrorCodes.getErrorMessage(currentPageCount).toUpperCase() + "\n");
					}

				} catch (Exception ex) {
					// System.out.println("Error in get pages for file " + i +
					// " '" + currentFile + "' : " + ex.getMessage());
					System.out.println(" Skipping '" + currentFile + "' due to get_pages error " + ex.getMessage());
				}

			}
		} else {
			System.out.println("Specify valid directory");
		}
	}

	public int determineConversionFormat() throws NullPointerException {
		// String outputFormat = myList.getSelectedItem();
		// //mOutputFormatList.getSelectedItem();
		String outputFormat = (String) comboBox.getSelectedItem();

		// ShowMessageDialog if the OutputFormat is empty
		if (outputFormat == null) {
			gDone = false;
			mConvertButton.setEnabled(true);
			JOptionPane.showMessageDialog(mBatchConvertFrame, "Please specify an output type to convert", getWarningString(), JOptionPane.OK_OPTION);
			System.out.println("Please specify an output Format to convert");
			openError.setEnabled(true);
			mStopButton.setEnabled(false);
		}

		Format format = FormatHash.getInstance().getFormat(outputFormat);
		conversionFormatExtension = format.getExtension();
		return format.getFormatCode();
	}

	public void layoutMainPanel() {
		mMainPanel = new JPanel();
		mMainPanel.setLayout(new GridBagLayout());
		mMainPanel.setBounds(0, 0, this.getBounds().width, this.getBounds().height);
		// XXX mMainPanel.setBackground(color);
		mMainPanel.setBackground(Color.blue);
		mBatchConvertFrame.add(mMainPanel);
	}

	public void content() {
		Color originalColor = newColor;

		imageResize = new ImageResizeDialog();
		docInputSettings = new DocumentInput();
		pdfOptions = new PDFOptionsDialog();
		about = new About();
		textOutputPanel = new JPanel();
		textOutputPanel.setBackground(this.mainWidgetPanelColor);
		textOutputPanel.setBounds(500, 375, 390, 200);
		textOutputPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Conversion Time", TitledBorder.LEADING,
				TitledBorder.TOP, null, SystemColor.blue));
		textOutput = new JTextArea();
		textOutput.setEditable(false);
		textOutput.setBackground(newColor);
		textOutput.setForeground(Color.BLUE);
		textOutput.setEnabled(true);
		textOutput.setLineWrap(true);

		DefaultCaret caret = (DefaultCaret) textOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		scroll = new JScrollPane(textOutput);// textArea is placed in ScrollPane
		scroll.setPreferredSize(new Dimension(375, 170));// 425,90, 415
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		textOutputPanel.add(scroll);

		filePreviewPanel = new JPanel(); // the console output panel
		filePreviewPanel.setBackground(this.mainWidgetPanelColor);
		filePreviewPanel.setBounds(5, 375, 350, 200);
		filePreviewPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Console Output", TitledBorder.LEADING,
				TitledBorder.TOP, null, SystemColor.blue));
		fileOuput = new JTextArea();
		fileOuput.setEditable(false);
		fileOuput.setBackground(newColor);
		fileOuput.setForeground(Color.BLUE);
		fileOuput.setEnabled(true);
		fileOuput.setWrapStyleWord(true);
		fileScroll = new JScrollPane(fileOuput);
		fileScroll.setPreferredSize(new Dimension(330, 170));
		fileScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		fileScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		filePreviewPanel.add(fileScroll);
		// filePreviewPanel.setVisible(false);
		separator = new JSeparator(); // top separator line
		separator.setBackground(Color.black);
		separator.setBounds(0, 54, 890, 2);

		middleSeparator = new JSeparator(); // middle separator line
		middleSeparator.setForeground(Color.black);
		middleSeparator.setBounds(0, 365, 890, 2);

		logoArea = new JLabel("");
		logoArea.setIcon(new ImageIcon(BatchConvert7.class.getResource("/com/resource/smallLogo.png")));// newSnowlogo.png
		logoArea.setBounds(0, 10, mainPanelWidth, 56);

		headerImage = new JLabel("");
		headerImage.setIcon(new ImageIcon(BatchConvert7.class.getResource("/com/resource/newRasterMasterLogo.png")));
		headerImage.setBounds(800, 0, 67, 56);

		footerImage = new JLabel("");
		footerImage.setIcon(new ImageIcon(BatchConvert7.class.getResource("/com/resource/footerResized.png")));
		footerImage.setBounds(150, 555, 100, 28);

		verPanel = new JPanel();
		verPanel.setBackground(this.mainWidgetPanelColor);
		verPanel.setBounds(10, 580, 141, 38);
		verPanel.setBorder(new TitledBorder(UIManager.getBorder("TitleBoarder.border"), "Version", TitledBorder.LEADING, TitledBorder.TOP, null,
				SystemColor.blue));
		verLabel = new JLabel("Batch Convert 1.8");
		verPanel.add(verLabel);

		dateLabel.setBounds(160, 595, 150, 28);

		deleteFiles = new JLabel("Delete Source Files");
		deleteFiles.setBounds(325, 260, 150, 20);

		resizeOptions = new JLabel("Image Settings");
		resizeOptions.setBounds(325, 220, 150, 20);

		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, mainPanelWidth, 21);
		menuBar.setBackground(newColor);

		menuFile = new JMenu("File");
		menuFile.setBackground(newColor);
		menuBar.add(menuFile);

		menu_itemExit = new JMenuItem("Exit");
		menu_itemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		menuFile.add(menu_itemExit);

		menuOptions = new JMenu("Options");
		menuOptions.setBackground(newColor);
		menuBar.add(menuOptions);

		menuItems_imageSettings = new JMenuItem("Image Settings");
		menuItems_imageSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menuItems_imageSettings.isSelected()) {
					checkResizeImage.setSelected(true);
					imageResize.setVisible(true);

				}
			}
		});
		menuItems_imageSettings.setSelected(true);
		menuOptions.add(menuItems_imageSettings);

		menuItems_documentInput = new JMenuItem("Document Input");
		menuItems_documentInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menuItems_documentInput.isSelected()) {
					documentCheck.setSelected(true);
					docInputSettings.setVisible(true);
				}
			}
		});
		menuItems_documentInput.setSelected(true);
		menuOptions.add(menuItems_documentInput);

		menu_deleteItem = new JMenuItem("Delete Source Files");
		menu_deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menu_deleteItem.isSelected()) {
					removeFiles.setSelected(true);
					removeFiles.setEnabled(true);
				}
			}
		});
		menu_deleteItem.setSelected(true);
		menuOptions.add(menu_deleteItem);

		menuItem_grayScale = new JMenuItem("8-bit GrayScale");
		menuItem_grayScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menuItem_grayScale.isSelected()) {
					selectgrayScale.setSelected(true);
					selectgrayScale.setEnabled(true);
				}
			}
		});
		menuItem_grayScale.setSelected(true);
		menuOptions.add(menuItem_grayScale);

		menuItem_promte = new JMenuItem("Promote");
		menuItem_promte.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menuItem_promte.isSelected()) {
					selectPromote.setSelected(true);
					selectPromote.setEnabled(true);
				}
			}
		});
		menuItem_promte.setSelected(true);
		menuOptions.add(menuItem_promte);

		menuItem_pdfOutput = new JMenuItem("PDF Output");
		menuItem_pdfOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menuItem_pdfOutput.isSelected()) {
					pdfResizeCheckbox.setSelected(true);
					pdfOptions.setVisible(true);
				}
			}
		});
		menuItem_pdfOutput.setSelected(true);
		menuOptions.add(menuItem_pdfOutput);

		menuHelp = new JMenu("Help");
		menuHelp.setBackground(newColor);
		menuBar.add(menuHelp);

		menu_itemAbout = new JMenuItem("About");
		menu_itemAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (menu_itemAbout.isSelected()) {
					about.setVisible(true);
				}
			}
		});
		menu_itemAbout.setSelected(true);
		menuHelp.add(menu_itemAbout);

		timerTextArea = new JMenuItem("Clear Timer Area ");
		timerTextArea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timerTextArea.isSelected()) {
					textOutput.setText("");
				}
			}
		});
		timerTextArea.setSelected(true);
		menuOptions.add(timerTextArea);

		int inputStatusCBWidth = 300;
		inputDocument = new JLabel("Document Input Options");
		inputDocument.setBounds(325, 240, inputStatusCBWidth, 20);

		grayScale = new JLabel("8-Bit Gray Scale");
		grayScale.setBounds(165, 200, inputStatusCBWidth, 20);

		promoteImage = new JLabel("Promote to 24-Bit");
		promoteImage.setBounds(165, 220, inputStatusCBWidth, 20);

		pdfResizeLabel = new JLabel("PDF Output");
		pdfResizeLabel.setBounds(165, 240, inputStatusCBWidth, 20);

		pdfInputLabel = new JLabel("Set PDF Input");
		pdfInputLabel.setBounds(325, 200, 150, 20);

		cmykLabel = new JLabel("RGB to CMYK Data");
		cmykLabel.setBounds(325, 180, 150, 20);

		histogramLabel = new JLabel("Improve 8-Bit GrayScale");
		histogramLabel.setBounds(325, 300, 150, 20);

		fasConversionLabel = new JLabel("Enable Fast Conversion");
		fasConversionLabel.setBounds(325, 280, 150, 20);

		textExtractLabel = new JLabel("Extract Text");
		textExtractLabel.setBounds(325, 160, 150, 20);
		
		detectBitDepthLabel = new JLabel("Detect BitDepth");
		detectBitDepthLabel.setBounds(325, 320, 150, 20);

		afpFontmapLabel = new JLabel("AFP Font Mapping Path");
		afpFontmapLabel.setBounds(30, 285, 150, 20);

		pdfaFontmapLabel = new JLabel("PDF/A Font Path");
		pdfaFontmapLabel.setBounds(30, 305, 150, 20);

		problemFilesLabel = new JLabel("Send Problem Files to");
		problemFilesLabel.setBounds(30, 325, 150, 20);

		consoleOutputLabel = new JLabel("Show Console Output");
		consoleOutputLabel.setBounds(30, 345, 150, 20);

	}

	public void layoutWidgetPanel() {
		Color originalColor = newColor;

		content();
		pdfOptions = new PDFOptionsDialog();
		pdfInputDialog = new PDFInputDialog();
		imageResize = new ImageResizeDialog();
		docInputSettings = new DocumentInput();
		mWidgetPanel = new Panel();
		mWidgetPanel.setBounds(0, 1, mBatchConvertFrame.getWidth(), mBatchConvertFrame.getHeight());
		mWidgetPanel.setLayout(null);

		int width = mBatchConvertFrame.getWidth();
		int heitht = mBatchConvertFrame.getHeight();
		mWidgetPanel.setBackground(this.mainWidgetPanelColor);

		statusPanel = new JPanel();
		statusPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Status", TitledBorder.LEADING, TitledBorder.TOP, null,
				SystemColor.blue));
		statusPanel.setBounds(5, 205, 135, 60);
		statusPanel.setBackground(newColor);

		mInputDirLabel = new JLabel("Input Directory: ");
		mInputDirLabel.setBounds(5, 70, 110, 15);
		
		mOutputDirLabel = new JLabel("Output Directory: ");
		mOutputDirLabel.setBounds(5, 105, 110, 15);
		mOutputDirLabel.setForeground(SystemColor.black);
		statusPanel.add(mInputDirLabel, mOutputDirLabel);
		mInputDirButton = new JButton("Browse...");
		mInputDirButton.setBounds(405, 65, 90, 30);
		mInputDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String inputDir = prefs.get("inputDir", "C:\\Test\\Input\\");
				System.out.println("mInputDirButton.actionPerformed.inputDir:" + inputDir);
				
				mFileBrowser.setCurrentDirectory(new File(inputDir));
				mFileBrowser.setDialogTitle("Select Input Directory");
				mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION) {
					mInputDirField.setText(mFileBrowser.getSelectedFile().toString());
					mInputDirectoryName = mFileBrowser.getSelectedFile().toString();
					prefs.put("inputDir", mInputDirField.getText());
				}
			}
		});

		mOutputDirButton = new JButton("Browse...");
		mOutputDirButton.setBounds(405, 100, 90, 30);
		mOutputDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String outputDir = prefs.get("outputDir", "C:\\Test\\Input\\");
				System.out.println("mOutputDirButton.actionPerformed.outputDir:" + outputDir);
				
				mFileBrowser.setCurrentDirectory(new File(outputDir));
				mFileBrowser.setDialogTitle("Select Output Directory");
				mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION) {
					mOutputDirField.setText(mFileBrowser.getSelectedFile().toString());
					mOutputDirectoryName = mFileBrowser.getSelectedFile().toString();
					String tmp = mOutputDirField.getText();
					prefs.put("outputDir", mOutputDirField.getText());
				}
			}
		});

		mProblemFilesButton = new JButton("...");
		mProblemFilesButton.setBounds(275, 325, 40, 20);
		mProblemFilesButton.setEnabled(false);
		mProblemFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Open FileChooser in a separate thread to avoid possible
				// thread deadlocks
				try {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.dir")));
							mFileBrowser.setDialogTitle("Select Directory Path");
							mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
							if (status == JFileChooser.APPROVE_OPTION) {
								mProblemFilesField.setText(mFileBrowser.getSelectedFile().getName().toString());
								mProblemDirectoryName = mFileBrowser.getSelectedFile().getAbsolutePath().toString();
							}
						}

					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});

		mAfpFontmapButton = new JButton("...");
		mAfpFontmapButton.setBounds(275, 285, 40, 20);
		mAfpFontmapButton.setEnabled(false);
		mAfpFontmapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.dir")));
				mFileBrowser.setDialogTitle("Select AFP Fontmap File");
				mFileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION) {
					afpFontmapField.setText(mFileBrowser.getSelectedFile().getName().toString());
					mAfpFontmapDirectoryName = mFileBrowser.getSelectedFile().getAbsolutePath().toString();

				}
			}
		});
		
		mPdfaFontmapDirectoryName = prefs.get("pdfaFontPath", "c:/fonts");
		
		mPdfaFontmapButton = new JButton("...");
		mPdfaFontmapButton.setBounds(275, 305, 40, 20);
		mPdfaFontmapButton.setEnabled(false);
		mPdfaFontmapButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String fontDir = prefs.get("pdfaFontPath", "c:/fonts");
				mFileBrowser.setCurrentDirectory(new File(fontDir));
				mFileBrowser.setDialogTitle("Select Pdf/A Fontmap Directory");
				mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION) {
					mPdfaFontmapDirectoryName = mFileBrowser.getSelectedFile().getAbsolutePath().toString();
					pdfaFontmapField.setText(mPdfaFontmapDirectoryName);
					prefs.put("pdfaFontPath", mPdfaFontmapDirectoryName);
					
					System.out.println("mPdfaFontmapDirectoryName set to " + mPdfaFontmapDirectoryName);

				}
			}
		});

		problemFilesCheckbox = new JCheckBox();
		problemFilesCheckbox.setBounds(5, 325, 20, 20);
		problemFilesCheckbox.setBackground(this.mainWidgetPanelColor);
		problemFilesCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (problemFilesCheckbox.isSelected()) {
					mProblemFilesButton.setEnabled(true);
					mProblemFilesField.setEnabled(true);
				} else {
					mProblemFilesButton.setEnabled(false);
					mProblemFilesField.setEnabled(false);
				}
			}
		});

		histogramCheckbox = new JCheckBox("");
		histogramCheckbox.setToolTipText("Improves the dynamic range of 8-bit GrayScale images");
		histogramCheckbox.setBounds(470, 300, 20, 20);
		histogramCheckbox.setSelected(false);
		histogramCheckbox.setBackground(this.mainWidgetPanelColor);
		histogramCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (histogramCheckbox.isSelected()) {
					histogramCheckbox.setSelected(true);
					return;
				}
			}
		});

		removeFiles = new JCheckBox("");
		removeFiles.setBounds(470, 260, 20, 20);
		removeFiles.setSelected(false);
		removeFiles.setBackground(this.mainWidgetPanelColor);
		removeFiles.addPropertyChangeListener(new PropertyChangeListener() {
			public synchronized void propertyChange(PropertyChangeEvent evt) {
				if (removeFiles.isSelected()) {
					// checkSelected = removeFiles.isSelected();
					if (!(mProgressBar.getMaximum() >= complete) && !gDone) {
						deleteDirectoryFiles();
						gDone = true;
						removeFiles.setEnabled(true);
					}
				}
			}
		});
		documentCheck = new JCheckBox();
		documentCheck.setBounds(470, 240, 20, 20);
		documentCheck.setSelected(false);
		documentCheck.setBackground(this.mainWidgetPanelColor);
		documentCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (documentCheck.isSelected()) {

					documentCheck.setEnabled(true);
					docInputSettings.setVisible(true);
				} else {
					docInputSettings.setVisible(false);
				}
			}
		});

		checkResizeImage = new JCheckBox("");
		checkResizeImage.setBounds(470, 220, 20, 20);
		checkResizeImage.setSelected(false);
		checkResizeImage.setBackground(this.mainWidgetPanelColor);
		checkResizeImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (checkResizeImage.isSelected()) {

					checkResizeImage.setEnabled(true);
					imageResize.setVisible(true);
				} else {
					imageResize.setVisible(false);
				}
			}
		});
		selectPromote = new JCheckBox();
		selectPromote.setBounds(275, 220, 20, 20);
		selectPromote.setToolTipText("promote 1, 4, or 8-bit images to 24-bit");
		selectPromote.setSelected(false);
		selectPromote.setBackground(this.mainWidgetPanelColor);
		selectPromote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (selectPromote.isSelected()) {
					selectPromote.setEnabled(true);
					return;
				}

			}
		});

		selectgrayScale = new JCheckBox();
		selectgrayScale.setBounds(275, 200, 20, 20);
		selectgrayScale.setToolTipText("reduce a color image to 8-bit gray scale");
		selectgrayScale.setSelected(false);
		selectgrayScale.setBackground(this.mainWidgetPanelColor);
		selectgrayScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (selectgrayScale.isSelected()) {
					selectgrayScale.setEnabled(true);
					return;
				}
			}
		});

		pdfResizeCheckbox = new JCheckBox();
		pdfResizeCheckbox.setBounds(275, 240, 20, 20);
		pdfResizeCheckbox.setSelected(false);
		pdfResizeCheckbox.setBackground(this.mainWidgetPanelColor);
		pdfResizeCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (pdfResizeCheckbox.isSelected()) {

					pdfResizeCheckbox.setEnabled(true);
					pdfOptions.setVisible(true);
				} else {
					pdfOptions.setVisible(false);
				}
			}
		});
		pdfInputCheckbox = new JCheckBox();
		pdfInputCheckbox.setBounds(470, 200, 20, 20);
		pdfInputCheckbox.setToolTipText("Change the PDF DIP/BITS. This only works on PDF files");
		pdfInputCheckbox.setBackground(this.mainWidgetPanelColor);
		pdfInputCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (pdfInputCheckbox.isSelected()) {
					pdfInputDialog.setVisible(true);
				} else {
					pdfInputDialog.setVisible(false);
				}
			}
		});
		convertCMYKCheckbox = new JCheckBox();
		convertCMYKCheckbox.setBounds(470, 180, 20, 20);
		convertCMYKCheckbox.setToolTipText("Convert 24-bit RGB data to 32-bit CMYK data");
		convertCMYKCheckbox.setBackground(this.mainWidgetPanelColor);
		convertCMYKCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (convertCMYKCheckbox.isSelected()) {
					convertCMYKCheckbox.setEnabled(true);
					return;

				}
			}
		});
		fastConversionCheckbox = new JCheckBox();
		fastConversionCheckbox.setBounds(470, 280, 20, 20);
		fastConversionCheckbox.setToolTipText("improves performance for decompression and conversion only " + "works with MO:DCA or AFP images");
		fastConversionCheckbox.setBackground(this.mainWidgetPanelColor);
		fastConversionCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fastConversionCheckbox.isSelected()) {
					fastConversionCheckbox.setEnabled(true);
					System.out.println(" Fast conversion enabled");
					return;
				} else {
					System.out.println(" Fast conversion disabled");
				}
			}
		});
		extractTextCheckbox = new JCheckBox();
		extractTextCheckbox.setBounds(470, 160, 20, 20);
		
		String selectedItem = (String)comboBox.getSelectedItem();		
		if(selectedItem.equals("PDFA"))	{
			textExtractLabel.setForeground(SystemColor.black);
			extractTextCheckbox.setSelected(true);
		}	

		extractTextCheckbox.setBackground(this.mainWidgetPanelColor);
		extractTextCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (extractTextCheckbox.isSelected()) {
					extractTextCheckbox.setEnabled(true);
					return;
				}
			}
		});
		detectBitDepthCheckbox = new JCheckBox();
		detectBitDepthCheckbox.setBounds(470, 320, 20, 20);
		detectBitDepthCheckbox.setBackground(this.mainWidgetPanelColor);
		detectBitDepthCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (detectBitDepthCheckbox.isSelected()) {
					detectBitDepthCheckbox.setEnabled(true);
					return;
				}
			}
		});
		afpFontmapCheckbox = new JCheckBox();
		afpFontmapCheckbox.setBounds(5, 285, 20, 20);
		afpFontmapCheckbox.setBackground(this.mainWidgetPanelColor);
		afpFontmapCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (afpFontmapCheckbox.isSelected()) {
					mAfpFontmapButton.setEnabled(true);
					afpFontmapField.setEnabled(true);
				} else {
					mAfpFontmapButton.setEnabled(false);
					afpFontmapField.setEnabled(false);
				}
			}
		});
		pdfaFontmapCheckbox = new JCheckBox();
		pdfaFontmapCheckbox.setBounds(5, 305, 20, 20);
		pdfaFontmapCheckbox.setBackground(this.mainWidgetPanelColor);
		pdfaFontmapCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (pdfaFontmapCheckbox.isSelected()) {
					mPdfaFontmapButton.setEnabled(true);
					pdfaFontmapField.setEnabled(true);
				} else {
					mPdfaFontmapButton.setEnabled(false);
					pdfaFontmapField.setEnabled(false);
				}
			}
		});
		showConsoleOutput = new JCheckBox();
		showConsoleOutput.setBounds(5, 345, 20, 20);
		showConsoleOutput.setBackground(this.mainWidgetPanelColor);
		showConsoleOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (showConsoleOutput.isSelected()) {
					redirectSystemOutStreams();
					showConsoleOutput.setSelected(true);
				}
			}
		});
		showConsoleOutput.setSelected(true);

		mInputDirField = new JTextField(prefs.get("inputDir", "C:\\Test\\Input"));
		mInputDirectoryName = mInputDirField.getText();
		
		mInputDirField.setBounds(110, 70, 290, 20);
		mInputDirField.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}

			public void focusGained(FocusEvent e) {
				// intentionally left blank
			}
		});
		mOutputDirField = new JTextField(prefs.get("outputDir", "C:\\Test\\Output"));
		mOutputDirectoryName = mOutputDirField.getText();
		
		mOutputDirField.setBounds(110, 105, 290, 20);
		mOutputDirField.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}

			public void focusGained(FocusEvent e) {
				// intentionally left blank
			}
		});
		mProblemFilesField = new JTextField();
		mProblemFilesField.setBounds(170, 325, 100, 19);
		mProblemFilesField.setEnabled(false);
		mProblemFilesField.setEditable(true);
		mProblemFilesField.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				mProblemDirectoryName = mProblemFilesField.getText();
			}

			public void focusGained(FocusEvent e) {
				// intentionally left blank
			}
		});

		afpFontmapField = new JTextField();
		afpFontmapField.setBounds(170, 285, 100, 19);
		afpFontmapField.setEnabled(false);
		afpFontmapField.setEditable(true);
		afpFontmapField.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				mAfpFontmapDirectoryName = afpFontmapField.getText();
			}

			public void focusGained(FocusEvent e) {
				// intentionally left blank
			}
		});

		pdfaFontmapField = new JTextField();
		pdfaFontmapField.setBounds(170, 305, 100, 19);
		pdfaFontmapField.setEnabled(false);
		pdfaFontmapField.setEditable(true);
		pdfaFontmapField.setText(mPdfaFontmapDirectoryName);
		pdfaFontmapField.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e) {
				mPdfaFontmapDirectoryName = pdfaFontmapField.getText();
			}

			public void focusGained(FocusEvent e) {
				// intentionally left blank
			}
		});

		mOutputFormatLabel = new JLabel("Select Output Format: ");
		mOutputFormatLabel.setBounds(mWidgetPanel.getWidth() / 2 + 5, mOutputDirButton.getY() + mOutputDirButton.getHeight() + 15,
				mWidgetPanel.getWidth() / 2 - 30, 15);

		mInputStatusLabel = new JLabel("Input Status: ");
		mInputStatusLabel.setBounds(10, mOutputDirButton.getY() + mOutputDirButton.getHeight() + 60, mWidgetPanel.getWidth() / 5, 15);

		mOutputStatusLabel = new JLabel("Output Status: ");

		mOutputStatusLabel.setBounds(10, 265, 150, 16);

		mPreviewPanel = new PreviewPanel();
		mPreviewBorder = new JPanel();
		mPreviewBorder.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Preview", TitledBorder.LEADING, TitledBorder.TOP,
				null, SystemColor.blue));
		int b = 20;
		mPreviewBorder.setBounds(500, 60, 390, 300);

		mPreviewBorder.setBackground(this.mainWidgetPanelColor);

		mPreviewPanel.setBackground(newColor); // no effect
		mPreviewPanel.setBounds(510, 80, 370, 270);
		// mInputStatusLabel.getY() + mInputStatusLabel.getHeight() - 160,
		// mWidgetPanel.getWidth() / 2 - 45, 280); // 200
		// mPreviewPanel.setBounds(500,
		// mInputStatusLabel.getY() + mInputStatusLabel.getHeight() - 160,
		// mWidgetPanel.getWidth() / 2 - 45, 280); // 200
		mPreviewBorder.add(mPreviewPanel);

		mProgressBar = new JProgressBar();
		mProgressBar.setMinimum(minValue);
		mProgressBar.setMaximum(complete);
		mProgressBar.setForeground(Color.lightGray);
		mProgressBar.setStringPainted(true);

		mProgressPanel = new JPanel();
		mProgressPanel.setBounds(500, 580, 385, 45);
		mProgressPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Progress", TitledBorder.LEADING, TitledBorder.TOP,
				null, SystemColor.blue));
		mProgressPanel.setBackground(this.mainWidgetPanelColor);
		mProgressBar.setBounds(510, 600, 365, 20);

		int yOffset = 210;
		int ySpacing = 50;
		int buttonNumber = 0; // counting from top to bottom, 0,1,...
		mConvertButton = new JButton("Convert");
		mConvertButton.setHorizontalAlignment(SwingConstants.LEFT);
		mConvertButton.setBackground(Color.LIGHT_GRAY);
		mConvertButton.setBounds(mWidgetPanel.getWidth() / 2 - 80, mProgressBar.getY() + mProgressBar.getHeight() - yOffset
				+ (buttonNumber++ * ySpacing) - 30, 120, 40);// 2

		mConvertButton.addActionListener(new ActionListener() {

			// >>>>>>>>>>>>>> the convert loop <<<<<<<<<<<<<<<
			// Each page in each file in mInputDirectoryPath is converted to
			// the chosen output format
			// and stored in mOutputDirectoryPath

			public void actionPerformed(ActionEvent e) {

				if (mInputDirectoryName != null && mOutputDirectoryName != null) {

					// savePreferences();
					// restorePreferences();

					File inputDir = new File(mInputDirectoryName);
					File outputDir = new File(mOutputDirectoryName);
					if (true)
					// if (inputDir.isDirectory() && outputDir.isDirectory())
					{
						mStopButton.setEnabled(true);
						mConvertButton.setEnabled(false);
						openError.setEnabled(false);
						mProgressBar.setEnabled(true);
						gDone = false;
						int delay = 0;
						int period = 1000;
						myTimer = new Timer();
						convertToFormat = determineConversionFormat();

						try {
							preprocessDirectory(); // / sets inputFileList
							mProgressBar.setMaximum(myFileProcessor.length);
						} catch (Exception ex) {
							System.out.println(" Error converting to current format...Please check your file or classpath for proper jar file "
									.toString());
							ex.printStackTrace();
						}

						// mProgressBar.setMaximum(myFileProcessor.length);
						/*
						 * Note the delay of 1000 ms before processing next image.
						 */
						/*
						 * This sample does NOT demonstrate the speed of RasterMaster
						 */

						myTimer.schedule(new TimerTask() {
							Random random = new Random();

							public void run() {
								if (counter < myFileProcessor.length && !gDone) {
									boolean pdfa = false;
									String currentFile = myFileProcessor[counter].getCurrentFile();
									String currentFileSaveAs = myFileProcessor[counter].getCurrentFileSaveAs();
									int currentPage = myFileProcessor[counter].getCurrentPage();
									s = new Snowbnd();
									s.setFrame(mBatchConvertFrame);
									jobOutput = new Job(currentPage, counter);
									imageResize = new ImageResizeDialog();
									docInputSettings = new DocumentInput();
									pdfOptions = new PDFOptionsDialog();
									pdfInputDialog = new PDFInputDialog();
									x_resize = 0;
									y_resize = 0;
									length[0] = 0;
									error[0] = 0;
									errorA[0] = 0;

									/*
									 * IMGLOW_set_document_input This method allows you to set the document DPI and Bit Depth. It is used for PDF, Word, PCL, and AFP formats. A higher DPI
									 * yields a higher quality document that takes longer to process and takes up more memory and storage space. A status of zero or higher indicates success
									 * Note using this option will post a -7 error, Please ignore this error. Page manipulation still works
									 */
									if (documentCheck.isSelected() && documentCheck.isEnabled()) {
										if (s != null && DocumentInput.dpiComboBox != null && DocumentInput.bitsPerPixelCombo != null) {
											{
												status = s.IMGLOW_set_document_input(DocumentInput.numDPI,DocumentInput.numBits, s.IMGLOW_get_filetype(currentFile));
												System.out.println(" Document Input status: " + status);
											}

										}
									}
									/*
									 * IMGLOW_set_pdf_input This method only works on PDF files, it allows for changing DPI and Bits A higher dpi yields a higher quality document
									 * that takes longer to process and take up more memory and storage space A status of zero indicates success
									 */
									if (pdfInputCheckbox.isSelected() && pdfInputCheckbox.isEnabled()) // make
																										// sure
																										// we
																										// have
																										// everything
																										// selected
									{
										if (s != null && PDFInputDialog.dpiComboBox != null && PDFInputDialog.bits_pix_ComboBox != null) {
											if (comboBox.getSelectedIndex() == 13) // PDF
																					// index
											{
												comboBox.getSelectedItem();
												// set pdf input changes the
												// DPI/Bits of the current PDF
												status = s.IMGLOW_set_pdf_input(PDFInputDialog.numDPI, PDFInputDialog.numBits_pix);
												System.out.println(" PDF Input status: " + status);

											}
										}
									}

									if (afpFontmapCheckbox.isSelected() && afpFontmapCheckbox.isEnabled()) {
										if (afpFontmapField.getText().length() > 0) {

											status = s.IMGLOW_set_fontmap_path(mAfpFontmapDirectoryName);
											System.out.println("AFP Font mapping Changed: " + status);
										}
									}

									/*
									 * This feature improves performance for decompression and conversion 1 = ON, 0 = OFF Only works with MO:DCA or AFP images
									 */
									if (fastConversionCheckbox.isSelected() && fastConversionCheckbox.isEnabled()) {
										// fast convert only work on AFP
										status = s.IMGLOW_set_fast_convert(1, 74);// AFP
										System.out.println(" Applying fast conversion to AFP image: " + status);
									}
									//
									// <<<<<<<<<<<<<<<<<<<<<<<<<< PDF/A <<<<<<<<<<<<<<<<<<<<<<<
									//
									// input dir, mInputDirectoryPath has been set
									// output dir, mOutputDirectoryPath has been set
									// IMGLOW_set_pdfa_font_path
									// IMGLOW_get_pages()
									//
									// if(IMGLOW_extract_text())				// vector
									//   IMG_save_document 						// searchable pdf
									// else										// raster
									//   IMG_decompress_bitmap()
									//   IMG_save_bitmap

									if (comboBox.getSelectedItem() == "PDFA") {
										pdfa = true;
										mPdfaFontmapDirectoryName = pdfaFontmapField.getText();
										System.out.println("mPdfaFontmapDirectoryName: " + mPdfaFontmapDirectoryName);
										File fontdir = new File(mPdfaFontmapDirectoryName);
										Snowbnd snowImage = new Snowbnd();

										if (fontdir.isDirectory() && snowImage.IMGLOW_set_pdfa_font_path(mPdfaFontmapDirectoryName) == 0) {

											System.out.println("converting to Pdf/a:");

											int[] error = new int[1];
											int[] length = new int[1];
											byte[] buff = null;
											
											if(settingPdfaFontDirectory)	{
												int result = snowImage.IMGLOW_set_pdfa_font_path(mPdfaFontmapDirectoryName);
												if (result == 0) {
													settingPdfaFontDirectory = false;
													System.out.println("Setting pdfa font path to " + mPdfaFontmapDirectoryName);
												} 	
												else {
													System.out.println("Problem setting font path to " + mPdfaFontmapDirectoryName + " result: " + result);
												}
											}
											Path currentFilePath = Paths.get(currentFile);
											int status = 0;
											int numberOfPages = snowImage.IMGLOW_get_pages(currentFile);
											System.out.println("Converting " + currentFile + ": numberOfPages: " + numberOfPages);

											String fullOutputFileName = "";
											for (currentPage = 0; currentPage < numberOfPages; currentPage++) {

												if (currentPage == 0) {
													Path outputDirectoryPath = Paths.get(mOutputDirectoryName);
													Path outputFilePath = Paths.get(currentFile);
													Path fullOutputPath = outputDirectoryPath.resolve(outputFilePath.getFileName());
													fullOutputFileName = fullOutputPath.toString() + ".pdf";
													fullOutputPath = Paths.get(fullOutputFileName);

													try {
														Files.deleteIfExists(fullOutputPath);
													} catch (IOException e) {
														System.out.println("Unable to delete " + fullOutputPath.toString());
														e.printStackTrace();
													}
													System.out.println("Saving to  " + fullOutputPath.toString());
												}

												buff = snowImage.IMGLOW_extract_text(currentFile, length, error, currentPage);

												if (buff != null || error[0] >= 0) { // must be a vector document

													System.out.println("vector document: text buff length: " + buff.length + "  page: " + currentPage);

													int toDisplayPreview = s.IMG_decompress_bitmap(currentFile, currentPage);
													mPreviewPanel.repaint();

													status = snowImage.IMG_save_document(fullOutputFileName, buff, Snow.Defines.PDFA);

													if (status < 0) {
														System.out.println("Save Error on vector document: " + status);
														//break;
													}
												} else {
													System.out.println("raster document: no text extracted");

													status = snowImage.IMG_decompress_bitmap(currentFile, currentPage);
													if (status < 0) {
														System.out.println("Decompress Error: " + status);
														//break;
													}
													status = snowImage.IMG_save_bitmap(fullOutputFileName, Snow.Defines.PDF);
													if (status < 0) {
														System.out.println("Save Error on bitmap document: " + status);
														//break;
													}
												}
												counter++;	// update the master list here
											}	// for(currentPage...)
										}	// if(fontDir...)
										else {
											System.out.println("Valid Pdf/a fonts directory not specified.");
											System.out.println(" Fonts Directory: " + mPdfaFontmapDirectoryName);
											gDone = true;
										}
										return;	// it would be nice if the current thread ended here and got rid of the spinning cursor
									} // end of Pdfa
									if (!pdfa) {
										if (comboBox.getSelectedItem() == "SVG") {

											/*
											 * SVG conversion
											 */

											System.out.println("converting to svg... counter = " + counter);

											Path in = Paths.get(currentFile);
											String fName = (in.getFileName()).toString();
											int i = fName.lastIndexOf('.');
											fName = fName.substring(0, i);

											String pnum = "_p" + String.valueOf(currentPage);

											String ext = ".svg";

											String pagedFilename = mOutputDirectoryName + "/" + fName + pnum + ext;

											status = s.IMG_vector_to_svg(currentFile, pagedFilename, currentPage);

											int toDisplayPreview = s.IMG_decompress_bitmap(currentFile, currentPage);
										}	// SVG 
										else {
											status = s.IMG_decompress_bitmap(currentFile, currentPage);
									
										}	// SVG

										setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
										if (status < 0) // check decompression for error. 
														//Return value less than zero is an error
										{
											/*
											 * if there was an error in decompression, check if our field is enabled we will then log out all the problem files to a dir
											 */
											if (mProblemFilesField.isEnabled() && mProblemFilesField.getText().length() > 0) {
												File sourceDir = new File(currentFile); // check
																						// the
																						// current
																						// file
												File desDir = new File(mProblemDirectoryName); // outputProblem Path
												try {
													// move all problem files
													// and
													// right name to desDir
													boolean moveSuccessful = sourceDir.renameTo(new File(desDir, sourceDir.getName()));
													System.out.println(" Error, within: " + sourceDir.getName() + " was moved to: "
															+ desDir.getAbsolutePath());
													if (!moveSuccessful) {
														System.out.println(" Error, file was not moved");
													}
												} catch (Exception ex) {
													ex.printStackTrace();
												}
												/*
												 * Return values less than zero indicate an error
												 */
												System.out.println(" Error decompressing page at pageIndex = " + currentFile + " : "
														+ ErrorCodes.getErrorMessage(status));
												System.out.println(" Corrupted file was moved ");
											} else {
												/*
												 * Return values less than zero indicate an error
												 */
												System.out.println(" Error decompressing page at pageIndex = " + currentPage + " : "
														+ ErrorCodes.getErrorMessage(status));
											}

										}
										/*
										 * IMGLOW_detect_color This method checks all pixels to determine if the image is color or grayscale
										 */
										if (detectBitDepthCheckbox.isSelected() && detectBitDepthCheckbox.isEnabled()) {
											int pageBitsPerPixel = s.IMGLOW_detect_color();
											System.out.println(" The bit depth of the page at pageIndex " + currentPage + " is " + pageBitsPerPixel);
										}

										/*
										 * IMG_resize_bitmap will change the internal size of the current image this defers from pdf_input A status of zero indicates success
										 */
										if (checkResizeImage.isSelected() && checkResizeImage.isEnabled()) {
											if (s != null && ImageResizeDialog.x_resize != 0 && ImageResizeDialog.y_resize != 0) {
												status = s.IMG_resize_bitmap(ImageResizeDialog.xstatus, ImageResizeDialog.ystatus);
												System.out.println(" Image Resize status: " + status);

											}
										}
										/*
										 * 59 is the snowbound PDF format 
										 * IMGLOW_extract_text(): This method extracts text from PTOCA, PCL, PDF, MS Word, AFP, RTF and MS Excel files.
										 * It returns the buffer of extracted text in ASCII format. 
										 * IMG_save_document. This method will save to a searchable PDF file 
										 * A status of zero indicates success 
										 */
										if (extractTextCheckbox.isSelected() && extractTextCheckbox.isEnabled()) {
											savedAsSearchablePdf = true;
											if (s != null) {
												try {
													byte extractedText[] = s.IMGLOW_extract_text(currentFile, length, error, currentPage);

													status =  s.IMG_save_document(currentFileSaveAs,  extractedText,  Snow.Defines.PDF);	// 59
													
													System.out.println(" Saving SearchablePDF: " + status);
													System.out.println(" ExtractedText..." + length[0]);
												} catch (Exception ex) {
													ex.printStackTrace();
												}
											}
										}
										else 	{
											savedAsSearchablePdf = false;
										}

										/*
										 * IMGLOW_set_pdf_output This method sets the output size for saving PDF files A status of zero indicates success
										 */
										if (pdfResizeCheckbox.isSelected() && pdfResizeCheckbox.isEnabled()) {
											if (s != null && PDFOptionsDialog.x_resize_pdf != 0 && PDFOptionsDialog.y_resize_pdf != 0) {
												if (s != null) {
													status = s.IMGLOW_set_pdf_output(PDFOptionsDialog.pdf_x_status, PDFOptionsDialog.pdf_y_status);
													System.out.println(" PDF Output Size: " + status);

												}
											}
										}
										/*
										 * Convert 8-bit grayscale PIXEL_DEPTH_UNSUPPORTED Indicates the output format does not support the current bits per pixel. If so, the
										 * current image/page will be dropped A status of zero indicates success
										 */
										if (selectgrayScale.isSelected() && selectgrayScale.isEnabled()) {
											if (s != null) {
												status = s.IMG_color_gray();
												System.out.println("Color image to 8-bit " + status);

											}
											if (status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED) {
												System.out.println(" saving_bitmap: " + status);
												System.out.println(" save_bitmap error: " + ErrorCodes.getErrorMessage(status));
												System.out.println(" Trying to convert current page to lowest bit...");
												status = s.IMG_diffusion_mono();
												System.out.println(" Image diffusion: " + status);
											}
										}
										// GrayScale Histogram
										if (histogramCheckbox.isSelected() && histogramCheckbox.isEnabled()) {
											if (s != null) {
												status = s.IMG_histogram_equalize();
												System.out.println("Improving 8-bit GrayScale image: " + status);
											}
										}
										/*
										 * Promote to 24-bit IMG_promote_24. This method permanently converts the current 1, 4, or 8-bit image to a 24-bit image.
										 * PIXEL_DEPTH_UNSUPPORTED Indicates the output format does not support the current bits per pixel. If so, the current image/page will be
										 * dropped A status of zero indicates success
										 */
										if (selectPromote.isSelected() && selectPromote.isEnabled()) {
											if (s != null) {
												status = s.IMG_promote_24();
												System.out.println(" Image changed to 24bit: " + status);

											}
											if (status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED) {
												System.out.println(" saving_bitmap: " + status);
												System.out.println(" save_bitmap error: " + ErrorCodes.getErrorMessage(status));
												System.out.println(" Trying to diffuse image...");
												status = s.IMG_diffusion_mono();
												System.out.println(" Image_diffusion: " + status);
												// status =
												// s.IMG_save_bitmap(currentFileSaveAs,
												// convertToFormat);
											}
										}

										/*
										 * convert RGB to CMYK IMG_rgb_to_cmyk.This method converts the current image from 24-bit RGB data to 32-bit CMYK data If the current image
										 * is not 24bit, error: PIXEL_DEPTH_UNSUPPORTED If so, the current image/page will be dropped A status of zero indicates success
										 */
										if (convertCMYKCheckbox.isSelected() && convertCMYKCheckbox.isEnabled()) {
											if (s != null) {
												status = s.IMG_rgb_to_cmyk();
												System.out.println(" Converting current 24-bit to 32-bit CMYK data: " + status);

											}
											if (status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED) {
												System.out.println(" saving_bitmap: " + status);
												System.out.println(" save_bitmap error: " + ErrorCodes.getErrorMessage(status));
												System.out.println(" Trying to convert to closest bit...");
												status = s.IMG_color_gray();
												System.out.println(" Color Gray status: " + status);
												if (status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED) {
													System.out.println(" saving_bitmap: " + status);
													System.out.println(" save_bitmap error: " + ErrorCodes.getErrorMessage(status));
													System.out.println(" Trying to convert current page to lowest bit...");
													status = s.IMG_thresh_mono();
													System.out.println(" Converting to 1-bit status: " + status);
													// status =
													// s.IMG_save_bitmap(currentFileSaveAs,
													// convertToFormat);
												}
												// status =
												// s.IMG_save_bitmap(currentFileSaveAs,
												// convertToFormat);

											}

										}

										/*
										 * verify no errors occurred while decompressing PIXEL_DEPTH_UNSUPPORTED Indicates the output format does not support the current bits per
										 * pixel. If so, the current image/page will be dropped A status of zero indicates success
										 */
										if (status > ErrorCodes.OUT_OF_MEMORY && !savedAsSearchablePdf) {
											System.out.println(" Decompress bitmap status: " + status);
											status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
											mOutputStatusLabel.setText("Output Status: " + status);
											jobOutput.complete();
											if (status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED) {
												System.out.println(" saving_bitmap: " + status);
												System.out.println(" save_bitmap error: " + ErrorCodes.getErrorMessage(status));
												System.out.println(" Pixel Depth will be Dropped to 8-bit...");
												status = s.IMG_color_gray();
												System.out.println(" Color Gray status: " + status);
												status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
												if (status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED) {
													System.out.println(" saving_bitmap: " + status);
													System.out.println(" save_bitmap error: " + ErrorCodes.getErrorMessage(status));
													System.out.println(" Convert current page to lowest bit...");
													status = s.IMG_diffusion_mono();
													System.out.println(" Converting image to 1-bit: " + status);
													// status =
													// s.IMG_save_bitmap(currentFileSaveAs,
													// convertToFormat);
												}
											}
											/*
											 * If successful, IMG_save_bitmap() returns the cumulative number of bytes written out
											 */
											System.out.println(" save_bitmap Successful: " + status);

											if (status == ErrorCodes.JWEBENGINE_JAR_NOT_IN_CLASSPATH) {
												System.out.println(" save_bitmap: " + status);
												System.out.println(" save_bitmap error: " + ErrorCodes.getErrorMessage(status));
											}
										}
										/*
										 * error on decompression, display message
										 */
										else if(!savedAsSearchablePdf)	{
											System.out.println(" decompress_bitmap status: " + status);
											System.out.println(" saved_bitmap error: " + ErrorCodes.getErrorMessage(status));
											System.out.println(" Error, page or file will be skipped : " + currentFile);

										}
									}
									mInputStatusLabel.setText("Input Status: " + status);

									mPreviewPanel.repaint();
									mPreviewPanel.updateUI();

									// status =
									// s.IMG_save_bitmap(currentFileSaveAs,
									// convertToFormat);

									/*
									 * if(status >= 0)//log which file got skipped { //status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
									 * //System.out.println(" Page # " + currentPage + " successful ;"); mOutputStatusLabel .setText("Output Status: " + status); output.complete();
									 * } else { //output.terminate(); System.out.println( " Error, file will be skipped : " + currentFile); }
									 */

									counter++;
									mProgressBar.setValue(counter);

									while (counter <= minValue) {
										Runnable runable = new Runnable() {

											public void run() {
												mProgressBar.setValue(counter);
												// mProgressBar.repaint();
											}

										};
										EventQueue.invokeLater(runable);
										counter += random.nextInt(10);

										try {
											Thread.sleep(random.nextInt(1000)); // sleep
																				// for
																				// 1second

										} catch (InterruptedException e) {
										}
									}

									System.out.println(" Current TestThread ended:  " + Thread.currentThread().getName() + " BatchConvert "
											+ new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(endTime = new Date()));
									System.out.println("------------------------------------------------------------------------");

									// Log out the total conversion time
									long totalTime = endTime.getTime() - startTime.getTime();
									System.out.println("total conversion time = " + (double) totalTime / 1000 + " seconds ");

									// Just for separation
									System.out.println("------------------------------------------------------------------------");

									// count pages from 1
									if (currentPage >= 0) {
										textOutput.append("---------------------------------------------" + "\n");
										currentPage++;
										jobOutput.waitTick();
										System.out.println(jobOutput);
										textOutput.append(currentFile.toString() + "\n" + " Page  " + currentPage + "\n"
												+ " total conversion time = " + (double) totalTime / 1000 + " seconds " + "\n");
									}

									if (true)
										return;

								} else {
									myTimer.cancel();
									try {
										// Sleep for 2seconds before resetting
										// the progressBar
										Thread.sleep(2000);
										Toolkit.getDefaultToolkit().beep();
										mProgressBar.setValue(mProgressBar.getMinimum());
									} catch (InterruptedException ex) {
									}
									mConvertButton.setEnabled(true);
									mStopButton.setEnabled(false);
									openError.setEnabled(true);
									mProgressBar.setEnabled(false);
									counter = 0;
									totalPageCount = 0;
								}
							}
						}, delay, period);	// new TimerTask
					} else {
						System.out.println("Directory Paths do not both point to directories.");
						System.out.println(" InputDirectory: " + inputDir.getName() + " " + inputDir.isDirectory());
						System.out.println(" OutputDirectory: " + outputDir.getName() + " " + outputDir.isDirectory());
					}
				}	// if(!pdfa

				else {
					System.out.println("One or more directory paths not specified.");
					System.out.println(" InputDirectory: " + mInputDirectoryName);
					System.out.println(" OutputDirectory: " + mOutputDirectoryName);
				}	// if(!pdfa)
			}
		}); // >>>>>>>>>>>>>> end of the convert loop <<<<<<<<<<<<<<<

		mStopButton = new JButton("Stop");
		mStopButton.setHorizontalAlignment(SwingConstants.LEFT);
		mStopButton.setBackground(Color.LIGHT_GRAY);
		mStopButton.setBounds(mWidgetPanel.getWidth() / 2 - 80, mProgressBar.getY() + mProgressBar.getHeight() - yOffset
				+ (buttonNumber++ * ySpacing) - 30, 120, 40);
		mStopButton.setEnabled(false);
		mStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gDone = true;
				mConvertButton.setEnabled(true);
				mStopButton.setEnabled(false);
				openError.setEnabled(true);
				mProgressBar.setEnabled(false);
				totalPageCount = 0;
				counter = 0;
			}
		});

		mCancelButton = new JButton("Cancel");
		mCancelButton.setHorizontalAlignment(SwingConstants.LEFT);
		mCancelButton.setBackground(Color.LIGHT_GRAY);
		mCancelButton.setBounds(mWidgetPanel.getWidth() / 2 - 80, mProgressBar.getY() + mProgressBar.getHeight() - yOffset
				+ (buttonNumber++ * ySpacing) - 30, 120, 40);// 2
		mCancelButton.setEnabled(true);
		mCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);// clean up
			}
		});

		openError = new JButton("Log file");
		openError.setBounds(mWidgetPanel.getWidth() / 2 - 80, mProgressBar.getY() + mProgressBar.getHeight() - yOffset + (buttonNumber++ * ySpacing)
				- 30, 120, 40);
		openError.setEnabled(true);
		openError.addActionListener(new ActionListener()// Event Dispatcher
				{
					public void actionPerformed(ActionEvent e) {
						File file = new File(logFileName);
						Desktop desk = Desktop.getDesktop();
						if (file.exists()) {
							// Check for supported desktop type return
							// if current desktop is not supported
							if (!Desktop.isDesktopSupported()) {
								return;
							}
							if (!desk.isSupported(Desktop.Action.OPEN)) {
								return;
							}
							try {
								Desktop.getDesktop().open(file);// open the log
																// file
							} catch (IOException ex) {
								System.out.println(" File was not created" + ex.getMessage());
							}
							return;
						}
					}

				});
		mWidgetPanel.add(mInputDirLabel);
		mWidgetPanel.add(mOutputDirLabel);
		mWidgetPanel.add(mInputDirField);
		mWidgetPanel.add(mOutputDirField);
		mWidgetPanel.add(mInputDirButton);
		mWidgetPanel.add(mOutputDirButton);
		mWidgetPanel.add(mInputStatusLabel);
		mWidgetPanel.add(mOutputStatusLabel);
		mWidgetPanel.add(mPreviewPanel);
		mWidgetPanel.add(mProgressBar);
		mWidgetPanel.add(mConvertButton);
		mWidgetPanel.add(mStopButton);
		mWidgetPanel.add(mCancelButton);
		mWidgetPanel.add(openError);
		mMainPanel.add(mWidgetPanel);

		newColor = Color.pink;
		mMainPanel.setBackground(newColor);
		newColor = originalColor;

		mWidgetPanel.add(textOutputPanel);
		mWidgetPanel.add(comboPanel);
		mWidgetPanel.add(filePreviewPanel);
		mWidgetPanel.add(separator);
		mWidgetPanel.add(middleSeparator);
		mWidgetPanel.add(logoArea);
		mWidgetPanel.add(verPanel);
		mWidgetPanel.add(mProgressPanel);
		mWidgetPanel.add(mPreviewBorder);
		mWidgetPanel.add(statusPanel);
		mWidgetPanel.add(dateLabel);
		mWidgetPanel.add(deleteFiles);
		mWidgetPanel.add(removeFiles);
		mWidgetPanel.add(resizeOptions);
		mWidgetPanel.add(checkResizeImage);
		mWidgetPanel.add(headerImage);
		mWidgetPanel.add(menuBar);
		mWidgetPanel.add(histogramCheckbox);
		mWidgetPanel.add(mProblemFilesField);
		mWidgetPanel.add(inputDocument);
		mWidgetPanel.add(documentCheck);
		mWidgetPanel.add(grayScale);
		mWidgetPanel.add(promoteImage);
		mWidgetPanel.add(selectPromote);
		mWidgetPanel.add(selectgrayScale);
		mWidgetPanel.add(pdfResizeLabel);
		mWidgetPanel.add(pdfResizeCheckbox);
		mWidgetPanel.add(pdfInputLabel);
		mWidgetPanel.add(pdfInputCheckbox);
		mWidgetPanel.add(cmykLabel);
		mWidgetPanel.add(convertCMYKCheckbox);
		mWidgetPanel.add(histogramLabel);
		mWidgetPanel.add(fasConversionLabel);
		mWidgetPanel.add(fastConversionCheckbox);
		mWidgetPanel.add(problemFilesLabel);
		mWidgetPanel.add(mProblemFilesButton);
		mWidgetPanel.add(problemFilesCheckbox);
		mWidgetPanel.add(textExtractLabel);
		mWidgetPanel.add(extractTextCheckbox);
		mWidgetPanel.add(detectBitDepthLabel);
		mWidgetPanel.add(detectBitDepthCheckbox);
		mWidgetPanel.add(afpFontmapLabel);
		mWidgetPanel.add(afpFontmapCheckbox);
		mWidgetPanel.add(afpFontmapField);
		mWidgetPanel.add(mAfpFontmapButton);

		mWidgetPanel.add(pdfaFontmapLabel);
		mWidgetPanel.add(pdfaFontmapCheckbox);
		mWidgetPanel.add(pdfaFontmapField);
		mWidgetPanel.add(mPdfaFontmapButton);

		mWidgetPanel.add(consoleOutputLabel);
		mWidgetPanel.add(showConsoleOutput);

	}

	public void layoutPanels() {
		layoutMainPanel();
		layoutWidgetPanel();
		mConvertButton.setIcon(new ImageIcon(BatchConvert7.class.getResource("/com/resource/go.png")));
		mCancelButton.setIcon(new ImageIcon(BatchConvert7.class.getResource("/com/resource/Close.png")));
		mStopButton.setIcon(new ImageIcon(BatchConvert7.class.getResource("/com/resource/stopIcon.png")));
		openError.setIcon(new ImageIcon(BatchConvert7.class.getResource("/com/resource/images.jpg")));
		// mInputDirButton.setIcon(new
		// ImageIcon(BatchConvert.class.getResource("/com/resource/open.png")));
		// mOutputDirButton.setIcon(new
		// ImageIcon(BatchConvert.class.getResource("/com/resource/open.png")));
		// sendDirButton.setIcon(new
		// ImageIcon(BatchConvert.class.getResource("/com/resource/open.png")));

	}

	public void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/*
	 * Log out all System.Error to a log file
	 */
	public static void saveConsoleError(String filename, String initialStr, boolean logOutFile, boolean append) throws Throwable {
		PrintStream out = new PrintStream(new FileOutputStream(logFileName));
		PrintStream console = System.out;
		try {
			logStream = new BatchConvert7(new FileOutputStream(logFileName, append), logOutFile, console);
			System.setOut(out);
			System.setErr(out);
			System.setOut(console);
			// System.out.println(out);
		} catch (IOException ex) {
			System.err.println("Could not outputFile " + logFileName);
			System.exit(1);
		}
		if ((initialStr != null) && (initialStr.length() != 0)) {
			System.err.println(initialStr);
		}
	}

	private void updateConsoleTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fileOuput.append(text);
			}
		});
	}

	/*
	 * Redirect all System.out to our console Textfield
	 */
	private void redirectSystemOutStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateConsoleTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateConsoleTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	/*
	 * public static void saveFileBytes(byte[] data, File file) { if (data == null) { return; } try { FileOutputStream fos = new FileOutputStream(file); fos.write(data);
	 * fos.close(); } catch (Exception e) { Logger.getInstance().printStackTrace(e); } }
	 */
	/*
	 * public void remove() { if(outputList.getSelectedIndices().length > 0) { int[] index = outputList.getSelectedIndices(); for (int i = index.length-1; i >=0; i--) {
	 * listModel.removeElementAt(i); } } outputList.updateUI(); }
	 */
	public int getsize() {
		return this.numItems;
	}

	public void execute() {
		this.setTitle("Snowbound Software :: Batch Convert");
		// this.setSize(650, 400);
		// setBounds(100, 100, 863, 623);
		setBounds(100, 100, mainPanelWidth, mainPanelHeight);
		this.setResizable(false);
		initializeFormatsList();
		layoutPanels();
		addListeners();
		this.setVisible(true);
	}

	public synchronized boolean openDirectory() {
		File aDirectory = new File(mOutputDirectoryName);
		Desktop desk = Desktop.getDesktop();
		if (aDirectory.isDirectory()) {
			if (aDirectory.exists()) {
				if (!Desktop.isDesktopSupported()) {
					return false;
				}
				if (!desk.isSupported(Desktop.Action.OPEN)) {
					return false;
				}
				try {
					Desktop.getDesktop().open(aDirectory);// open Output Folder
					// Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL \""
					// + aDirectory +"\"");
				} catch (IOException ex) {
					System.out.println(" Directory did not exist " + ex.getMessage());
				}
				return false;
			}
		}
		return true;
	}

	/*
	 * private static void recursiveDirectory(String dirPath, File directory, String ext, boolean recv, List filenames) { String[]listofFileName = directory.list();//list files in
	 * current directory for(int i = 0; i < listofFileName.length; i++){ String name = listofFileName[i]; File afile = new File(directory, name);//process both the directory and
	 * our file name if(afile.isDirectory()){ if(recv){//Set to true, if we want to read subdir (hangs up gui, RasterMaster thinks its a file) recursiveDirectory(dirPath + name +
	 * File.separatorChar, afile, ext, recv, filenames);//recursive call } else if(ext == null || name.endsWith(ext)){//check if extension has an extension filenames.add(dirPath +
	 * name);//add directoryPath and name to our list }
	 * 
	 * }
	 * 
	 * } }
	 */
	private static int scanDirectoryFiles(File directory) {
		int size = 0; // bytes
		for (File afile : directory.listFiles()) {
			if (afile.isFile()) {
				size += afile.length();
			}
		}
		File[] dis = directory.listFiles();
		for (File dir : dis) {
			if (dir.isDirectory()) {
				size += scanDirectoryFiles(dir);
			}
		}
		return size;
	}

	public synchronized void deleteDirectoryFiles() {
		File directory = new File(mInputDirectoryName);
		File[] allFiles = directory.listFiles();
		for (File afile : allFiles) {
			if (afile.isFile()) {
				afile.delete();
			} else if (mInputDirectoryName.length() > 0) {
				System.out.println("Could not delete " + afile);
			}
		}
	}

	public static void main(String[] argv) throws FileNotFoundException, Throwable {
		splash = new SplashWindow();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			splash.showSplash();
			// XXX Thread.sleep(5000);
			Thread.sleep(1000);
			splash.dispose();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		mBatchConvertFrame = new BatchConvert7(null, true, log);
		Rectangle r = mBatchConvertFrame.getBounds();
		r.width += 0.5 * r.width;
		r.height += 0.5 * r.height;
		mBatchConvertFrame.setBounds(r);
		mBatchConvertFrame.execute();

		saveConsoleError(logFileName, "Test Log: " + new Date(), true, true);// log
																				// the
																				// console
																				// output
																				// to
																				// File

	}
}