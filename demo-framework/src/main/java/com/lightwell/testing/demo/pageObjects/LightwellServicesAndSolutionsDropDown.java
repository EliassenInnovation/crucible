package com.lightwell.testing.demo.pageObjects;

public class LightwellServicesAndSolutionsDropDown extends DemoPageObjectBase{
    public static final String NAME = "LightwellServicesAndSolutionsDropDown";

    public LightwellServicesAndSolutionsDropDown(){
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable() {
        addSubViews(new String[]{"Lightwell"});

        store("services and solutions drop down","//li[@id=\"mega-menu-item-5056\"]");

        store("capabilities link", "//li[@id=\"mega-menu-item-7489\"]/a[@href = \"https://www.lightwellinc.com/services-solutions/capabilities/\"]");
        store("capabilities link part", "capabilities");

        store("integration and apis link", "//li[@id=\"mega-menu-item-7490\"]/a[@href= \"https://www.lightwellinc.com/services-solutions/capabilities/integration-apis/\"]");
        store("integration and apis link part", "capabilities/integration-apis");

        store("B2B integration and edi link", "//li[@id=\"mega-menu-item-10107\"]/a[@href= \"https://www.lightwellinc.com/services-solutions/capabilities/b2b-integration-edi/\"]");
        store("B2B integration and edi link part", "capabilities/b2b-integration-edi");

        store("application and software development link", "//li[@id=\"mega-menu-item-10111\"]/a[@href=\"https://www.lightwellinc.com/services-solutions/capabilities/application-development/\"]");
        store("application and software development link part", "capabilities/application-development");

    }

}
