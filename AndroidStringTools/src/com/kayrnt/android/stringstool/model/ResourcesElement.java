package com.kayrnt.android.stringstool.model;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resources")
public class ResourcesElement {
	public String parentDirectory;
	public List<StringElement> string;
}
