package net.minecraft;

import java.util.ArrayList;

public class MinecraftLauncher {

	public static final String version = "5.0u";

	public static void main(String[] args) throws Exception {
		float heapSizeMegs = (float) (Runtime.getRuntime().maxMemory() / 1024L / 1024L);

		if (heapSizeMegs > 511.0F)
			LauncherFrame.main(args);
		else
			try {
				String pathToJar = MinecraftLauncher.class
						.getProtectionDomain().getCodeSource().getLocation()
						.toURI().getPath();

				ArrayList<String> params = new ArrayList<String>();

				params.add("javaw");
				params.add("-Xmx1024m");
				params.add("-Dsun.java2d.noddraw=true");
				params.add("-Dsun.java2d.d3d=false");
				params.add("-Dsun.java2d.opengl=false");
				params.add("-Dsun.java2d.pmoffscreen=false");
				params.add("-Xnoclassgc");
				params.add("-XX:+AggressiveOpts");
				params.add("-Xincgc");
				params.add("-classpath");
				params.add(pathToJar);
				params.add("net.minecraft.LauncherFrame");
				for (String arg : args)
					params.add(arg);

				ProcessBuilder pb = new ProcessBuilder(params);
				Process process = pb.start();

				if (process == null)
					throw new Exception("!");
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				LauncherFrame.main(args);
			}
	}
}