package ca.mcgill.cs.jetuml.gui.tips;

import java.util.Optional;

/**
 * A media type for a tip element.
 */
public enum Media 
{
	TEXT, IMAGE;
	
	/**
	 * Returns the Media object that matches the given media name (ignoring case)
	 * if one matches. Returns Optional.empty() if pMediaName is null or no Media
	 * object matches with the given name.
	 * 
	 * @param pMediaName the name of the media
	 * @return Media object matching the given name or Optional.empty() if no matching 
	 * 		   Media object is found.
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
