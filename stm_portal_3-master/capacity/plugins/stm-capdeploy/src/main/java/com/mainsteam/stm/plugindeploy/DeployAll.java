package com.mainsteam.stm.plugindeploy;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DeployAll {
	public static void main(String[] argv) throws IOException {
		File dir = new File(
				"/Users/sunsht/Documents/5.8QZ/svnoc4/oc/Capacity/plugins/oc-capdeploy/lib");
		File[] files = dir.listFiles();
		Set<String> hasException = new TreeSet<String>();
		Set<String> allFNSet = new TreeSet<String>();
		for (File file : files) {
			if (file.getName().contains("jar")) {
				allFNSet.add(file.getName());
				JarFile jarFile = new JarFile(file);
				Enumeration<JarEntry> entrys = jarFile.entries();
				while (entrys.hasMoreElements()) {
					JarEntry jarEntry = entrys.nextElement();
					// System.out.println(jarEntry.getName());
					if (jarEntry.getName().contains("Exception")) {
						hasException.add(file.getName());
					}
				}
				jarFile.close();
			}
		}
		System.out.println("allSize:" + allFNSet.size());
		System.out.println("hasExceptionSize:" + hasException.size());
		for (String fn : allFNSet) {
			if (!hasException.contains(fn)) {
				System.out.println(fn);
			}
		}
	}
}
