package com.kayrnt.android.stringstool.model;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "string")
public class StringElement {

	@XmlAttribute
	public String name;
	@XmlValue
	public
	String text;
	
	@Override
	public String toString() {
		return "StringElement [name=" + name + ", text=" + text + "]";
	}
	
	
	
}
