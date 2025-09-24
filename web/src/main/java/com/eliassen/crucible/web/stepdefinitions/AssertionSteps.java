package com.eliassen.crucible.web.stepdefinitions;

import io.cucumber.java.en.When;
import com.eliassen.crucible.core.helpers.AlphabeticalOrder;
import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.core.helpers.RegexValidator;
import com.eliassen.crucible.core.helpers.TestHelperBase;
import com.eliassen.crucible.web.helpers.NavHelper;
import com.eliassen.crucible.web.helpers.TableHelper;
import com.eliassen.crucible.web.helpers.TestHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.Assert.*;

public class AssertionSteps {
    public static final String VALUE = "value";

    @When("{string} is not visible")
    @And("The/the {string} is not visible")
    @Then("I check that (the ){string} is not visible")
    public void iCheckThatIsNotVisible(String elementName) {
        try {
            //using direct methods since CurrentPage.Element searches FOR visibility
            String xpath = CurrentPage.getPageObjectItem(elementName);
            WebElement element = CurrentPage.getElementByXpath(xpath, false);
            assertFalse(element.isDisplayed());
        } catch (NoSuchElementException nse) {
            assertTrue(true);
        }
    }

    @Then("I check that (the ){string} is visible")
    public void iCheckThatIsVisible(String elementName) {
        try {
            WebElement element = NavHelper.getElement(elementName);
            assertTrue(element.isDisplayed());
        } catch (NoSuchElementException nse) {
            assertTrue("Element not found using xpath:" + CurrentPage.getPageObjectItem(elementName) + "!", false);
        }
    }

    @And("{string} is visible")
    @Then("The/the {string} is visible")
    public void isVisible(String elementName) {
        try {
            WebElement element = NavHelper.getElement(elementName);
            assertTrue(element.isDisplayed());
        } catch (NoSuchElementException nse) {
            assertTrue("Element not found using xpath:" + CurrentPage.getPageObjectItem(elementName) + "!", false);
        }
    }

    @Then("The breadcrumb says {string}")
    public void theBreadcrumbSays(String breadcrumb) {
        String[] breadCrumbArray = breadcrumb.split("/");
        String[] breadcrumbPieces = TestHelper.getBreadcrumbPieces();

        assertArrayEquals(breadCrumbArray, breadcrumbPieces);
    }

    @And("{string} exists")
    @Then("The/the {string} exists")
    public void exists(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        assertNotNull(element);
    }

    @And("{string} text is {string}")
    @Then("The/the {string} text is {string}")
    public void textIs(String elementName, String expectedOption) {
        WebElement element = CurrentPage.element(elementName);
        assertEquals(expectedOption, element.getText());
    }

    @And("{string} is empty")
    @Then("The/the {string} is empty")
    public void isEmpty(String elementName) {
        WebElement element = CurrentPage.element(elementName, false);
        NavHelper.scrollElementIntoView(element);
        assertTrue(element.getText().isEmpty());
    }

    @And("{string} is disabled")
    @Then("The/the {string} is disabled")
    public void isDisabled(String elementName) {
        WebElement element = CurrentPage.element(elementName, false);
        NavHelper.scrollElementIntoView(element);
        assertFalse(element.isEnabled());
    }

    @And("{string} is enabled")
    @Then("The/the {string} is enabled")
    public void isEnabled(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        assertTrue(element.isEnabled());
    }

    @And("{string} text equals my remembered {string}")
    @Then("The/the {string} text equals my remembered {string}")
    public void theTextEqualsMyRemembered(String elementName, String key) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getText();
        String retrievedText = CurrentPage.retrieve(key);
        assertEquals(elementText, retrievedText);
    }

    @And("{string} field is empty")
    @Then("The/the {string} field is empty")
    public void fieldIsEmpty(String elementName) {
        WebElement element = CurrentPage.element(elementName, false);
        NavHelper.scrollElementIntoView(element);
        String elementText = element.getAttribute(VALUE);
        assertTrue(elementText.isEmpty());
    }

    @And("{string} field is not empty")
    @Then("The/the {string} field is not empty")
    public void fieldIsNotEmpty(String elementName) {
        WebElement element = CurrentPage.element(elementName, false);
        NavHelper.scrollElementIntoView(element);
        String elementText = element.getAttribute(VALUE);
        assertTrue(!elementText.isEmpty());
    }

    @Then("The/the {string} text in the field equals {string}")
    public void theTextInTheFieldEquals(String elementName, String expectedOption) {
        WebElement element = CurrentPage.element(elementName);
        String elementValue = element.getAttribute(VALUE);
        assertEquals(expectedOption, elementValue);
    }

    @And("{string} comes before (the ){string} alphabetically")
    @Then("The/the {string} comes before (the ){string} alphabetically")
    public void isListedBefore(String firstElement, String secondElement) {
        String text1 = CurrentPage.element(firstElement).getText();
        String text2 = CurrentPage.element(secondElement).getText();
        assertTrue(text1.substring(0, 1).compareTo(text2.substring(0, 1)) <= 0);
    }

    @And("{string} column of the table is in alphabetical order")
    @Then("The/the {string} column of the table is in alphabetical order")
    public void theColumnOfTableIsInAlphabeticalOrder(String columnElement) {
        TestHelper.checkSortOrderOfAListOfElements(columnElement, AlphabeticalOrder.forward);
    }

    @And("{string} column of the table is in reverse alphabetical order")
    @Then("The/the {string} column of the table is in reverse alphabetical order")
    public void theColumnOfTableIsInReverseAlphabeticalOrder(String columnElement) {
        TestHelper.checkSortOrderOfAListOfElements(columnElement, AlphabeticalOrder.reverse);
    }

    @And("{string} text is not empty")
    @Then("The/the {string} text is not empty")
    public void isNotEmpty(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        assertFalse(element.getText().isEmpty());
    }

    @And("{string} text contains {string}")
    @Then("The/the {string} text contains {string}")
    public void theTextContains(String elementName, String expectedOption) {
        WebElement element = CurrentPage.element(elementName);
        String text = element.getText();
        if (text.equals("")) {
            text = element.getAttribute(VALUE);
        }
        assertTrue(text.contains(expectedOption));
    }

    @And("{string} text contains (the ){string} text")
    @Then("The/the {string} text contains (the ){string} text")
    public void theTextContainsText(String elementName, String elementName2) {
        WebElement element = CurrentPage.element(elementName);
        WebElement element2 = CurrentPage.element((elementName2));
        assertTrue(element.getText().contains(element2.getText()));
    }

    @Then("My remembered {string} does not contain the text from (the ){string}")
    public void myRememberedDoesNotContainTheTextFrom(String key, String elementName) {
        String retrievedText = CurrentPage.retrieve(key);
        WebElement element = CurrentPage.element(elementName);
        String text = element.getText();
        assertFalse(retrievedText.contains(text));
    }

    @Then("My remembered {string} contains the text from (the ){string}")
    public void myRememberedContainsTextFrom(String key, String elementName) {
        TestHelperBase.wait(2);
        String retrievedText = CurrentPage.retrieve(key);
        Logger.log("Remembered: " + retrievedText);
        WebElement element = CurrentPage.element(elementName);
        String text = element.getText();
        Logger.log("Expected: " + text);
        assertTrue(retrievedText.contains(text));
    }

    @And("{string} field text equals my remembered {string}")
    @Then("The/the {string} field text equals my remembered {string}")
    public void theFieldTextEqualsMyRemembered(String elementName, String key) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getAttribute(VALUE);
        String retrievedText = CurrentPage.retrieve(key);
        assertEquals(elementText, retrievedText);
    }

    @And("{string} text content equals my remembered {string}")
    @Then("The/the {string} text content equals my remembered {string}")
    public void theTextContentEqualsMyRemembered(String elementName, String key) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getAttribute("textContent");
        String retrievedText = CurrentPage.retrieve(key);
        assertEquals(elementText, retrievedText);
    }

    @Then("The state of (the ){string} checkbox is the opposite of my remembered {string}")
    public void theStateOfTheCheckboxIsTheOppositeOfMyRemembered(String elementName, String key) {
        WebElement element = CurrentPage.element(elementName);
        String state = element.getAttribute("aria-checked");
        String retrievedText = CurrentPage.retrieve(key);
        boolean previousState = Boolean.parseBoolean(retrievedText);
        boolean currentState = Boolean.parseBoolean(state);
        assertTrue(currentState == !previousState);
    }

    @Then("I check that (the ){string} text is an email address")
    public void iCheckThatTextIsAnEmailAddress(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getAttribute("innerText");
        assertTrue(RegexValidator.validateEmail(elementText));
    }

    @Then("I check that (the ){string} is an email address")
    public void iCheckThatIsAnEmailAddress(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getAttribute(VALUE);
        assertTrue(RegexValidator.validateEmail(elementText));
    }

    @And("If (the ){string} field is empty I enter {string}")
    public void ifFieldIsEmptyIEnter(String elementName, String text) {
        WebElement element = CurrentPage.element(elementName);
        if (element.getAttribute(VALUE).isEmpty()) {
            CurrentPage.enterText(elementName, text);
        }
    }

    @Then("I check that (the ){string} text is a date")
    public void iCheckThatTextIsADate(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getText();
        assertTrue(RegexValidator.validateDate(elementText));
    }

    @Then("I check that (the ){string} is a date")
    public void iCheckThatIsADate(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getAttribute(VALUE);
        assertTrue(RegexValidator.validateDate(elementText));
    }

    @Then("I check that (the ){string} is a {string}")
    public void iCheckThatIsA(String elementName, String regexKey) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getAttribute(VALUE);
        assertTrue(RegexValidator.validate(elementText, regexKey));
    }

    @Then("I check that (the ){string} text is a {string}")
    public void iCheckThatTextIsA(String elementName, String regexKey) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getText();
        assertTrue(RegexValidator.validate(elementText, regexKey));
    }

    @And("{string} date column is in ascending order")
    @Then("The/the {string} date column is in ascending order")
    public void theDateColumnIsInAscendingOrder(String columnElement) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

        if (rows.size() < 2) {
            assertTrue(true);
        }
//refactor to work with dates
        for (int i = 0; i < rows.size() - 1; i++) {
            String date1 = rows.get(i).getText();
            String date2 = rows.get(i + 1).getText();
            assertTrue(date1.substring(0, 1).compareTo(date2.substring(0, 1)) <= 0);
        }
    }

    @And("{string} column texts all contain the text from (the ){string}")
    @Then("The/the {string} column texts all contain the text from (the ){string}")
    public void theColumnTextsAllContainTheTextFrom(String columnElement, String selectionElement) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
        String selection = CurrentPage.element(selectionElement).getText();

        if (rows.isEmpty()) {
            assertTrue(true);
        }
        for (int i = 0; i < rows.size() - 1; i++) {
            String rowText = rows.get(i).getText();
            assertTrue(selection.contains(rowText));
        }
    }

    @Then("The dates in (the ){string} are within the current month")
    public void theDatesInAreWithinTheCurrentMonth(String columnElement) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate listedDate = null;

        for (int i = 0; i < rows.size() - 1; i++) {
            String elementText = "";
            try {
                elementText = rows.get(i).getText();
            }
            //TODO Need specific exception
            catch (Exception x) {
                Logger.logError(x.getMessage());
            }

            try {
                listedDate = LocalDate.parse(elementText, formatter);
            } catch (DateTimeParseException e) {
                Logger.logError(e.getMessage());
            }

            int month = listedDate.getMonthValue();
            assertEquals(month, currentMonth);
        }
    }

    @Then("The list only contains items where (the ){string} column equals {string}")
    public void theListOnlyContainsItemsWhereColumnEquals(String columnElement, String text) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

        if (rows.isEmpty()) {
            assertTrue(true);
        } else {
            for (int i = 0; i < rows.size(); i++) {
                String elementText = "";
                try {
                    elementText = rows.get(i).getText().trim();
                } catch (Exception x) {
                    Logger.logError(x.getMessage());
                }
                assertTrue(elementText.equals(text));
            }
        }
    }

    @And("{string} column contains the remembered, space separated with comma {string} {string}")
    @Then("The/the {string} column contains the remembered, space separated with comma {string} {string}")
    public void theColumnContainsTheRememberedSpaceSeparatedWithComma(String columnElement, String first, String second) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
        String retrievedFirst = CurrentPage.retrieve(first);
        String retrievedSecond = CurrentPage.retrieve(second);
        String spaceSeparatedName = retrievedFirst + "," + " " + retrievedSecond;
        Boolean found = false;
        for (WebElement row : rows) {
            String rowName = row.getText();
            if (rowName.contains(spaceSeparatedName)) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @And("{string} column contains {string}")
    @Then("The/the {string} column contains {string}")
    public void theColumnContains(String columnElement, String text) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

        Boolean found = false;
        for (WebElement row : rows) {
            String rowName = row.getText();
            if (rowName.contains(text)) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @And("{string} text equals {string}")
    @Then("The/the {string} text equals {string}")
    public void theTextEquals(String elementName, String expectedText) {
        String elementText = TestHelper.getTextFromElement(CurrentPage.element(elementName));

        assertEquals(expectedText, elementText);
    }

    @And("{string} does not contain my persisted {string}")
    @Then("The/the {string} does not contain my persisted {string}")
    public void doesNotContainMyPersisted(String elementName, String key) {
        String persistedValue = CurrentPage.retrievePersisted(key);
        String elementText = TestHelper.getTextFromElement(elementName);

        assertFalse(elementText.contains(persistedValue));
    }

    @Then("I check that (the ){string} is disabled")
    public void iCheckThatIsDisabled(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        String attribute = element.getAttribute("disabled");
        assertTrue(Boolean.parseBoolean(attribute));
    }

    @Then("I check that (the ){string} is not disabled")
    public void iCheckThatIsNotDisabled(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        String attribute = element.getAttribute("disabled");
        assertFalse(Boolean.parseBoolean(attribute));
    }

    @Then("I check that I am not on (the ){string}")
    public void iCheckThatIAmNotOnThe(String expectedPage) {
        String expectedPageURL = CurrentPage.getPageObjectItem(expectedPage.toLowerCase());
        String actualPage = CurrentPage.actualURL();

        assertFalse(actualPage.contains(expectedPageURL));
    }

    @Then("I check (the ){string} content is a number")
    public void iCheckTheContentIsANumber(String elementName) {
        WebElement element = CurrentPage.element((elementName));
        String elementText = element.getText();

        assertTrue(TestHelperBase.isNumeric(elementText));
    }

    @And("{string} column texts all equal the text {string}")
    @Then("The/the {string} column texts all equal the text {string}")
    public void theColumnTextsAllEqualTheText(String columnElement, String expectedText) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

        if (rows.isEmpty()) {
            assertTrue(true);
        }
        for (int i = 0; i < rows.size(); i++) {
            String rowText = rows.get(i).getText();
            assertTrue(rowText.equals(expectedText));
        }
    }

    @And("{string} column texts do not contain duplicate values")
    @Then("The/the {string} column texts do not contain duplicate values")
    public void theColumnTextsDoesNotContainDuplicateValues(String columnElement) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
        if (rows.isEmpty()) {
            assertTrue(true);
        }

        boolean isDuplicate = false;
        for (int i = 0; i < rows.size(); i++) {
            for (int j = i + 1; j < rows.size(); j++) {
                if (rows.get(i).equals(rows.get(j))) {
                    isDuplicate = true;
                }
            }
            assertFalse(isDuplicate);
        }
    }

    @Then("I verify there are {string} rows where the {string} column text is {string}")
    public void iVerifyThereAreRowsWhereTheColumnTextIs(int expectedAmount, String columnElement, String expectedText) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
        int foundRows = 0;

        for (int i = 0; i < rows.size(); i++) {
            String rowText = rows.get(i).getText();
            if (rowText.equals(expectedText)) {
                foundRows++;
            }
        }

        assertEquals(expectedAmount, foundRows);
    }

    /**
     * @deprecated
     * No idea what this was written for or why it's here
     */
    @Deprecated(since="before time began")
    @Then("I verify the number of rows where the {string} column text is {string} equals my remembered value {string}")
    public void iVerifyTheNumberOfRowsWhereTheColumnTextIsEqualsMyRememberedValue(String columnElement, String expectedText, String rememberedValueKey) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
        int foundRows = 0;

        for (int i = 0; i < rows.size(); i++) {
            String rowText = rows.get(i).getText();
            if (rowText.equals(expectedText)) {
                foundRows++;
            }
        }

        String rememberedValueAsString = CurrentPage.retrieve(rememberedValueKey);
        int rememberedValue = Integer.parseInt(rememberedValueAsString);

        assertEquals(rememberedValue, foundRows);
    }

    @Then("I verify the number of rows where the {string} column text is {string} equals my stored value {string}")
    public void iVerifyTheNumberOfRowsWhereTheColumnTextIsEqualsMyStoredValue(String columnElement, String expectedText, String rememberedValueKey) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);
        int foundRows = 0;

        for (int i = 0; i < rows.size(); i++) {
            String rowText = rows.get(i).getText();
            if (rowText.equals(expectedText)) {
                foundRows++;
            }
        }

        String rememberedValueAsString = CurrentPage.retrievePersisted(rememberedValueKey);
        int rememberedValue = Integer.parseInt(rememberedValueAsString);

        assertEquals(rememberedValue, foundRows);
    }

    @Then("I check that (the ){string} radio button is selected")
    public void iCheckThatRadioButtonIsSelected(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        String class1 = element.getAttribute("class");
        assertTrue(class1.contains("checked"));
    }

    @And("{string} text contains my remembered {string}")
    @Then("The/the {string} text contains my remembered {string}")
    public void theTextContainsMyRemembered(String elementName, String key) {
        WebElement element = CurrentPage.element(elementName);
        String retrievedText = CurrentPage.retrieve(key);
        TestHelper.waitForElementToContainText(element, retrievedText);
        String elementText = element.getText();
        assertTrue(elementText.contains(retrievedText));
    }

    @And("{string} text content does not equal my remembered {string}")
    @Then("The/the {string} text content does not equal my remembered {string}")
    public void theTextContentNotEqualsMyRemembered(String elementName, String key) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = element.getAttribute("textContent").trim();
        String retrievedText = CurrentPage.retrieve(key);

        int currentValue = Integer.parseInt(elementText);
        int rememberedValue = Integer.parseInt(retrievedText);

        Boolean diff = true;
        if (currentValue != rememberedValue) {
            diff = true;
        } else diff = false;

        assertTrue(diff);
    }

    @And("{string} text does not contain {string}")
    @Then("The/the {string} text does not contain {string}")
    public void theTextNotContain(String elementName, String expectedOption) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = TestHelper.getTextFromElement(element);

        assertFalse(elementText.contains(expectedOption));
    }

    @And("{string} text equals (the ){string} text")
    @Then("The/the {string} text equals (the ){string} text")
    public void theTextEqualsTheText(String elementName, String elementName2) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = TestHelper.getTextFromElement(element);

        WebElement element2 = CurrentPage.element(elementName2);
        String elementText2 = TestHelper.getTextFromElement(element2);

        assertEquals(elementText, elementText2);
    }

    @And("{string} text does not equal (the ){string} text")
    @Then("The/the {string} text does not equal (the ){string} text")
    public void theTextNotEqualsTheText(String elementName, String elementName2) {
        WebElement element = CurrentPage.element(elementName);
        String elementText = TestHelper.getTextFromElement(element);

        WebElement element2 = CurrentPage.element(elementName2);
        String elementText2 = TestHelper.getTextFromElement(element2);

        assertNotEquals(elementText, elementText2);
    }

    @Then("I check that (the ){string} is colored {string}")
    public void iCheckThatIsColored(String elementName, String colorCode) {
        WebElement element = CurrentPage.element(elementName);
        String color = element.getCssValue("color");
        String hexcolor = Color.fromString(color).asHex();
        assertEquals(colorCode, hexcolor);
    }

    @And("{string} text is empty")
    @Then("The/the {string} text is empty")
    public void textIsEmpty(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        assertTrue(element.getText().isEmpty());
    }

    @And("{string} row contains text {string}")
    @Then("The/the {string} row contains text {string}")
    public void theRowContainsText(String rowElement, String expectedText) {
        List<WebElement> cells = TableHelper.GetColumnValues(rowElement);

        if (cells.isEmpty()) {
            assertTrue(true);
        }
        for (WebElement cell : cells) {
            String cellText = cell.getText();
            assertTrue(cellText.contains(expectedText));
        }
    }

    @Then("I check that (the ){string} is active")
    public void iCheckThatIsActive(String elementName) {
        WebElement element = CurrentPage.element(elementName);
        String class1 = element.getAttribute("class");
        assertTrue(class1.contains("active"));
    }

    @And("{string} column texts all contain the text {string}")
    @Then("The/the {string} column texts all contain the text {string}")
    public void theColumnTextsAllContainTheText(String columnElement, String expectedText) {
        List<WebElement> rows = TableHelper.GetColumnValues(columnElement);

        if (rows.isEmpty()) {
            assertTrue(true);
        }
        for (int i = 0; i < rows.size(); i++) {
            String rowText = rows.get(i).getText();
            assertTrue(rowText.contains(expectedText));
        }
    }
}
