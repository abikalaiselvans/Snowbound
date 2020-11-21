/**
 * Copyright (C) 2002-2013 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.vectorpdf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import java.awt.print.PrinterGraphics;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.io.IOException;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import javax.swing.JScrollPane;

import Snow.MessageBox;
import Snow.Snowbnd;

public class VectorPDF extends Frame implements Printable
{
    private static final long serialVersionUID = -2257537940727604263L;
    static VectorPDF mFrame = null;
    ImagePanel mImagePanel = null;
    int mZoom = 0;
    int mZoomOut = 0;
    JScrollPane scroll;
    boolean mZoomedOut = false;
    
    MenuBar mainMenuBar = new MenuBar();

    MenuItem menuItemOpen = new MenuItem("Open...");
    MenuItem menuItemPrint = new MenuItem("Print...");
    MenuItem menuItemExit = new MenuItem("Exit");
    MenuItem menuItemZoomIn = new MenuItem("Zoom In");
    MenuItem menuItemZoomOut = new MenuItem("Zoom Out");
    Menu menuFile = new Menu("File");
    Menu menuEdit = new Menu("Edit");

    private String mFileName = null;
    private String mCurrentDir = null;
    private String mCurrentFile = null;

    private Snowbnd mSnowbound = null;
    
    private boolean isLandscaped = false;

    public VectorPDF()
    {
        /* empty constructor */
    }

    /**************************************************************************
     * Class of panel that displays selected images.
     *************************************************************************/
    class ImagePanel extends Panel
    {
        private static final long serialVersionUID = 5544297606156624866L;

        public ImagePanel()
        {
            super();
            setLayout(new BorderLayout());
            
            mSnowbound = new Snowbnd();
            mSnowbound.alias = 4;
            
            scroll = new JScrollPane(mImagePanel);
            
            /* Turn on Vector PDF decompression */
            mSnowbound.decomp_vect = true;

            /* Snowbound needs to know destination container */
            mSnowbound.setFrame(mFrame);
        }

        public Snowbnd getImage()
        {
            return mSnowbound;
        }

        public void paint(Graphics g)
        {
            if (mSnowbound != null)
            {
            	Dimension dimension = getSize();
                Insets in = getInsets();
              
                dimension.width -= (in.right + in.left);
                dimension.height -= (in.top + in.bottom);

                g.translate(in.left, in.top);
                
                if (mZoom == 0)
                {
                	// Repaint background
                	g.setColor(getBackground());
                	g.fillRect(0, 0, dimension.width, dimension.height);
                }
                
                mSnowbound.IMG_display_bitmap_aspect(g,
                                                     this,
                                                     0,
                                                     0,
                                                     dimension.width,
                                                     dimension.height,
                                                     mZoom);
            }
        }

        /**********************************************************************
         * We don't draw background unless we're uncovering borders.
         *********************************************************************/
        public void update(Graphics aGraphics)
        {
            if (mZoomedOut)
            {
                mZoomedOut = false;

                if (mZoom == 0)
                {
                    Dimension dimension = getSize();
                    aGraphics.setColor(mImagePanel.getBackground());
                    aGraphics.fillRect(0, 0, dimension.width, 
                                       dimension.height);
                }
            }
            paint(aGraphics);
        }
    }

    /***************************************************************************
     * Display image in lower panel.
     **************************************************************************/
    public int decompressImage(String fileName)
    {
        /* Snowbound currently only supports 1 page vector PDFs */
    	/* NOTE: print quality may increase with newer JVMs */
    	
        int nStat = mSnowbound.IMG_decompress_bitmap(fileName, 0);

        try
        {
        	if (nStat < 0)
            {
        		System.out.println("Error decompressing: " + nStat);
                new MessageBox(null, "Error decompressing Image: " + fileName, true);                 
            }
            else 
            {
            	mZoom = 0;
            	updateLayout(mSnowbound.getHeight(), mSnowbound.getWidth());
            	mImagePanel.repaint();
            }
        }
        catch (Throwable ex)
        {
        	ex.getMessage();
        }
        
        return nStat;
    }

    /**************************************************************************
     * Open file in response to menu pick.
     *************************************************************************/
    public void openFile(ActionEvent e) throws IOException
    {
        FileDialog fd = new FileDialog(mFrame, "Open File", FileDialog.LOAD);

        if (mCurrentDir != null)
        {
            fd.setDirectory(mCurrentDir);
        }

        fd.setVisible(true);

        if (fd.getFile() != null)
        {
            mCurrentDir = fd.getDirectory();
            mCurrentFile = fd.getFile();
            mFileName = new String(mCurrentDir + mCurrentFile);
            
            int stat = decompressImage(mFileName);
            
            if( stat >= 0 ) 
            {
                menuItemPrint.setEnabled(true);
                menuItemZoomIn.setEnabled(true);
            }
        }
    }
    
    /**************************************************************************
     * Print the current image in response to menu pick.
     *************************************************************************/
    public void printImage(ActionEvent e)
    {       
        PrintRequestAttributeSet printAttributes = new HashPrintRequestAttributeSet();
        printAttributes.add(PrintQuality.HIGH);
        
        PrinterJob printJob = PrinterJob.getPrinterJob();
        
        if ( printJob.printDialog(printAttributes) )
            try
            {
            	printJob.setPrintable(this);
                printJob.print();
            }
        	catch (PrinterException pe)
            {
                pe.printStackTrace();
            }
    }
    
    /**************************************************************************
     * Perform the actual Printing; required by java.awt.print.Printable
     *************************************************************************/
    
    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
    {
        if (pageIndex > 0) 
        {
            /* Snowbound currently only supports 1 page Vector PDFs */
            return (NO_SUCH_PAGE);
        } 
        else 
        {
            PrinterGraphics p = (PrinterGraphics) g;
            Graphics2D g2d = (Graphics2D) g;
            double pageWidth = pageFormat.getImageableWidth();
            double pageHeight = pageFormat.getImageableHeight();
            int xs = (int) pageFormat.getImageableX();
            int ys = (int) pageFormat.getImageableY();
            
            mSnowbound.IMG_print_bitmap(g, xs, ys, (int)pageWidth, (int)pageHeight, 72);
            
            paint(g2d);
            
            return(PAGE_EXISTS);
        }
    }
    
    /**************************************************************************
     * Update panel layout.
     *************************************************************************/
    
    public void updateLayout(double height, double width) 
    {
    	// Resize Frame to scale to file dimensions
    	if (width > height)
    		this.setSize(800, (int)(850 * (height/width)));
    	else
    		this.setSize((int)(750 * (width/height)), 800);
        
        mImagePanel.setBounds(0,
        					  0,
        					  this.getWidth(),
        					  this.getHeight());
        
        mImagePanel.repaint();   
    }
    
    public void mouseWheel(MouseWheelEvent e)
    {
    	int movedAmount = e.getWheelRotation();
    	if (movedAmount < 0)
    	{
    		--movedAmount;
    	}
    	else if(movedAmount > 0)
    	{
    		movedAmount++;
    	}
    	if (e.getScrollAmount() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
    	{
    		e.getScrollAmount();
    		e.getUnitsToScroll();
    		scroll.getVerticalScrollBar().getUnitIncrement(1);  		
    	}
    	else
    	{
    		scroll.getVerticalScrollBar().getBlockIncrement(1);
    	}
    }

    /**************************************************************************
     * Create menus and listeners.
     *************************************************************************/
    public void createMenus()
    {
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemPrint);
        menuFile.add(menuItemExit);
        menuEdit.add(menuItemZoomIn);
        menuEdit.add(menuItemZoomOut);
        mainMenuBar.add(menuFile);
        mainMenuBar.add(menuEdit);
        
        menuItemOpen.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try {
					openFile(e);
				} catch (IOException ex) {
					System.out.println("Failed to open file" + ex.getMessage());
				}
            }
        });
        menuItemPrint.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e )
            {
                printImage(e);
            }
        });
        menuItemExit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
                System.exit(0);
            }
        });
        menuItemZoomIn.addActionListener(new ActionListener() {
        	public void actionPerformed( ActionEvent e )
        	{
        		Dimension d = mImagePanel.getSize();
        		
        		if (mZoom == 0)
        			mZoom = (int)(mSnowbound.getInitialZoom(d.width, d.height));
        		else
        			mZoom += 20;
        		
                menuItemZoomOut.setEnabled(true);
                mImagePanel.repaint();
        	}
        });
        menuItemZoomOut.addActionListener(new ActionListener() {
        	public void actionPerformed( ActionEvent e )
        	{
        		Dimension d = mImagePanel.getSize();
        		
        		if (mZoom > (int)(mSnowbound.getInitialZoom(d.width, d.height)))
        		{
        			mZoom -= 20;
        			mZoomedOut = true;
        		}
        		else {
        			mZoom = 0;
        			menuItemZoomOut.setEnabled(false);
        		}
        		
                mImagePanel.repaint();
        	}
        });
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        menuItemPrint.setEnabled(false);
        menuItemZoomIn.setEnabled(false);
        menuItemZoomOut.setEnabled(false);
        this.setMenuBar(mainMenuBar);
    }

    /**************************************************************************
     * Panel for drawing images.
     *************************************************************************/
    public void layoutImagePanel()
    {
        mImagePanel.setBounds(0, 
        					  0, 
        					  this.getBounds().width, 
        					  this.getBounds().height);
        mImagePanel.setBackground(Color.GRAY);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.weighty = 100;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.SOUTH;
        
        mFrame.add(mImagePanel);
    }

    /**************************************************************************
     * Triggers all application processing.
     *************************************************************************/
    public void execute()
    {
        mImagePanel = new ImagePanel();

        createMenus();
        layoutImagePanel();
        
        this.setTitle("Snowbound Software :: Vector PDF");
        this.setSize(600, 800);
        this.setVisible(true);
    }

    public static void main(String[] argv)
    {
        mFrame = new VectorPDF();
        mFrame.execute();
    }
}
