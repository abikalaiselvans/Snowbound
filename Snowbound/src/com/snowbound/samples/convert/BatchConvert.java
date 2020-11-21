/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */
package com.snowbound.samples.convert;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JProgressBar;

import com.snowbound.samples.common.FileProcessor;

import Snow.Format;
import Snow.FormatHash;
import Snow.Snowbnd;

/**
 * The main class for our BatchConvert sample extends java.awt.Frame
 */
public class BatchConvert extends Frame
{
    private static final long serialVersionUID = -2257537939627604263L;
    static BatchConvert mBatchConvertFrame = null;
    PreviewPanel mPreviewPanel = null;
    Panel mMainPanel = null;
    Panel mWidgetPanel = null;
    Snowbnd s = null;
    public String mInputDirPath = null;
    public String mOutputDirPath = null;
    Label mInputDirLabel = null;
    Label mOutputDirLabel = null;
    Label mOutputFormatLabel = null;
    Label mInputStatusLabel = null;
    Label mOutputStatusLabel = null;
    TextField mInputDirField = null;
    TextField mOutputDirField = null;
    Button mInputDirButton = null;
    Button mOutputDirButton = null;
    Button mStartButton = null;
    Button mStopButton = null;
    List mOutputFormatList = null;
    JProgressBar mProgressBar = null;
    JFileChooser mFileBrowser = null;
    Timer myTimer = null;
    int counter = 0;
    int totalPageCount = 0;
    int convertToFormat = 0;
    int status = -1;
    boolean gDone = false;
    String[] inputFileList = null;
    String conversionFormatExtension = null;
    String mInputDirectoryPath = null;
    String mOutputDirectoryPath = null;
    FileProcessor[] myFileProcessor = null;

    public BatchConvert()
    {
        mFileBrowser = new JFileChooser();
        mFileBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        mBatchConvertFrame = this;
        s = new Snowbnd();
    }

    /**
     * PreviewPanel is used for drawing a preview thumbnail of the current
     * image being processed.  PreviewPanel should update with each step
     * of the progress bar.
     */
    class PreviewPanel extends Panel
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
                g.setColor(Color.WHITE);
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
            paint(g);
        }
    }

    public void initializeFormatsList()
    {
        mOutputFormatList = new List();
        FormatHash formatHash = FormatHash.getInstance();
        String[] saveFormats = formatHash.getAvailibleSaveFormats();
        for (int index = 0; index < saveFormats.length; index++)
        {
            mOutputFormatList.add(saveFormats[index]);
        }
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
			int nFiles = countFilesInDirectory(inputDirectory);
			//System.out.println(nFiles);
            for (int i = 0; i < inputFileList.length; i++)
            {
                String currentFile = mInputDirectoryPath
                    + System.getProperty("file.separator") + inputFileList[i];
		
				//System.out.println(currentFile);
				//Skip directories. Get page count of each file
				if (s.IMGLOW_get_pages(currentFile) < 0) 
				{
					totalPageCount=totalPageCount;
				}
					else 
				{
            		totalPageCount += s.IMGLOW_get_pages(currentFile);
		  		}
            }
            myFileProcessor = new FileProcessor[totalPageCount];
            int globalImageCount = 0;
            for (int i = 0; i < inputFileList.length; i++)
            {
                String currentFile = mInputDirectoryPath
                    + System.getProperty("file.separator") + inputFileList[i];
                String currentFileSaveAs = mOutputDirectoryPath
                    + System.getProperty("file.separator") + inputFileList[i]
                    + "." + conversionFormatExtension;
                int currentPageCount = s.IMGLOW_get_pages(currentFile);
                for (int j = 0; j < currentPageCount; j++)
                {
                    myFileProcessor[globalImageCount] = new FileProcessor();
                    myFileProcessor[globalImageCount]
                        .setCurrentFile(currentFile);
                    myFileProcessor[globalImageCount]
                        .setCurrentFileSaveAs(currentFileSaveAs);
                    myFileProcessor[globalImageCount].setCurrentPage(j);
                    globalImageCount++;
                }
            }
        }
        else
        {
            System.out.println("Specify valid directory");
        }
    }

    public int determineConversionFormat()
    {
        String outputFormat = mOutputFormatList.getSelectedItem();
        Format format = FormatHash.getInstance().getFormat(outputFormat);
        conversionFormatExtension = format.getExtension();
        return format.getFormatCode();
    }

    public void layoutMainPanel()
    {
        mMainPanel = new Panel();
        mMainPanel.setLayout(new GridBagLayout());
        mMainPanel.setBounds(0,
                             0,
                             this.getBounds().width,
                             this.getBounds().height);
        mMainPanel.setBackground(Color.WHITE);
        mBatchConvertFrame.add(mMainPanel);
    }

    public void layoutWidgetPanel()
    {
        mWidgetPanel = new Panel();
        mWidgetPanel.setBounds(0,
                               1,
                               mBatchConvertFrame.getWidth(),
                               mBatchConvertFrame.getHeight());
        mWidgetPanel.setLayout(null);
        mInputDirLabel = new Label("Input Directory: ");
        mInputDirLabel.setBounds(5, 5, 110, 15);
        mOutputDirLabel = new Label("Output Directory: ");
        mOutputDirLabel.setBounds(5, 35, 110, 15);
        mInputDirButton = new Button("Browse...");
        mInputDirButton.setBounds(mWidgetPanel.getWidth() - 100, 5, 75, 20);
        mInputDirButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                mFileBrowser.setCurrentDirectory(new File(System
                    .getProperty("user.dir")));
                mFileBrowser.setDialogTitle("Select Input Directory");
                int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
                if (status == JFileChooser.APPROVE_OPTION)
                {
                    mInputDirField.setText(mFileBrowser.getSelectedFile()
                        .toString());
                    mInputDirectoryPath = mFileBrowser.getSelectedFile()
                        .toString();
                }
            }
        });
        mOutputDirButton = new Button("Browse...");
        mOutputDirButton.setBounds(mWidgetPanel.getWidth() - 100, 30, 75, 20);
        mOutputDirButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                mFileBrowser.setCurrentDirectory(new File(System
                    .getProperty("user.dir")));
                mFileBrowser.setDialogTitle("Select Output Directory");
                int status = mFileBrowser.showDialog(mBatchConvertFrame, "OK");
                if (status == JFileChooser.APPROVE_OPTION)
                {
                    mOutputDirField.setText(mFileBrowser.getSelectedFile()
                        .toString());
                    mOutputDirectoryPath = mFileBrowser.getSelectedFile()
                        .toString();
                }
            }
        });
        mInputDirField = new TextField("Specify input directory to process...");
        mInputDirField.setBounds(mInputDirLabel.getX()
            + mInputDirLabel.getWidth() + 5, 5, (mInputDirButton.getX() - 10)
            - (mInputDirLabel.getX() + mInputDirLabel.getWidth() + 5), 20);
        mInputDirField.addFocusListener(new FocusListener()
        {
            public void focusLost(FocusEvent e)
            {
                mInputDirectoryPath = mInputDirField.getText();
            }

            public void focusGained(FocusEvent e)
            {
                //intentionally left blank
            }
        });
        mOutputDirField = new TextField("Specify output directory to convert to...");
        mOutputDirField.setBounds(mOutputDirLabel.getX()
                                      + mOutputDirLabel.getWidth() + 5,
                                  35,
                                  (mOutputDirButton.getX() - 10)
                                      - (mOutputDirLabel.getX()
                                          + mOutputDirLabel.getWidth() + 5),
                                  20);
        mOutputDirField.addFocusListener(new FocusListener()
        {
            public void focusLost(FocusEvent e)
            {
                mOutputDirectoryPath = mOutputDirField.getText();
            }

            public void focusGained(FocusEvent e)
            {
                //intentionally left blank
            }
        });
        mOutputFormatLabel = new Label("Select Output Format: ");
        mOutputFormatLabel.setBounds(mWidgetPanel.getWidth() / 2 + 5,
                                     mOutputDirButton.getY()
                                         + mOutputDirButton.getHeight() + 15,
                                     mWidgetPanel.getWidth() / 2 - 30,
                                     15);
        mInputStatusLabel = new Label("Input Status: ");
        mInputStatusLabel.setBounds(5,
                                    mOutputDirButton.getY()
                                        + mOutputDirButton.getHeight() + 15,
                                    mWidgetPanel.getWidth() / 5,
                                    15);
        mOutputStatusLabel = new Label("Output Status: ");
        mOutputStatusLabel.setBounds(mInputStatusLabel.getX()
                                         + mInputStatusLabel.getWidth(),
                                     mOutputDirButton.getY()
                                         + mOutputDirButton.getHeight() + 15,
                                     mWidgetPanel.getWidth() / 2 - 30,
                                     15);
        mOutputFormatList.setBounds(mWidgetPanel.getWidth() / 2 + 5,
                                    mOutputFormatLabel.getY()
                                        + mOutputFormatLabel.getHeight() + 15,
                                    mWidgetPanel.getWidth() / 2 - 30,
                                    200);
        mOutputFormatList.select(0);
        //		mOutputFormatList.addActionListener(new ActionListener() {
        //			public void actionPerformed( ActionEvent e) {
        //				int format = ((Integer)mFormatHash.snowboundFormatsPrettyPrint.get(mOutputFormatList.getSelectedItem())).intValue();
        //			}
        //		});
        mPreviewPanel = new PreviewPanel();
        mPreviewPanel.setBackground(Color.GRAY);
        mPreviewPanel.setBounds(5,
                                mInputStatusLabel.getY()
                                    + mInputStatusLabel.getHeight() + 15,
                                mWidgetPanel.getWidth() / 2 - 30,
                                200);
        mProgressBar = new JProgressBar();
        mProgressBar.setBounds(25, mPreviewPanel.getY()
            + mPreviewPanel.getHeight() + 15, mWidgetPanel.getWidth() - 60, 15);
        mStartButton = new Button("Start Conversion");
        mStartButton.setBounds(mWidgetPanel.getWidth() / 2 - 125, mProgressBar
            .getY()
            + mProgressBar.getHeight() + 15, 100, 25);
        mStartButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (mInputDirectoryPath != null && mOutputDirectoryPath != null)
                {
                    File inputDir = new File(mInputDirectoryPath);
                    File outputDir = new File(mOutputDirectoryPath);
                    if (true)
                    //                    if (inputDir.isDirectory() && outputDir.isDirectory())
                    {
                        mStopButton.setEnabled(true);
                        mStartButton.setEnabled(false);
                        mProgressBar.setEnabled(true);
                        gDone = false;
                        int delay = 0;
                        int period = 1000;
                        myTimer = new Timer();
                        convertToFormat = determineConversionFormat();
                        preprocessDirectory();
                        mProgressBar.setMaximum(myFileProcessor.length);
                        /* Note the delay of 1000 ms before processing next image. */
                        /* This sample does NOT demostrate the speed of RasterMaster */
                        myTimer.schedule(new TimerTask()
                        {
                            public void run()
                            {
                                if (counter < myFileProcessor.length && !gDone)
                                {
                                    String currentFile = myFileProcessor[counter]
                                        .getCurrentFile();
                                    String currentFileSaveAs = myFileProcessor[counter]
                                        .getCurrentFileSaveAs();
                                    int currentPage = myFileProcessor[counter]
                                        .getCurrentPage();
                                    s = new Snowbnd();
                                    s.setFrame(mBatchConvertFrame);
                                    /* you can adjust the input DPI and BPP for a specified format prior to decompression */
                                    //s.IMGLOW_set_document_input(dpi, bits_pix, format);
				    /* you can set the OOXML license prior to decompression */
				    //status = s.IMGLOW_set_ooxml_license("C:\\wbuildsrc\\ThirdParty\\Aspose\\License\\Aspose.Total.Product.Family.lic");
                                    status = s
                                        .IMG_decompress_bitmap(currentFile,
                                                               currentPage);
                                    mInputStatusLabel.setText("Input Status: "
                                        + status);
                                    /* after decompression, you can adjust the bit depth */
                                    //s.IMG_promote_24();
                                    //s.IMG_color_gray();
                                    mPreviewPanel.repaint();
                                    status = s
                                        .IMG_save_bitmap(currentFileSaveAs,
                                                         convertToFormat);
                                    mOutputStatusLabel
                                        .setText("Output Status: " + status);
                                    counter++;
                                    mProgressBar.setValue(counter);
                                    Rectangle progressRect = mProgressBar
                                        .getBounds();
                                    progressRect.x = 0;
                                    progressRect.y = 0;
                                    mProgressBar.paintImmediately(progressRect);
                                }
                                else
                                {
                                    myTimer.cancel();
                                    mStartButton.setEnabled(true);
                                    mStopButton.setEnabled(false);
                                    mProgressBar.setEnabled(false);
                                    counter = 0;
                                    totalPageCount = 0;
                                }
                            }
                        },
                                         delay,
                                         period);
                    }
                    else
                    {
                        System.out
                            .println("Directory Paths do not both point to directories.");
                        System.out
                            .println(" InputDirectory: " + inputDir.getName()
                                + " " + inputDir.isDirectory());
                        System.out.println(" OutputDirectory: "
                            + outputDir.getName() + " "
                            + outputDir.isDirectory());
                    }
                }
                else
                {
                    System.out
                        .println("One or more directory paths not specified.");
                    System.out.println(" InputDirectory: "
                        + mInputDirectoryPath);
                    System.out.println(" OutputDirectory: "
                        + mOutputDirectoryPath);
                }
            }
        });
        mStopButton = new Button("Stop Conversion");
        mStopButton.setBounds(mWidgetPanel.getWidth() / 2 + 25, mProgressBar
            .getY()
            + mProgressBar.getHeight() + 15, 100, 25);
        mStopButton.setEnabled(false);
        mStopButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                gDone = true;
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
                mProgressBar.setEnabled(false);
                totalPageCount = 0;
                counter = 0;
            }
        });
        mWidgetPanel.add(mInputDirLabel);
        mWidgetPanel.add(mOutputDirLabel);
        mWidgetPanel.add(mInputDirField);
        mWidgetPanel.add(mOutputDirField);
        mWidgetPanel.add(mInputDirButton);
        mWidgetPanel.add(mOutputDirButton);
        mWidgetPanel.add(mOutputFormatLabel);
        mWidgetPanel.add(mInputStatusLabel);
        mWidgetPanel.add(mOutputStatusLabel);
        mWidgetPanel.add(mOutputFormatList);
        mWidgetPanel.add(mPreviewPanel);
        mWidgetPanel.add(mProgressBar);
        mWidgetPanel.add(mStartButton);
        mWidgetPanel.add(mStopButton);
        mMainPanel.add(mWidgetPanel);
    }

    public void layoutPanels()
    {
        layoutMainPanel();
        layoutWidgetPanel();
    }

    public void addListeners()
    {
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    public void execute()
    {
        this.setTitle("Snowbound Software :: Batch Convert");
        this.setSize(650, 400);
        this.setResizable(false);
        initializeFormatsList();
        layoutPanels();
        addListeners();
        this.setVisible(true);
    }

	// Get number of files in directory.  Do not count directories in the file count.
    public static int countFilesInDirectory(File directory) {
      int count = 0;
      for (File file : directory.listFiles()) {
          if (file.isFile()) {
              count++;
          }
          if (file.isDirectory()) {
              count = count;
          }
      }
      return count;
  }

    public static void main(String[] argv)
    {
        mBatchConvertFrame = new BatchConvert();
        mBatchConvertFrame.execute();
    }
}
