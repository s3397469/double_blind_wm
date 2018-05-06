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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RGBPlaceHolder {

	int grbValue;

	List<String> pixPositions;

	String frequencyRatio;
	double roundedFrequencyRatio;

	int x, y;

	private double pixCountToBeChanged;

	public void addNewPixPosition(int x, int y) {
		pixPositions.add(String.valueOf(x) + "," + String.valueOf(y));
	}

	RGBPlaceHolder(int x, int y, int grbValue) {
		this.x = x;
		this.y = y;
		this.grbValue = grbValue;
		pixPositions = new ArrayList<String>();
		
		this.addNewPixPosition(this.x,this.y);
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

	public String getFrequencyRatio() {
		return frequencyRatio;
	}

	public void setFrequencyRatio(String frequencyRatio) {
		this.frequencyRatio = frequencyRatio;
	}

	public int hashCode() {
		return this.grbValue;
	}

	public boolean equals(Object obj) {

		if (obj instanceof RGBPlaceHolder) {
			RGBPlaceHolder p = (RGBPlaceHolder) obj;
			return p.grbValue == (this.grbValue);
		}
		return false;
	}

	// public float getPixFrequencyCount() {
	// return pixFrequencyCount;
	// }
	//
	// public void setPixFrequencyCount(float pixFrequencyCount) {
	// this.pixFrequencyCount = pixFrequencyCount;
	// }

	public double getRoundedFrequencyRatio() {
		float rawFrequencyRatio = Float.parseFloat(this.getFrequencyRatio() == null ? "0" : this.getFrequencyRatio());
		return Math.ceil(rawFrequencyRatio);
	}

	public int getRepetitionCount() {
		return pixPositions.size();
	}

	public int getRGB() {
		return grbValue;
	}

	public void setRGB(int grbValue) {
		this.grbValue = grbValue;
	}

	public List<String> getPixPositions() {
		return pixPositions;
	}

	public void setPixCountToBeChanged(double pixCountToBeChanged) {
		this.pixCountToBeChanged= pixCountToBeChanged;
	}

	public double getPixCountToBeChanged() {
		return pixCountToBeChanged;
	}

}
