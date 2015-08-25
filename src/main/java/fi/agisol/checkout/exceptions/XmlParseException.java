package fi.agisol.checkout.exceptions;

//@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class XmlParseException extends RuntimeException {
	private static final long serialVersionUID = 7329549724412246907L;

	public XmlParseException(String text) {
		super(text);
	}

}