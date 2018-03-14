package ca.mcgill.cs.jetuml;

import ca.mcgill.cs.jetuml.application.Clipboard;
import javafx.embed.swing.JFXPanel;

/**
 * Loads JavaFX toolkit and environment.
 * @author Kaylee I. Kutschera
 */
@SuppressWarnings("unused")
public class JavaFXLoader {
	private final static JavaFXLoader INSTANCE = new JavaFXLoader();
	private static JFXPanel aJFXPanel = new JFXPanel();
	
	private JavaFXLoader() {}
	
	public static JavaFXLoader instance()
	{
		return INSTANCE;
	}
}
