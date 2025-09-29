package com.eliassen.crucible.demo.pageObjects;

public class Homepage extends DemoPageObjectBase
{
    public final static String NAME = "Homepage";

    public Homepage()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"Lightwell"});

        // URL'S for "simplify complexities" section
        store("view all of our capabilities url part","services-solutions/capabilities");
        store("integration & api management url part", "/integration-apis/");
        store("integration & api management link", "//a[@href='https://www.lightwellinc.com/services-solutions/capabilities/integration-apis/']");


        // "Simplify complexities" section
        store("view all of our capabilities button", "//a[@href='https://www.lightwellinc.com/services-solutions/capabilities/' and contains(@class, 'elementor-button-link')]");
//      store("integration & api management card", "//div[contains(@class, 'hover-content-box')]/a[contains(@href, '/integration-apis/')]");
//        store("integration & api management card", "//div[contains(@class, 'block-item')]/a[contains(@class, 'box-link__block')]");
        store("integration & api management card", "//h4[contains(text(), 'Integration & API Management')]/../../div[contains(@class, 'box-extra-wrap')]//div[contains(@class, 'box-extra')]");
        store("integration & api management card parent", "//h4[contains(text(), 'Integration & API Management')]/../..");

    // URL'S for "See our industry solutions" section
        store("retail url part", "how-we-help/your-industry/retail/");
        store("consumer goods url part", "how-we-help/your-industry/consumer-goods/");
        store("food & beverage url part", "how-we-help/your-industry/food-beverage/");
        store("logistics & transportation url part", "how-we-help/your-industry/logistics-transportation/");
        store("manufacturing url part", "how-we-help/your-industry/manufacturing/");
        store("financial services & insurance url part", "how-we-help/your-industry/financial-services-insurance/");
        store("healthcare url part", "how-we-help/your-industry/healthcare/");
        store("Pharma & life sciences url part", "how-we-help/your-industry/pharma-life-sciences/");
        store("Energy & utilities url part", "how-we-help/your-industry/energy-utilities/");
        store("your industry url part", "how-we-help/your-industry/");

        // "See our industry solutions" section
        store("retail card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/retail/']");
        store("consumer goods card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/consumer-goods/']");
        store("food & beverage card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/food-beverage/']");
        store("logistics & transportation card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/logistics-transportation/']");
        store("manufacturing card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/manufacturing/']");
        store("financial services & insurance card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/financial-services-insurance/']");
        store("healthcare card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/healthcare/']");
        store("Pharma & life sciences card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/pharma-life-sciences/']");
        store("Energy & utilities card","//div/a[@href='https://www.lightwellinc.com/how-we-help/your-industry/energy-utilities/']");
        store("see our industry solutions button","//a[@href='https://www.lightwellinc.com/how-we-help/your-industry/' and contains(@class, 'elementor-button-link')]");

        // "Business & tech goals" section
        store("i want to dropdown","//ul[@class='custom-drop-down']/li[2]");
        store("list of goal items", "//div/ul/li[@class='output']");
        store("goal box 1", "//ul/li[@id='GoalBox_1']");
    }
}