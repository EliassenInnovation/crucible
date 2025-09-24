package com.lightwell.testing.demo.pageObjects;

public class AboutUs extends DemoPageObjectBase {

    public final static String Name = "AboutUs";

    public AboutUs(){
        super();
        setPageName(Name);
        this.pageUrlPart = "about-lightwell/";
    }
    @Override
    public void fillPageTable(){
        addSubViews(new String[] {"Lightwell"});
        store("connect with us button","//a[@href = 'https://www.lightwellinc.com/contact/']/span");
        store("header","//div[@class='elementor-widget-container']/h5");
        store("why lightwell button","//a[@href='https://www.lightwellinc.com/why-lightwell/']/span");
        store("our brand","//a[@href='https://www.lightwellinc.com/our-brand/']");
        store("leadership team","//a[@href='https://www.lightwellinc.com/leadership-team/' and @class='link-over']");
        store("mission & core values","//a[@href='https://www.lightwellinc.com/mission-core-values/' and @class='link-over']");
        store("culture","//a[@href='https://www.lightwellinc.com/mission-core-values/culture/']");
        store("community","//a[@href='https://www.lightwellinc.com/mission-core-values/community/']");
        store("careers","//a[@href='https://www.lightwellinc.com/careers/' and @class='link-over']");

        store("our brand title","//h1[contains(@class,'elementor-heading-title') and contains(text(),'The Lightwell Brand')]");
        store("leadership team title","//h1[contains(@class,'elementor-heading-title') and contains(text(),'Leading a premier technology company through 20+ years of success')]");
        store("mission & core values title","//h1[contains(@class,'elementor-heading-title') and contains(text(),'Our mission and values as a top technology company')]");
        store("culture title","//h1[contains(@class,'elementor-heading-title') and contains(text(),'Championing a collaborative culture for business and IT professionals')]");
        store("community title","//h1[contains(@class,'elementor-heading-title') and contains(text(),'As a top IT services company, we proudly give back to our community')]");
        store("careers title","//h1[contains(@class,'elementor-heading-title') and contains(text(),'Rewarding IT jobs are launched at Lightwell')]");

    }

}
