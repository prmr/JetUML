package ca.mcgill.cs.stg.jetuml.framework;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;

import org.junit.Test;

public class TestRecentFilesQueue 
{
	@Test
	public void testInit()
	{
		RecentFilesQueue queue = new RecentFilesQueue();
		assertEquals(0, queue.size());
		assertEquals("", queue.serialize());
	}
	
	@Test
	public void testAdd()
	{
		RecentFilesQueue queue = new RecentFilesQueue();
		queue.add("testdata/test1.class.jet");
		assertEquals(1, queue.size());
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), queue.iterator().next());
		queue.add("THISISNOTAFILE|||");
		assertEquals(1, queue.size());
		queue.add("testdata/test1.object.jet");
		assertEquals(2, queue.size());
		Iterator<File> iterator = queue.iterator();
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), iterator.next());
		
		queue.add("testdata/test1.sequence.jet");
		assertEquals(3, queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test1.sequence.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), iterator.next());
		
		queue.add("testdata");
		assertEquals(3, queue.size());
		
		queue.add("");
		assertEquals(3, queue.size());
		
		queue.add("testdata/test1.sequence.jet");
		assertEquals(3, queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test1.sequence.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), iterator.next());
		
		queue.add("testdata/test1.object.jet");
		assertEquals(3, queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.sequence.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), iterator.next());
		
		queue.add("testdata/test1.state.jet");
		assertEquals(4, queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test1.state.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.sequence.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), iterator.next());
		
		queue.add("testdata/test1.usecase.jet");
		assertEquals(5, queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test1.usecase.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.state.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.sequence.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), iterator.next());
		
		queue.add("testdata/test2.sequence.jet");
		assertEquals(5, queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test2.sequence.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.usecase.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.state.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());
		assertEquals(new File("testdata/test1.sequence.jet").getAbsoluteFile(), iterator.next());		
	}
	
	@Test
	public void testGetMostRecentDirectory()
	{
		RecentFilesQueue queue = new RecentFilesQueue();
		assertEquals(new File("."), queue.getMostRecentDirectory() );
		queue.add("testdata/test1.class.jet");
		assertEquals(new File("testdata").getAbsoluteFile(), queue.getMostRecentDirectory() );
	}
	
	@Test
	public void testSerialize()
	{
		RecentFilesQueue queue = new RecentFilesQueue();
		assertEquals("", queue.serialize());
		queue.add("testdata/test1.class.jet");
		assertEquals(new File("testdata/test1.class.jet").getAbsolutePath(), queue.serialize());
		
		queue.add("testdata/test1.object.jet");
		String out = new File("testdata/test1.object.jet").getAbsolutePath() + "|" +
				new File("testdata/test1.class.jet").getAbsolutePath();
		assertEquals(out, queue.serialize());
		
		queue.add("testdata/test1.sequence.jet");
		out = 	new File("testdata/test1.sequence.jet").getAbsolutePath() + "|" +
				new File("testdata/test1.object.jet").getAbsolutePath() + "|" +
				new File("testdata/test1.class.jet").getAbsolutePath();
		assertEquals(out, queue.serialize());
	}
	
	
	@Test
	public void testDeserialize()
	{
		RecentFilesQueue queue = new RecentFilesQueue();
		queue.deserialize("");
		assertEquals(0, queue.size());
		
		String in = new File("testdata/test1.class.jet").getAbsolutePath();
		queue.deserialize(in);
		assertEquals(1,queue.size());
		Iterator<File> iterator = queue.iterator();
		assertEquals(new File(in).getAbsoluteFile(), iterator.next());
		
		in = new File("testdata/test1.object.jet").getAbsolutePath();
		queue.deserialize(in);
		assertEquals(1,queue.size());
		iterator = queue.iterator();
		assertEquals(new File(in).getAbsoluteFile(), iterator.next());
		
		in = new File("testdata/test1.object.jet").getAbsolutePath() + "|" +
				new File("testdata/test1.object.jet").getAbsolutePath();
		queue.deserialize(in);
		assertEquals(1,queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());	
	
		in = new File("testdata/test1.object.jet").getAbsolutePath() + "|" +
				new File("testdata/test1.class.jet").getAbsolutePath();
		queue.deserialize(in);
		assertEquals(2,queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());	
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), iterator.next());	
		
		in = new File("testdata/test1.object.jet").getAbsolutePath() + "|" +
				new File("testdata/test1.class.jet").getAbsolutePath() + "|" +
				new File("testdata/test1.sequence.jet").getAbsolutePath();
		queue.deserialize(in);
		assertEquals(3,queue.size());
		iterator = queue.iterator();
		assertEquals(new File("testdata/test1.object.jet").getAbsoluteFile(), iterator.next());	
		assertEquals(new File("testdata/test1.class.jet").getAbsoluteFile(), iterator.next());	
		assertEquals(new File("testdata/test1.sequence.jet").getAbsoluteFile(), iterator.next());	
	}
}
