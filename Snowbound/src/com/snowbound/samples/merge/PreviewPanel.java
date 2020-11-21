package com.snowbound.samples.merge;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPanel;

import Snow.ErrorCodes;
import Snow.Snowbnd;

/**
 * A Panel to display a preview of the specified image.
 */
public class PreviewPanel extends JPanel
{
	private static final long serialVersionUID = -1219600281152445777L;
	
	private Snowbnd Simage;
	
	/**
	 * Constructs a PreviewPanel.
	 * 
	 * @param sb - The Snowbnd image object to display
	 */
	public PreviewPanel(Snowbnd sb)
	{
		Simage = sb;
	}
	
	/**
	 * Decompresses the image at the specified file path.
	 * 
	 * @param filePath - The file path of the image to decompress
	 */
	public void decompressImage(String filePath)
	{
		Simage.alias = 4;
		
		Simage.setFrame((JPanel)getParent());
		
		int status = Simage.IMG_decompress_bitmap(filePath, 0);
		
		if (status > -1)
		{
			repaint();
		}
		else
		{
			new Snow.MessageBox(null, "Error decompressing Image", true);
			
			System.out.println(ErrorCodes.getErrorMessage(status));
		}
		
		updateUI();
	}
	
	/**
	 * Displays the image on the panel.
	 * 
	 * @param g - The Graphics object used to draw the background
	 */
	public void paint(Graphics g)
	{
		if (Simage != null)
		{
			Insets in = getInsets();
			
			Dimension dm = getSize();
			
			g.setColor(getBackground());
			g.fillRect(0, 0, dm.width, dm.height);
			
			dm.width -= (in.right + in.left);
			dm.height -= (in.top + in.bottom);
			
			g.translate(in.left, in.top);
			
			Simage.IMG_display_bitmap_aspect(g, this, 0, 0, dm.width,
					                         dm.height, 0);
		}
	}
}
