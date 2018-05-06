package au.edu.img.tools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import au.edu.img.watermark.RGBPlaceHolder;

public class RandomUtils {

	
	static String RES_LOCATION="/res";
	
	public static enum PixelChangeOperation {
		ADD, SUBSTRACT
	}

	// To generate a Random object using a given seed.
	public static Random generateRadomUseingSeed(String seed) {

		if (null != seed && seed.trim().length() > 0)
			System.out.println("Generating the Random for the seed : " + seed);
		else {

			System.out.println("Generating the Random ");
			return new Random();
		}

		// Convert to binary
		String binary = new BigInteger(seed.getBytes()).toString(2);
		// System.out.println("To binary: " + binary);

		// Convert binary back to int
		String[] splitBinary = binary.split("(?<=\\G.{5})");

		Integer totalSeed = new Integer(0);
		for (String str : splitBinary)
			totalSeed += Integer.parseInt(str, 2);

		return new Random(totalSeed);
	}

	// To check valid image source both in FS & class path
	public static boolean isValidPath(String sourceLocation) {

		if (loadResourceFromClassPath(sourceLocation) != null)
			return true;
		else if (new File(sourceLocation).isFile() && new File(sourceLocation).exists()) {
			return true;
		} else {
			System.err.println("Invalid source location. Please provide valid image file.");
			return false;
		}
	}

	public static File loadFile(String sourceFilePath) {
		File returnFile = loadResourceFromClassPath(sourceFilePath);

		if (returnFile != null)
			return returnFile;
		else
			return new File(sourceFilePath);
	}

	// Use to generate the image output file
	public static File generateOutputFile(File sourceImage) {
		String fileName = sourceImage.getName().replaceFirst("[.][^.]+$", "");

		StringBuilder newFileName = new StringBuilder(fileName);
		newFileName.append("_").append(String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()));
		return new File(sourceImage.getAbsolutePath().replace(fileName, newFileName.toString()));

	}

	// Use to load the file from Class Path
	public static File loadResourceFromClassPath(String classPathFileName) {
		Path path = null;
		try {
			path = Paths.get(RandomUtils.class.getResource(RES_LOCATION+File.separator+classPathFileName).toURI());
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		return new File(path.toString()).getAbsoluteFile();
	}

	public static void main(String[] arg) {

	}

	// Use to sort a Map<?,?> based on the Values.
	public static Map<?, ?> sortMapByValues(Map<?, ?> map) {
		List<?> list = new LinkedList(map.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		// Copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	public static double calculateRoundedPixCountToChange(RGBPlaceHolder colorGroup, final double changeRatio) {
		return Math.ceil(colorGroup.getRepetitionCount() * changeRatio / 100);
	}
	
	public static double calculateRoundedChangeRatio(RGBPlaceHolder colorGroup, final int changeRatio) {
		return Math.ceil(colorGroup.getRoundedFrequencyRatio() * changeRatio / 100);
	}

	public static void modifyImageRGBValue(final BufferedImage bufferdImageSourceFile, int x, int y,
			PixelChangeOperation operation, int amount) {
		int rgbValue = bufferdImageSourceFile.getRGB(x, y);
		
//		Color c = new Color(bufferdImageSourceFile.getRGB(x, y));
//
//		StringBuffer buff = new StringBuffer();
//		
//		buff.append("R ").append(c.getRed());
//		buff.append("G ").append(c.getGreen());
//		buff.append("B ").append(c.getBlue());
//
//		System.out.println("X Y " + x  + "  " + y + " : RGB Value : "+ rgbValue + " " + buff);
		
//		if (rgbValue < 0)
//			amount *= -1;
//
//		switch (operation) {
//		case ADD:
//			// Adding to RGB values
//			rgbValue = rgbValue + amount;
//			break;
//		case SUBSTRACT:
//			// Deduct to RGB values
//			rgbValue = rgbValue - amount;
//			break;
//		}
		
		Color oldColor = new Color(bufferdImageSourceFile.getRGB(x, y));

		System.out.println("X:" + x +" Y:"+ y+ " RGB : " + oldColor.getRGB());
		Color newColor = new Color(oldColor.getRed() + 5, oldColor.getGreen() + 5, oldColor.getBlue() + 5);


		bufferdImageSourceFile.setRGB(x, y, newColor.getRGB());
		
//		c = new Color(bufferdImageSourceFile.getRGB(x, y));
//
//		buff = new StringBuffer();
//		
//		buff.append("R ").append(c.getRed());
//		buff.append("G ").append(c.getGreen());
//		buff.append("B ").append(c.getBlue());
//		
		
		
//		System.out.print("\t ::: "+ ": RGB Value : "+ rgbValue + " " + buff);
		
		bufferdImageSourceFile.flush();
	}

	public static void saveBuffedImage(final BufferedImage bufferdImageSourceFile, File imageSourceFile) {
		try {

			File markedImageFile = RandomUtils.generateOutputFile(imageSourceFile);
			ImageIO.write(bufferdImageSourceFile, "png", markedImageFile);
			System.out.println("Image with the watermark will be saved in : " + markedImageFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void modifyImageRGBValue(BufferedImage bufferdImageSourceFile, String axisPosition,
			PixelChangeOperation operation, int amount) {

		String[] values = axisPosition.split(",");

		modifyImageRGBValue(bufferdImageSourceFile, Integer.parseInt(values[0]), Integer.parseInt(values[1]), operation,
				amount);
	}
}
