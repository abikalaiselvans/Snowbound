package com.snowbound.samples.printing;

import java.awt.Dimension;
import java.awt.PrintJob;

import Snow.Snowbnd;

public class PrintedPageGeometry
{
    public int xs, ys;
    public int pd_height, pd_width;
    public int xs_offset, ys_offset;
    public int height, width;
    public int getHeight, getWidth;
    public Dimension pd;
    public int sres;

    public static PrintedPageGeometry getGeometry(PrintJob pjob,
                                                  Snowbnd printSnowImage,
                                                  int resolution)
    {
        PrintedPageGeometry retVal = new PrintedPageGeometry();
        retVal.generateOneDimensionalPrintGeometry(pjob,
                                                   printSnowImage,
                                                   resolution);
        return retVal;
    }

    /**
     * 
     */
    private void generateOneDimensionalPrintGeometry(PrintJob pjob,
                                                     Snowbnd printSnowImage,
                                                     int resolution)
    {
        pd = pjob.getPageDimension();
        sres = 72;
        if (printSnowImage.dis_rotate == 90 || printSnowImage.dis_rotate == 270)
        {
            getWidth = printSnowImage.getHeight();
            getHeight = printSnowImage.getWidth();
        }
        else
        {
            getWidth = printSnowImage.getWidth();
            getHeight = printSnowImage.getHeight();
        }
        if (pd.width != 612)
        {
            xs_offset = 0;
        }
        else
        {
            xs_offset = (int) (pd.width * 0.025);
            pd.width *= 0.95;
        }
        if (pd.height != 792)
        {
            ys_offset = 0;
        }
        else
        {
            ys_offset = (int) (pd.height * 0.025);
            pd.height *= 0.95;
        }
        width = (pd.width * resolution) / sres;
        pd_width = (pd.width * resolution) / sres;
        pd_height = (pd.height * resolution) / sres;
        height = (getHeight * width) / getWidth;
        printSnowImage.alias = 0;
        if (height > pd_height)
        {
            height = pd_height;
            width = (getWidth * height) / getHeight;
            ys = 0;
            xs = (pd_width - width) / 2;
        }
        else
        {
            xs = 0;
            ys = (pd_height - height) / 2;
        }
    }
}
