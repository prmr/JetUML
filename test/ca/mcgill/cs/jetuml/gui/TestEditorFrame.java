/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.filechooser.FileFilter;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.gui.EditorFrame;
import junit.framework.TestCase;

public class TestEditorFrame 
{ 
	private Method aCreateFileFilter;
	private EditorFrame aEditorFrame; 
	
	@Before
	public void setup() throws Exception
	{
		aCreateFileFilter = EditorFrame.class.getDeclaredMethod("createFileFilter", String.class);
		aCreateFileFilter.setAccessible(true);
		aEditorFrame = new EditorFrame(UMLEditor.class);
	}
	
	@Test
	public void testReplaceExtension()
	{
		assertEquals("foo.png", EditorFrame.replaceExtension("foo.jet", ".jet", ".png"));
		assertEquals("", EditorFrame.replaceExtension("", ".jet", ".png"));
		assertEquals("foo.class.png", EditorFrame.replaceExtension("foo.class.jet", ".jet", ".png"));
	}
	
	@Test
	public void testCreateFileFilterToString()
	{
		FileFilter filter = createFileFilter("PNG");
		assertEquals("PNG", filter.toString());
	}
	
	@Test
	public void testCreateFileFilterAcceptFileUpperCase()
	{
		FileFilter filter = createFileFilter("PNG");
		File temp = new File("foo.PNG");
		assertTrue(filter.accept(temp));
		temp.delete();
	}
	
	@Test
	public void testCreateFileFilterAcceptFileLowerCase()
	{
		FileFilter filter = createFileFilter("PNG");
		File temp = new File("foo.png");
		assertTrue(filter.accept(temp));
		temp.delete();
	}
	
	@Test
	public void testCreateFileFilteRejectFile()
	{
		FileFilter filter = createFileFilter("PNG");
		File temp = new File("foo.gif");
		assertFalse(filter.accept(temp));
		temp.delete();
	}
	
	@Test
	public void testCreateFileFilteAcceptDirectory()
	{
		FileFilter filter = createFileFilter("PNG");
		File temp = new File("foo");
		temp.mkdir();
		assertTrue(temp.isDirectory());
		assertTrue(filter.accept(temp));
		temp.delete();
	}
	
	private FileFilter createFileFilter(String pFormat)
	{
		try
		{
			return (FileFilter) aCreateFileFilter.invoke(aEditorFrame, pFormat);
		}
		catch(InvocationTargetException | IllegalAccessException pException)
		{
			TestCase.fail();
			return null;
		}
	}
}
