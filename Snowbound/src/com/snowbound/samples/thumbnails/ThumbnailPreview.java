package com.snowbound.samples.thumbnails;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


import Snow.Defines;
import Snow.ErrorCodes;
import Snow.Format;
import Snow.FormatHash;
import Snow.MessageBox;
import Snow.SANN_RECT;
import Snow.SnowAnn;
import Snow.Snowbnd;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.List;


import com.snowbound.common.utils.ImageUtils;
//import com.snowbound.samples.annapp.MissingIcon;
import com.snowbound.samples.common.SnowboundPanelWithAnnotations;

import com.snowbound.samples.redaction.RedactionController;


public class ThumbnailPreview extends JFrame {
	
	public static final int maxDimension = 300;
	public static ThumbnailPreview tp;
	Color bkgColor = new Color(240,240,240);
	Snowbnd s;
	public JMenuItem open,exit;
	PreviewPanel picPreview;
	final int maxIcons = 3;
	int totalPages = 0;
    int thumPage = 0;
    int page = 0;
    ActionListener downLis;
   

    JButton thumb1 = new JButton();
    JButton thumb2 = new JButton();
    JButton thumb3 = new JButton();
    JButton thumb4 = new JButton();
    JButton thumb5 = new JButton();
    JButton thumb6 = new JButton();
    Insets in;
    String path = null;
    Dimension d;
    public Snowbnd Simage = new Snowbnd();

    String decompressedFilename = null;
    SnowboundPanelWithAnnotations snowpanel;
    String filename = null;
    JPanel mPreviewBorder;
    PreviewPanel mPreviewPanel;
    Color newColor = new Color(240,240,240);
    JLabel mInputDirLabel;
    JTextField mInputDirField;
    String mInputDirectoryPath;
    JButton mInputDirButton;
    public JToolBar buttonHolder;
    public JLabel photographLabel;
    //MissingIcon placeholderIcon = new MissingIcon();
    
    public iconWorker buttonAdder;
    PreviewPanel jp; 
    ActionListener upLis;
    JButton down, up;
    
    
    public ThumbnailPreview (String title) {
    	
		snowpanel = new SnowboundPanelWithAnnotations();
		setTitle(title);		
		addMenu();
		setRules();
		addPrevPanel();
		addArrows();
		addLayout();	
		new PreviewController(this);
		
	}
    
    

    public void addLayout() {
    	mInputDirLabel = new JLabel("Select an Image: ");
		mInputDirLabel.setBounds(5, 0, 110, 50);
		add(mInputDirLabel);

		mInputDirButton = new JButton("Browse");
		setLayout(null); 
		
		mInputDirButton.setBounds(430, 10, 530, 40);
		mInputDirButton.setSize(100,30);
		add(mInputDirButton);
		
		
		mInputDirLabel = new JLabel("Scroll Through Pages");
		mInputDirLabel.setBounds(600, 30, 650, 30);
		add(mInputDirLabel);
		
		
			
    }
    
    /**
     * Basic menu operations.
     */
	
	public void addMenu() {
		JMenuBar mb = new JMenuBar();
		setJMenuBar(mb);
		JMenu FileMenu = new JMenu("File");
		mb.add(FileMenu);
		open = new JMenuItem("Open");
		FileMenu.add(open);
		exit = new JMenuItem("Exit");
		FileMenu.add(exit);
		JMenu Options = new JMenu("Options");
		mb.add(Options);
		JMenu Help = new JMenu("Help");
		mb.add(Help);
		
	}
	
	/**
	 * Adds arrows to the frame. These are responsible for going 
	 * through the thumbnails.
	 */
	public void addArrows() {
		up = new JButton();
		  try {
		    Image img = ImageIO.read(getClass().getResource("/com/resource/up.jpg"));
		    img = getScaledImage(img, (30), 43);
		    up.setIcon(new ImageIcon(img));
		  } catch (IOException ex) {
			  
		  }
		  up.setBounds(110, 2, 150, 50);
		  up.setSize(30,43);
		  buttonHolder.add(up);
		  
		  down = new JButton();
		  try {
		    Image img = ImageIO.read(getClass().getResource("/com/resource/down.jpg"));
		    img = getScaledImage(img, (30), 43);
		    down.setIcon(new ImageIcon(img));
		  } catch (IOException ex) {
			  System.out.println("Error: Can't find file");
		  }
		  
		  
		  upLis = new ActionListener() {
			  
	            public void actionPerformed(ActionEvent e)
	            {	
	            	if(thumPage - 6 >= -1) { // Is only available if there are 6 images above
	            	thumPage = thumPage - 6;
	            	refresh();
	                checkArrows();
	            	}
	            	
	            }
		  };
		  up.addActionListener(upLis);
		  
		  
		  down.setBounds(110, 440, 150, 480);
		  down.setSize(30,43);
		  
		  if (totalPages - thumPage < 6) {
			  down.setEnabled(false);
		  }
				  
		  downLis = new ActionListener() {
			  
	            public void actionPerformed(ActionEvent e)
	            {		   
	            	
	            	if(totalPages - thumPage > 6) {
	            	thumPage = thumPage + 6;
	            	refresh();
	                checkArrows();
	            }
		  } };
		  checkArrows();
		  down.addActionListener(downLis);
		  buttonHolder.setBorder(null);
		  buttonHolder.add(down);
	}
    /**
     * Adds Jpanel that displays the image and the Jpanel that sets up the
     * border around that image
     */
	public void addPrevPanel() {
		setLayout(null);  
	    setVisible(true);
	    mPreviewBorder = new JPanel();
		mPreviewBorder.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.blue));
		mPreviewBorder.setBounds(0, 60, 540, 530);
		add(mPreviewBorder);
		mPreviewBorder.setLayout(null);
		jp = new PreviewPanel();  
	    jp.setBounds(5, 15, 540, 550);  
	    jp.setBackground(Color.WHITE);
	    jp.setSize(530, 510);
	    mPreviewBorder.add(jp);
	    buttonHolder = new JToolBar();
	    buttonHolder.setBounds(540, 70, 800, 586);
	    buttonHolder.setSize(250,516);
	    buttonHolder.setOpaque(false);
	    add(buttonHolder);
	    buttonHolder.setLayout(null);
	    buttonHolder.setFloatable(false);
	    buttonAdder = new iconWorker();
	    buttonAdder.execute();
	}
	
	/**
	 * Sets up the window size and makes sure the
	 * window isn't resizable.
	 */
	
	public void setRules() {
		
		setSize(825, 650);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	/**
	 * This method is called when you open a new document.
	 * The path is changed and a new image and set of thumbnails is
	 * loaded.
	 * @param p the new path
	 */
	public void changePath(String p) {
		path = p;
		page = 0;
        thumPage = 0;
		buttonAdder.cancel(true);
        buttonHolder.remove(thumb1);
        buttonHolder.remove(thumb2);
        buttonHolder.remove(thumb3);
        buttonHolder.remove(thumb4);
        buttonHolder.remove(thumb5);
        buttonHolder.remove(thumb6);
        
        
        buttonHolder.validate();
        buttonHolder.repaint();
        
        
        
        buttonAdder = new iconWorker();
        buttonAdder.execute();
        
        buttonHolder.revalidate();
        
        /*
         * Repaint new panel
         */
        
        mPreviewBorder.remove(jp);
        mPreviewBorder.validate();
        mPreviewBorder.repaint();
        
        
        
        
        jp = new PreviewPanel();
        jp.setBounds(5, 15, 540, 550);  
	    jp.setBackground(Color.WHITE);
	    jp.setSize(530, 510);
	    mPreviewBorder.add(jp);
	}
	
	
	/**
	 * @param args unused
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tp = new ThumbnailPreview("Thumbnail Preview");
                
                try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					
					e.printStackTrace();
				}
                tp.setVisible(true);
            }
        });

	}
	
	public void addMenuActionListeners(ActionListener openActionListener)
		{
		open.addActionListener(openActionListener);
		exit.addActionListener(openActionListener);
		mInputDirButton.addActionListener(openActionListener);
		}
	/**
	 * This extends the SwingWorker class. Swingworker works in the background
	 * of the GUI. This allows for components to be easily updated. 
	 * @author acarey
	 *
	 */
	
	
	class iconWorker extends SwingWorker<Void, ThumbnailAction> {

		
		/**
		 * Creates full size and thumbnail versions of the target image files.
		 */
		
		@Override
		protected Void doInBackground() throws Exception {
			
				publish(new ThumbnailAction(null, null, null));
			return null;
		}


		@Override
		protected void process(List<ThumbnailAction> chunks) {

			  int pageLeft; // determines how many thumbnails to make. max of 6
			  if ( totalPages - thumPage < 6) {
				  pageLeft = totalPages - thumPage;
			  }
			  else {
				  pageLeft = 6;
			  }
			  if (path != null){
			  File documentFile = new File(path); // get the path
			  byte[] myByte = null; 
			  BufferedImage bImageFromConvert;
			  InputStream strm;
			  
			  BufferedImage[] images = new BufferedImage[6];
			  /**
			   * Creates thumbnail images.
			   */
			  for(int i = 0; i < pageLeft; i++) {
				  try {
					myByte = getThumbnailBytes(Simage, documentFile,
								i + thumPage, maxDimension);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				  strm = new ByteArrayInputStream(myByte); 
				  try {
					bImageFromConvert = ImageIO.read(strm);
					images[i] = bImageFromConvert;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				  
			  }
			  /*
			   *This action listener helps display the page of the thumbnail
			   * the user clicked on. 
			   */
			  
			  ActionListener thumbAction = new ActionListener() {
				 
		            public void actionPerformed(ActionEvent e)
		            {		        
		            /*
		             * Note: the thumbs to not necessarily correspond with count.
		             * This is because the thumbnails names are not in order 
		             * with corresponding pages.
		             * 
		             */
		            	String name = ((Component) e.getSource()).getName();
		            	int count = 0;
		            	if (name.equals("thumb1")) {
		            		count = 0;
		            	}
		            	if (name.equals("thumb2")) {
		            		count = 2;
		            	}
		            	if (name.equals("thumb3")) {
		            		count = 1;     
		            	}
		            	if (name.equals("thumb4")) {
		            		count = 3;
		            	}
		            	if (name.equals("thumb5")) {
		            		count = 5;
		            	}
		            	if (name.equals("thumb6")) {
		            		count = 4;
		            	}
		                mPreviewBorder.remove(jp); // remove previous preview panel
		                mPreviewBorder.validate();
		                mPreviewBorder.repaint();
		                
		                /*
		                 * Create new preview panel which displays the proper page. 
		                 */
		                
		                tp.page = tp.thumPage + count; // go to the page which corresponds with the thumbnail
		                jp = new PreviewPanel();
		                jp.setBounds(5, 15, 540, 550);  
		        	    jp.setBackground(Color.WHITE);
		        	    jp.setSize(530, 510);
		        	    mPreviewBorder.add(jp);
		                
		                
		            }
			  };
			
			  
			  buttonHolder.setLayout(null);
			  
			  /*
			   * The following sets up the placement for these thumbnails.
			   * The program will only create the thumbnail if there is
			   * a corresponding page.
			   */
			  
			  if(pageLeft > 0) {
			  buttonHolder.remove(thumb1);
			  buttonHolder.revalidate();
			   
			  thumb1 = new JButton();
			  thumb1.setName("thumb1");
			  images[0] = (BufferedImage) getScaledImage(images[0], getThumbWidth(images[0]), getThumbHeight(images[0]));
			  thumb1.setIcon(new ImageIcon(images[0]));			 
			  thumb1.setBounds(5,55,105,155);
			  thumb1.setSize(120, 120);
			  thumb1.addActionListener(thumbAction);
			  thumb1.revalidate();
			  
			  buttonHolder.add(thumb1);
			  }
			  
			  
			  if(pageLeft > 1) {
			  buttonHolder.remove(thumb2);
			  buttonHolder.revalidate();
			  thumb2 = new JButton();
			  thumb2.setName("thumb2");
			  images[2] = (BufferedImage) getScaledImage(images[2], getThumbWidth(images[2]), getThumbHeight(images[2]));
			  thumb2.setIcon(new ImageIcon(images[2]));			 
			  thumb2.setBounds(5,180,105,280);
			  thumb2.setSize(120, 120);
			 
			  thumb2.addActionListener(thumbAction);
			  
			  
			  buttonHolder.add(thumb2);
			  }
			  if(pageLeft > 2) {
		      buttonHolder.remove(thumb3);
			  buttonHolder.revalidate();
			  thumb3 = new JButton();
			  thumb3.setName("thumb3");
			  images[1] = (BufferedImage) getScaledImage(images[1], getThumbWidth(images[1]), getThumbHeight(images[1]));
			  thumb3.setIcon(new ImageIcon(images[1]));			 
			  thumb3.setBounds(130,55,235,155);
			  thumb3.setSize(120, 120);
			  thumb3.addActionListener(thumbAction);
			  buttonHolder.add(thumb3);
			  }
			  if(pageLeft > 3) {
			  buttonHolder.remove(thumb4);
			  buttonHolder.revalidate();
		      thumb4 = new JButton();
		      thumb4.setName("thumb4");
			  images[3] = (BufferedImage) getScaledImage(images[3], getThumbWidth(images[3]), getThumbHeight(images[3]));
			  thumb4.setIcon(new ImageIcon(images[3]));			 
			  thumb4.setBounds(130,180,235,280);
			  thumb4.setSize(120, 120);
			  thumb4.addActionListener(thumbAction);
			  
			  
			  buttonHolder.add(thumb4);
			  }
			  if(pageLeft > 4) {
			  buttonHolder.remove(thumb5);
			  buttonHolder.revalidate();
			  thumb5 = new JButton();
			  thumb5.setName("thumb5");
			  images[5] = (BufferedImage) getScaledImage(images[5], getThumbWidth(images[5]), getThumbHeight(images[5]));
			  thumb5.setIcon(new ImageIcon(images[5]));			 
			  thumb5.setBounds(130,305,235,335);
			  thumb5.setSize(120, 120);
			  thumb5.addActionListener(thumbAction);
			  
			  buttonHolder.add(thumb5);
			  }
			  if(pageLeft > 5){
			  buttonHolder.remove(thumb6);
			  buttonHolder.revalidate();
			  thumb6 = new JButton();
			  thumb6.setName("thumb6");
			  images[4] = (BufferedImage) getScaledImage(images[4], getThumbWidth(images[4]), getThumbHeight(images[4]));
			  thumb6.setIcon(new ImageIcon(images[4]));			 
			  thumb6.setBounds(5,305,105,335);
			  thumb6.setSize(120, 120);
			  thumb6.addActionListener(thumbAction);
			  
			  buttonHolder.add(thumb6);
			  }

			  
			  
			  /*
			   * This resets the actionListener  
			   */
			  }
			  if(up != null) {
			  up.removeActionListener(upLis);
			  up.addActionListener(upLis = new ActionListener() {
				  
		            public void actionPerformed(ActionEvent e)
		            {	
		            	if( thumPage - 6 >= -1) {
		            	thumPage = thumPage - 6;
		            	refresh();
		                checkArrows();
		            	}
		            	
		            }
			  });
			  }
			  
			  if (down!= null) {
			  down.removeActionListener(downLis);
			  down.addActionListener(downLis = new ActionListener() {
				  
		            public void actionPerformed(ActionEvent e)
		            {		   
		            	
		            	if(totalPages - thumPage > 6) {
		            	thumPage = thumPage + 6;
		                refresh();
		                checkArrows();
		            }
		            	
			  } });
			  
			  
		}
			  
			  /*
			   * This is to check arrows after a file has been loaded
			   */
			  checkArrows();
		}
		
		
		

	};
	
	public void checkArrows() {
		if( thumPage - 6 >= -1) {
          	up.setEnabled(true);
          }
          else {
          	up.setEnabled(false);
          }
	
          if(totalPages - thumPage > 6) {
          	down.setEnabled(true);
          }
          else {
          	down.setEnabled(false);
          }
	}
	/** 
	 * Add new thumbnails. Get rid of old thumbnails
	 */
	public void refresh() {
		buttonAdder.cancel(true);
        buttonHolder.remove(thumb1);
        buttonHolder.remove(thumb2);
        buttonHolder.remove(thumb3);
        buttonHolder.remove(thumb4);
        buttonHolder.remove(thumb5);
        buttonHolder.remove(thumb6);
        
        
        buttonHolder.validate();
        buttonHolder.repaint();
        
        
        buttonAdder = new iconWorker();
        
        buttonAdder.execute();
        
        buttonHolder.revalidate();
	}
	
	/**
	 * This method scales the image to the thumbnail size. 
	 * Based on converting to 100 x 100 scale. 
	 * For example a 400 x 300 image would be converted into a 
	 * 100 x 75 image.
	 * @param img
	 * @return
	 */
	public int getThumbHeight(BufferedImage img) {
		int height = img.getHeight(null);
		int width = img.getWidth(null);
		
		if((height < 100) && (width < 100)) {
			height = height; // to prevent a smaller picture
							//	from being blown up in thumbnail
		}
		else if (height >= width) {
			height = 100;
		}
		else if (height < width) {
			double widthRatio = (width/100);
			height =  (int) (height/widthRatio);
		}
		
		return height;
	}
	/**
	 * This method scales the image to the thumbnail size. 
	 * Based on converting to 100 x 100 scale. 
	 * For example a 400 x 300 image would be converted into a 
	 * 100 x 75 image.
	 * @param img
	 * @return width
	 */
	public int getThumbWidth(BufferedImage img) {
		int height = img.getHeight(null);
		int width = img.getWidth(null);
		if((height < 100) && (width < 100)) {
			width = width; // to prevent a smaller picture
							//	from being blown up in thumbnail
		}
		else if (width >= height) {
			width = 100;
		}
		else if (width < height) {
			double heightRatio = (height/100);
			width =  (int) (width/heightRatio);
		}
		else {
			width = 100;
		}
	
		return width;
	}
	
	/**
	 * Creates an ImageIcon if the path is valid.
	 * 
	 * @param String
	 *            - resource path
	 * @param String
	 *            - description of the file
	 */
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Resizes an image using a Graphics2D object backed by a BufferedImage.
	 * 
	 * @param srcImg
	 *            - source image to scale
	 * @param w
	 *            - desired width
	 * @param h
	 *            - desired height
	 * @return - the new resized image
	 */
	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

		/**
		 * @param simage
		 * @param documentStream
		 * @param pageIndex
		 * @param maxDimension
		 * @param supportRedactions
		 *            TODO
		 * @param extractedPageThumbnails
		 *            TODO
		 * @return
		 */
		protected static byte[] getThumbnailBytes(Snowbnd simage, File documentFile,
				int pageIndex, int maxDimension) throws Exception {
			
			final int THUMBNAIL_DPI = 72;
			final int THUMBNAIL_BITDEPTH = 24;
			final int THUMBNAIL_BYTESIZE = 60000;
			int thumbnailDPI = THUMBNAIL_DPI;
			int thumbnailBitDepth = THUMBNAIL_BITDEPTH;
			// FIXME - cache this stream???
			int filetype = ErrorCodes.EXCEPTION_ERROR;
			filetype = simage.IMGLOW_get_filetype(documentFile
					.getAbsolutePath());
			if (filetype == Defines.XLSX) {
				/*
				 * AJH: Because XLSX renders pages vastly differently when
				 * various DPIs are used, we need to make sure the thumbnail DPI
				 * matches the DPI of the main image(s). Otherwise there will be
				 * page indexing errors, page not found errors, etc. We also
				 * will render at 1-bit only because otherwise we will run into
				 * memory errors.
				 */
				thumbnailDPI = THUMBNAIL_DPI;
				thumbnailBitDepth = 1;
			}
			int truesize = 0;
			byte[] tempBytes = null;
			byte[] finalBytes;
			int myformat = Defines.PNG;
			simage.PrintStack = true;
			Format documentFormat = FormatHash.getInstance()
					.getFormat(filetype);
			/*
			 * Best to use this local variable for the rest of this method, in
			 * case the FormatHash is out of date.
			 */
			boolean isVectorDocument = documentFormat != null
					&& documentFormat.isVectorFormat();
			int thumbWidth = 0;
			int thumbHeight = 0;
			if (!isVectorDocument) {
				simage.IMGLOW_get_fileinfo(documentFile.getAbsolutePath(),
						pageIndex);
				int imageHeight = simage.getHeight();
				int imageWidth = simage.getWidth();
				int xDPI = simage.getXdpi();
				int yDPI = simage.getYdpi();
				Dimension thumbSize = ImageUtils.getThumbnailSize(maxDimension,
						imageWidth, imageHeight, xDPI, yDPI);
				thumbWidth = thumbSize.width;
				thumbHeight = thumbSize.height;
			}
			// gLogger.log(Logger.FINEST, "thumbWidth: " +
			// thumbWidth
			// + ", thumbHeight: " + thumbHeight);
			int result = 0;
			boolean hasRedactionOnPage = false;
			simage.IMGLOW_set_document_input(thumbnailDPI, thumbnailBitDepth,
					filetype);
			/*
			 * You can't call set_decomp_size on a 16 bit image, so we will
			 * decompress normally and then call get_thumbnail. -AJH.
			 */
			if ((simage.getBitsPerPixel() == 1)
					|| (simage.getBitsPerPixel() == 16)
					|| (hasRedactionOnPage == true)) {
				/**
				 * reset the decomp size in case we are re-using a scaled one -
				 * AJH
				 */
				simage.IMGLOW_set_decompsize(0, 0);
				long preCall = System.currentTimeMillis();
				result = simage.IMG_decompress_bitmap(
						documentFile.getAbsolutePath(), pageIndex);
				long postCall = System.currentTimeMillis();
				long duration = postCall - preCall;
				// Change made to accomodate browsers inability to display 16b
				// PNGs.
				if ((filetype == Defines.DICOM)
						&& (simage.getBitsPerPixel() == 16)) {
					simage.IMG_window_level(0, 0, 1);
					simage.IMG_promote_8();
				}
				simage.IMG_create_thumbnail(thumbWidth, thumbHeight);
				simage.setYdpi(simage.getXdpi());
			} else {
				if (!isVectorDocument) {
					/*
					 * thumbWidth should be non-zero for raster formats, and
					 * should be 0 for vector text formats. AJH
					 */
					simage.IMGLOW_set_decompsize(thumbWidth, thumbHeight);
				}
				long preCall = System.currentTimeMillis();
				result = simage.IMG_decompress_bitmap(
						documentFile.getAbsolutePath(), pageIndex);
				if (result < 0) {
					if (filetype == Defines.PDF) {
						/*
						 * The following is a hack for files that can be
						 * processed by itext, but can not be processed directly
						 * by rastermaster.
						 */
						int stat[] = new int[1];
						byte[] buff = null;
						buff = simage
								.IMGLOW_extract_page(
										documentFile.getAbsolutePath(),
										pageIndex, stat);
						DataInputStream extractedStream = new DataInputStream(
								new ByteArrayInputStream(buff));
						/* Don't try to set a decomp size when we are pdf */
						simage.IMGLOW_set_decompsize(0, 0);
						result = simage.IMG_decompress_bitmap(extractedStream,
								0);
						if (result < 0) {
							/*
							 * We return a byte of length 1. if we return null
							 * or an empty byte array, then the client can not
							 * cache the contents The client will be written to
							 * appropriately handle this
							 */
							return null;
						}
					} else {
						System.out.println("Could not find file");;
					}
				}
				long postCall = System.currentTimeMillis();
				long duration = postCall - preCall;
			}
			if (isVectorDocument) {
				/*
				 * Since we didn't call set_decomp_size() for the vector formats
				 * We need to scale the image here
				 */
				int imageWidth = simage.getWidth();
				int imageHeight = simage.getHeight();
				Dimension thumbSize = ImageUtils.getThumbnailSize(maxDimension,
						imageWidth, imageHeight, thumbnailDPI, thumbnailDPI);
				simage.IMG_create_thumbnail(thumbSize.width, thumbSize.height);
			}
			if (result < 0) {
				System.out.println("Decompressing thumbnail: "
						+ ErrorCodes.getErrorMessage(result));
			} else {
				int degrees = simage.IMGLOW_get_image_orientation();
				if (degrees > 0) {
					simage.IMG_rotate_bitmap((360 - degrees) * 100);
				}
			}
			int[] sizeArray = new int[1];
			try {
				// truesize = simage.IMG_save_bitmap(tempBytes, format);
				long preCall = System.currentTimeMillis();
				tempBytes = simage.IMG_save_bitmap(THUMBNAIL_BYTESIZE,
						THUMBNAIL_BYTESIZE, myformat, sizeArray);
				long postCall = System.currentTimeMillis();
				long duration = postCall - preCall;
				truesize = sizeArray[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
			finalBytes = new byte[truesize];
			System.arraycopy(tempBytes, 0, finalBytes, 0, truesize);
			// gLogger.log(Logger.FINE, "Processed thumbnail is " + truesize
			// + " bytes");
			return finalBytes;
		}

    /**
     * Action class that shows the image specified in it's constructor.
     */
		
}
  class ThumbnailAction extends AbstractAction {
        

        private Icon displayPhoto;
        


        public ThumbnailAction(Icon photo, Icon thumb, String desc){
            displayPhoto = photo;
            putValue(SHORT_DESCRIPTION, desc);
            putValue(LARGE_ICON_KEY, thumb);
        }
        

        public void actionPerformed(ActionEvent e) {

        }
    }
  	
  	
  
	class PreviewPanel extends JPanel
	{
		private static final long serialVersionUID = 5544297606459224866L;

		public PreviewPanel()
		{
			super();
		}

		public Snowbnd getImage()
		{
			return new Snowbnd();
		}

		/**
		 * Checks to see if the image is larger than the panel. 
		 * Will fit to screen if it is. Else, it does not. 
		 */
		public void paint(Graphics g)
		{ 
			if(ThumbnailPreview.tp.path != null) {
			Snowbnd s = getImage();
			File documentFile = new File(ThumbnailPreview.tp.path);
			byte[] myBytes = null;
			BufferedImage img = null;
			try {
				myBytes = ThumbnailPreview.getThumbnailBytes(s, documentFile,
						ThumbnailPreview.tp.page, 800);
				ByteArrayInputStream strm = new ByteArrayInputStream(myBytes); 
				 
					 img = ImageIO.read(strm);
		
			} catch (Exception e) {
				System.out.println("Could not find file");
			//	e.printStackTrace();
			}

			
			if (s != null)
			{
				Insets in = getInsets();
				Dimension dimension = getSize();
				dimension.width -= (in.right + in.left);
				dimension.height -= (in.top + in.bottom);
				g.translate(in.left, in.top);
				int screenWidth;
				int screenHeight;
				if((img != null) && (img.getHeight() < dimension.height) && (img.getWidth() < dimension.width)) {
					screenWidth = img.getWidth();
					screenHeight = img.getHeight();
				}
				else { // image is bigger than size of panel
					screenWidth = dimension.width;
					screenHeight = dimension.height;
				}
				
			
				s.IMG_display_bitmap_aspect(g,
						this,
						0,
						0,
						screenWidth,
						screenHeight,
						0);
			}
		}
		}
		public void update(Graphics g)
		{
			paint(g);

		}

	}


	
	

