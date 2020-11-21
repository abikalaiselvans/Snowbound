package com.mkm.test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

public class MultiOutputStream extends OutputStream
{
	//public static String userHome = System.getProperty("user.home",".");
	//public static String FILE_SEPARATOR = System.getProperty("file.separator","/");
	//public static String logFileName = (userHome+FILE_SEPARATOR+"ErrorOuput.log");
	OutputStream[] outputStreams;
	
	public MultiOutputStream(OutputStream... outputStreams)
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
			
			MultiOutputStream multiOut= new MultiOutputStream(System.out, fout);
			//MultiOutputStream multiErr= new MultiOutputStream(System.err, ferr);
			
			PrintStream stdout= new PrintStream(multiOut);
			PrintStream stderr= new PrintStream(ferr);
			
			System.setOut(stdout);
			System.setErr(stderr);
		}
		catch (FileNotFoundException ex)
		{
			//Could not create/open the file
		}
	}
	
	
}