package fi.agisol.checkout.api.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Trade tells the basic information about started transaction.
 * (Used to build client side html-payment-form)
 * 
 * @author www.agisol.fi
 *
 */
public class Trade implements Serializable {
	
	private static final long serialVersionUID = -2691999079703313774L;

	/**
	 * Checkout generated payment identifier
	 */
	public final String paymentId;
	
	/**
	 * Internally (by "checkout-java" component) generated identifier
	 */
	public final String stamp;
	
	/**
	 * Amount of the payment in euro-cents
	 */
	public final Integer amount;
	
	/**
	 * Reference number of the payment
	 */
	public final String reference;
	
	/**
	 * Custom information about payment/order
	 */
	public final String message;
	
	/**
	 * First name of the customer related to payment
	 */
	public final String firstName;
	
	/**
	 * Last name of the customer related to payment
	 */
	public final String lastName;
	
	/**
	 * List of available banks to proceed with the payment
	 */
	public final List<Bank> banks;

	/**
	 * Default constructor
	 * 
	 * @param paymentId is id generated by Checkout
	 * @param stamp is id generated Internally (by "checkout-java" component)
	 * @param amount of the payment in euro-cents
	 * @param reference number of the payment
	 * @param description contains custom information about payment/order
	 * @param firstName of the customer related to payment
	 * @param lastName of the customer related to payment
	 * @param banks available to proceed with the payment
	 */
	public Trade(
			String paymentId,
			String stamp,
			Integer amount,
			String reference,
			String description,
			String firstName,
			String lastName,
			List<Bank> banks) {
		this.stamp = stamp;
		this.reference = reference;
		this.message = description;
		this.firstName = firstName;
		this.lastName = lastName;
		this.paymentId = paymentId;
		this.amount = amount;
		this.banks = Collections.unmodifiableList(banks);
	}

}
