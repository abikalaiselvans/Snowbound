/**
 * Copyright (C) 2012-2017 by Snowbound Software Corp. All rights reserved.
 */
 
package com.snowbound.samples.common;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;



public class SplashWindow extends JWindow{
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
		if(splashIcon != null){
			return;
		}		
		/*progresPanel = new JPanel();
		getContentPane().add(progresPanel,BorderLayout.SOUTH);
		this.pack();
		progresPanel.setVisible(true);*/
		
	}
	public void showSplash()
	{
		splashImage = Toolkit.getDefaultToolkit().getImage(SplashWindow.class.getResource("/com/resource/splashScreen.png"));
		
		splashIcon = new ImageIcon(splashImage);
		setSize(splashIcon.getIconWidth(), splashIcon.getIconHeight());
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		java.awt.Rectangle frame = getBounds();
		int x_size = (int) (screenDimension.width - frame.getWidth() - 490);
		int y_size =  (int) (screenDimension.height - frame.getHeight() - 380);
		setLocation(x_size,y_size);
		setVisible(true);
	}
	public void paint(Graphics g)
	{
		super.paint(g);
		g.drawImage(splashImage,0,0,this);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              
	}
	public void showProgressBar(int counter)
	{
		
		if(progress != null)
		{
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
