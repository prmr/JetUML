package ca.mcgill.cs.jetuml.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import javafx.stage.FileChooser.ExtensionFilter;

public class TestFileExtensions 
{
	
	@Test
	public void testGetAll() 
	{
		List<ExtensionFilter> filters = FileExtensions.getAll();
		assertEquals(12, filters.size());
		for(ExtensionFilter filter : filters) 
		{
			assertNotNull(filter);
		}
	}
	
	@Test
	public void testGetOnValidInput() 
	{
		assertNotNull(FileExtensions.get("Class Diagram Files"));
		assertNotNull(FileExtensions.get("Jet Files"));
		assertNotNull(FileExtensions.get("All Files"));
	}
	
	@Test
	public void testGetOnInvalidInput() 
	{
		assertEquals(null, FileExtensions.get(""));
	}

}
