/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.PrintJob;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.EventListener;
import java.util.Properties;

import Snow.IMG_RECT;
import Snow.MessageBox;
import Snow.SANN_RECT;
import Snow.SNBD_SEARCH_RESULT;
import Snow.SnowAnn;
import Snow.Snowbnd;

/**
 * This class...
 */
public class SnowboundPanelWithAnnotations extends AbstractImagePanel
{
    private static final long serialVersionUID = -7247303745068964316L;
    private static final String DecompressErrorMessage = new String("Error decompressing Image");
    private int zoom , pageNumber = 0;
    private Snowbnd Simage = null;
    private Snowbnd CopySimage = null;
    private Insets in = null;
    private Dimension dimension = null;
    private int stat = 0;
    private BorderLayout bl = new BorderLayout();
    private RubberBandListener mRubberBandListener;
    private WatermarkListener mWatermarkListener;
    private DefaultListener mDefaultListener;
    private MagListener mMagListener;
    private MagPanel magPanel;
    private Image offscreenImage;
    private boolean backImageActive = false;
    private boolean magActive = false;
    private boolean zoomRectangle = false;
    private static final int MIN_TEST_ANGLE = -15;
    private static final int MAX_TEST_ANGLE = 15;
    private String imageFile;
    protected MagPanel magpanel = null;
    public Image magImage = null;
    private Dimension d = null;
    private Rectangle selectionRectangle = null;
    private SNBD_SEARCH_RESULT[] mSearchResults = null;
    private SnowAnn annLayer;
    private String watermarkImagePath = null;
    private int watermarkMergeFlag = -1;
    
    private snbdMouseAdapter mouseAdapter = new snbdMouseAdapter();
    private snbdMouseMotionAdapter mouseMotionAdapter = new snbdMouseMotionAdapter();

    /**
     * Accessor for the magPanel member
     * 
     * @return magPanel member
     */
    public MagPanel getMagPanel()
    {
        return magPanel;
    }

    /**
     * Toggle the magnified pane
     */
    public void toggleMagnifier()
    {
        if (magActive)
        {
            removeMagnifier();
        }
        else
        {
            enableMagnifier();
        }
    }

    /**
     * Remove the magnifier from the panel
     */
    private void removeMagnifier()
    {
        magActive = false;
        mMagListener.done();
        activateDefaultListener();
    }
    
    /**
     * Add the magnifier to the panel
     */
    private void enableMagnifier()
    {
        magActive = true;
        repaint();
        createBackImage();
        activateMagListener();
    }

    /**
     * Creates an offscreen image for use in back image rendering.
     */
    public void createBackImage()
    {
        offscreenImage = createImage(getSize().width, getSize().height);
    }
    
    /**
     * Enable/disable back image rendering
     * 
     * @param tf whether to render the back image
     */
    public void setBackImageActive(boolean tf)
    {
        if (tf)
        {
            backImageActive = true;
        }
        else
        {
            backImageActive = false;

            if (offscreenImage != null)
            {
                offscreenImage.flush();
            }

            offscreenImage = null;
        }
    }

    /**
     * Draws the offscreen image.
     */
    public void drawBackImage()
    {
        Graphics g = offscreenImage.getGraphics();

        getImage().IMG_display_bitmap_aspect(g,
                                             this,
                                             0,
                                             0,
                                             dimension.width,
                                             dimension.height,
                                             zoom);

        g.dispose();
    }

    /**
     * Save the image to the specified filename
     * 
     * @param fileName The name of the destination file
     * @param fileType The file type of the destination file
     */
    public void saveImage(String fileName, int fileType)
    {
    	try
    	{
    		Simage.IMG_save_bitmap(fileName, fileType);
    	}
    	catch(Exception ex)
    	{
    		System.out.println("Error saving Image" + ex.getMessage());
    	}
        
    }

    /**
     * Accessor of the image
     * 
     * @return The image
     */
    public Snowbnd getImage()
    {
        return Simage;
    }
    
    /**
     * Returns the zoom ratio of the displayed image.
     * 
     * @return the zoom ratio of the displayed image or 0 if no image.
     */
    public double getZoomRatio()
    {
        if (getImage() == null)
        {
            return 0;
        }

        double zoomRatio;

        try
        {
            Point base = new Point(0, 0);
            getImage().map_image_to_wnd(this, base);

            if (getImage().vsb == null && base.y > 0)
            {
                zoomRatio = ((double) this.getSize().width)
                    / (double) (getImage().getWidth());
            }
            else if (getImage().hsb == null && base.x > 0)
            {
                zoomRatio = ((double) this.getSize().height / (double) getImage()
                    .getHeight());
            }
            else
            {
                zoomRatio = (zoom / 100.0);
            }
        }
        catch (Exception e)
        {
            zoomRatio = 0.0;
        }

        return zoomRatio;
    }
    
    public int nextPage() {
    	pageNumber += 1;
    	return decompressImage(imageFile);
    }
    
    public int previousPage() {
    	if( pageNumber > 0 )
    		pageNumber -= 1;
    	return decompressImage(imageFile);
    }
    
    public void searchAndDisplay(String searchString)
    {
        int[] length = new int[1];
        int[] error = new int[1];
        int[] errorA = new int[1];
        length[0] = 0;
        error[0] = 0;
        errorA[0] = 0;

        try
        {
            byte extractedText[] = Simage.IMGLOW_extract_text(imageFile,
                                                                  length,
                                                                  error,
                                                                  0);

            mSearchResults = Simage.IMGLOW_search_text(extractedText,
                                    searchString,
                                    0,
                                    errorA);

            createHighlightRects(this.getGraphics());
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

        if (mSearchResults != null && String.valueOf(mSearchResults) != null)
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
    
    private void paintAnnotations(Graphics aGraphics)
    {
        annLayer.ann_width = Simage.getWidth();
        annLayer.ann_height = Simage.getHeight();
        annLayer.SANN_set_croprect(Simage.dis_crop_xs,
                                   Simage.dis_crop_ys,
                                   Simage.dis_crop_xe,
                                   Simage.dis_crop_ye);

        annLayer.SANN_display_annotations(aGraphics,
                                          this,
                                          0,
                                          0,
                                          this.getWidth(),
                                          this.getHeight());
    }
    
    /**
     * Open and decompress the specified byte[] array
     * 
     * @param fileBytes A byte[] array image representation
     * 
     * @return status
     */
    
    public int decompressImage( byte[] fileBytes ) 
    {
    	DataInputStream di = new DataInputStream(new ByteArrayInputStream( fileBytes ));
    	stat = Simage.IMG_decompress_bitmap(di,0);
    	try
    	{
    		if (stat < 0)
            {
                new MessageBox(null, DecompressErrorMessage, true);
                System.out.println("Error decompressing byte image" + ErrorCodes.getErrorMessage(stat));
            }
    		else
    		{
    			System.out.println("Decompressing complete");
    		}

            repaint();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
        

        return stat;
    }

    /**
     * Open and decompress the specified image
     * 
     * @param fileName The name of the image to open
     * 
     * @return status
     */
    public int decompressImage(String fileName)
    {
        imageFile = fileName;
    	stat = Simage.IMG_decompress_bitmap(fileName, pageNumber);

    	try
    	{
    		if (stat < 0)
            {
                // Snowbound's message box routine
                new MessageBox(null, DecompressErrorMessage, true);
                System.out.println("Error decompressing specified file" + ErrorCodes.getErrorMessage(stat));
            }
    		else
    		{
    			System.out.println("Decompression complete");
    		}

            repaint();
    	}
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }

        return stat;
    }

    /**
     * Enable the listeners for the rubber band
     */
    public void activateRubberBandListener()
    {
        removeMouseListeners();
        addMouseMotionListener(mRubberBandListener);
        addMouseListener(mRubberBandListener);
    }
    
    public void activateWatermarkListener()
    {
    	removeMouseListeners();
    	addMouseMotionListener(mWatermarkListener);
    	addMouseListener(mWatermarkListener);
    }

    /**
     * Enable the default listeners
     */
    public void activateDefaultListener()
    {
        removeMouseListeners();
        addMouseMotionListener(mDefaultListener);
        addMouseListener(mDefaultListener);

    }

    /**
     * Activate the listeners for the magnifier pane
     */
    public void activateMagListener()
    {
        removeMouseListeners();
        addMouseMotionListener(mMagListener);
        addMouseListener(mMagListener);
        mMagListener.update();
        mMagListener.start();
    }

    /**
     * Remove all mouse listeners
     * 
     * Avoid using getMouseListeners() and getMouseMotionListeners() here, 
     * as these methods were added in Java 1.4 and we are still supporting 
     * earlier JVMs.
     */
    private void removeMouseListeners()
    {
        EventListener[] list = getListeners(MouseListener.class);

        for (int i = 0; i < list.length; i++)
        {
            removeMouseListener((MouseListener) list[i]);
        }

        list = getListeners(MouseMotionListener.class);

        for (int i = 0; i < list.length; i++)
        {
            removeMouseMotionListener((MouseMotionListener) list[i]);
        }
    }

    /**
     * @see java.awt.Container#paint(java.awt.Graphics)
     */
    public void paint(Graphics g)
    {
        Graphics offgc = null;
        Image offscreen = null;
        Rectangle box = g.getClipBounds();
        Dimension dimension = getSize();
        
        if (zoom == 0)
        {
        	/* draw a white background */
        	g.setColor(this.getBackground());
            g.fillRect(0, 0, dimension.width, dimension.height);
        }
        

        if (backImageActive)
        {
            // try/catch for IE bug
            try
            {
                offscreen = createImage(box.width, box.height);
                offgc = offscreen.getGraphics();
            }
            catch (Exception e)
            {
            	System.out.println("Error creating image" + e.getMessage());
            }
            catch (Error e)
            {
            }

            if (offscreen != null && offgc != null)
            {
                offgc.setColor(getBackground());
                offgc.fillRect(0, 0, box.width, box.height);
                offgc.setColor(getForeground());
                offgc.translate(-box.x, -box.y);

                if (backImageActive)
                {
                    offgc.drawImage(offscreenImage, 0, 0, null);
                    super.paint(offgc);
                    g.drawImage(offscreen, box.x, box.y, this);
                    return;
                }

                internalPaint(offgc);
                g.drawImage(offscreen, box.x, box.y, this);

                // transfer offscreen to window
                offgc.dispose();
                return;
            }
        }
        else
        {
            internalPaint(g);
        }
    }

    /**
     * Internal paint routine
     * 
     * @param g Graphics object
     */
    private void internalPaint(Graphics g)
    {
        if (Simage != null)
        {
            setDimensionsWidthHeight();
            g.translate(in.left, in.top);
            stat = Simage.IMG_display_bitmap_aspect(g,
                                                    this,
                                                    0,
                                                    0,
                                                    dimension.width,
                                                    dimension.height,
                                                    zoom);
        }
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    /**
     * Zoom in
     * 
     * @param inc 0-display whole image, 100 is a one-to-one pixel display
     * 
     * @return status
     */
    public int zoomIn(int inc)
    {
    	Dimension d = dimension;
    	
        if (zoom == 0)
        {
            zoom = (int)(Simage.getInitialZoom(d.width, d.height));
        }
        else
        {
            zoom += inc;
        }
        
        //System.out.println("zoomIn(): " + zoom);

        //repaint();
        return 0;
    }

    /**
     * Zoom out
     * 
     * @param inc 0-display whole image, 100 is a one-to-one pixel display
     * 
     * @return status
     */
    public int zoomOut(int inc)
    {
        Dimension d = dimension;

        if (zoom > (int)(Simage.getInitialZoom(d.width, d.height)))
        {
            zoom -= inc;
        }
        else
        {
            zoom = 0;
        }
        
        //System.out.println("zoomOut(): " + zoom);

        //repaint();
        return 0;
    }

    /**
     * Reduces the with and height of the component by the insets.
     */
    private void setDimensionsWidthHeight()
    {
        dimension = getSize();
        in = getInsets();
        dimension.width -= (in.right + in.left);
        dimension.height -= (in.top + in.bottom);
    }

    /**
     * Zoom the image to the area constrained by selrect
     * 
     * @param selrect the selected area to zoom into
     */
    public void zoomRubberBand(Rectangle selrect)
    {
        Point upperLeft = new Point();
        Point lowerRight = new Point();
        upperLeft.x = selrect.x;
        upperLeft.y = selrect.y;

        // adjust the screen coordinates to image coordinates
        Simage.map_wnd_to_image(this, upperLeft);
        lowerRight.x = selrect.x + selrect.width;
        lowerRight.y = selrect.y + selrect.height;

        // adjust screen coordinates to image coordinates
        Simage.map_wnd_to_image(this, lowerRight);

        // adjust
        if (upperLeft.x > lowerRight.x)
        {
            selrect.x = upperLeft.x;
            upperLeft.x = lowerRight.x;
            lowerRight.x = selrect.x;
        }
        if (upperLeft.y > lowerRight.y)
        {
            selrect.y = upperLeft.y;
            upperLeft.y = lowerRight.y;
            lowerRight.y = selrect.y;
        }

        zoom = Simage.set_croprect_scroll(this,
                                          upperLeft.x,
                                          upperLeft.y,
                                          lowerRight.x - upperLeft.x,
                                          lowerRight.y - upperLeft.y,
                                          1);

        if (zoom < 0)
        {
            zoom = 0;
        }

        repaint();
        activateDefaultListener();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public void rotateImage(int angle)
    {
        Simage.IMG_rotate_bitmap(angle);
    }

    public void flipX()
    {
        Simage.IMG_flip_bitmapx();
    }

    public void flipY()
    {
        Simage.IMG_flip_bitmapy();
    }
    
    public void invert()
    {
        Simage.IMG_invert_bitmap();
    }

    public int getBitsPerPixel()
    {
        return Simage.getBitsPerPixel();
    }

    public void setContrast(int contrast)
    {
        Simage.IMGLOW_set_contrast(contrast);
    }

    public void setBrightness(int bright)
    {
        Simage.IMGLOW_set_brightness(bright);
    }
    
    public void colorGray()
    {
    	int bits = Simage.getBitsPerPixel();
    	
    	stat = Simage.IMG_color_gray();
    }

    public void deskewImage()
    {
        int rotate[] = new int[1];
        int bits = Simage.getBitsPerPixel();

        try
        {
        	if (bits == 1)
            {
                stat = Simage.IMG_get_deskew_angle(rotate,
                                                   MIN_TEST_ANGLE,
                                                   MAX_TEST_ANGLE);
                
                if (stat >= 0 && rotate[0] != 0)
                {
                    Simage.IMG_deskew_bitmap(rotate[0]);
                    repaint();
                }
            }
        }
        catch(Exception ex)
        {
        	System.out.println("Error in deskewImage manipulation" + ex.getMessage());
        }
        
    }
    
    public void setDespeckleQuality(int despeckle)
    {
        int bits = Simage.getBitsPerPixel();

        if (bits == 1)
        {
            Simage.IMG_despeckel_bitmap(despeckle);
        }
    }

    public void setJPEGparameters(int quality,
                                  int horizontalInterleave,
                                  int verticalInterleave)
    {
        Simage.IMGLOW_set_comp_quality(quality);
        Simage.IMGLOW_set_jpg_interleave(horizontalInterleave,
                                         verticalInterleave);
    }
    
    public void printPSPCL(int type, String destination)
    {
        String tempFile;
        tempFile = imageFile + "temp";
        saveImage(tempFile, type);

        // This portion of the code is not portable
        // This is windows specific code, notice that we need to launch
        // the command interpeter in order to copy the file, rt.exec is made
        // to run executable programs but no copy.exe exists on your machine
        // if you are running windows, this is a common pitfall and avoided
        // but running our commands through the interpeter.
        try
        {
            Runtime rt = Runtime.getRuntime();
            rt.exec("cmd.exe /C copy " + tempFile + " " + destination);
            rt.exec("cmd.exe /C del " + tempFile);
        }
        catch (IOException IOE)
        {
            System.out.println(IOE);
            IOE.printStackTrace();
        }
    }
    
    public void printImage(com.snowbound.samples.printing.PrintingUI ui, int dpi)
    {
        int width, height, x, y;
        int imageHeight, imageWidth;
        PrintJob printJob;
        Properties props = new Properties();
        Graphics printGraphics;

        printJob = getToolkit().getPrintJob(ui, "Snowbound Sample", props);

        if (printJob != null && Simage != null)
        {
            printGraphics = printJob.getGraphics();
            if (printGraphics != null)
            {
                Dimension pd = printJob.getPageDimension();

                if (Simage.dis_rotate == 90 || Simage.dis_rotate == 270)
                {
                    imageWidth = Simage.getHeight();
                    imageHeight = Simage.getWidth();
                }
                else
                {
                    imageWidth = Simage.getWidth();
                    imageHeight = Simage.getHeight();
                }

                width = (pd.width / 72) * dpi;
                height = (imageHeight * width) / imageWidth;

                if (height > ((pd.height / 72) * dpi))
                {
                    x = (((pd.width / 72) * dpi) - width) / 2;
                    y = 0;
                }
                else
                {
                    x = 0;
                    y = (((pd.height / 72) * dpi) - height) / 2;
                }

                Simage
                    .IMG_print_bitmap(printGraphics, x, y, width, height, dpi);

                printGraphics.dispose();
            }
            printJob.end();
        }
        return;
    }
    /**
     * Accessor for zoomRectangle member, which indicates whether or not the
     * "Zoom Rectangle" is active.
     * 
     * @return whether Zoom Rectangle is active
     */
    
    public boolean getZoomRect()
    {
        return zoomRectangle;
    }

    /**
     * Mutator for zoomRectangle member, which indicates whether or not the
     * "Zoom Rectangle" is active.
     * 
     * @param yn new value for zoomRectangle
     */
    public void setZoomRect(boolean yn)
    {
        zoomRectangle = yn;
    }
    
    /**
     * Accessor for drawMag member which indicates whether the "Magnifier Box"
     * is active
     * 
     * @param yn the new value for drawMag
     */
    public boolean getDrawMag()
    {
        return magActive;
    }
    /**
     * Mutator for drawMag member which indicates whether the "Magnifier Box" is
     * active
     * 
     * @param yn the new value for drawMag
     */
    public void setDrawMag(boolean yn)
    {
        magActive = yn;
    }
    
    public void disableFitWidthHeight()
    {
        Simage.fit_to_height(0);
        Simage.fit_to_width(0);
    }

    public void fitToWidth()
    {
        zoom = 0;
        Simage.fit_to_width(1);
        repaint();
    }

    public void fitToHeight()
    {
        zoom = 0;
        Simage.fit_to_height(1);
        repaint();
    }
    
    public void toggleMagnifyRectangle()
    {
        // If an image is opened and no magnifying panel already exists
        if (Simage != null && (getDrawMag() == false))
        {
            d = getSize();
            magImage = createImage(d.width, d.height);
            Graphics g = magImage.getGraphics();

            Simage.IMG_display_bitmap_aspect(g,
                                             this,
                                             0,
                                             0,
                                             d.width,
                                             d.height,
                                             zoom);

            g.dispose();
            magpanel = new MagPanel(100, Simage, this);

            magpanel.addMouseMotionListener(mouseMotionAdapter);

            add(magpanel);

            // repaint method called to correct issue of area under menu not
            // getting repainted when magpanel is added to the screen.
            magpanel.repaint();

            setDrawMag(true);
        }
        // We have turned off the Magnification Box
        else
        {
            magpanel.removeMouseMotionListener(mouseMotionAdapter);

            magImage = null;

            remove(magpanel);
            magpanel = null;

            setDrawMag(false);
        }
    }
    
    public void find() {
    	
    }
    
    /**
     * Draw the zoom rectangle
     */
    private void snbd_draw_pen()
    {
        Graphics g;
        int xsize, ysize;

        if (getZoomRect() == true)
        {
            setDimensionsWidthHeight();
            g = getGraphics();
            g.setXORMode(Color.white);

            xsize = selectionRectangle.width;
            ysize = (d.height * xsize) / d.width;

            if (ysize > d.height)
            {
                ysize = selectionRectangle.height;
                xsize = (d.width * ysize) / d.height;
            }

            g
                .drawRect(selectionRectangle.x,
                          selectionRectangle.y,
                          xsize,
                          ysize);
            g.setPaintMode();
        }
    }
    
    public void setWatermarkImagePath( String imagePath ) {
    	watermarkImagePath = imagePath;
    }
    
    public String getWatermarkImagePath() {
    	return watermarkImagePath;
    }
    
    public void setWatermarkMergeFlag( int mergeFlag ) {
    	watermarkMergeFlag = mergeFlag;
    }
    
    public int getWatermarkMergeFlag() {
    	return watermarkMergeFlag;
    }
    
    public int watermarkMerge( Rectangle rect ) 
    {
    	Snowbnd watermarkImage = new Snowbnd();
    	int status = -1;
    	int imageBitDepth = 0, watermarkBitDepth = 0;
    	
        Point upperLeft = new Point();
        Point lowerRight = new Point();
        upperLeft.x = rect.x;
        upperLeft.y = rect.y;

        // adjust the screen coordinates to image coordinates
        Simage.map_wnd_to_image(this, upperLeft);
        lowerRight.x = rect.x + rect.width;
        lowerRight.y = rect.y + rect.height;

        // adjust screen coordinates to image coordinates
        Simage.map_wnd_to_image(this, lowerRight);

        // adjust
        if (upperLeft.x > lowerRight.x)
        {
            rect.x = upperLeft.x;
            upperLeft.x = lowerRight.x;
            lowerRight.x = rect.x;
        }
        if (upperLeft.y > lowerRight.y)
        {
            rect.y = upperLeft.y;
            upperLeft.y = lowerRight.y;
           lowerRight.y = rect.y;
        }

    	if( watermarkImagePath != null ) {
    		status = watermarkImage.IMG_decompress_bitmap(watermarkImagePath, 0);
    	}
    	
    	if( status > -1 ) {
    		//resize watermark to fix box drawn.
    		//this will mess up aspect ratio so capture on point
    		//instead of box.
                        
            status = watermarkImage.IMG_resize_bitmap(lowerRight.x - upperLeft.x, 
            		lowerRight.y - upperLeft.y);
    		
    		
    		imageBitDepth = Simage.getBitsPerPixel();
    		watermarkBitDepth = watermarkImage.getBitsPerPixel();
    	}
    	
		if ( imageBitDepth < 24 )
		{
			Simage.IMG_promote_24();
		}

		if ( watermarkBitDepth < 24 )
		{
			watermarkImage.IMG_promote_24();
		}
		
		status = Simage.IMG_merge_block( watermarkImage, upperLeft.x, upperLeft.y, watermarkMergeFlag );
		
		repaint();
    	
    	return status;
    }
    
    public int waterMarkMerge(int options)
    {
    	try
    	{
    		//Prevent running if no was decompressed
    		if (Simage == null)
    			return ( -40);

    		//Get original image
    		Simage = CopySimage.IMG_get_bitmap_block(0, 0, CopySimage.getWidth(),
                                                CopySimage.getHeight());

    		//Get Temporary Snowbound Object
       Snowbnd wmtmp = new Snowbnd();

       if (Simage.getBitsPerPixel() != 1) {
         //Decompress 24 bpp WaterMark Image
         stat = wmtmp.IMG_decompress_bitmap("WaterMarkImage.jpg", 0);
         if (stat < 0) {
           //Error Msg
           File ftmp = new File("WaterMarkImage.jpg");
           if (ftmp.exists())
           {
             System.err.println("Error in Decomp WaterMark: Stat =" + stat + ErrorCodes.getErrorMessage(stat));
             return ( -40);
           }
           else
           {
             System.err.println("Can not find WaterMarkImage.jpg: Please place image in the Main Class Directory! (i.e WaterMarkApp.class)");
             return ( -40);
           }
         }
         ///Resize WaterMark Image to size of the Open Image
         //There are other methods to resize images --
         stat = wmtmp.IMG_resize_bitmap(Simage.getWidth(), Simage.getHeight());
         if (stat < 0) {
           //Error Msg
           System.err.println("Error in resizing WaterMark: Stat =" + stat + ErrorCodes.getErrorMessage(stat));
           return ( -40);
         }
         //Promote WaterMark Image to 24 bpp - Both Images must have the same BitDepth
         if (wmtmp.getBitsPerPixel() != 24) {
           stat = wmtmp.IMG_promote_24();
           if (stat < 0) {
             //Error Msg
             System.err.println("Error in Promoting WaterMark to 24 bpp: Stat =" +
                                stat + ErrorCodes.getErrorMessage(stat));
             return ( -40);
           }
         }
         //Promote open image to 24 bpp to match WaterMark Image - Both Images must have the same BitDepth
         if (Simage.getBitsPerPixel() != 24) {
           stat = Simage.IMG_promote_24();
           if (stat < 0) {
             //Error Msg
             System.err.println(
                 "Error in Promoting Open Image to 24 bpp: Stat =" + stat + ErrorCodes.getErrorMessage(stat));
             return ( -40);
           }
         }
         //Set WaterMark
         //Set proper size of byte arrays so as to not generate a -38 error if the byte array is smaller than the internal one.
         int tmplen = 0;
         tmplen = (wmtmp.getBitsPerPixel() * wmtmp.getWidth()) / 8;
         tmplen += (tmplen & 3);
         byte wmbuff[] = new byte[tmplen];
         tmplen = (Simage.getBitsPerPixel() * Simage.getWidth()) / 8;
         tmplen += (tmplen & 3);
         byte ibuff[] = new byte[tmplen];

         //Access each y-axis line till WaterMark image is completed
         for (int i = 0; i <= wmtmp.getHeight(); i++) {
           //Get WaterMark Line (y-axis) buffer array
           stat = wmtmp.IMGLOW_get_raster(i, wmbuff);
           if (stat < 0) {
             //Error Msg
             System.err.println(
                 "Error in WaterMark get byte array at line (y-axis) =" + i +
                 ": Stat =" + stat + ErrorCodes.getErrorMessage(stat));
             return ( -40);
           }
           //Get Open image Line (y-axis) buffer array
           stat = Simage.IMGLOW_get_raster(i, ibuff);
           if (stat < 0) {
             //Error Msg
             System.err.println(
                 "Error in Open Image get byte array at line (y-axis) =" + i +
                 ": Stat =" + stat + ErrorCodes.getErrorMessage(stat));
             return ( -40);
           }

           //Search for the proper range and if found replace
           for (int ii = 0; ii < wmbuff.length; ii++) {
             //WaterMarkImage is gray scaled so find bytes in given range
             if (! ( (wmbuff[ii] >= -70) && (wmbuff[ii] <= -1))) {
               if (options == 1)
                 ibuff[ii] = 0;
               else if (options > 1)
                 ibuff[ii] ^= 0xff;
               else
                 ibuff[ii] = -127; //wmbuff[ii];
             }
           }

           //Put changed data in back into the open image
           stat = Simage.IMGLOW_put_raster(i, ibuff);
           if (stat < 0) {
             //Error Msg
             System.err.println(
                 "Error in Open Image put byte array at line (y-axis) =" + i +
                 ": Stat =" + stat + ErrorCodes.getErrorMessage(stat));
             return ( -40);
           }
         }//Loop to next line
       }
       else {
         //Decompress 1 bpp WaterMark Image
         stat = wmtmp.IMG_decompress_bitmap("WaterMarkImage2.bmp", 0);
         if (stat < 0)
         {
           File ftmp = new File("WaterMarkImage2.bmp");
           if (ftmp.exists())
           {
             System.err.println("Error in Decomp WaterMark: Stat =" + stat + ErrorCodes.getErrorMessage(stat));
             return ( -40);
           }
           else
           {
             System.err.println("Can not find WaterMarkImage2.bmp: Please place image in the Main Class Directory! (i.e WaterMarkApp.class)");
             return ( -40);
           }
         }
         //Resize WaterMark Image to size of the Open Image
         //There are other methods to resize images --
         stat = wmtmp.IMG_resize_bitmap(Simage.getWidth(), Simage.getHeight());
         if (stat < 0) {
           //Error Msg
           System.err.println("Error in resizing WaterMark: Stat =" + stat + ErrorCodes.getErrorMessage(stat));
           return ( -40);
         }

         //Set WaterMark = for 1 bpp images
         stat = Simage.IMG_merge_block(wmtmp, 0, 0, 1);
         if (stat < 0) {
           //Error Msg
           System.err.println("Error in applying 1 bpp WaterMark: Stat =" + stat + ErrorCodes.getErrorMessage(stat));
           return ( -40);
         }
       }
       
       repaint();
       
       return 0;
       //uncaught exception
     }catch(java.lang.NullPointerException npe){return (-40);}
    }
    

    /**
     * Default constructor
     */
    public SnowboundPanelWithAnnotations()
    {
        super();
        mRubberBandListener = new RubberBandListener(this);
        mWatermarkListener = new WatermarkListener(this);
        mDefaultListener = new DefaultListener();
        mMagListener = new MagListener(this);
        magPanel = new MagPanel(this, 100);
        annLayer = new SnowAnn(0, 0);

        activateDefaultListener();
        setLayout(bl);
        
        // Create a new Snowbound image object.
        Simage = new Snowbnd();
        Simage.alias = 4;
        
        // Set the destination container for the image display to the current
        // panel
        Simage.setFrame(this);
        
        // Init Zoom Factor
        zoom = 0;
    }
    
    /**
     * Mouse Listener for the Panel
     */
    class snbdMouseAdapter extends MouseAdapter
    {
        private SnowboundPanelWithAnnotations sbp = null;

        /**
         * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e)
        {
            // If we are not in Magnification Panel mode will draw a selection
            // Rectangle
            if ((getDrawMag() == false) && (getZoomRect() == true))
            {
                selectionRectangle = new Rectangle();
                selectionRectangle.x = e.getX();
                selectionRectangle.y = e.getY();
                selectionRectangle.width = 5;
                selectionRectangle.height = 5;
                snbd_draw_pen();
            }
        }

        /**
         * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e)
        {
            sbp = (SnowboundPanelWithAnnotations) e.getSource();

            // If we are in mag panel mode we don't need to do any of this
            // clicking the mouse shouldn't matter in that mode
            if ((getDrawMag() == false) && (getZoomRect() == true))
            {
                snbd_draw_pen();
                Point pt = new Point();
                Point pt2 = new Point();
                pt.x = selectionRectangle.x;
                pt.y = selectionRectangle.y;
                Simage.map_wnd_to_image(sbp, pt);

                pt2.x = selectionRectangle.x + selectionRectangle.width;
                pt2.y = selectionRectangle.y + selectionRectangle.height;
                Simage.map_wnd_to_image(sbp, pt2);

                if (pt.x > pt2.x)
                {
                    selectionRectangle.x = pt.x;
                    pt.x = pt2.x;
                    pt2.x = selectionRectangle.x;
                }

                if (pt.y > pt2.y)
                {
                    selectionRectangle.y = pt.y;
                    pt.y = pt2.y;
                    pt2.y = selectionRectangle.y;
                }

                if (zoomRectangle == true)
                {
                    zoom = Simage.set_croprect_scroll(sbp, pt.x, pt.y, pt2.x
                        - pt.x, pt2.y - pt.y, 1);

                    repaint();

                    if (zoom < 0)
                    {
                        zoom = 0;
                    }

                    zoomRectangle = false;
                }

                if ((getDrawMag() == true))
                {
                    remove(magpanel);
                }

                // Turn off the Mag Box and Zoom Rectangle
                setDrawMag(false);
                setZoomRect(false);
            }         
        }
    }

    /**
     * Mouse Motion Listener for the panel
     */
    class snbdMouseMotionAdapter extends MouseMotionAdapter
    {
        /**
         * @see java.awt.event.MouseMotionAdapter#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e)
        {
            if (getZoomRect() == true)
            {
                if ((e.getX() > selectionRectangle.x)
                    && (e.getY() > selectionRectangle.y))
                {
                    snbd_draw_pen();
                    selectionRectangle.width = e.getX() - selectionRectangle.x;
                    selectionRectangle.height = e.getY() - selectionRectangle.y;
                    snbd_draw_pen();
                }
            }
        }

        /**
         * @see java.awt.event.MouseMotionAdapter#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved(MouseEvent e)
        {
            int magXpos, magYpos;

            if (magActive == true)
            {
                Point pt = new Point();
                pt.x = e.getX();
                pt.y = e.getY();

                if (e.getSource() == magpanel)
                {
                    pt.x = pt.x + (int) magpanel.getBounds().getX();
                    pt.y = pt.y + (int) magpanel.getBounds().getY();
                }

                // The mag box is 100x100
                magXpos = pt.x - 50;
                magYpos = pt.y - 50;
                Simage.map_wnd_to_image(SnowboundPanelWithAnnotations.this, pt);

                magpanel.move_mag(magXpos, magYpos, pt.x, pt.y);
            }
        }
    }
}
