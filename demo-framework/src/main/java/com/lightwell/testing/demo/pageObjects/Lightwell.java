package com.lightwell.testing.demo.pageObjects;

public class Lightwell extends DemoPageObjectBase
{
    public final static String NAME = "Lightwell";

    public Lightwell()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"Common"});

        store("see how button","//a[contains(@href,'how-we-help')]");
        store("careers link","//ul[@class='sub-menu']/li/a[text()='Careers']");
        store("career page heading","//h2[contains(text(),'Work at Lightwell')]");
        store("page title","//title");
        store("mega menu company tab","//a[@class='mega-menu-link'][text()='Company']/..");
        store("mega menu careers link","//a[@class='mega-menu-link'][text()='Careers']");
        store("magnifying glass", "//img[contains(@src, 'search-icon')]");
        store("search field", "//div[contains(@class, 'mega-search-wrap')]");
        store("close icon", "//img[contains(@class, 'close-icon')]");
        store("data analytics card", "//div[contains(@class, 'add-hover-block-item')]/a/h4[contains(text(),'Data Analytics')]/../..");
        store("move to the cloud selection", "//li/a/span[text()='Move to the Cloud']/..");
        store("i want to menu", "//div[contains(@class,'elementor-widget-container')]/ul/li/span[contains(text(),'I WANT TO:')]");
        store("drop down cloud link", "//div[contains(@class,'inner-links')]/span/a[contains(@href,'move-to-the-cloud')]");
    }
}
