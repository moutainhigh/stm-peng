package com.mainsteam.stm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mainsteam.stm.export.chart.CircularData;
import com.mainsteam.stm.export.chart.Point;
import com.mainsteam.stm.export.data.DataRow;
import com.mainsteam.stm.export.data.DataTable;
import com.mainsteam.stm.export.pdf.PDFHelper;

public class PDFHelperTest {

//	public static void main(String[] args) throws IOException {
//		List<Point> data = new ArrayList<Point>();
//		for (int i = 0; i < 3; i++) {
//			for (int j = 0; j < 5; j++) {
//				Point p = new Point();
//				data.add(p);
//				p.setLineName("line" + i);
//				p.setxName("xNmae" + j);
////				p.setValue(new Random().nextDouble());
//				p.setValue(0);
//			}
//		}
//
//		DataTable dataTable = new DataTable("table1中文", 20);
//		DataRow hand = new DataRow();
//		dataTable.setHand(hand);
//		for (int i = 0; i < dataTable.getColumnSize(); i++) {
//			hand.addColumn("列" + i);
//		}
//		for (int i = 0; i < 20; i++) {
//			DataRow dataRow = new DataRow();
//			dataTable.addRow(dataRow);
//			for (int j = 0; j < dataTable.getColumnSize(); j++) {
//				// dataRow.addColum("row[" + i + "]," + "colum是否[" + j + "]");
//				dataRow.addColumn("1");
//			}
//		}
//
//		List<CircularData> dataRing = new ArrayList<>();
//		for (int i = 0; i < 10; i++) {
//			CircularData c = new CircularData();
//			c.setName("name" + i);
//			c.setValue(new Random().nextDouble());
//			dataRing.add(c);
//		}
//
//		PDFHelper p = new PDFHelper("设备流量");
//		p.addLine("流量折线图", "中文x", "yLable", data);
//		// p.addLine(null, "中文x", "yLable", data);
//		p.addCircular("ring1中文", dataRing);
//		p.addTable(dataTable);
//
//		FileOutputStream fos = new FileOutputStream(new File("/tmp/hh.pdf"));
//		fos.write(p.generate());
//		fos.flush();
//		fos.close();
//	}
}
