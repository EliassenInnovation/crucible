package com.eliassen.crucible.web.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;

public class DomHelper {
    public static void grabDom(){
        String dom = (String) CurrentPage.executeJavascript("return document.body.innerHTML;");

        try {
            // Step 2: Parse the HTML into a Jsoup Document
            Document jsoupDoc = Jsoup.parse(dom);

            // Step 3: Remove <script> blocks
            Elements xmlElements = jsoupDoc.select("script");
            for (Element xmlElement : xmlElements) {
                xmlElement.remove();
            }

            // Step 4: Convert the Jsoup Document to XML
            String xmlOutput = jsoupDoc.outerHtml();
            CurrentPage.getScenario().attach(xmlOutput, "text/html", "Captured_DOM");
            //Logger.log(xmlOutput); // The cleaned-up XML/HTML structure
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
