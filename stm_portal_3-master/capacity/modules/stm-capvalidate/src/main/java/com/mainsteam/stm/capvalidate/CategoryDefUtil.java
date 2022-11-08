package com.mainsteam.stm.capvalidate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mainsteam.stm.caplib.common.CategoryDef;

/**
 * 
 * @author Administrator
 *
 */
public class CategoryDefUtil {

	private static final String PARENTID = "parentid";

	private static final String NAME = "name";

	private static final String ID = "id";

	private static final String CATEGORY = "Category";
	
	private Map<String, String> parentIdMap = new HashMap<String, String>();
	private List<String> validateErrors = new ArrayList<String>();
	
	public CategoryDefUtil() {
	}

	public CategoryDef loadCategory(String filePath) {
		
		try {
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(new File(filePath));
			Element root = doc.getRootElement();

			@SuppressWarnings("unchecked")
			List<Element> catElementList = root.elements(CATEGORY);
			CategoryDef[] categorys = new CategoryDef[catElementList.size()];
			for (int i = 0; i < categorys.length; i++) {
				categorys[i] = this.initCategory(catElementList.get(i));
			}
			
			CategoryDef rootCategory = null;
			for (int i = 0; i < categorys.length; i++) {
				String parentId = parentIdMap.get(categorys[i].getId());
				if (categorys[i].getId().equals(parentId)) {
					this.validateErrors.add("文件" + filePath + "同一个节点" + catElementList.get(0).getPath()
							+ "的属性parentid值" + parentId + "和id值不能相同");
				}
				if (parentId == null) {
					if (rootCategory == null) {
						rootCategory = categorys[i];
					} else {
						this.validateErrors.add("文件" + filePath + "节点" + catElementList.get(0).getPath() + "属性parentid只能配置一个为NULL");
					}
				}
			}
			
			CategoryDef[] childCategorys = this.getChildCategors(rootCategory, categorys, catElementList.get(0).getPath());
			rootCategory.setChildCategorys(childCategorys);

			return rootCategory;
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return null;
	}

	private CategoryDef[] getChildCategors(CategoryDef category,
			CategoryDef[] categorys, String nodePath) {
		List<CategoryDef> childCategoryList = new ArrayList<CategoryDef>();
		for (int i = 0; i < categorys.length; i++) {
			String parentId = parentIdMap.get(categorys[i].getId());
			if (category.getId().equals(parentId)) {
				categorys[i].setParentCategory(category);
				categorys[i].setChildCategorys(this.getChildCategors(
						categorys[i], categorys, nodePath));
				childCategoryList.add(categorys[i]);
			}
		}

		if (childCategoryList.isEmpty())
			return null;

		CategoryDef[] childCategorys = new CategoryDef[childCategoryList.size()];
		for (int i = 0; i < childCategorys.length; i++)
			childCategorys[i] = childCategoryList.get(i);

		return childCategorys;
	}

	private CategoryDef initCategory(Element element) {
		CategoryDef category = new CategoryDef();

		String id = element.attributeValue(ID);
		category.setId(id);
		category.setName(element.attributeValue(NAME));
		String parentid = element.attributeValue(PARENTID);
		parentIdMap.put(category.getId(), parentid);
		return category;
	}

	public List<String> getValidateError() {
		return this.validateErrors;
	}
	
}
