package com.lightwell.testing.demo.pageObjects;

public class Resources extends DemoPageObjectBase
{
    public static final String NAME = "Resources";

    public Resources()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"Lightwell"});

        store("resources page", "resources");
        store("resource button", "//a[@href='https://www.lightwellinc.com/resources/' and contains(@class, 'mega-menu-link')]");
        store("resource drop down menu", "//ul[contains(@class, 'mega-sub-menu')]");
        store("blog link", "//a[@href='https://www.lightwellinc.com/blog/' and contains(@class, 'mega-menu-link')]");
        store("resource library link", "//a[@href='https://www.lightwellinc.com/resources/resource-library/' and contains(@class, 'mega-menu-link')]");
        store("success stories link", "//a[@href='https://www.lightwellinc.com/resources/resource-library/?_sft_resource_type=case-study' and contains(@class, 'mega-menu-link')]");
        store("webinar & events link", "//a[@href='https://www.lightwellinc.com/resources/webinars-events/' and contains(@class, 'mega-menu-link')]");
        store("lightwell brochures link", "//a[@href='https://www.lightwellinc.com/resources/resource-library/?_sft_resource_type=brochure' and contains(@class, 'mega-menu-link')]");

        store("blog link url part", "blog/");
        store("resource library url part", "resources/resource-library/");
        store("success stories url part", "resources/resource-library/?_sft_resource_type=case-study");
        store("webinar & events url part", "resources/webinars-events/");
        store("lightwell brochures url part", "resources/resource-library/?_sft_resource_type=brochure");
    }
}
