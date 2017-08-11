package hu.farago.charlesriver.service;

import java.util.List;

import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.crd.beans.OrderAllocationBean;
import com.crd.beans.OrderBean;
import com.crd.beans.OrderFillBean;
import com.crd.beans.OrderPlacementBean;
import com.crd.beans.lookup.OrderLookupSupport;
import com.crd.beans.lookup.SecurityLookup;
import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.ordersvc.core.OrderSvcReturn;
import com.crd.ordersvc.soap.Trading;

import hu.farago.charlesriver.model.dto.XLSOrder;

@Service
public class OrderService {

	private static final String BV = "BV";
	private static final String GC = "GC";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

	public List<OrderSvcReturn> placeOrders(ClientSession clientSession, List<XLSOrder> orders)
			throws ServiceException, TransportException {
		List<OrderSvcReturn> returns = Lists.newArrayList();
		for (XLSOrder xlsOrder : orders) {
			LOGGER.info("Processing row: " + xlsOrder.toString());
			OrderBean orderBean = new OrderBean(true);
			populateOrderBean(orderBean, xlsOrder);

			OrderAllocationBean orderAllocationBean = new OrderAllocationBean(true);
			populateOrderAllocationBean(orderAllocationBean, xlsOrder);
			orderBean.setAllocationBeans(new OrderAllocationBean[] { orderAllocationBean });

//			OrderPlacementBean placement = new OrderPlacementBean();
//			populateOrderPlacementBean(placement, xlsOrder);
//			orderBean.setPlacementBeans(new OrderPlacementBean[] { placement });
//
//			if (xlsOrder.getFillQty() != null) {
//				OrderFillBean fill = new OrderFillBean();
//				populateOrderFillBean(fill, xlsOrder);
//			}

			Trading trading = clientSession.getTrading();
			OrderSvcReturn[] orderSvcReturns = trading.createOrder(new OrderBean[] { orderBean }, null);
			returns.addAll(Lists.newArrayList(orderSvcReturns));
		}

		return returns;
	}

	private void populateOrderBean(OrderBean orderBean, XLSOrder xlsOrder) {
		SecurityLookup secLkp = new SecurityLookup();
		secLkp.setIsin(xlsOrder.getSecId());

		OrderLookupSupport ordSupt = new OrderLookupSupport();
		ordSupt.setSecId(secLkp);

		orderBean.setLookupSupportBean(ordSupt);

		// orderBean.setScenarioId(700500003);
		// orderBean.setBasketId(); // xlsOrder.getBasketId()
		orderBean.setTransType(xlsOrder.getTransType());
		orderBean.setTrader(BV);
		orderBean.setManager(GC);
		orderBean.setStrategyCd1(xlsOrder.getStrategyCd1());
		orderBean.setExecBroker(xlsOrder.getExecBroker());
		orderBean.setTradeDate(new java.sql.Date(xlsOrder.getTradeDate().getTime()));
		orderBean.setUdfChar_2(xlsOrder.getUdfChar_2());
		orderBean.setRefId(xlsOrder.getRefId());
	}

	private void populateOrderAllocationBean(OrderAllocationBean orderAllocationBean, XLSOrder xlsOrder) {
		orderAllocationBean.setAcctCd(xlsOrder.getAcctCd() + "");
		orderAllocationBean.setTargetQty(xlsOrder.getTargetQty());
	}

	private void populateOrderPlacementBean(OrderPlacementBean orderPlacementBean, XLSOrder xlsOrder) {
		orderPlacementBean.setPlaceQty(xlsOrder.getTargetQty());
	}

	private void populateOrderFillBean(OrderFillBean fill, XLSOrder xlsOrder) {
		fill.setFillQty(xlsOrder.getFillQty());
		fill.setFillPrice(xlsOrder.getFillPrice());
	}
}
