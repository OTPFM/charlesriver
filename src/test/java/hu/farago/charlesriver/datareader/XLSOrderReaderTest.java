package hu.farago.charlesriver.datareader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import hu.farago.charlesriver.model.dto.XLSOrder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XLSOrderReaderTest {

	private static final String CRD_INPUTJOBS_XLSX = "/crd_inputjobs.xlsx";

	@Autowired
	private XLSOrderReader reader;
	
	private File xlsFile;
	
	@Before
	public void before() throws FileNotFoundException {
		xlsFile = ResourceUtils.getFile(this.getClass().getResource(CRD_INPUTJOBS_XLSX));
	}
	
	@Test
	public void readFileAndRetrieveOrdersTest() throws Exception {
		assertNotNull(xlsFile);
		List<XLSOrder> orders = reader.readFileAndRetrieveOrders(xlsFile);
		assertNotNull(orders);
		assertEquals(3, orders.size());
		assertEquals(testLine(), orders.get(0));
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
		
		return ret;
	}

}
