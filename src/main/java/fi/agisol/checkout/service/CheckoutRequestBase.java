package fi.agisol.checkout.service;

import java.net.URI;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public abstract class CheckoutRequestBase {

	/*** Version of the Checkout API (Always "0001") */
	@NotNull
	@Size(min = 1, max = 4)
	public final String version = CheckoutConstants.VERSION;

	/***
	 * Order number is a string of characters identifying the customer's
	 * purchase. (Mandatory)
	 **/
	@NotNull
	@Size(min = 1, max = 20)
	public final String stamp;

	/***
	 * Reference number is sent to bank by default and is automatically created.
	 * In those payment methods that are used as an interface, this field can
	 * contain own reference number, which is sent to the bank service instead
	 * of the automatically generated reference number. (Mandatory).
	 */
	@NotNull
	@Size(min = 1, max = 20)
	public final String reference;

	/** Merchant account id (Mandatory) */
	@NotNull
	@Size(min = 1, max = 20)
	public final String merchant; // test account "375917"

	/** Payment amount in cents (Mandatory between 1 - 200 000) */
	@NotNull
	@Min(value = 1)
	@Max(value = 200000)
	public final int amount;

	/*** Currency code (always "EUR") */
	@NotNull
	@Size(min = 1, max = 3)
	public final String currency = CheckoutConstants.CURRENCY;

	/***
	 * AUTHCODE algorithm. 2 -> MD5. 3 -> HMAC-SHA256
	 */
	@NotNull
	@Min(value = 2)
	@Max(value = 3)
	public final int algorithm;

	/** Merchant account password (Mandatory) */
	@NotNull
	@Size(min = 1, max = 100)
	public final String password; // test password "SAIPPUAKAUPPIAS"

	public CheckoutRequestBase(String stamp, String reference, String merchant, String password, int amount,
			int algorithm) {
		this.stamp = stamp;
		this.reference = reference;
		this.merchant = merchant;
		this.password = password;
		this.amount = amount;
		this.algorithm = algorithm;
	}

	public String getPostContent() {
		// get post params map and create post content
		// (eg. "ParamKey1=ParamValue1&ParamKey2=ParamValue2")
		String result = "";
		for (Map.Entry<String, String> e : this.getPostParams().entrySet()) {
			result += e.getKey() + "=" + e.getValue() + "&";
		}
		return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
	}

	public abstract String calculateHash();

	public abstract URI getUri();

	protected abstract Map<String, String> getPostParams();

}
