package com.jss.baggage.model;

public class Baggage {
	private String bagNbr;
	private String enetryPoint;
	private String flightId;

	public Baggage(String bagNbr, String enetryPoint, String flightId) {
		this.bagNbr = bagNbr;
		this.enetryPoint = enetryPoint;
		this.flightId = flightId;
	}

	public String toString() {
		return bagNbr;
	}

	public String getBagNbr() {
		return bagNbr;
	}

	public void setBagNbr(String bagNbr) {
		this.bagNbr = bagNbr;
	}

	public String getEnetryPoint() {
		return enetryPoint;
	}

	public void setEnetryPoint(String enetryPoint) {
		this.enetryPoint = enetryPoint;
	}

	public String getFlightId() {
		return flightId;
	}

	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}

}
