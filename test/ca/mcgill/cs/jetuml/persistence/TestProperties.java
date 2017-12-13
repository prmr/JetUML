package ca.mcgill.cs.jetuml.persistence;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.application.MultiLineString;

import static org.junit.Assert.*;

public class TestProperties
{
	private Properties aProperties;
	
	@Before
	public void setup()
	{
		aProperties = new Properties();
	}
	
	@Test
	public void TestBasicPut()
	{
		aProperties.put("A", "A");
		aProperties.put("B", 100);
		aProperties.put("C", false);
		aProperties.put("D", MultiLineString.Align.CENTER);
		assertEquals(4, size());
		assertEquals("A", (String) aProperties.get("A"));
		assertEquals(100, (int) aProperties.get("B"));
		assertEquals(false, (boolean) aProperties.get("C"));
		assertEquals(MultiLineString.Align.CENTER, (MultiLineString.Align) aProperties.get("D"));
	}
	
	@Test
	public void TestPutOverride()
	{
		aProperties.put("A", "A");
		assertEquals(1, size());
		assertEquals("A", (String) aProperties.get("A"));
		aProperties.put("A", 100);
		assertEquals(1, size());
		assertEquals(100, (int) aProperties.get("A"));
		aProperties.put("A", false);
		assertEquals(1, size());
		assertEquals(false, (boolean) aProperties.get("A"));
		aProperties.put("A", MultiLineString.Align.CENTER);
		assertEquals(1, size());
		assertEquals(MultiLineString.Align.CENTER, (MultiLineString.Align) aProperties.get("A"));
	}
	
	private int size()
	{
		int size = 0;
		for( @SuppressWarnings("unused") String s : aProperties )
		{
			size++;
		}
		return size;
	}

}
