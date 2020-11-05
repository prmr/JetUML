package ca.mcgill.cs.jetuml.gui.tips;

import static ca.mcgill.cs.jetuml.gui.tips.TipLoader.NUM_TIPS;

/**
 * Class holding the tip id logic related to generating the next or previous
 * tip ids and the id of the new next tip of the day.
 */
public class ViewedTips 
{
	private int aCurrentTipId;
	
	private int aNewNextTipOfTheDayId;
	
	/**
	 * @param pFirstTipOfTheDayId the id of the first loaded tip of the day 
	 * @pre pId >= 1 && pId <= NUM_TIPS
	 */
	public ViewedTips(int pFirstTipOfTheDayId)
	{
		assert pFirstTipOfTheDayId >= 1 && pFirstTipOfTheDayId <= NUM_TIPS;
		
		aCurrentTipId = pFirstTipOfTheDayId;
		
		if (pFirstTipOfTheDayId == NUM_TIPS)
		{
			aNewNextTipOfTheDayId = 1;
		}
		else
		{
			aNewNextTipOfTheDayId = pFirstTipOfTheDayId + 1;
		}
	}
	
	/**
	 * Updates the current tip id to the next tip id and the id of the next tip of
	 * the day to the id of the new next tip of the day if necessary, and returns
	 * the new current tip id.
	 * @return next tip's id
	 */
	public int getNextTipId()
	{
		updateCurrentTipIdToNextTipId();
		updateNextTipOfTheDayId();
		return aCurrentTipId;
	}
	
	/**
	 * Updates the current tip id to the previous tip id, and returns the new 
	 * current tip id.
	 * @return previous tip's id
	 */
	public int getPreviousTipId()
	{
		updateCurrentTipIdToPreviousTipId();
		return aCurrentTipId;
	}
	
	/**
	 * @return the id of the new next tip of the day
	 */
	public int getNewNextTipOfTheDayId()
	{
		return aNewNextTipOfTheDayId;
	}
	
	private void updateCurrentTipIdToNextTipId()
	{
		if (aCurrentTipId == NUM_TIPS)
		{
			aCurrentTipId = 1;
		}
		else
		{
			aCurrentTipId = aCurrentTipId + 1;
		}
	}
	
	private void updateCurrentTipIdToPreviousTipId()
	{
		if (aCurrentTipId == 1)
		{
			aCurrentTipId = NUM_TIPS;
		}
		else
		{
			aCurrentTipId = aCurrentTipId - 1;
		}
	}
	
	/**
	 * Checks if the tip of the day's id has been reached, and if so, updates 
	 * the id of the next tip of the day to the next id.
	 */
	private void updateNextTipOfTheDayId()
	{
		if (aCurrentTipId == aNewNextTipOfTheDayId)
		{
			if (aNewNextTipOfTheDayId == NUM_TIPS)
			{
				aNewNextTipOfTheDayId = 1;
			}
			else
			{
				aNewNextTipOfTheDayId = aNewNextTipOfTheDayId + 1;
			}
		}
	}
}
