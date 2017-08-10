package hu.farago.charlesriver.datareader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import hu.farago.charlesriver.model.dto.XLSOrder;

@Component
public class XLSOrderReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(XLSOrderReader.class);

	public List<XLSOrder> readFileAndRetrieveOrders(File xlsFile) {
		XSSFWorkbook myWorkBook = null;
		try {
			FileInputStream fis = new FileInputStream(xlsFile);
			myWorkBook = new XSSFWorkbook(fis);
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIterator = mySheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (myWorkBook != null) {
				try {
					myWorkBook.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}

		return null;
	}

}
