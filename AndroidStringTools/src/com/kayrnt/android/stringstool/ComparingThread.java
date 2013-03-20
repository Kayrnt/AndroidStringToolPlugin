package com.kayrnt.android.stringstool;

import com.kayrnt.android.stringstool.model.StringElement;

public class ComparingThread extends Thread{


		private final StringElement element;
		private final int position;

		public ComparingThread(StringElement element, int i) {
			super();
			this.element = element; 
			position = i;
		}

		@Override
		public void run() {
			Main.checkElementIsInOtherStringsXML(element, position);
		}


}
