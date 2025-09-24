package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.Logs;

import java.util.HashSet;
import java.util.Set;

public class MockOptions implements WebDriver.Options {
    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public void deleteCookieNamed(String name) {

    }

    @Override
    public void deleteCookie(Cookie cookie) {

    }

    @Override
    public void deleteAllCookies() {

    }

    @Override
    public Set<Cookie> getCookies() {
        return new HashSet<>();
    }

    @Override
    public Cookie getCookieNamed(String name) {
        return new Cookie("mockCookie","mckValue");
    }

    @Override
    public WebDriver.Timeouts timeouts() {
        return new MockTimeouts();
    }

    @Override
    public WebDriver.Window window() {
        return new MockWindow();
    }

    @Override
    public Logs logs() {
        return new MockLogs();
    }
}
