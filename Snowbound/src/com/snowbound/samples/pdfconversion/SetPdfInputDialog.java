package com.snowbound.samples.pdfconversion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A Set PDF Input Dialog that allows the user to select values for the
 * input DPI and bit depth of the rendered bitmap image of the PDF file.
 */
public class SetPdfInputDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 3816318704165193425L;
	
	private JFormattedTextField dpiField;
    private JComboBox bitCombo;
	
	private Integer dpi;
	private Integer bpd;
	
	/**
	 * Constructor for the Set PDF input Dialog.
	 * 
	 * @param frame - The Frame that contains the dialog.
	 * @param component - The Component that is dialog is located
	 *                    relative to.
	 */
	public SetPdfInputDialog(Frame frame, Component component)
	{
		super(frame, "PDF Input Options", true);
		
		// Initialize dpiBox panel and components
		JPanel dpiBox = new JPanel();
		dpiBox.setBorder(BorderFactory.createTitledBorder("DPI"));
		Dimension d2 = new Dimension(200, 60);
		dpiBox.setPreferredSize(d2);
		dpiBox.setMaximumSize(d2);
		dpiBox.setMinimumSize(d2);
		
		dpiField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		dpiField.setValue(200);
		dpiField.setColumns(8);
		dpiField.setToolTipText("Dots per inch in which to render the PDF.");
		
		dpiBox.add(new JLabel("Enter DPI: "));
		dpiBox.add(dpiField);
		
		// Initialize bitBox panel and components
		JPanel bitBox = new JPanel();
		bitBox.setBorder(BorderFactory.createTitledBorder("Bit Depth"));
		Dimension d3 = new Dimension(200, 90);
		bitBox.setPreferredSize(d3);
		bitBox.setMaximumSize(d3);
		bitBox.setMinimumSize(d3);
		
		String[] bitDepths = {"1 (Black & White)", "8 (Grayscale)", "24 (Color)"};
        bitCombo = new JComboBox(bitDepths);
		bitCombo.setSelectedIndex(2);
		bitCombo.setToolTipText("Bitmap pixel depth for rendered PDF.");
		
		bitBox.add(new JLabel("Select bit depth:"));
		bitBox.add(bitCombo);
		
		// Add dpiBox and bitBox to optionsPane
		JPanel optionsPane = new JPanel();
		optionsPane.add(new JLabel("< Set PDF Input >"));
		optionsPane.add(dpiBox);
		optionsPane.add(bitBox);
		
		// Initialize buttons
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		// Add buttons to buttonPane
		JPanel buttonPane = new JPanel();
		buttonPane.add(submitButton);
		buttonPane.add(cancelButton);
		
		// Add all components to the frame
		this.getContentPane().setLayout(new BorderLayout(2, 2));
		this.add(optionsPane, BorderLayout.CENTER);
		this.add(buttonPane, BorderLayout.SOUTH);
	
		// Initialize frame
		this.setSize(230, 250);
		this.setLocationRelativeTo(component);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * Handles clicks on the "Submit" and the "Cancel" buttons.
	 * 
	 * @param e - The ActionEvent that occurred
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Submit"))
		{
			dpi = ((Number)dpiField.getValue()).intValue();
			
			int index = bitCombo.getSelectedIndex();
			switch (index)
			{
				case 0:
					bpd = 1;
					break;
				case 1:
					bpd = 8;
					break;
				case 2:
					bpd = 24;
					break;
				default:
					bpd = 24;
					break;
			}
			
			this.dispose();
		}
		else
		{
			this.dispose();
		}
	}
	
	/**
	 * Gets the DPI entered by the user.
	 * 
	 * @return - The DPI
	 */
	public Integer getDPI()
	{
		return dpi;
	}
	
	/**
	 * Gets the bit depth selected by the user.
	 * 
	 * @return - The bit depth
	 */
	public Integer getBPD()
	{
		return bpd;
	}
}
