package net.minecraft;
 
import SevenZip.LzmaAlone;
import java.applet.Applet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import org.sinrel.anjocaido.OptionsForm;

import anjocaido.minecraftmanager.Zipper;
 
public class GameUpdater implements Runnable {	 
	
	public static final int STATE_INIT = 1;
	public static final int STATE_DETERMINING_PACKAGES = 2;
	public static final int STATE_CHECKING_CACHE = 3;
	public static final int STATE_DOWNLOADING = 4;
	public static final int STATE_EXTRACTING_PACKAGES = 5;
	public static final int STATE_UPDATING_CLASSPATH = 6;
	public static final int STATE_SWITCHING_APPLET = 7;
	public static final int STATE_INITIALIZE_REAL_APPLET = 8;
	public static final int STATE_START_REAL_APPLET = 9;
	public static final int STATE_DONE = 10;
	
	public int percentage;
	public int currentSizeDownload;
	public int totalSizeDownload;
	public int currentSizeExtract;
	public int totalSizeExtract;
	
	protected URL[] urlList;
	private static ClassLoader classLoader;
	protected Thread loaderThread;
	protected Thread animationThread;
	public boolean fatalError;
	public String fatalErrorDescription;
	protected String subtaskMessage = "";
	protected int state = 1;
	protected boolean lzmaSupported = false;
	protected boolean pack200Supported = false;
	protected String[] genericErrorMessage = { "An error occured while loading the applet.", "Please contact support to resolve this issue.", "<placeholder for error message>" };
	protected boolean certificateRefused;
	protected String[] certificateRefusedMessage = { "Permissions for Applet Refused.", "Please accept the permissions dialog to allow", "the applet to continue the loading process." };
	protected static boolean natives_loaded = false;
	public boolean forceUpdate = false;
	public static final String[] gameFiles = { "lwjgl.jar", "jinput.jar", "lwjgl_util.jar", "minecraft.jar", "client.zip" };
	
	InputStream[] isp;
	URLConnection urlconnectionp;
 
   public void init() {
	   this.state = 1;
	   try {
		   Class.forName("LZMA.LzmaInputStream");
		   this.lzmaSupported = true;
	   } catch (Throwable localThrowable) {}
	   
	   try {
		   Pack200.class.getSimpleName();
		   this.pack200Supported = true;
	   } catch (Throwable localThrowable1) {}
   }
   
   private String generateStacktrace(Exception exception) {
	   Writer result = new StringWriter();
	   PrintWriter printWriter = new PrintWriter(result);
	   exception.printStackTrace(printWriter);
	   return result.toString();
   }
 
   protected String getDescriptionForState() {
	   switch (state) {
	   case 1:
		   return "Инициализация загрузчика";
		   case 2:
			   return "Обнаружение пакетов для скачки";
			   case 3:
				   return "Проверка кеш-файлов";
				   case 4:
					   return "Скачивание пакетов";
					   case 5:
						   return "Извлечение скачанных пакетов";
						   case 6:
							   return "Обновление путей";
							   case 7:
								   return "Сворачивание апплета";
								   case 8:
									   return "Инициализация реального апплета";
									   case 9:
										   return "Старт реального апплета";
										   case 10:
											   return "Загрузка завершена";
											   }
	   return "Неизвестное положение";
  }

   protected void loadJarURLs() throws MalformedURLException, IOException  {
	   this.state = 2;
 
	   this.urlList = new URL[gameFiles.length + 1];
 
	   URL path = new URL(MinecraftUtil.getOptions().getOption(OptionsForm.UPDATE_OPTION));
 
	   for (int i = 0; i < gameFiles.length; i++) {
		   	this.urlList[i] = new URL(path, gameFiles[i]);
	   }
 
	   String osName = System.getProperty("os.name");
	   String nativeJar = null;
 
	   if (osName.startsWith("Win"))
		   nativeJar = "windows_natives.jar.lzma";
	   else if (osName.startsWith("Linux"))
		   nativeJar = "linux_natives.jar.lzma";
	   else if (osName.startsWith("Mac"))
		   nativeJar = "macosx_natives.jar.lzma";
	   else if ((osName.startsWith("Solaris")) || (osName.startsWith("SunOS")))
		   nativeJar = "solaris_natives.jar.lzma";
	   else {
		   fatalErrorOccured("OS (" + osName + ") не поддерживается", null);
	   }
 
	   if (nativeJar == null)
		   fatalErrorOccured("lwjgl файлы не найдены", null);
	   else
		   this.urlList[(this.urlList.length - 1)] = new URL(path, nativeJar);
	   }
 
   public void run() {
	   init();
	   this.state = 3;
 
	   this.percentage = 5;
	   
	   try {
		   loadJarURLs();
		   
		   @SuppressWarnings({ "unchecked", "rawtypes" })
		   String path = (String)AccessController.doPrivileged(new PrivilegedExceptionAction() {
			   public Object run() throws Exception {
				   return MinecraftUtil.getWorkingDirectory() + File.separator + "bin" + File.separator;
			   }
		   });
		   File dir = new File(path);
 
		   if (!dir.exists()) {
			   dir.mkdirs();
		   }
		   
		   int before = this.percentage;
		   boolean cacheAvailable = false;
		   
		   if (canPlayOffline()) {
			   cacheAvailable = true;
			   this.percentage = 90;
		   }
		   
		   if ((this.forceUpdate) || (!cacheAvailable)) {
			   if (this.percentage != before) {
				   this.percentage = before;
			   }
			   
			   System.out.println("����: " + path);
			   downloadJars(path);
			   extractJars(path);
			   extractNatives(path);
			   File zip = new File(path + "client.zip");
			   System.out.println(zip.toString());
			   zip.setWritable(true);
			   Zipper.unzipFolder(zip, MinecraftUtil.getWorkingDirectory());
			   zip.delete();
			   
			   this.percentage = 90;
		   }
		   
		   updateClassPath(dir);
		   this.state = 10;
	   } catch (AccessControlException ace) {
		   fatalErrorOccured(ace.getMessage(), ace);
		   this.certificateRefused = true;
	   }catch (Exception e) {
		   fatalErrorOccured(e.getMessage(), e);
	   } finally {
		   this.loaderThread = null;
	   }
   }
 
   protected void updateClassPath(File dir) throws Exception {
	   this.state = 6;
	   this.percentage = 95;
 
	   URL[] urls = new URL[this.urlList.length];
	   
	   for (int i = 0; i < this.urlList.length; i++) {
		   urls[i] = new File(dir, getJarName(this.urlList[i])).toURI().toURL();
		   System.out.println("URL: " + urls[i]);
	   }
 
	   if (classLoader == null)
		   classLoader = new URLClassLoader(urls) {
		   protected PermissionCollection getPermissions(CodeSource codesource) {
		   PermissionCollection perms = null;
		   
		   try {
			   Method method = SecureClassLoader.class.getDeclaredMethod("getPermissions", new Class[] { CodeSource.class });
			   method.setAccessible(true);
			   perms = (PermissionCollection)method.invoke(getClass().getClassLoader(), new Object[] { codesource });
 
			   String host = "www.minecraft.net";
 
			   if ((host != null) && (host.length() > 0))
				   perms.add(new SocketPermission(host, "connect,accept"));
			   else {
				   codesource.getLocation().getProtocol().equals("file");
			  }
 
			  perms.add(new FilePermission("<<ALL FILES>>", "read"));
	       } catch (Exception e) {
	    	   e.printStackTrace();
           }
		   return perms;
		   }};
	   String path = dir.getAbsolutePath();
	   
	   if (!path.endsWith(File.separator)) {
		   path = path + File.separator;
	   }
	   
	   unloadNatives(path);
	   
	   System.setProperty("org.lwjgl.librarypath", path + "natives");
	   System.setProperty("net.java.games.input.librarypath", path + "natives");

	   natives_loaded = true;
   }
 
   private void unloadNatives(String nativePath) {
	   if (!natives_loaded)
		   return;
	   try {
		   Field field = ClassLoader.class.getDeclaredField("loadedLibraryNames");
		   field.setAccessible(true);
		   
		   @SuppressWarnings("rawtypes")
		   Vector libs = (Vector)field.get(getClass().getClassLoader());
 
		   String path = new File(nativePath).getCanonicalPath();
 
		   for (int i = 0; i < libs.size(); i++) {
			   String s = (String)libs.get(i);
			   
			   if (s.startsWith(path)) {
				   libs.remove(i);
				   i--;
			   }
		   }
	   } catch (Exception e) {
		   e.printStackTrace();
	  }
   }
 
   public Applet createApplet() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	   @SuppressWarnings("rawtypes")
	   Class appletClass = classLoader.loadClass("net.minecraft.client.MinecraftApplet");
	   return (Applet)appletClass.newInstance();
   }
 
   protected void downloadJars(String path) throws Exception
   {
     this.state = 4;
 
     int[] fileSizes = new int[this.urlList.length];
 
     for (int i = 0; i < this.urlList.length; i++) {
       System.out.println(this.urlList[i]);
       URLConnection urlconnection = null;
       urlconnection = this.urlList[i].openConnection();
       urlconnection.setDefaultUseCaches(false);
       if ((urlconnection instanceof HttpURLConnection)) {
         ((HttpURLConnection)urlconnection).setRequestMethod("HEAD");
       }
       fileSizes[i] = urlconnection.getContentLength();
       this.totalSizeDownload += fileSizes[i];
     }
 
     int initialPercentage = this.percentage = 10;
 
     byte[] buffer = new byte[65536];
     for (int i = 0; i < this.urlList.length; i++) {
       int unsuccessfulAttempts = 0;
       int maxUnsuccessfulAttempts = 3;
       boolean downloadFile = true;
 
       while (downloadFile) {
         downloadFile = false;
 
         URLConnection urlconnection = this.urlList[i].openConnection();
 
         if ((urlconnection instanceof HttpURLConnection)) {
           urlconnection.setRequestProperty("Cache-Control", "no-cache");
           try{
        	   urlconnection.connect();
           }catch (Exception e) {
        	   System.out.println(this.urlList[i]);
       		if (this.urlList[i].toString().endsWith("client.zip"))
       			continue;
       		else throw e;
		}
         }
 
         String currentFile = getFileName(this.urlList[i]);
         InputStream inputstream = getJarInputStream(currentFile, urlconnection);
         if (inputstream == null) continue;
         FileOutputStream fos = new FileOutputStream(path + currentFile);

         long downloadStartTime = System.currentTimeMillis();
         int downloadedAmount = 0;
         int fileSize = 0;
         String downloadSpeedMessage = "";
         int bufferSize;
         while ((bufferSize = inputstream.read(buffer, 0, buffer.length)) != -1)
         {
           fos.write(buffer, 0, bufferSize);
           this.currentSizeDownload += bufferSize;
           fileSize += bufferSize;
           this.percentage = (initialPercentage + this.currentSizeDownload * 45 / this.totalSizeDownload);
           this.subtaskMessage = ("Загрузка: " + currentFile + " " + this.currentSizeDownload * 100 / this.totalSizeDownload + "%");
 
           downloadedAmount += bufferSize;
           long timeLapse = System.currentTimeMillis() - downloadStartTime;
 
           if (timeLapse >= 1000L) {
             float downloadSpeed = downloadedAmount / (float)timeLapse;
 
             downloadSpeed = (int)(downloadSpeed * 100.0F) / 100.0F;
 
             downloadSpeedMessage = " @ " + downloadSpeed + " KB/sec";
 
             downloadedAmount = 0;
 
             downloadStartTime += 1000L;
           }
 
           this.subtaskMessage += downloadSpeedMessage;
         }
 
         inputstream.close();
         fos.close();
 
         if ((!(urlconnection instanceof HttpURLConnection)) || (fileSize == fileSizes[i]) || 
           (fileSizes[i] <= 0)) {
           continue;
         }
         unsuccessfulAttempts++;
 
         if (unsuccessfulAttempts < maxUnsuccessfulAttempts) {
           downloadFile = true;
           this.currentSizeDownload -= fileSize;
         } else {
           throw new Exception("failed to download " + currentFile);
         }
       }
 
     }
 
     this.subtaskMessage = "";
   }
 
   protected InputStream getJarInputStream(String currentFile, URLConnection urlconnection) throws Exception
   {
     InputStream[] is = new InputStream[1];
 
     this.isp = is;
     this.urlconnectionp = urlconnection;
     for (int j = 0; (j < 3) && (is[0] == null); j++) {
       Thread t = new Thread()
       {
         public void run()
         {
           try {
             GameUpdater.this.isp[0] = GameUpdater.this.urlconnectionp.getInputStream();
           }
           catch (Exception localIOException)
           {
           }
         }
       };
       t.setName("JarInputStreamThread");
       t.start();
 
       int iterationCount = 0;
       while ((is[0] == null) && (iterationCount++ < 5))
         try {
           t.join(1000L);
         }
         catch (InterruptedException localInterruptedException) {
         }
       if (is[0] != null)
         continue;
       try
       {
         t.interrupt();
         t.join();
       }
       catch (InterruptedException localInterruptedException1) {
       }
     }
     if (is[0] == null) {
       if (currentFile.equals("minecraft.jar")) {
         throw new Exception("Unable to download " + currentFile);
       }
       if(currentFile.equalsIgnoreCase("client.zip")) return null;
       throw new Exception("Unable to download " + currentFile);
     }
 
     return is[0];
   }
 
   protected void extractLZMA(String in, String out) throws Exception
   {
     File f = new File(in);
     File fout = new File(out);
     LzmaAlone.decompress(f, fout);
     f.delete();
   }
 
   protected void extractPack(String in, String out) throws Exception
   {
     File f = new File(in);
     FileOutputStream fostream = new FileOutputStream(out);
     JarOutputStream jostream = new JarOutputStream(fostream);
 
     Pack200.Unpacker unpacker = Pack200.newUnpacker();
     unpacker.unpack(f, jostream);
     jostream.close();
 
     f.delete();
   }
 
   protected void extractJars(String path) throws Exception
   {
     this.state = 5;
 
     float increment = 10.0F / this.urlList.length;
 
     for (int i = 0; i < this.urlList.length; i++) {
       this.percentage = (55 + (int)(increment * (i + 1)));
       String filename = getFileName(this.urlList[i]);
 
       if (filename.endsWith(".pack.lzma")) {
         this.subtaskMessage = ("Extracting: " + filename + " to " + filename.replaceAll(".lzma", ""));
         extractLZMA(path + filename, path + filename.replaceAll(".lzma", ""));
 
         this.subtaskMessage = ("Extracting: " + filename.replaceAll(".lzma", "") + " to " + filename.replaceAll(".pack.lzma", ""));
         extractPack(path + filename.replaceAll(".lzma", ""), path + filename.replaceAll(".pack.lzma", ""));
       } else if (filename.endsWith(".pack")) {
         this.subtaskMessage = ("Extracting: " + filename + " to " + filename.replace(".pack", ""));
         extractPack(path + filename, path + filename.replace(".pack", ""));
       } else if (filename.endsWith(".lzma")) {
         this.subtaskMessage = ("Extracting: " + filename + " to " + filename.replace(".lzma", ""));
         extractLZMA(path + filename, path + filename.replace(".lzma", ""));
       }
     }
   }
 
   protected void extractNatives(String path) throws Exception {
     this.state = 5;
 
     int initialPercentage = this.percentage;
 
     String nativeJar = getJarName(this.urlList[(this.urlList.length - 1)]);
 
     Certificate[] certificate = Launcher.class.getProtectionDomain().getCodeSource().getCertificates();
 
     if (certificate == null) {
       URL location = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
 
       JarURLConnection jurl = (JarURLConnection)new URL("jar:" + location.toString() + "!/net/minecraft/Launcher.class").openConnection();
       jurl.setDefaultUseCaches(true);
       try {
         certificate = jurl.getCertificates();
       } catch (Exception localException) {
       }
     }
     File nativeFolder = new File(path + "natives");
     if (!nativeFolder.exists()) {
       nativeFolder.mkdir();
     }
 
     JarFile jarFile = new JarFile(path + nativeJar, true);
     @SuppressWarnings("rawtypes")
	Enumeration entities = jarFile.entries();
 
     this.totalSizeExtract = 0;
 
     while (entities.hasMoreElements()) {
       JarEntry entry = (JarEntry)entities.nextElement();
 
       if ((entry.isDirectory()) || (entry.getName().indexOf('/') != -1)) {
         continue;
       }
       this.totalSizeExtract = (int)(this.totalSizeExtract + entry.getSize());
     }
 
     this.currentSizeExtract = 0;
 
     entities = jarFile.entries();
 
     while (entities.hasMoreElements()) {
       JarEntry entry = (JarEntry)entities.nextElement();
 
       if ((entry.isDirectory()) || (entry.getName().indexOf('/') != -1)) {
         continue;
       }
       File f = new File(path + "natives" + File.separator + entry.getName());
       if ((f.exists()) && (!f.delete()))
       {
         continue;
       }
 
       InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));
       OutputStream out = new FileOutputStream(path + "natives" + File.separator + entry.getName());
 
       byte[] buffer = new byte[65536];
       int bufferSize;
       while ((bufferSize = in.read(buffer, 0, buffer.length)) != -1) {
         out.write(buffer, 0, bufferSize);
         this.currentSizeExtract += bufferSize;
 
         this.percentage = (initialPercentage + this.currentSizeExtract * 20 / this.totalSizeExtract);
         this.subtaskMessage = ("Extracting: " + entry.getName() + " " + this.currentSizeExtract * 100 / this.totalSizeExtract + "%");
       }
 
       validateCertificateChain(certificate, entry.getCertificates());
 
      in.close();
      out.close();
     }
     this.subtaskMessage = "";
 
     jarFile.close();
 
     File f = new File(path + nativeJar);
     f.delete();
   }
 
   protected static void validateCertificateChain(Certificate[] ownCerts, Certificate[] native_certs) throws Exception
   {
     if (ownCerts == null) {
       return;
     }
     if (native_certs == null) {
       throw new Exception("Unable to validate certificate chain. Native entry did not have a certificate chain at all");
     }
 
     if (ownCerts.length != native_certs.length) {
       throw new Exception("Unable to validate certificate chain. Chain differs in length [" + ownCerts.length + " vs " + native_certs.length + "]");
     }
 
     for (int i = 0; i < ownCerts.length; i++)
       if (!ownCerts[i].equals(native_certs[i]))
         throw new Exception("Certificate mismatch: " + ownCerts[i] + " != " + native_certs[i]);
   }
 
   protected String getJarName(URL url)
   {
     String fileName = url.getFile();
 
     if (fileName.contains("?")) {
       fileName = fileName.substring(0, fileName.indexOf("?"));
     }
     if (fileName.endsWith(".pack.lzma"))
       fileName = fileName.replaceAll(".pack.lzma", "");
     else if (fileName.endsWith(".pack"))
       fileName = fileName.replaceAll(".pack", "");
     else if (fileName.endsWith(".lzma")) {
       fileName = fileName.replaceAll(".lzma", "");
     }
 
     return fileName.substring(fileName.lastIndexOf('/') + 1);
   }
 
   protected String getFileName(URL url) {
     String fileName = url.getFile();
     if (fileName.contains("?")) {
       fileName = fileName.substring(0, fileName.indexOf("?"));
     }
     return fileName.substring(fileName.lastIndexOf('/') + 1);
   }
 
   protected void fatalErrorOccured(String error, Exception e) {
     e.printStackTrace();
     this.fatalError = true;
     this.fatalErrorDescription = ("Fatal error occured (" + this.state + "): " + error);
     System.out.println(this.fatalErrorDescription);
     if (e != null)
       System.out.println(generateStacktrace(e));
   }
 
   public boolean canPlayOffline()
   {
     if ((!MinecraftUtil.getBinFolder().exists()) || (!MinecraftUtil.getBinFolder().isDirectory())) {
       return false;
     }
     if ((!MinecraftUtil.getNativesFolder().exists()) || (!MinecraftUtil.getNativesFolder().isDirectory())) {
       return false;
     }
     if (MinecraftUtil.getBinFolder().list().length < gameFiles.length) {
       return false;
     }
     if (MinecraftUtil.getNativesFolder().list().length < 1) {
       return false;
     }
     String[] bins = MinecraftUtil.getBinFolder().list();
     for (String necessary : gameFiles) {
       boolean isThere = false;
       for (String found : bins) {
         if (necessary.equalsIgnoreCase(found) || necessary.equalsIgnoreCase("client.zip")) {
           isThere = true;
           break;
         }
       }
       if (!isThere)
       {
         return false;
       }
     }
     return true;
   }
 }