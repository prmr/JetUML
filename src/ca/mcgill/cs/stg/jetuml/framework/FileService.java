/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.stg.jetuml.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * This singleton class produces common file open and file save dialogs for normal operation.
 */
public final class FileService
{	
	private static FileService service;
	
	private JFileChooser aFileChooser;

	private FileService()
	{
		aFileChooser = new JFileChooser();
	}
	
	/**
	 * Sets the initial directory referenced by the dialogs.
	 * @param pInitialDirectory The initial directory.
	 */
	public void setInitialDirectory(File pInitialDirectory)
	{
		aFileChooser.setCurrentDirectory(pInitialDirectory);
	}
	
	/**
     * Gets the FileService instance.
     * @return a service for local dialogs
	 */
	public static FileService getInstance()
	{
		if (service != null) 
		{
			return service;
		}
		else
		{
			service = new FileService();
			return service;
		}
	}
   
	/**
	 * Gets an Open object that encapsulates the stream and name of the file that the user selected.
	 * @param pDefaultDirectory the default directory for the file chooser
	 * @param pDefaultFile the default file for the file chooser
	 * @param pFilter the extension filter
	 * @param pOptionalFilters is an array of diagram type specific extensions. It can be null.
	 * @return the Open object for the selected file
	 * @throws FileNotFoundException If there's any problem opening the file.
	 */
	public FileService.Open open(String pDefaultDirectory, String pDefaultFile, 
			ExtensionFilter pFilter, ExtensionFilter[] pOptionalFilters) throws FileNotFoundException
	{
		aFileChooser.resetChoosableFileFilters();
		aFileChooser.setFileFilter(pFilter);
     
		//The following loop adds in FileExtensions for a user to choose based on Diagram type.
		//Done by JoelChev
		if(pOptionalFilters != null)
		{
			for(ExtensionFilter aFilter: pOptionalFilters)
			{
				aFileChooser.addChoosableFileFilter(aFilter);
			}
		}
		if(pDefaultDirectory != null)
		{
			aFileChooser.setCurrentDirectory(new File(pDefaultDirectory));
		}
		if(pDefaultFile == null)
		{
			aFileChooser.setSelectedFile(null);
		} 
		else
		{
			aFileChooser.setSelectedFile(new File(pDefaultFile));
		}         
		int response = aFileChooser.showOpenDialog(null);         
		if(response == JFileChooser.APPROVE_OPTION) 
		{
			return new Open(aFileChooser.getSelectedFile());
		}
		else 
		{
			return new Open(null);
		}
	}
			
   
	/**
	 * Gets a Save object that encapsulates the stream and name of the file that the user selected (or will
	 * select).
	 * @param pDefaultDirectory the default directory for the file chooser
	 * @param pDefaultFile the default file for the file chooser
	 * @param pFilter the extension filter
	 * @param pRemoveExtension the extension to remove from the default file name
	 * @param pAddExtension the extension to add to the file name
	 * @return the Save object for the selected file
	 * @throws FileNotFoundException If there's any problem saving the file.
	 */
	public FileService.Save save(String pDefaultDirectory, String pDefaultFile, 
			ExtensionFilter pFilter, String pRemoveExtension, String pAddExtension) throws FileNotFoundException
	{
		aFileChooser.resetChoosableFileFilters();
		aFileChooser.setFileFilter(pFilter);
		if(pDefaultDirectory == null) 
		{
			aFileChooser.setCurrentDirectory(new File("."));
		}
		else 
		{
			aFileChooser.setCurrentDirectory(new File(pDefaultDirectory));
		}
		if(pDefaultFile != null)
		{
			File f = new File(editExtension(pDefaultFile, pRemoveExtension, pAddExtension));                  
			aFileChooser.setSelectedFile(f);
		}
		else 
		{
			aFileChooser.setSelectedFile(new File(""));
		}
		int response = aFileChooser.showSaveDialog(null);         
		if(response == JFileChooser.APPROVE_OPTION)
		{
			File f = aFileChooser.getSelectedFile();
			if(pAddExtension != null && f.getName().indexOf(".") < 0)
			{
				f = new File(f.getPath() + pAddExtension);
			}

			if(!f.exists()) 
			{
				return new Save(f);
			}
        
			ResourceBundle editorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
			int result = JOptionPane.showConfirmDialog( null, editorResources.getString("dialog.overwrite"), null, JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) 
			{
				return new Save(f);
			}                       
		}
		return new Save(null);
	}

	/**
	 * An Open object encapsulates the stream and name of the file that the user selected for opening.
	 */
	public class Open
	{
		private String aName;
		private InputStream aIn;
			
		/**
		 * Creates a new open object.
		 * @param pFile The file that is being opened.
		 * @throws FileNotFoundException If it's not possible to open it.
		 */
		public Open(File pFile) throws FileNotFoundException
		{
			if(pFile != null)
			{
				aName = pFile.getPath();
				aIn = new FileInputStream(pFile);
			}
		}

		/**
		 * Gets the name of the file that the user selected.
		 * @return the file name      
		 */
		public String getName()
		{
			return aName; 
		}
         
		/**
		 * Gets the input stream corresponding to the user selection.
		 * @return the input stream     
		 */
		public InputStream getInputStream()
		{ 
			return aIn; 
		}
	}

	/**
	 * A Save object encapsulates the stream and name of the file that the user selected for saving.
	 */
	public class Save
	{
		private String aName;
		private OutputStream aOut;
			
		/**
		 * @param pFile The file to save.
		 * @throws FileNotFoundException If it's not possible
		 */
		public Save(File pFile) throws FileNotFoundException
		{
			if(pFile != null)
			{
				aName = pFile.getPath();
				aOut = new FileOutputStream(pFile);
			}
		}

		/**
		 * Gets the name of the file that the user selected.
		 * @return the file name, or null if the file dialog is only displayed when the output
		 * stream is closed.       
		 */
		public String getName() 
			{ return aName; }
         
		/**
		 * Gets the output stream corresponding to the user selection.
		 * @return the output stream     
		 */
		public OutputStream getOutputStream() 
		{ return aOut; }
	}

	/**
   	 * Edits the file path so that it ends in the desired extension.
   	 * @param pOriginal the file to use as a starting point
     * @param pToBeRemoved the extension that is to be removed before adding the desired extension. Use
     * null if nothing needs to be removed. 
   	 * @param pDesired the desired extension (e.g. ".png"), or a | separated list of extensions
   	 * @return original if it already has the desired extension, or a new file with the edited file path
   	 */
	public static String editExtension(String pOriginal, String pToBeRemoved, String pDesired)
	{
		if (pOriginal == null) 
		{
			return null;
		}
		int n = pDesired.indexOf('|');
		if(n >= 0) 
		{
			pDesired = pDesired.substring(0, n);
		}
		String path = pOriginal;
		if(!path.toLowerCase().endsWith(pDesired.toLowerCase()))
		{   		
			if(pToBeRemoved != null && path.toLowerCase().endsWith(pToBeRemoved.toLowerCase())) 
			{
				path = path.substring(0, path.length() - pToBeRemoved.length());
			}
			path = path + pDesired;
		}
		return path;      
	}
}
