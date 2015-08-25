package fi.agisol.checkout.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import fi.agisol.checkout.utils.HashUtil;
import fi.agisol.checkout.utils.RandomUtil;

public class CheckoutRequestTrade extends CheckoutRequestBase {

	public static final int MAX_NAME_LENGTH = 40;
	public static final int MAX_EMAIL_LENGTH = 200;
	
	private static final String PAYMENT_SCHEME = "https";
	private static final String PAYMENT_HOST = "payment.checkout.fi";
	private static final String PAYMENT_PATH = "/";
	
	/***
	 * stamp consists of 5 random alphabetic characters + epoch time in milliseconds	
	 */
	private static String CreateStamp() {
		return RandomUtil.generateRandomAlphapetic(5) + Long.toString( Instant.now().getMillis() );
	}

	/*** Payment device interface (always 10-> XML, 1 -> old html interface) */
	@NotNull
	@Min(value=1)
	@Max(value=10)
	public final Integer device		= 10;

	/*** Payment type (always 0) */
	@NotNull
	public final Integer type		= 0;
	
	/*** Language code (must be "FI", "SE" or "EN") */
	@NotNull
    @Size(min = 1, max = 2)
	public final String language;
	
	/*** Country code (3 character country code) */
	@NotNull
    @Size(min = 1, max = 3)
	public final String country;
		
	/*** Content of the payment (always 1 -> normal , 2 -> adult content) */
	@NotNull
	@Min(value=1)
	@Max(value=2)
	public Integer content;
	
	/** Any data about the order in text format can be sent to the payment (Optional) */
	@NotNull
    @Size(min = 0, max = 1000)
	public final String message;

	/** Delivery date of order in format YYYYMMDD (Mandatory) */
	@NotNull
    @Size(min = 8, max = 8)
	public final String delivery_date;
	
	/** Payer's first name (Optional) */
	@NotNull
    @Size(min = 0, max = MAX_NAME_LENGTH)
	public final String firstname;
	
	/** Payer's surname (Optional) */
	@NotNull
    @Size(min = 0, max = MAX_NAME_LENGTH)
	public final String familyname;

	/** Payer's address (Optional) */
	@NotNull
    @Size(min = 0, max = 40)
	public final String address;
	
	/** Payer's post code (Optional) */
	@NotNull
    @Size(min = 0, max = 14)
	public final String postcode;
	
	/** Payer's post office (Optional) */
	@NotNull
    @Size(min = 0, max = 18)
	public final String postoffice;
	
	/** Payer's email (Optional) */
	@NotNull
    @Size(min = 0, max = MAX_EMAIL_LENGTH)
	public final String email;
	
	/** Payer's phone (Optional) */
	@NotNull
    @Size(min = 0, max = 30)
	public final String phone;
	
	
	
	/*** URI where we return after successful payment (Mandatory) */
	@NotNull
    @Size(min = 1, max = 300)
	public final String returnAddr; // name 'return' in request
	
	/*** URI where we return after cancelled payment (Mandatory) */
	@NotNull
    @Size(min = 1, max = 300)
	public final String cancel;
	
	/*** URI where we return after rejected payment (Optional) */
	@NotNull
    @Size(min = 0, max = 300)
	public final String reject;
	
	/*** URI where we return after delayed payment (Optional) */
	@NotNull
    @Size(min = 0, max = 300)
	public final String delayed;
	
	
	public CheckoutRequestTrade(
			String merchant, String password, 
			int amount, String reference,
			String returnAddr, String cancelAddr,
			String rejectAddr, String delayedAddr,
			Integer content,
			LocalDate deliveryDate,
			String firstname, String familyname, String email,
			String message, 
			String phone, String address, String postCode, String postOffice, 
			String country, String language) {
		super(	CreateStamp(),
				reference,
				merchant,
				password,
				amount,
				CheckoutConstants.ALGORITH_MD5);
		
		this.firstname = firstname;
		this.familyname = familyname;
		this.email = email;
		
		this.address = address;
		this.postcode = postCode;
		this.postoffice = postOffice;
		this.phone = phone;
		
		this.country = country;
		this.language = language;
		
		this.message = message;
		
		this.delivery_date = DateTimeFormat.forPattern("yyyyMMdd").print(deliveryDate);

		this.returnAddr = 	returnAddr;
		this.cancel = 		cancelAddr;
		this.reject = 		rejectAddr;
		this.delayed = 		delayedAddr;
		
		this.content = content;
	}

	@Override
	public String toString() {
		return "CheckoutTradeRequest [version=" + version
				+ ", algorithm=" + algorithm + ", stamp=" + stamp
				+ ", reference=" + reference + ", language=" + language
				+ ", country=" + country + ", currency=" + currency
				+ ", device=" + device + ", content=" + content + ", type="
				+ type + ", message=" + message + ", delivery_date="
				+ delivery_date + ", firstname=" + firstname + ", familyname="
				+ familyname + ", address=" + address + ", postcode="
				+ postcode + ", postoffice=" + postoffice + ", email=" + email
				+ ", phone=" + phone + ", merchant=" + merchant + ", password="
				+ password + ", amount=" + amount + ", returnAddr="
				+ returnAddr + ", cancel=" + cancel + ", reject=" + reject
				+ ", delayed=" + delayed + "]";
	}

	@Override
	public String calculateHash() {
		String result = null;
		
		if( algorithm == CheckoutConstants.ALGORITH_HMAC_SHA256) {
			String fromValue =  StringUtils.join( 
				new Object [] {this.version, this.stamp,
					Integer.toString(this.amount), this.reference, this.message,
					this.language, this.merchant, this.returnAddr, this.cancel,
					this.reject, this.delayed, this.country, this.currency,
					Integer.toString(this.device), Integer.toString(this.content), 
					Integer.toString(this.type), Integer.toString(this.algorithm),
					this.delivery_date, this.firstname, this.familyname,
					this.address, this.postcode, this.postoffice},
				"+");
			
			result = HashUtil.calculateHmacSha256(this.password, fromValue);
			
		} else if (algorithm == CheckoutConstants.ALGORITH_MD5) {

			String fromValue =  StringUtils.join( 
				new Object [] { this.version, this.stamp,
					Integer.toString(this.amount), this.reference, this.message,
					this.language, this.merchant, this.returnAddr, this.cancel,
					this.reject, this.delayed, this.country, this.currency,
					Integer.toString(this.device), Integer.toString(this.content), 
					Integer.toString(this.type), Integer.toString(this.algorithm),
					this.delivery_date, this.firstname, this.familyname,
					this.address, this.postcode, this.postoffice, this.password},
				"+");
			result = HashUtil.calculateMd5(fromValue);
		}
		
		return (result != null) ? result.toUpperCase() : result;
		
	}


	@Override
	public URI getUri() {
		URI uri = null;
		try {
			uri = new URI(PAYMENT_SCHEME, PAYMENT_HOST, PAYMENT_PATH, null);
		} catch (URISyntaxException e) {
			// should not happen
			e.printStackTrace();
		}
		return uri;
	}

	@Override
	protected Map<String, String> getPostParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("VERSION", 	this.version);
		params.put("STAMP", 	this.stamp );
		params.put("AMOUNT", 	Integer.toString(this.amount) );
		params.put("REFERENCE", this.reference);
		params.put("MESSAGE", 	this.message);
		params.put("LANGUAGE",	this.language);
		params.put("MERCHANT", 	this.merchant);
		params.put("RETURN", 	this.returnAddr);
		params.put("CANCEL", 	this.cancel);
		params.put("REJECT", 	this.reject);
		params.put("DELAYED", 	this.delayed);
		params.put("COUNTRY", 	this.country);
		params.put("CURRENCY", 	this.currency);
		params.put("DEVICE", 	Integer.toString(this.device));
		params.put("CONTENT", 	Integer.toString(this.content));
		params.put("TYPE", 		Integer.toString(this.type));
		params.put("ALGORITHM", 	Integer.toString(this.algorithm));
		params.put("DELIVERY_DATE", this.delivery_date);
		params.put("FIRSTNAME", 	this.firstname);
		params.put("FAMILYNAME", 	this.familyname);
		params.put("ADDRESS", 		this.address);
		params.put("POSTCODE", 		this.postcode);
		params.put("POSTOFFICE", 	this.postoffice);
		params.put("MAC", 			this.calculateHash());
		params.put("EMAIL", 		this.email);
		params.put("PHONE", 		this.phone);
		return params;
	}
}