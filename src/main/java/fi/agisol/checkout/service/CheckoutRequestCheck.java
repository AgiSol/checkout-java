package fi.agisol.checkout.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.agisol.checkout.utils.HashUtil;

public class CheckoutRequestCheck extends CheckoutRequestBase {

	private static final String PAYMENT_SCHEME = "https";
	private static final String PAYMENT_HOST = "rpcapi.checkout.fi";
	private static final String PAYMENT_PATH = "/poll";

	private static final int ALGORITH_MD5_IN_THIS_REQUEST = 1; // checkout has different value for MD5 in different requests ???

	public final int format = 1; // FORMAT always return XML -> 1

	public CheckoutRequestCheck(String stamp, String merchant, String password, int amount, String reference) {
		super(stamp, reference, merchant, password, amount, ALGORITH_MD5_IN_THIS_REQUEST);
	}

	@Override
	public String calculateHash() {
		String fromValue = StringUtils.join( new Object [] {this.version, this.stamp, this.reference, this.merchant,
				Integer.toString(this.amount), this.currency, Integer.toString(this.format),
				Integer.toString(this.algorithm), this.password }, "+");
		String result = HashUtil.calculateMd5(fromValue);
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
		params.put("VERSION", this.version);
		params.put("STAMP", this.stamp);
		params.put("REFERENCE", this.reference);
		params.put("MERCHANT", this.merchant);
		params.put("AMOUNT", Integer.toString(this.amount));
		params.put("CURRENCY", this.currency);
		params.put("FORMAT", Integer.toString(this.format));
		params.put("ALGORITHM", Integer.toString(this.algorithm));
		params.put("MAC", this.calculateHash());
		return params;
	}
}
