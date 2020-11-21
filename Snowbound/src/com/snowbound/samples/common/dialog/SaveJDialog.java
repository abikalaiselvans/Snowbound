package com.snowbound.samples.common.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * A Save Dialog that allows the user to select the format of the
 * file they want to save. Select the file format and press "Okay"
 * or double-click on the file type to select it. 
 */
public class SaveJDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 3885081875218870245L;

    private JList list;
	private JButton okayButton;
	private JButton cancelButton;
	
	private String value;
	
	private static String[] formats = {"ASCII", "BMP_COMPRESSED", "BMP_UNCOMPRESSED",
                                       "CALS", "DIB", "DICOM", "EPS", "GIF",
                                       "GIF_INTERLACED", "IMNET", "IOCA", "JBIG2",
                                       "JEDMICS", "JPEG", "MO:DCA_IOCA", "NCR", "PDF",
                                       "PDF-JBIG2", "PNG", "TIFF-2D", "TIFF_ABIC",
                                       "TIFF_ABIC_BW", "TIFF_G3_FAX", "TIFF_G4_FAX",
                                       "TIF_G4-FAX_FO", "TIFF_HUFFMAN", "TIFF-JPEG",
                                       "TIFF_LZW", "TIFF_PACK", "TIFF_UNCOMPRESSED",
                                       "XEROX_EPS"};
	
	private static String[] extensions = {".txt", "bmp", ".bmp", ".cal", ".dib", ".dcm",
		                                  ".eps", ".gif", ".gif", ".imt", ".ica", ".jb2",
		                                  ".jed", ".jpg", ".mca", ".ncr", ".pdf", ".pdf",
		                                  ".png", ".tif", ".tif", ".tif", ".tif", ".tif",
		                                  ".tif", ".tif", ".tif", ".tif", ".tif", ".tif",
		                                  ".eps"};
	
	private static int[] formatCodes = {38, 12, 1, 18, 48, 55, 14, 4, 44, 42, 24, 77,
		                                56, 13, 49, 65, 59, 79, 43, 17, 46, 47, 8, 10,
		                                51, 7, 40, 9, 16, 0, 45};
	
	private HashMap<String, String> extMap;
	private HashMap<String, Integer> codeMap;
	
	/**
	 * Constructor for a SaveJDialog
	 * 
	 * @param frame - The frame for the SaveJDialog
	 * @param component - The component in which the Dialog's location
	 *                    is relative to
	 */
	public SaveJDialog(Frame frame, Component component)
	{
		super(frame, "Select Output Format", true);
		
		// Initialize and add values to the HashMaps for format
		// extensions and format codes
		extMap = new HashMap<String, String>();
		codeMap = new HashMap<String, Integer>();
		
		for (int i = 0; i < formats.length; i++)
		{
			extMap.put(formats[i], extensions[i]);
			codeMap.put(formats[i], formatCodes[i]);
		}
		
		// Initialize buttons
		okayButton = new JButton("Okay");
		okayButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		// Initialize the JList for format selection
        list = new JList(formats);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2) 
				{
					okayButton.doClick();
				}
			}
		});
		
		// Add Components to Panels
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okayButton);
		buttonPanel.add(cancelButton);
		
		JScrollPane listScroller = new JScrollPane(list);
		
		// Add Panels to frame
		this.getContentPane().setLayout(new BorderLayout());
		this.add(listScroller, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		// Initialize frame
		this.setSize(300, 500);
		this.setResizable(false);
		this.setLocationRelativeTo(component);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * Handles clicks on the Okay and Cancel buttons
	 * 
	 * @param e - The ActionEvent that occurred
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("Okay"))
		{
            value = (String) list.getSelectedValue();
			
			if (value != null)
			{
				this.dispose();
			}		
		}
		else
		{
			this.dispose();
		}
	}
	
	/**
	 * Returns the value of the selection
	 * 
	 * @return The value of the selection
	 */
	public String getSelectedValue()
	{
		return value;
	}
	
	/**
	 * Gets the format extension of the specified format
	 * 
	 * @param format - The file format
	 * @return - The format extension
	 */
	public String getFormatExtension(String format)
	{
		return extMap.get(format);
	}
	
	/**
	 * Gets the format code of the specified format
	 * 
	 * @param format - The file format
	 * @return - The format code
	 */
	public int getFormatCode(String format)
	{
		return codeMap.get(format);
	}
}
