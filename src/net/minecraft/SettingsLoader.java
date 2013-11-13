package net.minecraft;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SettingsLoader {

	private SettingsLoader() {

	}

	public static Settings load() throws IOException {
		File settingsfile = new File(MinecraftUtil.getWorkingDirectory(),
				"settings");
		return load(settingsfile);
	}

	public static Settings load(File f) throws IOException {
		if (!f.exists()) {
			f.createNewFile();
		}
		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream oin = new ObjectInputStream(fis);
			Settings settings = (Settings) oin.readObject();
			return settings;
		} catch (EOFException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return new Settings();
	}

	public static void save(Settings s) throws IOException {
		File settingsfile = new File(MinecraftUtil.getWorkingDirectory(),
				"settings");
		save(s, settingsfile);
	}

	public static void save(Settings s, File f) throws IOException {
		if (!f.exists()) {
			f.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(s);
		oos.flush();
		oos.close();
	}

}
