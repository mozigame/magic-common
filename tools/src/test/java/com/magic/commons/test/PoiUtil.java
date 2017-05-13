package com.magic.commons.test;

import com.sun.imageio.plugins.wbmp.WBMPImageReader;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * PoiUtil
 *
 * @author zj
 * @date 2017/4/29
 */
public class PoiUtil {

    private static final int MAX_RAW = 100;
    private static final int MAX_SHEET = 10000;

    /**
     *
     * @param list
     */
    public static void generate(String[] headers, List<Person> list) throws Exception{
        SXSSFWorkbook wb = new SXSSFWorkbook(MAX_RAW);
        SXSSFSheet sheet = null;
        for (int i = 0; i < list.size(); i++) {
            if (i % MAX_SHEET == 0){
                sheet = (SXSSFSheet)wb.createSheet("sheet-" + (i / MAX_SHEET + 1));
                Row headerRow = sheet.createRow(0);
                Cell cell = headerRow.createCell(0);
                cell.setCellValue(headers[0]);
                Cell cell1 = headerRow.createCell(1);
                cell1.setCellValue(headers[1]);
                Cell cell2 = headerRow.createCell(2);
                cell2.setCellValue(headers[2]);
                Cell cell3 = headerRow.createCell(3);
                cell3.setCellValue(headers[3]);
                Cell cell4 = headerRow.createCell(4);
                cell4.setCellValue(headers[4]);
            }
            Person person = list.get(i);
            Row row = sheet.createRow(i % MAX_SHEET + 1);
            Cell cell = row.createCell(0);
            cell.setCellValue(person.getName());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(person.getAge());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(person.getSex());
            cell2.setCellValue(person.getTelephone());
            cell2.setCellValue(person.getAddress());
        }
        FileOutputStream outputStream = new FileOutputStream("d:\\test.xlsx");
        wb.write(outputStream);
        outputStream.close();
    }

    private static void export() throws Exception{
        String[] headers = new String[]{"姓名", "年龄", "性别", "手机号码", "居住地址"};
        SXSSFWorkbook wb = new SXSSFWorkbook(MAX_RAW);
        SXSSFSheet sheet = null;
        ArrayList<Person> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            Person person = new Person();
            person.setName("name-" + i);
            person.setAge(i % 100 + 1);
            person.setSex((byte) '男');
            person.setTelephone("134********");
            person.setAddress("中国北京市海淀区中关村e世界");
            list.add(person);
            if (list.size() % 100000 == 0){
                for (int j = 0; j < list.size(); j++) {
                    if (j % MAX_SHEET == 0){
                        sheet = (SXSSFSheet)wb.createSheet("sheet-" + System.currentTimeMillis());
                        Row headerRow = sheet.createRow(0);
                        Cell cell = headerRow.createCell(0);
                        cell.setCellValue(headers[0]);
                        Cell cell1 = headerRow.createCell(1);
                        cell1.setCellValue(headers[1]);
                        Cell cell2 = headerRow.createCell(2);
                        cell2.setCellValue(headers[2]);
                        Cell cell3 = headerRow.createCell(3);
                        cell3.setCellValue(headers[3]);
                        Cell cell4 = headerRow.createCell(4);
                        cell4.setCellValue(headers[4]);
                    }
                    Person person1 = list.get(j);
                    Row row = sheet.createRow(j % MAX_SHEET + 1);
                    Cell cell = row.createCell(0);
                    cell.setCellValue(person1.getName());
                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(person1.getAge());
                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(person1.getSex());
                    Cell cell3 = row.createCell(3);
                    cell3.setCellValue(person1.getTelephone());
                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(person1.getAddress());
                }
                list.clear();
            }
        }
        FileOutputStream outputStream = new FileOutputStream("d:\\test.xlsx");
        wb.write(outputStream);
        outputStream.close();

    }


    public static void main(String[] args) throws Exception {
        /*ArrayList<Person> list = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            Person person = new Person();
            person.setName("name-" + i);
            person.setAge(i % 100 + 1);
            person.setSex((byte) '男');
            person.setTelephone("134********");
            person.setAddress("中国北京市海淀区中关村e世界");
            list.add(person);
        }
        String[] headers = new String[]{"姓名", "年龄", "性别", "手机号码", "居住地址"};
        generate(headers, list);

        Person person = new Person();
        person.setName("name-" + 0);
        person.setAge(0 % 100 + 1);
        person.setSex((byte) '男');
        person.setTelephone("134********");
        person.setAddress("中国北京市海淀区中关村e世界");
        System.out.println(person.toString().getBytes().length * 10000000);*/


        Person person = new Person();
        person.setName("name-" + 0);
        person.setSex((byte) '男');
        person.setAge(0 % 100 + 1);
        person.setTelephone("134********");
        person.setAddress("中国北京市海淀区中关村e世界");
        Field[] fields = person.getClass().getDeclaredFields();
        System.out.println(fields.length);
        for (Field field : fields){
            System.out.println(field.getName());
        }

        //export();
    }


}
