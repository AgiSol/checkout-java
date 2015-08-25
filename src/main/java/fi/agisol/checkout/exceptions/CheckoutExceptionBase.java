package fi.agisol.checkout.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import fi.agisol.checkout.service.CheckoutRequestTrade;

public abstract class CheckoutExceptionBase extends RuntimeException {

	private static final long serialVersionUID = -7821632738270712982L;

	public CheckoutExceptionBase(String exceptionText) {
		super(exceptionText);
	}

	protected static String violationsAsOneString(Set<ConstraintViolation<CheckoutRequestTrade>> violations) {
		String result = "";
		for (ConstraintViolation<CheckoutRequestTrade> v : violations) {
			result += "Constraint violation: " + v.getPropertyPath() + "  " + v.getMessage() + System.lineSeparator();
		}
		return result;
	}

}
