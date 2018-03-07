package com.mdc.combot.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ResourceUtil {

	private static Map<String,URL> resources = new HashMap<String,URL>();
	
	public static void addResource(String resourcePath, URL realPath) {
		resources.put(resourcePath, realPath);
		System.out.println("adding " + resourcePath + " " + realPath);
	}
	
	public static URL getResource(String resource) {
		System.out.println("Getting " + resource);
		URL r = resources.get(resource);
		System.out.print("result: " + r);
		return resources.get(resource);
	}
	
}
