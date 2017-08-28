package hu.farago.charlesriver.service;

import java.io.IOException;
import java.util.List;

import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.crd.beans.OrderAllocationBean;
import com.crd.beans.OrderBean;
import com.crd.beans.OrderBeanStream;
import com.crd.beans.OrderFillBean;
import com.crd.beans.OrderPlacementBean;
import com.crd.beans.lookup.OrderLookupSupport;
import com.crd.beans.lookup.SecurityLookup;
import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.client.util.ResultSetRequest;
import com.crd.ordersvc.core.OrderSvcReturn;
import com.crd.ordersvc.soap.Trading;

import hu.farago.charlesriver.model.dto.XLSOrder;

@Service
public class OrderService {

	private static final String BV = "BV";
	private static final String GC = "GC";

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

	public List<OrderSvcReturn> placeOrders(ClientSession clientSession, List<XLSOrder> orders)
			throws ServiceException, TransportException, IOException {

		Trading trading = clientSession.getTrading();
		List<OrderSvcReturn> returns = Lists.newArrayList();

		for (XLSOrder xlsOrder : orders) {
			LOGGER.info("Processing row: " + xlsOrder.toString());
			if (hasFill(xlsOrder)) {
				placeXLSOrderWithFill(trading, returns, xlsOrder);
			} else {
				placeXLSOrderWithoutFill(trading, returns, xlsOrder);
			}
		}

		return returns;
	}

	private void placeXLSOrderWithoutFill(Trading trading, List<OrderSvcReturn> returns, XLSOrder xlsOrder)
			throws ServiceException, TransportException {
		OrderBean orderBean = createBeanWithAllocation(xlsOrder);

		OrderSvcReturn[] orderSvcReturns = trading.createOrder(new OrderBean[] { orderBean }, null);
		returns.addAll(Lists.newArrayList(orderSvcReturns));
	}

	private void placeXLSOrderWithFill(Trading trading, List<OrderSvcReturn> returns, XLSOrder xlsOrder)
			throws ServiceException, TransportException, IOException {
		ResultSetRequest request = new ResultSetRequest("hu.farago.charlesriver.service.OrderService");
		request.addParameter("refId", xlsOrder.getRefId());

		OrderBeanStream beanStream = trading.fetchOrder(request.getResultSetName(), request.getRequestedColumns(),
				request.getPredicate(), request.getStoredQueryId(), request.getParameters());

		OrderBean oldBean = beanStream.read();

		if (oldBean == null) {
			oldBean = createBeanWithAllocation(xlsOrder);
			decorateOrderBeanWithFill(xlsOrder, oldBean);
			
			OrderSvcReturn[] orderSvcReturns = trading.createOrder(new OrderBean[] { oldBean }, null);
			returns.addAll(Lists.newArrayList(orderSvcReturns));
		} else {
			decorateOrderBeanWithFill(xlsOrder, oldBean);
			
			OrderSvcReturn[] orderSvcReturns = trading.updateOrder(new OrderBean[] { oldBean }, null);
			returns.addAll(Lists.newArrayList(orderSvcReturns));
		}
	}

	private void decorateOrderBeanWithFill(XLSOrder xlsOrder, OrderBean oldBean) {
		OrderPlacementBean placement = new OrderPlacementBean();
		populateOrderPlacementBean(placement, xlsOrder);

		OrderFillBean fill = new OrderFillBean();
		populateOrderFillBean(fill, xlsOrder);
		placement.setFillBeans(new OrderFillBean[] { fill });

		oldBean.setPlacementBeans(new OrderPlacementBean[] { placement });
	}

	private OrderBean createBeanWithAllocation(XLSOrder xlsOrder) {
		OrderBean orderBean = new OrderBean(true);
		populateOrderBean(orderBean, xlsOrder);

		OrderAllocationBean orderAllocationBean = new OrderAllocationBean(true);
		populateOrderAllocationBean(orderAllocationBean, xlsOrder);
		orderBean.setAllocationBeans(new OrderAllocationBean[] { orderAllocationBean });
		return orderBean;
	}

	private boolean hasFill(XLSOrder xlsOrder) {
		return xlsOrder.getFillQty() != null && xlsOrder.getFillPrice() != null;
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
