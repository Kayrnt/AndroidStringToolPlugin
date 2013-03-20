package com.kayrnt.android.stringstool.model;

import java.io.File;

public class AndroidStringRessource {

	private File file;
	private String[] parts;

	private String transformed;
	private ResourcesElement resources;
	
	public AndroidStringRessource(File file, ResourcesElement resources) {
	this.file = file;
	this.resources = resources;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getTransformed() {
		return transformed;
	}
	public void setTransformed(String transformed) {
		this.transformed = transformed;
	}
	public ResourcesElement getResources() {
		return resources;
	}
	public void setResources(ResourcesElement resources) {
		this.resources = resources;
	}

	public String[] getParts() {
		return parts;
	}

	public void setParts(String[] parts) {
		this.parts = parts;
	}

}
