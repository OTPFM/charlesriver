package hu.farago.charlesriver.service;

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

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

	@Autowired
	private OrderService orderService;
	
	private ClientSession clientSession;

	@Before
	public void before() throws ServiceException, TransportException {
		ClientSession tempSession = new ClientSession("test", "test", 0);
		clientSession = Mockito.spy(tempSession);
		doNothing().when(clientSession).logon(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void placeOrdersTest() {
		orderService.placeOrders(clientSession);
	}

}
