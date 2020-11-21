/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */
 
package com.snowbound.samples.printing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.JobAttributes;
import java.awt.PrintJob;
import java.awt.Toolkit;

import Snow.Defines;
import Snow.Snowbnd;

import com.snowbound.common.components.print.PrintedPageGeometry;
import com.snowbound.common.utils.Logger;

public class SilentPrintExample
{
    private static void print(String filename,
                              int pageStart,
                              int pageEnd,
                              int resolution)
    {
        Frame printFrame = new Frame();
        PrintJob pjob = null;

        try
        {
            JobAttributes job = new JobAttributes();
            job.setDialog(JobAttributes.DialogType.NONE);
            job.setFromPage(pageStart + 1);
            job.setToPage(pageEnd + 1);
            String jobName = filename;
            pjob = Toolkit.getDefaultToolkit().getPrintJob(printFrame,
                                                           jobName,
                                                           job,
                                                           null);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        printRun(filename, pjob, pageStart, pageEnd, resolution);
    }

    private static void printRun(String filename,
                                 PrintJob pjob,
                                 int startIndex,
                                 int endIndex,
                                 int resolution)
    {
        Snowbnd printSnowImage = new Snowbnd();
        try
        {
            for (int pageIndex = startIndex; pageIndex < (endIndex + 1); pageIndex++)
            {
                printSnowImage.IMGLOW_set_document_input(resolution,
                                                         1,
                                                         Defines.PDF);
                printSnowImage.IMG_decompress_bitmap(filename, pageIndex);
                Dimension pageSize = pjob.getPageDimension();
                if (printSnowImage.getBitsPerPixel() == 1)
                {
                    printSnowImage.alias = 0;
                }
                System.out.println("Printing page " + (pageIndex + 1));
                Graphics pg = pjob.getGraphics();
                PrintedPageGeometry geometry = PrintedPageGeometry
                    .getOneDimensionalGeometry(pjob, printSnowImage, resolution);
                printSnowImage
                    .IMG_print_bitmap(pg,
                                      geometry.xs
                                          + ((geometry.xs_offset * resolution) / 72),
                                      geometry.ys
                                          + ((geometry.ys_offset * resolution) / 72),
                                      geometry.width,
                                      geometry.height,
                                      (resolution * 72) / geometry.sres);
            }
            pjob.end();
        }
        catch (Exception e)
        {
            Logger.getInstance().printStackTrace(e);
        }
        return;
    }

    public static void main(String args[])
    {
        String filename = "c:/Users/user/imgs/document.pdf";
        String jobName = "Sample: document.pdf";
        int startPageIndex = 0;
        int endPageIndex = 1;
        int resolution = 300;
        int bitDepth = 1; // 1 for black and white, 24 for color
        print(filename, startPageIndex, endPageIndex, resolution);
    }
}
