/**
 * Copyright (C) 2012-2017 by Snowbound Software Corp. All rights reserved.
 * @author Bismark Frimpong
 * 
 */
package com.snowbound.samples.tiffTags;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Button;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

import javax.swing.UIManager;

import Snow.ErrorCodes;
import Snow.Format;
import Snow.FormatHash;
import Snow.Snowbnd;
import javax.swing.JScrollPane;
import java.awt.Font;

public class DumTiffTagsApp {

	
	// Description of Tags Info
    public static final int NEW_SUBFILE_TYPE = 254;
    public static final int IMAGE_WIDTH = 256;
    public static final int IMAGE_LENGTH = 257;
    public static final int BITS_PER_SAMPLE = 258;
    public static final int COMPRESSION = 259;
    public static final int PHOTO_INTERPRETATION = 262;
    public static final int THRESH_HOLDING = 263;
    public static final int CELL_WIDTH = 264;
    public static final int CELL_LENGTH = 265;
    public static final int FILL_ORDER = 266;
    public static final int DOCUMENT_NAME = 269;
    public static final int IMAGE_DESCRIPTION = 270;
    public static final int MAKE = 271;
    public static final int MODEL = 272;  
    public static final int STRIP_OFFSETS = 273;
    public static final int ORIENTATION = 274;
    public static final int SAMPLES_PER_PIXEL = 277;
    public static final int ROWS_PER_STRIP = 278;
    public static final int STRIP_BYTE_COUNT = 279;
    public static final int MIN_SAMPLE_VALUE = 280;
    public static final int MAX_SAMPLE_VALUE = 281;
    public static final int X_RESOLUTION = 282;
    public static final int Y_RESOLUTION = 283;
    public static final int PLANAR_CONFIGURATION = 284;
    public static final int PAGE_NAME = 285;
    public static final int X_POSITION = 286;
    public static final int Y_POSITION = 287;
    public static final int FREE_OFFSETS = 288;
    public static final int FREE_BYTE_COUNTS = 289;
    public static final int GRAY_RESPONSE_UNIT = 290;
    public static final int GRAY_RESPONSE_CURVE = 291;
    public static final int GROUP3_OPTIONS = 292;
    public static final int GROUP4_OPTIONS = 293;
    public static final int RESOLUTION_UNIT = 296;
    public static final int PAGE_NUMBER = 297;
    public static final int TRANSFER_FUNCTION = 301;
    public static final int SOFTWARE = 305;
    public static final int DATE_TIME = 306;
    public static final int ARTEST = 315;
    public static final int HOST_COMPUTER = 316;
    public static final int PREDICTOR = 317;
    public static final int COLOR_IMAGE_TYPE = 318;
    public static final int COLORLIST = 319;
    public static final int COLOR_MAP = 320;
    public static final int HALFONE_HINT = 321;
    public static final int TILE_WIDTH = 322;
    public static final int TILE_LENGTH = 323;
    public static final int TILE_OFFSETS = 324;
    public static final int TILE_BYTE_COUNTS = 325;
    public static final int BAD_FAX_LINES = 326;
    public static final int CLEAN_FAX_DATA = 327;
    public static final int CONSECUTIVE_BAD_FAX_LINES = 328;
    public static final int SUB_IFDS = 330;
    public static final int INK_SET = 332;
    public static final int INK_NAMES = 333;
    public static final int NUMBER_OF_INKS = 334;
    public static final int DOT_RANGE = 336;
    public static final int TARGET_PRINTER = 337;
    public static final int EXTRA_SAMPLES = 338;
    public static final int SAMPLE_FORMAT = 339;
    public static final int SMIN_SAMPLE_VALUE = 340;
    public static final int SMAX_SAMPLE_VALUE = 341;
    public static final int TRANSFER_RANGE = 342;
    public static final int CLIP_PATH = 343;
    public static final int XCLIP_PATH_UNITS = 344;
    public static final int YCLIP_PATH_UNITS = 345;
    public static final int INDEXED = 346;
    public static final int JPEG_TABLES = 347;
    public static final int OPIPROXY = 351;
    public static final int GLOBAL_PARAMETERS_IFD = 400;
    public static final int PROFILE_TYPE = 401;
    public static final int FAX_PROFILE = 402;
    public static final int CODING_METHODS = 403;
    public static final int VERSION_YEAR = 404;
    public static final int MODE_NUMBER = 405;
    public static final int DECODE = 433;
    public static final int DEFAULT_IMAGE_COLOR = 434;
    public static final int JPEG_PROC = 512;
    public static final int JPEG_INTERCHANGE_FORMAT = 513;
    public static final int JPEG_INTERGANGE_FORMAT_LENGTH = 514;
    public static final int JPEG_RESTART_INTERVAL = 515;
    public static final int JPEG_LOSSLESS_PREDICTORS = 517;
    public static final int JPEG_POINT_TRANSFORMS = 518;
    public static final int JPEGQ_TABLES = 519;
    public static final int JPEG_DC_TABLES = 520;
    public static final int JPEG_AC_TABLES = 521;
    public static final int YCBCR_COEFFICIENTS = 529;
    public static final int YCBCR_SUB_SAMPLING = 530;
    public static final int YCBCR_POSITIONING = 531;
    public static final int REFERENCE_BLACK_WHITE = 532;
    public static final int STRIP_ROW_COUNTS = 559;
    public static final int XMP = 700;
    public static final int IMAGE_ID = 32781;
    public static final int WANG_ANNOTATION = 32932;
    public static final int CFA_REPEAT_PATTERN_DIM = 33421;
    public static final int CFA_PATTERN = 33422;
    public static final int BATTERY_LEVEL = 33423;
    public static final int COPYRIGHT = 33232;
    public static final int EXPOSURE_TIME = 33434;
    public static final int FNUMBER = 33437;
    public static final int MD_FILE_TAG = 33445;
    public static final int MD_SCALE_PIXEL = 33446;
    public static final int MD_COLOR_TABLE = 33447;
    public static final int MD_LABNAME = 33448;
    public static final int MD_SAMPLE_INFO = 33449;
    public static final int MD_PREP_DATE = 33450;
    public static final int MD_PREP_TIME = 33451;
    public static final int MD_FILE_UNITS = 33452;
    public static final int MODEL_PIXEL_SCALE_TAG = 33550;
    public static final int IPTC_NAA = 33723;
    public static final int INGR_PACKET_DATA_TAG = 33918;
    public static final int ING_FLAG_REGISTERS = 33919;
    public static final int IRASB_TRANSFORMATION_MATRIX = 33920;
    public static final int MODEL_TIEPOINT_TAG = 33922;
    public static final int SITE = 34016;
    public static final int COLOR_SEQUENECE = 34017;
    public static final int IT8_HEADER = 34018;
    public static final int RASTER_PADDING = 34019;
    public static final int BITS_PER_RUN_LENGTH = 34020;
    public static final int BITS_PER_EXTENDED_RUN_LENGTH = 34021;
    public static final int COLOR_TABLE = 34022;
    public static final int IMAGE_COLOR_INDICATOR = 34023;
    public static final int BACKGROUND_COLOR_INDICATOR = 34024;
    public static final int IMAGE_COLOR_VALUE = 34025;
    public static final int BACKGROUND_COLOR_VALUE = 34026;
    public static final int PIXEL_INTENSITY_RANGE = 34027;
    public static final int TRANSPARENCY_INDICATOR = 34028;
    public static final int COLOR_CHARACTERIZATION = 34029;
    public static final int HCUSAGE = 34030;
    public static final int TRAP_INDICATOR = 34031;
    public static final int CMYK_EQUICALENT = 34032;
    public static final int RESERVED = 34033;
    public static final int MODEL_TRANSFORMATION_TAG = 34264;
    public static final int PHOTOSHOP = 34377;
    public static final int EXIT_IFD = 34665;
    public static final int INTER_COLOR_PROFILE = 34675;
    public static final int IMAGE_LAYER = 34732;
    public static final int GEOKEY_DIRECTORY_TAG = 34735;
    public static final int GEODOUBLE_PARAMS_TAG = 34736;
    public static final int GEOASCII_PARAMS_TAG = 34737;
    public static final int EXPOSURE_PROGRAM = 34850;
    public static final int SPECTRAL_SENSITIVITY = 34852;
    public static final int GPS_INFO = 34853;
    public static final int ISO_SPEED_RATINGS = 34855;
    public static final int OECF = 34856;
    public static final int INTERLACE = 34857;
    public static final int TIME_ZONE_OFFSET = 34858;
    public static final int SELF_TIME_MODE = 34859;
    public static final int HYLAFAX_FAX_RECV_PARAMS = 34908;
    public static final int HYLAFAX_FAX_SUB_ADDRESS = 34909;
    public static final int HYLAFAX_FAX_RECV_TIME = 34910;
    public static final int EXIF_VERSION = 36864;
    public static final int DATE_TIME_ORIGINAL = 36867;
    public static final int DATE_TIME_DIGITIZED = 36868;
    public static final int COMPONENTS_CONFIGURATION = 37121;
    public static final int COMPRESSED_BITS_PER_PIXEL = 37122;
    public static final int SHUTTER_SPEED_VALUE = 37377;
    public static final int APERTURE_VALUE = 37378;
    public static final int BRIGHTNESS_VALUE = 37379;
    public static final int EXPOSURE_BIAS_VALUE = 37380;
    public static final int MAX_APARTURE_VALUE = 37381;
    public static final int SUBJECT_DISTANCE = 37382;
    public static final int METERING_MODE = 37383;
    public static final int LIGHT_SOURCE = 37384;
    public static final int FLASH = 37385;
    public static final int FOCAL_LENGTH = 37386;
    public static final int FLASH_ENERGY = 37387;
    public static final int SPATIAL_FREQUENCY_RESPONSE = 37388;
    public static final int NOISE = 37389;
    public static final int FOCAL_PLANE_X_RESOLUTION = 37390;
    public static final int FOCAL_PLANE_Y_RESOLUTION = 37391;
    public static final int FOCAL_PLANE_RESOLUTION_UNIT = 37392;
    public static final int IMAGE_NUMBER = 37393;
    public static final int SECURITY_CLASSIFICATION = 37394;
    public static final int IMAGE_HISTORY = 37395;
    public static final int SUBJECT_LOCATION = 37396;
    public static final int EXPOSURE_INDEX = 37397;
    public static final int TIFF_EP_STANDARD_ID = 37398;
    public static final int SENSING_METHOD = 37399;
    public static final int MAKER_NOTE = 37500;
    public static final int USER_COMMENT = 37510;
    public static final int SUBSEC_TIME = 37520;
    public static final int SUBSEC_TIME_ORIGINAL = 37521;
    public static final int SUBSEC_TIME_DIGITIZED = 37522;
    public static final int IMAGE_SOURCE_DATA = 37724;
    public static final int FLASH_PIX_VERSION = 40960;
    public static final int COLOR_SPACE = 40961;
    public static final int PIXEL_X_DIMENSION = 40962;
    public static final int PIXEL_Y_DIMENSION = 40963;
    public static final int RELATED_SOUND_FILE = 40964;
    public static final int INTEROPERABLILITY_IFD = 40965;
    public static final int SCENE_TYPE = 41729;
    public static final int CUSTOM_RENDERRED = 41985;
    public static final int EXPOSURE_MODE = 41986;
    public static final int WHITE_BALANCE = 41987;
    public static final int DIGITAL_ZOOM_RATION = 41988;
    public static final int FOCAL_LENGTH_IN_35MM_FILM = 41989;
    public static final int SCENE_CAPTURE_TYPE = 41990;
    public static final int GAIN_CONTROL = 41991;
    public static final int CONSTRAST = 41992;
    public static final int SATURATION = 41993;
    public static final int SHARPNESS = 41994;
    public static final int DEVICE_SETTINGS_DESCRIPTION = 41995;
    public static final int SUBJECT_DISTANCE_RANGE = 41996;
    public static final int IMAGE_UNIQUE_ID = 42016;
    public static final int GDAL_METADATA = 42112;
    public static final int GDAL_NODATA = 42113;
    public static final int PIXEL_FORMAT = 48129;
    public static final int TRANSFORMATION = 48130;
    public static final int UNCOMPRESSED = 48131;
    public static final int IMAGE_TYPE = 48132;
    public static final int IMAGE_HEIGHT = 48257;
    public static final int WIDTH_RESOULTION = 48258;
    public static final int HEIGHT_RESOLUTION = 48259;
    public static final int IMAGE_OFFSET = 48320;
    public static final int IMAGE_BYTE_COUNT = 48321;
    public static final int ALPHA_OFFSET = 48322;
    public static final int ALPHA_BTYE_COUNT = 48323;
    public static final int IMAGE_DATA_DISCARD = 48324;
    public static final int ALPHA_DATA_DISCARD = 48325;
    public static final int OCE_SCANJOB_DESCRIPTION = 50215;
    public static final int OCE_IDENTICICATION_NUMBER = 50217;
    public static final int OCE_IMAGE_LOGIC_CHARACTERISTICS = 50218;
    public static final int DNG_VERSION = 50706;
    public static final int DNG_BACKWARD_VERSION = 50707;
    public static final int UNIQUE_CAMERA_MODEL = 50708;
    public static final int LOCALIZED_CAMERA_MODEL = 50709;
    public static final int CFA_PLANE_COLOR = 50710;
    public static final int CFA_LAYOUT = 50711;
    public static final int LINEARIZATION_TABLE = 50712;
    public static final int BLACK_LEVEL_REPEAT_DIM = 50713;
    public static final int BLACK_LEVEL = 50714;
    public static final int BLACK_LEVEL_DELTAH = 50715;
    public static final int BLACK_LEVEL_DELTAV = 50716;
    public static final int WHITE_LEVEL = 50717;
    public static final int DEFAULT_SCALE = 50718;
    public static final int DEFAULT_CROP_ORIGIN = 50719;
    public static final int DEFAULT_CROP_SIZE = 50720;
    public static final int COLOR_MATRIX1 = 50721;
    public static final int COLOR_MATRIX2 = 50722;
    public static final int CAMERA_CALIBRATION1 = 50723;
    public static final int CAMERA_CALIBRATION2 = 50724;
    public static final int REDUCTION_MATRIX1 = 50725;
    public static final int REDUCTION_MATRIX2 = 50726;
    public static final int ANALOG_BALANCE = 50727;
    public static final int AS_SHOT_NEUTRAL = 50728;
    public static final int AS_SHOT_WHITE_XY = 50729;
    public static final int BASELINE_EXPOSURE = 50730;
    public static final int BASELINE_NOISE = 50731;
    public static final int BASELINE_SHARPNESS = 50732;
    public static final int BAYER_GREEN_SPLIT = 50733;
    public static final int LINEAR_RESPONSE_LIMIT = 50734;
    public static final int CAMERA_SERIAL_NUMBER = 50735;
    public static final int LENSE_INFO = 50736;
    public static final int CHROMA_BLUR_RADIUS = 50737;
    public static final int ANTI_ALIAS_STRENGTH = 50738;
    public static final int SHADOW_SCALE = 50739;
    public static final int DNG_PRIVATE_DATA = 50740;
    public static final int MAKER_NOTE_SAFETY = 50741;
    public static final int CALIBRATION_IIUMINANT1 = 50778;
    public static final int CALIBRATION_IIUMINANT2 = 50779;
    public static final int BEST_QUALITY_SCALE = 50780;
    public static final int RAW_DATA_UNIQUE_ID = 50781;
    public static final int ALIAS_LAYER_METADATA = 50784;
    public static final int ORIGINAL_RAW_FILE_NAME = 50827;
    public static final int ORIGINAL_RAW_FILE_DATA = 50828;
    public static final int ACTIVE_AREA = 50829;
    public static final int MASKED_AREAS = 50830;
    public static final int AS_SHOT_ICC_PROFILE = 50831;
    public static final int AS_SHOT_PRE_PROFILE_MATRIX = 50832;
    public static final int CURRENT_ICC_PROFILE = 50833;
    public static final int CURRENT_PRE_PROFILE_MATRIX = 50834;
    
    
    private static String[] TAG_DESCRIPTION = { "NewSubfileType", "ImageWidth", "ImageLength", "BitsPerSmaple", 
    	"Compression", "PhotoInterp", "ImageDescription", "StripOffsets", 
    	"Orientation", "SamplesPerPixel", "RowsPerStrip", "StripByteCount", 
    	"XResolution", "PlanarConfiguation", "ResoultionUnit", "PageNumber", 
    	"TransferFucntion", "Software", "DateTime" , "Artest","HostComputer" , 
    	"Predictor", "COlorImageType", "ColorList", " ColorMap", "HalphoneHint" , 
    	"TileWidth" ,"TileLength", "TileOffsets" ,"TileByteCounts", "BadFaxLines", 
    	"CleanFaxData", "ConsecutiveBadFaxLines" , "SubIFDS", "InkSet", "InkNames", 
    	"DotRange", "" };
    
    
    /*------------------------------------------*/
    //private static TagDescription tagDescprition;
    private static Snowbnd simage;
    private static int iTagNum;
    private static int tagImages;
    private static String dataVal;
    private static int stat = -1;
    private static int counter = 0;
    static int[] iTagValue = new int[1];
    static int tagid, count, value;
    private static String tagNamesBuff;
    private static String strTiffTag;
    private static TiffImageTags tiffTags;
    private static PrintStream log;
    //Initial size of buffer used by IMG_save_bitmap()
    private static int initialBufferSize = 50000;
    //Amount the buffer used by IMG_save_bitmap() will grow by if needed
    private static int increaseBufferSize = 20000;
    private static boolean accessFile;
    private static String tagNames ;
    //private static ArrayList<String> tagNames = new ArrayList<String>();
    private static String strItem;
    private static String directory;
    private static PrintStream console;
    private static final int TOTAL_TAGS = 512;
    private static int[] storeTags = new int[TOTAL_TAGS];
    public int currentPage = -1;
	private static int width;
	private static int height;
    private static String fileName;
    static String strTempString = "";
    
    public static String getTagDescription(int info)
    { 	
    	switch(info)
    	{
    	case NEW_SUBFILE_TYPE: tagNames = "NewSubfileType"; break;
    	case IMAGE_WIDTH: tagNames = "ImageWidth"; break;
    	case IMAGE_LENGTH: tagNames = "ImageLength"; break;
    	case BITS_PER_SAMPLE: tagNames = "BitsPerSample"; break;
    	case COMPRESSION: tagNames = "Compression"; break; 
    	case PHOTO_INTERPRETATION: tagNames = "PhotoInterp"; break;
    	case THRESH_HOLDING: tagNames = "ThreshHolding"; break;
    	case CELL_WIDTH: tagNames = "CellWidth"; break;
    	case CELL_LENGTH: tagNames = "CellLength"; break;
    	case FILL_ORDER: tagNames = "FillOrder"; break;
    	case DOCUMENT_NAME: tagNames = "DocumentName"; break;
    	case IMAGE_DESCRIPTION: tagNames = "ImageDescription"; break;
    	case MAKE: tagNames = "Make"; break;
    	case MODEL: tagNames = "Model"; break; 
    	case STRIP_OFFSETS: tagNames = "StripOffsets"; break;
    	case ORIENTATION: tagNames = "Orientation"; break;
    	case SAMPLES_PER_PIXEL: tagNames = "SamplesPerPixel"; break;
    	case ROWS_PER_STRIP: tagNames = "RowsPerStrip"; break;
    	case STRIP_BYTE_COUNT: tagNames = "StripByteCount"; break;
    	case MIN_SAMPLE_VALUE: tagNames = "MinSmapleValue"; break;
    	case MAX_SAMPLE_VALUE: tagNames = "MaxSampleValue"; break;   
    	case X_RESOLUTION: tagNames = "XResolution"; break;
    	case Y_RESOLUTION: tagNames = "YResolution"; break;
    	case PLANAR_CONFIGURATION: tagNames= "PlanarConfiguration"; break;	
    	case PAGE_NAME: tagNames = "PageName"; break;
    	case X_POSITION: tagNames = "XPosition"; break;
    	case Y_POSITION: tagNames = "YPosition"; break;
    	case FREE_OFFSETS: tagNames = "FreeOffsets"; break;
    	case FREE_BYTE_COUNTS: tagNames = "FreeByteCounts"; break;
    	case GRAY_RESPONSE_UNIT: tagNames = "GrayResponseUnit"; break;
    	case GRAY_RESPONSE_CURVE: tagNames = "GrayResponseCurve"; break;
    	case GROUP3_OPTIONS: tagNames = "Group3Options"; break;
    	case GROUP4_OPTIONS: tagNames = "Group4Options"; break;
    	case RESOLUTION_UNIT: tagNames = "ResolutionUnit"; break;
    	case PAGE_NUMBER: tagNames = "PageNumber"; break;
    	case TRANSFER_FUNCTION: tagNames = "TransferFunction"; break;
    	case SOFTWARE: tagNames = "Software"; break;
    	case DATE_TIME: tagNames = "DateTime"; break;
    	case ARTEST: tagNames = "Artest"; break;
    	case HOST_COMPUTER: tagNames = "HostComputer"; break;
    	case PREDICTOR: tagNames = "Predictor"; break; 
    	case COLOR_IMAGE_TYPE: tagNames = "ColorImageType"; break;
    	case COLORLIST: tagNames = "ColorList"; break;
    	case COLOR_MAP: tagNames = "ColorMap"; break; 
    	case HALFONE_HINT: tagNames = "HalFoneHint"; break;
    	case TILE_WIDTH: tagNames = "TileWidth"; break;
    	case TILE_LENGTH: tagNames = "TileHight"; break;
    	case TILE_OFFSETS: tagNames = "TileOffset"; break;
    	case TILE_BYTE_COUNTS: tagNames = "TileByteCounts"; break;
    	case BAD_FAX_LINES: tagNames = "BadFaxLines"; break;
    	case CLEAN_FAX_DATA: tagNames = "CleanFaxData";break;
    	case CONSECUTIVE_BAD_FAX_LINES: tagNames = "ConsecutiveBadFaxLines"; break;
    	case SUB_IFDS: tagNames = "SubIFDS"; break;
    	case INK_SET: tagNames = "InkSet"; break;
    	case NUMBER_OF_INKS: tagNames = "NumberOfInks";break;
    	case DOT_RANGE: tagNames = "DotRange"; break;
    	case TARGET_PRINTER: tagNames = "TargetPrinter";break;
    	case EXTRA_SAMPLES: tagNames = "ExtraSamples";break;
    	case SAMPLE_FORMAT: tagNames = "SampleFormat"; break; 
    	case SMIN_SAMPLE_VALUE: tagNames = "SMinSampleValue"; break;
    	case SMAX_SAMPLE_VALUE: tagNames = "SMaxSampleValue"; break;
    	case TRANSFER_RANGE: tagNames = "TransferRange"; break;
    	case CLIP_PATH: tagNames = "ClipPath"; break;
    	case XCLIP_PATH_UNITS: tagNames = " XClipPathUnits"; break;
    	case YCLIP_PATH_UNITS: tagNames = "YClipPathUnits";break;
    	case INDEXED: tagNames = "Indexed";break;
    	case JPEG_TABLES: tagNames = "JPEGTables"; break; 
    	case OPIPROXY: tagNames = "Opiproxy";break;
    	case GLOBAL_PARAMETERS_IFD: tagNames = "GlobalParametersIFD";break;
    	case PROFILE_TYPE: tagNames = " ProfileType";break;
    	case FAX_PROFILE: tagNames = "FaxProfile";break;
    	case CODING_METHODS:tagNames = "CodingMethods";break;
    	case VERSION_YEAR: tagNames = "VersionYear";break;
    	case MODE_NUMBER: tagNames = "ModeNumber";break;
    	case DECODE: tagNames = "Decode";break;
    	case DEFAULT_IMAGE_COLOR: tagNames = "DefaultImageColor";break;
    	case JPEG_PROC: tagNames = "JpegProc";break;
    	case JPEG_INTERCHANGE_FORMAT: tagNames = "JPegInterchangeFormat";break;
    	case JPEG_INTERGANGE_FORMAT_LENGTH: tagNames = "JPegInterchangeFormatLength";break;
    	case JPEG_RESTART_INTERVAL: tagNames = "JPegRestatInterval";break;
    	case JPEG_LOSSLESS_PREDICTORS: tagNames = "JPegLosslessPredictors";break;
    	case JPEG_POINT_TRANSFORMS: tagNames = "JPegPointTransforms";break;
    	case JPEGQ_TABLES: tagNames = "JPegQTables";break;
    	case JPEG_DC_TABLES: tagNames = "JPegDCTables";break;
    	case JPEG_AC_TABLES: tagNames = "JPegACTables";break;
    	case YCBCR_COEFFICIENTS: tagNames = "YCBCRCoefficients";break;
    	case YCBCR_SUB_SAMPLING: tagNames = "YCBCRSubSampling";break;
    	case YCBCR_POSITIONING: tagNames = "YCBCRPositioning";break;
    	case REFERENCE_BLACK_WHITE: tagNames = "ReferenceBlackWhite";break;
    	case STRIP_ROW_COUNTS: tagNames = "StripRowCounts";break;
    	case XMP: tagNames = "XMP";break;
    	case IMAGE_ID: tagNames = "ImageID";break;
    	case WANG_ANNOTATION: tagNames = "WangAnnotation";break;
    	case CFA_REPEAT_PATTERN_DIM: tagNames = "CFARepeatPatternDIM";break;
    	case CFA_PATTERN: tagNames = "CFAPattern";break;
    	case BATTERY_LEVEL: tagNames = "BatteryLevel";break;
    	case COPYRIGHT: tagNames = "Copyright";break;
    	case EXPOSURE_TIME: tagNames = "ExposuerTime";break;
    	case FNUMBER: tagNames = "FNumber";break;
    	case MD_FILE_TAG: tagNames = "MDFileTag";break;
    	case MD_SCALE_PIXEL: tagNames = "MDScalePixel";break;
    	case MD_COLOR_TABLE: tagNames = "MDColorTable";break;
    	case MD_LABNAME: tagNames = "MDLabName";break;
    	case MD_SAMPLE_INFO: tagNames = " MDSampleInfo";break; 
    	case MD_PREP_DATE : tagNames = "MDPrepDate ";  break;
    	case MD_PREP_TIME : tagNames = "MDPrepTime ";  break;
    	case MD_FILE_UNITS : tagNames = "MDFileUnits ";   break;
    	case MODEL_PIXEL_SCALE_TAG : tagNames = "ModelPixelScalseTag ";  break;
    	case IPTC_NAA : tagNames = "IPTCNaa ";  break;
    	case INGR_PACKET_DATA_TAG : tagNames = "INGRPacketDataTag ";  break;
    	case ING_FLAG_REGISTERS : tagNames = "INGFlagRegisters ";  break;
    	case IRASB_TRANSFORMATION_MATRIX : tagNames = "IRASBTrasformationMatrix ";  break;
    	case MODEL_TIEPOINT_TAG : tagNames = "ModelTiePointTag ";  break;
    	case SITE : tagNames = "Site ";  break;
    	case COLOR_SEQUENECE : tagNames = "ColorSequence ";  break;
    	case IT8_HEADER : tagNames = "IT8Header ";  break;
    	case RASTER_PADDING : tagNames = "RasterPadding ";  break;
    	case BITS_PER_RUN_LENGTH : tagNames = "BitsPerRunLength ";  break;
    	case BITS_PER_EXTENDED_RUN_LENGTH : tagNames = "BitsPerExtendedRunLength ";  break;
    	case COLOR_TABLE : tagNames = "ColorTable ";  break;
    	case IMAGE_COLOR_INDICATOR : tagNames = "ImageColorIndicator ";  break;
    	case BACKGROUND_COLOR_INDICATOR : tagNames = "BackgroundColorInidcator ";  break;
    	case IMAGE_COLOR_VALUE : tagNames = "ImageColorValue";  break;
    	case BACKGROUND_COLOR_VALUE : tagNames = "BackgroundColorValue ";  break;
    	case PIXEL_INTENSITY_RANGE : tagNames = "PixelIntensityRange ";  break;
    	case TRANSPARENCY_INDICATOR : tagNames = "TransparencyIndicator ";  break;
    	case COLOR_CHARACTERIZATION : tagNames = "ColorCharacterization ";  break;
    	case HCUSAGE: tagNames = "Hcusage "; break;
    	case TRAP_INDICATOR: tagNames = "TripIndicator"; break;
    	case CMYK_EQUICALENT : tagNames = "CMYKEEquicalent";  break;
    	case RESERVED : tagNames = "Reserved ";  break;
    	case MODEL_TRANSFORMATION_TAG : tagNames = "ModelTransformationTag ";  break;
    	case PHOTOSHOP : tagNames = "Photoshop ";  break;
    	case EXIT_IFD : tagNames = "ExitIFD";  break;
    	case INTER_COLOR_PROFILE : tagNames = "InterColorProfile";  break;
    	case IMAGE_LAYER : tagNames = "ImageLayer ";  break;
    	case GEOKEY_DIRECTORY_TAG : tagNames = "GeokeyDirectoryTag ";  break;
    	case GEODOUBLE_PARAMS_TAG : tagNames = "GeoDoubleParamsTag ";  break;
    	case GEOASCII_PARAMS_TAG : tagNames = "GeoAsciiParamsTag";  break;
    	case EXPOSURE_PROGRAM : tagNames = "ExposureProgram";  break;
    	case SPECTRAL_SENSITIVITY : tagNames = "SpectralSensitivity";  break;
    	case GPS_INFO : tagNames = "GPSInfo ";  break;
    	case ISO_SPEED_RATINGS : tagNames = "ISOSpeedRatings";  break;
    	case OECF : tagNames = "Oecf ";  break;
    	case INTERLACE : tagNames = "Interlace ";  break;
    	case TIME_ZONE_OFFSET : tagNames = "TimeZoneOffset ";  break;
    	case SELF_TIME_MODE : tagNames = "SelfTimeMode ";  break;
    	case HYLAFAX_FAX_RECV_PARAMS : tagNames = "HylafaxFaxRecvParams ";  break;
    	case HYLAFAX_FAX_SUB_ADDRESS : tagNames = "HylafaxFaxSubAddress ";  break;
    	case HYLAFAX_FAX_RECV_TIME : tagNames = "HylafaxFaxRecvTime ";  break;
    	case EXIF_VERSION : tagNames = "ExifVersion";  break;
    	case DATE_TIME_ORIGINAL : tagNames = "DateTimeOriginal ";  break;
    	case DATE_TIME_DIGITIZED : tagNames = "DateTimeDigitized ";  break;
    	case COMPONENTS_CONFIGURATION : tagNames = "ComponentsConfiguration ";  break;
    	case COMPRESSED_BITS_PER_PIXEL : tagNames = "CompressedBitsPerPixel ";  break;
    	case SHUTTER_SPEED_VALUE : tagNames = "ShutterSpeedValue ";  break;
    	case APERTURE_VALUE : tagNames = "ApertureValue ";  break;
    	case BRIGHTNESS_VALUE : tagNames = "BrightnessValue ";  break;
    	case EXPOSURE_BIAS_VALUE : tagNames = "ExposureBiasValue ";  break;
    	case MAX_APARTURE_VALUE : tagNames = "MaxApartureValue ";  break;
    	case SUBJECT_DISTANCE : tagNames = "SubjectDistance ";  break;
    	case METERING_MODE : tagNames = "MeteringMode ";  break;
    	case LIGHT_SOURCE : tagNames = "LightSource ";  break;
    	case FLASH : tagNames = "Flash ";  break;
    	case FOCAL_LENGTH : tagNames = "FocalLength ";  break;
    	case FLASH_ENERGY : tagNames = "FlashEnergy ";  break;
    	case SPATIAL_FREQUENCY_RESPONSE : tagNames = "SpatialFrequencyResponse ";  break;
    	case NOISE : tagNames = "Noise ";  break;
    	case FOCAL_PLANE_X_RESOLUTION : tagNames = "FocalPlaneXResoultion";  break;
    	case FOCAL_PLANE_Y_RESOLUTION : tagNames = "FocalPlaneYResoultion ";  break;
    	case FOCAL_PLANE_RESOLUTION_UNIT : tagNames = "FocalPlaneResoultionUnit ";  break;
    	case IMAGE_NUMBER : tagNames = "ImageNumber ";  break;
    	case SECURITY_CLASSIFICATION : tagNames = "SecurityClassification ";  break;
    	case IMAGE_HISTORY : tagNames = "ImageHistory ";  break;
    	case SUBJECT_LOCATION : tagNames = "SubjectLocatiion ";  break;
    	case EXPOSURE_INDEX : tagNames = "ExposureIndex ";  break;
    	case TIFF_EP_STANDARD_ID : tagNames = "TiffEPStandardID ";  break;
    	case SENSING_METHOD : tagNames = "SensingMethod ";  break;
    	case MAKER_NOTE : tagNames = "MakerNote ";  break;
    	case USER_COMMENT : tagNames = "UserComment ";  break;
    	case SUBSEC_TIME : tagNames = "SubjectTime ";  break;
    	case SUBSEC_TIME_ORIGINAL : tagNames = "SUBSECTimeOriginal ";  break;
    	case SUBSEC_TIME_DIGITIZED : tagNames = "SubSecTimeDigitized ";  break;
    	case IMAGE_SOURCE_DATA : tagNames = "ImageSourceData ";  break;
    	case FLASH_PIX_VERSION : tagNames = "FlashPixVersion ";  break;
    	case COLOR_SPACE : tagNames = "ColorSpace ";  break;
    	case PIXEL_X_DIMENSION : tagNames = "PixelXDimention ";  break;
    	case PIXEL_Y_DIMENSION : tagNames = "PixelYDimension ";  break;
    	case RELATED_SOUND_FILE : tagNames = "RelatedSoundFile ";  break;
    	case INTEROPERABLILITY_IFD : tagNames = "InteroperabilityIFD ";  break;
    	case SCENE_TYPE : tagNames = "SceneType ";  break;
    	case CUSTOM_RENDERRED : tagNames = "CustomRenderred ";  break;
    	case EXPOSURE_MODE : tagNames = "ExpousureMode ";  break;
    	case WHITE_BALANCE : tagNames = "WhiteBalance ";  break;
    	case DIGITAL_ZOOM_RATION : tagNames = "DigitalZoomRation ";  break;
    	case FOCAL_LENGTH_IN_35MM_FILM : tagNames = "FocalLengthIn35MMFilm ";  break;
    	case SCENE_CAPTURE_TYPE : tagNames = "SceneCaptureType ";  break;
    	case GAIN_CONTROL : tagNames = "GainControl ";  break;
    	case CONSTRAST : tagNames = "Contrast ";  break;
    	case SATURATION : tagNames = "Saturation ";  break;
    	case SHARPNESS : tagNames = "Sharpness ";  break;
    	case DEVICE_SETTINGS_DESCRIPTION : tagNames = "DeviceSettingsDescription ";  break;
    	case SUBJECT_DISTANCE_RANGE : tagNames = "SubjectDistanceRange ";  break;
    	case IMAGE_UNIQUE_ID : tagNames = "ImageUniqueID ";  break;
    	case GDAL_METADATA : tagNames = "GDALMetadata ";  break;
    	case GDAL_NODATA : tagNames = "GDALNodata ";  break;
    	case PIXEL_FORMAT : tagNames = "PixelFormat ";  break;
    	case TRANSFORMATION : tagNames = "Transformation ";  break;
    	case UNCOMPRESSED : tagNames = "Uncompressed ";  break;
    	case IMAGE_TYPE : tagNames = "ImagType ";  break;
    	case IMAGE_HEIGHT : tagNames = "ImageHeight ";  break;
    	case WIDTH_RESOULTION : tagNames = "WidthResoultion ";  break;
    	case HEIGHT_RESOLUTION : tagNames = "HeightResoultion ";  break;
    	case IMAGE_OFFSET : tagNames = "ImageOffset ";  break;
    	case IMAGE_BYTE_COUNT : tagNames = "ImageByteCount ";  break;
    	case ALPHA_OFFSET : tagNames = "AlphaOffset ";  break;
    	case ALPHA_BTYE_COUNT : tagNames = "AlphaByteCount ";  break;
    	case IMAGE_DATA_DISCARD : tagNames = "ImageDataDiscard ";  break;
    	case ALPHA_DATA_DISCARD : tagNames = "AlphaDataDiscard ";  break;
    	case OCE_SCANJOB_DESCRIPTION : tagNames = "OCEScanJobDescription ";  break;
    	case OCE_IDENTICICATION_NUMBER : tagNames = "OCEIdenticationNumber";  break;
    	case OCE_IMAGE_LOGIC_CHARACTERISTICS : tagNames = "OCEImageLogicCharacteristics";  break;
    	case DNG_VERSION : tagNames = "DNGVersion";  break;
    	case DNG_BACKWARD_VERSION : tagNames = "DNGBackwardVersion";  break;
    	case UNIQUE_CAMERA_MODEL : tagNames = "UniqueCameraModel";  break;
    	case LOCALIZED_CAMERA_MODEL : tagNames = "LocalizedCameraModel";  break;
    	case CFA_PLANE_COLOR : tagNames = "CFAPlaneColor";  break;
    	case CFA_LAYOUT : tagNames = "CFALayout";  break;
    	case LINEARIZATION_TABLE : tagNames = "LinerizationTable";  break;
    	case BLACK_LEVEL_REPEAT_DIM : tagNames = "BlackLevelRepeatDim";  break;
    	case BLACK_LEVEL : tagNames = "BlackLevel";  break;
    	case BLACK_LEVEL_DELTAH : tagNames = "BlackLevelDeltah";  break;
    	case BLACK_LEVEL_DELTAV : tagNames = "BlackLevelDeltav";  break;
    	case WHITE_LEVEL : tagNames = "WhiteLevel";  break;
    	case DEFAULT_SCALE : tagNames = "DefaultScale";  break;
    	case DEFAULT_CROP_ORIGIN : tagNames = "DefaultCropOrigin";  break;
    	case DEFAULT_CROP_SIZE : tagNames = "DefaultCropSize";  break;
    	case COLOR_MATRIX1 : tagNames = "ColorMatrix1";  break;
    	case COLOR_MATRIX2 : tagNames = "ColorMatrix2";  break;
    	case CAMERA_CALIBRATION1 : tagNames = "CameraCalibration1";  break;
    	case CAMERA_CALIBRATION2 : tagNames = "CameraCalibaration2";  break;
    	case REDUCTION_MATRIX1 : tagNames = "ReductionMatrix1";  break;
    	case REDUCTION_MATRIX2 : tagNames = "ReductionMatrix2";  break;
    	case ANALOG_BALANCE : tagNames = "AnalogBalance";  break;
    	case AS_SHOT_NEUTRAL : tagNames = "AsShotNeutral";  break;
    	case AS_SHOT_WHITE_XY : tagNames = "AsShotWhiteXY";  break;
    	case BASELINE_EXPOSURE : tagNames = "BaselineExposure";  break;
    	case BASELINE_NOISE : tagNames = "BaselineNoise";  break;
    	case BASELINE_SHARPNESS : tagNames = "BaselineSharpness";  break;
    	case BAYER_GREEN_SPLIT : tagNames = "BayerGreenSplit";  break;
    	case LINEAR_RESPONSE_LIMIT : tagNames = "LinearResponseLimit";  break;
    	case CAMERA_SERIAL_NUMBER : tagNames = "CameraSerialNumber";  break;
    	case LENSE_INFO : tagNames = "LenseInfo";  break;
    	case CHROMA_BLUR_RADIUS : tagNames = "ChromaBlurRadius";  break;
    	case ANTI_ALIAS_STRENGTH : tagNames = "AntiAliasStrength";  break;
    	case SHADOW_SCALE : tagNames = "ShadowScale";  break;
    	case DNG_PRIVATE_DATA : tagNames = "DNGPrivateData";  break;
    	case MAKER_NOTE_SAFETY : tagNames = "MakerNoteSafety";  break;
    	case CALIBRATION_IIUMINANT1 : tagNames = "CalibrationIIuminant1";  break;
    	case CALIBRATION_IIUMINANT2 : tagNames = "CalibrationIIuminant2";  break;
    	case BEST_QUALITY_SCALE : tagNames = "BestQualityScale";  break;
    	case RAW_DATA_UNIQUE_ID : tagNames = "RawDataUniqueID";  break;
    	case ALIAS_LAYER_METADATA : tagNames = "AliasLayerMetadata";  break;
    	case ORIGINAL_RAW_FILE_NAME : tagNames = "OriginalRawFileName";  break;
    	case ORIGINAL_RAW_FILE_DATA : tagNames = "OriginalRawFileData";  break;
    	case ACTIVE_AREA : tagNames = "ActiveArea";  break;
    	case MASKED_AREAS : tagNames = "MaskedAreas";  break;
    	case AS_SHOT_ICC_PROFILE : tagNames = "AsShotIccProfile";  break;
    	case AS_SHOT_PRE_PROFILE_MATRIX : tagNames = "AsShotPreProfileMatrix";  break;
    	case CURRENT_ICC_PROFILE : tagNames = "CurrentIccProfile";  break;
    	case CURRENT_PRE_PROFILE_MATRIX : tagNames = "CurrentPreProfileMatrix";  break;
    	default: tagNames = "Unknown Tag"; break;
    	}
    	return tagNames;
    }  	          	 	
	
	
	private JFrame frame;
	private JPanel mainPanel;
	private Button tagButton;
	private static TextArea descriptionArea;
	private Button saveButton;
	JFileChooser mFileBrowser = null;
	String mDirectoryPath = null;
	private TextField fileDirectoryField;
	private JScrollPane scrollPane;

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
				int tag = 0;
		    	int maxBytes = 255;
		    	int[] error = new int[1];
		    	error[0] = 0;	
				try {
					DumTiffTagsApp window = new DumTiffTagsApp();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DumTiffTagsApp() {
		initialize();
		mFileBrowser = new JFileChooser();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 830, 577);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		mainPanel = new JPanel();
		mainPanel.setBounds(0, 0, 822, 543);
		frame.getContentPane().add(mainPanel);
		mainPanel.setLayout(null);
		
		tagButton = new Button("Load File");
		tagButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mFileBrowser.setCurrentDirectory(new File(System.getProperty("user.dir")));				
				mFileBrowser.setDialogTitle("Select Output Directory");
				mFileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int status = mFileBrowser.showDialog(frame, "OK");
				if (status == JFileChooser.APPROVE_OPTION)
				{
					fileDirectoryField.setText(mFileBrowser.getSelectedFile().getName().toString());		
					mDirectoryPath = mFileBrowser.getSelectedFile().toString();
							
				}
			}
		});
		tagButton.setBounds(10, 27, 70, 29);
		mainPanel.add(tagButton);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(32, 144, 780, 389);
		mainPanel.add(scrollPane);
		
		descriptionArea = new TextArea();
		descriptionArea.setFont(new Font("Dialog", Font.PLAIN, 14));
		scrollPane.setViewportView(descriptionArea);
		descriptionArea.setBackground(Color.WHITE);
		descriptionArea.setEditable(false);
		
		saveButton = new Button("Read Tag");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fileDirectoryField.getText().length() > 0)
				{
					int[] error = new int[1];
			    	error[0] = 0;	
					File afile = new File(mDirectoryPath);
					if(afile.exists())
					{
						dumpAllTags(tagImages, counter, mDirectoryPath, error);		
						System.out.println("Done");
				    	
					}
					else
					{
			    		System.out.println("Error: File does not exist");
			    		System.out.println("Exiting program");
			    		//System.exit(0);
			    	}
				}
				
			}
		});
		saveButton.setBounds(10, 77, 70, 29);
		mainPanel.add(saveButton);
		
		fileDirectoryField = new TextField();
		fileDirectoryField.setBounds(98, 27, 250, 29);
		fileDirectoryField.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				mDirectoryPath = fileDirectoryField.getText();
			}

			public void focusGained(FocusEvent e)
			{
				//intentionally left blank
			}
		});
		mainPanel.add(fileDirectoryField);
	}
	public static boolean moreData()
    {
    	return (dataVal != null);
    }
    public static String saveTags(String info, String tagName)
    {
    	if(tagName == null)
    		return tagName ; 
    	String strName = info + " : " + tagName;
    	if(strTiffTag == null)
    	{
    		strTiffTag = strName;
    	}		
    	else
    	{
    		strTiffTag += strName;
    	}
		return strName ;
	
    }
   
    public static String toStrings()
    {
    	return  " name = " + fileName + ", directory = " + directory + ", Width = " + simage.getWidth() + ", Height = " + simage.getHeight() + 
    			", X-Resoultion = " + simage.getXdpi()  + ", Y-Resoution = " + simage.getYdpi() + ", Orientation = " + simage.IMGLOW_get_image_orientation() +
    			", Color = " + simage.IMGLOW_detect_color() + ", Bits-Per Pixel = " + simage.getBitsPerPixel();
    }
	
	public static byte[] dumpAllTags(int tag, int maxBytes, String inFile, int[]error)
	{
		tiffTags = new TiffImageTags(console);
		simage = new Snowbnd();
		int[] tagValue = new int[1];
		tagValue[0] = 0;
		byte[] data = new byte[1024];
		int formatCode = 0;
		int totalPage = 0;
		int totalPageCount = 0;
		int pageIndex = 0;
		int imageSize = 0;
		DataInputStream inputStream;
		tagNames = new String();


		try
		{
			
			int fileType = simage.IMGLOW_get_filetype(inFile);

			FormatHash formatHash = FormatHash.getInstance();
			Format inputExt = formatHash.getFormat(fileType);
			formatHash.getFormatName(formatCode);
			System.out.println("Processing File: " + inFile + inputExt.getFormatName());



			stat = simage.IMG_decompress_bitmap(inFile, pageIndex);

			//tagNames = getTags(tag);

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

			data = simage.IMG_save_bitmap(initialBufferSize, increaseBufferSize, fileType, error);	

			//get image data for each page 
			totalPage = simage.IMGLOW_get_pages(inFile);
			if (totalPage < 0)
			{
				/* Return values less than zero indicate an error */
				System.out.println("Error in counting pages: "
						+ ErrorCodes.getErrorMessage(totalPage));
				return data;
			}
			else
			{
				System.out.println("The input document has " + totalPage
						+ " pages.");
			}

			//get pages one by one 
			for(pageIndex = 0; pageIndex < totalPage; pageIndex++)
			{
				System.out.println(" Processing Pages: " + pageIndex);
				System.out.println(" Initializing....");

				
				int decompress = simage.IMG_decompress_bitmap(inFile, pageIndex);			
				if (decompress < 0) {
					System.out.println("Error Decompressing Image: "  + ErrorCodes.getErrorMessage(decompress));
					continue;
				}	
				for(tag = 0; tag <= storeTags.length  ; tag++)
				{		
					stat = simage.IMGLOW_get_tiff_tag(tag, maxBytes ,tagValue,inFile, data,pageIndex);
					if(tag >= fileType)
					{
						tagNames = saveTags(getTagDescription(tag), toStrings());
					}		
					/*if(stat < 0)
            		{
            			System.out.println("Error getting tags " + "\n" + ErrorCodes.getErrorMessage(stat) + "\n");
            		}*/

					data = simage.IMG_save_bitmap(initialBufferSize, increaseBufferSize, fileType, error);	
					imageSize = tagValue[0];    			
					if(imageSize < 0)
					{
						System.out.println("Error saving Image: " + ErrorCodes.getErrorMessage(stat));
					}

					if(stat >= 0)//dumpAllTags(tag, value, inFile, tagValue);
					{		
						tagValue[0] = 0;
						iTagNum = tag;
						iTagValue = tagValue;
						totalPageCount = pageIndex;
						tagNamesBuff = inFile;


						strItem += "   " + "TagValue = " +  iTagNum + ", \"" + tagNames + "\", value = " + tagValue.length + "\"  PageNumber = " +  totalPageCount +  "\n";
						descriptionArea.append(strItem);
						//descriptionArea.append("----------------------------------------------------------------");

						System.out.println(strItem);

					
						File outputFile = new File(tagNamesBuff +"_tiffTags" + pageIndex +".rtf");
						saveFile(data, outputFile);


						// separation for multiple pages
						System.out.println("----------------------------------------------------------------------");

					}
				}  



				/*	if (stat < 0);
    			{
    				System.out.println((new StringBuilder("ERROR: ")).append(stat).append(" [").append(ErrorCodes.getErrorMessage(stat))
    						.append("] on p. {").append(pageIndex).append("}").toString());
    			}*/

			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return data;

	}
	
	 public static void saveFile(byte[] data,File file) throws IOException
		{
	    	if (data == null)
			{
				return;
			}
			try
			{
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data);
				fos.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
}
