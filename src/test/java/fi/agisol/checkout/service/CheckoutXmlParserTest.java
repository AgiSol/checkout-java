package fi.agisol.checkout.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fi.agisol.checkout.api.dto.Bank;
import fi.agisol.checkout.api.dto.Trade;


public class CheckoutXmlParserTest {
	
	private CheckoutXmlParser parser;
	
	@Before
	public void setUp() {
		parser = new CheckoutXmlParser();
	}
	
	@Test
	public void getTrade_shouldReturnValidData() throws FileNotFoundException {
		InputStream fis = this.getClass().getResourceAsStream("/testdata/CheckoutResponseForTradeRequest.xml");
		Trade trade = parser.getTrade(fis);
		
		Assert.assertNotNull(trade);
		Assert.assertEquals(1490, trade.amount.intValue());
		Assert.assertEquals("My custom message", trade.message);
		Assert.assertEquals("Tero", trade.firstName);
		Assert.assertEquals("Testaaja", trade.lastName);
		Assert.assertEquals("28090883", trade.paymentId);
		
		Assert.assertNotNull(trade.banks);
		Assert.assertTrue(trade.banks.size() == 12);
		
		List<String> names = new ArrayList<String>();
		for (Bank bank: trade.banks) {
			names.add(bank.name);
		}
		
		Assert.assertTrue(names.size() == 12);
		Assert.assertArrayEquals(
				new String[]{"Nordea", "Osuuspankki", "Säästöpankki", "POP-Pankki", 
					"Aktia", "Danske Bank", "Handelsbanken",
					"S-Pankki", "Ålandsbanken", "LähiTapiola Pankki",
					"Neocard", "Tilisiirto"},
				names.toArray());
	}
	
	@Test
	public void getBankInformation_shouldReturnValidData() throws FileNotFoundException {
		InputStream fis = this.getClass().getResourceAsStream("/testdata/CheckoutResponseForCheckRequest.xml");
		CheckoutPaymentStatus status = parser.getStatus(fis);
		Assert.assertEquals(CheckoutPaymentStatus.STATUS_TRANSACTION_SUCCEEDED.getValue(), status.getValue());
	}
}
