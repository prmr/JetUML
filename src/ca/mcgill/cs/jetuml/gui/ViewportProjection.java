/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.gui;

/**
 * An instance of this class can be used to make viewport projection
 * computations to compare visible areas of a scrollpane viewport with an
 * underlying canvas. Instances of this class are intended to be used in
 * a narrow scope: to be initialized, used to perform calculations, 
 * then discarded. Both dimensions of the viewport must be equal to or
 * smaller than the canvas's.
 */
public final class ViewportProjection
{
	private final int aViewportWidth;
	private final int aViewportHeight;
	private final int aCanvasWidth;
	private final int aCanvasHeight;
	private final double aHValue;
	private final double aVValue;
	
	/**
	 * Creates an immutable ViewportProjection.
	 * 
	 * @param pViewportWidth The width of the viewport.
	 * @param pViewportHeight The height of the viewport.
	 * @param pCanvasWidth The width of the canvas.
	 * @param pCanvasHeight The height of the canvas.
	 * @param pHValue The horizontal position of the viewport over the canvas, from 0 to 1.
	 * @param pVValue The vertical position of the viewport over the canvas from 0 to 1.
	 */
	public ViewportProjection(int pViewportWidth, int pViewportHeight, int pCanvasWidth, int pCanvasHeight, double pHValue, double pVValue)
	{
		assert pViewportWidth >= 0 && pViewportHeight >=0 && pCanvasWidth >=0 && pCanvasHeight >= 0;
		assert pViewportWidth <= pCanvasWidth && pViewportHeight <= pCanvasHeight;
		assert pHValue >=0 && pHValue <= 1;
		assert pVValue >=0 && pVValue <= 1;
		aViewportWidth = pViewportWidth;
		aViewportHeight = pViewportHeight;
		aCanvasWidth = pCanvasWidth;
		aCanvasHeight = pCanvasHeight;
		aHValue = pHValue;
		aVValue = pVValue;
	}
	
	/**
	 * @return The ratio (in the unit interval) of the canvas width covered by 
	 * the viewport.
	 */
	public double getWidthRatio()
	{
		return aViewportWidth / (double) aCanvasWidth;
	}
	
	/**
	 * @return The ratio (in the unit interval) of the canvas height covered by 
	 * the viewport.
	 */
	public double getHeightRatio()
	{
		return aViewportHeight / (double) aCanvasHeight;
	}
	
	/**
	 * @return The number of pixels on the left of the canvas that
	 * are not visible in the viewport.
	 */
	public int getHiddenLeft()
	{
		double hiddentLeft = hiddenWidth() * aHValue;
		return Math.round((float) hiddentLeft);
	}
	
	/**
	 * @return The number of pixels on the right of the canvas that
	 * are not visible in the viewport.
	 */
	public int getHiddenRight()
	{
		double hiddenRight = hiddenWidth() * (1.0-aHValue);
		return Math.round((float) hiddenRight);
	}
	
	/**
	 * @return The number of pixels at the top of the canvas that
	 * are not visible in the viewport.
	 */
	public int getHiddenTop()
	{
		double hiddenTop = hiddenHeight() * aVValue;
		return Math.round((float) hiddenTop);
	}
	
	/**
	 * @return The number of pixels at the bottom of the canvas that
	 * are not visible in the viewport.
	 */
	public int getHiddenBottom()
	{
		double hiddenBottom = hiddenHeight() * (1.0 - aVValue);
		return Math.round((float) hiddenBottom); 
	}
	
	private int hiddenHeight()
	{
		return aCanvasHeight - aViewportHeight;
	}
	
	private int hiddenWidth()
	{
		return aCanvasWidth - aViewportWidth;
	}
	
	/**
	 * @param pX A x-coordinate on the canvas.
	 * @return True if the coordinate is hidden on the left of the viewport.
	 */
	public boolean isHiddenLeft(int pX)
	{
		assert pX >=0 && pX <= aCanvasWidth;
		return pX < getHiddenLeft();
	}
	
	/**
	 * @param pX A x-coordinate on the canvas.
	 * @return True if the coordinate is hidden on the right of the viewport.
	 */
	public boolean isHiddenRight(int pX)
	{
		assert pX >=0 && pX <= aCanvasWidth;
		return pX > aCanvasWidth - getHiddenRight();
	}
	
	/**
	 * @param pY A y-coordinate on the canvas.
	 * @return True if the coordinate is hidden on the top of the viewport.
	 */
	public boolean isHiddenTop(int pY)
	{
		assert pY >=0 && pY <= aCanvasHeight;
		return pY < getHiddenTop();
	}
	
	/**
	 * @param pY A y-coordinate on the canvas.
	 * @return True if the coordinate is hidden on the bottom of the viewport.
	 */
	public boolean isHiddenBottom(int pY)
	{
		assert pY >=0 && pY <= aCanvasHeight;
		return pY > aCanvasHeight - getHiddenBottom();
	}
	
	/**
	 * @param pX An x-coordinate to reveal.
	 * @return A newly computed HValue that will ensure pX
	 * is just visible in the viewport.
	 */
	public double getAdjustedHValueToRevealX(int pX)
	{
		if( isHiddenLeft(pX) )
		{
			return pX / (double) hiddenWidth();
		}
		else if( isHiddenRight(pX) )
		{
			return 1 - ((aCanvasWidth- pX) / (double) hiddenWidth());
		}
		else
		{
			return aHValue;
		}
	}
	
	/**
	 * @param pY A y-coordinate to reveal.
	 * @return A newly computed VValue that will ensure pY
	 * is just visible in the viewport.
	 */
	public double getAdjustedVValueToRevealY(int pY)
	{
		if( isHiddenTop(pY) )
		{
			return pY / (double) hiddenHeight();
		}
		else if( isHiddenBottom(pY) )
		{
			return 1 - ((aCanvasHeight- pY) / (double) hiddenHeight());
		}
		else
		{
			return aVValue;
		}
	}
	
	@Override
	public String toString()
	{
		return String.format("[ViewportProjection: vp=%dx%d; canvas=%dx%d; pos=%.2f, %.2f]", aViewportWidth, aViewportHeight,
				aCanvasWidth, aCanvasHeight, aHValue, aVValue);
	}
}
