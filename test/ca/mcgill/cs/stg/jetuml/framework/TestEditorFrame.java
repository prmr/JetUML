package ca.mcgill.cs.stg.jetuml.framework;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestEditorFrame 
{
	@Test
	public void testReplaceExtension()
	{
		assertEquals("foo.png", EditorFrame.replaceExtension("foo.jet", ".jet", ".png"));
		assertEquals("", EditorFrame.replaceExtension("", ".jet", ".png"));
		assertEquals("foo.class.png", EditorFrame.replaceExtension("foo.class.jet", ".jet", ".png"));
	}
}
