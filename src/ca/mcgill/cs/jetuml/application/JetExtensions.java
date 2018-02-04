package ca.mcgill.cs.jetuml.application;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import ca.mcgill.cs.jetuml.UMLEditor;
import javafx.stage.FileChooser.ExtensionFilter;


/**
 * A class to create and access diagram extension filters 
 * used by the file chooser.
 * 
 * @author Kaylee I. Kutschera
 */
public final class JetExtensions 
{
	private static final JetExtensions INSTANCE = new JetExtensions();
	
	private List<ExtensionFilter> aJetFilters = new LinkedList<>();
	
	private JetExtensions() 
	{
		ResourceBundle aAppResources = ResourceBundle.getBundle(UMLEditor.class.getName() + "Strings");
		aJetFilters.add(new ExtensionFilter("Jet Files", "*" + aAppResources.getString("files.extension")));
		aJetFilters.add(new ExtensionFilter(aAppResources.getString("state.name"), 
					    "*" + aAppResources.getString("state.extension") + aAppResources.getString("files.extension")));
		aJetFilters.add(new ExtensionFilter(aAppResources.getString("object.name"), 
						"*" + aAppResources.getString("object.extension") + aAppResources.getString("files.extension")));
		aJetFilters.add(new ExtensionFilter(aAppResources.getString("class.name"), 
						"*" + aAppResources.getString("class.extension") + aAppResources.getString("files.extension")));
		aJetFilters.add(new ExtensionFilter(aAppResources.getString("usecase.name"), 
						"*" + aAppResources.getString("usecase.extension") + aAppResources.getString("files.extension")));
		aJetFilters.add(new ExtensionFilter(aAppResources.getString("sequence.name"), 
						"*" + aAppResources.getString("sequence.extension") + aAppResources.getString("files.extension")));
		aJetFilters.add(new ExtensionFilter("All Files", "*.*"));
	}
	
	/**
	 * @return instance of JetExtensions
	 */
	public static JetExtensions getInstance()
	{
		return INSTANCE;
	}
	
	/**
	 * @return list of all diagram extension filters
	 */
	public List<ExtensionFilter> getFilters()
	{
		return aJetFilters;
	}
	
	/**
	 * @param pDescription description of the filter
	 * @return the corresponding diagram extension filter
	 */
	public ExtensionFilter getFilter(String pDescription) 
	{
		for(ExtensionFilter filter: aJetFilters) 
		{
			if(filter.getDescription().equals(pDescription))
			{
				return filter;
			}
		}
		return null;
	}
}
	
	