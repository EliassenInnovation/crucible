package com.eliassen.crucible.core.helpers;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.sharedobjects.MasterMind;
import io.cucumber.core.cli.Main;

import java.util.ArrayList;
import java.util.List;

public class ParallelHelper {

    public static final String PARALLEL_RUN_RESULTS = "parallel_run_results";

    public ParallelRunResults runInParallel(String[] environments, String tagsExpression){
        System.setProperty("cucumber.filter.tags", tagsExpression);
        return runInParallel(environments);
    }

    public ParallelRunResults runInParallel(String[] environments) {
        ParallelRunResults parallelRunResults = new ParallelRunResults();

        List<Thread> threads = new ArrayList<>();
        String[] cucumberOptions = System.getProperty("cucumber.expression").split(",");

        for (int i = 0; i < environments.length; i++) {
            final int index = i;
            final String environment = environments[i];

            Thread thread = new Thread(() -> {
                SystemHelper.cacheConfigSetting("environment", environment);
                try {
                    parallelRunResults.exitCodes.add(Main.run(cucumberOptions, Thread.currentThread().getContextClassLoader()));
                } catch (AssertionError a) {
                    //do nothing
                }
                parallelRunResults.screenShotPaths.add(MasterMind.retrieve(ScreenShotterBase.LATEST_SCREENSHOT_PATH));

            }, "cucumber-" + environment);

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return parallelRunResults;
    }

    public class ParallelRunResults{
        public List<String> screenShotPaths = new ArrayList<>();
        public List<Byte> exitCodes = new ArrayList<>();
    }
}
