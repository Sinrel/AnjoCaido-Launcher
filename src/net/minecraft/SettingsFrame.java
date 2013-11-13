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
import java.io.Serializable;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.FlowLayout;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class SettingsFrame extends JFrame implements Serializable {
	public static JButton buttonexit = new JButton("Выход");
	public static JButton buttonsave = new JButton("Сохранить");
	public static Choice langChoice = new Choice();
	public static JLabel labelChangeLang = new JLabel("Язык:");
	public static String Title = "Настройки лаунера";
	public static String memory = "Память";
	public static String min = "от:";
	public static String max = "до:";
	public static String session = "Сессия";
	private JPanel langPanel;
	private JSpinner minMemorySpinner;
	private JSpinner maxMemorySpinner;
	private JTextField sessionField;

	public SettingsFrame() {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setTitle(Title);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(319, 221);
		setVisible(true);
		setLocationRelativeTo(null);
		getContentPane().add(panel());
	}

	private JPanel panel() {
		JPanel panel = new JPanel();
		buttonexit.setBounds(184, 151, 100, 30);
		buttonexit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
			}
		});
		buttonsave.setBounds(10, 151, 100, 30);
		buttonsave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					Settings settings = SettingsLoader.load();
					switch (langChoice.getSelectedItem()) {
					case "Russian":
						settings.lang = "ru_RU";
						break;
					case "English":
						settings.lang = "en_US";
						break;
					}
					settings.maxRam = (Integer) maxMemorySpinner.getValue();
					settings.minRam = (Integer) minMemorySpinner.getValue();
					if(sessionField.getText().isEmpty())
						settings.session = "12345";
					else
						settings.session = sessionField.getText();
					SettingsLoader.save(settings);
					LoginForm.lf.loadLangLauncher();
					setVisible(false);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		panel.setLayout(null);
		panel.add(buttonsave);
		panel.add(buttonexit);

		langPanel = new JPanel();
		langPanel.setBounds(10, 11, 293, 41);
		FlowLayout fl_langPanel = (FlowLayout) langPanel.getLayout();
		fl_langPanel.setAlignment(FlowLayout.LEFT);
		panel.add(langPanel);
		langPanel.add(labelChangeLang);
		langPanel.add(langChoice);

		JPanel memPanel = new JPanel();
		FlowLayout fl_memPanel = (FlowLayout) memPanel.getLayout();
		fl_memPanel.setAlignment(FlowLayout.LEFT);
		memPanel.setBounds(10, 52, 293, 41);
		panel.add(memPanel);

		JLabel memLabel = new JLabel(memory);
		memPanel.add(memLabel);

		JLabel minLabel = new JLabel(min);
		memPanel.add(minLabel);

		Settings st = new Settings();
		try {
			st = SettingsLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(st.minRam);
		minMemorySpinner = new JSpinner();
		minMemorySpinner.setModel(new SpinnerNumberModel(st.minRam == 0 ? 512 : st.minRam, 256, 2048, 256));
		memPanel.add(minMemorySpinner);

		JLabel maxLabel = new JLabel(max);
		memPanel.add(maxLabel);

		maxMemorySpinner = new JSpinner();
		maxMemorySpinner.setModel(new SpinnerNumberModel(st.maxRam == 0 ? 1024 : st.maxRam, 256, 2048, 256));
		memPanel.add(maxMemorySpinner);
		
		JPanel sessionPanel = new JPanel();
		FlowLayout fl_sessionPanel = (FlowLayout) sessionPanel.getLayout();
		fl_sessionPanel.setAlignment(FlowLayout.LEFT);
		sessionPanel.setBounds(10, 94, 293, 45);
		panel.add(sessionPanel);
		
		JLabel sessionLabel = new JLabel(session);
		sessionPanel.add(sessionLabel);
		
		sessionField = new JTextField();
		sessionField.setText("12345");
		
		if(st.session != null && !st.session.isEmpty()){
			sessionField.setText(st.session);
		}
		
		sessionPanel.add(sessionField);
		sessionField.setColumns(10);
		return panel;
	}
}
