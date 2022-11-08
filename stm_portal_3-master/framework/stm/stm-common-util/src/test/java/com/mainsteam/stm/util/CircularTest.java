package com.mainsteam.stm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mainsteam.stm.export.chart.Circular;
import com.mainsteam.stm.export.chart.CircularData;

public class CircularTest {

	public static void main(String[] args) throws IOException {
		List<CircularData> data = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			CircularData c = new CircularData();
			c.setName("name" + i);
			c.setValue(new Random().nextDouble());
			data.add(c);
		}
		Circular c = new Circular();
		FileOutputStream fos = new FileOutputStream(new File("/tmp/c.jpg"));
		fos.write(c.generate(800, 500, "title", data));
		fos.flush();
		fos.close();
	}
}
