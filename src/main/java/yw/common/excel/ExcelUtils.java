package yw.common.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import yw.common.utils.Objects;
import yw.common.utils.ResultUtil;

public class ExcelUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

	/**
	 * 检查工作簿中的表头字段 是否与指定的模板表头字段一致
	 *
	 * @param headers
	 * @param wb
	 * @return
	 */
	public static boolean vaildExcel(String[] headers, Workbook wb) {

		if (wb == null || wb.getNumberOfSheets() < 1) {
			LOGGER.error("Excel无内容,不符合模板要求 ...");
			return false;
		}

		// 取出每个工作簿
		for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
			Sheet sheet = wb.getSheetAt(sheetNum);
			// 取出第一行，第一行为表头
			Row row = sheet.getRow(0);

			if (row == null) {
				LOGGER.error("工作簿中无内容,不符合模板要求 ...");
				return false;
			}

			int length = row.getRowNum();

			for (int i = 0; i < length; i++) {
				if (!headers[i].equals(getValue(row.getCell(i)))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 检查工作簿中的表头字段 是否与指定的模板表头字段一致
	 *
	 * @param headers
	 * @param wb
	 * @return
	 */
	public static JSONObject vaildExcel(String[] headers0, String[] headers1, Workbook wb) {
		JSONObject result = new JSONObject();
		result.put("flag", false);
		if (wb == null || wb.getNumberOfSheets() < 1) {
			LOGGER.error("Excel无内容,不符合模板要求 ...");
			result.put("flag", false);
			result.put("result", ResultUtil.getResponse("500", "Excel无内容,不符合模板要求 ..."));
			return result;
		}

		// 取出每个工作簿
		for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
			Sheet sheet = null;
			if (sheetNum == 0) {
				sheet = wb.getSheetAt(sheetNum);
			} else {
				sheet = wb.getSheetAt(sheetNum);
			}
			// 取出第一行，第一行为表头
			Row row = sheet.getRow(0);

			if (row == null) {
				LOGGER.error("工作簿中无内容,不符合模板要求 ...");
				result.put("flag", false);
				result.put("result", ResultUtil.getResponse("500", "工作簿中无内容,不符合模板要求 ..."));
				return result;
			}
			int length = row.getRowNum();
			if (sheetNum == 0) {
				for (int i = 0; i < length; i++) {
					if (!headers0[i].equals(getValue(row.getCell(i)))) {
						LOGGER.error("工作簿中无内容,不符合模板要求 ...");
						result.put("flag", false);
						result.put("result", ResultUtil.getResponse("500", "工作簿中无内容,不符合模板要求 ..."));
						return result;
					}
				}
			} else {
				for (int i = 0; i < length; i++) {
					if (!headers1[i].equals(getValue(row.getCell(i)))) {
						LOGGER.error("工作簿中标题栏,不符合模板要求 ...");
						result.put("flag", false);
						result.put("result", ResultUtil.getResponse("500", "工作簿中标题栏,不符合模板要求 ..."));
						return result;
					}
				}
			}

		}
		return result;
	}

	public static boolean analyze(String filePath, ExcelHandler excelHandler) {

		Workbook wb = null;
		InputStream input = null;
		try {
			File file = new File(filePath);
			input = new FileInputStream(file);// 读取文件流
		} catch (IOException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		try {
			wb = WorkbookFactory.create(input); // 构建excel文件
		} catch (Exception e1) {
			LOGGER.error(e1.getMessage(), e1);
		}

		boolean vaild = vaildExcel(excelHandler.getExcelTemplateHeaders(), wb);

		if (!vaild) {
			LOGGER.error("Excel {} 与模板不符...");
			return false;
		}

		for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
			Sheet sheet = wb.getSheetAt(sheetNum);
			LOGGER.info("sheet {},name:{}", sheetNum, sheet.getSheetName());

			int LastRow = sheet.getLastRowNum();

			for (int i = 1; i <= LastRow; i++) {
				Row row = sheet.getRow(i);

				excelHandler.handle(row);
			}
		}
		return true;
	}

	public static String getPhoneValueString(Cell cell) {

		if (Objects.isNull(cell)) {
			return "";
		}
		if (Objects.isNullString(cell)) {
			return "";
		}

		try {
			BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
			return bd.toPlainString();
		} catch (Exception e) {
			// LOGGER.error(e.getMessage(), e);
			return getValue(cell);
		}
	}

	public static String getValue(Cell cell) {

		if (Objects.isNull(cell)) {
			return "";
		}
		cell.setCellType(Cell.CELL_TYPE_STRING);
		if (Objects.isNullString(cell)) {
			return "";
		}
		return cell.getStringCellValue().trim();
	}

	
}
