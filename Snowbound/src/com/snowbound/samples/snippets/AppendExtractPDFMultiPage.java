/**
 * Copyright (C) 2007-2018 by Snowbound Software Corp. All rights reserved.
 * This is example code for all SnowBound customers to freely copy and use however they wish.
 * 
 * Authors: Luke Miranda, Jiero Coste
 * 
 * 
 */

package com.snowbound.samples.snippets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import Snow.Snowbnd;
import Snow.Defines;

//Uses the append and extract methods for only PDFs and TIFFs
//Uses IMGLOW_append_page(DataInputStream, extractedPage, Defines.PDF, error) and IMGLOW_extract_page(DataInputStream, extractedPage, Defines.PDF, error)
//Instead of IMGLOW_append_page(FilePath, extractedPage, Defines.PDF) and IMGLOW_extract_page(FilePath, extractedPage, Defines.PDF)
//Can be edited to just use the filepath versions instead of the datainputstream methods

public class AppendExtractPDFMultiPage {

	public static void main(String[] args) {

		String sourceDocument = "C:\\Test\\Input\\VirtualViewerHTML5APIUserGuide.pdf";
		String documentToAppend = "C:\\Test\\Input\\VirtualViewerIntegrationOverview.pdf";

		testPDFAppend(documentToAppend, sourceDocument);
	}

	// method to append PDFs to PDFs
	// Can be modified to append TIFFs to TIFFs
	// First parameter is the document you want to append to the original file (ex. I want to attach document b to the end of document a
	// so document b is the fileToAppend (documentToAppend) and document a is appendedFilename(sourceDocument)
	public static void testPDFAppend(String fileToAppend, String appendedFilename) {
		// used to collect/store error information for extract_page and append_page
		// error codes can be found in manual
		int[] error = new int[1];
		// Snowbound object created
		Snowbnd snowbnd = new Snowbnd();

		// convert input file to DataInputStream using FileInputStream > byte[] >
		// DataInputStream
		File file = new File(fileToAppend);
		FileInputStream fileInputStream = null;
		byte[] filebytes = new byte[(int) file.length()];
		DataInputStream dis = null;
		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(filebytes);
			fileInputStream.close();
			dis = new DataInputStream(new ByteArrayInputStream(filebytes));
		} catch (IOException e) {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e1) {
				}
			}
		}
		byte[] appendedBytes = null;
		// convert input file to DataInputStream using FileInputStream > byte[] >
		// DataInputStream
		file = new File(appendedFilename);
		fileInputStream = null;
		filebytes = new byte[(int) file.length()];
		DataInputStream dis2 = null;
		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(filebytes);
			fileInputStream.close();
			dis2 = new DataInputStream(new ByteArrayInputStream(filebytes));
		} catch (IOException e) {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e1) {
				}
			}
		}
		DataInputStream dos = null;
		byte[] extractedPage = null;
		int extractPageNum = 0;
		int totalPages1 = snowbnd.IMGLOW_get_pages(appendedFilename);
		// Page bytes of source document (original) extracted and put into extractedPage
		// byte[]
		// Cycles through all pages
		do {
			extractedPage = snowbnd.IMGLOW_extract_page(dis2, extractPageNum, error);
			// If bytes stored in extracted page
			// appendedBytes is appended to existing appendedBytes
			// else extractedPage is copied onto appendedBytes
			// Page number increased
			if (null != extractedPage) {
				error[0] = 0;
				if (null != appendedBytes) {
					dos = new DataInputStream(new ByteArrayInputStream(appendedBytes));
					appendedBytes = snowbnd.IMGLOW_append_page(dos, extractedPage, Defines.PDF, error);
				} else {
					appendedBytes = extractedPage;
				}
			}
			extractPageNum++;
		} while ((0 <= error[0]) && (null != extractedPage) && (extractPageNum < totalPages1));

		int totalPages2 = snowbnd.IMGLOW_get_pages(fileToAppend);
		int pageNum = 0;
		// Page bytes of source document (original) extracted and put into extractedPage
		// byte[]
		// Cycles through all pages
		do {
			extractedPage = snowbnd.IMGLOW_extract_page(dis, pageNum, error);
			// If bytes stored in extracted page
			// appendedBytes is appended to existing appendedBytes
			// else extractedPage is copied onto appendedBytes
			// Page number increased
			if (null != extractedPage) {
				error[0] = 0;
				if (null != appendedBytes) {
					dos = new DataInputStream(new ByteArrayInputStream(appendedBytes));
					appendedBytes = snowbnd.IMGLOW_append_page(dos, extractedPage, Defines.PDF, error);
				} else {
					appendedBytes = extractedPage;
				}
			}
			pageNum++;
		} while ((0 <= error[0]) && (null != extractedPage) && (pageNum < totalPages2));

		// write appendedBytes to output file
		java.io.OutputStream os = null;
		try {
			String outFilename = appendedFilename + "-output.pdf";
			os = new java.io.BufferedOutputStream(new FileOutputStream(outFilename));
			os.write(appendedBytes);
			os.close();
		} catch (IOException e) {
		}

		System.out.println("Done");
	}

}