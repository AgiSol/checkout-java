package fi.agisol.checkout.api.dto;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.joda.time.LocalDate;

/**
 * A Payment information for starting the payment transaction
 */
public class Payment implements Serializable {

	private static final long serialVersionUID = -6893626595942472552L;
	
	@NotNull
    @Size(min = 1, max = 300)
	private String returnUrl = "";
	
	@NotNull
    @Size(min = 1, max = 300)
	private String cancelUrl = "";
	
	@NotNull
	@Size(min = 1, max = 20)
	private String reference  = "";

	@NotNull
	@Min(value = 1)
	@Max(value = 200000)
	private Integer paymentAmount = 0;

	@NotNull
	@Min(value=1)
	@Max(value=2)
	public Integer content = 1;

	@NotNull
    @Size(min = 1, max = 3)
	private String country = "FIN";

	@NotNull
    @Size(min = 1, max = 2)
	private String language = "FI"; 

	@NotNull
	private LocalDate deliveryDate = LocalDate.now();

    @Size(min = 0, max = 300)
	public String rejectUrl = "";
    
    @Size(min = 0, max = 300)
	public String delayedUrl = "";

    @Size(min = 0, max = 40)
	private String firstName = "";
    
    @Size(min = 0, max = 40)
	private String lastName = "";

    @Size(min = 0, max = 200)
	private String email = "";

    @Size(min = 0, max = 30)
	private String phone = "";
    
    @Size(min = 0, max = 40)
	private String address = "";
    
    @Size(min = 0, max = 14)
	private String postCode = "";
    
    @Size(min = 0, max = 18)
	private String postOffice = "";

    @Size(min = 0, max = 1000)
	private String message = "";

	@Override
	public String toString() {
		return "Payment [reference=" + reference + ", paymentAmount="
				+ paymentAmount + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", deliveryDate=" + deliveryDate + ", deliveryDate=" + deliveryDate.toString()
				+ ", message=" + message
				+ "]";
	}

	/**
	 * Get reference number of the payment
	 * @return
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Set reference number of the payment (Mandatory)
	 * @param reference 
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Get payment amount of the payment in euro-cents
	 * @return paymentAmount 
	 */
	public Integer getPaymentAmount() {
		return paymentAmount;
	}

	/**
	 * Set payment amount of the payment in euro-cents (Mandatory)
	 * @param paymentAmount 
	 */
	public void setPaymentAmount(Integer paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	/***
	 * Get first name of the customer related to payment
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/***
	 * Set first name of the customer related to payment (Optional)
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/***
	 * Get last name of the customer related to payment
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}

	/***
	 * Set last name of the customer related to payment (Optional)
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	
	/***
	 * Get email of the customer related to payment
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/***
	 * Set email of the customer related to payment (Optional)
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Delivery date of the order
	 * @return deliveryDate
	 */
	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}
	/**
	 * Set delivery date of the order (default: Today)
	 * @param deliveryDate
	 */
	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * Get phone number of the customer related to payment
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Set phone number of the customer related to order (Optional)
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Get address of the customer related to payment
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Set address of the customer related to payment
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Get post code of the customer related to payment
	 * @return
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * Set post code of the customer related to payment
	 * @param postCode
	 */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/**
	 * Get post office of the customer related to payment
	 * @return
	 */
	public String getPostOffice() {
		return postOffice;
	}

	/**
	 * Set post office of the customer related to payment
	 * @param postOffice
	 */
	public void setPostOffice(String postOffice) {
		this.postOffice = postOffice;
	}


	/*** 
	 * Get 3 character country code 
	 * @return country code 
	 */
	public String getCountry() {
		return country;
	}

	/***
	 * Set 3 character country code (default 'FIN') 
	 * @param country code 
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/** 
	 * Get custom information about payment/order
	 * @return message information about the payment/order
	 */
	public String getMessage() {
		return message;
	}
	
	/** 
	 * Set custom information about payment/order
	 * @param message - information about the payment/order (Optional) 
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get return url where successfull payment transaction should return
	 * @return returnUrl (eg. "https://myapp.mydomain.com/payment/success")
	 */
	public String getReturnUrl() {
		return returnUrl;
	}
	
	/**
	 * Set return url where successfull payment transaction should return (Mandatory)
	 * @param returnUrl (eg. "https://myapp.mydomain.com/payment/success")
	 */
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	/**
	 * Get cancel url where cancelled/failed payment transaction should return
	 * @return cancelUrl (eg. "https://myapp.mydomain.com/payment/cancel")
	 */
	public String getCancelUrl() {
		return cancelUrl;
	}


	/**
	 * Set cancel url where cancelled/failed payment transaction should return (Mandatory)
	 * @param cancelUrl (eg. "https://myapp.mydomain.com/payment/cancel")
	 */
	public void setCancelUrl(String cancelUlr) {
		this.cancelUrl = cancelUlr;
	}

	/**
	 * Get rejected url where rejected payment transaction should return (Optional)
	 * @return rejectUrl (eg. "https://myapp.mydomain.com/payment/rejected")
	 */	
	public String getRejectUrl() {
		return rejectUrl;
	}
	
	/**
	 * Set rejected url where rejected payment transaction should return (Optional)
	 * @param rejectUrl (eg. "https://myapp.mydomain.com/payment/rejected")
	 */	
	public void setRejectUrl(String rejectUrl) {
		this.rejectUrl = rejectUrl;
	}

	/**
	 * Get delayed url where delayed payment transaction should return (Optional)
	 * @return delayedUrl (eg. "https://myapp.mydomain.com/payment/delayed")
	 */	
	public String getDelayedUrl() {
		return delayedUrl;
	}

	/**
	 * Set delayed url where delayed payment transaction should return (Optional)
	 * @param delayedUrl (eg. "https://myapp.mydomain.com/payment/delayed")
	 */	
	public void setDelayedUrl(String delayedUrl) {
		this.delayedUrl = delayedUrl;
	}

	/**
	 * Get language code (must be "FI", "SE" or "EN")
	 * @return language
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Set language code (Default "FI" -> must be "FI", "SE" or "EN")
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	

	/*** 
	 * Get content of the payment (always 1 -> normal , 2 -> adult content) 
	 * @return content
	 **/
	public Integer getContent() {
		return content;
	}
	
	/*** 
	 * Set content of the payment (always 1 -> normal , 2 -> adult content) 
	 * @param content
	 **/
	public void setContent(Integer content) {
		this.content = content;
	}

}
