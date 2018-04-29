
package au.edu.img.scramble;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import au.edu.img.tools.RandomUtils;

public class RandomPixelSwappingScrambler {

	File sourceImage;
	File scrambeldImage;
	File unScrambledImage;
	String originalImagePath;
	String randomSeed;
	Set<Integer> imageIndexMap;
	BufferedImage sourceImageBuffer;
	BufferedImage destImageBuffer;

	int rows = 10; // = Height
	int cols = 10; // = Width
	int chunks = 0; // = BlockSize

	// TODO : validate the image dominations are valid
	public static void main(String[] args) throws IOException {

		if (null == args || args.length < 3) {
			System.err.println(
					"Invalid input. Please provide a valid path to read the image file and dimension (Rows/Cols) . "
							+ "\n\t e.g. /user/home/testImage.jpg" + "\n\t e.g. 50 20");
			return;
		} else if (RandomUtils.isValidPath(args[0])) {

			String seed = "";

			if (args.length >= 4 && args[3] != null)
				seed = args[3];

			new RandomPixelSwappingScrambler(args[0], seed, Integer.valueOf(args[1]), Integer.valueOf(args[2]))
					.scrambleImage().unScrambleImage();
		} else {
			System.exit(500);
		}

	}

	String[] getSourceImgMetaData() {
		return new String[] { "Image dimension", sourceImageBuffer.getHeight() + "x" + sourceImageBuffer.getWidth(),
				"Image Color Mode", sourceImageBuffer.getColorModel().toString(), "Image Compression", "" };

	}

	RandomPixelSwappingScrambler(String originalImagePath, final String randomSeed, int rows, int cols) {

		System.out.println("STARTING  Random Pixel Swapping Scrambler ....");

		this.originalImagePath = originalImagePath;
		this.sourceImage = new File(this.originalImagePath);
		this.randomSeed = randomSeed;

		if (rows > 0)
			this.rows = rows;
		if (cols > 0)
			this.cols = cols;

		chunks = rows * cols;

		System.out.println("BLOCK SIZE : " + chunks);

	}

	void unScrambleImage() throws IOException {

		System.out.println("STARTING : Unscrmbler on " + scrambeldImage.getAbsolutePath());

		List<BufferedImage> splitedImageList = splitImageIntoBlocks(ImageIO.read(scrambeldImage));

		List<BufferedImage> outputImage = reArrangeImage(imageIndexMap, splitedImageList);

		unScrambledImage = saveOutputImage(joinBufferedImage(outputImage), false);
	}

	RandomPixelSwappingScrambler scrambleImage() throws IOException {

		System.out.println("STARTING : Scrmbler on " + sourceImage.getAbsolutePath());

		sourceImageBuffer = ImageIO.read(sourceImage);

		List<BufferedImage> originalList = splitImageIntoBlocks(sourceImageBuffer);

		List<BufferedImage> shuffledList = new ArrayList<BufferedImage>(originalList);

		// use of the Fisher–Yates shuffle with a Randomly permute
		Collections.shuffle(shuffledList, RandomUtils.generateRadomUseingSeed(this.randomSeed));

		scrambeldImage = saveOutputImage(joinBufferedImage(shuffledList), true);
		destImageBuffer = ImageIO.read(scrambeldImage);

		imageIndexMap = new LinkedHashSet<>();

		originalList.forEach(originalIndex -> {
			// System.out.println(originalList.indexOf(originalIndex) + " = " +
			// shuffledList.indexOf(originalIndex));
			imageIndexMap.add(shuffledList.indexOf(originalIndex));
		});
		return this;
	}

	List<BufferedImage> reArrangeImage(Set<Integer> imageIndexMap, List<BufferedImage> splitedImageList) {

		List<BufferedImage> returnList = new ArrayList<>();

		imageIndexMap.forEach((originalIndex) -> {

			System.out.println("Inserting : " + splitedImageList.get(originalIndex) + " into : " + originalIndex);
			returnList.add(splitedImageList.get(originalIndex));
		});

		return returnList;
	}

	File saveOutputImage(final BufferedImage outputImageBuffer, boolean isUnscrambler) {

		String message = isUnscrambler ? "Scrambled" : "Unscrambled";
		File newFile = null;
		try {
			newFile = RandomUtils.generateOutputFile(sourceImage);
			ImageIO.write(outputImageBuffer, "png", newFile);
			System.out.println("\n" + message + " Image will be saved in : " + newFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newFile;
	}

	private BufferedImage joinBufferedImage(List<BufferedImage> imgArr) {

		int type = imgArr.get(0).getType();
		int chunkWidth = imgArr.get(0).getWidth();
		int chunkHeight = imgArr.get(0).getHeight();

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);

		int num = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				finalImg.createGraphics().drawImage(imgArr.get(num), chunkWidth * j, chunkHeight * i, null);
				num++;
			}
		}
		System.out.println("Mearging completed.");
		return finalImg;
	}

	private List<BufferedImage> splitImageIntoBlocks(BufferedImage imgBuffer) throws IOException {

		String imageData = "Provided row or column size should be within image size, Provided : R x C = " + this.rows
				+ " x " + this.cols + "  source image : H x W = " + imgBuffer.getHeight() + " x "
				+ imgBuffer.getWidth();

		if (this.rows > imgBuffer.getHeight() || this.cols > imgBuffer.getWidth())
			throw new Error(imageData);

		int chunkWidth = imgBuffer.getWidth() / cols; // determines the
														// chunk width
														// and height
		int chunkHeight = imgBuffer.getHeight() / rows;
		int count = 0;
		BufferedImage imgs[] = new BufferedImage[chunks]; // Image array to hold
															// image chunks
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				// Initialize the image array with image chunks
				imgs[count] = new BufferedImage(chunkWidth, chunkHeight, imgBuffer.getType());

				// // draws the image chunk
				Graphics2D gr = imgs[count++].createGraphics();
				gr.drawImage(imgBuffer, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x,
						chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);

				gr.dispose();
			}
		}
		System.out.println("Splitting completed.");
		return Arrays.asList(imgs);
	}

	public File getSourceImage() {
		return sourceImage;
	}

	public void setSourceImage(File sourceImage) {
		this.sourceImage = sourceImage;
	}

	public String getOriginalImagePath() {
		return originalImagePath;
	}

	public void setOriginalImagePath(String originalImagePath) {
		this.originalImagePath = originalImagePath;
	}

	public File getScrambeldImage() {
		return scrambeldImage;
	}

	public void setScrambeldImage(File scrambeldImage) {
		this.scrambeldImage = scrambeldImage;
	}

	public File getUnScrambledImage() {
		return unScrambledImage;
	}

	public void setUnScrambledImage(File unScrambledImage) {
		this.unScrambledImage = unScrambledImage;
	}

	public Set<Integer> getImageIndexMap() {
		return imageIndexMap;
	}

	public void setImageIndexMap(Set<Integer> imageIndexMap) {
		this.imageIndexMap = imageIndexMap;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getChunks() {
		return chunks;
	}

	public void setChunks(int chunks) {
		this.chunks = chunks;
	}

}