package fi.agisol.checkout.service;

import java.io.InputStream;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.agisol.checkout.api.Checkout;
import fi.agisol.checkout.api.dto.Payment;
import fi.agisol.checkout.api.dto.PaymentEndResult;
import fi.agisol.checkout.api.dto.Trade;
import fi.agisol.checkout.api.persistence.TransactionEntity;
import fi.agisol.checkout.api.persistence.TransactionRepository;
import fi.agisol.checkout.exceptions.CheckoutException;
import fi.agisol.checkout.exceptions.PaymentResultValidationException;
import fi.agisol.checkout.exceptions.PaymentDataValidationException;

public class CheckoutService implements Checkout {

    private TransactionRepository transactionRepository;
    
    private final Logger log = LoggerFactory.getLogger(CheckoutService.class);
    
    private final CheckoutXmlParser parser = new CheckoutXmlParser();
	
    private final CheckoutHttpClient client;

	
	private final Validator validator;

	private final String merchant;
	
	private final String password;
	
    public CheckoutService(TransactionRepository transactionRepository, String merchant, String password) {
        this.transactionRepository = transactionRepository;
		this.merchant = merchant;
		this.password = password;
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		
		client = new CheckoutHttpClient();
    }

    /* (non-Javadoc)
	 * @see fi.agisol.checkout.Checkout#startPayment(fi.agisol.checkout.api.PaymentMetaData, java.lang.String)
	 */
	@Override
	public Trade startPayment(Payment payment) {
		log.info("Starting payment: {}", payment );
		CheckoutRequestTrade tradeRequest = createTradeRequest(payment);
		
		InputStream response = client.postRequestAndGetResponseBody(tradeRequest);
		if(response == null) {
			throw new CheckoutException("Could not get trade data response from checkout!");
		}
		
		Trade tradeDto = parser.getTrade(response);

		log.debug("Creating and saving persistent TransactionEntity from started transaction");
		TransactionEntity ct = transactionRepository.create(
										tradeRequest.stamp,
										tradeDto.paymentId,
										tradeDto.reference,
										tradeDto.amount,
										tradeDto.message);
		
		transactionRepository.save(ct);

		log.info("Payment started and transaction entity saved.");
		
		return tradeDto;

	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.Checkout#endPayment(fi.agisol.checkout.dtos.CheckoutPaymentEndResultDto)
	 */
	@Override
	public boolean endPayment(PaymentEndResult paymentResult) {

		log.info("Ending payment with PaymentEndResult: {}", paymentResult );
		
    	TransactionEntity ct = getTransactionAndValidate(paymentResult);
		
    	// status should be ok after validation
		ct.setStatus(paymentResult.getStatus());
		ct.setEndTime(DateTime.now());
		
		CheckoutPaymentStatus paymentStatus = CheckoutPaymentStatus.getByValue(ct.getStatus());
		boolean debitSucceeded = false;
		if(paymentStatus == null) {
			String failureText = "Unknown status '%1$s' for transaction";
			setTransactionFailed(ct, failureText);
			log.warn(failureText);
		} else {
			debitSucceeded = paymentStatus.isDebitSucceeded();
		}
		
		transactionRepository.save(ct);
		
		log.info("Payment ended and transaction entity saved with debitSucceeded={}", debitSucceeded ? "true" : "false");
		
		return debitSucceeded;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.Checkout#updatePayment(fi.agisol.checkout.CheckoutTransaction)
	 */
	@Override
	public boolean updatePayment(String paymentStamp) {
		log.info("Updating payment with stamp: {}", paymentStamp );
		
		boolean statusUpdated = false;
		TransactionEntity transaction = this.transactionRepository.findByStamp(paymentStamp);
		if( transaction != null ) {
			
			CheckoutRequestCheck checkRequest = createCheckRequest(transaction);
			InputStream response = client.postRequestAndGetResponseBody(checkRequest);
			if(response == null) {
				throw new CheckoutException("Could not get check status data response from checkout!");
			}
			
			int currentStatus = parser.getStatus(response).getValue();
			
			int oldStatus = transaction.getStatus();
			
			statusUpdated = currentStatus != oldStatus;
			
			if(statusUpdated) {
				String newInfo = "* CHANGE -> " + LocalDateTime.now().toString() + 
						" status changed from '" + 
						oldStatus + "'->'" + currentStatus + "'. *";
				log.debug("Payment status changed -> {}", newInfo);
				
				String info = transaction.getInformation() != null ? transaction.getInformation() : "";
				info += newInfo;
				
				transaction.setInformation(info);
				transaction.setStatus(currentStatus);
				transaction.setCheckTime(DateTime.now());
				if( transaction.getEndTime() == null ) {
					transaction.setEndTime( transaction.getCheckTime() );
				}
				transaction = transactionRepository.save(transaction);
			} else {
				log.debug("Payment status not changed. Current status -> {}", currentStatus);
				transaction.setCheckTime(DateTime.now());
				transaction = transactionRepository.save(transaction);
			}
			
		} else {
			throw new CheckoutException("Could not find payment with paymentStamp '" + paymentStamp + "'.");
		}
		
		log.info("Updated payment successfull statusUpdated={}", statusUpdated ? "true":"false");
		
		return statusUpdated;
	}
    
	public boolean isCheckSumValid(PaymentEndResult paymentResult) {
		String calculated = paymentResult.calculateHash(password);
		String received = paymentResult.getMac();
		return calculated.equals(received);
	}

	private static void setTransactionFailed(TransactionEntity ct, String failureText) {
		ct.setStatus( CheckoutPaymentStatus.STATUS_TRANSACTION_SYSTEM_CANCEL.getValue() );
		ct.setInformation(failureText);
		ct.setEndTime(DateTime.now());
	}
	
	private TransactionEntity getTransactionAndValidate(PaymentEndResult paymentResult) {
		
		log.debug("Validating PaymentResult: {}", paymentResult);
		
		TransactionEntity ct = transactionRepository.findByStamp(paymentResult.getStamp());
		String violationsText = null;
		
    	if( ct == null ) {
    		ct = transactionRepository.create(
					paymentResult.getStamp(),
					paymentResult.getPaymentId(),
					paymentResult.getReference(),
					0,
					"");
    		violationsText = "Could not find matching transaction for CheckoutPaymentResult";
    	
    	} else {
		
	    	// saves ct as failed with reason and throws, if fails
    		Set<ConstraintViolation<PaymentEndResult>> violations = validator.validate(paymentResult);
    		if(!violations.isEmpty()) {
    			
    			String constraintViolationsText = "";
    			for (ConstraintViolation<PaymentEndResult> v : violations) {
    				constraintViolationsText += "Constraint violation: " + v.getPropertyPath() + 
    						"  " + v.getMessage() + System.lineSeparator();
    			}
    			
    			violationsText = 
    					"CheckoutPaymentResult field validation failed! Reasons ->" + 
    					System.lineSeparator() + 
    					constraintViolationsText;    			
    		
    		} else if( !ct.getPaymentId().equals( paymentResult.getPaymentId()) ) {
	
	    		violationsText = 
	    				String.format("CheckoutPaymentResult (stamp:%s) - payment id mismatch! " + 
							"Transaction PaymentID ('%s'), ResultID ('%s').",
							ct.getStamp(),
							ct.getPaymentId(), 
							paymentResult.getPaymentId());
				
	    		
	    	
	    	} else if( !isCheckSumValid(paymentResult) ) {
				
	    		violationsText = 
	    				String.format("CheckoutPaymentResult (stamp:%s) - checksum mismatch!",
	    						ct.getStamp());
	    		
			} else if( CheckoutPaymentStatus.getByValue(paymentResult.getStatus()) == null) {
	    		
				violationsText = 
	    				String.format("CheckoutPaymentResult (stamp:%s) - unknown status %d!",
	    						ct.getStamp(), paymentResult.getStatus());
				
			} else if( ct.getEndTime() != null ) {

	    		violationsText = "Matching transaction for CheckoutPaymentResult is already ended!";
	    		
			} else {
				log.debug("CheckoutPaymentResult for stamp:'{}' is valid", ct.getStamp());
			}
    	}
    	
    	// if violation found let's save transaction as failed and throw exception 
    	if(violationsText != null ) {
    		log.error(violationsText);
    		setTransactionFailed(ct, violationsText);
			transactionRepository.save(ct);
			
			throw new PaymentResultValidationException(violationsText);
    	}
    	
		return ct;
	}

	private CheckoutRequestCheck createCheckRequest(TransactionEntity ct) {
		return new CheckoutRequestCheck(ct.getStamp(), merchant, password, ct.getAmount(), ct.getReference());
	}
	
	private CheckoutRequestTrade createTradeRequest(Payment data) {
		
		log.debug("Creating and validating checkout tradeRequest from payment data with amount '{}' and reference '{}' for person: '{} {} {}' with return "
				+ "address '{}' and cancel address '{}'",
				data.getPaymentAmount(), data.getReference(), 
				data.getFirstName(), data.getLastName(), data.getEmail(), 
				data.getReturnUrl(), data.getCancelUrl());

		
		CheckoutRequestTrade tradeRequest = new CheckoutRequestTrade(
				merchant,
				password,
				data.getPaymentAmount(), 
				data.getReference(),
				data.getReturnUrl(), data.getCancelUrl(), 
				data.getRejectUrl(), data.getDelayedUrl(),
				data.getContent(),
				data.getDeliveryDate(),
				data.getFirstName(), data.getLastName(), data.getEmail(),
				data.getMessage(),
				data.getPhone(), data.getAddress(), data.getPostCode(), data.getPostOffice(), 
				data.getCountry(), data.getLanguage()
				);
		
		validate(tradeRequest);
		return tradeRequest;
	}
	
	private void validate(CheckoutRequestTrade checkoutRequest) {
		Set<ConstraintViolation<CheckoutRequestTrade>> violations = validator.validate(checkoutRequest);
		if(!violations.isEmpty()) {
			for (ConstraintViolation<CheckoutRequestTrade> v : violations) {
				log.warn("Constraint violation: " + v.getPropertyPath() + "  " + v.getMessage());
			}
			
			throw new PaymentDataValidationException(violations);
		}
	}

}


