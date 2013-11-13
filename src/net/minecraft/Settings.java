package net.minecraft;

import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Settings extends JFrame{
public static JButton buttonexit = new JButton("Выход");
public static JButton buttonsave = new JButton("Сохранить");
public static Choice langChoice = new Choice();
public static JLabel labelChangeLang = new JLabel("Язык:");
public static String Title = "Настройки лаунера";
public Settings() {
	setTitle(Title);
	setLocationRelativeTo(null);
	setResizable(false);
	setSize(300,350);
	setVisible(true);
	setLocationRelativeTo(null);
	getContentPane().add(panel());
	

	}
	

public static void main(String args[]) 
{
	new Settings();
}





private JPanel panel() 
{
	JPanel panel = new JPanel();
	panel.setLayout(null);
	buttonexit.setBounds(175, 270, 100, 30);
	buttonexit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			setVisible(false);
		}
	});
	buttonsave.setBounds(20, 270, 100, 30);
	buttonsave.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			try {
				File settingsfile = new File(MinecraftUtil.getWorkingDirectory(),"settings.txt");
				if(!settingsfile.exists())
				{
					settingsfile.createNewFile();
				}
				FileWriter writer = new FileWriter(settingsfile);
				switch(langChoice.getSelectedItem())
				{
				case "Russian": writer.write("0"); break;
				case "English": writer.write("1"); break;
				}
				writer.close();
				LoginForm.lf.loadLangLauncher();
				setVisible(false);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	});
	labelChangeLang.setBounds(40, 25, 40, 50);
	langChoice.setBounds(100, 40, 80, 60);
	panel.add(labelChangeLang);
	panel.add(langChoice);
	panel.add(buttonsave);
    panel.add(buttonexit);
	return panel;
}
}
