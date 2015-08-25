package fi.agisol.checkout.api.dto;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import fi.agisol.checkout.service.CheckoutPaymentStatus;
import fi.agisol.checkout.utils.HashUtil;

/***
 * PaymentEndResult is constructed by the API client from the information that
 * is received in from the bank return URL parameters
 * 
 * For example:
 * -> http://demo1.checkout.fi/xml2.php?test=1&VERSION=0001&STAMP=1440746210&REFERENCE=12344&PAYMENT=28091801&ALGORITHM=3&STATUS=-1&MAC=196DCF22ECB650B76336F68DC3ACC9E98BBF369EA6BA8E824D9807242A3557B0
 * 
 * @author www.agisol.fi
 *
 */
public class PaymentEndResult implements Serializable {
	
	private static final long serialVersionUID = -5881687839916068023L;

	/*** Version of the Checkout API (Always "0001") */
	@NotNull
    @Size(min = 1, max = 4)
	private final String version		= "0001";

	/*** AUTHCODE algorithm.  
	 * 		2 -> MD5. 
	 * 		3 -> HMAC-SHA256 */
	@NotNull
    @Min(value=2)
	@Max(value=3)
	private Integer algorithm = 2;
	
	@NotNull
    @Size(min = 1, max = 20)
	private String stamp;
	
	@NotNull
    @Size(min = 1, max = 20)
	private String reference;

	@NotNull
    @Size(min = 1, max = 20)
	private String paymentId;

	@NotNull
    @Min(value=CheckoutPaymentStatus.MIN_VALUE)
	@Max(value=CheckoutPaymentStatus.MAX_VALUE)
	private int status;

	@NotNull
	private String mac;

	public PaymentEndResult() {}
	
	public PaymentEndResult(
			String stamp, 
			String reference, 
			String paymentId, 
			int status,
			int algorithm,
			String mac) {
		
		this.stamp = stamp;
		this.reference = reference;
		
		this.paymentId = paymentId;
		this.status = status;
		this.algorithm = algorithm;
		this.mac = mac;
	}
	
	/***
	 * Calculate hash of the PaymentEndResult against the given password
	 * 	@param password Password for the checkout service
	 * @return calculated hash 
	 */
	public String calculateHash(String password) {
		String result = null;
		
		
		if( algorithm == 3) {
			String fromValue = StringUtils.join(
				new Object[] { 
					 this.version, this.stamp,
					 this.reference, 
					 this.paymentId,
					 Integer.toString( this.status ),
					 Integer.toString(this.algorithm) },
				"&");
			result = HashUtil.calculateHmacSha256(password, fromValue);
			
		} else if (algorithm == 2) {
			
			String fromValue = StringUtils.join(
				new Object[] { password, 
					this.version, this.stamp,
					this.reference, 
					this.paymentId,
					Integer.toString( this.status ),
					Integer.toString( this.algorithm ) },
				"&");
			
			result = HashUtil.calculateMd5(fromValue);
		}
		
		return (result != null) ? result.toUpperCase() : result;
	}
	

	@Override
	public String toString() {
		return "PaymentEndResult [version=" + version + ", algorithm="
				+ algorithm + ", stamp=" + stamp + ", reference=" + reference
				+ ", paymentId=" + paymentId
				+ ", status=" + status + ", mac=" + mac + "]";
	}
	
	/*** 
	 * Get Version of the Checkout API (Always "0001") 
	 * @return version
	 */
	public String getVersion() {
		return version;
	}
	
	/*** 
	 * Get algorithm used to hash result
	 * 		2 -> MD5 (DEFAULT) 
	 * 		3 -> HMAC-SHA256 
	 * @return algorithm
	 */
	public Integer getAlgorithm() {
		return algorithm;
	}
	
	/*** 
	 * Set algorithm used to hash result
	 * 		2 -> MD5 (DEFAULT) 
	 * 		3 -> HMAC-SHA256 
	 * @parameter algorithm
	 */
	public void setAlgorithm(Integer algorithm) {
		this.algorithm = algorithm;
	}
	
	/***
	 * Get stamp of the payment
	 * @return stamp
	 */
	public String getStamp() {
		return stamp;
	}

	/***
	 * Set stamp of the payment
	 * @parameter stamp
	 */
	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	/***
	 * Get reference of the payment
	 * @return reference
	 */
	public String getReference() {
		return reference;
	}

	/***
	 * Set reference of the payment
	 * @parameter reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/***
	 * Get paymentId of the payment
	 * @return paymentId
	 */
	public String getPaymentId() {
		return paymentId;
	}

	/***
	 * Set paymentId of the payment
	 * @parameter paymentId
	 */
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	/***
	 * Get status of the payment 
	 * More info from:
	 *  - http://www.checkout.fi/materiaalit/tekninen-materiaali/
	 *  - page 5/7 -> http://www.checkout.fi/wp-content/uploads/2015/04/CTD-Tekninenrajapintakuvaus-210415-1146-170.pdf
	 *
	 * @return status
	 */
	public int getStatus() {
		return status;
	}

	/***
	 * Set status of the payment
	 *  More info from:
	 *  - http://www.checkout.fi/materiaalit/tekninen-materiaali/
	 *  - page 5/7 -> http://www.checkout.fi/wp-content/uploads/2015/04/CTD-Tekninenrajapintakuvaus-210415-1146-170.pdf
	 *
	 * @parameter status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/***
	 * Get mac (security hash) of the payment 
	 * More info from:
	 *  - http://www.checkout.fi/materiaalit/tekninen-materiaali/
	 *  - page 4/7 -> http://www.checkout.fi/wp-content/uploads/2015/04/CTD-Tekninenrajapintakuvaus-210415-1146-170.pdf
	 *
	 * @return mac
	 */
	public String getMac() {
		return mac;
	}
	
	/***
	 * Get mac (security hash) of the payment 
	 * More info from:
	 *  - http://www.checkout.fi/materiaalit/tekninen-materiaali/
	 *  - page 4/7 -> http://www.checkout.fi/wp-content/uploads/2015/04/CTD-Tekninenrajapintakuvaus-210415-1146-170.pdf
	 *
	 * @return mac
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

}