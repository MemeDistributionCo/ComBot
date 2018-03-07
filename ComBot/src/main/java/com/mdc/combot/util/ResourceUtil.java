package com.mdc.combot.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ResourceUtil {

	private static Map<String,URL> resources = new HashMap<String,URL>();
	
	public static void addResource(String resourcePath, URL realPath) {
		resources.put(resourcePath, realPath);
	}
	
	public static URL getResource(String resource) {
		return resources.get(resource);
	}
	
}
