package fi.agisol.checkout.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fi.agisol.checkout.api.dto.PaymentEndResult;

public class CheckoutServiceTest {

	private String CHECKOUT_TEST_MERCHANT = "375917";
	private String CHECKOUT_TEST_PASSWORD = "SAIPPUAKAUPPIAS";
	private CheckoutService checkoutService;
	
	@Before
	public void setUp() {
		checkoutService = new CheckoutService(null, CHECKOUT_TEST_MERCHANT, CHECKOUT_TEST_PASSWORD);
	}
	
	@Test
	public void isCheckSumValid_shouldSucceed() {
		PaymentEndResult paymentResult = new PaymentEndResult("1428926188", "0002", "23768792", 2, 2,
				"B22F9B07FFA520513172D73D3D2C3227");
		Assert.assertTrue(checkoutService.isCheckSumValid(paymentResult));
	}
}
