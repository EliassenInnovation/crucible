package com.eliassen.crucible.web.helpers;

import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TableHelper
{
    public static List<WebElement> GetColumnValues(String columnElement)
    {
        String xpath = CurrentPage.getPageObjectItem(columnElement);
        List<WebElement> rows = CurrentPage.getElementsByXpath(xpath);

        return  rows;
    }
}
