package yw.common.excel;

import org.apache.poi.ss.usermodel.Row;

/**
 * 对Excel的操作工具
 * 
 * @author dev
 *
 */
public interface ExcelHandler {

	/**
	 * 获取Excel模板的表头字段
	 * 
	 * @return
	 */
	public String[] getExcelTemplateHeaders();
	
	/**
	 * 对Excel中每一行的处理
	 * 
	 * @param row
	 * @return
	 */
	public boolean handle(Row row) throws Exception;

}
