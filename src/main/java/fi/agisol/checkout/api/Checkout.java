package fi.agisol.checkout.api;

import fi.agisol.checkout.api.dto.Payment;
import fi.agisol.checkout.api.dto.PaymentEndResult;
import fi.agisol.checkout.api.dto.Trade;
import fi.agisol.checkout.api.persistence.TransactionEntity;
import fi.agisol.checkout.exceptions.PaymentDataValidationException;
import fi.agisol.checkout.exceptions.PaymentResultValidationException;

/**
 * Wrapper for checkout.fi HTTP-API
 * 
 * @author www.agisol.fi
 */
public interface Checkout {

	/***
	 * Start checkout payment by sending request to checkout-API and returning TradeData
	 * 	@param payment	information about payment (amount, reference, origin, etc)
	 * @return Trade object containing information about started payment (payment and bank information etc.)
	 * 
	 * Creates and saves the transaction through registered TransactionRepository
	 * 	@see fi.agisol.checkout.api.persistence.TransactionRepository#create(String, String payment, String reference, Integer amount, String message);
	 * 	@see fi.agisol.checkout.api.persistence.TransactionRepository#save(TransactionEntity ct)
	 * 
	 * @throws CheckoutException if cannot get Trade response from Checkout.fi API
	 * @throws PaymentDataValidationException if payment data valid according to checkout.fi regulations
	 */
	Trade startPayment(Payment payment);
	
	
	/***
	 * End checkout payment by giving payment result and updates the transaction entity to registered repository
	 * 	@param paymentResult which is populated with the data gotten from bank after payment ended
	 * @return TRUE if payment and debit was succeeded (else FALSE)
	 * 
	 * Uses registered TransactionRepository to find started transaction and to update it
	 * 	@see fi.agisol.checkout.api.persistence.findByStamp#save(String stamp)
	 * 	@see fi.agisol.checkout.api.persistence.TransactionRepository#save(TransactionEntity ct)
	 * If active transaction isn't found, failed transaction will be persisted via TransactionRepository
	 * 	@see fi.agisol.checkout.api.persistence.TransactionRepository#create(String, String payment, String reference, Integer amount, String message);
	 *
	 * @throws PaymentResultValidationException if paymentResult contains some errors or matching transaction isn't found
	 */
	boolean endPayment(PaymentEndResult paymentResult);
	

	/***
	 * Checks the status of the payment by sending request to checkout-API and updating transaction entity
	 * according to it.
	 * @param paymentStamp payment stamp which should correspond to "active payment"
	 * @return TRUE if status of the payment changes (else FALSE)
	 * 
	 * Uses registered TransactionRepository to find started transaction and to update it
	 * 	@see fi.agisol.checkout.api.persistence.findByStamp#save(String stamp)
	 * 	@see fi.agisol.checkout.api.persistence.TransactionRepository#save(TransactionEntity ct)
	 * 
	 * @throws CheckoutException if cannot get Status response from Checkout.fi API 
	 * 		 	or cannot find payment with given stamp
	 */
	boolean updatePayment(String paymentStamp);

}