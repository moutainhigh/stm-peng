package com.mainsteam.stm.capbase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.caplib.common.CategoryDef;

/**
 * 用于解析category的xml文件，获得category的节点对应的对象，即一个节点对应一个实体对象，
 * 比如Category节点对应CategoryDef类。
 * @author Administrator
 *
 */
public class CategoryDefUtil {

	/**
	 * 分类的上级id
	 */
	private static final String PARENTID = "parentid";
	/**
	 * 分类名称
	 */
	private static final String NAME = "name";
	/**
	 * 分类id
	 */
	private static final String ID = "id";
	/**
	 * 分类节点
	 */
	private static final String CATEGORY = "Category";
	/**
	 * 当前节点id对应的父亲节点Id
	 */
	private Map<String, String> parentIdMap = new HashMap<String, String>();

	public CategoryDefUtil() {
	}

	/**
	 * 加载category的xml文件，因为xml的元素都是叶子节点，通过parentid来指定从属关系，所以只需要遍历根节点下的
	 * 所有子节点即可。
	 * @param filePath 文件路径
	 * @return 返回所有的分类
	 */
	public CategoryDef loadCategory(String filePath) {

		try {
//			SAXReader saxReader = new SAXReader();
//			saxReader.setEncoding("UTF-8");
//			Document doc = saxReader.read(new File(filePath));
//			Element root = doc.getRootElement();
			
			SAXReader reader = new SAXReader();
			InputStream ifile = new FileInputStream(filePath); 
			InputStreamReader ir = new InputStreamReader(ifile, "UTF-8"); 
			reader.setEncoding("UTF-8");
			Document document = reader.read(ir);// 读取XML文件
			Element root = document.getRootElement();// 得到根节点
			

			@SuppressWarnings("unchecked")
			List<Element> catElementList = root.elements(CATEGORY);
			CategoryDef[] categorys = new CategoryDef[catElementList.size()];
			for (int i = 0; i < categorys.length; i++) {
				categorys[i] = this.initCategory(catElementList.get(i));
			}

			CategoryDef rootCategory = null;
			for (int i = 0; i < categorys.length; i++) {
				String parentId = parentIdMap.get(categorys[i].getId());
				if (parentId == null) {
					rootCategory = categorys[i];
					break;
				}
			}
			if(null == rootCategory){
				return null;
			}
			CategoryDef[] childCategorys = this.getChildCategors(rootCategory,
					categorys);
			rootCategory.setChildCategorys(childCategorys);

			return rootCategory;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 返回当前分类下的一级子分类
	 * @param category 指定当前的分类
	 * @param categorys 包含所有的分类
	 * @return 返回所有分类中，父类属于category的子分类
	 */
	private CategoryDef[] getChildCategors(CategoryDef category,
			CategoryDef[] categorys) {
		List<CategoryDef> childCategoryList = new ArrayList<CategoryDef>();
		for (int i = 0; i < categorys.length; i++) {
			String parentId = parentIdMap.get(categorys[i].getId());
			if (category.getId().equalsIgnoreCase(parentId)) {
				categorys[i].setParentCategory(category);
				categorys[i].setChildCategorys(this.getChildCategors(
						categorys[i], categorys));
				childCategoryList.add(categorys[i]);
			}
		}

		if (childCategoryList.isEmpty())
			return null;

		CategoryDef[] childCategorys = new CategoryDef[childCategoryList.size()];
//		for (int i = 0; i < childCategorys.length; i++)
//			childCategorys[i] = childCategoryList.get(i);

		return childCategoryList.toArray(childCategorys);
	}

	/**
	 * 初始化一个CategoryDef对象，包括id，name，并将它放入id-parentid对应关系的map中
	 * @param element 一个dom树元素
	 * @return 返回一个新的CategoryDef对象
	 */
	private CategoryDef initCategory(Element element) {
		CategoryDef category = new CategoryDef();

		category.setId(element.attributeValue(ID));
		category.setName(element.attributeValue(NAME));
		String dispStr = element.attributeValue("isDisplay");
		if(StringUtils.isNotEmpty(dispStr)){
			Boolean b = Boolean.valueOf(dispStr);
			if(!b){
				category.setDisplay(false);
			}
		}
		parentIdMap.put(category.getId(), element.attributeValue(PARENTID));
		return category;
	}

}
