/*
 ****************************************************************************
 *
 * Copyright (c)2018 The Vanguard Group of Investment Companies (VGI)
 * All rights reserved.
 *
 * This source code is CONFIDENTIAL and PROPRIETARY to VGI. Unauthorized
 * distribution, adaptation, or use may be subject to civil and criminal
 * penalties.
 *
 ****************************************************************************
 Module Description:

 $HeadURL:$
 $LastChangedRevision:$
 $Author:$
 $LastChangedDate:$
*/
package au.edu.img.run;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class Test {

	public static void main(String[] args) throws URISyntaxException {

		Color myWhite = new Color(255, 255, 255); // Color white
		int rgb = myWhite.getRGB();

		try {
			BufferedImage img = null;
			try {
				Path path = Paths.get(Test.class.getResource("/res/saved.png").toURI());

				img = ImageIO.read(new File(path.toString()));
			} catch (IOException e) {
			}

			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {

					Color c = new Color(img.getRGB(x, y));

					System.out.println("RGB : " + img.getRGB(x, y));
					Color col = new Color(c.getRed() + 5, c.getGreen() + 5, c.getBlue() + 5);

					img.setRGB(x, y, col.getRGB());
				}
			}

			// retrieve image
			File outputfile = new File("saved.png");
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
		}
		// Path path =
		// Paths.get(Test.class.getResource("/res/basic_colors.png").toURI());
		// File f = new File(path.toString());
		// System.err.println( f.getAbsolutePath());
	}

}
