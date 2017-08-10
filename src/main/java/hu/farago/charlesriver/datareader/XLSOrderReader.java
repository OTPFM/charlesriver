package hu.farago.charlesriver.datareader;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import hu.farago.charlesriver.model.dto.XLSOrder;

@Component
public class XLSOrderReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XLSOrderReader.class);

	public List<XLSOrder> readFileAndRetrieveOrders(File xlsFile) {
		return null;
	}

}
