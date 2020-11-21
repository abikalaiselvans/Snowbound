package com.snowbound.samples.common;

/**
 * This class will represent all pages which need to be converted
 * for all documents in the input directory.  This class allows us
 * to know the output name and format for each file ahead of time,
 * and lets us know how many pages will need to processed in total.
 * We use this information to update the progress bar accurately.
 */

public class FileProcessor
{
    public String currentFile = null;
    public String currentFileSaveAs = null;
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