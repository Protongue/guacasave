package org.guacommunity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.ImageIcon;

public final class Assets {
	
	static Assets assets = new Assets();

	public static final String APP_NAME = "GuacaSave";
	public static final String VERSION_NUMBER = "0.1a";
	
	public static ImageIcon FOLDER_ICON = new ImageIcon(Assets.class.getResource("/icons/folder.png"));
	public static ImageIcon KEYBOARD_ICON = new ImageIcon(Assets.class.getResource("/icons/keyboard.png"));
	
	public static final String HELP_TEXT = getFileText("/text/help.html");
	public static final String ABOUT_TEXT = getFileText("/text/about.html");

	/**
	 * Gets the text of the specified file.
	 * Reserved tokens are replaced with their respective text.
	 */
	private static String getFileText(String fileName) {
		String fileText;
		try {
			fileText = new String(Files.readAllBytes(Paths.get(Assets.class.getResource(fileName).toURI())), StandardCharsets.UTF_8);
		} catch (Exception e) {
			fileText = fileName + " could not be loaded!";
		}
		fileText = fileText.replace("{{APP_NAME}}", APP_NAME);
		fileText = fileText.replace("{{VERSION_NUMBER}}", VERSION_NUMBER);
		
		return fileText;
	}
}
