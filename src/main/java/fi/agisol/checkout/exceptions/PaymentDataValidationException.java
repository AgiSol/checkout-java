package fi.agisol.checkout.exceptions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import fi.agisol.checkout.service.CheckoutRequestTrade;

// @ResponseStatus(HttpStatus.FORBIDDEN)
public class PaymentDataValidationException extends CheckoutExceptionBase {
	
	private static final long serialVersionUID = 75851997983694452L;

	public PaymentDataValidationException(Set<ConstraintViolation<CheckoutRequestTrade>> violations) {
		super("CheckoutTradeRequest had following validation violations: " + 
				System.lineSeparator() + violationsAsOneString(violations));
	}

}