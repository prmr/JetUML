/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.application;

/**
 * A zoom helps convert coordinates between two system: a base
 * system, and one that can be zoomed. The zoom level determines
 * the amount of scaling. The identity zoom level is 0 
 * (no zooming). Positive zoom levels are normally
 * associated with zooming in. Negative zoom levels lesser are
 * normally associated with zooming out. The zoom factor
 * is the amount of scaling to do. It is a function of the 
 * zoom level and a constant zoom increment. There is also
 * a max and a min possible zoom level, which is the same
 * for all zooms.
 * 
 * @author Martin P. Robillard
 */
public class Zoom
{
	private static final double ZOOM_FACTOR_INCREMENT = Math.sqrt(2);
	private static final int MAX_LEVELS = 4;
	
	private static final double[] LEVELS = new double[MAX_LEVELS * 2 + 1];
	
	private int aZoomLevelIndex = MAX_LEVELS;
	
	static // Initialize factors
	{
		for( int i = 0; i < MAX_LEVELS * 2 + 1; i++ )
		{
			LEVELS[i] = levelToFactor(-(MAX_LEVELS - i));
		}
	}
	
	private static double levelToFactor(int pLevel)
	{
		if( pLevel == 0 )
		{
			return 1;
		}
		else if( pLevel < 0 )
		{
			return -1.0 / (pLevel * ZOOM_FACTOR_INCREMENT);
		}
		else // pLevel > 0
		{
			return pLevel * ZOOM_FACTOR_INCREMENT;
		}
	}
	
	/**
	 * Increases the zoom level by one step.
	 */
	public void increaseLevel()
	{
		aZoomLevelIndex = Math.min(LEVELS.length-1, aZoomLevelIndex + 1);
	}
	
	/**
	 * Decreases the zoom level by one step.
	 */
	public void decreaseLevel()
	{
		aZoomLevelIndex = Math.max(0, aZoomLevelIndex - 1);
	}
	
	/**
	 * @return The zoom factor that corresponds
	 * to the current zoom level.
	 */
	public double factor()
	{
		return LEVELS[aZoomLevelIndex];
	}
	
	@Override
	public String toString()
	{
		return String.format("[Zoom: level=%d]", aZoomLevelIndex - MAX_LEVELS + 1);
	}
}
