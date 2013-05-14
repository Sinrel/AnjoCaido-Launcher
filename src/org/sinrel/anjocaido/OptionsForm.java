package org.sinrel.anjocaido;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Label;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.JLabel;

import net.minecraft.MinecraftUtil;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import anjocaido.minecraftmanager.MinecraftBackupManager;

public class OptionsForm extends JFrame {

	private static final long	serialVersionUID	= -5463659953417497059L;
	
	private JPanel contentPane;
	private JTextField serverField;
	private JTextField portField;
	private JPanel panel;
	private JRadioButton useAutoConnectRadioButton;
	
	public static final String SERVER_OPTION = "sinrel-server";
	public static final String PORT_OPTION = "sinrel-port";
	public static final String UPDATE_OPTION= "";
	
	private Label updateLabel;
	private JTextField updateField;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame parent = new JFrame(); 
					parent.setBounds(0, 0, 1440, 900);
					OptionsForm frame = new OptionsForm(parent);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void saveOptions()
	{
		try {
			Options options = MinecraftUtil.getOptions();
			if(useAutoConnectRadioButton.isSelected()){
				if(serverField.getText().trim() != "") options.setOption(SERVER_OPTION, serverField.getText());
				else options.setOption(SERVER_OPTION, "nothing");
				if(portField.getText().trim() != "") options.setOption(PORT_OPTION, portField.getText());
				else options.setOption(PORT_OPTION, "nothing");
			}else
			{
				options.setOption(SERVER_OPTION, "nothing");
				options.setOption(PORT_OPTION, "nothing");
			}
			options.setOption(UPDATE_OPTION, updateField.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setEnablePanel(Boolean value)
	{
		panel.setEnabled(value);
		for(Component c : panel.getComponents()) c.setEnabled(value);
	}
	
	/**
	 * Create the frame.
	 */
	public OptionsForm(Frame parent) {
		try {
			UIManager.setLookAndFeel(MinecraftBackupManager.feel);
		} catch (UnsupportedLookAndFeelException e2) {
			e2.printStackTrace();
		}
		Options options = MinecraftUtil.getOptions();
		Boolean enable = true;
		String server = null;
		String port = null;
		try {
			server = options.getOption(SERVER_OPTION);
			System.out.println(server);
		    port = options.getOption(PORT_OPTION);
			if (server == null || server.equals("nothing")) enable = false;
			else enable = true;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		setResizable(false);
		setBounds(parent.getBounds().x + parent.getBounds().width / 2,
				parent.getBounds().y + parent.getBounds().height / 2, 
				256,
				284);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton saveButton = new JButton("Сохранить");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveOptions();
				try {
					OptionsForm.this.setVisible(false);
					OptionsForm.this.dispose();
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		});
		saveButton.setBounds(113, 213, 127, 32);
		contentPane.add(saveButton);
		
		useAutoConnectRadioButton = new JRadioButton("Автоподключение к серверу");
		useAutoConnectRadioButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				setEnablePanel(useAutoConnectRadioButton.isSelected());
			}
		});
		useAutoConnectRadioButton.setBounds(10, 6, 197, 23);
		
		contentPane.add(useAutoConnectRadioButton);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 36, 230, 87);
		contentPane.add(panel);
		panel.setLayout(null);
		panel.setEnabled(enable);
		
		serverField = new JTextField();
		serverField.setBounds(74, 11, 146, 26);
		panel.add(serverField);
		serverField.setColumns(10);
		serverField.setEnabled(enable);
		if(enable) serverField.setText(server);
		
		JLabel serverLabel = new JLabel("Сервер:");
		serverLabel.setBounds(22, 16, 54, 14);
		serverLabel.setEnabled(enable);
		panel.add(serverLabel);
		
		portField = new JTextField();
		portField.setBounds(134, 47, 86, 26);
		panel.add(portField);
		portField.setColumns(10);
		portField.setEnabled(enable);
		if(enable) portField.setText(port);
		
		JLabel portLabel = new JLabel("порт:");
		portLabel.setBounds(97, 52, 46, 14);
		portLabel.setEnabled(enable);
		panel.add(portLabel);
		useAutoConnectRadioButton.setSelected(enable);
		
		updateLabel = new Label("Сервер обновлений:");
		updateLabel.setBounds(10, 134, 230, 23);
		contentPane.add(updateLabel);
		
		updateField = new JTextField();
		updateField.setBounds(10, 177, 230, 26);
		contentPane.add(updateField);
		updateField.setColumns(10);
		try {
			updateField.setText(MinecraftUtil.getOptions().getOption(OptionsForm.UPDATE_OPTION));
			
			Label label = new Label("(не рекомендуется изменять)");
			label.setBounds(10, 151, 230, 23);
			contentPane.add(label);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
