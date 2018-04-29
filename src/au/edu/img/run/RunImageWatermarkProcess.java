package au.edu.img.run;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import au.edu.img.scramble.RandomPixelSwappingScrambler;
import au.edu.img.tools.RandomUtils;

public class RunImageWatermarkProcess extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	enum ImageLocation {
		SOURCE_IMG, SCRAMBLED_IMG, WATERMARKD_IMG
	}

	RandomPixelSwappingScrambler imageScrambler;

	public RunImageWatermarkProcess(RandomPixelSwappingScrambler imageScrambler, File markedImageFile) {

		this.imageScrambler = imageScrambler;
		this.setSize(800, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("s3397469 Image watermarking ");

		this.setLayout(new GridLayout(1, 3));
		this.add(obtainImageLabel(imageScrambler.getOriginalImagePath(), ImageLocation.SOURCE_IMG));
		this.add(obtainImageLabel(imageScrambler.getScrambeldImage().getAbsolutePath(), ImageLocation.SCRAMBLED_IMG));
		this.add(obtainImageLabel(markedImageFile.getAbsolutePath(), ImageLocation.WATERMARKD_IMG));
		// this.add(obtainImageMetadata());
		// this.add(obtainImageMetadata());
		this.pack();
		this.setVisible(true);

	}

	private JLabel obtainImageLabel(String path, ImageLocation imageLocation) {
		ImageIcon imageIcon = new ImageIcon(path);

		JLabel imageLabel = getAppropriateLabel(imageLocation);

		imageLabel.setIcon(new ImageIcon(getScaledImage(imageIcon.getImage(), 350, 350)));
		imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		imageLabel.setVerticalAlignment(JLabel.CENTER);

		return imageLabel;
	}

	private JLabel getAppropriateLabel(ImageLocation imageLocation) {
		switch (imageLocation) {
		case SOURCE_IMG:
			return new JLabel(" Source Image");
		case SCRAMBLED_IMG:
			return new JLabel(" Scrambled Image");
		case WATERMARKD_IMG:
			return new JLabel(" Watermarkd Image");
		default:
			return new JLabel("");
		}
	}

	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}

	// TODO : validate the image dominations are valid
	public static void main(String[] args) throws IOException {

		if (null == args || args.length < 3) {
			System.err.println(
					"Invalid input. Please provide a valid path to read the image file and dimension (Rows/Cols) . "
							+ "\n\t e.g. /user/home/testImage.jpg" + "\n\t e.g. 50 20");
			return;
		} else if (RandomUtils.isValidPath(args[0])) {
			init(args[0], args[1], args[2]);
		} else {
			System.exit(500);
		}

	}

	private static void init(String imagePath, String rows, String cols) throws IOException {
		//
		// // Steps
		//
		// // 1 Load the original image
		// RandomPixelSwappingScrambler imageScrambler = new
		// RandomPixelSwappingScrambler(imagePath, Integer.valueOf(rows),
		// Integer.valueOf(cols));
		//
		// // 2 Scramble the original image
		// imageScrambler.scrambleImage();
		//
		// // TODO : 3 Load image meta-data and show
		//
		// // 4 Add the watermark to scrambled image
		// File markedImageFile = new
		// InsertGPToImg(imageScrambler.getScrambeldImage().getAbsolutePath()).insertWatermark();
		//
		// // TODO : 5 Extract the watermark
		//
		// new RunImageWatermarkProcess(imageScrambler,markedImageFile);
	}

}