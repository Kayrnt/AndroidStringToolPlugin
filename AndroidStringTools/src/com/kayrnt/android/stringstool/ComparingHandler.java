package com.kayrnt.android.stringstool;

import com.kayrnt.android.stringstool.model.StringElement;

public class ComparingHandler implements Runnable{

	private final StringElement element;
	private final int position;

	public ComparingHandler(StringElement element, int i) {
		super();
		this.element = element; 
		position = i;
	}

	@Override
	public void run() {
		Main.checkElementIsInOtherStringsXML(element, position);
		Main.threads.remove(this);
		if(Main.threads.isEmpty()) {
			Main.mergeStrings();
			Main.closeAndroidXMLStrings();
		}
	}

}
