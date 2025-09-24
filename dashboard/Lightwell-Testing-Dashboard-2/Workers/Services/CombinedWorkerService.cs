using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using System.Threading.Tasks;
using System.Threading;
using System;
using System.Linq;

namespace Lightwell_Testing_Dashboard_2.Workers.Services
{
    public class CombinedWorkerService : BackgroundService
    {
        private readonly TestResultWorker _testResultWorker;
        private readonly IConfiguration _config;

        public CombinedWorkerService(TestResultWorker testResultWorker, IConfiguration config)
        {
            _testResultWorker = testResultWorker;
            _config = config;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            // Extract refresh intervals from the configuration
            int testResultsInterval = _config.GetValue<int>("TestResultsRefreshInterval");
            int jobFeedInterval = _config.GetValue<int>("FailedJobFeedRefreshInterval");

            // Run both tasks asynchronously
            var buildTestResultsTask = RunBuildTestResultsAsync(testResultsInterval, stoppingToken);
            var getJobFeedTask = RunGetJobFeedAsync(jobFeedInterval, stoppingToken);

            // Wait for both tasks to complete (this only happens if the service stops)
            await Task.WhenAll(buildTestResultsTask, getJobFeedTask);
        }

        private async Task RunBuildTestResultsAsync(int intervalMinutes, CancellationToken stoppingToken)
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    //Console.WriteLine($"Running BuildTestResults at {DateTime.Now}");
                    _testResultWorker.BuildTestResults();
                    await Task.Delay(TimeSpan.FromMinutes(intervalMinutes), stoppingToken);
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Error in BuildTestResults: {ex.Message}");
                }
            }
        }

        private async Task RunGetJobFeedAsync(int intervalMinutes, CancellationToken stoppingToken)
        {
            const int maxRetries = 5;  // Maximum number of retries
            int retryDelayMs = 5000;   // Start with a 5-second delay

            while (!stoppingToken.IsCancellationRequested)
            {
                int retryCount = 0;
                bool success = false;

                while (retryCount < maxRetries && !success)
                {
                    try
                    {
                        // Ensure TestResults is not null or empty before proceeding
                        if (TestResultWorker.TestResults == null || !TestResultWorker.TestResults.Any())
                        {
                            throw new InvalidOperationException("TestResults is null or empty. Retrying...");
                        }

                        _testResultWorker.BuildTestResultsFromJobFeedEntries();
                        success = true; // If no exception, mark success
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine($"Error in GetJobFeed: {ex.Message}. Retrying in {retryDelayMs / 1000} seconds...");

                        retryCount++;
                        if (retryCount >= maxRetries)
                        {
                            Console.WriteLine("Max retry attempts reached. Skipping this cycle.");
                            break;
                        }

                        await Task.Delay(retryDelayMs, stoppingToken);
                        retryDelayMs *= 2; // Exponential backoff (5s -> 10s -> 20s ...)
                    }
                }

                // Wait for the next scheduled run (if stopping is not requested)
                if (!stoppingToken.IsCancellationRequested)
                {
                    await Task.Delay(TimeSpan.FromMinutes(intervalMinutes), stoppingToken);
                }
            }
        }


    }
}