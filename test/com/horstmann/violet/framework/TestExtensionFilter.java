package com.horstmann.violet.framework;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class TestExtensionFilter
{
	@Test
	public void testBasicConstructor()
	{
		ExtensionFilter filter = new ExtensionFilter("", new String[]{""});
		assertEquals("", filter.getDescription());
		String[] extensions = filter.getExtensions();
		assertEquals(1, extensions.length);
		assertEquals("", extensions[0]);
		
		filter = new ExtensionFilter("Tar files", new String[]{".tar", ".tar.gz"});
		assertEquals("Tar files", filter.getDescription());
		extensions = filter.getExtensions();
		assertEquals(2, extensions.length);
		assertEquals(".tar", extensions[0]);
		assertEquals(".tar.gz", extensions[1]);
	}
	
	@Test 
	public void testCompoundConstructor()
	{
		ExtensionFilter filter = new ExtensionFilter("Tar files", ".tar");
		assertEquals("Tar files", filter.getDescription());
		String[] extensions = filter.getExtensions();
		assertEquals(1, extensions.length);
		assertEquals(".tar", extensions[0]);
		
		filter = new ExtensionFilter("Tar files", ".tar|.tar.gz");
		assertEquals("Tar files", filter.getDescription());
		extensions = filter.getExtensions();
		assertEquals(2, extensions.length);
		assertEquals(".tar", extensions[0]);
		assertEquals(".tar.gz", extensions[1]);
		
		filter = new ExtensionFilter("Tar files", " .tar | .tar.gz ");
		assertEquals("Tar files", filter.getDescription());
		extensions = filter.getExtensions();
		assertEquals(2, extensions.length);
		assertEquals(" .tar ", extensions[0]);
		assertEquals(" .tar.gz ", extensions[1]);
	}
	
	@Test 
	public void testAccept()
	{
		ExtensionFilter filter = new ExtensionFilter("", new String[] {""});
		assertTrue(filter.accept(new File("test")));
		filter = new ExtensionFilter("Test files", new String[] {".txt"});
		assertFalse(filter.accept(new File("README.md")));
		filter = new ExtensionFilter("Readme files", new String[] {".md"});
		assertTrue(filter.accept(new File("README.md")));
	}
}
