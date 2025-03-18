package org.jetuml.geom;

/**
 * How to position some text relative to a bounding box.
 */
public enum Alignment
{
	LEFT, CENTER;

	/**
	 * Compute and return an anchor of the text, within the bounding box.
	 * 
	 * @param pBoundingBox The box containing the text. 
	 * @return A point to use as the anchor for drawing the text.
	 */
	public Point getAnchor(Rectangle pBoundingBox)
	{
		int x = pBoundingBox.x();
		if (this == CENTER)
		{
			x = pBoundingBox.center().x();
		}
		int y = pBoundingBox.maxY();
		return new Point(x, y);
	}
}