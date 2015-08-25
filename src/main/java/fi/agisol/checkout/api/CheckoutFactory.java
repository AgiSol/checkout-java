package fi.agisol.checkout.api;

import fi.agisol.checkout.api.persistence.TransactionRepository;
import fi.agisol.checkout.service.CheckoutService;

/**
 * Factory to create wrapper for checkout.fi HTTP-API
 * 
 * @author www.agisol.fi
 */
public class CheckoutFactory {
	
	/***
	 * Create Checkout implementation
	 * 	@param repository reference to repository implementation (Eg. implementation could store transaction data to DB)
	 * 	@param merchant user name for the Checkout service
	 * 	@param password password for the Checkout service
	 * @return implementation for Checkout
	 */
	public static Checkout create(TransactionRepository repository, String merchant, String password) {
		return new CheckoutService(repository, merchant, password);
	}
}
