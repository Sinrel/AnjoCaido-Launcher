package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.MinecraftUtil.OS;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LauncherFrame extends Frame implements Serializable {

	public static final int VERSION = 12;
	private static final long serialVersionUID = 1L;

	private LoginForm loginForm;

	public boolean forceUpdate = false;

	public LauncherFrame() {
		super("Minecraft Launcher" + " v" + MinecraftLauncher.version);
		setBackground(Color.BLACK);

		this.loginForm = new LoginForm(this);

		setLayout(new BorderLayout());
		add(this.loginForm, "Center");

		this.loginForm.setPreferredSize(new Dimension(854, 480));

		pack();
		setLocationRelativeTo(null);

		try {
			setIconImage(ImageIO.read(LauncherFrame.class
					.getResource("favicon.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(30000L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.exit(0);
					}
				}.start();
				System.exit(0);
			}
		});
	}

	public void login(String userName, String version) {
		try {
			Settings settings = SettingsLoader.load();
			startMinecraft(version, MinecraftUtil.getWorkingDirectory()
					.getAbsolutePath(), userName, settings.session == null ? "12345" : settings.session);
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void showError(String error) {
		removeAll();
		add(this.loginForm);

		this.loginForm.setError(error);
		validate();
	}

	private static void startMinecraft(String version, String root,
			String username, String session) throws FileNotFoundException,
			IOException {
		root = root.endsWith("/") ? root : root + '/';
		String versionDir = new File(root, "versions/" + version)
				.getAbsolutePath() + "/";
		String assetsDir = new File(root, "assets").getAbsolutePath() + "/";

		Settings settings = SettingsLoader.load();
		
		List<String> params = new ArrayList<>();
		params.add("java");
		params.add("-Xincgc");
		params.add("-Xms" + (settings.minRam == 0 ? 512 : settings.minRam) + "m");
		params.add("-Xmx" + (settings.maxRam == 0 ? 1024 : settings.maxRam) + "m");
		params.add("-Djava.library.path=\"" + versionDir + "natives\"");

		JsonParser parser = new JsonParser();
		JsonObject elem = parser.parse(
				new InputStreamReader(new FileInputStream(versionDir + version
						+ ".json"))).getAsJsonObject();
		JsonArray libraries = elem.get("libraries").getAsJsonArray();

		params.add("-cp");
		StringBuilder path = new StringBuilder();
		for (JsonElement lib : libraries) {
			String[] vars = lib.getAsJsonObject().get("name").getAsString()
					.split(":");
			String libPath = root + "libraries/"
					+ vars[0].replaceAll("\\.", "/") + "/" + vars[1] + "/"
					+ vars[2] + "/" + vars[1] + "-" + vars[2] + ".jar";
			path.append(libPath + ";");
			JsonElement natives = lib.getAsJsonObject().get("natives");
			if (natives != null) {
				String os = "windows";
				if (MinecraftUtil.getPlatform() == OS.windows) {
					os = "windows";
				} else if (MinecraftUtil.getPlatform() == OS.macos) {
					os = "osx";
				} else if (MinecraftUtil.getPlatform() == OS.linux) {
					os = "linux";
				}

				File nativesZip = new File(root + "libraries/"
						+ vars[0].replaceAll("\\.", "/") + "/" + vars[1] + "/"
						+ vars[2] + "/" + vars[1] + "-" + vars[2] + "-"
						+ "natives-" + os + ".jar");
				Zipper.unzipFolder(nativesZip, new File(versionDir, "natives"));
			}

		}
		path.append(versionDir + version + ".jar");
		params.add("\"" + path.toString() + "\"");

		params.add(elem.get("mainClass").getAsString());
		params.add(elem.get("minecraftArguments").getAsString()
				.replace("${auth_player_name}", username)
				.replace("${auth_session}", session)
				.replace("${version_name}", version)
				.replace("${game_directory}", root)
				.replace("${game_assets}", assetsDir)
				.replace("${auth_uuid}", "1")
				.replace("${auth_access_token}", "1")
				.replace("${auth_uuid}", session));

		StringBuilder sb = new StringBuilder();
		for (String s : params) {
			System.out.print(s + " ");
			sb.append(s + " ");
		}
		Runtime.getRuntime().exec(sb.toString());
	}

	public static void main(String[] args) throws Exception {
		LauncherFrame launcherFrame = new LauncherFrame();
		if (args.length >= 2)
			launcherFrame.login(args[0], args[1]);
		launcherFrame.setVisible(true);
	}
}