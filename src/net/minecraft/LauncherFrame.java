 package net.minecraft;
 
 import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.sinrel.anjocaido.Options;
import org.sinrel.anjocaido.OptionsForm;
 
public class LauncherFrame extends Frame {
	
	public static final int VERSION = 12;
	private static final long serialVersionUID = 1L;
	
	private Launcher launcher;
	private LoginForm loginForm;
	
	public boolean forceUpdate = false;
 
	public LauncherFrame() {
		super("Minecraft Launcher" + " v"+MinecraftLauncher.version );	
		setBackground(Color.BLACK);
		
		this.loginForm = new LoginForm(this);
		
		setLayout(new BorderLayout());
		add(this.loginForm, "Center");
		
		this.loginForm.setPreferredSize(new Dimension(854, 480));
		
		pack();		
		setLocationRelativeTo(null);
		
		try {
			setIconImage(ImageIO.read(LauncherFrame.class.getResource("favicon.png")));
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
			    
			    if (LauncherFrame.this.launcher != null) {
			    	LauncherFrame.this.launcher.stop();
			    	LauncherFrame.this.launcher.destroy();
			    }
			    System.exit(0);
			}
	    });
    }
	
	public String getFakeResult(String userName) {
		return MinecraftUtil.getFakeLatestVersion() + ":35b9fd01865fda9d70b157e244cf801c:" + userName + ":12345:";
    }
 
	public void login(String userName,String server,String port){
		String result = getFakeResult(userName);
		String[] values = result.split(":");
		
		this.launcher = new Launcher();
		this.launcher.forceUpdate = this.forceUpdate;
		this.launcher.customParameters.put("userName", values[2].trim());
		this.launcher.customParameters.put("sessionId", values[3].trim());
		this.launcher.customParameters.put("stand-alone", "true");
		
		if(!server.equalsIgnoreCase("null")){
			this.launcher.customParameters.put("server", server);
			this.launcher.customParameters.put("port", port);
		}
		this.launcher.init();
		
		removeAll();
		add(this.launcher, "Center");
		
		validate();
		
		this.launcher.start();
		this.loginForm.loginOk();
		this.loginForm = null;
		
		setTitle("Minecraft");
   }
 
	@SuppressWarnings("unused")
	private void showError(String error) {
		removeAll();
		add(this.loginForm);
		
		this.loginForm.setError(error);
		validate();
    }
 
	public boolean canPlayOffline(String userName) {
		Launcher launcher2 = new Launcher();
		launcher2.init(userName, "12345");
		return launcher2.canPlayOffline();
    }
 
	public static void main(String[] args) throws Exception {
		Options options = MinecraftUtil.getOptions();
		if(options.getOption(OptionsForm.UPDATE_OPTION) == null) options.setOption(OptionsForm.UPDATE_OPTION, "http://s3.amazonaws.com/MinecraftDownload/");
		
		LauncherFrame launcherFrame = new LauncherFrame();
		if(args.length == 2){
			String port = "25565";
			String server = null;
			if(args[1].split(":").length > 0){
				server = args[1].split(":")[0];
				port = args[1].split(":")[1];
			}
			else server = args[1];
			launcherFrame.login(args[0], server, port);
		}
		else if(args.length == 1)
		{
			launcherFrame.login(args[0], "null", "null");
		}
		launcherFrame.setVisible(true);
    }
 }