package com.effectiveJava;
public class Point {
	private double latitude;
	private double longitude;

	public Point(double latitude, double longitude) {
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	@Override
	public String toString() {
		return "Latitude : " + getLatitude() + "   Longitude  : " + getLongitude();
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}