package fi.agisol.checkout.service;

import fi.agisol.checkout.api.persistence.TransactionEntity;
import fi.agisol.checkout.api.persistence.TransactionRepository;

public class TestTransactionRepository implements TransactionRepository {
	
	public TransactionEntity transaction;
	public int createCallCount = 0;
	public int saveCallCount = 0;
	public int findByStampCallCount = 0;
	
	
	@Override
	public TransactionEntity create(String stamp, String paymentId, String reference, Integer amount, String message) {
		createCallCount++;
		return new TestTransactionEntity(stamp,paymentId, reference, amount, message);
	}

	@Override
	public TransactionEntity save(TransactionEntity transactionEntity) {
		saveCallCount++;
		this.transaction = transactionEntity;
		return transaction;
	}

	@Override
	public TransactionEntity findByStamp(String stamp) {
		findByStampCallCount++;
		return this.transaction;
	}


}
