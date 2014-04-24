package org.guacommunity;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class Settings {

	private final Preferences settingsPrefs;
	private static final String SAVE_FILE_KEY = "SAVE_FILE";
	private static final String BACKUP_DIRECTORY_KEY = "BACKUP_DIRECTORY";
	
	private Path saveFilePath;
	private Path backupDirectoryPath;
	
	private final JDialog dialog;
	private final JPanel contentPane;
	private final JPanel buttonPanel;
	private final JLabel saveFileLabel, backupDirectoryLabel;
	private final JTextField saveFileField, backupDirectoryField;
	private final JButton saveFileButton, backupDirectoryButton, applyButton, cancelButton;
	private final JFileChooser saveFileChooser, backupDirectoryChooser;
	
	/**
	 * Creates the settings dialog. The user sets application preferences here.
	 */
	public Settings(JFrame parentFrame) {
		settingsPrefs = Preferences.userNodeForPackage(getClass());
		
		dialog = new JDialog(
			parentFrame,
			Assets.APP_NAME + " Settings",
			Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setResizable(false);
		contentPane = new JPanel();
		dialog.setContentPane(contentPane);
		
		saveFileChooser = new JFileChooser();
		saveFileChooser.setFileFilter(new FileNameExtensionFilter("DAT file", "dat"));
		saveFileChooser.setFileHidingEnabled(false);
		
		saveFileLabel = new JLabel("save.dat File Location: ");
		saveFileField = new JTextField(30);
		saveFileField.setEditable(false);
		saveFileButton = new JButton(Assets.FOLDER_ICON);
		saveFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = saveFileChooser.showOpenDialog(dialog);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setSaveFilePath(saveFileChooser.getSelectedFile().toPath());
				}
			}
		});
		
		backupDirectoryChooser = new JFileChooser();
		backupDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		backupDirectoryChooser.setFileHidingEnabled(false);
		
		backupDirectoryLabel = new JLabel("Backup Directory: ");
		backupDirectoryField = new JTextField(30);
		backupDirectoryField.setEditable(false);
		backupDirectoryButton = new JButton(Assets.FOLDER_ICON);
		backupDirectoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = backupDirectoryChooser.showOpenDialog(dialog);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setBackupDirectoryPath(backupDirectoryChooser.getSelectedFile().toPath());
					backupDirectoryField.setText(backupDirectoryPath.toString());
				}
			}
		});
		
		buttonPanel = new JPanel();
		applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		buttonPanel.add(applyButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		buttonPanel.add(cancelButton);
		
		// Component layout
		contentPane.setLayout(new MigLayout());

		contentPane.add(saveFileLabel);
		contentPane.add(saveFileField);
		contentPane.add(saveFileButton, "wrap");
		contentPane.add(backupDirectoryLabel);
		contentPane.add(backupDirectoryField);
		contentPane.add(backupDirectoryButton, "wrap");
		contentPane.add(buttonPanel, "spanx, align center");
		
		dialog.pack();
	}
		
	/**
	 * Gets the save file path from preferences.
	 */
	public Path getSaveFilePath() {
		return Paths.get(settingsPrefs.get(SAVE_FILE_KEY, ""));
	}
	
	/**
	 * Gets the backup directory path from preferences.
	 */
	public Path getBackupDirectoryPath() {
		return Paths.get(settingsPrefs.get(BACKUP_DIRECTORY_KEY, ""));
	}
	
	/**
	 * Returns whether the settings from preferences are valid.
	 */
	public boolean isValid() {
		File saveFile = new File(getSaveFilePath().toUri());
		File backupDirectory = new File(getBackupDirectoryPath().toUri());
		
		return (saveFile.exists() && saveFile.isFile() && backupDirectory.exists() && backupDirectory.isDirectory());
	}
	
	/**
	 * Sets the save file path on the dialog form (does not persist to preferences).
	 */
	private void setSaveFilePath(Path savePath) {
		this.saveFilePath = savePath;
		saveFileField.setText(savePath.toString());
	}
	
	/**
	 * Sets the backup directory path on the dialog form (does not persist to preferences).
	 */
	private void setBackupDirectoryPath(Path backupPath) {
		this.backupDirectoryPath = backupPath;
		backupDirectoryField.setText(backupPath.toString());
	}
	
	/**
	 * Shows the settings dialog with the current settings.
	 */
	public void show() {
		String savePathString = settingsPrefs.get(SAVE_FILE_KEY, "");
		String backupPathString = settingsPrefs.get(BACKUP_DIRECTORY_KEY, "");

		setSaveFilePath(Paths.get(savePathString));
		setBackupDirectoryPath(Paths.get(backupPathString));
		
		dialog.setVisible(true);
	}
	
	/**
	 * Hides the settings dialog.
	 */
	public void hide() {
		dialog.setVisible(false);
	}
	
	/**
	 * When user clicks OK, save settings and hide the settings dialog.
	 */
	private void ok() {
		settingsPrefs.put(SAVE_FILE_KEY, saveFilePath.toString());
		settingsPrefs.put(BACKUP_DIRECTORY_KEY, backupDirectoryPath.toString());
		GuacaSave.setButtonOperability();
		dialog.setVisible(false);
	}
	
	/**
	 * When user clicks Cancel, discard any changes and hide the settings dialog.
	 */
	private void cancel() {
		hide();
	}
}
