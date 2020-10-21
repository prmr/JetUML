package ca.mcgill.cs.jetuml.gui;

import java.util.Optional;

/**
 * A media type for a tip element.
 */
public enum Media 
{
	TEXT, IMAGE;
	
	/**
	 * Returns the Media object that matches the given media name (ignoring case)
	 * if one matches. Returns Optional.empty() if pMediaName is null.
	 * 
	 * @param pMediaName the name of the media
	 * @return Media object matching the given name.
	 */
	public static Optional<Media> getMedia(String pMediaName)
	{
		for(Media media : Media.values())
		{
			String mediaName = media.name();
			if(mediaName.equalsIgnoreCase(pMediaName))
			{
				return Optional.of(media);
			}
		}
		return Optional.empty();
	}
}