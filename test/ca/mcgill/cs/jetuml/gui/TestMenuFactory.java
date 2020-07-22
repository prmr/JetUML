/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package 	ca.mcgill.cs.jetuml.gui;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.ApplicationResources;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;

public class TestMenuFactory 
{
	private MenuFactory aMenuFactory;
	private Method aInstallMnenomicMethod;
	
	public TestMenuFactory() throws Exception
	{
		aInstallMnenomicMethod = MenuFactory.class.getDeclaredMethod("installMnemonic", String.class, String.class);
		aInstallMnenomicMethod.setAccessible(true);
	}
	
	public String installMnemonic(String pText, String pMnemonic)
	{
		try
		{
			return (String) aInstallMnenomicMethod.invoke(aMenuFactory, pText, pMnemonic);
		}
		catch( Exception e )
		{
			e.printStackTrace();
			fail();
			return null;
		}
	}
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup() throws Exception
	{
		Constructor<?> defaultConstructor = ApplicationResources.class.getDeclaredConstructor();
		defaultConstructor.setAccessible(true);
		ApplicationResources testResources = (ApplicationResources) defaultConstructor.newInstance();
		Field resourceBundleField = ApplicationResources.class.getDeclaredField("aResouceBundle");
		resourceBundleField.setAccessible(true);
		resourceBundleField.set(testResources, ResourceBundle.getBundle(TestMenuFactory.class.getName()));
		// Confirm that everything works
		assert testResources.containsKey("file.text");
		aMenuFactory = new MenuFactory(testResources);
	}
	
	@Test
	public void testInstallMnemonic()
	{
		assertEquals("_File", installMnemonic("File", "F"));		
		assertEquals("Fil_e", installMnemonic("File", "e"));
		assertEquals("F_ile", installMnemonic("File", "i"));
		assertEquals("File", installMnemonic("File", "x"));
	}
	
	@Test
	public void testCreateMenuWithTextAndMnemonic()
	{
		MenuItem item = aMenuFactory.createMenu("file", false);
		assertEquals("_File", item.getText());
	}
	
	@Test
	public void testCreateMenuItemWithAll()
	{
		MenuItem item = aMenuFactory.createMenuItem("file.open", false, e -> {});
		assertEquals("_Open", item.getText());
		assertNotNull(item.getGraphic());
		KeyCombination accelerator = item.getAccelerator();
		assertNotNull(accelerator);
		if( System.getProperty("os.name", "unknown").toLowerCase().startsWith("mac") )
		{
			assertEquals("Meta+O", accelerator.getName());
		}
		else
		{
			assertEquals(ModifierValue.DOWN, accelerator.getControl());
			assertEquals("Ctrl+O", accelerator.getName());
		}
	}
	
	@Test
	public void testCreateMenuItemWithTextOnly()
	{
		MenuItem item = aMenuFactory.createMenuItem("file.new", false, e -> {});
		assertEquals("New", item.getText());
		assertNull(item.getGraphic());
		assertNull(item.getAccelerator());
	}
}
