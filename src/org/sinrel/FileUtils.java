package org.sinrel;

import java.io.*;
import java.util.List;

public class FileUtils {
	
	private FileUtils(){}
	
	public static void writeAllLines(File file, List<String> lines) throws IOException
	{
		WriteRead.writeToFile(file, lines);
	}
	
	public static List<String> getAllLines(File file) throws IOException
	{
		return WriteRead.readFromFile(file);
	}
}
