package ca.mcgill.cs.jetuml.gui;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.io.InputStream;

/**
 * Tip element representing an image. 
 */
public class TipImage implements TipElement 
{
	private String aImage;
	
	/**
	 * @param pImage a string of the image name (with file format);
	 */
	public TipImage(String pImage)
	{
		aImage = pImage;
	}
	
	/**
	 * @return node containing the image
	 */
	public Node getAsNode() 
	{
		String tipImagesDir = RESOURCES.getString("tips.images.path");
		InputStream inputStream = TipImage.class.getResourceAsStream(tipImagesDir + aImage);
		Image image = new Image(inputStream);
		ImageView imageNode = new ImageView(image);
		return imageNode;
	}
}
