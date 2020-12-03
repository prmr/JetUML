/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
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
