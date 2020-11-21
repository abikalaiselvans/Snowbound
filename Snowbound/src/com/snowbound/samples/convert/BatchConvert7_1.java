package com.snowbound.samples.convert;
/**
 * Copyright (C) 2007-2017 by Snowbound Software Corp. All rights reserved.
 * This is example code for all SnowBound customers to freely copy and use however they wish.
 * 
 * This sample provides a GUI that lets you choose a directory or a list, then converts 
 * all of the files in the directory to an output directory.
 * 
 *  This sample makes use of the following RasterMaster methods:
 *  
 *  IMG_display_bitmap_aspect(java.awt.Graphics,java.awt.Container,int,int,int,int,int)
 *  IMGLOW_get_pages(String inputFilename)
 *	IMGLOW_set_document_intput(int dpi, int bitDepth, int format)
 *	IMGLOW_decompress_bitmap(String inputFilename, int pageIndex)
 *	IMGLOW_extract_text(String,int[],int[],int)
 *	IMGLOW_set_pcl_input(int,int);
 *	IMGLOW_set_pdf_input(int,int);
 *	IMGLOW_set_comp_quality(int)
 *	IMGLOW_set_jpg_interleave(int,int)
 *	IMGLOW_set_fontmap_path(String)
 *	IMGLOW_set_pdf_output(int,int)
 *	IMGLOW_extract_page(String,int,int[])
 *	IMGLOW_set_fast_convert(int,int)
 *	IMGLOW_set_brightness(int)
 *	IMGLOW_set_contrast(int)
 *	IMGLOW_append_page(String,byte[],int);
 *	IMG_promote_24(void)
 *	IMG_promote_8(void)
 *	IMG_color_gray(void)
 *	IMG_save_bitmap(String outputFilename, int outputFormat)
 *	IMG_despeckle_bitmap(int)
 *	IMG_resize_bitmap(int,int)
 *	IMG_histogram_equalize(void)
 *	IMG_rgb_to_cmyk(void)
 *	IMG_diffusion_mono(void)	
 *	IMG_invert_bitmap(void)
 *	IMG_antique_effect(void)
 *	alias(int)
 *	alias_quality(int)
 *	threshold(int)
 *  
 * Authors: Alan Shepard, Barbara Bazemore, Bismark Frimpong
 *  
 * Requires Java 1.6x and later
 */
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JProgressBar;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JSpinner;

//import com.snowbound.common.utils.Logger;






import static java.nio.file.StandardCopyOption.*;
import Snow.Defines;
import Snow.ErrorCodes;
import Snow.Format;
import Snow.FormatHash;
import Snow.MessageBox;
import Snow.Snowbnd;

import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ImageIcon;

class FileProgress
{
	public String currentFile;
	public String currentFileSaveAs;
	public int currentPage = -1;
	int completePage = 0;
	boolean done = true;


	public void setCurrentFile(String currentFile)
	{
		this.currentFile = currentFile;
	}

	public void setCurrentFileSaveAs(String currentFileSaveAs)
	{
		this.currentFileSaveAs = currentFileSaveAs;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	public String getCurrentFile()
	{
		return currentFile;
	}

	public String getCurrentFileSaveAs()
	{
		return currentFileSaveAs;
	}

	public int getCurrentPage()
	{
		return currentPage;
	}
	public boolean getPage()
	{
		if(currentPage > completePage)
		{
			currentPage++;
			return false;

		}
		return done;
	}
}

//Redirect Java System.out and System.err streams to both a File and Console.
class SaveLogOutputStream extends OutputStream
{
	OutputStream[] outputStreams;
	
	public SaveLogOutputStream(OutputStream... outputStreams)
	{
		this.outputStreams= outputStreams; 
	}
	
	@Override
	public void write(int b) throws IOException
	{
		for (OutputStream out: outputStreams)
			out.write(b);			
	}
	
	@Override
	public void write(byte[] b) throws IOException
	{
		for (OutputStream out: outputStreams)
			out.write(b);
	}
 
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		for (OutputStream out: outputStreams)
			out.write(b, off, len);
	}
 
	@Override
	public void flush() throws IOException
	{
		for (OutputStream out: outputStreams)
			out.flush();
	}
 
	@Override
	public void close() throws IOException
	{
		for (OutputStream out: outputStreams)
			out.close();
	}
	
	public static void saveOutputStream(File saveError) throws IOException
	{
		try
		{
			File afile = new File(saveError.toString());
			if(!afile.exists())
			{
				afile.createNewFile();
				
				
			}
			FileOutputStream fout= new FileOutputStream(afile);
			FileOutputStream ferr= new FileOutputStream(afile);
			
			SaveLogOutputStream multiOut= new SaveLogOutputStream(System.out, fout);
			//SaveLogOutputStream multiErr= new SaveLogOutputStream(System.err, ferr);
			
			PrintStream stdout= new PrintStream(multiOut);
			PrintStream stderr= new PrintStream(ferr);
			
			System.setOut(stdout);
			System.setErr(stderr);
		}
		catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
			//Could not create/open the file
		}
		
	}
}

public class BatchConvert7_1 extends JFrame {

	private static final long serialVersionUID = -838980578749588100L;
	static Snowbnd s ;
	PreviewPanel mPreviewPanel;
	private static FileProgress[] myFileProcessor;
	private static BatchConvert7_1 mBatchConvertFrame;
	private static SaveLogOutputStream outStream;
	private ConvertInputdocuments doInputWork;
	private ListInputDocumentConvert doListInputWork;
	private FileFilter filterFile;
	private FileFilter pdfTiffFilter;
	/*private static String userHome = System.getProperty("user.home",".");
	private static String FILE_SEPARATOR = System.getProperty("file.separator","/");
	private static String logFileName = (userHome+FILE_SEPARATOR+"ErrorOuput.log");*/
	private JPanel contentPane;
	private JMenuBar menuBar;
	private JSeparator separator;
	private JList<File> list;
	private JScrollPane scrollPane;
	private JTabbedPane advanceOptionsPane;
	private JButton addButn;
	private JButton removeBtn;
	private JButton removeAllBtn;
	private JButton moveUpBtn;
	private JButton downBtn;
	private JFileChooser mFileBrowser;
	private JButton convBtn;
	private JButton pauseBtn;
	private JButton btnLogfile;
	private JPanel directoryPanel;
	private JPanel optionsPanel;
	private JLabel inputDirLabel;
	private JLabel outputDirLabel;
	private JTextField inputTextField;
	private JTextField ouptutTextField;
	private JButton outputBrowseBtn;
	private JButton inputBrowseBtn;
	private JPanel statusPanel;
	private JLabel inputStatusLabel;
	private JLabel outputStatusLabel;
	private JPanel convTimePanel;
	private JPanel brightness_contrast_Panel;
	private JPanel pdf_doc_options;
	private JPanel imgManipulations;
	private JTextField listOuputTextField;
	private JButton listBrowseBtn;
	public JComboBox batchComboBox;
	private JProgressBar mProgressBar;
	public JCheckBox chckbxPdfInput;
	private JLabel dpiLabel;
	public JComboBox<Integer> pdfInputdpiComboBox;
	private JLabel bitPerPixelLabel;
	public JComboBox<Integer> pdfInputbitPerPixelComboBox;
	private JCheckBox chckbxPdfOuput;
	private JLabel widthLabel;
	private JLabel heightLabel;
	private JSpinner wSpinner;
	private JSpinner hSpinner;
	private JCheckBox chckbxDocumentInput;
	private JLabel docDPILabel;
	private JComboBox<Integer> docInputDpiComboBox;
	private static JCheckBox chckbxPclInput;
	private static JComboBox<Integer> pclDpiComboBox;
	private static JComboBox<Integer> pclBppComboBox;
	private JLabel DocbitPerPixelLabel;
	private JLabel pclInputDpiLabel;
	private JLabel pclBppLabel;
	private static JCheckBox chckbxDespeckle;
	private static JComboBox<Integer> despeckleComboBox;
	private static JCheckBox chckbxAntiqueEffect ;
	private static JCheckBox chckbxInvert;
	private static JCheckBox chckbxPromoteEightBit;
	private JComboBox<Integer> docBitPerPixelComboBox;
	private JCheckBox chckbxImage;
	private JSpinner imgWidthSpinner;
	private JLabel imgHeightLabel;
	private JSpinner imgHeightSpinner;
	private JLabel imgWidthLabel;
	private static JCheckBox chckbxbitGrayscale;
	private static JCheckBox chckbxPromotebit;
	private static JCheckBox chckbxImprovebitGrayscale;
	private static JCheckBox chckbxRgbToCymk;
	private static JCheckBox chckbxEnableFastConversion;
	private JCheckBox chckbxDeleteSourceFiles;
	private JCheckBox chckbxSendProblemFiles;
	private JTextField problemFilesTextField;
	private JButton problemFilesBtn;
	private JPanel mPreviewBorder;
	private int totalPageCount = 0;
	private JCheckBox chckbxSaveLog;
	private static JCheckBox chckbxDuffuion;
	DefaultListModel<File> listModel;	
	Color newColor = new Color(240,240,240);
	String conversionFormatExtension = null;
	String mInputDirectoryPath = null;
	String mSourcepdfPath = null;
	String mTargetpdfPath = null;
	static String mOutputDirectoryPath = null;
	static String mListOutputDirectoryPath = null;
	String mFileInputLocationPath = null;
	String mProblemDirectoryPath = null;
	static String mAfpFontmapDirectoryPath = null;
	String[] inputFileList = null;
	File[] totalFiles = null;
	File[] jlistFiles = null;
	File outputSourceFile = null;
	File errorFile = null;
	File getFileNameSave= null;
	File getListfileSave = null;
	Timer myTimer = null;
	Thread inputStatusThread = null;
	Thread outputStatusThread = null;
	Thread processDirectoryThread = null;
	int counter = 0;
	int convertToFormat = 0;
	static int status = -1;
	private boolean gDone = false;
	private boolean isPaused = false;
	int minValue = 0;
	int complete = 100;
	int result = 0;	
	private int mZoom = 1;
	private boolean mZoomedOut = false;
	private static int vectorPdfPageIndex = 0;
	private static int bitMapPageIndex = 0;
	private static int nstat;
	private byte[] data;
	//Initial size of buffer used by IMG_save_bitmap()
	private static int initialBufferSize = 60000;
	//Amount the buffer used by IMG_save_bitmap() will grow by if needed
	private static int expandBufferSize = 40000;
	String	inFileName;
	String	outBaseName;
	String	outExt;
	int[] length = new int[2];
	int[] error = new int[2];
	static int[] errorA = new int[1];
	int pageBitsPerPixel;
	int docColorDetect;
	private JCheckBox chckbxListSelections;
	private JCheckBox chckbxBatchConversion;
	private JCheckBox chckbxSplit;
	private static JCheckBox chckbxExtractPdf;
	private static int pdfInputNumDPI;
	private static int pdfInputNumBits_pix;
	private static int docInputNumDPI;
	private static int pclInputNumDpi;
	private static int pclInputNumBits_pix;
	private static int despeckleNumInput;
	private static int anti_alising;
	private static int anti_aliasing_quality;
	private static int docInputNumBits_pix;
	private static int jpegCompQuality;
	private static int pdf_x_status,pdf_y_status;
	private static int x_resize_image,y_resize_image;
	private static int img_threshold;
	private static int h_interleaveValue;
	private static int v_interleaveValue;
	private static JCheckBox chckbxExtractText;
	private static JCheckBox chckbxJpegCompression;
	private JTextArea convtimeTextArea;
	private JScrollPane covtimeScrollPane;
	private JPanel brightnessPanel;
	private JPanel contrastPanel;
	private JRadioButton rdbtnBrightness_increase;
	private JRadioButton rdbtnBrightness_decrease;
	private JRadioButton radioContrast_increase;
	private JRadioButton radioContrast_decrease;
	private JSlider brightnessIncreaseSlid;
	private JSlider brightnessDecreaseSlid;
	private JSlider contrastIncreaseSlid;
	private JSlider contrastDecreaseSlid;
	private JRadioButton rdbtnBrightness_noChange;
	private JRadioButton radioContrast_noChange;
	public static Date startTime;
	public static Date endTime;
	private JLabel snowboundLogoLabel;
	private static JTextField afpFontmapTextField;
	private static JCheckBox chckbxAfpFontmapping;
	private JButton afpFontmapBtn;
//	private static byte[] extractedText;
	private static JSpinner jpegCompSpinner;
	ExecutorService executorService = Executors.newCachedThreadPool();
	private static JComboBox<Integer> h_InterleaveComboBox;
	private static JComboBox<Integer> v_InterleaveComboBox;
	private static JCheckBox chckbxAntialiasing;
	private static JComboBox<Integer> aliasingComboBox;
	private JLabel aliasingQualityLabel;
	private static JComboBox<Integer> aliasQualityComboBox;
	private JLabel h_InterleaveLabel;
	private JLabel v_InterleaveLabel;
	private static JCheckBox chckbxImgThreshold;
	private static JSpinner thresholdSpinner;
	private JCheckBox chckbxSavesearchable;
	private JButton stopBtn;
	private JPanel advancePdf;
	private JLabel sourcePdfLabel;
	private JTextField sourcepdfTextfield;
	private JButton sourceBtn;
	private JTextField targetTextfield;
	private JLabel targetpdfLabel;
	private JButton targetBtn;
	private JCheckBox chckbxMergePdf;
	private JLabel mergeDoc_infoLabel;
	private JButton btnMergeDoc;
	private JProgressBar mFileProgressbar;
	private final Random randomInt = new Random();
	private final Random randomNum = new Random();
	private JProgressBar waitProgress;
	//private static Logger gLogger = Logger.getInstance();

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
					mBatchConvertFrame  = new BatchConvert7_1();
					mBatchConvertFrame.initializeFormatsList();
					mBatchConvertFrame.setLocation((int) ((screenDimension.width - mBatchConvertFrame.getWidth())/2), (int) ((screenDimension.height - mBatchConvertFrame.getHeight())/2));
					mBatchConvertFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	 public BatchConvert7_1() {
		super();
		setIconImage(Toolkit.getDefaultToolkit().getImage(BatchConvert7_1.class.getResource("/com/resource/RasterMaster 100x100.png")));
		setTitle("Snowbound Software :: BatchConvert7.1");
		s = new Snowbnd();	
		mFileBrowser = new JFileChooser();
		filterFile =  new FileNameExtensionFilter( "Font map: FNT", "fnt");
		pdfTiffFilter = new FileNameExtensionFilter("PDF-TIFF: PDF, TIFF", "pdf", "tif");
		mBatchConvertFrame = this;
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		java.awt.Rectangle frame = getBounds();
		int majorVersion[]={0};
		int minorVersion[]={0};
		s.IMG_get_version(majorVersion, minorVersion);
		System.out.println("RasterMaster Java version: " + majorVersion[0] + "." + minorVersion[0]);
		
		// License stuff. We do this once
		int licnStat = s.IMGLOW_set_ooxml_license();
		if(licnStat < 0)
		{
			if(licnStat == -52)
			{
				JOptionPane.showMessageDialog(mBatchConvertFrame, "Failed to enable ooxml license: OOXML_LICENSE_NOT_FOUND \0"+licnStat, "OOXML_LICENSE", JOptionPane.OK_OPTION);
			}	
			else if(licnStat == -53)
			{
				JOptionPane.showMessageDialog(mBatchConvertFrame, "Failed to enable ooxml license: OOXML_LICENSE_EXPIRED \0"+licnStat, "OOXML_LICENSE", JOptionPane.OK_OPTION);
			}
			else
			{
				JOptionPane.showMessageDialog(mBatchConvertFrame, "Failed to enable ooxml license! Error: %d \0 "+licnStat, "OOXML_LICENSE" , JOptionPane.OK_OPTION);
			}
		}		
		System.out.println("License File status = " + licnStat + ", " + ErrorCodes.getErrorMessage(licnStat));
		String licensePath = s.IMGLOW_get_ooxml_license_path();
		System.out.println("License File Path = " + licensePath);
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener( new ExitBatchConvert() );  
		setBounds(100, 100, 742, 828);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		separator = new JSeparator();
		separator.setBounds(0, 63, 736, 2);
		contentPane.add(separator);
		
		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 736, 21);
		contentPane.add(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenu mnAbout = new JMenu("Help");
		menuBar.add(mnAbout);
				
		advanceOptionsPane = new JTabbedPane(JTabbedPane.TOP);
		advanceOptionsPane.setBorder(new TitledBorder(null, "Advance Options", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		advanceOptionsPane.setBounds(0, 444, 323, 346);
		contentPane.add(advanceOptionsPane);
		
		pdf_doc_options = new JPanel();
		advanceOptionsPane.addTab("PDF/Document Options", null, pdf_doc_options, null);
		pdf_doc_options.setLayout(null);
		
		chckbxPdfInput = new JCheckBox("PDF Input");
		chckbxPdfInput.setToolTipText("Change PDF DPI and Bits. This only works on PDF Files");
		chckbxPdfInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxPdfInput.isSelected() == true)
				{
					pdfInputdpiComboBox.setEnabled(true);
					pdfInputbitPerPixelComboBox.setEnabled(true);
				}
				else
				{
					pdfInputdpiComboBox.setEnabled(false);
					pdfInputbitPerPixelComboBox.setEnabled(false);
				}
			}
		});
		chckbxPdfInput.setBounds(0, 9, 77, 25);
		pdf_doc_options.add(chckbxPdfInput);
		
		dpiLabel = new JLabel("DPI");
		dpiLabel.setBounds(118, 13, 23, 16);
		pdf_doc_options.add(dpiLabel);
		pdfInputdpiComboBox = new JComboBox();
		pdfInputdpiComboBox.setEnabled(false);
		pdfInputdpiComboBox.setBounds(145, 9, 50, 22);
		//pdfInputbitPerPixelComboBox.setSelectedItem(null);
		
		
		pdf_doc_options.add(pdfInputdpiComboBox);
		
		bitPerPixelLabel = new JLabel("BPP");
		bitPerPixelLabel.setBounds(219, 13, 23, 16);
		pdf_doc_options.add(bitPerPixelLabel);
		pdfInputbitPerPixelComboBox = new JComboBox();
		pdfInputbitPerPixelComboBox.setEnabled(false);
		pdfInputbitPerPixelComboBox.setBounds(246, 9, 50, 22);
		//pdfInputbitPerPixelComboBox.setSelectedItem(null);
		
		pdf_doc_options.add(pdfInputbitPerPixelComboBox);
		
				chckbxPdfOuput = new JCheckBox("PDF Ouput");
				chckbxPdfOuput.setToolTipText("Set the PDF width and height");
				chckbxPdfOuput.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(chckbxPdfOuput.isSelected() == true)
						{
							wSpinner.setEnabled(true);
							hSpinner.setEnabled(true);
						}
						else
						{
							wSpinner.setEnabled(false);
							hSpinner.setEnabled(false);
						}
						
					}
				});
				chckbxPdfOuput.setBounds(0, 140, 77, 25);
				pdf_doc_options.add(chckbxPdfOuput);
				
				widthLabel = new JLabel("Width");
				widthLabel.setBounds(102, 144, 39, 16);
				pdf_doc_options.add(widthLabel);
				
				heightLabel = new JLabel("Height");
				heightLabel.setBounds(205, 144, 41, 16);
				pdf_doc_options.add(heightLabel);
				
				wSpinner = new JSpinner();
				wSpinner.setEnabled(false);
				wSpinner.setBounds(145, 140, 50, 19);
				pdf_doc_options.add(wSpinner);
				
				hSpinner = new JSpinner();
				hSpinner.setEnabled(false);
				hSpinner.setBounds(246, 142, 50, 19);
				pdf_doc_options.add(hSpinner);
				
				chckbxDocumentInput = new JCheckBox("Document Input");
				chckbxDocumentInput.setToolTipText("Set the document DIP and BITS for PDF,DOC,PCL,PPT,RT,XLS,AFP and MODCA formats");								
				chckbxDocumentInput.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(chckbxDocumentInput.isSelected() == true)
						{
							docInputDpiComboBox.setEnabled(true);
							docBitPerPixelComboBox.setEnabled(true);
							chckbxSavesearchable.setEnabled(true);
						}
						else
						{
							docInputDpiComboBox.setEnabled(false);
							docBitPerPixelComboBox.setEnabled(false);
							chckbxSavesearchable.setEnabled(false);
						}
						
					}
				});
				chckbxDocumentInput.setBounds(0, 37, 103, 25);
				pdf_doc_options.add(chckbxDocumentInput);
				
				docDPILabel = new JLabel("DPI");
				docDPILabel.setBounds(118, 42, 23, 16);
				pdf_doc_options.add(docDPILabel);
				docInputDpiComboBox = new JComboBox();
				docInputDpiComboBox.setEnabled(false);
				docInputDpiComboBox.setBounds(145, 39, 50, 22);
				//docInputDpiComboBox.setSelectedItem(null);
				
				pdf_doc_options.add(docInputDpiComboBox);
				
				DocbitPerPixelLabel = new JLabel("BPP");
				DocbitPerPixelLabel.setBounds(219, 42, 23, 16);
				pdf_doc_options.add(DocbitPerPixelLabel);
				docBitPerPixelComboBox = new JComboBox();
				docBitPerPixelComboBox.setEnabled(false);
				docBitPerPixelComboBox.setBounds(246, 39, 50, 22);
				//docBitPerPixelComboBox.setSelectedItem(null);
				pdf_doc_options.add(docBitPerPixelComboBox);
				
				chckbxSavesearchable = new JCheckBox("Save-searchable");
				chckbxSavesearchable.setEnabled(false);
				chckbxSavesearchable.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(chckbxSavesearchable.isSelected() == true)
						{
							chckbxSavesearchable.setSelected(true);
						}
						
					}
				});
				chckbxSavesearchable.setBounds(0, 73, 113, 23);
				pdf_doc_options.add(chckbxSavesearchable);
				
				chckbxPclInput = new JCheckBox("PCL Input");
				chckbxPclInput.setToolTipText("Set the dots per inch and the pixel depth for pcl files.");
				chckbxPclInput.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(chckbxPclInput.isSelected() == true)
						{
							pclDpiComboBox.setEnabled(true);
							pclBppComboBox.setEnabled(true);
						}
						else
						{
							pclDpiComboBox.setEnabled(false);
							pclBppComboBox.setEnabled(false);
						}
					}
				});
				chckbxPclInput.setBounds(0, 110, 77, 23);
				pdf_doc_options.add(chckbxPclInput);
				
				pclInputDpiLabel = new JLabel("DPI");
				pclInputDpiLabel.setBounds(118, 112, 23, 16);
				pdf_doc_options.add(pclInputDpiLabel);
				
				pclDpiComboBox = new JComboBox();
				pclDpiComboBox.setEnabled(false);
				pclDpiComboBox.setBounds(145, 110, 50, 22);
				pdf_doc_options.add(pclDpiComboBox);
				
				pclBppComboBox = new JComboBox();
				pclBppComboBox.setEnabled(false);
				pclBppComboBox.setBounds(246, 110, 50, 22);
				pdf_doc_options.add(pclBppComboBox);
				
				pclBppLabel = new JLabel("BPP");
				pclBppLabel.setBounds(219, 113, 23, 16);
				pdf_doc_options.add(pclBppLabel);
				
				
				chckbxExtractPdf = new JCheckBox("Extract vector-page PDF");
				chckbxExtractPdf.setToolTipText("Extracts page from a multi-page PDF or TIFF document");
				chckbxExtractPdf.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if(chckbxExtractPdf.isSelected() == true)
						{
							chckbxExtractPdf.setEnabled(true);
							return;				
						}
					}
				});
				chckbxExtractPdf.setBounds(0, 172, 147, 23);
				pdf_doc_options.add(chckbxExtractPdf);
				
				chckbxExtractText = new JCheckBox("Extract text");
				chckbxExtractText.setToolTipText("Extracts text from PTOCA, PCL, PDF, MS Word, AFP, RTF and MS Excel files");
				chckbxExtractText.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						if(chckbxExtractText.isSelected() == true)
						{
							chckbxExtractText.setEnabled(true);
							return;				
						}
					}
				});
				chckbxExtractText.setBounds(0, 198, 87, 23);
				pdf_doc_options.add(chckbxExtractText);
				
				chckbxAfpFontmapping = new JCheckBox("AFP font-map");
				chckbxAfpFontmapping.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(chckbxAfpFontmapping.isSelected() == true)
						{
							chckbxAfpFontmapping.setSelected(true);
							afpFontmapTextField.setEnabled(true);
							afpFontmapBtn.setEnabled(true);
								
						}
						else
						{
							afpFontmapTextField.setEnabled(false);
							afpFontmapBtn.setEnabled(false);
						}
					}
				});
				chckbxAfpFontmapping.setBounds(0, 224, 91, 23);
				pdf_doc_options.add(chckbxAfpFontmapping);
				
				afpFontmapTextField = new JTextField();
				afpFontmapTextField.setEnabled(false);
				afpFontmapTextField.setBounds(105, 225, 113, 20);
				afpFontmapTextField.addFocusListener(new FocusListener()
				{
					public void focusLost(FocusEvent e)
					{
						mAfpFontmapDirectoryPath = afpFontmapTextField.getText();
					}
					public void focusGained(FocusEvent e)
					{
						//intentionally left blank
					}
				});
				pdf_doc_options.add(afpFontmapTextField);
				afpFontmapTextField.setColumns(10);

				afpFontmapBtn = new JButton("Browse");
				afpFontmapBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {											       
						mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.name")));				
						mFileBrowser.setDialogTitle("Select AFP Fontmap File");
						mFileBrowser.setFileFilter(filterFile);
						mFileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);

						int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
						if (status == JFileChooser.APPROVE_OPTION)
						{
							afpFontmapTextField.setText(mFileBrowser.getSelectedFile().getName());			
							mAfpFontmapDirectoryPath = mFileBrowser.getSelectedFile().getPath() + ".fnt";

						}
					}
				});
				afpFontmapBtn.setEnabled(false);
				afpFontmapBtn.setBounds(224, 224, 67, 23);
				pdf_doc_options.add(afpFontmapBtn);
					
		imgManipulations = new JPanel();
		advanceOptionsPane.addTab("Image Manipulations", null, imgManipulations, null);
		imgManipulations.setLayout(null);
		
		chckbxImage = new JCheckBox("Image Settings");
		chckbxImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(chckbxImage.isSelected() == true)
				{
					imgWidthSpinner.setEnabled(true);
					imgHeightSpinner.setEnabled(true);
				}
				else
				{
					imgWidthSpinner.setEnabled(false);
					imgHeightSpinner.setEnabled(false);
				}
				
			}
		});
		chckbxImage.setBounds(0, 0, 104, 25);
		imgManipulations.add(chckbxImage);
		
		imgWidthSpinner = new JSpinner();
		imgWidthSpinner.setEnabled(false);
		imgWidthSpinner.setBounds(147, 4, 50, 19);
		imgManipulations.add(imgWidthSpinner);
		
		imgHeightLabel = new JLabel("Height");
		imgHeightLabel.setBounds(209, 7, 44, 16);
		imgManipulations.add(imgHeightLabel);
		
		imgHeightSpinner = new JSpinner();
		imgHeightSpinner.setEnabled(false);
		imgHeightSpinner.setBounds(254, 6, 50, 19);
		imgManipulations.add(imgHeightSpinner);
		
		imgWidthLabel = new JLabel("Width");
		imgWidthLabel.setBounds(110, 5, 42, 16);
		imgManipulations.add(imgWidthLabel);
		
		chckbxbitGrayscale = new JCheckBox("8-Bit GrayScale");
		chckbxbitGrayscale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxbitGrayscale.isSelected() == true)
				{
					chckbxbitGrayscale.setEnabled(true);
				}
			}
		});
		chckbxbitGrayscale.setBounds(200, 142, 104, 25);
		imgManipulations.add(chckbxbitGrayscale);
		
		chckbxPromotebit = new JCheckBox("Promote 24-bit");
		chckbxPromotebit.setToolTipText("This option permanently converts the current 1, 4 or 8-bit image to 24-bit");
		chckbxPromotebit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxPromotebit.isSelected() == true)
				{
					chckbxPromotebit.setEnabled(true);
				}
			}
		});
		chckbxPromotebit.setBounds(0, 144, 105, 25);
		imgManipulations.add(chckbxPromotebit);
		
		chckbxImprovebitGrayscale = new JCheckBox("Improve 8-bit GrayScale");
		chckbxImprovebitGrayscale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxImprovebitGrayscale.isSelected() == true)
				{
					chckbxImprovebitGrayscale.setEnabled(true);
				}
			}
		});
		chckbxImprovebitGrayscale.setBounds(0, 174, 141, 25);
		imgManipulations.add(chckbxImprovebitGrayscale);
		
		chckbxRgbToCymk = new JCheckBox("RGB to CMYK Data");
		chckbxRgbToCymk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxRgbToCymk.isSelected() == true)
				{
					chckbxRgbToCymk.setEnabled(true);
				}
			}
		});
		chckbxRgbToCymk.setBounds(0, 204, 128, 25);
		imgManipulations.add(chckbxRgbToCymk);
		
		chckbxEnableFastConversion = new JCheckBox("Enable Fast Conversion");
		chckbxEnableFastConversion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxEnableFastConversion.isSelected() == true)
				{
					chckbxEnableFastConversion.setEnabled(true);
				}
			}
		});
		chckbxEnableFastConversion.setBounds(0, 232, 153, 25);
		imgManipulations.add(chckbxEnableFastConversion);
		
		chckbxJpegCompression = new JCheckBox("JPEG Compression");
		chckbxJpegCompression.setToolTipText("Sets the compression quality factor when saving JPEG images. 0-100");
		chckbxJpegCompression.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxJpegCompression.isSelected() == true)
				{
					jpegCompSpinner.setEnabled(true);
					h_InterleaveComboBox.setEnabled(true);
					v_InterleaveComboBox.setEnabled(true);
					h_InterleaveLabel.setEnabled(true);
					v_InterleaveLabel.setEnabled(true);
				}
				else
				{
					jpegCompSpinner.setEnabled(false);
					h_InterleaveComboBox.setEnabled(false);
					v_InterleaveComboBox.setEnabled(false);
					h_InterleaveLabel.setEnabled(false);
					v_InterleaveLabel.setEnabled(false);
				}
			}
		});
		chckbxJpegCompression.setBounds(0, 33, 113, 19);
		imgManipulations.add(chckbxJpegCompression);
		
		jpegCompSpinner = new JSpinner();
		jpegCompSpinner.setEnabled(false);
		jpegCompSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		jpegCompSpinner.setBounds(115, 32, 42, 20);
		imgManipulations.add(jpegCompSpinner);
		
		/*jpegCompComboBox = new JComboBox();
		jpegCompComboBox.setEnabled(false);
		jpegCompComboBox.setBounds(204, 109, 44, 20);
		//jpegCompComboBox.setSelectedItem(null);
		imgManipulations.add(jpegCompComboBox);*/
		
		chckbxDespeckle = new JCheckBox("Despeckle");
		chckbxDespeckle.setToolTipText("This option removes noise from 1-bit images. Only works with 1-bit images");
		chckbxDespeckle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxDespeckle.isSelected() == true)
				{
					despeckleComboBox.setEnabled(true);
				}
				else
				{
					despeckleComboBox.setEnabled(false);
				}
			}
		});
		chckbxDespeckle.setBounds(167, 31, 73, 23);
		imgManipulations.add(chckbxDespeckle);

		despeckleComboBox = new JComboBox();
		despeckleComboBox.setEnabled(false);
		despeckleComboBox.setBounds(254, 34, 51, 22);
		imgManipulations.add(despeckleComboBox);

		chckbxAntiqueEffect = new JCheckBox("Antique Effect");
		chckbxAntiqueEffect.setToolTipText("This option gives a redish brown monochrome tints. Only works with 24-bit images");
		chckbxAntiqueEffect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxAntiqueEffect.isSelected() == true)
				{
					chckbxAntiqueEffect.setSelected(true);
				}						
			}
		});
		chckbxAntiqueEffect.setBounds(200, 172, 95, 23);
		imgManipulations.add(chckbxAntiqueEffect);

		chckbxInvert = new JCheckBox("Invert");
		chckbxInvert.setToolTipText("This option changes black to white and white to black");
		chckbxInvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxInvert.isSelected() == true)
				{
					chckbxInvert.setSelected(true);
				}						
			}
		});
		chckbxInvert.setBounds(200, 232, 81, 23);
		imgManipulations.add(chckbxInvert);


		chckbxPromoteEightBit = new JCheckBox("Promote 8-bit");
		chckbxPromoteEightBit.setToolTipText("This option permanently converts the current 1, 4, 16 or 24-bit image to 8-bit");
		chckbxPromoteEightBit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxPromoteEightBit.isSelected() == true)
				{
					chckbxPromoteEightBit.setSelected(true);
				}						
			}
		});
		chckbxPromoteEightBit.setBounds(200, 115, 95, 23);
		imgManipulations.add(chckbxPromoteEightBit);
		
		chckbxDuffuion = new JCheckBox("Diffusion Mono");
		chckbxDuffuion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxDuffuion.isSelected() == true)
				{
					chckbxDuffuion.setSelected(true);
				}
			}
		});
		chckbxDuffuion.setBounds(200, 202, 97, 23);
		imgManipulations.add(chckbxDuffuion);
			
		advancePdf = new JPanel();
		advanceOptionsPane.addTab("Advance PDF", null, advancePdf, null);
		advancePdf.setLayout(null);
		
		sourcePdfLabel = new JLabel("Source file");
		sourcePdfLabel.setBounds(10, 42, 60, 23);
		advancePdf.add(sourcePdfLabel);
		
		sourcepdfTextfield = new JTextField();
		sourcepdfTextfield.setEnabled(false);
		sourcepdfTextfield.setBounds(69, 43, 152, 20);
		sourcepdfTextfield.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				mSourcepdfPath = sourcepdfTextfield.getText();
			}
			public void focusGained(FocusEvent e)
			{
				//intentionally left blank
			}
		});
		advancePdf.add(sourcepdfTextfield);
		sourcepdfTextfield.setColumns(10);
		
		sourceBtn = new JButton("Source");
		sourceBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.name")));
				mFileBrowser.setDialogTitle("Select source file");
				mFileBrowser.setFileFilter(pdfTiffFilter);
				mFileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION)
				{
					sourcepdfTextfield.setText(mFileBrowser.getSelectedFile().getName());	
							
					mSourcepdfPath = mFileBrowser.getSelectedFile().toString();
							//.toString();
				}
			}
		});
		sourceBtn.setEnabled(false);
		sourceBtn.setBounds(231, 42, 75, 23);
		advancePdf.add(sourceBtn);
		
		targetpdfLabel = new JLabel("Target file");
		targetpdfLabel.setBounds(10, 84, 60, 23);
		advancePdf.add(targetpdfLabel);
		
		targetTextfield = new JTextField();
		targetTextfield.setEnabled(false);
		targetTextfield.setColumns(10);
		targetTextfield.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				mTargetpdfPath = targetTextfield.getText();
			}
			public void focusGained(FocusEvent e)
			{
				//intentionally left blank
			}
		});
		
		targetTextfield.setBounds(69, 85, 152, 20);
		advancePdf.add(targetTextfield);
		
		targetBtn = new JButton("Target");
		targetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.name")));
				mFileBrowser.setDialogTitle("Select target file");
				mFileBrowser.setFileFilter(pdfTiffFilter);
				mFileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION)
				{
					targetTextfield.setText(mFileBrowser.getSelectedFile().getName());
					mTargetpdfPath = mFileBrowser.getSelectedFile().toString();
							//.toString();
				}
			}
		});
		targetBtn.setEnabled(false);
		targetBtn.setBounds(231, 84, 75, 23);
		advancePdf.add(targetBtn);
		
		mergeDoc_infoLabel = new JLabel("Targe file will be appended to Source file");
		mergeDoc_infoLabel.setForeground(new Color(205, 92, 92));
		mergeDoc_infoLabel.setFont(mergeDoc_infoLabel.getFont().deriveFont(11f));
		mergeDoc_infoLabel.setBounds(10, 124, 211, 28);
		advancePdf.add(mergeDoc_infoLabel);
		
		btnMergeDoc = new JButton("Append");
		btnMergeDoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxMergePdf.isSelected() == true)
				{
					error[0] = 0;
					Thread workerThread = new Thread(new Runnable(){
						@Override
						public void run() {
							if(mSourcepdfPath != null && mTargetpdfPath != null || sourcepdfTextfield.getText().length() < 0 || targetTextfield.getText().length() < 0)
							{
								setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								appendVectorPage(s, vectorPdfPageIndex,error);
								appendNonVectorPage(s, vectorPdfPageIndex,bitMapPageIndex,error);
								System.out.println("Done");
								setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								JOptionPane.showMessageDialog(mBatchConvertFrame, "Done", "Append Document", JOptionPane.OK_OPTION);
							}
							else
							{
								JOptionPane.showMessageDialog(mBatchConvertFrame, "Specify source or target file", "Append Document", JOptionPane.OK_OPTION);
							}
							
						}			
					});
					workerThread.start();
				}
			}
		});
		btnMergeDoc.setBounds(231, 122, 75, 33);
		advancePdf.add(btnMergeDoc);
		
		chckbxMergePdf = new JCheckBox("Merge Document");
		chckbxMergePdf.setToolTipText("This option appends pages to an existing multi-page PDF or TIFF document");
		chckbxMergePdf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxMergePdf.isSelected() == true)
				{
					sourcepdfTextfield.setEnabled(true);
					//chckbxListSelections.setSelected(false);
					//chckbxBatchConversion.setSelected(false);
					sourceBtn.setEnabled(true);
					targetTextfield.setEnabled(true);
					targetBtn.setEnabled(true);
				}
				else
				{
					sourcepdfTextfield.setEnabled(false);
					sourceBtn.setEnabled(false);
					targetTextfield.setEnabled(false);
					targetBtn.setEnabled(false);
				}
			}
		});
		chckbxMergePdf.setBounds(0, 7, 107, 23);
		advancePdf.add(chckbxMergePdf);
		
		brightness_contrast_Panel = new JPanel();
		advanceOptionsPane.addTab("Picture Controls", null, brightness_contrast_Panel, null);
		brightness_contrast_Panel.setLayout(null);
		
		brightnessPanel = new JPanel();
		brightnessPanel.setBorder(new TitledBorder(null, "Brightness", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		brightnessPanel.setBounds(0, 11, 296, 112);
		brightness_contrast_Panel.add(brightnessPanel);
		brightnessPanel.setLayout(null);
		
		rdbtnBrightness_increase = new JRadioButton("Increase");
		rdbtnBrightness_increase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(rdbtnBrightness_increase.isSelected() == true)
				{
					rdbtnBrightness_increase.setSelected(true);
					brightnessIncreaseSlid.setEnabled(true);
					rdbtnBrightness_decrease.setSelected(false);
					brightnessDecreaseSlid.setEnabled(false);
					rdbtnBrightness_noChange.setSelected(false);
				}
			}
		});
		rdbtnBrightness_increase.setBounds(6, 18, 67, 23);
		brightnessPanel.add(rdbtnBrightness_increase);
		
		rdbtnBrightness_decrease = new JRadioButton("Decrease");
		rdbtnBrightness_decrease.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnBrightness_decrease.isSelected() == true)
				{
					rdbtnBrightness_decrease.setSelected(true);
					brightnessDecreaseSlid.setEnabled(true);
					rdbtnBrightness_increase.setSelected(false);
					brightnessIncreaseSlid.setEnabled(false);
					rdbtnBrightness_noChange.setSelected(false);
				}
			}
		});
		rdbtnBrightness_decrease.setBounds(6, 82, 72, 23);
		brightnessPanel.add(rdbtnBrightness_decrease);
		
		brightnessIncreaseSlid = new JSlider();
		brightnessIncreaseSlid.setValue(0);
		brightnessIncreaseSlid.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				brightnessIncreaseSlid = (JSlider) arg0.getSource();
			}
		});
		brightnessIncreaseSlid.setEnabled(false);
		brightnessIncreaseSlid.setMinorTickSpacing(5);
		brightnessIncreaseSlid.setMajorTickSpacing(25);
		brightnessIncreaseSlid.setSnapToTicks(true);
		brightnessIncreaseSlid.setPaintTicks(true);
		brightnessIncreaseSlid.setPaintLabels(true);
		brightnessIncreaseSlid.setMaximum(127);
		brightnessIncreaseSlid.setBounds(84, 11, 202, 45);
		brightnessPanel.add(brightnessIncreaseSlid);
		
		brightnessDecreaseSlid = new JSlider();
		brightnessDecreaseSlid.setMinimum(-125);
		brightnessDecreaseSlid.setValue(0);
		brightnessDecreaseSlid.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				brightnessDecreaseSlid = (JSlider) e.getSource();
			}
		});
		brightnessDecreaseSlid.setEnabled(false);
		brightnessDecreaseSlid.setSnapToTicks(true);
		brightnessDecreaseSlid.setPaintTicks(true);
		brightnessDecreaseSlid.setPaintLabels(true);
		brightnessDecreaseSlid.setMinorTickSpacing(5);
		brightnessDecreaseSlid.setMaximum(0);
		brightnessDecreaseSlid.setMajorTickSpacing(25);
		brightnessDecreaseSlid.setBounds(84, 59, 202, 46);
		brightnessPanel.add(brightnessDecreaseSlid);
		
		rdbtnBrightness_noChange = new JRadioButton("No Change");
		rdbtnBrightness_noChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnBrightness_noChange.isSelected() == true)
				{
					rdbtnBrightness_noChange.setSelected(true);
					brightnessDecreaseSlid.setEnabled(false);
					brightnessIncreaseSlid.setEnabled(false);
					rdbtnBrightness_increase.setSelected(false);
					rdbtnBrightness_decrease.setSelected(false);
					
				}
			}
		});
		rdbtnBrightness_noChange.setBounds(6, 48, 79, 23);
		brightnessPanel.add(rdbtnBrightness_noChange);
		
		contrastPanel = new JPanel();
		contrastPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Contrast", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
		contrastPanel.setBounds(0, 122, 296, 125);
		brightness_contrast_Panel.add(contrastPanel);
		contrastPanel.setLayout(null);
		
		radioContrast_increase = new JRadioButton("Increase");
		radioContrast_increase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(radioContrast_increase.isSelected() == true)
				{
					radioContrast_increase.setSelected(true);
					contrastIncreaseSlid.setEnabled(true);
					radioContrast_noChange.setSelected(false);
					radioContrast_decrease.setSelected(false);
					contrastDecreaseSlid.setEnabled(false);
					
				}
			}
		});
		radioContrast_increase.setBounds(6, 18, 67, 23);
		contrastPanel.add(radioContrast_increase);
		
		radioContrast_decrease = new JRadioButton("Decrease");
		radioContrast_decrease.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(radioContrast_decrease.isSelected() == true)
				{
					radioContrast_decrease.setSelected(true);
					contrastDecreaseSlid.setEnabled(true);
					radioContrast_noChange.setSelected(false);
					radioContrast_increase.setSelected(false);
					contrastIncreaseSlid.setEnabled(false);
				}
			}
		});
		radioContrast_decrease.setBounds(6, 95, 78, 23);
		contrastPanel.add(radioContrast_decrease);
		
		contrastIncreaseSlid = new JSlider();
		contrastIncreaseSlid.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				contrastIncreaseSlid = (JSlider) e.getSource();
				
			}
		});
		contrastIncreaseSlid.setValue(0);
		contrastIncreaseSlid.setEnabled(false);
		contrastIncreaseSlid.setSnapToTicks(true);
		contrastIncreaseSlid.setPaintTicks(true);
		contrastIncreaseSlid.setPaintLabels(true);
		contrastIncreaseSlid.setMinorTickSpacing(5);
		contrastIncreaseSlid.setMaximum(127);
		contrastIncreaseSlid.setMajorTickSpacing(25);
		contrastIncreaseSlid.setBounds(81, 11, 205, 45);
		contrastPanel.add(contrastIncreaseSlid);
		
		contrastDecreaseSlid = new JSlider();
		contrastDecreaseSlid.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				contrastDecreaseSlid = (JSlider) e.getSource();
			}
		});
		contrastDecreaseSlid.setValue(0);
		contrastDecreaseSlid.setEnabled(false);
		contrastDecreaseSlid.setSnapToTicks(true);
		contrastDecreaseSlid.setPaintTicks(true);
		contrastDecreaseSlid.setPaintLabels(true);
		contrastDecreaseSlid.setMinorTickSpacing(5);
		contrastDecreaseSlid.setMinimum(-125);
		contrastDecreaseSlid.setMaximum(0);
		contrastDecreaseSlid.setMajorTickSpacing(25);
		contrastDecreaseSlid.setBounds(81, 67, 205, 46);
		contrastPanel.add(contrastDecreaseSlid);
		
		radioContrast_noChange = new JRadioButton("No Change");
		radioContrast_noChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(radioContrast_noChange.isSelected() == true)
				{
					radioContrast_noChange.setSelected(true);
					radioContrast_decrease.setSelected(false);
					radioContrast_increase.setSelected(false);
					contrastIncreaseSlid.setEnabled(false);
					contrastDecreaseSlid.setEnabled(false);
					
				}
			}
		});
		radioContrast_noChange.setBounds(5, 54, 79, 23);
		contrastPanel.add(radioContrast_noChange);
		
		String[] numDpiValues = {"100", "200", "300", "400"};
		for(int i = 0; i < numDpiValues.length; i++)
		{
			pdfInputdpiComboBox.addItem(new Integer(numDpiValues[i]));
		}
		pdfInputdpiComboBox.setSelectedItem(null);
		
		String[] numbppValues = {"1", "8", "24"};
		for(int i = 0; i < numbppValues.length; i++)
		{
			pdfInputbitPerPixelComboBox.addItem(new Integer(numbppValues[i]));
		}
		pdfInputbitPerPixelComboBox.setSelectedItem(null);
		
		String[] docDpiNumItems = {"100", "200", "300", "400"};
		for(int i = 0; i < docDpiNumItems.length; i++)
		{
			docInputDpiComboBox.addItem(new Integer(docDpiNumItems[i])); // add String items as Integer to ComboBox
		}
		docInputDpiComboBox.setSelectedItem(null);
		
		String[] docBppnumValues = {"1", "8", "24"};
		for(int i = 0; i < docBppnumValues.length; i++)
		{
			docBitPerPixelComboBox.addItem(new Integer(docBppnumValues[i]));
		}
		docBitPerPixelComboBox.setSelectedItem(null);
								
		/*String[] jpegCompNumVal = {"1", "100"};
		for(int i = 0; i < jpegCompNumVal.length; i++)
		{
			jpegCompComboBox.addItem(new Integer(jpegCompNumVal[i]));
		}
		jpegCompComboBox.setSelectedItem(null);*/
		
		String[] despeckleNumVal = {"1", "30", "100"};
		for(int i = 0; i < despeckleNumVal.length; i++)
		{
			despeckleComboBox.addItem(new Integer(despeckleNumVal[i]));
		}
		despeckleComboBox.setSelectedItem(null);
		
		h_InterleaveLabel = new JLabel("H_Interleave");
		h_InterleaveLabel.setEnabled(false);
		h_InterleaveLabel.setBounds(5, 59, 85, 19);
		imgManipulations.add(h_InterleaveLabel);
		
		String[] h_InterleaveNum = {"1", "2","3","4"};
		h_InterleaveComboBox = new JComboBox();
		for(int i = 0; i < h_InterleaveNum.length; i++)
		{
			h_InterleaveComboBox.addItem(new Integer(h_InterleaveNum[i]));
		}
		h_InterleaveComboBox.setEnabled(false);
		h_InterleaveComboBox.setBounds(110, 59, 51, 22);
		h_InterleaveComboBox.setSelectedItem(null);
		imgManipulations.add(h_InterleaveComboBox);
		
		v_InterleaveLabel = new JLabel("V_Interleave");
		v_InterleaveLabel.setEnabled(false);
		v_InterleaveLabel.setBounds(5, 89, 73, 19);
		imgManipulations.add(v_InterleaveLabel);
		
		String[] v_InterleaveNum = {"1", "2","3","4"};
		v_InterleaveComboBox = new JComboBox();
		for(int i = 0; i < v_InterleaveNum.length; i++)
		{
			v_InterleaveComboBox.addItem(new Integer(v_InterleaveNum[i]));
		}
		v_InterleaveComboBox.setEnabled(false);
		v_InterleaveComboBox.setBounds(110, 85, 51, 22);
		v_InterleaveComboBox.setSelectedItem(null);
		imgManipulations.add(v_InterleaveComboBox);
		
		chckbxAntialiasing = new JCheckBox("Anti-Aliasing");
		chckbxAntialiasing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxAntialiasing.isSelected() == true)
				{
					aliasingComboBox.setEnabled(true);
					aliasingQualityLabel.setEnabled(true);
					aliasQualityComboBox.setEnabled(true);
				}
				else
				{
					aliasingComboBox.setEnabled(false);
					aliasingQualityLabel.setEnabled(false);
					aliasQualityComboBox.setEnabled(false);
				}
			}
		});
		chckbxAntialiasing.setBounds(167, 57, 85, 25);
		imgManipulations.add(chckbxAntialiasing);
		aliasingComboBox = new JComboBox();
		aliasingComboBox.setBounds(254, 58, 51, 22);
		imgManipulations.add(aliasingComboBox);
		aliasingComboBox.setEnabled(false);
		
		aliasingQualityLabel = new JLabel("Aliasing Quality");
		aliasingQualityLabel.setEnabled(false);
		aliasingQualityLabel.setBounds(172, 85, 78, 23);
		imgManipulations.add(aliasingQualityLabel);
		
		aliasQualityComboBox = new JComboBox();
		aliasQualityComboBox.setBounds(254, 82, 51, 22);
		imgManipulations.add(aliasQualityComboBox);
		aliasQualityComboBox.setEnabled(false);
		
		String[] aliasingQualityNumVal = {"1", "100"};
		for(int i = 0; i < aliasingQualityNumVal.length; i++)
		{
			aliasQualityComboBox.addItem(new Integer(aliasingQualityNumVal[i]));
		}
		aliasQualityComboBox.setSelectedItem(null);
		
		String[] aliasingNumVal = {"0", "1", "2", "3", "4"};
		for(int i = 0; i < aliasingNumVal.length; i++)
		{
			aliasingComboBox.addItem(new Integer(aliasingNumVal[i]));
		}
		aliasingComboBox.setSelectedItem(null);
		
		chckbxImgThreshold = new JCheckBox("Image Threshold");
		chckbxImgThreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxImgThreshold.isSelected() == true)
				{
					thresholdSpinner.setEnabled(true);
				}
				else
				{
					thresholdSpinner.setEnabled(false);
				}
			}
		});
		chckbxImgThreshold.setBounds(0, 118, 113, 23);
		imgManipulations.add(chckbxImgThreshold);
		
		thresholdSpinner = new JSpinner();
		thresholdSpinner.setEnabled(false);
		thresholdSpinner.setModel(new SpinnerNumberModel(128, 0, 255, 1));
		thresholdSpinner.setBounds(115, 116, 51, 22);
		imgManipulations.add(thresholdSpinner);
		
		String[] pclDpiVal = {"100","200","300","400"};
		for(int i = 0; i < pclDpiVal.length; i++)
		{
			pclDpiComboBox.addItem(new Integer(pclDpiVal[i]));
		}
		pclDpiComboBox.setSelectedItem(null);
		
		String[] pclBppVal = {"1","24"};
		for(int i = 0; i < pclBppVal.length; i++)
		{
			pclBppComboBox.addItem(new Integer(pclBppVal[i]));
		}
		pclBppComboBox.setSelectedItem(null);
		
		addButn = new JButton("Add");
		addButn.setEnabled(false);
		listModel = new DefaultListModel<File>();
		list = new JList<File>(listModel);
		list.setEnabled(false);
		list.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "List items", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
		scrollPane = new JScrollPane();
		scrollPane.setBounds(3, 65, 226, 340);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(list);	
		addButn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mFileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				mFileBrowser.setMultiSelectionEnabled(true);
				mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.name")));
				mFileBrowser.setDialogTitle("Select a File");
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if(status == JFileChooser.APPROVE_OPTION)
				{
					//int insert = list.getModel().getSize();
					for (File file : mFileBrowser.getSelectedFiles())
					{
						listModel.addElement(file);
					}
					//listModel.add(insert, mFileBrowser.getSelectedFile());
					//System.out.println("LisetModel" + listModel);
					
					list.updateUI();
				}
			}
		});
		contentPane.add(scrollPane);
		
		addButn.setBounds(230, 65, 93, 33);
		contentPane.add(addButn);
		
		removeBtn = new JButton("Remove");
		removeBtn.setEnabled(false);
		removeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//remove();
				listModel = (DefaultListModel) list.getModel();
				int selectedIndex = list.getSelectedIndex();
				if (selectedIndex != -1) {
				    listModel.remove(selectedIndex);
				}
				list.updateUI();
				
				
			}
		});
		removeBtn.setBounds(230, 99, 93, 33);
		contentPane.add(removeBtn);
		
		removeAllBtn = new JButton("Remove All");
		removeAllBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listModel.clear();
				list.updateUI();
			}
		});
		removeAllBtn.setEnabled(false);
		removeAllBtn.setBounds(230, 133, 93, 33);
		contentPane.add(removeAllBtn);
		
		moveUpBtn = new JButton("Up");
		moveUpBtn.setEnabled(false);
		moveUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		moveUpBtn.setBounds(230, 168, 93, 33);
		contentPane.add(moveUpBtn);
		
		downBtn = new JButton("Down");
		downBtn.setEnabled(false);
		downBtn.setBounds(230, 203, 93, 33);
		contentPane.add(downBtn);
		
		mPreviewPanel = new PreviewPanel();
		mPreviewBorder = new JPanel();
		mPreviewBorder.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		//mPreviewPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		mPreviewBorder.setBounds(326, 533, 399, 205);
		//mPreviewPanel.setBounds(230, 544, 495, 194);
		//mPreviewBorder.setBackground(newColor);
		//mPreviewPanel.setBackground(Color.GRAY);
		//mPreviewPanel.setBounds(230, 544, 495, 194);
		mPreviewPanel.setPreferredSize(new Dimension(391,202));;
		mPreviewBorder.add(mPreviewPanel);
		//contentPane.add(mPreviewPanel);
		contentPane.add(mPreviewBorder);
		
		
		convBtn = new JButton("Start");
		convBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ExecutorService executorThread = Executors.newFixedThreadPool(3);
				if(chckbxBatchConversion.isEnabled() && chckbxBatchConversion.isSelected() )
				{
					if(batchComboBox.getSelectedItem() != null)
					{
						if (mInputDirectoryPath != null && mOutputDirectoryPath != null)
		                {					
		                   /* File inputDir = new File(mInputDirectoryPath);
		                    File outputDir = new File(mOutputDirectoryPath);*/	                   		                    		              	                    	              
							doInputWork = new ConvertInputdocuments();
		                    executorThread.execute(doInputWork);
		                   
		                    try{
		                    	Thread.sleep(1200);
		                    }
		                    catch(InterruptedException ex){
		                    	ex.printStackTrace();
		                    }
		                    executorThread.shutdown();
		                    //doInputWork.execute();
		                   
		                }
						else
						{
							JOptionPane.showMessageDialog(mBatchConvertFrame, "Directory paths not specified", "Specify directory", JOptionPane.OK_OPTION);
							System.out
			                .println("One or more directory paths not specified.");
			            System.out.println("InputDirectory: " + mInputDirectoryPath);		               
			            System.out.println("OutputDirectory: " + mOutputDirectoryPath);               
						}
						
					}			
					else
					{
						
						JOptionPane.showMessageDialog(mBatchConvertFrame, "Please select an output type to convert", "Choose output", JOptionPane.OK_OPTION);
	                }
				}				
									
				///List c starts here
				
				else if (chckbxListSelections.isEnabled() && chckbxListSelections.isSelected())
                {
					if(mListOutputDirectoryPath != null)
					{
						if(batchComboBox.getSelectedItem() != null)
						{
							//File inputDir = new File(mInputDirectoryPath);
							//File outputDir = new File(mListOutputDirectoryPath);
							
							doListInputWork = new ListInputDocumentConvert();
							
							//ExecutorService executor = Executors.newCachedThreadPool();
						    //List<Future<Long>> list = new ArrayList<Future<Long>>();				  
						    //Future<Long> submit = (Future<Long>) executorThread.submit(doListInputWork);
						    //list.add(submit);	
							executorThread.execute(doListInputWork);
						    try{
		                    	Thread.sleep(1500);	                    	
		                    }
		                    catch(InterruptedException ex){
		                    	ex.printStackTrace();
		                    }
						  
						    executorThread.shutdown();
							//doListInputWork.execute();													
						}
						else
						{
							JOptionPane.showMessageDialog(mBatchConvertFrame, "Please select an output type to convert", "Choose output", JOptionPane.OK_OPTION);
						}
						
					}					
					else
					{
						JOptionPane.showMessageDialog(mBatchConvertFrame, "Directory paths not specified", "Specify directory", JOptionPane.OK_OPTION);
						System.out
		                .println("One or more directory paths not specified.");    
						System.out.println(" OutputDirectory: "
		                + mOutputDirectoryPath);
					}																				               
                }			
				else
				{
					JOptionPane.showMessageDialog(mBatchConvertFrame, "Please select conversion method", "Choose conversion type", JOptionPane.OK_OPTION);
				}
				
			}
		});
		convBtn.setBounds(230, 239, 93, 33);
		contentPane.add(convBtn);
		
		pauseBtn = new JButton("Pause");
		pauseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doInputWork = new ConvertInputdocuments();
				doListInputWork = new ListInputDocumentConvert();
				gDone = doInputWork!=null && !doInputWork.isCancelled() && !gDone;
				isPaused = doListInputWork != null && !doListInputWork.isCancelled() && !isPaused;
				((JButton)arg0.getSource()).setText(gDone ? "Resume" : "Pause");
				((JButton)arg0.getSource()).setText(isPaused ? "Resume" : "Pause");
				//gDone = true;
				//convBtn.setEnabled(true);
				//pauseBtn.setEnabled(true);
				//btnLogfile.setEnabled(true);
				//mProgressBar.setEnabled(false);
				//totalPageCount = 0;
				//counter = 0;
			}
		});
		pauseBtn.setBounds(230, 276, 93, 33);
		contentPane.add(pauseBtn);
		
		btnLogfile = new JButton("LogFile");
		btnLogfile.setEnabled(false);
		btnLogfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Desktop desk = Desktop.getDesktop();
				File file = new File(errorFile.getAbsolutePath());
				if(!file.isFile())
					System.out.println("File has not been created yet!");
				else if(file.exists())
				{
					//Check for supported desktop type return
					//if current desktop is not supported
					if(!Desktop.isDesktopSupported())
					{
						return;
					}
					if(!desk.isSupported(Desktop.Action.OPEN))
					{
						return;
					}
					try {
						Desktop.getDesktop().open(file);//open the log file
					} catch (IOException ex) {						
						System.out.println(" File was not created" + ex.getMessage());
					}
					return;
				}

			}
		});
		btnLogfile.setBounds(230, 355, 93, 33);
		contentPane.add(btnLogfile);
		
		directoryPanel = new JPanel();
		directoryPanel.setBorder(new TitledBorder(null, "Directory Settings", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		directoryPanel.setBounds(326, 158, 399, 128);
		contentPane.add(directoryPanel);
		directoryPanel.setLayout(null);
		
		inputDirLabel = new JLabel("Input Directory:");
		inputDirLabel.setBounds(12, 26, 89, 16);
		directoryPanel.add(inputDirLabel);
		
		outputDirLabel = new JLabel("Output Directory:");
		outputDirLabel.setBounds(12, 64, 103, 16);
		directoryPanel.add(outputDirLabel);
		
		inputTextField = new JTextField("Specify input directory to process...");
		inputTextField.setBounds(105, 23, 194, 22);
		inputTextField.setColumns(10);
		inputTextField.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				mInputDirectoryPath = inputTextField.getText();
			}
			public void focusGained(FocusEvent e)
			{
				//intentionally left blank
			}
		});
		directoryPanel.add(inputTextField);	
		
		ouptutTextField = new JTextField("Specify output directory to convert to...");
		ouptutTextField.setColumns(10);
		ouptutTextField.setBounds(105, 61, 194, 22);
		ouptutTextField.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				mOutputDirectoryPath = ouptutTextField.getText();
			}

			public void focusGained(FocusEvent e)
			{
				//intentionally left blank
			}
		});
		directoryPanel.add(ouptutTextField);
		
		inputBrowseBtn = new JButton("Browse");
		inputBrowseBtn.setBounds(306, 22, 81, 25);
		inputBrowseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.name")));
				mFileBrowser.setDialogTitle("Select Input Directory");
				mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION)
				{
					inputTextField.setText(mFileBrowser.getSelectedFile()
							.toString());
					mInputDirectoryPath = mFileBrowser.getSelectedFile()
							.toString();
				}
			}
		});
		directoryPanel.add(inputBrowseBtn);
		
		outputBrowseBtn = new JButton("Browse");
		outputBrowseBtn.setBounds(306, 60, 81, 25);
		outputBrowseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				mFileBrowser.setCurrentDirectory(new File(System
						.getProperty("user.name")));
				mFileBrowser.setDialogTitle("Select Output Directory");
				mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION)
				{
					ouptutTextField.setText(mFileBrowser.getSelectedFile()
							.toString());
					mOutputDirectoryPath = mFileBrowser.getSelectedFile()
							.toString();
				}
			}
		});
		directoryPanel.add(outputBrowseBtn);
		
		//batchComboBox = new JComboBox();
		//batchComboBox.setBounds(105, 98, 194, 25);
		//batchComboBox.setSelectedItem(null);
		//batchComboBox.getSelectedIndex();
		//directoryPanel.add(batchComboBox);
		
		JLabel lblOutputFormat = new JLabel("Output Format:");
		lblOutputFormat.setBounds(12, 102, 89, 16);
		directoryPanel.add(lblOutputFormat);
		
		optionsPanel = new JPanel();
		optionsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Work as", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));
		optionsPanel.setBounds(326, 73, 399, 81);
		contentPane.add(optionsPanel);
		optionsPanel.setLayout(null);
		
		chckbxListSelections = new JCheckBox("List Conversion");
		chckbxListSelections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(chckbxListSelections.isSelected())
				{
					chckbxBatchConversion.setSelected(false);
					listBrowseBtn.setEnabled(true);
					listOuputTextField.setEnabled(true);
					//listFormatComboBox.setEnabled(true);
					list.setEnabled(true);
					addButn.setEnabled(true);
					removeBtn.setEnabled(true);
					removeAllBtn.setEnabled(true);
					moveUpBtn.setEnabled(true);
					downBtn.setEnabled(true);
					chckbxExtractPdf.setEnabled(false);
					chckbxExtractText.setEnabled(false);
					chckbxDeleteSourceFiles.setEnabled(false);
					
					
				}
				else
				{
					listBrowseBtn.setEnabled(false);
					listOuputTextField.setEnabled(false);
					//listFormatComboBox.setEnabled(false);
					addButn.setEnabled(false);
					removeBtn.setEnabled(false);
					removeAllBtn.setEnabled(false);
					moveUpBtn.setEnabled(false);
					downBtn.setEnabled(false);
					chckbxExtractPdf.setEnabled(true);
					chckbxExtractText.setEnabled(true);
					chckbxDeleteSourceFiles.setEnabled(true);
				}
				
				
			}
		});
		chckbxListSelections.setBounds(8, 20, 122, 25);
		optionsPanel.add(chckbxListSelections);
		
		chckbxBatchConversion = new JCheckBox("Batch Conversion");
		chckbxBatchConversion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxBatchConversion.isSelected())
				{
					chckbxListSelections.setSelected(false);
					listBrowseBtn.setEnabled(false);
					listOuputTextField.setEnabled(false);
					//listFormatComboBox.setEnabled(false);
					addButn.setEnabled(false);
					removeBtn.setEnabled(false);
					removeAllBtn.setEnabled(false);
					moveUpBtn.setEnabled(false);
					downBtn.setEnabled(false);
					chckbxExtractPdf.setEnabled(true);
					chckbxExtractText.setEnabled(true);
					chckbxDeleteSourceFiles.setEnabled(true);
					
				}
			}
		});
		chckbxBatchConversion.setBounds(8, 50, 122, 25);
		optionsPanel.add(chckbxBatchConversion);
		
		chckbxSplit = new JCheckBox("Split individual Pages");
		chckbxSplit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxSplit.isSelected() == true)
				{
					chckbxSplit.setSelected(true);
				}
			}
		});
		chckbxSplit.setBounds(130, 21, 137, 23);
		optionsPanel.add(chckbxSplit);
		
		//listFormatComboBox = new JComboBox();
		//listFormatComboBox.setBounds(0, 84, 194, 25);
		//listOptionsPanel.add(listFormatComboBox);
		
		chckbxDeleteSourceFiles = new JCheckBox("Delete Source Files");
		chckbxDeleteSourceFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxDeleteSourceFiles.isSelected() == true){
					chckbxDeleteSourceFiles.setSelected(true);
				}
			}
		});
		chckbxDeleteSourceFiles.setBounds(132, 50, 123, 25);
		optionsPanel.add(chckbxDeleteSourceFiles);
		
		chckbxSaveLog = new JCheckBox("Save Log File");
		chckbxSaveLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(chckbxSaveLog.isSelected() == true)
				{
					chckbxSaveLog.setSelected(true);
					btnLogfile.setEnabled(true);
					outStream = new SaveLogOutputStream();
					mFileBrowser.setCurrentDirectory( new File("./"));
					mFileBrowser.setDialogTitle("Specify a file to save");
					int openDialog = mFileBrowser.showSaveDialog(mBatchConvertFrame);
					if ( openDialog == JFileChooser.APPROVE_OPTION )
					{		
						boolean doSaveError = true;
						boolean overrideExistingFile = false;
						errorFile = new File(mFileBrowser.getSelectedFile() + ".txt");
						System.out.println("Save as file: " + errorFile.getAbsolutePath());
						while(doSaveError && errorFile.exists() && !overrideExistingFile) //prompt if we choose to overwrite
						{						
							openDialog = (JOptionPane.showConfirmDialog(mBatchConvertFrame, "File exists", "Replace existing file", JOptionPane.YES_NO_CANCEL_OPTION));
							if(!overrideExistingFile){
								if(mFileBrowser.showSaveDialog(mBatchConvertFrame) == JFileChooser.APPROVE_OPTION){
									errorFile = new File(mFileBrowser.getSelectedFile() +".txt");									
									if(openDialog == JOptionPane.YES_OPTION)
										overrideExistingFile = true;
								}
								else
								{
									doSaveError = false;
								}
							}
							else
							{
								overrideExistingFile = true;
							}
						}
						if(doSaveError)//if we reach here log out console output to a txt file
						{
							try {
								SaveLogOutputStream.saveOutputStream(errorFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
						}
					}
					if(openDialog == JFileChooser.CANCEL_OPTION)
					{
						chckbxSaveLog.setSelected(false);
						btnLogfile.setEnabled(false);
					}
				}
				else
				{
					btnLogfile.setEnabled(false);
				}

			}
		});
		chckbxSaveLog.setBounds(269, 21, 97, 23);
		optionsPanel.add(chckbxSaveLog);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(230, 656, 158, -14);
		contentPane.add(progressBar);
		
		statusPanel = new JPanel();
		statusPanel.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		statusPanel.setBounds(540, 285, 185, 74);
		contentPane.add(statusPanel);
		statusPanel.setLayout(null);
		
		inputStatusLabel = new JLabel("Input Status:");
		inputStatusLabel.setBounds(12, 27, 163, 16);
		statusPanel.add(inputStatusLabel);
		
		outputStatusLabel = new JLabel("Output Status:");
		outputStatusLabel.setBounds(12, 54, 163, 16);
		statusPanel.add(outputStatusLabel);
		
		convTimePanel = new JPanel();
		convTimePanel.setBorder(new TitledBorder(null, "Conversion Time", TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLUE));
		convTimePanel.setBounds(327, 353, 399, 178);
		contentPane.add(convTimePanel);
		convTimePanel.setLayout(null);
		
		covtimeScrollPane = new JScrollPane();
		covtimeScrollPane.setBounds(0, 11, 399, 167);
		convTimePanel.add(covtimeScrollPane);
		
		convtimeTextArea = new JTextArea();
		convtimeTextArea.setWrapStyleWord(true);
		convtimeTextArea.setLineWrap(true);
		convtimeTextArea.setBackground(UIManager.getColor("Button.background"));
		convtimeTextArea.setEditable(false);
		covtimeScrollPane.setViewportView(convtimeTextArea);
		
		mFileProgressbar = new JProgressBar(0,100);
		mFileProgressbar.setStringPainted(true);
		//mFileProgressbar.setMinimum(0);
		//mFileProgressbar.setMaximum(100);
		mFileProgressbar.setForeground(new Color(165, 42, 42));
		mFileProgressbar.setBounds(326, 770, 399, 20);
		contentPane.add(mFileProgressbar);
		
		mProgressBar = new JProgressBar(0,100);
		//mProgressBar.setMinimum(minValue);
		//mProgressBar.setMaximum(complete);
		mProgressBar.setForeground(Color.BLUE);
		mProgressBar.setBounds(326, 744, 399, 20);
		mProgressBar.setStringPainted(true);
		contentPane.add(mProgressBar);
		
		chckbxSendProblemFiles = new JCheckBox("Send Problem Files to:");
		chckbxSendProblemFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(chckbxSendProblemFiles.isSelected() == true)
				{
					problemFilesTextField.setEnabled(true);
					problemFilesBtn.setEnabled(true);
				}
				else
				{
					problemFilesTextField.setEnabled(false);
					problemFilesBtn.setEnabled(false);
				}
			}
		});
		chckbxSendProblemFiles.setBounds(326, 285, 162, 25);
		contentPane.add(chckbxSendProblemFiles);
		
		problemFilesTextField = new JTextField();
		problemFilesTextField.setEnabled(false);
		problemFilesTextField.setColumns(10);
		problemFilesTextField.setBounds(326, 311, 147, 21);
		contentPane.add(problemFilesTextField);
		
		problemFilesBtn = new JButton("Browse");
		problemFilesBtn.setEnabled(false);
		problemFilesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				// Open FileChooser in a separate thread to avoid possible thread deadlocks
				try
				{
					EventQueue.invokeLater(new Runnable(){
						@Override
						public void run() {													
							mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.name")));
							mFileBrowser.setDialogTitle("Select Directory Path");
							mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
							if(status == JFileChooser.APPROVE_OPTION)
							{
								problemFilesTextField.setText(mFileBrowser.getSelectedFile().getName().toString());
								mProblemDirectoryPath = mFileBrowser.getSelectedFile().getAbsolutePath().toString();		
							}
						}

					});
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		problemFilesBtn.setBounds(473, 310, 67, 25);
		contentPane.add(problemFilesBtn);
		
		stopBtn = new JButton("Stop");
		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(doInputWork!= null && !doInputWork.isDone()) {
					doInputWork.cancel(true);
				}
				else
				{
					isPaused = doListInputWork!= null && !doListInputWork.isDone();
					doListInputWork.cancel(true);
				}
				doInputWork = null;
				doListInputWork = null;
				gDone = false;
				isPaused = false;
				pauseBtn.setText("Pause");
				pauseBtn.setEnabled(false);			
				//mProgressBar.setValue(mProgressBar.getMinimum());
				//mFileProgressbar.setValue(mFileProgressbar.getMinimum());
			}
		});
		stopBtn.setBounds(230, 315, 93, 33);
		contentPane.add(stopBtn);
		
		listBrowseBtn = new JButton("Browse");
		listBrowseBtn.setBounds(221, 408, 67, 25);
		contentPane.add(listBrowseBtn);
		listBrowseBtn.setEnabled(false);
		
		listOuputTextField = new JTextField("Specify output directory to convert to...");
		listOuputTextField.setBounds(13, 408, 207, 25);
		contentPane.add(listOuputTextField);
		listOuputTextField.setEnabled(false);
		listOuputTextField.setColumns(10);
		
		snowboundLogoLabel = new JLabel("");
		snowboundLogoLabel.setIcon(new ImageIcon(BatchConvert7_1.class.getResource("/com/resource/snowlogo1.png")));
		snowboundLogoLabel.setBounds(0, 19, 736, 42);
		contentPane.add(snowboundLogoLabel);
		listOuputTextField.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				mOutputDirectoryPath = ouptutTextField.getText();
			}

			public void focusGained(FocusEvent e)
			{
				//intentionally left blank
			}
		});
		
		listBrowseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mFileBrowser.setCurrentDirectory(new File(System
						.getProperty("user.name")));
				mFileBrowser.setDialogTitle("Select Output Directory");
				mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
				if (status == JFileChooser.APPROVE_OPTION)
				{
					listOuputTextField.setText(mFileBrowser.getSelectedFile()
							.toString());
					mListOutputDirectoryPath = mFileBrowser.getSelectedFile()
							.toString();
				}
				
			}
		});
	}
	 public void setDataObject(byte[] data)
	 {
		 this.data = data;
	 }
	
	/**
	 * PreviewPanel is used for drawing a preview thumbnail of the current
	 * image being processed.  PreviewPanel should update with each step
	 * of the progress bar.
	 */
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

		public void paint(Graphics g)
		{
			if (s != null)
			{
				Insets in = getInsets();
				Dimension dimension = getSize();
				dimension.width -= (in.right + in.left);
				dimension.height -= (in.top + in.bottom);
				g.translate(in.left, in.top);
				/* draw a white background over previous image */
				g.setColor(newColor);
				g.fillRect(0, 0, dimension.width, dimension.height);
				s.IMG_display_bitmap_aspect(g,
						this,
						0,
						0,
						dimension.width,
						dimension.height,
						 0);
			}
		}

		public void update(Graphics g)
		{
			/*if (mZoomedOut)
            {
                mZoomedOut = false;

                if (mZoom == 1)
                {
                    Dimension dimension = getSize();
                    g.setColor(mPreviewPanel.getBackground());
                    g.fillRect(0, 0, dimension.width, dimension.height);
                }
            }*/
			paint(g);

		}

	}
	
	/**
	 * This class allows us to update our progress bar,
	 * we also use for pausing document conversion
	 */
	enum Component { TOTAL, FILE, LOG, PAUSE }
	class Progress {
		public final Object value;
		public final Component component;
		public Progress(Component component, Object value) {
			this.component = component;
			this.value = value;
		}
	}
	/**
	 * This extends the SwingWorker class. Swingworker works in the background
	 * of the GUI. This allows for  components to be easily updated. 
	 * 
	 */
	class ConvertInputdocuments extends SwingWorker<Integer, Progress>{
		int getCurrentPageCount = 1;
		int statusResult = 0;
		long totalTime;
		File getFileName;
		@Override
		protected Integer doInBackground() throws Exception { //do long task in background
			
			if (true)
            {
				pauseBtn.setEnabled(true);
				convBtn.setEnabled(false);
				stopBtn.setEnabled(true);
				mProgressBar.setEnabled(true);
				gDone = false;
				myTimer = new Timer();
				convertToFormat = determineConversionFormat();			
				try{
					preprocessDirectory();
					mProgressBar.setMaximum(myFileProcessor.length);//(myFileProcessor.length);
				}
				
				catch(Exception ex)
				{
					System.out.println("Error converting to current format...Please check your file or classpath for settings " + ex.getMessage());
					ex.printStackTrace();
				}
				publish(new Progress(Component.LOG, "Length Of Document Progress: " + myFileProcessor.length));
				while(counter < myFileProcessor.length && !gDone && !isCancelled())
				{	
					String currentFile = myFileProcessor[counter].getCurrentFile();
					getFileName = new File(currentFile);
					String currentFileSaveAs = myFileProcessor[counter].getCurrentFileSaveAs();			
					getFileNameSave = new File(currentFileSaveAs);
					int currentPage = myFileProcessor[counter].getCurrentPage();
					int lengthOfTask = 10+randomInt.nextInt(50);
					int currentFileLength = 0;
					boolean blinking =  false;
					getCurrentPageCount = currentPage;
					s = new Snowbnd();
					length[0] = 0;
					error[0] = 0;
					errorA[0] = 0;
					//  advancePanel = new AdvanceOptionsPanel();
					s.setFrame(mBatchConvertFrame);

					//advancePanel.pdfInputPanelOptions();
					docInputPanelOptions();
					pdfInputPanelOptions();
					getPclInputOptions(); //PCL input settings	                                    
					getAfpFontmap(); //afp font mapping
					fastAFPConversion(); //fast conversion
					anti_aliasingPanelOptions(); //anti-aliasing

					status = s.IMG_decompress_bitmap(currentFile, currentPage);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					if (status < 0) // check decompression for error. Return value less than zero is an error
					{
						/*
						 * if there was an error in decompression, check if our field is enabled
						 * we will then log out all the problem files to a dir
						 */
						if(chckbxSendProblemFiles.isEnabled() && problemFilesTextField.getText().length() > 0)
						{
							File sourceDir = new File(currentFile); // check the current file
							File desDir = new File(mProblemDirectoryPath); // outputProblem Path
							try
							{
								//move all problem files and right name to desDir
								boolean moveSuccessful = sourceDir.renameTo(new File(desDir, sourceDir.getName()));
								System.out.println("Error, within: " + sourceDir.getName() + " was moved to: "  + desDir.getAbsolutePath());
								if (!moveSuccessful) {
									System.out.println("Error, file was not moved");
								}
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}
							/* Return values less than zero indicate an error */
							System.out.println("Error decompressing page at pageIndex = "
									+ currentFile + " : "+ ErrorCodes.getErrorMessage(status));
							System.out.println("Corrupted file was moved ");		
						}
						else
						{
							/* Return values less than zero indicate an error */
							System.out.println("Error decompressing page at pageIndex = "
									+ currentPage + " : "+ ErrorCodes.getErrorMessage(status));

						}
						System.out.println("Error decompressing Image: "+ ErrorCodes.getErrorMessage(status));																									

					}

					pdfOutputPanelOptions(); //pdfOutput options call
					imageOutputOptions(); //imageOutput options call
					eightBitGrayScale(); //8-bit GrayScale
					antiqueEffect(); //Antique effect
					getInvertImage(); //Invert image call;
					getPromoteEightBit(); //promoteEightBit image call
					improveGrayScale(); //improve 8-bit GrayScale
					promoteImageBit(); //promote 24-bit
					getImageDiffusion(); //1-bit 
					getImgeThreashold();
					getJpegCompressionOptions(); //jpeg compression quality
					getDespeckleOptions(); //Despeckle call
					brightnessContrastOptions(); //brightness_contrast call
					cmykImage(); //cymkRGB call
					extractVectorPage(s, currentFile, currentPage, error);				
					try {
						extractTextDoc(s, length, currentPage);
					} catch (Throwable e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}				

					/* verify no errors occurred while decompressing 
					 * PIXEL_DEPTH_UNSUPPORTED Indicates the output format does not 
					 * support the current bits per pixel. If so, the current image/page will be dropped
					 * A status of zero indicates success	
					 */
					if (status > ErrorCodes.OUT_OF_MEMORY )
					{
						if(currentPage >= 0)
							currentPage++;					
						System.out.println("Current file: "+ getFileName.getName() + "\n" + " Page  " + currentPage);
						System.out.println("Decompress bitmap status: " + status);
						status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);	
						//outputStatusLabel .setText("Output Status: " + status);  
						statusResult = status;
						if(chckbxSavesearchable.isSelected() == true)
						{
							getFileNameSave.delete(); //delete save_bitmap file if we create searchable, just a preferered way so we don't save 2 files
						}
						if(chckbxExtractPdf.isSelected() == true)
						{
							getFileNameSave.delete();
						}
						if (status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
						{
							System.out.println("saving_bitmap: " + status);
							System.out.println("save_bitmap error: "
									+ ErrorCodes.getErrorMessage(status));
							System.out.println("Pixel Depth will be Dropped to 8-bit...");
							status = s.IMG_color_gray();											
							System.out.println("Color Gray status: " + status);									
							status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
							if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
							{
								System.out.println("saving_bitmap: " + status);
								System.out.println("save_bitmap error: "
										+ ErrorCodes.getErrorMessage(status));
								System.out.println("Convert current page to lowest bit...");
								status = s.IMG_diffusion_mono();										
								System.out.println("Converting image to 1-bit: " + status);					
								//status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
							}
						}
						/* If successful, IMG_save_bitmap() returns the cumulative number of bytes written out */
						System.out.println("save_bitmap Successful: " + status);
						System.out.println("--------------------------------------"); //just for seperation

						if (status == ErrorCodes.JWEBENGINE_JAR_NOT_IN_CLASSPATH)
						{
							System.out.println("save_bitmap: " + status);
							System.out.println("save_bitmap error: " + ErrorCodes.getErrorMessage(status));						                        
						}		
					}
					/* error on decompression, display message */
					else
					{								

						System.out.println("decompress_bitmap status: " + status);
						System.out.println("saved_bitmap error: "  + ErrorCodes.getErrorMessage(status));
						System.out.println("Error, page or file will be skipped : " + currentFile);

					}

					//Split multiple documents
					if(chckbxSplit.isSelected() == true) //done
					{
						
					
						int splitPage = 0;
						//File outputFile = new File(startFile+"_extractedPage"+pageIndex+".pdf");
						File outputFile = new File(currentFile+"_pages"+currentPage +getFormatsNames());
						splitPage = s.IMG_save_bitmap(outputFile.toString(), convertToFormat);
						outputFile.renameTo(new File(mOutputDirectoryPath, outputFile.getName()));	
						getFileNameSave.delete();
						System.out.println("Splitting pages: " + splitPage);	
						
					}
					statusResult = status;
					//inputStatusLabel.setText("Input Status: " + status);
					mPreviewPanel.repaint();
					mPreviewPanel.updateUI();
                      
				
					//mProgressBar.setValue(counter);
					counter++;	
					publish(new Progress(Component.TOTAL, 100 * counter / myFileProcessor.length ));
					//setProgress(0);
					while(currentFileLength<=lengthOfTask && !isCancelled()) {
						if(gDone) {
							try{
								Thread.sleep(randomInt.nextInt(500));
							}catch(InterruptedException ie) {
							}
							publish(new Progress(Component.PAUSE, blinking));
							blinking = !blinking;
							continue;
						}
						int iv = 100 * currentFileLength / lengthOfTask;
						Thread.sleep(15);
						publish(new Progress(Component.FILE, iv+1));
						currentFileLength++;
					}
														
					System.out.println("Current TestThread ended:  " + Thread.currentThread().getName()
							+ " BatchConvert7.1 " + new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(endTime = new Date()));
					System.out.println("------------------------------------------------------------------------");									

					//Log out the total conversion time 
					totalTime = endTime.getTime() - startTime.getTime();
					System.out.println("total conversion time = " + (double)totalTime/(1000F) + " seconds ");											

					// Just for separation
					System.out.println("------------------------------------------------------------------------");																			
					System.out.println();
					//count pages from 1
					if(currentPage >= 0)
					{
						publish(new Progress(Component.LOG, "\n------------------------------\n"));
						//currentPage++;																							
						publish(new Progress(Component.LOG, getFileName.getName()  + "\n" + "Page  " + currentPage + "\n" +  "total conversion time = " + (double) totalTime/(1000F) + " seconds " + "\n"));
					}
				}

            }

			return status;
		}

		@Override
		protected void done() {
			if(isCancelled())
			{
				convtimeTextArea.append("\n"+"Cancelled"+"\n");
			}
			else
			{
				try
				{
					int doComplete = get();		
					System.out.println("Complete with status : " + doComplete);
					//mProgressBar.setString("Complete");
					//Sleep for 2seconds before resetting the progressBar
					//Thread.sleep(2000);			
					Toolkit.getDefaultToolkit().beep();
					mProgressBar.setValue(mProgressBar.getMinimum());
					//mFileProgressbar.setValue(mFileProgressbar.getMinimum());
					if(chckbxDeleteSourceFiles.isSelected() == true)
					{
						deleteDirectoryFiles();
					}												
				}
				catch(InterruptedException ex){} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			convBtn.requestFocusInWindow();
			convBtn.setEnabled(true);
			pauseBtn.setEnabled(false);
			stopBtn.setEnabled(false);
			mProgressBar.setEnabled(false);
			mPreviewPanel.revalidate();	
			convtimeTextArea.append("\n"+"Done"+"\n");
			convtimeTextArea.setCaretPosition(convtimeTextArea.getDocument().getLength());
			counter = 0;
			totalPageCount =0;
		}

		 //update UI from here	
		@Override
		protected void process(List<Progress> chunks) {		
			for(Progress s: chunks) {
				switch(s.component) {
				case TOTAL: mFileProgressbar.setValue((Integer)s.value); break;
				case FILE:  mProgressBar.setValue((Integer)s.value); break;
				case LOG:   convtimeTextArea.append((String)s.value); break;
				case PAUSE: {
					if((Boolean)s.value) {
						convtimeTextArea.append(".");
					}
					break;
				}	
				}
				outputStatusLabel.setText("Output Status: " + statusResult);   
				inputStatusLabel.setText("Input Status: " + statusResult);	
			}
			/*int recentVal = chunks.get(chunks.size()-1);// Here we receive the values that we publish(), they may come grouped in chunks.
			mProgressBar.setValue(recentVal);
			outputStatusLabel.setText("Output Status: " + statusResult);   
			inputStatusLabel.setText("Input Status: " + statusResult);		
			convtimeTextArea.append("---------------------------------------------" + "\n");																				
			convtimeTextArea.append(getFileName.getName() + "\n" + " Job " + recentVal + "\n" +  " total conversion time = " + (double) totalTime/(1000F) + " seconds " + "\n");*/

		}

	}
	
	class ListInputDocumentConvert extends SwingWorker<Integer, Progress>{
		int getCurrentPage = 1;
		int docStatus = 0;
		long totalTime;
		File getCurrentFileName;
		@Override
		protected Integer doInBackground() throws Exception {//worker thread
			
			if (true)
            {
				pauseBtn.setEnabled(true);
				convBtn.setEnabled(false);
				stopBtn.setEnabled(true);
				mProgressBar.setEnabled(true);
				isPaused = false;
				myTimer = new Timer();
				convertToFormat = determineConversionFormat();
				try{
					preprocessListDirectory();
					mProgressBar.setMaximum(myFileProcessor.length);//(myFileProcessor.length);
				}	
				catch(Exception ex)
				{
					System.out.println("Error converting to current format...Please check your file or classpath for settings ");
					ex.printStackTrace();
				}
				publish(new Progress(Component.LOG, "Length Of Document Progress: " + myFileProcessor.length));
				while(counter < myFileProcessor.length && !isPaused && !isCancelled())
				{	
					String currentFile = myFileProcessor[counter].getCurrentFile();	
					getCurrentFileName = new File(currentFile);
					String currentFileSaveAs = myFileProcessor[counter].getCurrentFileSaveAs();				
					getListfileSave = new File(currentFileSaveAs);
					int currentPage = myFileProcessor[counter].getCurrentPage();
					int lengthOfTask = 10+randomNum.nextInt(50);
					int currentFileLength = 0;
					boolean blinking =  false;
					getCurrentPage = currentPage;
					s = new Snowbnd();
					length[0] = 0;
					error[0] = 0;
					errorA[0] = 0;
					//  advancePanel = new AdvanceOptionsPanel();
					s.setFrame(mBatchConvertFrame);

					//advancePanel.pdfInputPanelOptions();
					docInputPanelOptions();
					pdfInputPanelOptions();
					getPclInputOptions(); //PCL input settings	                                    
					getAfpFontmap(); //afp font mapping
					fastAFPConversion(); //fast conversion
					anti_aliasingPanelOptions(); //anti-aliasing

					status = s.IMG_decompress_bitmap(currentFile, currentPage);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					if (status < 0) // check decompression for error. Return value less than zero is an error
					{
						/*
						 * if there was an error in decompression, check if our field is enabled
						 * we will then log out all the problem files to a dir
						 */
						if(chckbxSendProblemFiles.isEnabled() && problemFilesTextField.getText().length() > 0)
						{
							File sourceDir = new File(currentFile); // check the current file
							File desDir = new File(mProblemDirectoryPath); // outputProblem Path
							try
							{
								//move all problem files and right name to desDir
								boolean moveSuccessful = sourceDir.renameTo(new File(desDir, sourceDir.getName()));
								System.out.println("Error, within: " + sourceDir.getName() + " was moved to: "  + desDir.getAbsolutePath());
								if (!moveSuccessful) {
									System.out.println("Error, file was not moved");
								}
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}
							/* Return values less than zero indicate an error */
							System.out.println("Error decompressing page at pageIndex = "
									+ currentFile + " : "+ ErrorCodes.getErrorMessage(status));
							System.out.println("Corrupted file was moved ");		
						}
						else
						{
							/* Return values less than zero indicate an error */
							System.out.println("Error decompressing page at pageIndex = "
									+ currentPage + " : "+ ErrorCodes.getErrorMessage(status));

						}
						System.out.println("Error decompressing Image: "+ ErrorCodes.getErrorMessage(status));																									

					}
					pdfOutputPanelOptions(); //pdfOutput options call
					imageOutputOptions(); //imageOutput options call
					eightBitGrayScale(); //8-bit GrayScale
					antiqueEffect(); //Antique effect
					getInvertImage(); //Invert image call;
					getPromoteEightBit(); //promoteEightBit image call
					eightBitGrayScale(); //8-bit GrayScale
					improveGrayScale(); //improve 8-bit GrayScale
					promoteImageBit(); //promote 24-bit
					getImageDiffusion(); //1-bit 
					getImgeThreashold();
					getDespeckleOptions(); //Despeckle call
					getJpegCompressionOptions(); //jpeg compression quality
					brightnessContrastOptions(); //brightness_contrast call
					cmykImage(); //cymkRGB call									

					/* verify no errors occurred while decompressing 
					 * PIXEL_DEPTH_UNSUPPORTED Indicates the output format does not 
					 * support the current bits per pixel. If so, the current image/page will be dropped
					 * A status of zero indicates success	
					 */
					if (status > ErrorCodes.OUT_OF_MEMORY )
					{
						if(currentPage >= 0)
							currentPage++;							
						System.out.println("Current file: "+ getCurrentFileName.getName() + "\n" + " Page  " + currentPage);
						System.out.println("Decompress bitmap status: " + status);
						status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
						//outputStatusLabel .setText("Output Status: " + status);  
						docStatus = status;
						if(chckbxSavesearchable.isSelected() == true)
						{
							getListfileSave.delete();
						}
						if (status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
						{
							System.out.println("saving_bitmap: " + status);
							System.out.println("save_bitmap error: "
									+ ErrorCodes.getErrorMessage(status));
							System.out.println("Pixel Depth will be Dropped to 8-bit...");
							status = s.IMG_color_gray();											
							System.out.println("Color Gray status: " + status);									
							status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
							if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
							{
								System.out.println("saving_bitmap: " + status);
								System.out.println("save_bitmap error: "
										+ ErrorCodes.getErrorMessage(status));
								System.out.println("Convert current page to lowest bit...");
								status = s.IMG_diffusion_mono();										
								System.out.println("Converting image to 1-bit: " + status);					
								//status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
							}
						}
						/* If successful, IMG_save_bitmap() returns the cumulative number of bytes written out */
						System.out.println("save_bitmap Successful: " + status);
						System.out.println("--------------------------------------"); //just for seperation

						if (status == ErrorCodes.JWEBENGINE_JAR_NOT_IN_CLASSPATH)
						{
							System.out.println("save_bitmap: " + status);
							System.out.println("save_bitmap error: " + ErrorCodes.getErrorMessage(status));						                        
						}		
					}
					/* error on decompression, display message */
					else
					{								

						System.out.println("decompress_bitmap status: " + status);
						System.out.println("saved_bitmap error: "  + ErrorCodes.getErrorMessage(status));
						System.out.println("Error, page or file will be skipped : " + currentFile);

					}

					//Split multiple documents
					if(chckbxSplit.isSelected() == true) //can only split one doc at a time. Multiple documents causes problems(needs fix)
    				{										
						listModel = (DefaultListModel<File>) list.getModel();
						int spliDoc = 0;
						for (int i = 0; i < listModel.getSize(); i++)
			    		{
			    			Object item = listModel.get(i).getName();
			    			File afile = new File(item+"_pages"+currentPage+getFormatsNames());
			    			spliDoc = s.IMG_save_bitmap(afile.toString(), convertToFormat);
							afile.renameTo(new File(mListOutputDirectoryPath, afile.getName()));	
							getListfileSave.delete();
							System.out.println("Splitting pages: " + spliDoc);					
			    		}
						
																												                            			
    				}
					docStatus = status;
					//inputStatusLabel.setText("Input Status: " + status);
					mPreviewPanel.repaint();
					mPreviewPanel.updateUI();
                                               
					//mProgressBar.setValue(counter);
					counter++;	
					publish(new Progress(Component.TOTAL, 100 * counter / myFileProcessor.length ));
					//setProgress(0);
					while(currentFileLength<=lengthOfTask && !isCancelled()) {
						if(isPaused) {
							try{
								Thread.sleep(randomNum.nextInt(500));
							}catch(InterruptedException ie) {
							}
							publish(new Progress(Component.PAUSE, blinking));
							blinking = !blinking;
							continue;
						}
						int iv = 100 * currentFileLength / lengthOfTask;
						Thread.sleep(15);
						publish(new Progress(Component.FILE, iv+1));
						currentFileLength++;
					}

					System.out.println("Current TestThread ended:  " + Thread.currentThread().getName()
							+ " BatchConvert7.1 " + new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(endTime = new Date()));
					System.out.println("------------------------------------------------------------------------");									

					//Log out the total conversion time 
					totalTime = endTime.getTime() - startTime.getTime();
					System.out.println("total conversion time = " + (double)totalTime/(1000F) + " seconds ");											

					// Just for separation
					System.out.println("------------------------------------------------------------------------");																			
					System.out.println();
					//count pages from 1
					if(currentPage >= 0)
					{
						publish(new Progress(Component.LOG, "\n------------------------------\n"));
						//currentPage++;																							
						publish(new Progress(Component.LOG, getCurrentFileName.getName()+ "\n" + "Page  " + currentPage + "\n" +  "total conversion time = " + (double) totalTime/(1000F) + " seconds " + "\n"));
					}
				}

            }
			return status;
		}

		@Override
		protected void done() {
			if(isCancelled())
			{
				convtimeTextArea.append("\n"+"Cancelled"+"\n");
			}
			else
			{
				try
				{
					int complete = get();
					System.out.println("Complete with status : " + complete);
					int numDocument = listModel.getSize();
					System.out.println("Input documents: " + numDocument);
					mProgressBar.setValue(mProgressBar.getMinimum());
					Toolkit.getDefaultToolkit().beep();															
				}
				catch(InterruptedException ex){} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}		
			convBtn.requestFocusInWindow();
			convBtn.setEnabled(true);
			pauseBtn.setEnabled(false);
			stopBtn.setEnabled(false);
			mProgressBar.setEnabled(false);
			mPreviewPanel.revalidate();	
			convtimeTextArea.append("\n"+"Done"+"\n");
			convtimeTextArea.setCaretPosition(convtimeTextArea.getDocument().getLength());
			counter = 0;
			totalPageCount =0;
		}
		@Override
		protected void process(List<Progress> chunks) {//update UI from here	
			for(Progress s: chunks) {
				switch(s.component) {
				case TOTAL: mFileProgressbar.setValue((Integer)s.value); break;
				case FILE:  mProgressBar.setValue((Integer)s.value); break;
				case LOG:   convtimeTextArea.append((String)s.value); break;
				case PAUSE: {
					if((Boolean)s.value) {
						convtimeTextArea.append(".");
					}
					break;
				}	
				}
				outputStatusLabel.setText("Output Status: " + docStatus);   
				inputStatusLabel.setText("Input Status: " + docStatus);			
			}		
			/*int recentVal = chunks.get(chunks.size()-1);
			mProgressBar.setValue(recentVal);
			outputStatusLabel.setText("Output Status: " + docStatus);   
			inputStatusLabel.setText("Input Status: " + docStatus);			
			convtimeTextArea.append("---------------------------------------------" + "\n");																				
			convtimeTextArea.append(getCurrentFileName.getName() + "\n" + " Job " + recentVal + "\n" +  " total conversion time = " + (double) totalTime/(1000F) + " seconds " + "\n");*/
		}
		
	}
	
	//@SuppressWarnings("unchecked")
	public void initializeFormatsList()
	{

		FormatHash formatHash = FormatHash.getInstance();
		String[] saveFormats = formatHash.getAvailibleSaveFormats();
		java.util.Arrays.sort(saveFormats);
		batchComboBox = new JComboBox(saveFormats);
		batchComboBox.setBounds(105, 98, 194, 25);		
		for (int index = 0; index < saveFormats.length; index++)
		{		
			batchComboBox.setSelectedItem(null);
			batchComboBox.getSelectedIndex();
			directoryPanel.add(batchComboBox,saveFormats[index]);


		}
	}
	
	public int determineConversionFormat()throws NullPointerException
	{
		String outputFormat = (String) batchComboBox.getSelectedItem();
		Format format = FormatHash.getInstance().getFormat(outputFormat);
		conversionFormatExtension = format.getExtension();
		return format.getFormatCode();
	}
	
	 /* This method has the potential to cause alot of overhead if the page count is large */
    /* For a speed increase, consider improving the threading model or running headless */
    public void preprocessDirectory()
    {
        File inputDirectory = new File(mInputDirectoryPath);
        File outputDirectory = new File(mOutputDirectoryPath);      
        Snowbnd s = new Snowbnd();
        if (true)
        //        if (inputDirectory.isDirectory() && outputDirectory.isDirectory())
        {
            inputFileList = inputDirectory.list();
            totalFiles = inputDirectory.listFiles();
            float fileLengths = ScanFileSizeInMB(inputDirectory.toString());                   
            if(fileLengths >= 25)
            {
           	 	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           	 	convtimeTextArea.append("Decompressing, Please be paitient..."+"\n");
            }
            System.out.println("size: " + fileLengths);
			scanDirectoryFiles(inputDirectory);
			
			//Begin timer task
			System.out.println("------------------------------------------------------------------------");
			System.out.println("Current TestThread started: " + Thread.currentThread().getName() + "BatchConvert7.1 " + new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(startTime = new Date()));
			System.out.println("------------------------------------------------------------------------");
								
			for (int i = 0; i < inputFileList.length; i++)
			{          	
				String currentFile = mInputDirectoryPath + System.getProperty("file.separator") + inputFileList[i];
				//Skip directories. Get page count of each file
				try
				{

					int pageCount = s.IMGLOW_get_pages(currentFile);
					if (pageCount < 0)
					{
						if(problemFilesTextField.isEnabled() && problemFilesTextField.getText().length() > 0)
						{
							File sourceDir = new File(currentFile);
							File desDir = new File(mProblemDirectoryPath);
							try
							{
								boolean moveSuccessful = sourceDir.renameTo(new File(desDir, sourceDir.getName()));
								System.out.println("Error, within: " + sourceDir.getName() + " was moved to: "  + desDir.getAbsolutePath());
								if (!moveSuccessful) {
									System.out.println("Error, file was not moved");
								}
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}
							/* Return values less than zero indicate an error */
							System.out.println("Error in counting pages: " + ErrorCodes.getErrorMessage(pageCount) + "," + "ProblemFileName: " + currentFile);
							System.out.println("Corrupted file was moved");
						}
						else
						{
							/* Return values less than zero indicate an error */
							System.out.println("Error in counting pages: " + ErrorCodes.getErrorMessage(pageCount) + "," + "CorruptedFileName: " + currentFile);
						}
					}
					if(pageCount >= 0)
					{
						totalPageCount += pageCount;
					}				
					else
					{
						//creating new objects..need to make one instance
						//new MessageBox(mBatchConvertFrame , "Error converting file: " + " ' Please check the file or classpath for proper jar file ' ", true);
						System.out.println("Caught error in get pages for file ".toString() + i + " '" + currentFile + "': " + pageCount + "\n"
								+ErrorCodes.getErrorMessage(pageCount).toUpperCase() + "\n");
					}
					/*if(totalPageCount > 1)					
							{
								System.out.println("Saving as multipage document");
							}*/


					//totalPageCount += s.IMGLOW_get_pages(currentFile);
				}
				catch(Exception ex)
				{
					System.out.println("Caught error in get pages for file " + i + " '" + currentFile + "' : " + ex.getMessage());

					ex.printStackTrace();
				}						
				contentPane.repaint();
			}          
            myFileProcessor = new FileProgress[totalPageCount];
            int globalImageCount = 0;
            File deleteOutputDir = null;
            for (int i = 0; i < inputFileList.length; i++)
            {

				String currentFile = mInputDirectoryPath
						+ System.getProperty("file.separator") + inputFileList[i];
				String currentFileSaveAs = mOutputDirectoryPath
						+ System.getProperty("file.separator") + inputFileList[i]
								+ "." + conversionFormatExtension;
				deleteOutputDir = new File(currentFileSaveAs);			
				if(deleteOutputDir.exists())
				{
					deleteOutputDir.delete();
				}				            
				try
				{
					int currentPageCount = s.IMGLOW_get_pages(currentFile);
					if(currentPageCount > 0 )
					{
						for (int j = 0; j < currentPageCount; j++)
						{
							myFileProcessor[globalImageCount] = new FileProgress();
							myFileProcessor[globalImageCount]
									.setCurrentFile(currentFile);
							myFileProcessor[globalImageCount]
									.setCurrentFileSaveAs(currentFileSaveAs);
							myFileProcessor[globalImageCount].setCurrentPage(j);
							globalImageCount++;
						}
					}
					else
					{
						totalPageCount += s.IMGLOW_get_pages(currentFile);
						System.out.println(" Error in currentImage/file format: Please check your file: "  +
								ErrorCodes.getErrorMessage(currentPageCount).toUpperCase() + "\n");
					}

				}
				catch(Exception ex)
				{
					//System.out.println("Error in get pages for file " + i + " '" + currentFile + "' : " + ex.getMessage());
					System.out.println("Skipping '" + currentFile + "' due to get_pages error " + ex.getMessage());
				}
            }
        }
        else
        {
            System.out.println("Specify valid directory");
        }
    }
    
    public void preprocessListDirectory() throws IOException
    {  	  	    				
    	listModel = (DefaultListModel<File>) list.getModel();
    	File outputListDirectoryPath = new File(mListOutputDirectoryPath);
    	Snowbnd s = new Snowbnd();
    	if (true)
    	{  		    	
    		//if(listModel.getSize() > 0)	
    		 float fileLengths = listModel.getSize();  
             if(fileLengths >= 20)
             {
            	 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            	 convtimeTextArea.append("Decompressing, Please be paitient..."+"\n");
             }
    		//Begin timer task
    		System.out.println("------------------------------------------------------------------------");
    		System.out.println("Current TestThread started: " + Thread.currentThread().getName() + "BatchConvert7.1 " + new SimpleDateFormat("MM/dd/yyy HH:mm:ss.SS").format(startTime = new Date()));
    		System.out.println("------------------------------------------------------------------------");
    		
    		for (int i = 0; i < listModel.getSize(); i++)
    		{ 			
    			Object item = list.getModel().getElementAt(i);
    			String currentFile = item.toString() + System.getProperty("file.separator") ;	
  					
    			try
				{
					
					int pageCount = s.IMGLOW_get_pages(currentFile);
					if (pageCount < 0)
					{
						if(problemFilesTextField.isEnabled() && problemFilesTextField.getText().length() > 0)
						{
							File sourceDir = new File(currentFile);
							File desDir = new File(mProblemDirectoryPath);
							try
							{
								boolean moveSuccessful = sourceDir.renameTo(new File(desDir, sourceDir.getName()));
								System.out.println("Error, within: " + sourceDir.getName() + " was moved to: "  + desDir.getAbsolutePath());
								if (!moveSuccessful) {
									System.out.println("Error, file was not moved");
								}
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}
							/* Return values less than zero indicate an error */
							System.out.println("Error in counting pages: " + ErrorCodes.getErrorMessage(pageCount) + "," + " ProblemFileName: " + currentFile);
							System.out.println("Corrupted file was moved");
						}
						else
						{
							/* Return values less than zero indicate an error */
							System.out.println("Error in counting pages: " + ErrorCodes.getErrorMessage(pageCount) + "," + " CorruptedFileName: " + currentFile);
						}
					}
					if(pageCount >= 0)
					{
						totalPageCount += pageCount;
					}				
					else
					{
						//creating new objects..need to make one instance
						//new MessageBox(mBatchConvertFrame , "Error converting file: " + " ' Please check the file or classpath for proper jar file ' ", true);
						System.out.println("Caught error in get pages for file ".toString() + i + " '" + currentFile + "': " + pageCount + "\n"
								+ErrorCodes.getErrorMessage(pageCount).toUpperCase() + "\n");
					}
					/*if(totalPageCount > 1)					
					{
						System.out.println("Saving as multipage document");
					}*/


					//totalPageCount += s.IMGLOW_get_pages(currentFile);
				}
				catch(Exception ex)
				{
					System.out.println("Caught error in get pages for file " + i + " '" + currentFile + "' : " + ex.getMessage());

					ex.printStackTrace();
				}
				contentPane.repaint();
    		}
    		
           // we iterates the entire list items or elements.
    		myFileProcessor = new FileProgress[totalPageCount];
    		int globalImageCount = 0;
    		File deleteOutputDir = null;
    		for (int i = 0; i < listModel.getSize(); i++)
    		{
    			Object item = list.getModel().getElementAt(i); //creat an object 
    			String currentFile = item.toString()
    					+ System.getProperty("file.separator");
    			String currentFileSaveAs = mListOutputDirectoryPath
    					+ System.getProperty("file.separator") + listModel.get(i).getName() 
    					+ "." + conversionFormatExtension;
    			deleteOutputDir = new File(currentFileSaveAs);			
				if(deleteOutputDir.exists())
				{
					deleteOutputDir.delete();
				}				              			
    			try
				{
					int currentPageCount = s.IMGLOW_get_pages(currentFile);
					if(currentPageCount > 0 )
					{
						for (int j = 0; j < currentPageCount; j++)
						{
							myFileProcessor[globalImageCount] = new FileProgress();
							myFileProcessor[globalImageCount]
									.setCurrentFile(currentFile);
							myFileProcessor[globalImageCount]
									.setCurrentFileSaveAs(currentFileSaveAs);
							myFileProcessor[globalImageCount].setCurrentPage(j);
							globalImageCount++;
						}
					}
					else
					{
						totalPageCount += s.IMGLOW_get_pages(currentFile);
						System.out.println(" Error in currentImage/file format: Please check your file: "  +
								ErrorCodes.getErrorMessage(currentPageCount).toUpperCase() + "\n");
					}

				}
				catch(Exception ex)
				{
					//System.out.println("Error in get pages for file " + i + " '" + currentFile + "' : " + ex.getMessage());
					System.out.println("Skipping '" + currentFile + "' due to get_pages error " + ex.getMessage());
				}
    		}
    	}
    	else
    	{
    		System.out.println("Specify valid directory");
    	}


    } 
    private Object getAllItems()
    {
    	Object item = null;
    	for(int i = 0; i < list.getModel().getSize(); i++)
    	{
    		item = list.getModel().getElementAt(i);
    	}
		return item;
    }
    
	private static int scanDirectoryFiles(File directory)
	{
		int size = 0; 
		for(File afile : directory.listFiles())
		{
			if(afile.isFile()){
				size += afile.length();
			}
		}
		File[] dis = directory.listFiles();
		for(File dir: dis)
		{
			if(dir.isDirectory()){
				size += scanDirectoryFiles(dir);
			}
		}
		return size;
	}
	
	public static float ScanFileSizeInMB(String fileName) {
		float ret = ScanFileSizeInBytes(fileName);
		ret = ret / (float) (1024 * 1024);
		return ret;
		
	}
	public static long ScanFileSizeInBytes(String fileName) {
		long ret = 0;
		File f = new File(fileName);
		if (f.isFile()) {
			return f.length();
		} else if (f.isDirectory()) {
			File[] contents = f.listFiles();
			for (int i = 0; i < contents.length; i++) {
				if (contents[i].isFile()) {
					ret += contents[i].length();
				} else if (contents[i].isDirectory())
					ret += ScanFileSizeInBytes(contents[i].getPath());
			}
		}
		return ret;
	}
	
	/**
	 * PDF-Documents and Image manipulation here.
	 */
	public int pdfNumDpi()
	{		
		Integer intDpi = (Integer)pdfInputdpiComboBox.getSelectedItem();
		pdfInputNumDPI = intDpi.intValue();
		return pdfInputNumDPI;
		
	}
	
	public int pdfNumBitsPerPixel()
	{
		Integer intBits_pix = (Integer) pdfInputbitPerPixelComboBox.getSelectedItem();
		pdfInputNumBits_pix = intBits_pix.intValue();	
		return pdfInputNumBits_pix;
	}
	
	private int docNumDpi()
	{		
		Integer intDpi = (Integer)docInputDpiComboBox.getSelectedItem();
		docInputNumDPI = intDpi.intValue();
		//System.out.println("DPI: " + docInputNumDPI);
		return docInputNumDPI;
	}
	
	private int docNumBitsPerPixel()
	{
		Integer intBits_pix = (Integer) docBitPerPixelComboBox.getSelectedItem();
		docInputNumBits_pix = intBits_pix.intValue();	
		//System.out.println(" DPI: " + docInputNumBits_pix);
		return docInputNumBits_pix;
	}
	
	public static int pclDpiInput()
	{
		Integer pclDpi = (Integer)pclDpiComboBox.getSelectedItem();
		pclInputNumDpi = pclDpi.intValue();
		return pclInputNumDpi;
	}
	
	public static int pclBppInput()
	{
		Integer pclBpp = (Integer)pclBppComboBox.getSelectedItem();
		pclInputNumBits_pix = pclBpp.intValue();
		return pclInputNumBits_pix;
	}
	
	private int pdfwOutput()
	{
		return pdf_x_status = ((Integer)wSpinner.getValue()).intValue();
	}
	
	private static int antiAliasingVal()
	{
		Integer antiVal = (Integer)aliasingComboBox.getSelectedItem();
		anti_alising = antiVal.intValue();
		return anti_alising;
	}
	
	private static int antiAliasingQualityVal()
	{
		Integer antiQualityVal = (Integer)aliasQualityComboBox.getSelectedItem();
		anti_aliasing_quality = antiQualityVal.intValue();
		return anti_aliasing_quality;
	}
	
	private int pdfhOutput()
	{	
		return pdf_y_status = ((Integer)hSpinner.getValue()).intValue();
	}
	
	private int imageWithOutput()
	{
		return x_resize_image = ((Integer)imgWidthSpinner.getValue()).intValue();
	}
	
	private int imageHeightOutput()
	{
		return y_resize_image = ((Integer)imgHeightSpinner.getValue()).intValue();
	}
	
	private static int imgeThreadhold()
	{
		return img_threshold = ((Integer)thresholdSpinner.getValue()).intValue();
	}
	
	
	/*private static int jpegCompressionOptions()
	{
		Integer jpegCompVal = (Integer)jpegCompComboBox.getSelectedItem();
		jpegCompQuality = jpegCompVal.intValue();
		return jpegCompQuality;
	}*/
	
	private static int jpegCompressionOptions()
	{
		return jpegCompQuality = ((Integer)jpegCompSpinner.getValue()).intValue();
		
	}
	private static int horizotal_Interleave()
	{
		Integer h_interleave = (Integer)h_InterleaveComboBox.getSelectedItem();
		h_interleaveValue = h_interleave.intValue();
		return h_interleaveValue;
	}
	
	private static int vertical_Interleave()
	{
		Integer v_interleave = (Integer)v_InterleaveComboBox.getSelectedItem();
		v_interleaveValue = v_interleave.intValue();
		return v_interleaveValue;
	}
	
	public static int despeckleOptions()
	{
		Integer despeckleVal = (Integer)despeckleComboBox.getSelectedItem();
		despeckleNumInput = despeckleVal.intValue();
		return despeckleNumInput;
	}
	
	private int getBrightness_decreaseSlider()
	{
		int value = 0;
		if(!brightnessDecreaseSlid.getValueIsAdjusting())
			value = brightnessDecreaseSlid.getValue();
		return value;
	}
	
	private int getContrast_increaseSlider()
	{
		 int value = 0;// = brightnessIncreaseSlid.getValue();
		 if(!contrastIncreaseSlid.getValueIsAdjusting())
			 value = contrastIncreaseSlid.getValue();
		return value;
	}
	
	private int getContrast_decreaseSlider()
	{
		int value = 0;
		if(!contrastDecreaseSlid.getValueIsAdjusting())
			value = contrastDecreaseSlid.getValue();
		return value;
	}
	private int  getBrightness_increase_Slider()
	{
		 int value = 0;// = brightnessIncreaseSlid.getValue();
		 if(!brightnessIncreaseSlid.getValueIsAdjusting())
			 value = brightnessIncreaseSlid.getValue();
		return value;
	}
	
	/*
	 * IMGLOW_set_pdf_input
	 * This method only works on PDF files, it allows for changing DPI and Bits
	 * A higher dpi yields a higher quality document that takes longer to process
	 * and take up more memory and storage space
	 * A status of zero indicates success	
	 */
	private void pdfInputPanelOptions()
	{
		if(chckbxPdfInput.isSelected() == true) //make sure we have everything selected
		{
			if(s != null && pdfInputdpiComboBox.getSelectedItem() != null && pdfInputbitPerPixelComboBox.getSelectedItem() != null)
			{
				if(batchComboBox.getSelectedIndex() == 13) //PDF index
				{
					batchComboBox.getSelectedItem();
					//set pdf input changes the DPI/Bits of the current PDF
					System.out.println(" PDF DIP: " + pdfInputNumDPI + "\n" + "PDF BitsPerPixel: "+ pdfInputNumBits_pix );
					status = s.IMGLOW_set_pdf_input(pdfNumDpi(), pdfNumBitsPerPixel());
					System.out.println("PDF Input status: " + status);

				}												
			}
		}
	}
	
	/*
	 * IMGLOW_set_document_input
	 * This method allows you to set the document DIP and BITS
	 * It is use for PDF, Word, RTF, PCL, and AFP formats
	 * A higher dpi yields a higher quality document that takes longer to process
	 * and take up more memory and storage space
	 * A status of zero indicates success	
	 * Note using this option will post a -7 error, Please ignore this error. Page manipulation still works
	 */
	private void docInputPanelOptions()
	{
		int currentPage = myFileProcessor[counter].getCurrentPage();				
		length[0] = 0;
		error[0] = 0;
		errorA[0] = 0;
		if(chckbxDocumentInput.isSelected() == true)
		{
			if(s != null && docInputDpiComboBox.getSelectedItem() != null && docBitPerPixelComboBox.getSelectedItem() != null)
			{			
				if(batchComboBox.getSelectedIndex() == 0) //AFP index
				{
					batchComboBox.getSelectedIndex();
					status = s.IMGLOW_set_document_input(docNumDpi(), docNumBitsPerPixel(), 74);//Bug					
					System.out.println("Document Input status: " + status);
					status = s.IMGLOW_set_document_input(docNumDpi(), docNumBitsPerPixel(), 59);//PDF Document		
				}
				else if(batchComboBox.getSelectedIndex() == 13) // PDF index
				{
					batchComboBox.getSelectedItem();
					status = s.IMGLOW_set_document_input(docNumDpi(), docNumBitsPerPixel(), 59);					
					System.out.println("Document Input status: " + status);	
					if(chckbxSavesearchable.isSelected() == true && batchComboBox.getSelectedIndex() == 13)
					{					
						try {
							saveSearchable(length,error,currentPage);
						} catch (Throwable e) {		
							e.printStackTrace();
						}				
					}
					
					status = s.IMGLOW_set_document_input(docNumDpi(),docNumBitsPerPixel(), 86); //Word Document
					status = s.IMGLOW_set_document_input(docNumDpi(),docNumBitsPerPixel(), 87); //RTF Document
					status = s.IMGLOW_set_document_input(docNumDpi(),docNumBitsPerPixel(), 84); //Excel Document
					
				}
				else if(batchComboBox.getSelectedIndex() == 11 || batchComboBox.getSelectedIndex() == 12)//PCL_1 and PCL_5 index
				{
					batchComboBox.getSelectedItem();
					status = s.IMGLOW_set_document_input(docNumDpi(),docNumBitsPerPixel(), 57);//PCL_1	
					status = s.IMGLOW_set_document_input(docNumDpi(),docNumBitsPerPixel(), 76);//PCL_5
					System.out.println("Document Input status: " + status);
					status = s.IMGLOW_set_document_input(docNumDpi(),docNumBitsPerPixel(), 59);//PDF document	
					status = s.IMGLOW_set_document_input(docNumDpi(),docNumBitsPerPixel(), 86); //Word Document
					status = s.IMGLOW_set_document_input(docNumDpi(),docNumBitsPerPixel(), 84); //Excel Document		
				}
				
			}
		}					
	}
	/*
	 * IMGLOW_extract_text
	 * This method extracts text from PTOCA, PCL, PDF, MS Word, AFP, RTF and MS Excel files
	 */
	public void saveSearchable(int[] length,int[] error, int pageIndex) throws Throwable
	{
		String currentFile = myFileProcessor[counter].getCurrentFile();	
		String currentFileSaveAs = myFileProcessor[counter].getCurrentFileSaveAs();			
		byte extractedText[] = s.IMGLOW_extract_text(currentFile,length,error,pageIndex);
		int saveStatus = 0;
		File outputFile = new File(currentFileSaveAs+"_searchable"+".pdf");
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(extractedText));
		saveStatus = s.IMG_decompress_bitmap(dis, pageIndex);
		saveStatus = s.IMG_save_document(outputFile.toString(),extractedText, 59);
		System.out.println("Save-searchable status: " + saveStatus);
		
	}	
	/*
	 * Simage.alias = value; / alias_quality
	 * This field sets the aliasing factor for the specified image. It is used to turn aliasing on or off for a
	 * specific image when one or more images are loaded in memory.
	 * 
	 * Simage.alias_quality
	 * This field sets the effectiveness of scale to gray and preserve black algorithms for anti-aliasing
	 * for black and white images when using the preserve black option. This field changes the
	 * amount of detail preserved when scaling down large images.
	 */
	public static void anti_aliasingPanelOptions()
	{
		if(chckbxAntialiasing.isSelected() == true)
		{
			if(s != null && aliasingComboBox.getSelectedItem() != null && aliasQualityComboBox.getSelectedItem() != null)
			{
				s.alias = antiAliasingVal();
				s.alias_quality = antiAliasingQualityVal();
				System.out.println("Anti-aliasing: " + anti_alising + "\n" + "Anti-quality: " + anti_aliasing_quality);
			}
		}
	}
	
	/*
	 * Simage.threshold
	 * This field allows the ability to set the threshold value for the Color Manipulation Methods
	 * method or when converting a color or an 8-bit grayscale image to a 1-bit format such as TIFF G4.
	 */
	public static void getImgeThreashold()
	{
		int thresholdValue;
		if(chckbxImgThreshold.isSelected() == true)
		{
			if(s != null)
			{
				thresholdValue = s.threshold = imgeThreadhold();
				System.out.println("Threshold value: " + thresholdValue);
			}
		}
	}
	
	/*
	 * IMGLOW_set_pcl_input
	 * This method sets the rendering for PCL decompression. PCL files are rendered as a bitmap at decompression time
	 * These values allow you to set the dots per inch and the pixel depth of the resulting bitmap.
	 */
	public static void getPclInputOptions()
	{
		if(chckbxPclInput.isSelected() == true)
		{
			if(s != null && pclDpiComboBox.getSelectedItem() != null && pclBppComboBox.getSelectedItem() != null)
			{
				status = s.IMGLOW_set_pcl_input(pclDpiInput(), pclBppInput());
				System.out.println("PCL DPI: " + pclInputNumDpi + "\n" + "PCL BPP: " + pclInputNumBits_pix);
				System.out.println("PCL Input status: " + status);
			}
		}
	}

	/*
	 * IMGLOW_set_jpg_interleave
	 * This method sets the JPEG interleave factor for writing JPEG images. This is the factor for
	 * decimating the blue and red chroma planes when writing out JPEG images.
	 * 
	 * IMGLOW_set_comp_quality
	 * IMGLOW_set_comp_quality is rarely used. If the size of the TIFF_JPEG output file needs 
	 * to be reduced and it is acceptable to reduce the quality of the output, you can call this method
	 */
	public static void getJpegCompressionOptions()
	{
		int interleave;
		if(chckbxJpegCompression.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMGLOW_set_comp_quality(jpegCompressionOptions());
				interleave = s.IMGLOW_set_jpg_interleave(horizotal_Interleave(), vertical_Interleave());
				System.out.println("JPEG Compression status: " + status + "\n" + "Interleave status: " + interleave);
				System.out.println("JPEG Compression quality: " + jpegCompQuality);
				System.out.println("JPEG h_Interleave: " + h_interleaveValue + "\n" + "JPEG v_Interleave: " + v_interleaveValue);
			}
		}
	}
	
	/*
	 * IMG_despeckle_bitmap
	 * This method removes noise (random pixel data) from 1-bit images. This method works only with 1-bit images
	 * The quality variable can accept a range of 100 values.
	 */
	public static void getDespeckleOptions()
	{
		if(chckbxDespeckle.isSelected() == true)
		{
			if(s != null && despeckleComboBox.getSelectedItem() != null)
			{
				status = s.IMG_despeckle_bitmap(despeckleOptions());
				if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
				{
					System.out.println("This method only works with 1-bit images");
				}
				System.out.println("Despeckle quality: " + despeckleNumInput);
				System.out.println("Despeckle status: " + status);
			}
		}
	}
	
	/*
	 * IMGLOW_set_pdf_output
	 * This method sets the output size for saving PDF files
	 * A status of zero indicates success		
	 */
	private void pdfOutputPanelOptions()
	{
		if(chckbxPdfOuput.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMGLOW_set_pdf_output(pdfwOutput(), pdfhOutput());
				System.out.println("PDF width: " + pdf_x_status + "\n" + "PDF height: " + pdf_y_status);
				System.out.println("PDF Output Status: " + status);

			}
			
		}		
		else
		{
			wSpinner.setValue(0);
			hSpinner.setValue(0);
			pdf_x_status = 0;
			pdf_y_status = 0;
			
		}
	}
	
	/*
	 * IMG_resize_bitmap will change the internal size of the current image
	 * this defers from pdf_input
	 * A status of zero indicates success	
	 */
	private void imageOutputOptions()
	{
		if(chckbxImage.isSelected() == true)
		{
			if(s != null)
			{
				status =  s.IMG_resize_bitmap(imageWithOutput(), imageHeightOutput());
				System.out.println("Image width: " + x_resize_image +"\n" + "Image height: " + y_resize_image);
				System.out.println("Image Output Status: " + status);
				
			}
		}
	}
	
	/*
	 * IMGLOW_extract_page
	 *  extracts the specified page from a multi-page PDF or TIFF
	 * A status of zero indicates success		
	 */
	private static byte[] extractVectorPage(Snowbnd snowObject, String startFile, int pageIndex, int [] error)
	{
		byte extractedPage[] = null;
		if(chckbxExtractPdf.isSelected() == true)
		{
			//A byte array containing the extracted vector PDF page
			extractedPage = snowObject.IMGLOW_extract_page(startFile, pageIndex, error);
			//Create a new file to save non-deleted pages to
			File outputFile = new File(startFile+"_extractedVectorPage"+pageIndex+".pdf");
			//Save data stored in byte array to new document
			saveFileBytes(extractedPage,outputFile);
		}
		return extractedPage;
		
	}
	
	private static byte[] extractVectorPage_1(Snowbnd snowObject, String startFile, int pageIndex, int [] error, boolean access)
	{
		byte extractedPage[] = null;
		if(chckbxExtractPdf.isSelected() == true)
		{
			//A byte array containing the extracted vector PDF page
			extractedPage = snowObject.IMGLOW_extract_page(startFile, pageIndex, error);
			//Create a new file to save non-deleted pages to
			File outputFile = new File(startFile+"_extractedPage"+pageIndex+".pdf");
			//Save data stored in byte array to new document
			//saveListFileBytes(extractedPage,outputFile);
			if(access == true)
			{
				System.out.println("Page "+(pageIndex + 1)+" extracted from "+startFile+" and saved here: " +outputFile);
			}
			
		}
		return extractedPage;
		
	}

	/*
	 * 
	 * 59 is the snowbound PDF format
	 * IMGLOW_extract_text. This method extracts text from  PTOCA, PCL, PDF, MS Word, AFP, RTF and MS Excel files.
	 * It returns the buffer of extracted text in ASCII format.
	 * IMG_save_document. This method will save to a searchable PDF file
	 * A status of zero indicates success	
	 * Note: a searchable file and the buffer of the extracted text is saved
	 */
	private byte[] extractTextDoc(Snowbnd snowObject, int[] length, int pageIndex)throws Throwable
	{	
		String currentFile = myFileProcessor[counter].getCurrentFile();	
		String currentFileSaveAs = myFileProcessor[counter].getCurrentFileSaveAs();	
		int saveStatus = 0;
		File outputFile = null;
		byte[] extractedText = null;
		int[] stat = new int[1];
		if(chckbxExtractText.isSelected() == true)
		{			
			extractedText = snowObject.IMGLOW_extract_text(currentFile,length,stat,pageIndex);
			outputFile = new File(currentFileSaveAs+"_extractedPage"+".pdf");	
			setDataObject(extractedText);
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(extractedText));//this will throw a NullPointerException, due to page error but we will skip	
			status = snowObject.IMG_decompress_bitmap(dis, pageIndex);
			if(status < 0)
			{
				System.out.println("decompression failed at: " + pageIndex + ":" + ErrorCodes.getErrorMessage(status));
				//saveStatus = s.IMG_save_bitmap(outputSourceFile.toString(), convertToFormat)
			}	 
			saveStatus = snowObject.IMG_save_document(outputFile.toString(),extractedText, 59);	
			saveStatus = errorA[0];
			if(saveStatus < 0)
			{
				System.out.println("Extraction failed at: " + pageIndex + " " + ErrorCodes.getErrorMessage(saveStatus));
			}				
			System.out.println("ExtractedText: " + length[0]);
			System.out.println("Save status: " + saveStatus);	
			System.out.println("Save error: "+ Integer.toString(errorA[0]));		
		}
		if (stat[0] < 0)
        {
			System.out.println("Error extracting a text from document: "+ ErrorCodes.getErrorMessage(stat[0]));
        }
		/*if (extractedText.length > saveStatus)
        {
            byte[] trimmedBytes = new byte[saveStatus];
            System.arraycopy(extractedText, 0, trimmedBytes, 0, saveStatus);
            extractedText = trimmedBytes;
        }		*/
		return extractedText;
	}
	
	/*
	 * Convert 8-bit grayscale
	 * PIXEL_DEPTH_UNSUPPORTED Indicates the output format does not 
	 * support the current bits per pixel. If so, the current image/page will be dropped
	 * A status of zero indicates success	
	 */
	public static void eightBitGrayScale()
	{
		if(chckbxbitGrayscale.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMG_color_gray();
				System.out.println("Color image to 8-bit " + status);

			}
			if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
			{
				System.out.println("saving_bitmap: " + status);
				System.out.println("save_bitmap error: "
						+ ErrorCodes.getErrorMessage(status));
				System.out.println("Trying to convert current page to lowest bit...");
				status = s.IMG_diffusion_mono();												
				System.out.println("Image diffusion: " + status);	
			}
		}
	}
	
	/*
	 *  IMG_antique_effect
	 *  This method replaces colors with sepia tones, which are reddish brown monochrome tints
	 *  When applied to a photo, they give the picture a warm, antique feeling.
	 * A status of zero indicates success		
	 */
	public static void antiqueEffect()
	{
		if(chckbxAntiqueEffect .isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMG_antique_effect();
				if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
				{
					System.out.println("This method only works with 1-bit images");
				}
				System.out.println("Antique effect status: " + status);
			}
		}
	}
	
	/*
	 *  IMG_promote_8
	 *  This method permanently converts the current 1, 4, 16, or 24-bit image to 8 bit.
	 *  A status of zero indicates success		
	 */
	public static void getPromoteEightBit()
	{
		if(chckbxPromoteEightBit.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMG_promote_8();
				System.out.println("Promote 8-bit status: " + status);
			}
			if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED) //do this so we don't skip a page
			{
				System.out.println("saving_bitmap: " + status);
				System.out.println("save_bitmap error: "+ ErrorCodes.getErrorMessage(status));				
				System.out.println("Trying to convert current page to lowest bit...");
				status = s.IMG_thresh_mono();												
				System.out.println("Image diffusion: " + status);	
			}
		}
	}
	
	/*
	 *  IMG_invert_bitmap
	 *  This method changes black to white and white to black.
	 *  A status of zero indicates success		
	 */
	public static void getInvertImage()
	{
		if(chckbxInvert.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMG_invert_bitmap();
				System.out.println("Invert status: " + status);
			}
		}
	}
	/*
	 *  IMG_histogram_equalize
	 *  This method is a histogram equalization which improves the dynamic range of 8-bit gray scale
	 *  images by remapping pixels based on a probability algorithm. This method works only with 8-bit images.
	 *  A status of zero indicates success		
	 */
	public static void improveGrayScale()
	{
		if(chckbxImprovebitGrayscale.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMG_histogram_equalize();
				System.out.println("Improving 8-bit GrayScale image: " + status);
			}
		}
	}
	
	/*
	 * Promote to 24-bit
	 * IMG_promote_24. This method permanently converts the current 1, 4, or 8-bit image to a 24-bit image.
	 * PIXEL_DEPTH_UNSUPPORTED Indicates the output format does not 
	 * support the current bits per pixel. If so, the current image/page will be dropped
	 * A status of zero indicates success	
	 */
	public static void promoteImageBit()
	{
		if(chckbxPromotebit.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMG_promote_24();
				System.out.println("Image changed to 24bit: " + status);

			}
			if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
			{
				System.out.println("saving_bitmap: " + status);
				System.out.println("save_bitmap error: "
						+ ErrorCodes.getErrorMessage(status));
				System.out.println("Trying to diffuse image...");
				status = s.IMG_diffusion_mono();											
				System.out.println("Image_diffusion: " + status);					
				//status = s.IMG_save_bitmap(currentFileSaveAs, convertToFormat);
			}
		}
	}
	
	/*
	 * convert RGB to CMYK
	 * IMG_rgb_to_cmyk.This method converts the current image from 24-bit RGB data to 32-bit CMYK data
	 * If the current image is not 24bit, error: PIXEL_DEPTH_UNSUPPORTED
	 * If so, the current image/page will be dropped
	 * A status of zero indicates success	
	 */
	public static void cmykImage()
	{
		if(chckbxRgbToCymk.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMG_rgb_to_cmyk();
				System.out.println("Converting current 24-bit to 32-bit CMYK data: " + status);

			}
			if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
			{
				System.out.println("saving_bitmap: " + status);
				System.out.println("save_bitmap error: "
						+ ErrorCodes.getErrorMessage(status));
				System.out.println("Trying to convert to closest bit...");
				status = s.IMG_color_gray();											
				System.out.println("Color Gray status: " + status);
				if(status == ErrorCodes.PIXEL_DEPTH_UNSUPPORTED)
				{
					System.out.println("saving_bitmap: " + status);
					System.out.println("save_bitmap error: "
							+ ErrorCodes.getErrorMessage(status));
					System.out.println("Trying to convert current page to lowest bit...");
					status = s.IMG_thresh_mono();										
					System.out.println("Converting to 1-bit status: " + status);					
				}
			}
		}
	}
	/*
	 *  IMG_diffusion_mono
	 *  This method permanently converts 4, 8, or 24 bit images to 1-bit per pixel bi-level 
	 *  images using the Stucky error diffusion technique.
	 *  A status of zero indicates success		
	 */
	public static void getImageDiffusion()
	{
		if(chckbxDuffuion.isSelected() == true)
		{
			if(s != null)
			{
				status = s.IMG_diffusion_mono();	
				System.out.println("1-Bit image conversion status: " + status);
			}
		}
		
	}
	/*
	 * IMGLOW_set_fast_convert
	 * This feature improves performance for decompression and conversion
	 * 1 = ON, 0 = OFF
	 * Only works with MO:DCA or AFP images
	 */
	public static void fastAFPConversion()
	{
		if(chckbxEnableFastConversion.isSelected() == true)
		{
			// fast convert only work on AFP
			status = s.IMGLOW_set_fast_convert(1, 74);//AFP
			System.out.println("AFP fast conversion: " + status);
		}
	}
	
	/*
	 * IMGLOW_set_fontmap_path
	 * This method programmatically sets font mapping for AFP documents
	 */
	public static void getAfpFontmap()
	{
		if(chckbxAfpFontmapping.isSelected() == true)
		{
			if(s != null && afpFontmapTextField.getText().length() > 0)
			{
				status =  s.IMGLOW_set_fontmap_path(mAfpFontmapDirectoryPath);
				System.out.println("AFP Font mapping Changed: " + status);	
			}
		}
	}
	
	
	/*
	 * IMGLOW_set_brightness
	 * This method sets the brightness of the current image.
	 * 
	 * IMGLOW_set_contrast
	 * This method sets the contrast of the current image.
	 */
	public void brightnessContrastOptions()
	{
		int brightness = 0;
		int contrast = 0;
		if(rdbtnBrightness_increase.isSelected() == true)
		{
			if(s != null)
			{
				brightness = s.IMGLOW_set_brightness(getBrightness_increase_Slider());
				System.out.println("Increase brightness: " + brightness);
			}
		}
		if (rdbtnBrightness_increase.isSelected() == true && radioContrast_increase.isSelected() == true )
		{	
			brightness = s.IMGLOW_set_brightness(getBrightness_increase_Slider());
			System.out.println("Increase brightness: " + brightness);
			contrast = s.IMGLOW_set_contrast(getContrast_increaseSlider());
			System.out.println("Increase contrast: " + contrast);
		}
			
		if(rdbtnBrightness_decrease.isSelected() == true && radioContrast_decrease.isSelected() == true)
		{		
			brightness = s.IMGLOW_set_brightness(getBrightness_decreaseSlider());
			System.out.println("Decrease brightness: " + brightness);
			contrast = s.IMGLOW_set_contrast(getContrast_decreaseSlider());
			System.out.println("Decrease constrast: " + contrast);		
		}
		
		if(rdbtnBrightness_increase.isSelected() == true && radioContrast_decrease.isSelected() == true)
		{		
			brightness = s.IMGLOW_set_brightness(getBrightness_increase_Slider());
			System.out.println("Increase brightness: " + brightness);
			contrast = s.IMGLOW_set_contrast(getContrast_decreaseSlider());
			System.out.println("Decrease constrast: " + contrast);		
		}
		if(rdbtnBrightness_decrease.isSelected() == true && radioContrast_increase.isSelected() == true)
		{		
			brightness = s.IMGLOW_set_brightness(getBrightness_decreaseSlider());
			System.out.println("Decrease brightness: " + brightness);
			contrast = s.IMGLOW_set_contrast(getContrast_increaseSlider());
			System.out.println("Increase constrast: " + contrast);		
		}
		
		else if(rdbtnBrightness_decrease.isSelected() == true)
		{
			if(s != null)
			{
				brightness = s.IMGLOW_set_brightness(getBrightness_decreaseSlider());
				System.out.println("Decrease brightness: " + brightness);
			}
		}
		else if(radioContrast_increase.isSelected() == true)
		{
			if(s != null)
			{
				contrast = s.IMGLOW_set_contrast(getContrast_increaseSlider());
				System.out.println("Increase contrast: " + contrast);
			}
		}
		else if(radioContrast_decrease.isSelected() == true)
		{
			if(s != null)
			{
				contrast = s.IMGLOW_set_contrast(getContrast_decreaseSlider());
				System.out.println("Decrease contrast: " + contrast);
			}
		}
		
	}
	
	/*
	 * IMGLOW_append_page
	 * This method appends a specified page to an existing multi-page PDF or TIFF document
	 * contained in an existing DataInputStream
	 */
	public void appendNonVectorPage(Snowbnd snowObject, int pageIndex,int bitMapPageIndex, int [] error)
	{
		//Decompress image
		String currentFile = mSourcepdfPath + System.getProperty("file.separator");
		File getCurrentFile = new File(currentFile);
		String otherfile = mTargetpdfPath + System.getProperty("file.separator");
		File getotherFile = new File(otherfile);
		int pageCount = snowObject.IMGLOW_get_pages(otherfile);
		if (pageCount < 0)
        {
            /* Return values less than zero indicate an error */
            System.out.println("Error in counting pages: "   + ErrorCodes.getErrorMessage(pageCount));
            return;        
        }
        else
        {
        	System.out.println("-------------------------------------------------------");
            System.out.println("Target document has " + pageCount   + " pages.");
        }
		for (bitMapPageIndex = 0; bitMapPageIndex < pageCount; bitMapPageIndex++)
		{	
			/* pageIndex is zero-based */
			System.out.println("Processing page at pageIndex " + bitMapPageIndex);
			try
			{
				 nstat = snowObject.IMG_decompress_bitmap(otherfile, bitMapPageIndex);		
				 mPreviewPanel.repaint();
				/* Check the decompressStatus for an error */
				if (nstat < 0)
				{
					/* Return values less than zero indicate an error */
					System.out.println("Error decompressing page at pageIndex="+ pageIndex + " : "+ ErrorCodes.getErrorMessage(nstat));					
					continue;
				}
				if(nstat >= 0)
				{
					//Save decompressed image to byte array as PDF
					System.out.println(getotherFile.getName() + " Successfully decompressed.");
					byte[] savedPdfBytes = snowObject.IMG_save_bitmap(initialBufferSize, expandBufferSize, Defines.PDF, error);
					//Append non-vector PDF page to vector PDF
					snowObject.IMGLOW_append_page(currentFile,savedPdfBytes,Defines.PDF);
					System.out.println(getotherFile.getName()+ " appended to "+getCurrentFile.getName());
					System.out.println("---------------------------------------------------------------");
				}
				else
				{
					System.out.println("Error decompressing image. Error code "+nstat);
					//System.exit(0);
				}
			}
			catch (Exception e)
            {
                System.out
                    .println("Exception thrown at pageIndex=" + pageIndex);
                e.printStackTrace();
            }
		}		
	//	nstat = snowObject.IMG_decompress_bitmap(bitMap,bitMapPageIndex);		
	}
	
	/*
	 * IMGLOW_append_page
	 * This method appends a specified page to an existing multi-page PDF or TIFF document
	 * contained in an existing DataInputStream
	 */
	public void appendVectorPage(Snowbnd snowObject, int pageIndex, int [] error)
	{
	
		String currentFile = mSourcepdfPath + System.getProperty("file.separator");
		//File getCurrentFile = new File(currentFile);
		int pageCount = snowObject.IMGLOW_get_pages(currentFile);
		if (pageCount < 0)
        {
            /* Return values less than zero indicate an error */
            System.out.println("Error in counting pages: "
                + ErrorCodes.getErrorMessage(pageCount));
            return;
        }
        else
        {
        	System.out.println("---------------------------------------------------------------");
            System.out.println("Source document has " + pageCount   + " pages.");      
        }
		
		for (pageIndex = 0; pageIndex < pageCount; pageIndex++)
		{
			/* pageIndex is zero-based */
			System.out.println("Processing page at pageIndex " + pageIndex);
			try
			{
				int decompressStatus = snowObject
						.IMG_decompress_bitmap(currentFile, pageIndex);
				 mPreviewPanel.repaint();
				/* Check the decompressStatus for an error */
				if (decompressStatus < 0)
				{
					/* Return values less than zero indicate an error */
					System.out.println("Error decompressing page at pageIndex="+ pageIndex + " : "+ pageIndex + " : "+ ErrorCodes.getErrorMessage(decompressStatus));																	
					continue; 
				}
				//Append a vector PDF page extracted from the same vector PDF
				byte[] savedPdfBytes = snowObject.IMG_save_bitmap(initialBufferSize, expandBufferSize, Defines.PDF, error);
				//File outputFile = new File(savedPdfBytes+"_mergedPage"+pageIndex+".pdf");
				//snowObject.IMGLOW_append_page(currentFile,mergeExtractedPath(snowObject,currentFile, pageIndex,error,accessDirectly),Defines.PDF );
				snowObject.IMGLOW_append_page(currentFile,savedPdfBytes,Defines.PDF);			
				//System.out.println("Page "+(pageIndex)+" appeneded to "+ getCurrentFile.getName());
			}
			catch (Exception e)
            {
                System.out
                    .println("Exception thrown at pageIndex=" + pageIndex);
                e.printStackTrace();
            }
		}	
	}	
	
	public void remove()
	{
		if(list.getSelectedIndices().length > 0) {
			int[] index = list.getSelectedIndices();
			for (int i = index.length-1; i >=0; i--) {
				listModel.removeElementAt(i);
			}
		}
		list.updateUI();
	}
	
	/***************************************************************************
	 * Save byte array to file
	 **************************************************************************/
	public static void saveFileBytes(byte[] data, File file)
	{
		File desDir = new File(mOutputDirectoryPath);
		if (data == null)
		{
			return;
		}
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			//file.renameTo(new File(desDir + file.getName()));
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//move this manually to your outputfolder
		//might cause performance issues
		file.renameTo(new File(desDir, file.getName()));
									
	}
	
	
	public static void moveFiles(File sourceLocation , File targetLocation)
		    throws IOException {
		
			File newfile=new File(targetLocation+"/"+sourceLocation.getName());
		    try {
		        Files.move(sourceLocation.toPath(),newfile.toPath(),REPLACE_EXISTING);
		    } catch (IOException e1) {
		       
		        e1.printStackTrace();
		    }
		           
	}		
	private static void copFiles(File source, File dest) throws IOException,  IllegalArgumentException
	{
		FileInputStream in = new FileInputStream(source);
		FileOutputStream out = new FileOutputStream(dest+"/"+source.getName());
		try {
			int doneCnt = -1, bufSize = 50000;
			byte buf[] = new byte[bufSize];
			while ((doneCnt = in.read(buf, 0, bufSize)) >= 0)
				if ( doneCnt == 0 )  Thread.yield();
				else  out.write(buf, 0, doneCnt);
			out.flush();
		}
		finally {
			try { in.close(); } catch (IOException e) {}
			try { out.close(); } catch (IOException e) {}
		}

	}
	
	private void deleteDirectoryFiles()
	{
		
		File directory = new File(mInputDirectoryPath);
		File[] allFiles = directory.listFiles();
		for(File afile : allFiles)
		{
			if(afile.isFile()){
				afile.delete();
			}
			else if(mInputDirectoryPath.length() > 0)
			{
				System.out.println("Could not delete " + afile);
			}
		}	
	}
	
	//we call this method to create the right extension
	//when splitting documents
	public String getFormatsNames()

	{
		String formatsType = null;	
		if(batchComboBox.getSelectedIndex() == 1)
		{
			formatsType = ".bmp";
		}
		else if(batchComboBox.getSelectedIndex() == 2)
		{
			formatsType = ".cal";
		}
		else if(batchComboBox.getSelectedIndex() == 3)
		{
			formatsType = ".eps";
		}
		else if(batchComboBox.getSelectedIndex() == 4)
		{
			formatsType = ".eps";
		}

		else if(batchComboBox.getSelectedIndex() == 5)
		{
			formatsType = ".eps";
		}
		else if(batchComboBox.getSelectedIndex() == 6)
		{
			formatsType = ".gif";
		}
		else if(batchComboBox.getSelectedIndex() == 7)
		{
			formatsType = ".ica";
			
		}
		else if(batchComboBox.getSelectedIndex() == 8)
		{
			formatsType = ".jpg";
		}
		else if(batchComboBox.getSelectedIndex() == 9)
		{
			formatsType = ".jp2";
		}
		else if(batchComboBox.getSelectedIndex() == 10)
		{
			formatsType = ".mca";
		}
		else if(batchComboBox.getSelectedIndex() == 11)
		{
			formatsType = ".pcl";
		}
		
		else if(batchComboBox.getSelectedIndex() == 12)
		{
			formatsType = ".pcl";
		}
		else if(batchComboBox.getSelectedIndex() == 13)
		{
			formatsType = ".pdf";
		}
		else if(batchComboBox.getSelectedIndex() == 14)
		{
			formatsType = ".pdf";
		}
		else if(batchComboBox.getSelectedIndex() == 15)
		{
			formatsType = ".png";
		}
		else if(batchComboBox.getSelectedIndex() == 16)
		{
			formatsType = ".tif";
		}

		else if(batchComboBox.getSelectedIndex() == 17)
		{
			formatsType = ".tif";
		}

		else if(batchComboBox.getSelectedIndex() == 18)
		{
			formatsType = ".tif";
		}
		else if(batchComboBox.getSelectedIndex() == 19)
		{
			formatsType = ".tif";
		}
		else if(batchComboBox.getSelectedIndex() == 20)
		{
			formatsType = ".tif";
		}
		else if(batchComboBox.getSelectedIndex() == 21)
		{
			formatsType = ".tif";
		}
		else if(batchComboBox.getSelectedIndex() == 22)
		{
			formatsType = ".tif";
		}
		else if(batchComboBox.getSelectedIndex() == 23)
		{
			formatsType = ".tif";
		}
		else if(batchComboBox.getSelectedIndex() == 24)
		{
			formatsType = ".tif";
		}
		return formatsType;
	}

/*	public int geFormatTypes()

	{
		int formatsType = 0;	
		if(batchComboBox.getSelectedIndex() == 1)
		{
			formatsType = Defines.BMP_UNCOMPRESSED;
		}
		else if(batchComboBox.getSelectedIndex() == 2)
		{
			formatsType = Defines.CALS;
		}
		else if(batchComboBox.getSelectedIndex() == 3)
		{
			formatsType = Defines.EPS;
		}
		else if(batchComboBox.getSelectedIndex() == 4)
		{
			formatsType = Defines.EPS_BITMAP;
		}

		else if(batchComboBox.getSelectedIndex() == 5)
		{
			formatsType = Defines.EPS_BITMAP_G4;
		}
		else if(batchComboBox.getSelectedIndex() == 6)
		{
			formatsType = Defines.GIF;
		}
		else if(batchComboBox.getSelectedIndex() == 7)
		{
			formatsType = Defines.ICONTYPE;
			
		}
		else if(batchComboBox.getSelectedIndex() == 8)
		{
			formatsType = Defines.JPEG;
		}
		else if(batchComboBox.getSelectedIndex() == 9)
		{
			formatsType = Defines.JPEG2000;
		}
		else if(batchComboBox.getSelectedIndex() == 10)
		{
			formatsType = Defines.MODCA_IOCA;
		}
		else if(batchComboBox.getSelectedIndex() == 11)
		{
			formatsType = Defines.PCL_1;
		}
		
		else if(batchComboBox.getSelectedIndex() == 12)
		{
			formatsType = Defines.PCL_5;
		}
		else if(batchComboBox.getSelectedIndex() == 13)
		{
			formatsType = Defines.PDF;
		}
		else if(batchComboBox.getSelectedIndex() == 14)
		{
			formatsType = Defines.PDF_LZW;
		}
		else if(batchComboBox.getSelectedIndex() == 15)
		{
			formatsType = Defines.PNG;
		}
		else if(batchComboBox.getSelectedIndex() == 16)
		{
			formatsType = Defines.TIFF_G3_FAX;
		}

		else if(batchComboBox.getSelectedIndex() == 17)
		{
			formatsType = Defines.TIFF_G4_FAX;
		}

		else if(batchComboBox.getSelectedIndex() == 18)
		{
			formatsType = Defines.TIFF_G4_FAX_FO;
		}
		else if(batchComboBox.getSelectedIndex() == 19)
		{
			formatsType = Defines.TIFF_JBIG;
		}
		else if(batchComboBox.getSelectedIndex() == 20)
		{
			formatsType = Defines.TIFF_JPEG;
		}
		else if(batchComboBox.getSelectedIndex() == 21)
		{
			formatsType = Defines.TIFF_JPEG7;
		}
		else if(batchComboBox.getSelectedIndex() == 22)
		{
			formatsType = Defines.TIFF_LZW;
		}
		else if(batchComboBox.getSelectedIndex() == 23)
		{
			formatsType = Defines.TIFF_PACK;
		}
		else if(batchComboBox.getSelectedIndex() == 24)
		{
			formatsType = Defines.TIFF_UNCOMPRESSED;
		}
		return formatsType;
	}*/
	private class ExitBatchConvert extends WindowAdapter {  
        public void windowClosing( WindowEvent e ) {  
            int option = JOptionPane.showOptionDialog(mBatchConvertFrame,  "Are you sure you want to quit?","Exit Dialog", 
            		JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE, null, null,null );                                        		                                 
            if( option == JOptionPane.YES_OPTION ) {  
                System.exit( 0 );  
            }  
        }  
    }
}
