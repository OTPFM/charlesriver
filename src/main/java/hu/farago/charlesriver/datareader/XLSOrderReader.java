package hu.farago.charlesriver.datareader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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

	public List<XLSOrder> readFileAndRetrieveOrders(File xlsFile) throws Exception {
		XSSFWorkbook myWorkBook = null;
		try {
			FileInputStream fis = new FileInputStream(xlsFile);
			LOGGER.info("Opening workbook");
			myWorkBook = new XSSFWorkbook(fis);
			XSSFSheet mySheet = myWorkBook.getSheetAt(0);
			Iterator<Row> rowIterator = mySheet.iterator();

			List<XLSOrder> orders = new LinkedList<>();
			rowIterator.next(); // skip the first line
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getCell(0).getNumericCellValue() == 0) { // "For blank cells we return a 0. "
					// we read all the valid lines
					break;
				}
				XLSOrder order = makeXLSOrderByRow(row);
				orders.add(order);
			}
			return orders;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		} finally {
			if (myWorkBook != null) {
				try {
					LOGGER.info("Closing workbook");
					myWorkBook.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
	}

	private XLSOrder makeXLSOrderByRow(Row row) throws ParseException {
		XLSOrder order = new XLSOrder();
		Iterator<Cell> cellIterator = row.cellIterator();

		order.setAcctCd(readCellValueNumber(cellIterator).intValue());

		order.setBasketId(readCellValueString(cellIterator));
		order.setExecBroker(readCellValueString(cellIterator));
		order.setSecId(readCellValueString(cellIterator));
		order.setBbCode(readCellValueString(cellIterator));
		order.setTransType(readCellValueString(cellIterator));

		order.setTargetQty(readCellValueNumber(cellIterator));
		order.setFillQty(readCellValueNumber(cellIterator));
		order.setFillPrice(readCellValueNumber(cellIterator));

		order.setTargetCrrncy(readCellValueString(cellIterator));
		order.setStrategyCd1(readCellValueString(cellIterator));

		order.setTradeDate(readCellValueDate(cellIterator, "yyyy-MM-dd"));
		order.setUdfChar_2(readCellValueString(cellIterator));

		order.setRefId(readCellValueString(cellIterator));
		return order;
	}

	/*
	 * Cell reading operations
	 */

	private String readCellValueString(Iterator<Cell> cellIterator) {
		if (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			return cell.getStringCellValue();
		}
		return null;
	}

	private Double readCellValueNumber(Iterator<Cell> cellIterator) {
		if (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			switch (cell.getCellTypeEnum()) {
			case NUMERIC:
				return cell.getNumericCellValue();
			case STRING:
				String cellStr = cell.getStringCellValue();
				if (StringUtils.isEmpty(cellStr)) {
					return null;
				}
				try {
					return Double.valueOf(cellStr);
				} catch (Exception e) {
					try {
						return new Double(Integer.valueOf(cellStr));
					} catch (Exception e3) {
						return null;
					}
				}
			default:
				return null;
			}
		}
		return null;
	}

	private Date readCellValueDate(Iterator<Cell> cellIterator, String pattern) throws ParseException {
		if (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			return DateUtils.parseDate(cell.getStringCellValue(), pattern);
		}
		return null;
	}
}
