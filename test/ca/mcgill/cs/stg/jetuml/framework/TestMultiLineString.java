/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestMultiLineString
{
	@Test
	public void testConstruction()
	{
		MultiLineString string = new MultiLineString();
		assertEquals(false, string.isUnderlined());
		assertEquals(false, string.isBold());
		assertEquals(MultiLineString.CENTER, string.getJustification());
		assertEquals("", string.getText());
		
		string = new MultiLineString(true);
		assertEquals(false, string.isUnderlined());
		assertEquals(true, string.isBold());
		assertEquals(MultiLineString.CENTER, string.getJustification());
		assertEquals("", string.getText());
	}
	
	@Test
	public void testConvertToHtml1() // empty text
	{
		MultiLineString string = new MultiLineString();
		assertEquals("<html><p align=\"center\"></p></html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml2() // simple one-line text
	{
		MultiLineString string = new MultiLineString();
		string.setText("«interface»");
		assertEquals("<html><p align=\"center\">&nbsp;«interface»&nbsp;</p></html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml3() // simple one-line text, bold
	{
		MultiLineString string = new MultiLineString(true);
		string.setText("«interface»");
		assertEquals("<html><p align=\"center\">&nbsp;<b>«interface»</b>&nbsp;</p></html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml4() // simple one-line text, bold, underline
	{
		MultiLineString string = new MultiLineString(true);
		string.setText("«interface»");
		string.setUnderlined(true);
		assertEquals("<html><p align=\"center\">&nbsp;<u><b>«interface»</b></u>&nbsp;</p></html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml5() // two-line text, bold, underline
	{
		MultiLineString string = new MultiLineString(true);
		string.setText("«interface»\nFoo");
		string.setUnderlined(true);
		assertEquals("<html><p align=\"center\">&nbsp;<u><b>«interface»</b></u>&nbsp;<br>&nbsp;<u><b>Foo</b></u>&nbsp;</p></html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml6() // two-line text, embedded html
	{
		MultiLineString string = new MultiLineString();
		string.setText("«interface»\n<b>Foo</b>");
		assertEquals("<html><p align=\"center\">&nbsp;«interface»&nbsp;<br>&nbsp;&lt;b&gt;Foo&lt;/b&gt;&nbsp;</p></html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testSetJustification()
	{
		for( int i = 0; i < 3; i++ )
		{
			MultiLineString string = new MultiLineString();
			string.setJustification(i);
			assertEquals(i, string.getJustification());
		}
	}
	
	@Test
	public void testEqualsProperties()
	{
		MultiLineString string1 = new MultiLineString();
		MultiLineString string2 = new MultiLineString();
		assertTrue(string1.equalProperties(string2));
		assertTrue(string2.equalProperties(string1));
		
		string2.setText("Foo");
		assertFalse(string1.equalProperties(string2));
		assertFalse(string2.equalProperties(string1));
		
		string2 = new MultiLineString();
		string2.setJustification(2);
		assertFalse(string1.equalProperties(string2));
		assertFalse(string2.equalProperties(string1));
		
		string2 = new MultiLineString(true);
		assertFalse(string1.equalProperties(string2));
		assertFalse(string2.equalProperties(string1));
		
		string2 = new MultiLineString();
		string2.setUnderlined(true);
		assertFalse(string1.equalProperties(string2));
		assertFalse(string2.equalProperties(string1));
		
		string1.setUnderlined(true);
		assertTrue(string1.equalProperties(string2));
		assertTrue(string2.equalProperties(string1));
	}
}
