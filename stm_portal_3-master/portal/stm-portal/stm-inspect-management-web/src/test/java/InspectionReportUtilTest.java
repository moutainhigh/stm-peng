import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.mainsteam.stm.portal.inspect.web.utils.InspectionReportHelper;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Body;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Conclusion;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Head;
import com.mainsteam.stm.portal.inspect.web.utils.entity.InspectionReport;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Item;
import com.itextpdf.text.DocumentException;

public class InspectionReportUtilTest {

	public static void main(String[] args) throws IOException,
			DocumentException {
		InspectionReport r = new InspectionReport();
		r.setHeads(new ArrayList<Head>());
		Head h = new Head();
		h.setName("巡检时间:");
		h.setValue("防盗锁接口附加赛打开");
		r.getHeads().add(h);
		Head h2 = new Head();
		h2.setName("h2");
		h2.setValue("a2");
		r.getHeads().add(h2);
		Head h3 = new Head();
		h3.setName("h3");
		h3.setValue("a3");
		r.getHeads().add(h3);
		Head h4 = new Head();
		h4.setName("h4");
		h4.setValue("a4");
		r.getHeads().add(h4);
		Head h5 = new Head();
		h5.setName("h5");
		h5.setValue("a5");
		r.getHeads().add(h5);
		// Head h6 = new Head();
		// h6.setName("h6");
		// h6.setValue("a6");
		// r.getHeads().add(h6);

		r.setName("中文");

		r.setBodys(new ArrayList<Body>());
		Body b0 = new Body();
		r.getBodys().add(b0);
		b0.setName("机房环境：检查机房环境相关项0");
		Item it0 = new Item();
		b0.setItems(new ArrayList<Item>());
		b0.getItems().add(it0);
		it0.setName("name0");
		it0.setDescription("desc0");
		it0.setReferenceValue("referenceValue0");
		it0.setValue("value0");
		it0.setSummary("summary0");
		it0.setStatus("正常");
		it0.setItems(new ArrayList<Item>());
		Item it01 = new Item();
		it0.getItems().add(it01);
		it01.setName("sdf");
		it01.setStatus("异常");

		Body b = new Body();
		r.getBodys().add(b);
		b.setName("机房环境：检查机房环境相关项");
		b.setItems(new ArrayList<Item>());
		it0 = new Item();
		b.getItems().add(it0);
		it0.setName("name_0");
		it0.setDescription("desc0");
		it0.setReferenceValue("referenceValue0");
		it0.setValue("value0");
		it0.setSummary("summary0");
		it0.setStatus("正常");
		Item it = new Item();
		b.getItems().add(it);
		it.setName("中无法第三");
		it.setDescription("desc");
		it.setReferenceValue("referenceValue");
		it.setValue("value");
		it.setSummary("summary");
		it.setStatus("status");

		it.setItems(new ArrayList<Item>());
		Item itc = new Item();
		it.getItems().add(itc);
		itc.setName("name1");
		itc.setDescription("description1");
		itc.setReferenceValue("referenceValue1");
		itc.setValue("value1");
		itc.setSummary("summary1");
		itc.setStatus("异常");
		Item itc1 = new Item();
		it.getItems().add(itc1);
		itc1.setName("name2");
		itc1.setDescription("description2");
		itc1.setReferenceValue("referenceValue2发送到会计法克里斯蒂健康法就打算klfgdhgjfjkdghjkdhghsfhdshjk所在瑞哦我额uiro");
		itc1.setValue("value2");
		itc1.setSummary("summary2");
		itc1.setStatus("异常");

		r.setConclusions(new ArrayList<Conclusion>());
		Conclusion c0 = new Conclusion();
		r.getConclusions().add(c0);
		c0.setName("");
		c0.setValue("疯狂举动四六级疯狂的随机附带缩进");

		Conclusion c = new Conclusion();
		r.getConclusions().add(c);
		c.setName("交换机总结：交换机整体情况");
		c.setValue("疯狂举动四六级疯狂的随机附带缩进");
		Conclusion c1 = new Conclusion();
		r.getConclusions().add(c1);
		c1.setName("交换机总结：交换机整体情况1");
		c1.setValue("疯狂举动四六级疯狂的随机附带缩进1");

		InspectionReportHelper s = new InspectionReportHelper();
		byte[] data = null;
		FileOutputStream ose = null;

		ose = new FileOutputStream("/tmp/workbook.xlsx");
		data = s.excel(r);
		ose.write(data);
		ose.flush();
		ose.close();
		ose = new FileOutputStream("/tmp/m.docx");
		data = s.word(r);
		ose.write(data);
		ose.flush();
		ose.close();

		ose = new FileOutputStream("/tmp/m.pdf");
		data = s.pdf(r);
		ose.write(data);
		ose.flush();
		ose.close();

		int ss = 0;
		for (Body body : r.getBodys()) {
			System.out.println(++ss + ". " + body.getName());
			for (Item ie : body.getItems()) {
				System.out.println("\t:" + ie.getName());
				if (ie.getItems() != null) {
					for (Item im : ie.getItems()) {
						System.out.println("\t\t:" + im.getName());
					}
				}
			}
		}
	}
}
