package com.snowbound.samples.thumbnails;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;






public class PreviewController {
	private ThumbnailPreview UI;

	public PreviewController(ThumbnailPreview tp) {
		UI = tp;
		UI.open.addActionListener(new menuItemOpenActionListener());
		UI.exit.addActionListener(new menuItemOpenActionListener());
		UI.mInputDirButton.addActionListener(new menuItemOpenActionListener());

	}
	
	class menuItemOpenActionListener
    implements ActionListener
{
    private String openDir = null;
    private String fileName = null;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
    	if (e.getActionCommand().equals("Open") || (e.getActionCommand().equals("Browse"))){
    	FileDialog fd = new FileDialog(UI,"Open Bitmap",FileDialog.LOAD);
        fd.setVisible(true);;
        
		fileName = fd.getDirectory() + fd.getFile();
		
		if (fileName != null)
		{
			UI.changePath(fileName);
			UI.totalPages = UI.Simage.IMGLOW_get_pages(fileName);
            //System.gc();
		}
		
		

    }
    	if (e.getActionCommand().equals("Exit")) {
    		System.exit(0);
    	}
		}   
	}
	
}
