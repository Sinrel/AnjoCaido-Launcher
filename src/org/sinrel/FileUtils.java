package org.sinrel;

import java.io.*;
import java.util.List;

public class FileUtils {
	
	private FileUtils(){}
	
	public static void writeAllLines(File file, List<String> lines) throws IOException
	{
		org.apache.commons.io.FileUtils.writeLines(file, lines);
	}
	
	public static List<String> getAllLines(File file) throws IOException
	{
		return org.apache.commons.io.FileUtils.readLines(file);
	}
}
