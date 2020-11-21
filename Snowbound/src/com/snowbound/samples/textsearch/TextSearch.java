/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.textsearch;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import Snow.IMG_RECT;
import Snow.MessageBox;
import Snow.SANN_RECT;
import Snow.SNBD_SEARCH_RESULT;
import Snow.SnowAnn;
import Snow.Snowbnd;

public class TextSearch extends Frame
{
    private static final long serialVersionUID = -2257537940727604263L;
    static TextSearch mFrame = null;
    Panel mMainPanel = null;
    ImagePanel mImagePanel = null;
    Panel mSearchPanel = null;
    int mZoom = 1;
    int mPage = 0;
    boolean mZoomedOut = false;
    Button mZoomIn = null;
    Button mZoomOut = null;

    private String mFileName = null;
    private String mCurrentDir = null;
    private String mCurrentFile = null;

    private SNBD_SEARCH_RESULT[] mSearchResults = null;
    public TextField mTextField = null;
    private Snowbnd mSnowbound = null;
    private SnowAnn annLayer;

    public TextSearch()
    {
        annLayer = new SnowAnn(0, 0);
    }

    public void searchAndDisplay()
    {
        int[] length = new int[1];
        int[] error = new int[1];
        int[] errorA = new int[1];
        length[0] = 0;
        error[0] = 0;
        errorA[0] = 0;

        try
        {
            byte extractedText[] = mSnowbound.IMGLOW_extract_text(mFileName,
                                                                  length,
                                                                  error,
                                                                  mPage);

            mSearchResults = mSnowbound
                .IMGLOW_search_text(extractedText,
                                    mTextField.getText(),
                                    0,
                                    errorA);

            createHighlightRects(mImagePanel.getGraphics());
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    /***************************************************************************
     * Show hits by drawing rectangles.
     **************************************************************************/
    public void createHighlightRects(Graphics aGraphics)
    {
        annLayer.SANN_set_fcolor(Color.yellow.getRed(),
                                 Color.yellow.getGreen(),
                                 Color.yellow.getBlue());
       
        if (mSearchResults != null)
        {
            for (int nIx = 0; nIx < mSearchResults.length; nIx++)
            {
                IMG_RECT[] recta = mSearchResults[nIx].rc;

                /***************************************************************
                 * Draw all rects required to cover hit.
                 **************************************************************/
                for (int r = 0; r < recta.length; r++)
                {
                    IMG_RECT imgRect = recta[r];
                    SANN_RECT sannRect = new SANN_RECT();

                    sannRect.top = imgRect.top;
                    sannRect.bottom = imgRect.bottom;
                    sannRect.left = imgRect.left;
                    sannRect.right = imgRect.right;
                    annLayer.SANN_add_object(SnowAnn.SANN_HIGHLIGHT_RECT,
                                             sannRect,
                                             null,
                                             null,
                                             0);
                }
            }

            paintAnnotations(aGraphics);
        }
    }

    /**
     * @param aGraphics
     */
    private void paintAnnotations(Graphics aGraphics)
    {
        annLayer.ann_width = mSnowbound.getWidth();
        annLayer.ann_height = mSnowbound.getHeight();
        annLayer.SANN_set_croprect(mSnowbound.dis_crop_xs,
                                   mSnowbound.dis_crop_ys,
                                   mSnowbound.dis_crop_xe,
                                   mSnowbound.dis_crop_ye);

        annLayer.SANN_display_annotations(aGraphics,
                                          mImagePanel,
                                          0,
                                          0,
                                          mImagePanel.getWidth(),
                                          mImagePanel.getHeight());
    }

    /***************************************************************************
     * Class of panel that displays selected images.
     **************************************************************************/
    class ImagePanel extends Panel
    {
        private static final long serialVersionUID = 5544297606156624866L;

        public ImagePanel()
        {
            super();
            setLayout(new BorderLayout());
            
            mSnowbound = new Snowbnd();
            mSnowbound.alias = 4;

            // Snowbound needs to know destination container
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
                Insets in = getInsets();
                Dimension dimension = getSize();

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
                paintAnnotations(g);
            }
        }

        /***********************************************************************
         * We don't draw background unless we're uncovering borders.
         **********************************************************************/
        public void update(Graphics aGraphics)
        {
            if (mZoomedOut)
            {
                mZoomedOut = false;

                if (mZoom == 1)
                {
                    Dimension dimension = getSize();
                    aGraphics.setColor(mImagePanel.getBackground());
                    aGraphics.fillRect(0, 0, dimension.width, dimension.height);
                }
            }
            paint(aGraphics);
        }
    }

    /***************************************************************************
     * Display image in lower panel.
     **************************************************************************/
    public int decompressImage(String fileName, int page)throws Throwable
    {
        int nStat = mSnowbound.IMG_decompress_bitmap(fileName, page);

        try
        {
        	if (nStat < 0)
            {
                new MessageBox(null, "Error decompressing Image", true);
                System.out.println(" Caught error in get pages for file ".toString() +
                                   mCurrentFile + "': " + nStat + " not allowed "
                                   .toUpperCase() + "\n");
            }
        	else{
        		System.out.println("OK");
        		
        	}
        	mImagePanel.repaint();
        }
        catch(Exception ex)
        {
        	System.out.println("jar file required for file type".toString());
        }
        
        return nStat;
    }

    /***************************************************************************
     * Open file in response to menu pick.
     * @throws Throwable 
     **************************************************************************/
    public void openFile(ActionEvent e) throws Throwable
    {
        FileDialog fd = new FileDialog(mFrame, "Open File", FileDialog.LOAD);

        if (mCurrentDir != null)
        {
            fd.setDirectory(mCurrentDir);
        }

        fd.setVisible(true);            
        if (fd.getFile() != null)        	
        {
        	try
        	{
        		 mCurrentDir = fd.getDirectory();
                 mCurrentFile = fd.getFile();
                 mFileName = new String(mCurrentDir + mCurrentFile);
                 decompressImage(mFileName,mPage);
                 mSearchResults = null;
                 mZoom = 0;
        	}
        	catch(Exception ex)
        	{
        		System.out.println("Caught error in File" + ex.getMessage());;
        	}
        }
    }

    /***************************************************************************
     * Display next page in response to menu action event.
     **************************************************************************/
    public void nextPage() {
    	mPage += 1;
    	try
    	{
			this.decompressImage(mFileName, mPage);
		}
    	catch (Throwable ex)
		{
			System.out.println("Caught error in nextPage: " + ex.getMessage());
		}
    }

    /***************************************************************************
     * Display next page in response to menu action event.
     **************************************************************************/
    public void previousPage() {
    	mPage -= 1;
    	try
    	{
			this.decompressImage(mFileName, mPage);
		}
    	catch (Throwable ex)
    	{
			System.out.println("Caught error in previousPage: " + ex.getMessage());
		}
    }

    /***************************************************************************
     * Create menus and listeners.
     **************************************************************************/
    public void createMenus()throws Exception
    {
        MenuBar mainMenuBar = new MenuBar();

        MenuItem menuItemOpen = new MenuItem("Open");
        MenuItem menuItemExit = new MenuItem("Exit");
        MenuItem menuItemNextPage = new MenuItem("Next Page");
        MenuItem menuItemPreviousPage = new MenuItem("Previous Page");
        Menu menuFile = new Menu("File");
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemNextPage);
        menuFile.add(menuItemPreviousPage);
        menuFile.add(menuItemExit);
        mainMenuBar.add(menuFile);

        menuItemOpen.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	try{
            		openFile(e);    		
            	}
            	catch(Throwable ex)
            	{
            		System.out.println("Caught error in file IO" + ex.getMessage());
            	}
                
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

        menuItemNextPage.addActionListener(new ActionListener()
        {
        	public void actionPerformed( ActionEvent e )
        	{
        		nextPage();
        	}
        });

        menuItemPreviousPage.addActionListener(new ActionListener()
        {
        	public void actionPerformed( ActionEvent e )
        	{
        		previousPage();
        	}
        });
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        this.setMenuBar(mainMenuBar);
    }

    /***************************************************************************
     * Parent panel.
     **************************************************************************/
    public void layoutMainPanel()
    {
        mMainPanel = new Panel();
        mMainPanel.setLayout(new GridBagLayout());
        mMainPanel.setBounds(0,
                             0,
                             this.getBounds().width,
                             this.getBounds().height);
        mFrame.add(mMainPanel);
    }

    /***************************************************************************
     * Panel for search and zoom.
     **************************************************************************/
    public void layoutSearchPanel()
    {
        mSearchPanel = new Panel();
        int nSearchHeight = 32;
        mSearchPanel.setBounds(0, 1, this.getWidth(), nSearchHeight);
        mSearchPanel.setBackground(SystemColor.menu);

        mSearchPanel.setLayout(null);
        Label pLabel = new Label("Find What:");
        pLabel.setBounds(5, 5, 60, 20);
        mSearchPanel.add(pLabel);

        mTextField = new TextField("", 50);
        mTextField.setBounds(pLabel.getX() + pLabel.getWidth() + 10,
                             pLabel.getY(), 200, 23);
        mSearchPanel.add(mTextField);

        /***********************************************************************
         * Update hilites as user types.
         **********************************************************************/
        mTextField.addKeyListener(new KeyListener()
        {
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    annLayer.SANN_delete_all_objects();
                    searchAndDisplay();
                    mImagePanel.repaint();
                }
            }

            public void keyPressed(KeyEvent e)
            {
            	/*if(e.getKeyCode() == KeyEvent.VK_ENTER)
            	{
            		searchAndDisplay();
            		mImagePanel.repaint();
            	}*/
            }

            public void keyTyped(KeyEvent e)
            {
            }
        });

        mZoomIn = new Button("Zoom In");
        mZoomIn.setBounds(mTextField.getX() + mTextField.getWidth() + 10,
                          mTextField.getY(), 75, 23);
        
        mZoomOut = new Button("Zoom Out");
        mZoomOut.setBounds(mZoomIn.getX() + mZoomIn.getWidth() + 10,
                           mZoomIn.getY(), 75, 23);
        
        mSearchPanel.add(mZoomIn);
        mSearchPanel.add(mZoomOut);

        mZoomIn.setEnabled(true);
        mZoomIn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
        		Dimension d = mImagePanel.getSize();
        		
        		if (mZoom == 0)
        			mZoom = (int)(mSnowbound.getInitialZoom(d.width, d.height));
        		else
        			mZoom += 20;
        		
                mZoomOut.setEnabled(true);
                mImagePanel.repaint();
            }
        });
        mZoomOut.setEnabled(false);
        mZoomOut.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
        		Dimension d = mImagePanel.getSize();

        		if (mZoom > (int)(mSnowbound.getInitialZoom(d.width, d.height)))
        		{
        			mZoom -= 20;
        			mZoomedOut = true;
        		}
        		else {
        			mZoom = 0;
        			mZoomOut.setEnabled(false);
        		}
        		mImagePanel.repaint();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weighty = 0;
        c.anchor = GridBagConstraints.NORTH;

        mMainPanel.add(mSearchPanel, c);
    }

    /***************************************************************************
     * Panel for drawing PDFs.
     **************************************************************************/
    public void layoutImagePanel()
    {
        int nSearchHeight = 32;
        mImagePanel.setBounds(0, nSearchHeight, this.getBounds().width,
                              this.getBounds().height - nSearchHeight);
        mImagePanel.setBackground(Color.GRAY);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.weighty = 100;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.SOUTH;

        mMainPanel.add(mImagePanel, c);
    }

    /***************************************************************************
     * Layout all panels.
     **************************************************************************/
    public void layoutPanels()
    {
        layoutMainPanel();
        layoutSearchPanel();
        layoutImagePanel();
    }

    /***************************************************************************
     * Triggers all application processing.
     * @throws Throwable 
     **************************************************************************/
    public void execute() throws Throwable
    {
        mImagePanel = new ImagePanel();

        this.setTitle("Snowbound Software :: Text Search");
        this.setSize(612, 792);
        createMenus();
        layoutPanels();
        
        this.setVisible(true);
    }

    public static void main(String[] argv) throws Throwable
    {
        mFrame = new TextSearch();
        mFrame.execute();
    }
}
