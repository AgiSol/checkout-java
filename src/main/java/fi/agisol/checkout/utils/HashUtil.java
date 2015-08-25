package fi.agisol.checkout.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashUtil {

	private static final Logger log = LoggerFactory.getLogger(HashUtil.class);

	public static String calculateMd5(String data) {
		String resultHash = null;

		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(data.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			resultHash = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while (resultHash.length() < 32) {
				resultHash = "0" + resultHash;
			}
		} catch (NoSuchAlgorithmException e) {
			log.error("Error calculating MD5 hash for data '" + data + "'", e);
		}

		return resultHash;
	}

	public static String calculateHmacSha256(String key, String data) {
		String resultHash = null;

		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"),"HmacSHA256");
			sha256_HMAC.init(secret_key);

			resultHash = Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes()));

		} catch (Exception e) {
			log.error("Error calculating HmacSHA256 hash for data '" + data
					+ "'", e);
		}

		return resultHash;
	}

}
