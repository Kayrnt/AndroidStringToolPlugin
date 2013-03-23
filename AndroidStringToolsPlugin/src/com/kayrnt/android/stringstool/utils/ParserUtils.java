/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kayrnt.android.stringstool.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various utility methods used by {@link JSONHandler} implementations.
 */
public class ParserUtils {

	
	static Pattern regex = Pattern.compile("<!--(.*?)-->", Pattern.DOTALL);
	static int commentId = 0;
	
	/**
	 * Build and return a {@link String} associed to the {@link InputStream}
	 * 
	 * @throws IOException
	 */
	
	public static String getString(File file) throws IOException {
		InputStream stream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
		StringBuilder json = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			json.append(transformIfNeeded(line));
		}
		reader.close();
		return json.toString();
	}
	
	public static String transformIfNeeded(String xmlLine) {
		Matcher matcher = regex.matcher(xmlLine);
		if (matcher.find()) {
		    String comment = matcher.group(1);
		    xmlLine = "<string name=\""+"__comment_"+commentId+"\">"+comment+"</string>";
		    commentId++;
		}
		//System.out.println(xmlLine);
		return xmlLine;
	}

}
