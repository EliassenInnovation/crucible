package com.eliassen.crucible;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.eliassen.crucible") // Replace with your module's base package
@IncludeClassNamePatterns(".*Test")      // Match test class names (e.g., ending with 'Test')
public class ModuleTestSuite {
}

