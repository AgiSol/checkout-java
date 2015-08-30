package fi.agisol.checkout.service;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import fi.agisol.checkout.api.persistence.TransactionEntity;

public class TestTransactionEntity implements TransactionEntity {

	@Override
	public String toString() {
		return "TransactionEntity [stamp=" + stamp 
				+ ", paymentId=" + paymentId
				+ ", startTime=" + startTime
				+ ", endTime=" + endTime + ", checkTime=" + checkTime
				+ ", message=" + message + ", amount=" + amount
				+ ", status=" + status
				+ ", information=" + information +  "]";
	}

    @NotNull
    private DateTime startTime = DateTime.now();

    private DateTime endTime;

    private DateTime checkTime;

    @NotNull
	private String stamp;

    @NotNull
	private String paymentId;

    @NotNull
	private Integer amount;

    @NotNull
	private Integer status;

    @NotNull
	private String reference;
    
	private String message;

    private String information;
    

    
    public TestTransactionEntity() {}

    public TestTransactionEntity(String stamp, String paymentId, String reference, Integer amount, String message) {
		this.stamp = stamp;
		this.paymentId = paymentId;
		this.reference = reference;
		this.amount = amount;
		this.message = message;
		this.status = CheckoutPaymentStatus.STATUS_TRANSACTION_UNFINISHED.getValue();
	}

	public void setTransactionFailed(String failureInfo) {
		this.setStatus( CheckoutPaymentStatus.STATUS_TRANSACTION_SYSTEM_CANCEL.getValue() );
		this.setInformation(failureInfo);
		this.setEndTime(DateTime.now());
	}
	
	/**
     * Returns boolean value which tells if transaction is open and active
     * 
     * @return
     */
    public Boolean isActive() {
    	return endTime == null;
    }
    
    /* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getStartTime()
	 */
	@Override
	public DateTime getStartTime() {
		return startTime;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setStartTime(org.joda.time.DateTime)
	 */
	@Override
	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getEndTime()
	 */
	@Override
	public DateTime getEndTime() {
		return endTime;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setEndTime(org.joda.time.DateTime)
	 */
	@Override
	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getStamp()
	 */
	@Override
	public String getStamp() {
		return stamp;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setStamp(java.lang.String)
	 */
	@Override
	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getPaymentId()
	 */
	@Override
	public String getPaymentId() {
		return paymentId;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setPaymentId(java.lang.String)
	 */
	@Override
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setMessage(java.lang.String)
	 */
	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getAmount()
	 */
	@Override
	public Integer getAmount() {
		return amount;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setAmount(java.lang.Integer)
	 */
	@Override
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getStatus()
	 */
	@Override
	public Integer getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setStatus(java.lang.Integer)
	 */
	@Override
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getInformation()
	 */
	@Override
	public String getInformation() {
		return information;
	}

	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setInformation(java.lang.String)
	 */
	@Override
	public void setInformation(String information) {
		this.information = information;
	}

    
    /* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getCheckTime()
	 */
	@Override
	public DateTime getCheckTime() {
		return checkTime;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setCheckTime(org.joda.time.DateTime)
	 */
	@Override
	public void setCheckTime(DateTime checkTime) {
		this.checkTime = checkTime;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#getReference()
	 */
	@Override
	public String getReference() {
		return reference;
	}
	
	/* (non-Javadoc)
	 * @see fi.agisol.checkout.api.persistence.ITransactionEntity#setReference(java.lang.String)
	 */
	@Override
	public void setReference(String reference) {
		this.reference = reference;
	}
}
