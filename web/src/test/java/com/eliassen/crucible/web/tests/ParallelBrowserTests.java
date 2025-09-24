package com.eliassen.crucible.web.tests;

import java.util.concurrent.LinkedBlockingQueue;

public class ParallelBrowserTests
{
    public static LinkedBlockingQueue<ResultClass> queue;

//    @Test
//    public void test_MultipleChromesTest()
//    {
//        queue = new LinkedBlockingQueue<>();
//        String desiredUrl = "https://www.google.com/";
//        int numberOfInstance = 10;
//        //ArrayList<CompletableFuture<Boolean>> driverWorked = new ArrayList<>();
//        System.setProperty("environment","dev");
//
//        ArrayList<Thread> threads = new ArrayList<>();
//        for(int x = 0; x < numberOfInstance; x++)
//        {
//            threads.add(new Thread(new Runnable()
//            {
//                public void run()
//                {
//                    CurrentPage.setPageObject(new PageObjectBase()
//                    {
//                        @Override
//                        public void fillPageTable()
//                        {
//
//                        }
//                    });
//                    CurrentPage.store("url_dev", desiredUrl);
//                    CurrentPage.setDevice(Driver.CHROME);
//                    String actualUrl = null;
//                    CurrentPage.goTo();
//                    int attempts = 0;
//                    do
//                    {
//                        try
//                        {
//                            actualUrl = CurrentPage.actualURL();
//                        }
//                        catch(NullPointerException n)
//                        {
//                            try
//                            {
//                                attempts++;
//                                CurrentPage.getDriver().navigate().refresh();
//                                Thread.currentThread().wait(1000 * attempts);
//                            } catch (InterruptedException e)
//                            {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    while(actualUrl == null);
//
//                    CurrentPage.getDriver().quit();
//                    try
//                    {
//                        ResultClass rc = new ResultClass();
//                        rc.actualUrl = actualUrl;
//                        rc.success = actualUrl.equals(desiredUrl);
//                        queue.put(rc);
//                    } catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }));
//            threads.get(x).start();
//        }
//
//        boolean done = false;
//        int queueSize = 0;
//        while(queueSize < numberOfInstance)
//        {
//            queueSize = queue.size();
//        }
//
//        boolean success = true;
//        double totalExecutionTime = 0;
//        for(Thread thread : threads)
//        {
//            try
//            {
//                ResultClass rc = null;
//                if(queue.peek() != null)
//                {
//                    rc = queue.take();
//                }
//
//                System.out.println("Succeeded: " + rc.success);
//                System.out.println("ActualUrl: " + rc.actualUrl);
//
//                if(!rc.success)
//                {
//                    success = false;
//
//                }
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        //double averageExecutionTime = totalExecutionTime / numberOfRuns;
//        //Logger.log("Average execution time: " + averageExecutionTime);
//
//        //CurrentPage.store("loadtestresult",Boolean.toString(success));
//        assertTrue(success);
//    }

    private class ResultClass
    {
        public boolean success;
        public String actualUrl;
    }
}
