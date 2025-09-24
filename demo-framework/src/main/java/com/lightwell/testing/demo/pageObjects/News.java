package com.lightwell.testing.demo.pageObjects;

public class News extends DemoPageObjectBase{
    public final static String Name = "News" ;
    public News(){
        super();
        setPageName(Name);
        this.pageUrlPart = "company/news/";
    }
    @Override
    public void fillPageTable(){
        addSubViews(new String[] {"Lightwell"});
        store("news breadcrumbs","//div[@class='breadcrumbs']");
        store("eg group read more link","//div[@class='search-filter-results']//div[@class='blog-cover'][1]");
        store("eg group header","//h1[@class='h2']");
        store("lightwell read more link","//div[@class='search-filter-results']//div[@class='blog-cover'][2]");
        store("lightwell header","//h1[@class='h2']");
        store("covid read more link","//div[@class='search-filter-results']//div[@class='blog-cover'][3]");
        store("covid header","//h1[@class='h2']");
    }

}
