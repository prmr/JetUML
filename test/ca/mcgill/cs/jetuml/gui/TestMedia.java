package ca.mcgill.cs.jetuml.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import ca.mcgill.cs.jetuml.gui.Media;

import org.junit.jupiter.api.Test;

public class TestMedia {

	@Test
	public void testMedia_nullParam()
	{
		Optional<Media> opt = Media.getMedia(null);
		assertTrue(opt.isEmpty());
	}
	
	@Test
	public void testMedia_differentLetterCase()
	{
		Optional<Media> opt = Media.getMedia("TeXt");
		assertEquals(opt.get(), Media.TEXT);
	}
	
	@Test
	public void testMedia_misspelled()
	{
		Optional<Media> opt = Media.getMedia("asdfqw4ek332zv1xmv5koq12voq8woep");
		assertTrue(opt.isEmpty());
	}
	
	@Test
	public void testMedia_lowerCaseWellSpelled()
	{
		Optional<Media> opt = Media.getMedia("image");
		assertEquals(opt.get(), Media.IMAGE);
	}
}
