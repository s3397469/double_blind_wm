
package au.edu.img.watermark;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import au.edu.img.tools.RandomUtils;

public class InsertGPWatermart {
	private static final int PATCH_SIZE = 7;
	private static int MAX_PIXEL_CHANGE_LIMIT = 15;
	static BufferedImage image;
	File sourceImage;
	static int finalImageWidth;
	static int finalImageHeight;
	static SPLIT_IMAGE_BAED_ON splitImageBasedOn;
	static final Map<PATCH_POSITON, Integer[][]> imagePixelRange = new HashMap<>();
	File markedImageFile;

	static enum SPLIT_IMAGE_BAED_ON {
		HEIGHT, WITDTH
	}

	static enum PATCH_POSITON {
		LEFT_PATCH, RIGHT_PATCH;
	}

	public InsertGPWatermart(String filePath) {
		readImage(filePath);
	}
	

	/*
	 * 1. Select non-overlapping two sets of pixels (Left patch, Right patch)
	 * 
	 * 2. Decide the pixel range for each Left and Right patch based on image HxW
	 * 
	 * 3. Randomly change pixel location (X,Y) for each patch 
	 * 
	 * Left patch : Change patch by adding number of pixels (random number) 
	 * Right patch: Change right patch by subtracting same amount of pixels
	 * 
	 */
	public File insertWatermark() {

		// 1. Select non-overlapping two sets of pixels (Left patch, Right patch)

		decideImageSplit();

		// 2. Decide the pixel range for each Left and Right patch based on image Height
		// & Width
		decidePixelRangeForEatchPatch();

		System.out.println("Patch A median BEFORE modifying = "
				+ calculateMedian(extractPixelArray(imagePixelRange.get(PATCH_POSITON.LEFT_PATCH))));

		System.out.println("Patch B median BEFORE modifying = "
				+ calculateMedian(extractPixelArray(imagePixelRange.get(PATCH_POSITON.RIGHT_PATCH))));

		// 3. Randomly select pixel location (X,Y) for each patch :
		changePixels();

		System.out.println("Patch A median AFTER modifying = "
				+ calculateMedian(extractPixelArray(imagePixelRange.get(PATCH_POSITON.LEFT_PATCH))));

		System.out.println("Patch B median AFTER modifying = "
				+ calculateMedian(extractPixelArray(imagePixelRange.get(PATCH_POSITON.RIGHT_PATCH))));

		return markedImageFile;
	}

	private void changePixels() {

		System.out.println("");
		int amoutToChange = generateRandomPixceCountToChange();
		System.out.println("Amount of Pixels that will be added/removed =" + amoutToChange);

		System.out.println("LEFT POSITION =" + Arrays.deepToString(imagePixelRange.get(PATCH_POSITON.LEFT_PATCH)));
		System.out.println("RIGHT POSITION =" + Arrays.deepToString(imagePixelRange.get(PATCH_POSITON.RIGHT_PATCH)));

		Integer[][] leftPixels = imagePixelRange.get(PATCH_POSITON.LEFT_PATCH);
		Integer[][] rightPixels = imagePixelRange.get(PATCH_POSITON.RIGHT_PATCH);
		//
		int existingPixVal = 0;

		System.out.println();
		for (int index = 0; index < leftPixels.length; index++) {
			//
			existingPixVal = image.getRGB(leftPixels[index][0], leftPixels[index][1]);
			image.setRGB(leftPixels[index][0], leftPixels[index][1], existingPixVal + amoutToChange);
			System.out
					.println("LEFT Pixel :: " + leftPixels[index][0] + " x " + leftPixels[index][1] + " changed from = "
							+ existingPixVal + " to : " + image.getRGB(leftPixels[index][0], leftPixels[index][1]));

			existingPixVal = image.getRGB(rightPixels[index][0], rightPixels[index][1]);
			image.setRGB(rightPixels[index][0], rightPixels[index][1], existingPixVal + amoutToChange);
			System.out.println(
					"RIGHT Pixel :: " + rightPixels[index][0] + " x " + rightPixels[index][1] + " changed from = "
							+ existingPixVal + " to : " + image.getRGB(rightPixels[index][0], rightPixels[index][1]));
		}

		try {
			System.out.println();
			markedImageFile = RandomUtils.generateOutputFile(sourceImage);
			ImageIO.write(image, "png", markedImageFile);
			System.out.println("Image with the watermark will be saved in : " + markedImageFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readImage(String imagePath) {
		try {

			System.out.println("Reading the image : " + imagePath);
			sourceImage = new File(imagePath);
			image = ImageIO.read(sourceImage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void decideImageSplit() {
		System.out.println("");
		System.out.println("Image W x H = " + image.getWidth() + " : " + image.getHeight());

		if (image.getWidth() > image.getHeight()) {

			// drawPatch(0, image.getHeight(), image.getWidth());
			splitImageBasedOn = SPLIT_IMAGE_BAED_ON.WITDTH;
			devideImageWidth(image.getHeight(), image.getWidth());
			System.out.println("Splatted based on WIDTH : ");

		} else {
			// drawPatch(image.getHeight(), image.getWidth()), 0);
			splitImageBasedOn = SPLIT_IMAGE_BAED_ON.HEIGHT;
			devideImageByHeight(image.getHeight(), image.getWidth());
			System.out.println("Splatted based on HEIGHT : ");

		}
		System.out.println("");
	}

	private void decidePixelRangeForEatchPatch() {

		Integer[][] leftPatch = null;
		Integer[][] rightPatch = null;

		if (splitImageBasedOn.equals(SPLIT_IMAGE_BAED_ON.WITDTH)) {

			leftPatch = meargeXYPixels(pickRandomPixels(PATCH_SIZE, 0, finalImageWidth),
					pickRandomPixels(PATCH_SIZE, 0, finalImageHeight));
			rightPatch = meargeXYPixels(pickRandomPixels(PATCH_SIZE, finalImageWidth + 1, image.getWidth()),
					pickRandomPixels(PATCH_SIZE, 0, finalImageHeight));

		} else {

			leftPatch = meargeXYPixels(pickRandomPixels(PATCH_SIZE, 0, finalImageWidth),
					pickRandomPixels(PATCH_SIZE, 0, finalImageHeight));
			rightPatch = meargeXYPixels(pickRandomPixels(PATCH_SIZE, finalImageHeight, image.getWidth()),
					pickRandomPixels(PATCH_SIZE, finalImageHeight, image.getHeight()));
		}

		imagePixelRange.put(PATCH_POSITON.LEFT_PATCH, leftPatch);
		imagePixelRange.put(PATCH_POSITON.RIGHT_PATCH, rightPatch);

	}

	private Integer[] pickRandomPixels(int pixelCount, int min, int max) {

		Set<Integer> values = new HashSet<>();

		while (values.size() < pixelCount) {
			values.add(ThreadLocalRandom.current().nextInt(min, max + 1));
		}

		List<Integer> valueList = new ArrayList<Integer>(values);
		Collections.shuffle(valueList);
		return valueList.toArray(new Integer[valueList.size()]);

	}

	private void devideImageByHeight(int height, int width) {
		finalImageHeight = Math.round(height / 2);
		finalImageWidth = width;
	}

	private void devideImageWidth(int height, int width) {
		finalImageWidth = Math.round(width / 2);
		finalImageHeight = height;
	}

	// Return a random number between 1 to configured maximum pixel change
	// limit.
	private int generateRandomPixceCountToChange() {
		return ThreadLocalRandom.current().nextInt(1, MAX_PIXEL_CHANGE_LIMIT + 1);
	}

	// Find the pixel based on X and Y position.
	private Integer[][] meargeXYPixels(Integer[] xPixcles, Integer[] yPixcles) {
		Integer[][] finalPixcels = new Integer[PATCH_SIZE][2];

		for (int index = 0; index < xPixcles.length; index++) {
			finalPixcels[index][0] = xPixcles[index];
			finalPixcels[index][1] = yPixcles[index];
		}

		return finalPixcels;
	}

	// Calculate the median based on a list of pixels
	private double calculateMedian(Integer[] pixelArray) {
		// Sort the pixels
		Arrays.sort(pixelArray);

		// for (Integer intx : pixelArray)
		// System.err.println(intx);

		int middle = pixelArray.length / 2;
		if (pixelArray.length % 2 == 1) {
			return pixelArray[middle];
		} else {
			return (pixelArray[middle - 1] + pixelArray[middle]) / 2.0;
		}
	}

	// Returns an integer pixel in the default RGB color model (TYPE_INT_ARGB)
	// for the provided X and Y locations.
	private int readSelectedPixels(int x, int y) {

		int rgbValue = image.getRGB(x, y);
		// Color pixColor = new Color(rgbValue);
		// System.out.println("Pixel :: " + x + " x " + y + " = " + rgbValue);
		return rgbValue;

	}

	// extract RGB value of pixels
	private Integer[] extractPixelArray(Integer[][] sourcePixcelPositions) {

		Integer[] pixcelArray = new Integer[sourcePixcelPositions.length];

		for (int index = 0; index < sourcePixcelPositions.length; index++)
			pixcelArray[index] = readSelectedPixels(sourcePixcelPositions[index][0], sourcePixcelPositions[index][1]);

		return pixcelArray;
	}

	static public void main(String args[]) throws Exception {

		if (null == args || args.length < 1) {
			System.err.println(
					"Invalid input. Please provide a valid path to read the image files. \n\t e.g. /user/home/testImage.jpg");
			return;
		} else if (RandomUtils.isValidPath(args[0])) {
			new InsertGPWatermart(args[0]).insertWatermark();

		} else {
			System.exit(500);
		}

	}
}