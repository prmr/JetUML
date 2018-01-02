package ca.mcgill.cs.jetuml.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Test;

public class TestStringViewer
{
	private static Method convertToHtml;
	
	static
	{
		try
		{
			convertToHtml = StringViewer.class.getDeclaredMethod("convertToHtml", String.class);
			convertToHtml.setAccessible(true);
		}
		catch( Exception pException )
		{
			fail();
		}
	}
	
	private static String convertToHtml(StringViewer pViewer, String pString)
	{
		try
		{
			return (String) convertToHtml.invoke(pViewer, pString);
		}
		catch(Exception pException)
		{
			fail();
			return null;
		}
	}
	
	@Test
	public void testConvertToHtml1() // empty text
	{
		StringViewer viewer = new StringViewer(StringViewer.Align.CENTER, false, false);
		assertEquals("<html><p align=\"center\"></p></html>", convertToHtml( viewer, ""));
	}
	
	@Test
	public void testConvertToHtml2() // simple one-line text
	{
		StringViewer viewer = new StringViewer(StringViewer.Align.CENTER, false, false);
		assertEquals("<html><p align=\"center\">&nbsp;«interface»&nbsp;</p></html>", convertToHtml( viewer, "«interface»"));
	}
	
	@Test
	public void testConvertToHtml3() // simple one-line text, bold
	{
		StringViewer viewer = new StringViewer(StringViewer.Align.CENTER, true, false);
		assertEquals("<html><p align=\"center\">&nbsp;<b>«interface»</b>&nbsp;</p></html>", convertToHtml( viewer, "«interface»"));
	}
	
	@Test
	public void testConvertToHtml4() // simple one-line text, bold, underline
	{
		StringViewer viewer = new StringViewer(StringViewer.Align.CENTER, true, true);
		assertEquals("<html><p align=\"center\">&nbsp;<u><b>«interface»</b></u>&nbsp;</p></html>", convertToHtml( viewer, "«interface»"));
	}
	
	@Test
	public void testConvertToHtml5() // two-line text, bold, underline
	{
		StringViewer viewer = new StringViewer(StringViewer.Align.CENTER, true, true);
		assertEquals("<html><p align=\"center\">&nbsp;<u><b>«interface»</b></u>&nbsp;<br>&nbsp;<u><b>Foo</b></u>&nbsp;</p></html>", convertToHtml( viewer, "«interface»\nFoo"));
	}
	
	@Test
	public void testConvertToHtml6() // two-line text, embedded html
	{
		StringViewer viewer = new StringViewer(StringViewer.Align.CENTER, false, false);
		assertEquals("<html><p align=\"center\">&nbsp;«interface»&nbsp;<br>&nbsp;&lt;b&gt;Foo&lt;/b&gt;&nbsp;</p></html>", convertToHtml( viewer, "«interface»\n<b>Foo</b>"));
	}
	
	@Test
	public void testConvertToHtml7() // Ampersand escape
	{
		StringViewer viewer = new StringViewer(StringViewer.Align.CENTER, false, false);
		assertEquals("<html><p align=\"center\">&nbsp;&lt;b&gt;Foo&amp;&lt;/b&gt;&nbsp;</p></html>", convertToHtml( viewer, "<b>Foo&</b>"));
	}
}
