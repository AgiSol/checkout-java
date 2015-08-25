package fi.agisol.checkout.exceptions;

// @ResponseStatus(HttpStatus.FORBIDDEN)
public class PaymentResultValidationException extends CheckoutExceptionBase {

	private static final long serialVersionUID = -2277793579447226049L;

	public PaymentResultValidationException(String violations) {
		super("CheckoutPaymentResult had following validation violations: " + 
				System.lineSeparator() + violations);
	}

}
