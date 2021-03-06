package hu.farago.charlesriver.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class XLSOrder {

	private Integer acctCd;
	private String basketId;
	private String execBroker;
	private String secId;
	private String bbCode;
	private String transType;
	private Double targetQty;
	private Double fillQty;
	private Double fillPrice;
	private String targetCrrncy;
	private String strategyCd1;
	private Date tradeDate;
	private String udfChar_2;
	private String refId;

	public Integer getAcctCd() {
		return acctCd;
	}

	public void setAcctCd(Integer acctCd) {
		this.acctCd = acctCd;
	}

	public String getBasketId() {
		return basketId;
	}

	public void setBasketId(String basketId) {
		this.basketId = basketId;
	}

	public String getExecBroker() {
		return execBroker;
	}

	public void setExecBroker(String execBroker) {
		this.execBroker = execBroker;
	}

	public String getSecId() {
		return secId;
	}

	public void setSecId(String secId) {
		this.secId = secId;
	}

	public String getBbCode() {
		return bbCode;
	}

	public void setBbCode(String bbCode) {
		this.bbCode = bbCode;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public Double getTargetQty() {
		return targetQty;
	}

	public void setTargetQty(Double targetQty) {
		this.targetQty = targetQty;
	}

	public Double getFillQty() {
		return fillQty;
	}

	public void setFillQty(Double fillQty) {
		this.fillQty = fillQty;
	}

	public Double getFillPrice() {
		return fillPrice;
	}

	public void setFillPrice(Double fillPrice) {
		this.fillPrice = fillPrice;
	}

	public String getTargetCrrncy() {
		return targetCrrncy;
	}

	public void setTargetCrrncy(String targetCrrncy) {
		this.targetCrrncy = targetCrrncy;
	}

	public String getStrategyCd1() {
		return strategyCd1;
	}

	public void setStrategyCd1(String strategyCd1) {
		this.strategyCd1 = strategyCd1;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	
	public String getUdfChar_2() {
		return udfChar_2;
	}
	
	public void setUdfChar_2(String udfChar_2) {
		this.udfChar_2 = udfChar_2;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, "udfChar_2", "tradeDate");
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
