package com.horstmann.violet.framework;

import org.junit.Test;
import static org.junit.Assert.*;

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
}
