package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.Alert;

public class MockAlert implements Alert {
    @Override
    public void dismiss() {

    }

    @Override
    public void accept() {

    }

    @Override
    public String getText() {
        return "textValue";
    }

    @Override
    public void sendKeys(String keysToSend) {

    }
}
