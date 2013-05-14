package org.sinrel.anjocaido;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	
	private FileUtils(){}
	
	public static void writeAllLines(File file, List<String> lines) throws IOException
	{
		writeToFile(file, lines);
	}
	
	public static List<String> getAllLines(File file) throws IOException
	{
		return readFromFile(file);
	}
		
	private static String writeToFile(File filename, List<String> strings ){
	    PrintWriter writer = null;
	    try {
	     writer = new PrintWriter(
	             new OutputStreamWriter(
	             new FileOutputStream(filename), "windows-1251"));
	     String compared = "";
	     for (String s:strings)
	     {
	    	 compared += s + "\n";
	     }
	     writer.write(compared);
	     writer.close();
	    } catch (Exception ex) {} 
	return null;
	
	}
	
	private static List<String> readFromFile(File filename) {
		List<String> strings = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e1) {}
			String line;
		try {
			while ((line = reader.readLine()) != null) {
				strings.add(line);
			}
			
			if( reader != null )
				reader.close();
		} catch (IOException e) {}
		
		return strings;
	}
}
