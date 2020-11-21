/**
 * Copyright (C) 2002-2017 by Snowbound Software Corp. All rights reserved.
 */

package com.snowbound.samples.common;

public class ErrorCodes {
	public final static int OUT_OF_MEMORY = -1;
    public final static int FILE_NOT_FOUND = -2;
    public final static int CORRUPTED_FILE = -3;
    public final static int BAD_STRING = -4;
    public final static int BAD_RETURN = -5;
    public final static int CANT_CREATE_FILE = -6;
    public final static int FORMAT_NOT_SUPPORTED = -7;
    public final static int NO_BITMAP_FOUND = -8;
    public final static int DISK_FULL = -9;
    public final static int BAD_DISPLAY_AREA = -10;
    public final static int PAGE_NOT_FOUND = -11;
    public final static int DISK_READ_ERROR = -12;
    public final static int BAD_IMAGE_HANDLE = -13;
    public final static int NO_CLIPBOARD_IMAGE = -14;
    public final static int NO_SCANNER_FOUND = -15;
    public final static int ERROR_OPENING_SCANNER = -16;
    public final static int CANT_FIND_TWAIN_DLL = -17;
    public final static int USER_CANCEL = -18;
    public final static int EVAL_TIMEOUT = -19;
    public final static int USING_RUNTIME = -20;
    public final static int PIXEL_DEPTH_UNSUPPORTED = -21;
    public final static int PALETTE_IMAGES_NOT_ALLOWED = -22;
    public final static int NO_LZW_VERSION = -23;
    public final static int DLL_NOT_LOADED = -24;
    public final static int FORMAT_WILL_NOT_OTFLY = -25;
    public final static int NO_TCOLOR_FOUND = -26;
    public final static int COMPRESSION_NOT_SUPPORTED = -27;
    public final static int NO_MORE_PAGES = -28;
    public final static int FEEDER_NOT_READY = -29;
    public final static int NO_DELAY_TIME_FOUND = -30;
    public final static int TIFF_TAG_NOT_FOUND = -31;
    public final static int NOT_A_TILED_IMAGE = -32;
    public final static int NOT_SUPPORTED_IN_THIS_VERSION = -33;
    public final static int AUTOFEED_FAILED = -34;
    public final static int NO_FAST_TWAIN_SUPPORTED = -35;
    public final static int NO_PDF_VERSION = -36;
    public final static int NO_ABIC_VERSION = -37;
    public final static int EXCEPTION_ERROR = -38;
    public final static int NUM_ERROR_CODES = 38;
    private static String[] ERROR_MESSAGES =
    { "Out of memory", "File not found", "Corrupted file", "Bad string",
            "Bad return", "Can't create file", "Format not supported",
            "No bitmap found", "Disk full", "Bad display area",
            "Page not found", "Disk read error", "Bad image handle",
            "No clipboard image", "No scanner found", "Error opening scanner",
            "Can't find twain.dll", "User canceled",
            "Evaluation period expired",
            "Runtime version - Design mode not allowed",
            "Pixel depth not supported", "Not allowed on palettized images",
            "LZW compression not found", "?? WINDOWS ONLY ??",
            "Format can not decompress on-the-fly",
            "No transperancy information found", "Compression not supported",
            "No more pages", "Scanner feed not ready",
            "No delay time for animated GIF", "TIFF tag not found",
            "Not a tiled image", "Format not supported", "Autofeed failed",
            "TWAIN version does not support fast transfer",
            "PDF not in this version", "ABIC not in this version",
            "Internal error" };

    /**
     * @param id
     * @return
     */
    public static String getErrorMessage(int errorCode)
    {
        int arrayIndex = (errorCode * -1) - 1;
        try
        {
            String message = ERROR_MESSAGES[arrayIndex];
            return message;
        }
        catch (Exception e)
        {
            return "Unknown error";
        }
    }
}
