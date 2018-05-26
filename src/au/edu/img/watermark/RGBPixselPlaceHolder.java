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
package au.edu.img.watermark;

import java.awt.Color;

public class RGBPixselPlaceHolder {

	Color color;
	
	float pixFrequencyCount;
	int repetitionCount;
	String frequencyRatio;
	double roundedFrequencyRatio;

	int x, y;

	RGBPixselPlaceHolder(int x, int y, final Color color) {
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



	public String getFrequencyRatio() {
		return frequencyRatio;
	}

	public void setFrequencyRatio(String frequencyRatio) {
		this.frequencyRatio = frequencyRatio;
	}

	public String toString() {
		return this.getX() + " x " + this.getY() + " \t " + this.color.toString() + "\t\tF=" + repetitionCount + "\tR="
				+ frequencyRatio + "%";
	}

	public int hashCode() {
		return this.color.hashCode();
	}

	public boolean equals(Object obj) {

		if (obj instanceof RGBPixselPlaceHolder) {
			RGBPixselPlaceHolder p = (RGBPixselPlaceHolder) obj;
			return p.getColor().equals(this.getColor());
		}
		return false;
	}

	public float getPixFrequencyCount() {
		return pixFrequencyCount;
	}

	public void setPixFrequencyCount(float pixFrequencyCount) {
		this.pixFrequencyCount = pixFrequencyCount;
	}

	public double getRoundedFrequencyRatio() {
		float rawFrequencyRatio = Float
				.parseFloat(this.getFrequencyRatio() == null ? "0" : this.getFrequencyRatio());
		return Math.ceil(rawFrequencyRatio);
	}

	public int getRepetitionCount() {
		return repetitionCount;
	}

	public void setRepetitionCount(int repetitionCount) {
		this.repetitionCount = repetitionCount;
	}

}
