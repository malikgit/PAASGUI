package com.getusroi.paas.application.jaxb;

public class UnableToUnMarshalXML extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnableToUnMarshalXML() {
		super();
	}

	public UnableToUnMarshalXML(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnableToUnMarshalXML(String message, Throwable cause) {
		super(message, cause);
	}

	public UnableToUnMarshalXML(String message) {
		super(message);
	}

	public UnableToUnMarshalXML(Throwable cause) {
		super(cause);
	}

}
