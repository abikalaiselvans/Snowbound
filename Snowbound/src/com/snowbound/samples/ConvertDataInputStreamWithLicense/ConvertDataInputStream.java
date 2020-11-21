/**
 * Copyright (C) 2012-2017 by Snowbound Software Corp. All rights reserved.
 */
package com.snowbound.samples.ConvertDataInputStreamWithLicense;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import Snow.Defines;
import Snow.ErrorCodes;
import Snow.Format;
import Snow.FormatHash;
import Snow.Snowbnd;

public class ConvertDataInputStream {

	Snowbnd snowImage = new Snowbnd();
	private int status = -1;
	private static int initialBufferSize = 50000;
	private static int increaseBufferSize = 20000;
	private String dataVal;
	int nStat;

	public ConvertDataInputStream()
	{
		super();
		nStat = snowImage.IMGLOW_set_ooxml_license();
		System.out.println("License File status = " + nStat + ", " + ErrorCodes.getErrorMessage(nStat));
		String licensePath = snowImage.IMGLOW_get_ooxml_license_path();
		//if(nStat == ErrorCodes.OOXML_LICENSE_NOT_FOUND)break;
		System.out.println("License File Path = " + licensePath);
	}

	public byte[] convertStream(byte[] inFile, String fileName, int fileType)
	{
		Snowbnd snowImage = new Snowbnd();
		int index = 0;
		int totalPageCount = 0;
		int convByteArraySize = 0;
		int formatCode = 0;
		int totalSize = 0;
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(inFile));
		//status = snowImage.IMGLOW_set_ooxml_license_input_stream(inputStream);
		//System.out.println("DataInputStream LicenseTest: " + status);

		status = snowImage.IMGLOW_get_filetype(inputStream);

		status = snowImage.IMG_decompress_bitmap(inputStream, index);

		FormatHash formatHash = FormatHash.getInstance();
		Format inputExt = formatHash.getFormat(fileType);
		formatHash.getFormatName(formatCode);
		System.out.println((new StringBuilder("Converting File to: ")).append(fileName).append(" ").append(inputExt.getFormatName()));

		

		if (totalPageCount > 1)
		{
			System.out.println("Saving as multipage document");
			if (!formatHash.isValidMultiFormat(formatCode))
			{
				System.out.println("WARNING: "
						+ formatHash.getFormatName(formatCode)
						+ " may not be a valid multipage format!");
			}
		}
		int[] error = new int[1];
		byte[] data = snowImage.IMG_save_bitmap(initialBufferSize, increaseBufferSize, fileType, error);


		totalPageCount = snowImage.IMGLOW_get_pages(inputStream);
		if (totalPageCount < 0)
		{
			/* Return values less than zero indicate an error */
			System.out.println("Error in counting pages: "
					+ ErrorCodes.getErrorMessage(totalPageCount));
		}
		for(index = 0; index < totalPageCount; index++)
		{
			System.out.println("Processing page at pageIndex " + index);
			if(nStat == ErrorCodes.OOXML_LICENSE_NOT_FOUND)break;

			try
			{
				status = snowImage.IMG_decompress_bitmap(inputStream, index);
				
				if (status < 0)
				{
					System.out.println("Error decompressing Image: "
							+ ErrorCodes.getErrorMessage(status));
					continue;
				}
				
				totalSize = error[0];
				data = snowImage.IMG_save_bitmap(data, increaseBufferSize, fileType,  error);
				totalSize = error[0];
				convByteArraySize = error[0];
				if (convByteArraySize < 0)
				{
					System.out.println("Error saving Image: "
							+ ErrorCodes.getErrorMessage(convByteArraySize));
				}
				else
				{
					System.out.println("pageIndex: " + convByteArraySize + " bytes saved successfully");

				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}       

		}
		if(data.length > totalSize)
		{
			byte[] trimmedBytes = new byte[totalSize];
			System.arraycopy(data, 0, trimmedBytes, 0, totalSize);
			data = trimmedBytes;
		}
		return data;
	}
	public boolean moreData()
	{
		return (dataVal != null);
	}

	public static byte[] saveFile(String input) throws IOException
	{
		File afile = new File(input);
		FileInputStream is = new FileInputStream(afile);
		long length = afile.length();
		if (length > Integer.MAX_VALUE) {
		}
		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read (bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			System.out.println("Could not Read file "+ afile.getName());
		}
		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		ConvertDataInputStream convertStream = new ConvertDataInputStream();
		byte[] buffer;
		byte[] inputStream;
		String inputFile = "C:\\temp\\bullets.docx";
		RandomAccessFile random = new RandomAccessFile("C:\\temp\\bullets.docx", "r");
		if(new File(inputFile).exists() == true)
		{
			System.out.println("Reading inputFile: " + inputFile);
			try{
				buffer = ConvertDataInputStream.saveFile(inputFile);
				byte[] bufLength = new byte[(int)buffer.length];
				random.read(bufLength);//read the byte located at the current position

				inputStream = convertStream.convertStream(bufLength, "'Format Ext'", Defines.PDF);

				BufferedOutputStream bufferStream = new BufferedOutputStream(new FileOutputStream("C:\\temp\\bullets.pdf"));
				bufferStream.write(inputStream,0,inputStream.length);
				bufferStream.flush();
				bufferStream.close(); 			
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				random.close();
			}
			System.out.println("Done");
		}
		else
		{
			System.out.println("Error: File does not exist");
    		System.out.println("Exiting program");
    		System.exit(0);
		}

	}

}
