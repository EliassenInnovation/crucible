package com.lightwell.testing.demo.pageObjects;

public class WhyLightwell extends DemoPageObjectBase
{
    public final static String NAME = "WhyLightwell";

    public WhyLightwell()
    {
        super();
        setPageName(NAME);
        this.pageUrlPart = "why-lightwell/";
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"Lightwell"});

        store("why lightwell page","why-lightwell");

        //url parts
        store("let's connect url part","contact");
        store("leadership team url part","leadership-team/");
        store("mission & core values url part","mission-core-values/");
        store("culture url part","mission-core-values/");
        store("careers url part","careers/");
        store("community url part","mission-core-values/");
        store("our brand url part","mission-core-values/");
        store("industry expertise url part", "how-we-help/your-industry/");
        store("services we provide url part", "services-solutions/services/");
        store("solutions we deliver url part", "services-solutions/capabilities/");

        //span title
        store("industry expertise title","//span[contains(@class,'title') and contains(text(),'Industry Expertise')]");

        //headers
        store("why lightwell? heading","//h5[contains(@class,'elementor-heading-title') and contains(text(),'Why Lightwell?')]");
        store("get to know us better heading", "//h2[contains(@class,'elementor-heading-title') and contains(text(),'Get to Know Us Better')]");
        store("who we are and how we help heading", "//h2[contains(@class,'elementor-heading-title') and contains(text(),'Who We Are and How We Help')]");

        // buttons
        store("let's connect button", "//*[@id=\"post-4789\"]/div/div/div/div/section[1]/div[2]/div/div[1]/div/div/div[4]/div/div/a");

        //links
        store("leadership team", "//a[@href='https://www.lightwellinc.com/company/leadership-team/']");
        store("mission & core values", "//a[@href='https://www.lightwellinc.com/company/mission-core-values/']");
        store("culture", "//a[@href='https://www.lightwellinc.com/company/mission-core-values/culture/']");
        store("careers", "//a[@href='https://www.lightwellinc.com/company/careers/']");
        store("community", "//a[@href='https://www.lightwellinc.com/company/mission-core-values/community/']");
        store("our brand", "//a[@href='https://www.lightwellinc.com/company/mission-core-values/our-brand/']");
        store("industry expertise", "//a[href='https://www.lightwellinc.com/how-we-help/your-industry/']");
        store("services we provide", "//a[href='https://www.lightwellinc.com/services-solutions/services/']");
        store("solutions we deliver", "//a[href='https://www.lightwellinc.com/services-solutions/capabilities/']");

    }
}
