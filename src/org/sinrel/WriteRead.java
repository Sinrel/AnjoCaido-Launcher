package org.sinrel;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WriteRead {
	
	public static String addStringAndWriteToFile(File filename, String strPage ){
		List<String> strings = readFromFile(filename);
		strings.add(strPage);
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
	
	public static String writeToFile(File filename, List<String> strings ){
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
	
	public static List<String> readFromFile(File filename) {
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
		} catch (IOException e) {}
		return strings;
	}
	


}
