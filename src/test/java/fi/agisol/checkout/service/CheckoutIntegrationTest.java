package fi.agisol.checkout.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fi.agisol.checkout.api.Checkout;
import fi.agisol.checkout.api.CheckoutFactory;
import fi.agisol.checkout.api.dto.Payment;
import fi.agisol.checkout.api.dto.PaymentEndResult;
import fi.agisol.checkout.api.dto.Trade;
import fi.agisol.checkout.api.persistence.TransactionEntity;

public class CheckoutIntegrationTest {

	private static final String FIRST_NAME = "Tero";
	private static final String EMAIL = "tero@testaaja.net";
	private static final LocalDate DELIVERY_DATE = LocalDate.now().plusDays(7);
	private static final String CANCEL_ADDRESS = "http://localhost/cancel";
	private static final String ADDRESS = "Mystreet 1";
	private static final Integer AMOUNT = 1490;
	private static final String REFERENCE = "12345";
	private static final Integer CONTENT = 1;
	private static final String LAST_NAME = "Testaaja";
	private static final String MESSAGE = "My custom message";
	private static final String DELAYED_URL = "http://localhost/delayed";
	private static final String PHONE = "+3581231234";
	private static final String POST_CODE = "12345";
	private static final String POST_OFFICE = "Tampere";
	private static final String REJECTED_URL = "http://localhost/rejected";
	private static final String RETURN_URL = "http://localhost/success";

	private TestTransactionRepository testRepo;
	private Checkout checkout;

	private Payment payment;

	private String CHECKOUT_TEST_MERCHANT = "375917";
	private String CHECKOUT_TEST_PASSWORD = "SAIPPUAKAUPPIAS";

	@Before
	public void setUp() {

		payment = new Payment();
		payment.setAddress(ADDRESS);
		payment.setCancelUrl(CANCEL_ADDRESS);
		payment.setContent(CONTENT);
		payment.setCountry("FIN");
		payment.setDelayedUrl(DELAYED_URL);
		payment.setDeliveryDate(DELIVERY_DATE);
		payment.setEmail(EMAIL);
		payment.setFirstName(FIRST_NAME);
		payment.setLanguage("FI");
		payment.setLastName(LAST_NAME);
		payment.setMessage(MESSAGE);
		payment.setPaymentAmount(AMOUNT);
		payment.setPhone(PHONE);
		payment.setPostCode(POST_CODE);
		payment.setPostOffice(POST_OFFICE);
		payment.setReference(REFERENCE);
		payment.setRejectUrl(REJECTED_URL);
		payment.setReturnUrl(RETURN_URL);

		testRepo = new TestTransactionRepository();
		checkout = CheckoutFactory.create(testRepo, CHECKOUT_TEST_MERCHANT, CHECKOUT_TEST_PASSWORD);
	}

	/***
	 * NOTE this test uses actual checkout.fi-API and FAILS IF NO INTERNET
	 * connection !
	 * 
	 * @throws InterruptedException 
	 */
	@Test
	public void startPayment_checkPayment_EndPayment_success() throws InterruptedException {
		
		Trade trade = checkout.startPayment(payment);
		testTrade(trade);
		
		Assert.assertEquals(1, testRepo.createCallCount);
		Assert.assertEquals(1, testRepo.saveCallCount);
		TransactionEntity startedTransaction = testRepo.transaction;
		
		testStartedTransaction(startedTransaction);

		// let's sleep 1 seconds before checking payment
		Thread.sleep(1000);
		

		boolean updatedSinceCreated = checkout.updatePayment(startedTransaction.getStamp());
		Assert.assertFalse(updatedSinceCreated);
		Assert.assertEquals(CheckoutPaymentStatus.STATUS_TRANSACTION_UNFINISHED.getValue(),
				startedTransaction.getStatus().intValue());
		

		Assert.assertEquals(2, testRepo.saveCallCount);
		TransactionEntity checkedTransaction = testRepo.transaction;
		assertThatTimeIsClose(DateTime.now(), checkedTransaction.getCheckTime());

		// let's sleep 1 seconds before ending payment
		Thread.sleep(1000);

		PaymentEndResult paymentResult = createSuccessfulPaymentResult(checkedTransaction, CHECKOUT_TEST_PASSWORD);
		

		Assert.assertTrue(checkout.endPayment(paymentResult));
		Assert.assertEquals(3, testRepo.saveCallCount);

		TransactionEntity endedTransaction = testRepo.transaction;
		testEndedTransaction(paymentResult, endedTransaction);
	}

	private static void testEndedTransaction(PaymentEndResult paymentResult, TransactionEntity transaction) {
		Assert.assertEquals(AMOUNT, transaction.getAmount());
		Assert.assertEquals(CheckoutPaymentStatus.STATUS_TRANSACTION_SUCCEEDED.getValue(),
				transaction.getStatus().intValue());
		Assert.assertEquals(MESSAGE, transaction.getMessage());
		assertThatTimeIsClose(DateTime.now(), transaction.getEndTime());
		Assert.assertEquals(paymentResult.getPaymentId(), transaction.getPaymentId());
		Assert.assertNull(transaction.getInformation());

		Assert.assertEquals(REFERENCE, transaction.getReference());
		Assert.assertEquals(paymentResult.getStamp(), transaction.getStamp());

		// should be inside +/-2 min
		assertThatTimeIsClose(DateTime.now(), transaction.getStartTime()); 
		assertThatTimeIsClose(DateTime.now(), transaction.getCheckTime());

	}

	private static PaymentEndResult createSuccessfulPaymentResult(TransactionEntity ct, String passwd) {
		PaymentEndResult paymentResult = new PaymentEndResult(ct.getStamp(), ct.getReference(), ct.getPaymentId(),
				CheckoutPaymentStatus.STATUS_TRANSACTION_SUCCEEDED.getValue(), CheckoutConstants.ALGORITH_MD5,
				"MAC_CALCULATED_LATER");
		String mac = paymentResult.calculateHash(passwd);
		paymentResult.setMac(mac);
		return paymentResult;
	}

	private static void testStartedTransaction(TransactionEntity transaction) {
		
		Assert.assertEquals(AMOUNT, transaction.getAmount());
		Assert.assertEquals(CheckoutPaymentStatus.STATUS_TRANSACTION_UNFINISHED.getValue(),
				transaction.getStatus().intValue());
		Assert.assertNull(transaction.getCheckTime());
		Assert.assertEquals(MESSAGE, transaction.getMessage());
		Assert.assertNull(transaction.getEndTime());
		Assert.assertNotNull(transaction.getPaymentId());
		Assert.assertNull(transaction.getInformation());

		Assert.assertEquals(REFERENCE, transaction.getReference());
		Assert.assertNotNull(transaction.getStamp());

		assertThatTimeIsClose(DateTime.now(), transaction.getStartTime());

	}

	private static void assertThatTimeIsClose(DateTime expected, DateTime actual) {
		Assert.assertTrue(
				String.format("Actual (%s) is earlier than expected (%s)!", actual.toString(), expected.toString()),
				expected.minusMinutes(1).isBefore(actual));
		Assert.assertTrue(
				String.format("Actual (%s) is later than expected (%s)!", actual.toString(), expected.toString()),
				expected.plusMinutes(1).isAfter(actual));
	}

	private static void testTrade(Trade trade) {
		Assert.assertNotNull(trade);
		Assert.assertEquals(AMOUNT, trade.amount);

		Assert.assertEquals(AMOUNT, trade.amount);
		Assert.assertEquals(MESSAGE, trade.message);
		Assert.assertEquals(FIRST_NAME, trade.firstName);
		Assert.assertEquals(LAST_NAME, trade.lastName);

		Assert.assertNotNull(trade.paymentId);
		Assert.assertNotNull(trade.reference);

		Assert.assertNotNull(trade.banks);
		Assert.assertFalse(trade.banks.isEmpty());
	}

}
