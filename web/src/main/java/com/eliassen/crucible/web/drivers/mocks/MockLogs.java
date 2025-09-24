package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.Logs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MockLogs implements Logs {
    @Override
    public LogEntries get(String logType) {
        return new MockLogEntries(new ArrayList<LogEntry>());
    }

    @Override
    public Set<String> getAvailableLogTypes() {
        return new HashSet<>();
    }
}
