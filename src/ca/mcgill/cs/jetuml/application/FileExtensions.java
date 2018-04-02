package ca.mcgill.cs.jetuml.application;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import ca.mcgill.cs.jetuml.UMLEditor;
import javafx.stage.FileChooser.ExtensionFilter;


/**
 * A utility class to create and access diagram extension 
 * filters used by the file chooser.
 * 
 * @author Kaylee I. Kutschera
 */
public final class FileExtensions
{
	private static List<ExtensionFilter> aFileFilters = new LinkedList<>();
	
	private FileExtensions() {}
	
	/**
	 * Constructs all file extension filters related to the diagrams.
	 */
	static 
	{
		ResourceBundle aAppResources = ResourceBundle.getBundle(UMLEditor.class.getName() + "Strings");
		aFileFilters.add(new ExtensionFilter("Jet Files", "*" + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("class.name"), 
				"*" + aAppResources.getString("class.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("sequence.name"), 
				"*" + aAppResources.getString("sequence.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("state.name"), 
					    "*" + aAppResources.getString("state.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("object.name"), 
						"*" + aAppResources.getString("object.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("usecase.name"), 
						"*" + aAppResources.getString("usecase.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("class2.name"), 
				"*" + aAppResources.getString("class2.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("sequence2.name"), 
				"*" + aAppResources.getString("sequence2.extension") + aAppResources.getString("files.extension")));
				aFileFilters.add(new ExtensionFilter(aAppResources.getString("state2.name"), 
				"*" + aAppResources.getString("state2.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("object2.name"), 
						"*" + aAppResources.getString("object2.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter(aAppResources.getString("usecase2.name"), 
				"*" + aAppResources.getString("usecase2.extension") + aAppResources.getString("files.extension")));
		aFileFilters.add(new ExtensionFilter("All Files", "*.*"));
	}
	
	/**
	 * @return list of all diagram extension filters
	 */
	public static List<ExtensionFilter> getAll()
	{
		return aFileFilters;
	}
	
	/**
	 * @param pDescription description of the filter
	 * @return the corresponding diagram extension filter
	 */
	public static ExtensionFilter get(String pDescription) 
	{
		for(ExtensionFilter filter: aFileFilters) 
		{
			if(filter.getDescription().equals(pDescription))
			{
				return filter;
			}
		}
		return null;
	}
}
	
	