package org.jetuml.geom;

/**
 * How to position some text relative to a bounding box.
 */
public enum TextPosition
{
	TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

	/**
	 * @return true iif this object represents a top position.
	 */
	public boolean isTop()
	{
		return this == TOP_LEFT || this == TOP_CENTER || this == TOP_RIGHT;
	}

	/**
	 * @return true iif this object represents a center position in the vertical axis.
	 */
	public boolean isVerticallyCentered()
	{
		return this == CENTER_LEFT || this == CENTER_CENTER || this == CENTER_RIGHT;
	}

	/**
	 * @return true iif this object represents a bottom position.
	 */
	public boolean isBottom()
	{
		return this == BOTTOM_LEFT || this == BOTTOM_CENTER || this == BOTTOM_RIGHT;
	}

	/**
	 * @return true iif this object represents a left position.
	 */
	public boolean isLeft()
	{
		return this == TOP_LEFT || this == CENTER_LEFT || this == BOTTOM_LEFT;
	}
	
	/**
	 * @return true iif this object represents a right position.
	 */
	public boolean isRight()
	{
		return this == TOP_RIGHT || this == CENTER_RIGHT || this == BOTTOM_RIGHT;
	}

	/**
	 * @return true iif this object represents a centered position in the horizontal axis.
	 */
	public boolean isHorizontallyCentered()
	{
		return this == TOP_CENTER || this == CENTER_CENTER || this == BOTTOM_CENTER;
	}
	
	/**
	 * Compute and return an anchor of the text, within the bounding box.
	 * 
	 * @param pBoundingBox The box containing the text. 
	 * @return A point to use as the anchor for drawing the text.
	 */
	public Point getAnchor(Rectangle pBoundingBox)
	{
		int x = pBoundingBox.x();
		if (isHorizontallyCentered())
		{
			x = pBoundingBox.center().x();
		}
		else if (isRight())
		{
			x = pBoundingBox.maxX();
		}
		int y = pBoundingBox.maxY();
		if (isVerticallyCentered())
		{
			y = pBoundingBox.center().y();
		}
		else if (isTop())
		{
			y = pBoundingBox.y();
		}
		return new Point(x, y);
	}
}