package hu.farago.charlesriver.model.dto;

import java.util.LinkedList;
import java.util.List;

import com.crd.ordersvc.core.OrderProcessResult;
import com.crd.ordersvc.core.OrderSvcReturn;

public class SvcReturn {

	public boolean successful;
	public String refId;
	public String code;
	public String message;
	public List<SvcReturn> processResult = new LinkedList<>();

	public SvcReturn() {
	}

	public SvcReturn(String code, String message) {
		this.code = code;
		this.message = message;
		this.successful = false;
	}

	public static SvcReturn buildReturn(OrderSvcReturn ret) {
		SvcReturn newRet = new SvcReturn();

		newRet.successful = !ret.getHasError();
		if (newRet.successful)
			newRet.refId = ret.getRefId();

		OrderProcessResult[] orderProcessResults = ret.getResultArray();
		if (orderProcessResults != null) {
			for (int i = 0; i < orderProcessResults.length; i++) {
				OrderProcessResult res = orderProcessResults[i];
				newRet.processResult.add(new SvcReturn(res.getMsgCode(), res.getReason()));
			}
		}
		return newRet;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public String getRefId() {
		return refId;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
	public List<SvcReturn> getProcessResult() {
		return processResult;
	}

}
