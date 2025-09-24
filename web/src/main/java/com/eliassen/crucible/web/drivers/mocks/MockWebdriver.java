package com.eliassen.crucible.web.drivers.mocks;

import com.eliassen.crucible.web.drivers.CrucibleWebdriver;

public class MockWebdriver extends CrucibleWebdriver {
    public MockWebdriver(){
        setInstance(new MockDriver());
    }
}
