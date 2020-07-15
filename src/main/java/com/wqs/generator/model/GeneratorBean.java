package com.wqs.generator.model;

public class GeneratorBean {

	private String filePath;
	private String templateName;
	
	private String packageName;
	private String tabName;
	private String beanName;
	private String tabCommnet;
	
	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getTabCommnet() {
		return tabCommnet;
	}
	public void setTabCommnet(String tabCommnet) {
		this.tabCommnet = tabCommnet;
	}
	
}
