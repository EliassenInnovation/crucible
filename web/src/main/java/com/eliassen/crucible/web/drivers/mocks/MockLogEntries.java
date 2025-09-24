package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;

public class MockLogEntries extends LogEntries {
    public MockLogEntries(Iterable<LogEntry> entries) {
        super(entries);
    }
}
