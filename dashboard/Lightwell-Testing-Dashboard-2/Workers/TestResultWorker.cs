using Lightwell_Testing_Dashboard_2.Models;
using Lightwell_Testing_Dashboard_2.Models.JobFeed;
using Lightwell_Testing_Dashboard_2.Tools;
using Microsoft.AspNetCore.Components.Forms;
using Microsoft.Extensions.Configuration;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.Json.Nodes;
using System.Threading;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Workers
{
    public class TestResultWorker : WorkerBase
    {
        public const string NOT_FOUND = "not found";
        public const string BUILDING = "Building";
        public const string SUCCESS = "SUCCESS";
        public const string DISABLED = "disabled";
        public const string NOT_RUN = "not run";
        public const string FAILURE = "FAILURE";
        public const string ABORTED = "ABORTED";

        public const string NORMAL_SORT = "normal";
        public const string TIME_DESC_SORT = "time_desc";
        public const string TIME_ASC_SORT = "time_asc";

        public TestResultWorker(IConfiguration config) : base(config) { }

        private string _sortOrder;
        public string SortOrder
        {
            get
            {
                if (string.IsNullOrEmpty(_sortOrder))
                {
                    _sortOrder = NORMAL_SORT;
                }
                return _sortOrder;
            }
            set
            {
                _sortOrder = value;
            }
        }

        private string[] _buildNamesToIgnore;

        public string[] BuildNamesToIgnore
        {
            get
            {
                if (_buildNamesToIgnore == null)
                {
                    _buildNamesToIgnore = Config.GetSection("jenkins").GetSection("notTheseBuilds").Get<string[]>();
                }
                //if still null, return empty string
                if (_buildNamesToIgnore == null)
                {
                    return new string[0];
                }
                else
                {
                    return _buildNamesToIgnore;
                }
            }
        }

        private static ConcurrentDictionary<string, List<BuildResult>> _buildsByStatus;
        public static ConcurrentDictionary<string, List<BuildResult>> BuildsByStatus
        {
            get
            {
                if (_buildsByStatus == null)
                {
                    _buildsByStatus = new ConcurrentDictionary<string, List<BuildResult>>();
                }
                return _buildsByStatus;
            }
            set
            {
                _buildsByStatus = value;
            }
        }

        public DateTime OneDayAgo { get; set; }
        public static List<BuildResult> TestResults { get; set; }
        public static List<BuildResult> FailedTestResultsFromJobFeed { get; set; }
        public static JobFeed FailedJobFeed { get; set; }

        public async Task<List<JenkinsBuild>> GetJenkinsJobsAsync(string path = ApiWorker.JSON_API, string parent = "")
        {
            //makes it so we only see a single folder
            if(!DataGrabber.StartingFolder.Equals(string.Empty) && path.Equals(ApiWorker.JSON_API))
            {
                string additionalPath = "";
                string[] folderParts = DataGrabber.StartingFolder.Split(".");
                foreach (string part in folderParts)
                {
                    additionalPath += "/job/" + part;
                }
                path = additionalPath + path;
            }

            JObject buildsJson = ApiWorker.GetJsonFromApi(path);
            if(buildsJson == null)  
            {
                return new List<JenkinsBuild>();
            }

            JArray jobsArray = (JArray)buildsJson.GetValue("jobs");
            ConcurrentBag<JenkinsBuild> builds = new ConcurrentBag<JenkinsBuild>();

            if (jobsArray.Count > 50)
            {
                await Parallel.ForEachAsync(jobsArray, async (job, _) =>
                {
                    await ProcessJobAsync(job, builds);
                });
            }
            else
            {
                foreach (var job in jobsArray)
                {
                    await ProcessJobAsync(job, builds);
                }
            }

            return builds.ToList();
        }

        private async Task ProcessJobAsync(JToken job, ConcurrentBag<JenkinsBuild> builds)
        {
            JenkinsBuild build = JsonConvert.DeserializeObject<JenkinsBuild>(job.ToString());
            if (build.IsBuild)
            {
                builds.Add(build);
            }
            else if (build.IsFolder && ShouldIncludeFolder(build))
            {
                string folderPath = ApiWorker.ConstructApiUrl(new string[] { build.Url, ApiWorker.JSON_API });
                var subBuilds = await GetJenkinsJobsAsync(folderPath);
                foreach (var subBuild in subBuilds)
                {
                    builds.Add(subBuild);
                }
            }
        }

        public async void UpdateBuildInfoAsync(string jobPath)
        {
            if (!jobPath.Contains(ApiWorker.Jenkins))
            {
                if (!jobPath.StartsWith("/job/"))
                {
                    jobPath = "/job/" + jobPath;
                }

                    jobPath = ApiWorker.ConstructApiUrl(new string[] { ApiWorker.Jenkins, jobPath });
            }

            JObject buildJson = ApiWorker.GetJsonFromApi(jobPath + "/api/json");
            ConcurrentBag<JenkinsBuild> builds = new ConcurrentBag<JenkinsBuild>();
            await ProcessJobAsync(buildJson, builds);

            BuildResult result = await GetTestResultTask(builds.First());
            BuildResult oldBuild = TestResults.FirstOrDefault(r => r.BuildName.Equals(result.BuildName));

            if (oldBuild != null)
            {
                // Update the existing build result
                oldBuild.UpdateFrom(result);
            }
            else
            {
                // If not found, add it as a new entry
                TestResults.Add(result);
            }
        }

        public bool ShouldIncludeFolder(JenkinsBuild build)
        {
            HashSet<string> includeFolders = new HashSet<string>(DataGrabber.RestrictToFolders);
            HashSet<string> ignoreFolders = new HashSet<string>(DataGrabber.FoldersToIgnore);

            if (includeFolders.Count > 0 && !includeFolders.Any(build.JobPath.Contains))
                return false;

            return !ignoreFolders.Any(build.JobPath.Contains);
        }


        private int _alreadyRunning = 0;

        /// <summary>
        /// Triggered by BuildTestResultsService
        /// </summary>
        public async Task BuildTestResults()
        {
            if (Interlocked.Exchange(ref _alreadyRunning, 1) == 1)
                return; // Exit if already running

            try
            {
                List<JenkinsBuild> builds = await GetJenkinsJobsAsync();
                if (builds != null)
                {
                    List<BuildResult> unsortedBuilds = await GetTestResults(builds);
                    List<BuildResult> sortedBuilds = SortBuilds(unsortedBuilds);
                    TestResults?.Clear();
                    TestResults = sortedBuilds;
                    BuildsByStatusBuilder(TestResults);
                }
            }
            finally
            {
                Interlocked.Exchange(ref _alreadyRunning, 0); // Reset flag
            }
        }

        public List<BuildResult> GetTestResults(string sort = NORMAL_SORT, bool refresh = false)
        {
            if(TestResults == null)
            {
                BuildTestResults();
            }

            return TestResults;
        }

        private static void BuildsByStatusBuilder(List<BuildResult> builds)
        {
            BuildsByStatus.Clear();

            string falseParent = DataGrabber.StartingFolder.Split(".").LastOrDefault();

            foreach (BuildResult build in builds)
            {
                if(!falseParent.Equals(string.Empty))
                {
                    if (!build.Parent.Contains(falseParent))
                    {
                        break;
                    }
                    else
                    {
                        string[] parts = build.Parent.Split(".");
                        int index = Array.IndexOf(parts, falseParent);

                        build.Parent = string.Join(".", parts.Skip(index));
                    }
                }

                BuildsByStatus.AddOrUpdate(build.Result, new List<BuildResult> { build },
                    (key, existingList) =>
                    {
                        existingList.Add(build);
                        return existingList;
                    });
            }
        }

        public async Task<List<BuildResult>> GetTestResults(List<JenkinsBuild> builds)
        {
            // Use ConcurrentBag for thread-safe operations
            ConcurrentBag<BuildResult> allResults = new ConcurrentBag<BuildResult>();

            // Use Parallel.ForEachAsync for concurrency
            await Parallel.ForEachAsync(builds, async (build, _) =>
            {
                BuildResult result = await GetTestResultTask(build);
                if (ResultShouldBeIncluded(result))
                {
                    allResults.Add(result);
                }
            });

            return allResults.ToList();
        }

        public async Task<BuildResult> GetTestResultTask(JenkinsBuild buildDefinition)
        {
            BuildResult buildResult = new BuildResult();

            // Fetch general build details
            JObject buildsJson = await ApiWorker.GetJsonFromApiAsync(ApiWorker.ConstructApiUrl(new[] { buildDefinition.Url, ApiWorker.JSON_API }));

            if (buildsJson != null)
            {
                JenkinsBuild build = JsonConvert.DeserializeObject<JenkinsBuild>(buildsJson.ToString());

                buildResult.BuildName = build.Name;
                buildResult.Parent = build.Parent;
                buildResult.FullName = build.FullName;
                buildResult.Description = build.Description;
                AddTagsToBuildResult(buildResult, build);

                // Handle specific build details
                if (build.NextBuildNumber > 1)
                {
                    buildResult.BuildNumber = build.LastBuild.Number;

                    string url = ApiWorker.ConstructApiUrl(new[] { buildDefinition.Url, buildResult.BuildNumber.ToString(), ApiWorker.JSON_API });

                    JObject specificBuildJson = await ApiWorker.GetJsonFromApiAsync(url);
                    if (specificBuildJson != null)
                    {
                        JenkinsBuild specificBuild = JsonConvert.DeserializeObject<JenkinsBuild>(specificBuildJson.ToString());
                        PopulateSpecificBuildDetails(buildResult, build, specificBuild);
                    }
                }
                else
                {
                    PopulateDefaultBuildDetails(buildResult, build);
                }
            }

            return buildResult;
        }

        private async void PopulateSpecificBuildDetails(BuildResult buildResult, JenkinsBuild build, JenkinsBuild specificBuild)
        {
            if (!build.Buildable)
            {
                buildResult.Result = DISABLED;
                buildResult.PassedTests = 0;
                buildResult.TotalTests = 0;
                return;
            }

            if (specificBuild != null) 
            {
                if (specificBuild.TestResults == null)
                {
                    buildResult.Result = ApiWorker.IsBuilding(build.Color) ? BUILDING : specificBuild.Result;
                    buildResult.PassedTests = -1;
                    buildResult.TotalTests = -1;
                }
                else
                {
                    buildResult.Result = specificBuild.Result;
                    buildResult.PassedTests = specificBuild.TestResults.TotalCount - specificBuild.TestResults.FailCount;
                    buildResult.TotalTests = specificBuild.TestResults.TotalCount;

                    // Fetch JUnit results
                    buildResult.JunitTestResult = await GetJunitTestCaseInfo(build.Url, buildResult.BuildNumber);

                    // Parse timestamps and durations
                    ParseBuildTimestampsAndDurations(buildResult, specificBuild);

                    if (ApiWorker.IsBuilding(build.Color))
                    {
                        buildResult.Result = BUILDING;
                        buildResult.Duration = "-";
                        buildResult.RunDate = "-";
                    }
                }
            }

            buildResult.Link = "link coming";
        }

        private void PopulateDefaultBuildDetails(BuildResult buildResult, JenkinsBuild build)
        {
            buildResult.Result = build.Buildable ? NOT_RUN : DISABLED;
            buildResult.Duration = "0";
            buildResult.RunDate = "not run";
            buildResult.PassedTests = 0;
            buildResult.TotalTests = 0;
        }

        private void ParseBuildTimestampsAndDurations(BuildResult buildResult, JenkinsBuild specificBuild)
        {
            try
            {
                double duration = double.Parse(specificBuild.Duration);
                buildResult.ActualDuration = duration;
                buildResult.Duration = TimeTools.ConvertJenkinsDurationToHoursAndMinutes(duration);
            }
            catch (FormatException)
            {
                buildResult.Duration = specificBuild.Duration;
            }

            try
            {
                double timeStamp = double.Parse(specificBuild.Timestamp);
                buildResult.RunDate = TimeTools.ConvertTimeStampInSecondsToDateTimeString(timeStamp);
            }
            catch (FormatException)
            {
                buildResult.RunDate = specificBuild.Timestamp;
            }
        }


        public async Task<JunitTestResults> GetJunitTestCaseInfo(string buildDefinitionUrl, int buildNumber)
        {
            JObject results = await GetJunitTestCaseInfoJson(buildDefinitionUrl, buildNumber);
            return JsonConvert.DeserializeObject<JunitTestResults>(results?.ToString());
        }

        public async Task<JObject> GetJunitTestCaseInfoJson(string buildDefinitionUrl, int buildNumber)
        {
            string testReportJsonApi = "testReport/api/json";
            string apiUrl = ApiWorker.ConstructApiUrl(new string[] { buildDefinitionUrl, buildNumber.ToString(), testReportJsonApi });
            return await ApiWorker.GetJsonFromApiAsync(apiUrl);
        }

        private void AddTagsToBuildResult(BuildResult result, JenkinsBuild jenkinsBuild)
        {
            result.Tags = GetTagsFromString(jenkinsBuild.Description);
        }

        private string[] GetTagsFromString(string description)
        {
            List<string> tags = new List<string>();
            string[] lines = !String.IsNullOrEmpty(description) ? description.Split("\r\n") : new string[0];
            foreach (string line in lines)
            {
                tags.AddRange(line.Split(" ").Where(s => s.Contains("@") || s.Contains("#")));
            }
            for (int x = 0; x < tags.Count; x++)
            {
                tags[x] = tags[x].Replace("@", "").Replace("#", "");
            }

            return tags.ToArray();
        }

        private bool ResultShouldBeIncluded(BuildResult build)
        {
            //figure out how to filter folders
            return !BuildNamesToIgnore.Any(n => n.ToLower().Equals(build.BuildName));
        }

        public bool IsBuildDirectory(string dir)
        {
            if (Directory.Exists(dir + Path.DirectorySeparatorChar + DataGrabber.BUILDS_DIR))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public List<BuildResult> SortBuilds(List<BuildResult> unsortedBuilds)
        {
            List<BuildResult> sortedBuilds;


            switch (SortOrder)
            {
                case TIME_DESC_SORT:
                case TIME_ASC_SORT:
                    sortedBuilds = TimeSort(unsortedBuilds);
                    break;
                default:
                    sortedBuilds = NormalSort(unsortedBuilds);
                    break;
            }

            return sortedBuilds;
        }

        private List<BuildResult> NormalSort(List<BuildResult> builds)
        {
            builds.Sort((a, b) =>
            {
                int successComparison = (a.Result == SUCCESS ? 1 : 0).CompareTo(b.Result == SUCCESS ? 1 : 0);
                if (successComparison != 0) return successComparison;

                int resultComparison = string.Compare(a.Result, b.Result, StringComparison.Ordinal);
                if (resultComparison != 0) return resultComparison;

                return string.Compare(a.BuildName, b.BuildName, StringComparison.Ordinal);
            });

            return builds;
        }


        private List<BuildResult> TimeSort(List<BuildResult> unsortedBuilds)
        {
            Dictionary<double, List<BuildResult>> buildResultDurationBuckets = new Dictionary<double, List<BuildResult>>();
            List<BuildResult> sortedBuilds = new List<BuildResult>();

            foreach (BuildResult result in unsortedBuilds)
            {
                if (!buildResultDurationBuckets.ContainsKey(result.ActualDuration))
                {
                    buildResultDurationBuckets.Add(result.ActualDuration, new List<BuildResult>());
                }

                buildResultDurationBuckets[result.ActualDuration].Add(result);
            }

            List<double> keys = buildResultDurationBuckets.Keys.ToList<double>();

            if (SortOrder.Equals(TIME_ASC_SORT))
            {
                keys.Sort((a, b) => a.CompareTo(b));
            }
            else if (SortOrder.Equals(TIME_DESC_SORT))
            {
                keys.Sort((a, b) => b.CompareTo(a));
            }

            foreach (double key in keys)
            {
                foreach (BuildResult result in buildResultDurationBuckets[key])
                {
                    sortedBuilds.Add(result);
                }
            }

            return sortedBuilds;
        }

        public void BuildTestResultsFromJobFeedEntries()
        {
            try
            {
                OneDayAgo = DateTime.Now.AddDays(-1);

                // Fetch the failed job feed
                FailedJobFeed = DataGrabber.GetFailedJobFeed();
                List<JobFeedEntry> recentFails = FailedJobFeed.BuildsThatJustBroke;

                // Use a HashSet for faster lookups
                HashSet<string> recentFailNames = new HashSet<string>(recentFails.Select(rf => rf.Name));

                // Get and filter test results
                FailedTestResultsFromJobFeed = GetTestResultsFromJobFeedEntries(recentFailNames)
                    .Where(tr => ShouldIncludeTestResult(tr))
                    .ToList();
            }
            catch(NullReferenceException n)
            {
                Console.WriteLine(n.Message);
                Console.WriteLine(n.StackTrace.ToString());
            }
        }

        private bool ShouldIncludeTestResult(BuildResult testResult)
        {
            // Exclude certain statuses
            if (testResult == null || testResult.Result == null || 
                testResult.Result.Equals(TestResultWorker.DISABLED) ||
                testResult.Result.Equals(TestResultWorker.SUCCESS) ||
                testResult.Result.Equals(TestResultWorker.BUILDING))
            {
                return false;
            }

            // Include only if RunDate is within the last day
            if (DateTime.TryParse(testResult.RunDate, out DateTime runDate))
            {
                return runDate > OneDayAgo;
            }

            // Exclude invalid dates
            return false;
        }

        internal List<BuildResult> GetTestResultsFromJobFeedEntries(HashSet<string> recentFailNames)
        {
            if (recentFailNames == null)
            {
                throw new ArgumentNullException(nameof(recentFailNames), "recentFailNames cannot be null.");
            }

            if (TestResults == null)
            {
                GetTestResults();
                if (TestResults == null) // Double-check after initialization
                {
                    throw new InvalidOperationException("TestResults is still null after GetTestResults() call.");
                }
            }

            return TestResults
                .Where(tr => recentFailNames.Contains(tr.FullName))
                .ToList();
        }
    }
}
