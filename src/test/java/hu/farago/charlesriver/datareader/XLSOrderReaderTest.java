package hu.farago.charlesriver.datareader;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hu.farago.charlesriver.model.dto.XLSOrder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XLSOrderReaderTest {

	@Autowired
	private XLSOrderReader reader;
	
	private File xlsFile;
	
	@Before
	public void before() {
		xlsFile = FileUtils.getFile("crd_inputjobs.xlsx");
	}
	
	@Test
	public void readFileAndRetrieveOrdersTest() throws IOException {
		assertNotNull(xlsFile);
		List<XLSOrder> orders = reader.readFileAndRetrieveOrders(xlsFile);
		assertNotNull(orders);
	}

}
