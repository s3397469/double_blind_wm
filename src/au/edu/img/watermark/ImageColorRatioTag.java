package au.edu.img.watermark;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import au.edu.img.scramble.RandomPixelSwappingScrambler;
import au.edu.img.tools.RandomUtils;

public class ImageColorRatioTag {

	File imageSourceFile = null;
	BufferedImage bufferdImageSourceFile = null;
	int width = 0, heigth = 0;
	int threshold = 0;
	Set<RGBPixselPlaceHolder> uniqueColorSet = null;

	List<RGBPlaceHolder> uniqueColors = new ArrayList<>();

	public ImageColorRatioTag(String imagePath) throws IOException {
		this.imageSourceFile = RandomUtils.loadFile(imagePath);
		this.bufferdImageSourceFile = ImageIO.read(this.imageSourceFile);
		this.width = bufferdImageSourceFile.getWidth();
		this.heigth = bufferdImageSourceFile.getHeight();
	}

	public static void main(String[] args) throws Exception {

		
		if (null == args || args.length < 1) {
			System.err.println(
					"Invalid input. Please provide a valid path to read the image file. "
							+ "\n\t e.g. /user/home/testImage.jpg");
			return;
		} else if (RandomUtils.isValidPath(args[0])) {


			ImageColorRatioTag imageTag = new ImageColorRatioTag(args[0]);

			try {
				imageTag.threshold = Integer.parseInt(args[1]);
			} catch (Exception ex) {
				imageTag.threshold = 20;
				System.err.println("Provided Watarmark threshold is incorrect or empty, setting the default as 20%");
			}

			imageTag.startProcess();
		} else {
			System.exit(500);
		}
		
	}

	private void startProcess() throws Exception {

		// 1 Traverse the image, extract all the pixels with meta-data.
		loadPixelMetaData(bufferdImageSourceFile);
		System.out.println("Image size : H =" + getHeigth() + " x W =" + getWidth() + ", total unique pix's = "
				+ uniqueColors.size());

		// 2 Find pix's to change within the image based on T (threshold)
		findPixelFrequencyWithinImage();

		// 3 Randomly select the pix's and change the pix's
		applyChangesToImage();
	}

	private void applyChangesToImage() {

		// 1 select random pixs from list of unique pixs

//		for (RGBPlaceHolder uniqueColor : uniqueColors) {
		RGBPlaceHolder uniqueColor = uniqueColors.get(0);

		if(uniqueColor!=null){
//			// use of the Fisher–Yates shuffle with a Randomly permute
			 Collections.shuffle( (List<?>) uniqueColor.getPixPositions(),
			 RandomUtils.generateRadomUseingSeed(null));

			// 3 Add or deduct the RGB
			for (int index = 0; index < uniqueColor.getPixCountToBeChanged(); index++) {

//				if (index % 3 == 0)
					RandomUtils.modifyImageRGBValue(bufferdImageSourceFile, uniqueColor.getPixPositions().get(index),
							RandomUtils.PixelChangeOperation.ADD, 1);
//				else
//					RandomUtils.modifyImageRGBValue(bufferdImageSourceFile, uniqueColor.getPixPositions().get(index),
//							RandomUtils.PixelChangeOperation.SUBSTRACT, 5);
			}
		}

		// 3 Saving the new file
		RandomUtils.saveBuffedImage(bufferdImageSourceFile, imageSourceFile);

	}

	// Group each pix. based on the RGB color and find the frequency of
	// appearance
	private void findPixelFrequencyWithinImage() {

		for (RGBPlaceHolder uniqueColor : uniqueColors) {

			String frequencyRatio = calculatePixelAppearanceRatio(uniqueColor.getRepetitionCount());
			uniqueColor.setFrequencyRatio(frequencyRatio);

			double changRatio = RandomUtils.calculateRoundedChangeRatio(uniqueColor, 20);
			uniqueColor.setPixCountToBeChanged(RandomUtils.calculateRoundedPixCountToChange(uniqueColor, changRatio));

			System.out.println("\t" + String.format(
					"" + "Color : {%d} , Total Pix. Count {%d} , RGB Frequency ratio {%s}%% , Pix. Change Ratio {%.2f}%% , Pix. Change count {%.2f}",
					uniqueColor.grbValue, uniqueColor.getRepetitionCount(), uniqueColor.getRoundedFrequencyRatio(),
					changRatio, uniqueColor.getPixCountToBeChanged()));
		}

	}

	// Calculate the unique RGB color appearance ratio in the image.
	private String calculatePixelAppearanceRatio(int pixFrequencyCount) {

		float totPix = getHeigth() * getWidth();

		if (pixFrequencyCount == 0 || pixFrequencyCount > totPix)
			return "0";

		float percentage = 100 * pixFrequencyCount / totPix;
		// System.out.println(percentage + " = " + Math.round(percentage) +
		// "%");
		NumberFormat formatter = new DecimalFormat("0.00");
		return (formatter.format(percentage));
	}

	// Browse through all the pixs in the image, wrap them in a place holder
	// object with additional information.
	// Add all the pix. information to a list containing all the pixcles.
	private void loadPixelMetaData(BufferedImage image) throws IOException {

		// Path path =
		// Paths.get("C:\\Users\\ul2d\\Documents\\workspace-sts-3.7.2.RELEASE\\db_img_blind\\src\\res\\output.txt");

		// Use try-with-resource to get auto-closeable writer instance
		// try (BufferedWriter writer = Files.newBufferedWriter(path)) {

		for (int xAxis = 0; xAxis < this.width; xAxis++) {

			for (int yAxis = 0; yAxis < this.heigth; yAxis++) {
				Color color = new Color(image.getRGB(xAxis, yAxis));

				// writer.write("\nReading X x Y : " + xAxis + " " + yAxis);

				RGBPlaceHolder tmpRGBObject = new RGBPlaceHolder(xAxis, yAxis, color.getRGB());
				int index = uniqueColors.indexOf(tmpRGBObject);
				if (index >= 0) {
					uniqueColors.get(index).addNewPixPosition(xAxis, yAxis);
				} else
					uniqueColors.add(tmpRGBObject);

				// }

			}
		}

	}

	public File getImageSourceFile() {
		return imageSourceFile;
	}

	public void setImageSourceFile(File imageSourceFile) {
		this.imageSourceFile = imageSourceFile;
	}

	public BufferedImage getBufferdImageSourceFile() {
		return bufferdImageSourceFile;
	}

	public void setBufferdImageSourceFile(BufferedImage bufferdImageSourceFile) {
		this.bufferdImageSourceFile = bufferdImageSourceFile;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeigth() {
		return heigth;
	}

	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

}
