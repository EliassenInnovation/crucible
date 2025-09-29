package com.eliassen.crucible.demo.pageObjects;

public class Capabilities extends DemoPageObjectBase{
    public static final String NAME = "Capabilities";

    public Capabilities(){
        super();
        setPageName(NAME);
        this.pageUrlPart = "services-solutions/capabilities/";
    }

    @Override
    public void fillPageTable() {
        addSubViews(new String[]{"Lightwell"});

        store("capabilities page", "services-solutions/capabilities");

        store("lets talk button", "//div[@data-id =\"39173dbb\"]//a[@href = \"https://www.lightwellinc.com/contact/\"]");
        store("contact page", "https://www.lightwellinc.com/contact/");
    }

}
