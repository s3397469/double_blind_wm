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
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;

import au.edu.img.tools.RandomUtils;

public class ImageColorRatioTag {

	File imageSourceFile = null;
	BufferedImage bufferdImageSourceFile = null;
	int width = 0, heigth = 0;
	int threshold = 0;
	Set<RGBPixselPlaceHolder> uniqueColorSet = null;
	static int PIXCEL_CHANGE_AMOUNT = 5;
	static int THRESHOLD=20;

	List<RGBPlaceHolder> uniqueColors = new ArrayList<>();

	static Logger LOGGER = Logger.getLogger(ImageColorRatioTag.class.getName());

	public ImageColorRatioTag(String imagePath) throws IOException {
		this.imageSourceFile = RandomUtils.loadFile(imagePath);
		this.bufferdImageSourceFile = ImageIO.read(this.imageSourceFile);
		this.width = bufferdImageSourceFile.getWidth();
		this.heigth = bufferdImageSourceFile.getHeight();
	}

	public static void main(String[] args) throws Exception {

		if (null == args || args.length < 1) {
			System.err.println("Invalid input. Please provide a valid path to read the image file. "
					+ "\n\t e.g. /user/home/testImage.jpg");
			return;
		} else if (RandomUtils.isValidPath(args[0])) {

			ImageColorRatioTag imageTag = new ImageColorRatioTag(args[0]);

			initLogFile(LOGGER, imageTag.imageSourceFile.getAbsolutePath());

			try {
				imageTag.threshold = Integer.parseInt(args[1]);
			} catch (Exception ex) {
				imageTag.threshold = THRESHOLD;
				LOGGER.warning("Provided Watarmark threshold is incorrect or empty, setting the default as "+ THRESHOLD);
			}

			imageTag.startProcess();

		} else {
			System.exit(500);
		}

	}

	private void startProcess() throws Exception {

		// 1 Traverse the image, extract all the pixels with meta-data.
		loadPixelMetaData(bufferdImageSourceFile);
		LOGGER.info("Source image meta-data loaded,  size : H =" + getHeigth() + " x W =" + getWidth()
				+ ", total unique pix's = " + uniqueColors.size());

		// 2 Find pix's to change within the image based on T (threshold)
		findPixelFrequencyWithinImage();

		// 3 Randomly select the pix's and change the pix's (embed the
		// Watermark)
		embedWatermark();

		// 4 Save the output image
		File outPutImage = RandomUtils.saveBuffedImage(bufferdImageSourceFile, imageSourceFile);
		if (outPutImage == null) {
			LOGGER.warning("Watermark generation failed!");
			return;
		}

		LOGGER.info("Image with the watermark will be saved in : " + outPutImage.getAbsolutePath());

		// 5 Re-genarate the pixel meta-data and save to a private key
		this.bufferdImageSourceFile = ImageIO.read(outPutImage);
		loadPixelMetaData(bufferdImageSourceFile);
		LOGGER.info("Watermarked image meta-data loaded,  size : H =" + getHeigth() + " x W =" + getWidth()
				+ ", total unique pix's = " + uniqueColors.size());

		// 6 Generate the secured key pair and encrypt the watermarked image
		// meta-data
		generateKeyPair();
	}

	private void generateKeyPair() {
		// TODO Generate pri/pub key
		// Read meta-data and save in an encrypted file.
	}

	private void embedWatermark() {

		// 1 select random pixs from list of unique pixs

		for (RGBPlaceHolder uniqueColor : uniqueColors) {
			// RGBPlaceHolder uniqueColor = uniqueColors.get(0);

			if (uniqueColor != null) {
				// // use of the Fisher�Yates shuffle with a Randomly permute
				Collections.shuffle((List<?>) uniqueColor.getPixPositions(), RandomUtils.generateRadomUseingSeed(null));

				// 3 Add or deduct the RGB
				for (int index = 0; index < uniqueColor.getPixCountToBeChanged(); index++) {

					if (index % 3 == 0 && uniqueColor.getRGB() > 0)
						RandomUtils.modifyImageRGBValue(bufferdImageSourceFile,
								uniqueColor.getPixPositions().get(index), RandomUtils.PixelChangeOperation.ADD,
								PIXCEL_CHANGE_AMOUNT);
					else
						RandomUtils.modifyImageRGBValue(bufferdImageSourceFile,
								uniqueColor.getPixPositions().get(index), RandomUtils.PixelChangeOperation.SUBSTRACT,
								PIXCEL_CHANGE_AMOUNT);
				}
			}
		}

	}

	// Group each pix. based on the RGB color and find the frequency of
	// appearance
	private void findPixelFrequencyWithinImage() {

		for (RGBPlaceHolder uniqueColor : uniqueColors) {

			String frequencyRatio = calculatePixelAppearanceRatio(uniqueColor.getRepetitionCount());
			uniqueColor.setFrequencyRatio(frequencyRatio);

			double changRatio = RandomUtils.calculateRoundedChangeRatio(uniqueColor, 20);
			uniqueColor.setChangRatio(changRatio);
			uniqueColor.setPixCountToBeChanged(RandomUtils.calculateRoundedPixCountToChange(uniqueColor));

			LOGGER.info(uniqueColor.toString());
		}

	}

	// Calculate the unique RGB color appearance ratio in the image.
	private String calculatePixelAppearanceRatio(int pixFrequencyCount) {

		float totPix = getHeigth() * getWidth();

		if (pixFrequencyCount == 0 || pixFrequencyCount > totPix)
			return "0";

		float percentage = 100 * pixFrequencyCount / totPix;
		// LOGGER.info(percentage + " = " + Math.round(percentage) +
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

	// UTIL

	public static void initLogFile(Logger logger, String sourceImage) {
		FileHandler fileHandler;

		try {
			fileHandler = new FileHandler(sourceImage.concat(".log"));
			logger.addHandler(fileHandler);
			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);
		} catch (Exception e) {
			System.err.println("Error while creating the log file in : " + sourceImage);
			e.printStackTrace();
			logger.setUseParentHandlers(false);
		}
	}
}