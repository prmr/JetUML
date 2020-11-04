package ca.mcgill.cs.jetuml.gui.tips;

import static ca.mcgill.cs.jetuml.gui.tips.TipLoader.NUM_TIPS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class TestViewedTips {

	private ViewedTips aViewedTipsFirst;
	private ViewedTips aViewedTipsLast;
	
	@BeforeEach
	public void setupClass()
	{
		aViewedTipsFirst = new ViewedTips(1);
		aViewedTipsLast = new ViewedTips(NUM_TIPS);
	}
	
	@Test 
	public void testViewedTips_getNextTipIdIncrementsForIdOne()
	{
		assertEquals(aViewedTipsFirst.getNextTipId(), 2); 
		// Assuming there are at least 2 tips, 
		// this is checked by TestTipJsons.java
	}
	
	@Test
	public void testViewedTips_getNextTipIdWrapsAround()
	{
		assertEquals(aViewedTipsLast.getNextTipId(), 1);
	}
	
	@Test 
	public void testViewedTips_getPreviousTipIdDecrementsForGreatestId()
	{
		assertEquals(aViewedTipsLast.getPreviousTipId(), NUM_TIPS - 1); 
		// Assuming there are at least 2 tips, 
		// this is checked by TestTipJsons.java
	}
	
	@Test
	public void testViewedTips_getPreviousTipIdWrapsAround()
	{
		assertEquals(aViewedTipsFirst.getPreviousTipId(), NUM_TIPS);
	}
	
	@Test 
	public void testViewedTips_newViewedTipsCorrectNextTipOfTheDay()
	{
		assertEquals(aViewedTipsFirst.getNewNextTipOfTheDayId(), 2);
		assertEquals(aViewedTipsLast.getNewNextTipOfTheDayId(), 1);
	}
	
	@Test 
	public void testViewedTips_getNextTipIdIncrementsTipOfTheDayIfLastNextTip()
	{
		int currentNextTipOfTheDayId = aViewedTipsLast.getNewNextTipOfTheDayId();
		
		assertTrue(currentNextTipOfTheDayId != NUM_TIPS);
		
		aViewedTipsLast.getNextTipId();
		int newNextTipOfTheDayId = aViewedTipsLast.getNewNextTipOfTheDayId();
		
		assertEquals(newNextTipOfTheDayId, currentNextTipOfTheDayId + 1); 
	}
	
	@Test
	public void testViewedTips_getNextTipNoIncrementIfNotLastNextTip()
	{
		
		int initialTipOfTheDayId = aViewedTipsLast.getNewNextTipOfTheDayId();
		
		try
		{
			Field currentTipId = ViewedTips.class.getDeclaredField("aCurrentTipId");
			currentTipId.setAccessible(true);
			currentTipId.set(aViewedTipsLast, 1);
		}
		catch (Exception e)
		{
			fail();
		}
		
		aViewedTipsLast.getNextTipId();
		
		assertTrue(aViewedTipsLast.getNewNextTipOfTheDayId() == initialTipOfTheDayId);
	}
}
