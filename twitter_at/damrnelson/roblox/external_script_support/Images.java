package twitter_at.damrnelson.roblox.external_script_support;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {

	public static BufferedImage addIcon;
	public static BufferedImage removeIcon;
	
	public static BufferedImage[] scriptIcons = new BufferedImage[5];
	
	public static void init() throws IOException {
		addIcon = ImageIO.read(Images.class.getResourceAsStream("imgs/AddIcon.png"));
		removeIcon = ImageIO.read(Images.class.getResourceAsStream("imgs/RemoveIcon.png"));
		
		scriptIcons[0] = ImageIO.read(Images.class.getResourceAsStream("imgs/Script16.png"));
		scriptIcons[1] = ImageIO.read(Images.class.getResourceAsStream("imgs/Script20.png"));
		scriptIcons[2] = ImageIO.read(Images.class.getResourceAsStream("imgs/Script32.png"));
		scriptIcons[3] = ImageIO.read(Images.class.getResourceAsStream("imgs/Script40.png"));
		scriptIcons[4] = ImageIO.read(Images.class.getResourceAsStream("imgs/Script64.png"));
	}
	
}
