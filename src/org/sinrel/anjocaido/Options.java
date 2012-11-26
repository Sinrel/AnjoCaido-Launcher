package org.sinrel.anjocaido;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Options {

	private File fileName;
	
	public File getFileName() {
		return fileName;
	}

	public Options(File filename)
	{
		fileName = filename;
	}
	
	public void setOption(String key, String value) throws IOException
	{
		List<String> lines = FileUtils.getAllLines(fileName);
		for (String pair : lines)
		{
			String[] arr = pair.split(":",2);
			if(arr[0].equals(key))
			{
				lines.remove(pair);
				lines.add(key + ":" + value);
				FileUtils.writeAllLines(fileName, lines);
				return;
			}
		}
		lines.add(key + ":" + value);
		FileUtils.writeAllLines(fileName, lines);
	}
	
	public String getOption(String key) throws IOException
	{
		List<String> lines = FileUtils.getAllLines(fileName);
		for (String pair : lines)
		{
			String[] arr = pair.split(":",2);
			if(arr[0].equals(key))
			{
				String ret = "";
				for(int i = 1; i < arr.length; i++)
				{
						ret += arr[i] + ":";
				}
				return ret.substring(0, ret.length() - 1);
			}
		}
		return null;
	}
}
