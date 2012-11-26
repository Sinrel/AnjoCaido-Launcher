package anjocaido.minecraftmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;
import net.minecraft.MinecraftUtil;

public class BackupUtil {
	
	public static final String WORLD_BACKUP_EXTENSION = "mcworld";
	public static final String WORLD_BACKUP_GEN_NAME = "world_backup";
	public static final String GAME_BACKUP_EXTENSION = "mcgame";
	public static final String GAME_BACKUP_GEN_NAME = "minecraft_backup";
	public static final String DATE_TIME_FORMAT = "%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS";

  public static void uninstallGame(boolean includeSaves) {
	  deleteFileDir(MinecraftUtil.getBinFolder());
	  deleteFileDir(MinecraftUtil.getLoginFile());
	  deleteFileDir(MinecraftUtil.getResourcesFolder());
	  deleteFileDir(MinecraftUtil.getOptionsFile());
	  if (includeSaves){
		  deleteFileDir(MinecraftUtil.getSavesFolder());
	  }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void backupGame(File zipDestiny, boolean wholegame) {
	  File[] source;
	  if (!wholegame) {
		  ArrayList contents = new ArrayList();
		  File f = MinecraftUtil.getBinFolder();
		  if (f.exists()) {
    		contents.add(f);
    	}
    	f = MinecraftUtil.getResourcesFolder();
    	if (f.exists()) {
    		contents.add(f);
    	}
    	f = MinecraftUtil.getLoginFile();
    	if (f.exists()) {
    		contents.add(f);
    	}
    	f = MinecraftUtil.getOptionsFile();
    	if (f.exists()) {
    		contents.add(f);
    	}
    	source = (File[])contents.toArray(new File[contents.size()]);
    	}else{
    	source = MinecraftUtil.getWorkingDirectory().listFiles();
    	}
	  backupContents(source, zipDestiny, "minecraft_backup", "mcgame");
  }

  public static void restoreGame(File zipSource) {
	  File destiny = MinecraftUtil.getWorkingDirectory();
	  restoreContents(zipSource, destiny, "minecraft_backup");
  }
  
  public static List<File> getWorlds(){
	  List<File> files = new ArrayList<File>();
	  
	  for(File f : MinecraftUtil.getSavesFolder().listFiles())
	  {
		  if(f.isDirectory())
			  files.add(f);
	  }
	  return files;
  }

  public static File getWorldNFolder(int n) {
	  File source = new File(MinecraftUtil.getSavesFolder(), "World" + n);
	  return source;
  }

  public static void backupWorld(File source, File destZip) {
	  backupFile(source, destZip, "world_backup", "mcworld");
  }

  public static void restoreWorld(File destiny, File zipSource) {
	  restoreFile(zipSource, destiny, "world_backup");
  }

  public static void backupFile(File source, File zipDestiny, String genericName, String extension) {
	  File generic = new File(MinecraftUtil.getTempFolder(), genericName);
	  if (!source.exists()) {
		  throw new IllegalArgumentException("Source file does not exist: " + source.getName());
	  }
	  if (!zipDestiny.getName().endsWith("." + extension)) {
		  zipDestiny = new File(zipDestiny.getPath() + "." + extension);
	  }
	  if (generic.exists()) {
		  deleteFileDir(generic);
      }
	  source.renameTo(generic);
	  Zipper.zipFolder(generic, zipDestiny);
	  generic.renameTo(source);
  }
  
  public static String getExtension(File f) {
	  String ext = null;
	  String s = f.getName();
	  int i = s.lastIndexOf('.');
	  if ((i > 0) && (i < s.length() - 1)) {
		  ext = s.substring(i + 1).toLowerCase();
	  }
	  return ext;
  }

  public static boolean deleteFileDir(File dir) {
	  if (!dir.exists()) {
		  return false;
	  }
	  if (dir.isFile()){
		  return dir.delete();
	  }
	  if (dir.isDirectory()) {
		  for (File f : dir.listFiles()) {
			  deleteFileDir(f);
		  }
	  return dir.delete();
      }
	  return dir.delete();
  }

  public static void restoreFile(File zipSource, File destiny, String genericName) {
	  File generic = new File(MinecraftUtil.getTempFolder(), genericName);
	  if (generic.exists()) {
		  deleteFileDir(generic);
	  }
	  Zipper.unzipFolder(zipSource, MinecraftUtil.getTempFolder());
	  if (!generic.exists()) {
		  throw new IllegalStateException("Wrong content in zip file -> not found: " + generic.getName());
	  }
	  if (destiny.exists()) {
		  deleteFileDir(destiny);
	  }
	  if ((destiny.getParentFile() != null) && (!destiny.getParentFile().exists())) {
		  destiny.getParentFile().mkdirs();
	  }
	  generic.renameTo(destiny);
  }

  public static void backupContents(File[] folderContents, File zipDestiny, String genericName, String extension) {
	  for (File content : folderContents) {
		  if (!content.exists()) {
			  throw new IllegalArgumentException("You sent me a folder content that doesnt exist : " + content.getName());
		  }
	  }
	  if (!zipDestiny.getName().endsWith("." + extension)) {
		  zipDestiny = new File(zipDestiny.getPath() + "." + extension);
	  }
	  Zipper.zipFolders(folderContents, zipDestiny, genericName);
  }

  public static void restoreContents(File zipSource, File folderDestiny, String genericName) {
	  File genericFolder = new File(MinecraftUtil.getTempFolder(), genericName);
	  if (genericFolder.exists()) {
		  deleteFileDir(genericFolder);
	  }
	  if (!folderDestiny.exists()) {
		  folderDestiny.mkdirs();
	  }
	  if (!folderDestiny.isDirectory()) {
		  throw new IllegalArgumentException("The destiny folder must be a directory!");
	  }
	  Zipper.unzipFolder(zipSource, MinecraftUtil.getTempFolder());
	  if (!genericFolder.exists()) {
		  throw new IllegalStateException("Wrong content in zip file -> not found: " + genericFolder.getName());
	  }
	  File[] generics = genericFolder.listFiles();
	  for (File generic : generics) {
		  File destiny = new File(folderDestiny, generic.getName());
		  if (destiny.exists()) {
			  deleteFileDir(destiny);
		  }
		  generic.renameTo(destiny);
	  }
  }

  public static class GameFileFilter extends FileFilter {
	  public boolean accept(File f) {
		  if (f == null) {
			  return false;
		  }
		  if (f.isDirectory()) {
			  return true;
		  }
		  String ext = BackupUtil.getExtension(f);
		  return (ext != null) && (ext.equalsIgnoreCase("mcgame"));
    }

    public String getDescription() {
    	return "Minecraft Game files";
    }
  }

  public static class WorldFileFilter extends FileFilter {
	  public boolean accept(File f) {
		  if (f == null) {
			  return false;
		  }
		  if (f.isDirectory()) {
			  return true;
		  }
		  String ext = BackupUtil.getExtension(f);
		  return (ext != null) && (ext.equalsIgnoreCase("mcworld"));
	  }
	  
	  public String getDescription() {
		  return "Minecraft World files";
	  }
  }
}