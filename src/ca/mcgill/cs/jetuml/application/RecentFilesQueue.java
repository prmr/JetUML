/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.application;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A fixed-sized queue for storing a list of 
 * files recently manipulated by the application.
 */
public class RecentFilesQueue implements Iterable<File>
{
	private static final int CAPACITY = 5;
	
	private final LinkedList<File> aFiles = new LinkedList<>();
	
	/* 
	 * Somehow java.io.File does not consider two File objects
	 * equal if they don't have the same pathname so 
	 * new File("foo") != new File(new File("foo").getAbsolutePath())
	 * To get around this all files in this class are stored with
	 * their absolute path.
	 */
	
	/**
	 * Adds a file to the queue and removes the oldest
	 * file name from the queue if the queue is at capacity.
	 * The empty string is ignored. If the file corresponding
	 * to pFileName is already in the queue, it is not added, 
	 * but moved to the front of the queue. Only adds files
	 * that exist and are not a directory.
	 * @param pFileName The file to add as the most recent.
	 * @pre pFileName != null
	 */
	public void add(String pFileName)
	{
		assert pFileName != null;
		if( pFileName.length() == 0 )
		{
			return;
		}
		File newFile = new File(pFileName).getAbsoluteFile();
		if( aFiles.contains(newFile))
		{
			aFiles.remove(newFile);
		}
		if( newFile.exists() && newFile.isFile() )
		{
			aFiles.add(0, newFile);
			if( aFiles.size() > CAPACITY)
			{
				aFiles.removeLast();
			}
		}
	}
	
	/**
	 * @return The parent directory of the most recent
	 * file in the list, or the current directory "." 
	 * if the list is empty.
	 */
	public File getMostRecentDirectory()
	{
		if(aFiles.size() > 0)
		{
			return aFiles.get(0).getParentFile();
		}
		else
		{
			return new File(".");
		}
	}

	@Override
	public Iterator<File> iterator() 
	{
		return aFiles.iterator();
	}

	/**
	 * Encodes the list into a string of pipe-separated
	 * ("|") entries suitable for storing as a property.
	 * @return A string encoding all file entries.
	 */
	public String serialize()
	{
		StringBuilder builder = new StringBuilder();
		if( aFiles.size() > 0 )
		{
			builder.append(aFiles.get(0));
		}
		for( int i = 1; i < aFiles.size(); i++)
		{
			builder.append("|");
			builder.append(aFiles.get(i));
		}
		return builder.toString();
	}
	
	/**
	 * @return The number of files in the list.
	 */
	public int size()
	{
		return aFiles.size();
	}
	
	/**
	 * Clears the list and initializes it from pString, which should be
	 * a pipe-separated list of entries created using the serialize() method.
	 * Only files that actually exist are loaded into the list. Any file name in pString
	 * that does not corresponding to an existing file at the time this method
	 * is called is simply dropped.
	 * @param pString A pipe-separated list of entries.
	 * @pre pString != null;
	 */
	public void deserialize(String pString)
	{
		assert pString != null;
		aFiles.clear();
		String[] files = pString.split("\\|");
		for( int i = files.length-1; i >=0; i--)
		{
			if( files[i].trim().length() > 0 )
			{
				add(files[i]);
			}
		}
	}
}
