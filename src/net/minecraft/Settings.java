package net.minecraft;

import java.io.Serializable;

class Settings implements Serializable{
	public String lang = "ru_RU";
	public int minRam = 512;
	public int maxRam = 1024;
	public String session = "12345";
	private static final long serialVersionUID = -2876145322404487395L;
}
