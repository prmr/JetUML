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
		assertEquals("<html></html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml2() // simple one-line text
	{
		MultiLineString string = new MultiLineString();
		string.setText("«interface»");
		assertEquals("<html>&nbsp;«interface»&nbsp;</html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml3() // simple one-line text, bold
	{
		MultiLineString string = new MultiLineString(true);
		string.setText("«interface»");
		assertEquals("<html>&nbsp;<b>«interface»</b>&nbsp;</html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml4() // simple one-line text, bold, underline
	{
		MultiLineString string = new MultiLineString(true);
		string.setText("«interface»");
		string.setUnderlined(true);
		assertEquals("<html>&nbsp;<u><b>«interface»</b></u>&nbsp;</html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml5() // two-line text, bold, underline
	{
		MultiLineString string = new MultiLineString(true);
		string.setText("«interface»\nFoo");
		string.setUnderlined(true);
		assertEquals("<html>&nbsp;<u><b>«interface»</b></u>&nbsp;<br>&nbsp;<u><b>Foo</b></u>&nbsp;</html>", string.convertToHtml().toString());
	}
	
	@Test
	public void testConvertToHtml6() // two-line text, embedded html
	{
		MultiLineString string = new MultiLineString();
		string.setText("«interface»\n<b>Foo</b>");
		assertEquals("<html>&nbsp;«interface»&nbsp;<br>&nbsp;&lt;b&gt;Foo&lt;/b&gt;&nbsp;</html>", string.convertToHtml().toString());
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
