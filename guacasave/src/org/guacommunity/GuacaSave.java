package org.guacommunity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GuacaSave {
	
	private static JFrame mainWindow;
	private static Settings settings;
	
	/**
	 * Initializes and launches the application.
	 */
	public static void main(String[] args) {
		final JPanel contentPane;
		final JButton saveButton, loadButton;
		final JFileChooser loadChooser;
		final CopyOption[] copyOptions;
		
		// Set the application's frame (main window)
		mainWindow = new JFrame(Assets.APP_NAME);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setResizable(false);
		
		// Set the frame's content pane
		contentPane = new JPanel();
		mainWindow.setContentPane(contentPane);
		
		// Create settings
		settings = new Settings(mainWindow);
		
		// Set the frame's menu bar
		createMenuBar();
		
		// Set file copy options to overwrite and copy attributes
		copyOptions = new CopyOption[] {
			      StandardCopyOption.REPLACE_EXISTING,
			      StandardCopyOption.COPY_ATTRIBUTES
	    };
		
		// Set the save button
		saveButton = new JButton("Save...");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (Files.notExists(settings.getSaveFilePath())) {
					JOptionPane.showMessageDialog(
						mainWindow,
						"Save file not found.\n"
						+ "Confirm that the save file specified in\n"
						+ "File > Settings is correct.",
						"Save File Not Found",
						JOptionPane.ERROR_MESSAGE);
				} else {
					String backupName = JOptionPane.showInputDialog(mainWindow, "Backup save file name: ");
					
					if (backupName != null) {
						if (backupName.length() == 0) {
							JOptionPane.showMessageDialog(
								mainWindow,
								"Please specify a backup save file name.",
								"Invalid Backup Save Name",
								JOptionPane.ERROR_MESSAGE);
						} else {
							if (!backupName.endsWith(".dat")) {
								backupName = backupName + ".dat";
							}
								
							if (Files.notExists(settings.getBackupDirectoryPath())) {
								JOptionPane.showMessageDialog(
									mainWindow,
									"Backup directory does not exist.\nSpecify a valid backup directory in File > Settings.",
									"Invalid Backup Directory",
									JOptionPane.ERROR_MESSAGE);
							} else {
								Path fullBackupPath = settings.getBackupDirectoryPath().resolve(backupName);
								boolean saveBackup = true;
								if (Files.exists(fullBackupPath)) {
									int returnVal = JOptionPane.showConfirmDialog(
										mainWindow,
										"Backup save file '" + backupName + "' already exists.\nOverwrite?",
										"Overwrite?",
										JOptionPane.YES_NO_OPTION);
									
									saveBackup = (returnVal == JOptionPane.YES_OPTION);
								}
								
								if (saveBackup) {
									try {
										Files.copy(settings.getSaveFilePath(), fullBackupPath, copyOptions);
									} catch (IOException ioe) {
										JOptionPane.showMessageDialog(
											mainWindow,
											"Save backup failed unexpectedly:\n"
											+ ioe.getMessage(),
											"Save Backup Failed",
											JOptionPane.ERROR_MESSAGE);
									}
								}
							}
						}
					}
				}				
			}
		});
		contentPane.add(saveButton);
		
		// Set the load button
		loadChooser = new JFileChooser();
		loadChooser.setFileFilter(new FileNameExtensionFilter("DAT file", "dat"));
		loadChooser.setFileHidingEnabled(false);
		loadButton = new JButton("Load...");
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File backupDirectory = settings.getBackupDirectoryPath().toFile();
				loadChooser.setCurrentDirectory(backupDirectory);
				int returnVal = loadChooser.showOpenDialog(mainWindow);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						Files.copy(loadChooser.getSelectedFile().toPath(), settings.getSaveFilePath(), copyOptions);
					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(
							mainWindow,
							"Load backup failed unexpectedly:\n"
							+ ioe.getMessage(),
							"Load Backup Failed",
							JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
		});
		contentPane.add(loadButton);
		
		// Display the frame
		mainWindow.pack();
		mainWindow.setVisible(true);
	}
	
	/**
	 * Creates and assigns a menu bar to the main window.
	 */
	private static void createMenuBar() {
		JMenuBar menuBar;
		JMenu fileMenu, helpMenu;
		JMenuItem settingsItem, exitItem, helpItem, aboutItem;
		
		menuBar = new JMenuBar();
		
		// Create the file menu and its items
		fileMenu = new JMenu("File");
		
		settingsItem = new JMenuItem("Settings");
		settingsItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settings.show();
			}
			
		});
		fileMenu.add(settingsItem);
		
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		
		menuBar.add(fileMenu);
		
		// Create the help menu and its items
		helpMenu = new JMenu("Help");
		
		helpItem = new JMenuItem(Assets.APP_NAME + " Help");
		helpItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
					mainWindow,
					new JLabel(Assets.HELP_TEXT),
					Assets.APP_NAME + " Help",
					JOptionPane.PLAIN_MESSAGE,
					null);
			}
			
		});
		helpMenu.add(helpItem);
		
		aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
					mainWindow,
					new JLabel(Assets.ABOUT_TEXT),
					"About " + Assets.APP_NAME,
					JOptionPane.PLAIN_MESSAGE,
					null);
			}
			
		});
		helpMenu.add(aboutItem);
		
		menuBar.add(helpMenu);
		
		mainWindow.setJMenuBar(menuBar);
	}
}
