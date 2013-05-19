package net.minecraft;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class Launcher extends Applet implements Runnable, AppletStub {
	
	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> customParameters = new HashMap();
	
	private GameUpdater gameUpdater;
	private boolean gameUpdaterStarted = false;
	private Applet applet;
	private Image bgImage;
	private boolean active = false;
	private int context = 0;
	private VolatileImage img;
	
	public boolean forceUpdate = false;

	public boolean isActive() {
		if (this.context == 0) {
			this.context = -1;
			try {
				if (getAppletContext() != null)
					this.context = 1;
		    }catch (Exception localException) {}
	    }
		
		if (this.context == -1) {
			return this.active;
	    }
		return super.isActive();
    }

  public void init(String userName, String sessionId) {
	  try {
		  this.bgImage = ImageIO.read(LoginForm.class.getResource("dirt.png")).getScaledInstance(32, 32, 16);
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	  
	  this.customParameters.put("username", userName);
	  this.customParameters.put("sessionid", sessionId);

	  this.gameUpdater = new GameUpdater();
	  this.gameUpdater.forceUpdate = this.forceUpdate;
  }

  public boolean canPlayOffline() {
	  return this.gameUpdater.canPlayOffline();
  }

  public void init() {
	  if (this.applet != null) {
		  this.applet.init();
		  return;
	  }
	  init(getParameter("userName"), getParameter("sessionId"));
  }

  public void start() {
	  if (applet != null) {
		  applet.start();
		  return;
	  }
	  
	  if (gameUpdaterStarted) return;
	  
	  Thread t = new Thread() {
		  public void run() {
			  gameUpdater.run();
			  try {
				  if (!gameUpdater.fatalError)
					  replace(gameUpdater.createApplet());
			  }catch (ClassNotFoundException e) {
				  e.printStackTrace();
			  } catch (InstantiationException e) {
				  e.printStackTrace();
			  } catch (IllegalAccessException e) {
				  e.printStackTrace();
			  }
		  }
	  };
	  
	  t.setDaemon(true);
	  t.start();

	  t = new Thread() {
		  public void run() {
			  while (applet == null) {
				  repaint();
				  try {
					  Thread.sleep(10L);
				  } catch (InterruptedException e) {
					  e.printStackTrace();
				  }
			  }
		  }
	  };
	  
	  t.setDaemon(true);
	  t.start();

	  gameUpdaterStarted = true;
  }

  public void stop() {
	  if (this.applet != null) {
		  this.active = false;
		  this.applet.stop();
		  return;
	  }
  }

  public void destroy() {
	  if (this.applet != null) {
		  this.applet.destroy();
		  return;
	  }
  }

  public void replace(Applet applet) {
	  this.applet = applet;
	  
	  applet.setStub(this);
	  applet.setSize(getWidth(), getHeight());

	  setLayout(new BorderLayout());
	  add(applet, "Center");

	  applet.init();
	  
	  this.active = true;
	  
	  applet.start();
	  validate();
  }

  public void update(Graphics g) {
	  paint(g);
  }

  public void paint(Graphics g2) {
	  if (this.applet != null) {
		  return;
      }
	  
	  int w = getWidth() / 2;
	  int h = getHeight() / 2;
	  
	  if ((this.img == null) || (this.img.getWidth() != w) || (this.img.getHeight() != h)) {
		  this.img = createVolatileImage(w, h);
	  }

	  Graphics g = this.img.getGraphics();
	  
	  for (int x = 0; x <= w / 32; x++) {
		  for (int y = 0; y <= h / 32; y++) {
			  g.drawImage(this.bgImage, x * 32, y * 32, null);
		  }
      }
	  
	  g.setColor(Color.LIGHT_GRAY);

	  String msg = "Обновление MineCraft";
	  
	  if (this.gameUpdater.fatalError) {
		  msg = "Failed to launch";
	  }

	  g.setFont(new Font(null, 1, 20));
	  
	  FontMetrics fm = g.getFontMetrics();
	  
	  g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2, h / 2 - fm.getHeight() * 2);
	  g.setFont(new Font(null, 0, 12));
	  fm = g.getFontMetrics();
	  msg = this.gameUpdater.getDescriptionForState();
	  
	  if (this.gameUpdater.fatalError) {
		  msg = this.gameUpdater.fatalErrorDescription;
	  }

	  g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2, h / 2 + fm.getHeight() * 1);
	  msg = this.gameUpdater.subtaskMessage;
	  g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2, h / 2 + fm.getHeight() * 2);

	  if (!this.gameUpdater.fatalError) {
		  g.setColor(Color.black);
		  g.fillRect(64, h - 64, w - 128 + 1, 5);
		  g.setColor(new Color(32768));
		  g.fillRect(64, h - 64, this.gameUpdater.percentage * (w - 128) / 100, 4);
		  g.setColor(new Color(2138144));
		  g.fillRect(65, h - 64 + 1, this.gameUpdater.percentage * (w - 128) / 100 - 2, 1);
	  }
	  g.dispose();

	  g2.drawImage(this.img, 0, 0, w * 2, h * 2, null);
  }

  public void run() {}

  public String getParameter(String name) {
	  String custom = (String)this.customParameters.get(name);
	  
	  if (custom != null)
		  return custom;
	  try {
		  return super.getParameter(name);
	  } catch (Exception e) {
		  this.customParameters.put(name, null);
	  }
	  return null;
  }

  public void appletResize(int width, int height) {}

  public URL getDocumentBase() {
	  try {
		  return new URL("http://www.minecraft.net/game/");
	  } catch (MalformedURLException e) {
		  e.printStackTrace();
      }
	  return null;
  }
}