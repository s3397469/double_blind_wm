package au.edu.img.tools;

import java.io.File;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Random;

public class RandomUtils {

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

	// To check valid image source
	public static boolean isValidPath(String sourceLocation) {

		if (!new File(sourceLocation).isFile() || !new File(sourceLocation).exists()) {
			System.err.println("Invalid source location. Please provide valid image file.");
			return false;
		} else
			return true;
	}
	
	// Use to generate the image output file
	public static File generateOutputFile(File sourceImage) {
		String fileName = sourceImage.getName().replaceFirst("[.][^.]+$", "");

		StringBuilder newFileName = new StringBuilder(fileName);
		newFileName.append("_").append(String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()));
		return new File(sourceImage.getAbsolutePath().replace(fileName, newFileName.toString()));

	}

	public static void main(String[] arg) {

	}
}
