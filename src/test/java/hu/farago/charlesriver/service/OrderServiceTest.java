package hu.farago.charlesriver.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.ordersvc.core.OrderSvcReturn;
import com.crd.ordersvc.soap.Trading;
import com.crd.ordersvc.soap.TradingImpl;

import hu.farago.charlesriver.model.dto.XLSOrder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	private ClientSession clientSession;

	@SuppressWarnings("deprecation")
	@Before
	public void before() throws ServiceException, TransportException {
		ClientSession tempSession = new ClientSession("test", "test", 0);
		clientSession = Mockito.spy(tempSession);
		doNothing().when(clientSession).logon(Mockito.anyString(), Mockito.anyString());
		
		Trading trd = Mockito.mock(TradingImpl.class);
		Mockito.when(clientSession.getTrading())
				.thenReturn(trd);
		Mockito.when(trd.createOrder(Mockito.any(), Mockito.any())).thenReturn(new OrderSvcReturn[] {});
	}

	@Test
	public void placeOrdersEmptyTest() throws ServiceException, TransportException, IOException {
		orderService.placeOrders(clientSession, Lists.newArrayList());
	}

	@Test
	public void placeOrdersSampleTest() throws ServiceException, TransportException, IOException {
		List<OrderSvcReturn> retList = orderService.placeOrders(clientSession, Lists.newArrayList(testLine()));
		assertNotNull(retList);
	}

	private XLSOrder testLine() {
		XLSOrder ret = new XLSOrder();

		ret.setAcctCd(305);
		ret.setBasketId("GC1");
		ret.setExecBroker("MERRILL");
		ret.setSecId("US0378331005");
		ret.setBbCode("AAPL US");
		ret.setTransType("BUYL");
		ret.setTargetQty(1000.0);
		ret.setFillQty(null);
		ret.setFillPrice(null);
		ret.setTargetCrrncy("USD");
		ret.setStrategyCd1("CVTZ");
		ret.setRefId("TZBY170621SPY");
		ret.setTradeDate(new Date());

		return ret;
	}
}
