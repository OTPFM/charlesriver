package hu.farago.charlesriver.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import hu.farago.charlesriver.model.dto.Credentials;
import hu.farago.charlesriver.model.dto.SvcReturn;
import hu.farago.charlesriver.model.dto.XLSOrder;
import hu.farago.charlesriver.service.other.CredentialsService;

@Service
public class OrderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
	
	@Autowired
	private CredentialsService credentialService;

	public List<OrderSvcReturn> placeOrders(ClientSession clientSession, List<XLSOrder> orders)
			throws ServiceException, TransportException, IOException {

		Trading trading = clientSession.getTrading();
		List<OrderSvcReturn> returns = new ArrayList<OrderSvcReturn>();

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

	public List<SvcReturn> placeOrdersClient(ClientSession clientSession, List<XLSOrder> orders)
			throws ServiceException, TransportException, IOException {
		return placeOrders(clientSession, orders).parallelStream().map(ret -> SvcReturn.buildReturn(ret))
				.collect(Collectors.toList());
	}

	private void placeXLSOrderWithoutFill(Trading trading, List<OrderSvcReturn> returns, XLSOrder xlsOrder)
			throws ServiceException, TransportException {
		OrderBean orderBean = createBeanWithAllocation(xlsOrder);

		OrderSvcReturn[] orderSvcReturns = trading.createOrder(new OrderBean[] { orderBean }, null);
		returns.addAll(toList(orderSvcReturns));
	}

	private void placeXLSOrderWithFill(Trading trading, List<OrderSvcReturn> returns, XLSOrder xlsOrder)
			throws ServiceException, TransportException, IOException {
		ResultSetRequest request = new ResultSetRequest(credentialService.getCredentials().query);
		request.addParameter("refId", xlsOrder.getRefId());
		request.addRequestedColumn("OrderId");

		OrderBeanStream beanStream = trading.fetchOrder(request.getResultSetName(), request.getRequestedColumns(),
				request.getPredicate(), request.getStoredQueryId(), request.getParameters());

		OrderBean oldBean = beanStream.read();

		if (oldBean == null) {
			oldBean = createBeanWithAllocation(xlsOrder);
			decorateOrderBeanWithFill(xlsOrder, oldBean);

			OrderSvcReturn[] orderSvcReturns = trading.createOrder(new OrderBean[] { oldBean }, null);
			returns.addAll(toList(orderSvcReturns));
		} else {
			LOGGER.info("Update order for RefId: " + xlsOrder.getRefId());
			decorateOrderBeanWithFill(xlsOrder, oldBean);

			OrderSvcReturn[] orderSvcReturns = trading.createPlacementAndFill(oldBean.getPlacementBeans());
			returns.addAll(toList(orderSvcReturns));
		}
	}

	private void decorateOrderBeanWithFill(XLSOrder xlsOrder, OrderBean oldBean) {
		OrderPlacementBean placement = new OrderPlacementBean();
		placement.setPlaceQty(xlsOrder.getTargetQty());

		OrderFillBean fill = new OrderFillBean();
		fill.setFillQty(xlsOrder.getFillQty());
		fill.setFillPrice(xlsOrder.getFillPrice());
		placement.setFillBeans(new OrderFillBean[] { fill });
		
		// back and forth linking....pathetic
		placement.setOrderId(oldBean.getOrderId());
		fill.setOrderId(oldBean.getOrderId());
		oldBean.setPlacementBeans(new OrderPlacementBean[] { placement });
	}

	private OrderBean createBeanWithAllocation(XLSOrder xlsOrder) {
		LOGGER.info("Create new order for RefId: " + xlsOrder.getRefId());
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

		Credentials cred = credentialService.getCredentials();
		
		orderBean.setBasketId(cred.basket);
		orderBean.setTransType(xlsOrder.getTransType());
		orderBean.setTrader(cred.trader);
		orderBean.setManager(cred.supervisor);
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

	// utility

	private Collection<? extends OrderSvcReturn> toList(OrderSvcReturn[] orderSvcReturns) {
		List<OrderSvcReturn> ret = new ArrayList<>(orderSvcReturns.length);
		for (OrderSvcReturn svcReturn : orderSvcReturns) {
			ret.add(svcReturn);
		}
		return ret;
	}
}
