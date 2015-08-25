package fi.agisol.checkout.exceptions;

// @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CheckoutException extends CheckoutExceptionBase {
	
	private static final long serialVersionUID = -3958385898092880021L;

	public CheckoutException(String text) {
		super(text);
	}

}