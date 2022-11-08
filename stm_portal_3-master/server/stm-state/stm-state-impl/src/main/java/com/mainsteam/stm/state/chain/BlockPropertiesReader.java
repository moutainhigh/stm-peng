package com.mainsteam.stm.state.chain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 作者：ziw
 * @date 创建时间：2016年11月23日 上午11:27:43
 * @version 1.0
 */
public class BlockPropertiesReader {

	private static final String BLOCK_NAME_PATTERN = "^\\[([a-zA-Z][a-z0-9A-Z]*)\\]$";

	public BlockPropertiesReader() {
	}

	public static Map<String, List<String>> readBlockValues(InputStream in)
			throws IOException {
		Map<String, List<String>> blocks = new LinkedHashMap<String, List<String>>();
		String line;
		Pattern p = Pattern.compile(BLOCK_NAME_PATTERN);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				in));) {
			line = reader.readLine();
			String blockName = null;
			while (line != null) {
				line = line.trim();
				if (line.length() > 0 && line.charAt(0) != '#') {
					Matcher m = p.matcher(line);
					if (m.find()) {
						blockName = m.group(1);
					} else if (blockName != null) {
						storeValue(blocks, blockName, line);
					}
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			throw e;
		}
		return blocks;
	}
	

	private static void storeValue(Map<String, List<String>> valuesMap,
			String blockName, String value) {
		List<String> values = null;
		if (valuesMap.containsKey(blockName)) {
			values = valuesMap.get(blockName);
		} else {
			values = new ArrayList<>();
			valuesMap.put(blockName, values);
		}
		values.add(value);
	}

}
