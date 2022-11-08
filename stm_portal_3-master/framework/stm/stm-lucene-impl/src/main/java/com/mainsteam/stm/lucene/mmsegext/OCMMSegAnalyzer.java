package com.mainsteam.stm.lucene.mmsegext;

import java.io.Reader;

import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;

/**
 * <li>文件名称: MMSegAnalyzer.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   ziwenwen
 */
public class OCMMSegAnalyzer extends MMSegAnalyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		return new TokenStreamComponents(new OCMMSegTokenizer(newSeg(), reader));
	}
	
}


