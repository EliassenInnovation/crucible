package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

public class MockWindow implements WebDriver.Window{
    @Override
    public Dimension getSize() {
        return null;
    }

    @Override
    public void setSize(Dimension targetSize) {

    }

    @Override
    public Point getPosition() {
        return null;
    }

    @Override
    public void setPosition(Point targetPosition) {

    }

    @Override
    public void maximize() {

    }

    @Override
    public void minimize() {

    }

    @Override
    public void fullscreen() {

    }
}
