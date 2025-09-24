using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Workers.Services
{
    public class BuildTestResultsService : BackgroundService
    {
        readonly TestResultWorker _testResultWorker;
        readonly IConfiguration _config;

        public BuildTestResultsService(TestResultWorker testResultWorker, IConfiguration config)
        {
            _testResultWorker = testResultWorker;
            _config = config;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    // Your recurring task logic here
                    Console.WriteLine($"Task running at {DateTime.Now}");
                    _testResultWorker.BuildTestResults();
                    await Task.Delay(TimeSpan.FromMinutes(_config.GetValue<int>("TestResultsRefreshInterval")), stoppingToken); // Run every minute
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Error: {ex.Message}");
                }
            }
        }
    }
}
