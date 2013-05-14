package net.minecraft;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sinrel.anjocaido.Options;

public class MinecraftUtil {
	
	private static File workDir = null;
	private static File binDir = null;
	private static File resourcesDis = null;
	private static File optionsFile = null;
	private static File lastloginFile = null;
	private static File savesDir = null;
	private static File tempFolder = null;
	private static File nativesFolder = null;
	
	public static Options getOptions()
	{
		File optionsFile = new File(getWorkingDirectory(), "Sinrel-options.txt");
		if(!optionsFile.exists())
			try {
				optionsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return new Options(optionsFile);
	}

	public static File getWorkingDirectory() {
		if (workDir == null) {
			workDir = getWorkingDirectory("minecraft");
		}
		return workDir;
	}
	
	public static File getBinFolder() {
		if (binDir == null) {
			binDir = new File(getWorkingDirectory(), "bin");
		}
		return binDir;
	}

	public static File getResourcesFolder() {
		if (resourcesDis == null) {
			resourcesDis = new File(getWorkingDirectory(), "resources");
		}
		return resourcesDis;
	}
	
	public static File getOptionsFile() {
		if (optionsFile == null) {
			optionsFile = new File(getWorkingDirectory(), "options.txt");
		}
		return optionsFile;
	}

	public static File getLoginFile() {
		if (lastloginFile == null) {
			lastloginFile = new File(getWorkingDirectory(), "lastlogin");
		}
		return lastloginFile;
	}

	public static File getSavesFolder() {
		if (savesDir == null) {
			savesDir = new File(getWorkingDirectory(), "saves");
		}
		return savesDir;
	}

	public static File getNativesFolder() {
		if (nativesFolder == null) {
			nativesFolder = new File(getBinFolder(), "natives");
		}
		return nativesFolder;
    }

	public static File getTempFolder() {
		if (tempFolder == null) {
			tempFolder = new File(System.getProperties().getProperty("java.io.tmpdir"), "MCBKPMNGR");
		}
		
		if (!tempFolder.exists()) {
			tempFolder.mkdirs();
		}
		return tempFolder;
    }

	public static File getWorkingDirectory(String applicationName) {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		
		switch (getPlatform().ordinal()) {
		case 0:
			case 1:
				workingDirectory = new File(userHome, '.' + applicationName + '/');
				break;
				case 2:
					String applicationData = System.getenv("APPDATA");
					if (applicationData != null)
						workingDirectory = new File(applicationData, "." + applicationName + '/');
					else {
						workingDirectory = new File(userHome, '.' + applicationName + '/');
					}
					break;
					case 3:
						workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
						break;
						default:
							workingDirectory = new File(userHome, applicationName + '/');
		}
		
		if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs())) {
			throw new RuntimeException("The working directory could not be created: " + workingDirectory);
		}
		
		return workingDirectory;
   }

  private static OS getPlatform() {
	  String osName = System.getProperty("os.name").toLowerCase();
	  
	  if (osName.contains("win")) {
		  return OS.windows;
	  }
	  
	  if (osName.contains("mac")) {
		  return OS.macos;
	  }
	  
	  if (osName.contains("solaris")) {
		  return OS.solaris;
	  }
	  
	  if (osName.contains("sunos")) {
		  return OS.solaris;
	  }
	  
	  if (osName.contains("linux")) {
		  return OS.linux;
	   }
	  
	  if (osName.contains("unix")) {
		  return OS.linux;
	  }
	  
	  return OS.unknown;
  }

  public static String excutePost(String targetURL, String urlParameters) {
	  HttpURLConnection connection = null;
	  try {
		  URL url = new URL(targetURL);
		  connection = (HttpURLConnection)url.openConnection();
		  
		  connection.setRequestMethod("POST");
		  connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		  connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
		  connection.setRequestProperty("Content-Language", "en-US");

		  connection.setUseCaches(false);
		  connection.setDoInput(true);
		  connection.setDoOutput(true);

		  DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		  wr.writeBytes(urlParameters);
		  wr.flush();
		  wr.close();
      
		  InputStream is = connection.getInputStream();
		  BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		  
		  StringBuffer response = new StringBuffer();
		  String line;
		  while ((line = rd.readLine()) != null) {
			  response.append(line);
			  response.append('\r');
		  }
		  rd.close();

		  String str1 = response.toString();
		  return str1;
     }catch (Exception e) {
    	 e.printStackTrace();
    	 return null;
     }finally {
    	 if (connection != null)
    		 connection.disconnect();
     }
  }

  public static void resetVersion()
  {
    DataOutputStream dos = null;
    try {
      File dir = new File(getWorkingDirectory() + File.separator + "bin" + File.separator);
      File versionFile = new File(dir, "version");
      dos = new DataOutputStream(new FileOutputStream(versionFile));
      dos.writeUTF("0");
      dos.close();
    } catch (FileNotFoundException ex) {
      Logger.getLogger(MinecraftUtil.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(MinecraftUtil.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        dos.close();
      } catch (IOException ex) {
        Logger.getLogger(MinecraftUtil.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public static String getFakeLatestVersion() {
    try {
      File dir = new File(getWorkingDirectory() + File.separator + "bin" + File.separator);
      File file = new File(dir, "version");
      DataInputStream dis = new DataInputStream(new FileInputStream(file));
      String version = dis.readUTF();
      dis.close();
      if (version.equals("0")) {
        return "5909222";
      }
      return version; } catch (IOException ex) {
    }
    return "5909222";
  }

  private static enum OS {
	  linux, solaris, windows, macos, unknown;
  }
}