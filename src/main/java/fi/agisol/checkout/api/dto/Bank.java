package fi.agisol.checkout.api.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class Bank implements Serializable {

	private static final long serialVersionUID = 1341411474600551217L;
	
	/**
	 * Name of the bank
	 */
	public final String name;
	
	/**
	 * Url of the bank service where POST-request must be
	 * made when starting the payment transaction with the bank
	 */
	public final String url;
	
	/**
	 * Url to the image which contains logo/icon of the bank
	 */
	public final String icon;
	
	/**
	 * Map of key-value-pairs which need to be included as parameters
	 * to the POST-request when starting the payment transaction with the bank
	 */
	public final Map<String, String> properties;

	/**
	 * Default constructor
	 * 
	 * @param name of the bank
	 * @param url of the bank service where POST-request must be made
	 * @param icon contains url to the image which contains logo/icon of the bank
	 * @param properties which need to be included as parameters to the POST-request
	 */
	public Bank(String name, String url, String icon, Map<String, String> properties) {
		this.name = name;
		this.url = url;
		this.icon = icon;
		this.properties = Collections.unmodifiableMap(properties);
	}

}
