# checkout-java
Java 7 wrapper for Checkout Finland (checkout.fi) payment API


Usage:
Create implementation for persistence interfaces (TransactionEntity and TransactionRepository)
```java
	
	public class MyTransactionEntity implements TransactionEntity {

    private DateTime startTime = DateTime.now();
    private DateTime endTime;
    private DateTime checkTime;
		private String stamp;
		private String paymentId;
		private Integer amount;
		private Integer status;
		private String reference;
		private String message;
		private String information;
		
		public MyTransactionEntity() {}
		
		public MyTransactionEntity(String stamp, String paymentId, String reference, Integer amount, String message) {
			this.stamp = stamp;
			this.paymentId = paymentId;
			this.reference = reference;
			this.amount = amount;
			this.message = message;
			this.status = CheckoutPaymentStatus.STATUS_TRANSACTION_UNFINISHED.getValue();
		}
	
		/*
			Implement/Override all getter and setters from fi.agisol.checkout.api.persistence.ITransactionEntity
			...
		*/
	}
	
	public class MyTransactionRepository implements TransactionRepository {
	
		@Override
		public TransactionEntity create(String stamp, String paymentId, String reference, Integer amount, String message) {
			// Create object which implements TransactionEntity interface
			return new MyTransactionEntity(stamp,paymentId, reference, amount, message);
		}
		
		@Override
		public TransactionEntity save(TransactionEntity transactionEntity) {
			// Save TransactionEntity to persistent storage	
			// ...
			return savedTransaction;
		}
		
		@Override
		public TransactionEntity findByStamp(String stamp) {
			// Find TransactionEntity from persistent storage by 'payment stamp' and return it
			// ...
			return foundTransaction;
		}
	}
	
```

Example usage
```java

	TransactionRepository repo = new MyTransactionRepository();
	Checkout checkout = CheckoutFactory.create(repo, "MyCheckoutLogin", "MyCheckoutPassword");

	Payment paymentData = new Payment();
	// Populate payment with Amount, Reference, return address, cancel address, Payer Name/Email/Address
	// etc...
	Trade trade = checkout.startPayment(paymentData);
	// Information from Trade object can be used to create HTML-form from which user can select bank
	// and start payment transaction...
	
	// When user has finished the payment at bank service, the bank returns to given return address.
	// At this point at the client side the PaymentEndResult can be created with information gotten from bank
	// and the payment can be ended 
	PaymentEndResult result = new PaymentEndResult(stamp, reference, paymentId, status, algorithm, mac);
	checkout.endPayment(paymentResult);
	
```

See usage also from "CheckoutIntegrationTest".
