package com.horstmann.violet;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestEditorFrame {

	//Tests that it is now impossible to call print from the EditorFrame class.
	/**
	 * @author JoelChev
	 */
	@Test(expected= Error.class)
	public void testPrintIsGone()
	{
		EditorFrame.print();
	}
}
