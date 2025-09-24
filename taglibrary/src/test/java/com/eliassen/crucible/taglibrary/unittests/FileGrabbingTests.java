package com.eliassen.crucible.taglibrary.unittests;

import com.eliassen.crucible.taglibrary.worker.TagGrabber;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class FileGrabbingTests {
    private TagGrabber tagGrabber;

    @Before
    public void init() {
        tagGrabber = new TagGrabber();
    }

    @Test
    public void canGrabFeatureFiles() {
        try {
            Map<String, Object> featureFIles = tagGrabber.getFeatureFiles();
            assertTrue(featureFIles.size() > 0);
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    @Test
    public void canFindTags() {
        try {
            Set<String> tags = tagGrabber.getTagsFromFeatureFiles();
            assertTrue(tags.size() > 0);
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    @After
    public void cleanUp() {
        tagGrabber = null;
    }
}
