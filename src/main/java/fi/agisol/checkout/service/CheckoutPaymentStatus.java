package fi.agisol.checkout.service;

/**
 * See more info from:
 *  - http://www.checkout.fi/materiaalit/tekninen-materiaali/
 *  - page 5/7 -> http://www.checkout.fi/wp-content/uploads/2015/04/CTD-Tekninenrajapintakuvaus-210415-1146-170.pdf
 *  (referred 2015-08-20)
 */
public enum CheckoutPaymentStatus {

	STATUS_REPAID_TO_CUSTOMER(-10, false), 
	STATUS_TRANSACTION_NOT_FOUND(-4, false), 
	STATUS_TRANSACTION_TIMEOUT(-3, false), 
	STATUS_TRANSACTION_SYSTEM_CANCEL(-2, false), 
	STATUS_TRANSACTION_USER_CANCEL(-1, false), 
	STATUS_TRANSACTION_UNFINISHED(1, false),
	STATUS_TRANSACTION_SUCCEEDED(2, true), 
	STATUS_TRANSACTION_DELAYED(3, false),
	STATUS_RESERVED_STATUS4(4, false), // should work like delayed 
	STATUS_RESERVED_STATUS5(5, true), // should work like succeeded
	STATUS_TRANSACTION_FROZEN(6, true), 
	STATUS_TRANSACTION_REQUIRES_APPROVAL(7, true), 
	STATUS_TRANSACTION_APPROVED(8, true), 
	STATUS_RESERVED_STATUS9(9, true), 
	STATUS_PAYMENT_SETTLED(10, true);

	public static final int MIN_VALUE = -10;
	public static final int MAX_VALUE = 10;

	private final int value;
	private final boolean debitSucceeded;

	private CheckoutPaymentStatus(int value, boolean debitSucceeded) {
		this.value = value;
		this.debitSucceeded = debitSucceeded;
	}

	public int getValue() {
		return value;
	}

	public boolean isDebitSucceeded() {
		return debitSucceeded;
	}

	public static CheckoutPaymentStatus parse(String enumValue) {
		CheckoutPaymentStatus status = null;

		try {
			status = getByValue(Integer.parseInt(enumValue));

		} catch (NumberFormatException ex) {
			status = null;
		}

		return status;
	}

	public static CheckoutPaymentStatus getByValue(int enumValue) {
		CheckoutPaymentStatus status = null;

		for (CheckoutPaymentStatus item : CheckoutPaymentStatus.values()) {
			if (item.getValue() == enumValue) {
				status = item;
				break;
			}
		}

		return status;
	}
}
