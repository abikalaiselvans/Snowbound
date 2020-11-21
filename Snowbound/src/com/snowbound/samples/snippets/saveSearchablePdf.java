/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.snippets;
// This sample extracts the text from the input document, and saves it as a searchable
// PDF.  The user can also specify a text string to search for by assigning a value
// to the 'stringToSearch' variable.

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import Snow.SNBD_SEARCH_RESULT;


public class saveSearchablePdf {


	public static void main(String[] args) {
		String fileName="C:\\imgs\\simple2page.doc";
		Snow.Snowbnd snow;
		snow = new Snow.Snowbnd();
		int[] length = new int[2];
		int[] error = new int[2];
		int[] errorA = new int[1];
		length[0] = 0;
		error[0] = 0;
		errorA[0] = 0;
		SNBD_SEARCH_RESULT[] mSearchResults = null;
		String stringToSearch ="";

		//annotations that have been added and are stored with the document.";
		System.out.println("fileName"+fileName);
		int totalPages = snow.IMGLOW_get_pages(fileName);
		System.out.println("total Pages ="+totalPages);

		for (int page = 0; page < totalPages; page++) {
			byte extractedText[] = snow.IMGLOW_extract_text(fileName,length,error,page);

			int saveStatus = 0;
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(extractedText));
			saveStatus = snow.IMG_save_document("C:\\imgs\\output_file.pdf", extractedText, 59);

			System.out.println("extractedText..."+length[0]);
			System.out.println("save doc status..."+saveStatus);
			//mSearchResults = snow.IMGLOW_search_text(extractedText,stringToSearch,0,errorA);
			//System.out.println("mSearchResults>>.."+mSearchResults.getClass());
			System.out.println("errorA..."+ Integer.toString(errorA[0]));
		}
	}

}