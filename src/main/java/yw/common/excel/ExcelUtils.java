package yw.common.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
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
	 * @param wb
	 * @param excelHandlers
	 * @return
	 */
	public static boolean vaildExcel(Workbook wb, ExcelHandler... excelHandlers) {

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

			String headers[] = excelHandlers[sheetNum].getExcelTemplateHeaders();

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
			System.out.println("sheet.getSheetName()" + sheet.getSheetName());

			// 取出第一行，第一行为表头
			Row row = sheet.getRow(0);
			int length1 = row.getRowNum();
			System.out.println(length1);

			if (row == null) {
				LOGGER.error("工作簿中无内容,不符合模板要求 ...");
				result.put("flag", false);
				result.put("result", ResultUtil.getResponse("500", "工作簿中无内容,不符合模板要求 ..."));
				return result;
			}
			int FirstCellNum = row.getFirstCellNum();
			int LastCellNum = row.getLastCellNum();
			if (sheetNum == 0) {
				for (int i = FirstCellNum; i < LastCellNum; i++) {
					String cellValue = getValue(row.getCell(i));
					if (!headers0[i].equals(cellValue)) {
						LOGGER.error("工作簿中无内容,不符合模板要求 ...");
						result.put("flag", false);
						result.put("result", ResultUtil.getResponse("500", "工作簿中无内容,不符合模板要求 ..."));
						return result;
					}
				}
			} else {
				for (int i = FirstCellNum; i < LastCellNum; i++) {
					if (!headers1[i].equals(getValue(row.getCell(i)))) {
						LOGGER.error("工作簿中标题栏,不符合模板要求 ...");
						result.put("flag", false);
						result.put("result", ResultUtil.getResponse("500", "工作簿中标题栏,不符合模板要求 ..."));
						return result;
					}
				}
			}

		}
		result.put("flag", true);
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

	public static boolean analyze(String filePath, ExcelHandler... excelHandlers) {

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

		boolean vaild = vaildExcel(wb, excelHandlers);

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
				excelHandlers[sheetNum].handle(row);
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public static void exportExcel(String[] budgetHeaders, String[] secondBudgetHeaders,
			List<Map<String, Object>> budgetList, List<Map<String, Object>> secondBudgetList, OutputStream out) {

		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet dSheet = workbook.createSheet("各部门预算");
		HSSFSheet sSheet = workbook.createSheet("各部门二次预算信息");
		// 设置表格默认列宽度为15个字节
		dSheet.setDefaultColumnWidth((short) 18);
		sSheet.setDefaultColumnWidth((short) 18);

		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 16);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("宋体");
		// 把字体应用到当前的样式
		style.setFont(font);
		// 生成并设置另一个样式（红色）
		HSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(HSSFColor.RED.index);
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		font2.setFontHeightInPoints((short) 16);
		font2.setFontName("宋体");
		// 把字体应用到当前的样式
		style2.setFont(font2);

		// 生成并设置另一个样式（蓝色）
		HSSFCellStyle style3 = workbook.createCellStyle();
		style3.setFillForegroundColor(HSSFColor.BLUE.index);
		style3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style3.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style3.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 把字体应用到当前的样式
		style3.setFont(font2);

		// 生成并设置另一个样式（绿色）
		HSSFCellStyle style4 = workbook.createCellStyle();
		style4.setFillForegroundColor(HSSFColor.GREEN.index);
		style4.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style4.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style4.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style4.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style4.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style4.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		// 把字体应用到当前的样式
		style4.setFont(font2);

		// 生成并设置另一个样式（白色）
		HSSFCellStyle style5 = workbook.createCellStyle();
		style5.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style5.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style5.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style5.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style5.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style5.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style5.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
		style5.setFont(font2);

		// 人工预算
		HSSFRow dRow = dSheet.createRow(0);
		dRow.setHeight((short) 500);
		for (short i = 0; i < budgetHeaders.length; i++) {
			if (i == 0) {
				dSheet.setColumnWidth(i, (short) 9000);
			} else {
				dSheet.setColumnWidth(i, (short) 4500);
			}
			HSSFCell cell = dRow.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(budgetHeaders[i]);
			cell.setCellValue(text);
		}

		// 二次预算
		HSSFRow sRow1 = sSheet.createRow(0);
		sRow1.setHeight((short) 500);
		for (short i = 0; i < secondBudgetHeaders.length; i++) {
			HSSFCell cell = sRow1.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(secondBudgetHeaders[i]);
			cell.setCellValue(text);
		}

		// 遍历集合数据，产生数据行
		for (int i = 0; i < budgetList.size(); i++) {
			Map<String, Object> map = budgetList.get(i);
			dRow = dSheet.createRow(i + 1);
			dRow.setHeight((short) 500);
			// 月份
			HSSFCell month = dRow.createCell(0);
			String month1 = String.valueOf(map.get("month"));
			month.setCellValue(month1);
			month.setCellStyle(style5);

			// 部门
			HSSFCell departmentName = dRow.createCell(1);
			String departmentName1 = String.valueOf(map.get("departmentName"));
			departmentName.setCellValue(departmentName1);
			departmentName.setCellStyle(style5);

			// 部门属性
			HSSFCell departmentProperty = dRow.createCell(2);
			String departmentProperty1 = String.valueOf(map.get("departmentProperty"));
			departmentProperty.setCellValue(departmentProperty1);
			departmentProperty.setCellStyle(style5);

			// 上月结余
			HSSFCell lastMonthBalance = dRow.createCell(3);
			if (map.get("lastMonthBalance") != null || !"".equals(String.valueOf(map.get("lastMonthBalance")))) {
				String lastMonthBalance1 = String.valueOf(map.get("lastMonthBalance"));
				lastMonthBalance.setCellValue(lastMonthBalance1);
				lastMonthBalance.setCellStyle(style5);
			}

			// 本月核发
			HSSFCell IssuedThisMonth = dRow.createCell(4);
			if (map.get("IssuedThisMonth") != null || !"".equals(String.valueOf(map.get("IssuedThisMonth")))) {
				String IssuedThisMonth1 = String.valueOf(map.get("IssuedThisMonth"));
				IssuedThisMonth.setCellValue(IssuedThisMonth1);
				IssuedThisMonth.setCellStyle(style5);
			}

			// 总预算
			HSSFCell theBudget = dRow.createCell(5);
			if (map.get("theBudget") != null || !"".equals(String.valueOf(map.get("theBudget")))) {
				String theBudget1 = String.valueOf(map.get("theBudget"));
				theBudget.setCellValue(theBudget1);
				theBudget.setCellStyle(style5);
			}

		}

		for (int i = 0; i < secondBudgetList.size(); i++) {
			Map<String, Object> map = secondBudgetList.get(i);
			HSSFRow sRow = sSheet.createRow(i + 1);
			sRow.setHeight((short) 500);

			HSSFCell month = sRow.createCell(0);
			String month1 = String.valueOf(map.get("month"));
			month.setCellValue(month1);
			month.setCellStyle(style5);

			// 部门
			HSSFCell departmentName = sRow.createCell(1);
			String departmentName1 = String.valueOf(map.get("departmentName"));
			departmentName.setCellValue(departmentName1);
			departmentName.setCellStyle(style5);

			// 预算项
			HSSFCell salaryProjectName = sRow.createCell(2);
			String salaryProjectName1 = String.valueOf(map.get("salaryProjectName"));
			salaryProjectName.setCellValue(salaryProjectName1);
			salaryProjectName.setCellStyle(style5);

			// 金额
			HSSFCell salaryProjectValue = sRow.createCell(3);
			if (map.get("salaryProjectValue") != null || !"".equals(String.valueOf(map.get("salaryProjectValue")))) {
				String salaryProjectValue1 = String.valueOf(map.get("salaryProjectValue"));
				salaryProjectValue.setCellValue(salaryProjectValue1);
				salaryProjectValue.setCellStyle(style5);
			}
		}

		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	
	public static String getBigDecimalValue(Cell cell) {
		return getPhoneValueString(cell);
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
