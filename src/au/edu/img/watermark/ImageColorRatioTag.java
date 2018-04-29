package au.edu.img.watermark;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

public class ImageColorRatioTag {

	public static void main(String[] args) throws IOException {

		// 1 Find the unique pix. and their ratio

		ImageColorRatioTag imgTag = new ImageColorRatioTag();

		File imgFile = new File("C:/Users/Aeron/Documents/workspace/s3397469_ImageProcessing/src/res/basic_colors.png");

		BufferedImage image = ImageIO.read(imgFile);
		List<PlaceHolder> colorList = imgTag.extractPixs(image);

		System.out.println("Image size : H =" + image.getHeight() + " x W =" + image.getWidth() + ", total pix's = "
				+ colorList.size());

		Map<PlaceHolder, Float> colorRatio = new HashMap<>();
		Set<PlaceHolder> uniqueSet = new HashSet<PlaceHolder>(colorList);
		for (PlaceHolder uniqueColor : uniqueSet) {
			int pixFrequencyCount = Collections.frequency(colorList, uniqueColor);
			uniqueColor.setFrequency(pixFrequencyCount);

			String frequencyRatio = calculateRatio(pixFrequencyCount, image.getHeight(), image.getWidth());
			uniqueColor.setFrequencyRatio(frequencyRatio);

//			System.out.println(ColorNameUtils.getColorNameFromRgb(uniqueColor.getColor().getRed(),
//					uniqueColor.getColor().getGreen(), uniqueColor.getColor().getBlue()) + " = " + pixFrequencyCount);
			colorRatio.put(uniqueColor, Float.valueOf(frequencyRatio));
		}

		// 2 For each pix. group add a random number based on the percentage

		Map<PlaceHolder, Integer> map = (Map<PlaceHolder, Integer>) sortByValues(colorRatio);

		String fileName = imgFile.getParentFile().getAbsolutePath() + File.separator + imgFile.getName() + ".txt";

		try (PrintWriter fileOut = new PrintWriter(fileName)) {

			for (PlaceHolder c : map.keySet()) {
				System.err.println(c + " - " + map.get(c));
				fileOut.println(c + " - " + map.get(c));
			}
			fileOut.flush();
		}

	}

	static String calculateRatio(int pixFrequencyCount, int imgHeight, int imgWidth) {

		float totPix = imgHeight * imgWidth;

		if (pixFrequencyCount == 0 || pixFrequencyCount > totPix)
			return "0";

		float percentage = 100 * pixFrequencyCount / totPix;
		// System.out.println(percentage + " = " + Math.round(percentage) +
		// "%");
		NumberFormat formatter = new DecimalFormat("0.00000000");
		return (formatter.format(percentage));
	}

	private static Map<?, ?> sortByValues(Map map) {
		List list = new LinkedList(map.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	private List<PlaceHolder> extractPixs(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] result = new int[height][width];
		List<PlaceHolder> colorList = new ArrayList<>();

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {

				Color color = new Color(image.getRGB(col, row));
				colorList.add(new ImageColorRatioTag.PlaceHolder(row, col, color));
			}
		}

		return colorList;
	}

	class PlaceHolder {
		Color color;
		float pixFrequencyCount;
		int frequency;
		String frequencyRatio;

		int x, y;

		PlaceHolder(int x, int y, final Color color) {
			this.x = x;
			this.y = y;
			this.color = color;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public float getPixFrequencyCount() {
			return pixFrequencyCount;
		}

		public void setPixFrequencyCount(float pixFrequencyCount) {
			this.pixFrequencyCount = pixFrequencyCount;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}

		public String getFrequencyRatio() {
			return frequencyRatio;
		}

		public void setFrequencyRatio(String frequencyRatio) {
			this.frequencyRatio = frequencyRatio;
		}

		public String toString() {
			return this.getX() + " x " + this.getY() + " \t " + this.color.toString() + "\t\tF=" + frequency + "\tR="
					+ frequencyRatio + "%";
		}

		public int hashCode() {
			return this.color.hashCode();
		}

		public boolean equals(Object obj) {

			if (obj instanceof PlaceHolder) {
				PlaceHolder p = (PlaceHolder) obj;
				return p.getColor().equals(this.getColor());
			}
			return false;
		}
	}

}
