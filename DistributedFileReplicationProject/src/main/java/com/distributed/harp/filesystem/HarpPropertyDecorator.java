package com.distributed.harp.filesystem;

public class HarpPropertyDecorator implements HarpFileSystemProperty {

	protected HarpFileSystemProperty harpFileSystemProperty;
	
	public HarpPropertyDecorator() {
		this.harpFileSystemProperty = null;
	}
	
	public HarpPropertyDecorator(HarpFileSystemProperty harpFileSystemProperty) {
		this.harpFileSystemProperty = harpFileSystemProperty;
	}
	
	public void setHarpFileSystemProperty(HarpFileSystemProperty harpFileSystemProperty) {
		this.harpFileSystemProperty = harpFileSystemProperty;
	}
	
	public void activateSystemProperty() {
		this.harpFileSystemProperty.activateSystemProperty();
	}

}
