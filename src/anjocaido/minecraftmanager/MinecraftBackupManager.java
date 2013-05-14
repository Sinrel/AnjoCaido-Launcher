package anjocaido.minecraftmanager;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.minecraft.MinecraftUtil;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import java.awt.SystemColor;

public class MinecraftBackupManager extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final NimbusLookAndFeel feel = new NimbusLookAndFeel();
	
	private JButton backupgame;
	private JCheckBox fullgamebackup;
	
	private JButton jButton1;
	private JButton jButton2;
	private JButton jButton5;
	
	private JLabel jLabel1;
	private JLabel jLabel12;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JLabel jLabel7;
	private JLabel jLabel8;
	private JPanel jPanel1;
	private JPanel jPanel2;
	
	private JSeparator jSeparator1;
	private JTabbedPane jTabbedPane1;
	private JCheckBox saveuninstall;
	private JButton uninstall;
	
	private JComboBox< String > worldBox;

  public MinecraftBackupManager() {
  	getContentPane().setForeground(SystemColor.control);
	  try
    {
      UIManager.setLookAndFeel(feel);
    }catch (UnsupportedLookAndFeelException ex) {
    }
     initComponents();
     refreshButtons();
  }

  private void initComponents() {
	  this.jTabbedPane1 = new JTabbedPane();
	  
	  this.jPanel2 = new JPanel();
	  
	  this.uninstall = new JButton();
	  this.saveuninstall = new JCheckBox();
	  this.backupgame = new JButton();
	  this.jButton5 = new JButton();
	  
	  this.jLabel1 = new JLabel();
	  this.jLabel2 = new JLabel();
	  this.jLabel7 = new JLabel();
	  this.jLabel8 = new JLabel();
	  this.jLabel12 = new JLabel();
	  
	  this.fullgamebackup = new JCheckBox();
	  this.jSeparator1 = new JSeparator();
	  
	  setDefaultCloseOperation(2);
	  setTitle("Менеджер восстановления Minecraft");

	  this.uninstall.setText("Удалить игру");
	  this.uninstall.setEnabled(false);
	  this.uninstall.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent evt) {
			  MinecraftBackupManager.this.uninstallActionPerformed(evt);
		  }
	  });
	  this.saveuninstall.setText("Полное удаление (Включая сохранения)");
	  this.saveuninstall.setEnabled(false);

	  this.backupgame.setText("Резервная копия (фаилы игры)");
	  this.backupgame.setEnabled(false);
	  this.backupgame.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent evt) {
			  MinecraftBackupManager.this.backupgameActionPerformed(evt);
		  }
	  });
	  this.jButton5.setText("Восстановить игру");
	  this.jButton5.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent evt) {
			  MinecraftBackupManager.this.jButton5ActionPerformed(evt);
		  }
	  });
	  
	  this.jLabel1.setText("Возможно, вы захотите сделать резервную копию всей игры сразу же после её установки.");
	  this.jLabel2.setText("Никогда не знаешь когда выйдет обновление и сломает всё.");
	  this.jLabel7.setText("Опять Zip формат, но расширение .mcgame");

	  this.fullgamebackup.setText("Внутренние папки (saves и другие)");
	  this.fullgamebackup.setEnabled(false);
	  this.fullgamebackup.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent evt) {
			  MinecraftBackupManager.this.fullgamebackupActionPerformed(evt);
		  }
	  });
	  
	  this.jPanel1 = new JPanel();
	  jPanel1.setForeground(SystemColor.control);
	  
	  this.jLabel3 = new JLabel();
	  this.jLabel4 = new JLabel();
	  this.jLabel5 = new JLabel();
	  
	  this.jLabel3.setText("Все будет храниться в формате Zip.");
	  this.jLabel4.setText("Но с расширением .mcworld");
	  this.jLabel5.setText("Ты можешь восстановить абсолютно любой мир с любым именем");
	  
	  worldBox = new JComboBox< String >();
	  
	  JLabel lblNewLabel = new JLabel("Мир:");
	  
	  this.jButton1 = new JButton();
	  
	  	  this.jButton1.setText("Сделать резервную копию");
	  	  this.jButton1.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent evt) {
			  MinecraftBackupManager.this.jButton1ActionPerformed(evt);
    	  }
	  	  });
	  	  this.jButton2 = new JButton();
	  	  this.jButton2.setText("Восстановить мир");
	  	  this.jButton2.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent evt) {
			  MinecraftBackupManager.this.jButton2ActionPerformed(evt);
		  }
	  	  });
	  	  	  
	  	  	  JButton btnNewButton = new JButton("Открыть .minecraft");
	  	  	  btnNewButton.addActionListener(new ActionListener() {
	  	  	  	public void actionPerformed(ActionEvent arg0) {
			  	  	  	Desktop desktop = null;
			  	  	if (Desktop.isDesktopSupported()) {
			  	  	    desktop = Desktop.getDesktop();
			  	  	    try {
							desktop.open(MinecraftUtil.getWorkingDirectory());
						} catch (IOException e) {
							e.printStackTrace();
						}
			  	  	}
	  	  	  	}
	  	  	  });
	  	  
	  	  	  GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
	  	  	  jPanel1Layout.setHorizontalGroup(
	  	  	  	jPanel1Layout.createParallelGroup(Alignment.LEADING)
	  	  	  		.addGroup(jPanel1Layout.createSequentialGroup()
	  	  	  			.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
	  	  	  				.addGroup(jPanel1Layout.createSequentialGroup()
	  	  	  					.addGap(25)
	  	  	  					.addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING, false)
	  	  	  						.addGroup(jPanel1Layout.createSequentialGroup()
	  	  	  							.addComponent(lblNewLabel)
	  	  	  							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	  	  	  							.addComponent(worldBox, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
	  	  	  						.addComponent(jButton2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	  	  	  						.addComponent(jButton1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	  	  	  						.addComponent(btnNewButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	  	  	  				.addGroup(jPanel1Layout.createSequentialGroup()
	  	  	  					.addContainerGap()
	  	  	  					.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
	  	  	  						.addComponent(jLabel5)
	  	  	  						.addComponent(jLabel4)
	  	  	  						.addComponent(jLabel3))))
	  	  	  			.addContainerGap(310, Short.MAX_VALUE))
	  	  	  );
	  	  	  jPanel1Layout.setVerticalGroup(
	  	  	  	jPanel1Layout.createParallelGroup(Alignment.TRAILING)
	  	  	  		.addGroup(jPanel1Layout.createSequentialGroup()
	  	  	  			.addContainerGap(8, Short.MAX_VALUE)
	  	  	  			.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
	  	  	  				.addComponent(worldBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	  	  	  				.addComponent(lblNewLabel))
	  	  	  			.addPreferredGap(ComponentPlacement.RELATED)
	  	  	  			.addComponent(jButton2)
	  	  	  			.addPreferredGap(ComponentPlacement.RELATED)
	  	  	  			.addComponent(jButton1)
	  	  	  			.addPreferredGap(ComponentPlacement.RELATED)
	  	  	  			.addComponent(btnNewButton)
	  	  	  			.addGap(9)
	  	  	  			.addComponent(jLabel3)
	  	  	  			.addPreferredGap(ComponentPlacement.RELATED)
	  	  	  			.addComponent(jLabel4)
	  	  	  			.addPreferredGap(ComponentPlacement.RELATED)
	  	  	  			.addComponent(jLabel5)
	  	  	  			.addContainerGap())
	  	  	  );
	  	  	  this.jPanel1.setLayout(jPanel1Layout);
	  	  	  
	  	  	  	  this.jTabbedPane1.addTab("Игровые миры", this.jPanel1);
	  
	  GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
	  this.jPanel2.setLayout(jPanel2Layout);
	  jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGap(6, 6, 6).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel2).addComponent(this.jLabel7).addComponent(this.jLabel1))).addComponent(this.jSeparator1, -1, 551, 32767).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.uninstall).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.saveuninstall)).addGroup(jPanel2Layout.createSequentialGroup().addComponent(this.backupgame).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.fullgamebackup)).addComponent(this.jButton5, -2, 239, -2)).addContainerGap()));
	  jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.uninstall).addComponent(this.saveuninstall)).addGap(3, 3, 3).addComponent(this.jSeparator1, -2, 10, -2).addGap(18, 18, 18).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.backupgame).addComponent(this.fullgamebackup)).addGap(18, 18, 18).addComponent(this.jButton5).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel7).addContainerGap(-1, 32767)));
	  
	  this.jTabbedPane1.addTab("Установка игры", this.jPanel2);
	  this.jLabel8.setText("by AnjoCaido and Sinrel group");
	  this.jLabel12.setText("Создание резервной копии и восстановление мира занимает некоторое время");

	  GroupLayout layout = new GroupLayout(getContentPane());
	  getContentPane().setLayout(layout);
	  layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jTabbedPane1, GroupLayout.Alignment.LEADING, -1, 563, 32767).addGroup(layout.createSequentialGroup().addComponent(this.jLabel12).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 137, 32767).addComponent(this.jLabel8))).addContainerGap()));
	  layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(this.jTabbedPane1, -1, 234, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel8).addComponent(this.jLabel12)).addContainerGap()));
	  pack();
  }

  private void uninstallActionPerformed(ActionEvent evt) {
	  int result = JOptionPane.showConfirmDialog(this, "Вы уверены?", "Are you sure? (Uninstallation)", 0, 2);
	  if ((result == 1) || (result == -1)) {
		  return;
	  }
	  BackupUtil.uninstallGame(this.saveuninstall.isSelected());
	  JOptionPane.showMessageDialog(this, "Готово!", "Удаление", -1);
	  refreshButtons();
  }

  private void backupgameActionPerformed(ActionEvent evt) {
	  JFileChooser save = new JFileChooser();
	  Calendar now = GregorianCalendar.getInstance();
	  save.setSelectedFile(new File(String.format("MCGame_" + (this.fullgamebackup.isSelected() ? "Complete_" : "") + "%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS" + "_Backup." + "mcgame", new Object[] { now })));
	  save.setFileSelectionMode(0);
	  save.setFileFilter(new BackupUtil.GameFileFilter());
	  int result = save.showSaveDialog(this);
	  if (result != 0) {
		  return;
      }
	  File f = save.getSelectedFile();
	  if (f == null) {
		  return;
	  }
	  BackupUtil.backupGame(f, this.fullgamebackup.isSelected());
	  JOptionPane.showMessageDialog(this, "Готово!", "Резервная копия игры", -1);
  }

  private void jButton5ActionPerformed(ActionEvent evt) {
	  int result = JOptionPane.showConfirmDialog(this, "Вы уверены что хотите восстановить весь Minecraft?\nIT Это перезапишет все ваши данные\n(если вы восстанавливаете из полного бэкапа то вы потеряете и ваши миры тоже)\nУбедись что сохранили все важные данные игры!", "Вы уверенны?  (Полное восстоновление игры)", 0, 2);
	  
	  if ((result == 1) || (result == -1)) {
		  return;
	  }
	  
	  JFileChooser save = new JFileChooser();
	  save.setFileSelectionMode(0);
	  save.setFileFilter(new BackupUtil.GameFileFilter());
	  save.showOpenDialog(this);
	  File f = save.getSelectedFile();
	  
	  if ((f == null) || (!f.exists())){
		  return;
	  }
	  
	  try {
		  BackupUtil.restoreGame(f);
	  } catch (IllegalStateException ex) {
		  JOptionPane.showMessageDialog(this, "Ошибка!\nНеверное содержание архива!\nПапка внутри архива должна иметь имя 'minecraft_backup'.", "Восстановление игры", 0);
		  return;
	  }
	 
	  JOptionPane.showMessageDialog(this, "Готово!", "Восстановление игры", -1);
	  refreshButtons();
  }

  private void jButton1ActionPerformed(ActionEvent evt) {
	  int world = getWorldSelected();
	  File worldFolder = BackupUtil.getWorlds().get(world);
	  
	  if (world < 0) {
		  JOptionPane.showMessageDialog(this, "Сначала выберите мир для сохранения!", "Резервная копия мира", 0);
		  return;
	  }
	  
	  if (!worldFolder.exists()) {
		  JOptionPane.showMessageDialog(this, "Извините, но такого мира не существует!", "Резервная копия мира", 0);
		  return;
	  }
	  
	  JFileChooser save = new JFileChooser();
	  GregorianCalendar.getInstance();
	  save.setFileSelectionMode(0);
	  save.setSelectedFile(new File(worldFolder.getName() + ".mcworld"));
	  save.setFileFilter(new BackupUtil.WorldFileFilter());
	  
	  int result = save.showSaveDialog(this);
	  if (result != 0) {
		  return;
	  }
	  
	  File f = save.getSelectedFile();
	  
	  if (f == null) {
		  return;
      }
	  
	  BackupUtil.backupWorld(worldFolder, f);
	  JOptionPane.showMessageDialog(this, "Готово!", "Резервная копия игры", -1);
  }

  @SuppressWarnings( "null" )
private void jButton2ActionPerformed(ActionEvent evt) {  
	  JFileChooser save = new JFileChooser();
	  save.setFileSelectionMode(0);
	  save.setFileFilter(new BackupUtil.WorldFileFilter());
	  save.showOpenDialog(this);
	  
	  File f = save.getSelectedFile();
	  if (f == null){
		  return;
	  }
	  
	  Boolean accepted = true;
	  String name = f.getName();
	  String worldName = JOptionPane.showInputDialog("Введите имя мира",name.substring(0, name.indexOf('.')));
	  if(worldName != null || !worldName.trim().equals("")){
		  File world = new File(MinecraftUtil.getSavesFolder(), worldName);
		  if(world.exists())
		  {
			  accepted = JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this, "Такой мир уже существует, переписать?");
		  }
		  if(accepted){
			  try {
				  BackupUtil.restoreWorld(world, f);
			  } catch (IllegalStateException ex) {
				  JOptionPane.showMessageDialog(this, "Ошибка!\nНеверное содержание архива\nПапка с миром должна называться 'world_backup' ", "Восстановление мира", 0);
				  return;
			  }
			  JOptionPane.showMessageDialog(this, "Готово!", "Восстановление мира", -1);
			  refreshButtons();
		  }
	  }
  }

  private void fullgamebackupActionPerformed(ActionEvent evt) {
	  if (this.fullgamebackup.isSelected()){
		  this.backupgame.setText("Резервная копия (полная)");
	  }else{
		  this.backupgame.setText("Резервная копия (фаилы игры)");
	  }
  }

  public int getWorldSelected() {
	  return worldBox.getSelectedIndex();
  }

  public static void main(String[] args) {
	  EventQueue.invokeLater(new Runnable() {
		  public void run() {
			  new MinecraftBackupManager().setVisible(true);
		  }
	  });
  }

  private void refreshButtons() {
	  worldBox.removeAllItems();
	  if(MinecraftUtil.getSavesFolder().exists())
		  for(File f: BackupUtil.getWorlds()) worldBox.addItem(f.toPath().getFileName().toString());
	  
	  if ((MinecraftUtil.getBinFolder().exists()) || (MinecraftUtil.getLoginFile().exists()) || (MinecraftUtil.getOptionsFile().exists()) || (MinecraftUtil.getResourcesFolder().exists()) || (MinecraftUtil.getSavesFolder().exists()))
		  this.uninstall.setEnabled(true);
	  else {
		  this.uninstall.setEnabled(false);
	  }

	  if (MinecraftUtil.getSavesFolder().exists())
		  this.saveuninstall.setEnabled(true);
	  else {
		  this.saveuninstall.setEnabled(false);
	  }
	  
	  if (MinecraftUtil.getBinFolder().exists()) {
		  this.backupgame.setEnabled(true);
		  this.fullgamebackup.setEnabled(true);
      }else{
    	  this.backupgame.setEnabled(false);
    	  this.fullgamebackup.setEnabled(false);
      }
  }
}