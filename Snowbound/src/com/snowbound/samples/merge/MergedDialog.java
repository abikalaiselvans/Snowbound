package com.snowbound.samples.merge;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import Snow.ErrorCodes;
import Snow.Snowbnd;

import com.snowbound.samples.common.dialog.SaveJDialog;

/**
 * A Dialog that displays the Merged Image.
 */
public class MergedDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 4964641861882377001L;
	
	private Frame frame;
	
	private Snowbnd simage;
	
	private String openDir;

	/**
	 * Constructs a Dialog to display the Merged Image.
	 * 
	 * @param frame - The frame that contains the dialog
	 * @param component - The component this dialog's position is relative to
	 * @param simage - The Snowbnd object of the merged image
	 */
	public MergedDialog(Frame frame, Component comp, Snowbnd simage, String openDir)
	{
		super(frame, "Snowbound Software :: Merged Image Preview", false);
		
		this.frame = frame;
		this.simage = simage;
		this.openDir = openDir;
		
		// Create Buttons and add to Panel
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		
		JPanel buttonPane = new JPanel();
		buttonPane.add(saveButton);
		buttonPane.add(closeButton);
		
		// Create Preview Panel for the Merged Image
		PreviewPanel previewPane = new PreviewPanel(simage);
		previewPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		previewPane.repaint();
		
		// Add Panels to Dialog
		this.getContentPane().setLayout(new BorderLayout());
		this.add(previewPane, BorderLayout.CENTER);
		this.add(buttonPane, BorderLayout.SOUTH);
		
		// Resize Dialog to scale to image dimensions
		double width = simage.getWidth();
		double height = simage.getHeight();
		
		if (width >= height)
		{
			this.setSize(600, (int)(650 * height/width));
		}
		else
		{
			this.setSize((int)(780 * width/height), 800);
		}
			
		// Initialize Dialog
		this.setLocationRelativeTo(comp);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * Handles clicks on the "Save" and "Close" buttons.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Save"))
		{
			// Open a SaveJDialog for the user to select the output file type
			SaveJDialog sjd = new SaveJDialog(frame, this);
			
			String format = sjd.getSelectedValue();
			
			if (format == null)
			{
				return;
			}
			
			String formatExt = sjd.getFormatExtension(format);
			int formatCode = sjd.getFormatCode(format);
			
			// Open a Save Dialog from the JFileChooser for the user to specify
			// the output file name.
			JFileChooser chooser = new JFileChooser();
			
			chooser.setMultiSelectionEnabled(false);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileNameExtensionFilter(
					              format + " (*" + formatExt + ")",
                                  formatExt.replace(".", "")));
			
			if (openDir != null)
			{
				chooser.setCurrentDirectory(new File(openDir));
			}
				
			int option = chooser.showSaveDialog(frame);
			
			if (option == JFileChooser.APPROVE_OPTION &&
				chooser.getSelectedFile() != null)
			{
				String outputFileName = chooser.getSelectedFile().getPath();
				
				int status = simage.IMG_save_bitmap(outputFileName + formatExt,
                                                    formatCode);
				
				if (status > -1)
				{
					System.out.println("Merged image saved to: " + 
				                       outputFileName + formatExt);
				}
				else
				{
					System.out.println("Save Image Failed: " + 
				                       ErrorCodes.getErrorMessage(status));
				}
			}
		}
		else
		{
			this.dispose();
		}
	}	
}
