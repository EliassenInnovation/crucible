using Lightwell_Testing_Dashboard_2.Models;
using Lightwell_Testing_Dashboard_2.Models.JobFeed;
using Lightwell_Testing_Dashboard_2.Workers;
using Microsoft.Extensions.Configuration;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Xml;

namespace Lightwell_Testing_Dashboard_2.Tools
{
    public class DataGrabber
    {
        public const string BUILDS_DIR = "builds";
        public const string CUCUMBER_REPORTS_DIR = "cucumber-reports";
        public const string CUCUMBER_REPORT_FILE_NAME = "cucumber-trends.json";

        Dictionary<string, Dictionary<string, string[]>> _jobs;

        public Dictionary<string, Dictionary<string,string[]>> Jobs
        {
            get
            {
                if(_jobs == null)
                {
                    _jobs = new Dictionary<string, Dictionary<string, string[]>>();
                }
                return _jobs;
            }
        }

        public static HttpClient Client
        {
            get
            {
                return new HttpClient();
            }
        }

        private static string[] _foldersToIgnore;
        public static string[] FoldersToIgnore
        {
            get
            {
                if (_foldersToIgnore == null)
                {
                    _foldersToIgnore = WorkerBase.Config
                        .GetSection("jenkins")
                        .GetSection("notTheseFolders")
                        .Get<string[]>()
                        ?.Select(folder => folder.ToLowerInvariant())
                        .ToArray() ?? Array.Empty<string>();
                }
                return _foldersToIgnore;
            }
        }

        private static string[] _restrictToFolders;
        public static string[] RestrictToFolders
        {
            get
            {
                if (_restrictToFolders == null)
                {
                    _restrictToFolders = WorkerBase.Config
                        .GetSection("jenkins")
                        .GetSection("onlyTheseFolders")
                        .Get<string[]>()
                        ?.Select(folder => folder.ToLowerInvariant())
                        .ToArray() ?? Array.Empty<string>();

                    if (_restrictToFolders == null)
                        _restrictToFolders = new string[0];
                    }
                return _restrictToFolders;
            }
        }

        private static string _startingFolder;
        public static string StartingFolder 
        { 
            get
            {
                if(_startingFolder == null)
                {
                    _startingFolder = WorkerBase.Config.GetSection("jenkins").GetSection("startingFolder").Get<string>() ?? string.Empty;
                }
                return _startingFolder;
            }
        }

        public static string[] GetSubDirs(string dir)
        {
            string[] subDirs = Directory.GetDirectories(dir);

            if(RestrictToFolders != null && RestrictToFolders.Length > 0)
            {
                subDirs = RestrictToOnlyTheIncludeDirectories(subDirs);
            }
            else if (FoldersToIgnore != null && FoldersToIgnore.Length > 0)
            {
                subDirs = FilteroutIgnoredDirectories(subDirs);
            }

            return subDirs;
        }

        private static string[] FilteroutIgnoredDirectories(string[] subDirs)
        {
            List<string> dirs = new List<string>();
            foreach(string dir in subDirs)
            {
                bool includeThisDir = true;
                foreach(string dirNameToIgnore in FoldersToIgnore)
                {
                    if(dir.Contains(dirNameToIgnore))
                    {
                        includeThisDir = false;
                        break;
                    }
                }

                if(includeThisDir)
                {
                    dirs.Add(dir);
                }
            }

            return dirs.ToArray();
        }

        private static string[] RestrictToOnlyTheIncludeDirectories(string[] subDirs)
        {
            List<string> dirs = new List<string>();
            foreach (string dir in subDirs)
            {
                bool includeThisDir = false;
                foreach (string dirNameToInclude in RestrictToFolders)
                {
                    if (dir.ToLower().Contains(dirNameToInclude.ToLower()))
                    {
                        includeThisDir = true;
                        break;
                    }
                }

                if (includeThisDir)
                {
                    dirs.Add(dir);
                }
            }

            return dirs.ToArray();
        }

        public static string GetSubDirectoryNameString(string subDirPath)
        {
            string[] splitDirName;
            string subDirName = null;

            splitDirName = subDirPath.Split(Path.DirectorySeparatorChar);

            int indexForSubDir = splitDirName.Length - 1;

            while(string.IsNullOrEmpty(subDirName))
            {
                subDirName = splitDirName[indexForSubDir--];
            }

            return subDirName;
        }

        public static Dictionary<string, Dictionary<string, string[]>> GetCucumberJsonForAllBuilds(List<string> dirs)
        {
            string reportDir;
            Dictionary<string, string[]> specificReport;

            Dictionary<string, Dictionary<string, string[]>> jobSpecificReports = new Dictionary<string, Dictionary<string, string[]>>();

            foreach (string dir in dirs)
            {
                reportDir = dir + Path.DirectorySeparatorChar + CUCUMBER_REPORTS_DIR + Path.DirectorySeparatorChar;

                if(FileTool.DirectoryExists(reportDir))
                {
                    specificReport = ParseJobReport(reportDir, dir);
                    jobSpecificReports.Add(GetSubDirectoryNameString(dir), specificReport);
                }
            }

            return jobSpecificReports;
        }

        public static Dictionary<string,string[]> ParseJobReport(string reportDir, string parentDir)
        {
            string filePath = reportDir + Path.DirectorySeparatorChar;
            JObject parsedJson = JObject.Parse(FileTool.GetTextFileContents(CUCUMBER_REPORT_FILE_NAME, filePath));
            Dictionary<string, string[]> parsedReport = JsonConvert.DeserializeObject<Dictionary<string, string[]>>(parsedJson.ToString());
            parsedReport["durations"] = GetBuildsMostRecentDuration(parentDir);

            return parsedReport;
        }

        private static string[] GetBuildsMostRecentDuration(string parentDir)
        {
            XmlDocument buildXMLFile = GetMostRecentBuildXml(parentDir);
            string duration = buildXMLFile.GetElementsByTagName("duration")[0].InnerText;

            return new string[] { duration };
        }

        public static string GetBuildRunDate(string jobName, int buildNumber)
        {
            string buildXMLFile = WorkerBase.JENKINS_JOBS_DIR
                + jobName;

            string runDate = null;

            if(File.Exists(buildXMLFile))
            {
                XmlDocument buildXml = GetSpecificBuildXml(buildXMLFile,buildNumber); 

                XmlNode timeStampNode = buildXml.GetElementsByTagName("timestamp")[0];
                double timestamp = double.Parse(timeStampNode.InnerText);

                runDate = TimeTools.EPOCH.AddMilliseconds(timestamp).ToString();
            }

            return runDate;
        }

        //api flow
        public static Dictionary<int, CucumberTrend> GetCucumberTrends(JenkinsBuild build)
        {
            Dictionary<int, CucumberTrend> trends = new Dictionary<int, CucumberTrend>();

            //attempt to get trends from the job Workspace
            string cucumberTrendsUrl = build.Url + ApiWorker.WORKSPACE + "cucumber-trends.json";
            JObject cucumberTrends = ApiWorker.GetJsonFromApi(cucumberTrendsUrl, true);

            if(cucumberTrends != null)
            {
                trends = ParseCucumberTrends(cucumberTrends);
            }

            return trends;
        }

        public static Dictionary<int, CucumberTrend> GetCucumberTrends(string parentDir)
        {
            Dictionary<int, CucumberTrend> trends = new Dictionary<int, CucumberTrend>();
            string cucumberTrendsPath = "";

            try
            {
                cucumberTrendsPath = parentDir + Path.DirectorySeparatorChar + "cucumber-reports" +
                    Path.DirectorySeparatorChar + "cucumber-trends.json";
                if (File.Exists(cucumberTrendsPath))
                {
                    JObject cucumberTrends = JObject.Parse(File.ReadAllText(cucumberTrendsPath));

                    trends = ParseCucumberTrends(cucumberTrends);
                }
            }
            catch(Exception e)
            {
                System.Console.WriteLine("Attempting to read " + cucumberTrendsPath);
                throw;
            }

            return trends;
        }

        public static Dictionary<int, CucumberTrend> ParseCucumberTrends(JObject cucumberTrends)
        {
            Dictionary<int, CucumberTrend> trends = new Dictionary<int, CucumberTrend>();

            JArray buildNumbers = (JArray)cucumberTrends["buildNumbers"];
            JArray passedFeatures = (JArray)cucumberTrends["passedFeatures"];
            JArray failedFeatures = (JArray)cucumberTrends["failedFeatures"];
            JArray totalFeatures = (JArray)cucumberTrends["totalFeatures"];
            JArray passedScenarios = (JArray)cucumberTrends["passedScenarios"];
            JArray failedScenarios = (JArray)cucumberTrends["failedScenarios"];
            JArray totalScenarios = (JArray)cucumberTrends["totalScenarios"];
            JArray passedSteps = (JArray)cucumberTrends["passedSteps"];
            JArray failedSteps = (JArray)cucumberTrends["failedSteps"];
            JArray skippedSteps = (JArray)cucumberTrends["skippedSteps"];
            JArray pendingSteps = (JArray)cucumberTrends["pendingSteps"];
            JArray undefinedSteps = (JArray)cucumberTrends["undefinedSteps"];
            JArray totalSteps = (JArray)cucumberTrends["totalSteps"];
            JArray durations = (JArray)cucumberTrends["durations"];

            CucumberTrend trend;

            for (int x = 0; x < buildNumbers.Count; x++)
            {
                trend = new CucumberTrend();
                trend.PassedFeatures = passedFeatures[x].ToObject<int>();
                trend.FailedFeatures = failedFeatures[x].ToObject<int>();
                trend.TotalFeatures = totalFeatures[x].ToObject<int>();
                trend.PassedScenarios = passedScenarios[x].ToObject<int>();
                trend.FailedScenarios = failedScenarios[x].ToObject<int>();
                trend.TotalScenarios = totalScenarios[x].ToObject<int>();
                trend.PassedSteps = passedSteps[x].ToObject<int>();
                trend.FailedSteps = failedSteps[x].ToObject<int>();
                trend.SkippedSteps = skippedSteps[x].ToObject<int>();
                trend.PendingSteps = pendingSteps[x].ToObject<int>();
                trend.UndefinedSteps = undefinedSteps[x].ToObject<int>();
                trend.TotalSteps = totalSteps[x].ToObject<int>();
                trend.Duration = durations[x].ToObject<Int64>();

                trends.Add(Int32.Parse(buildNumbers[x].ToString()), trend);
            }

            return trends;
        }

        //api flow
        public static int GetMostRecentBuildNumber(JenkinsBuild build)
        {
            string apiUrl = ApiWorker.ConstructApiUrl(new string[] { build.Url, ApiWorker.JSON_API });
            JObject buildsJson = ApiWorker.GetJsonFromApi(apiUrl);
            int nextBuildNumber = Int32.Parse(buildsJson.GetValue("nextBuildNumber").ToString());
            int mostRecentBuildNumber = nextBuildNumber - 1;
            return mostRecentBuildNumber;
        }

        public static int GetMostRecentBuildNumber(string parentDir)
        {
            string nextBuildNumberFile = parentDir + Path.DirectorySeparatorChar + "nextBuildNumber";
            string nextBuildNumberString = File.ReadAllText(nextBuildNumberFile);

            int nextBuildNumber = Int32.Parse(nextBuildNumberString);

            int mostRecentBuildNumber = nextBuildNumber - 1;

            return mostRecentBuildNumber;
        }

        public static XmlDocument GetMostRecentBuildXml(string parentDir)
        {
            int mostRecentBuildNumber = DataGrabber.GetMostRecentBuildNumber(parentDir);

            string buildXmlFilePath = parentDir + Path.DirectorySeparatorChar + BUILDS_DIR + Path.DirectorySeparatorChar + mostRecentBuildNumber + Path.DirectorySeparatorChar + "build.xml";

            XmlDocument buildXml = null;
            if (File.Exists(buildXmlFilePath))
            {
                buildXml = CreateXml(buildXmlFilePath);
            }

            return buildXml;
        }

        //api flow
        public static JObject GetSpecificBuildJson(JenkinsBuild build, int buildNumber)
        {
            String apiUrl = ApiWorker.ConstructApiUrl(new string[] { build.Url, buildNumber.ToString(), ApiWorker.JSON_API });
            JObject buildJson = ApiWorker.GetJsonFromApi(apiUrl);
            return buildJson;
        }

        public static XmlDocument GetBuildConfigXml(string parentDir)
        {
            string configXmlFilePath = parentDir + Path.DirectorySeparatorChar + "config.xml";
            XmlDocument configXml = null;

            if (File.Exists(configXmlFilePath))
            {
                configXml = CreateXml(configXmlFilePath);
            }

            return configXml;
        }

        public static XmlDocument GetSpecificBuildXml(string parentDir, int buildNumber)
        {
            string buildXmlFilePath = parentDir;

            //not sure why the path WOULD contain the build number
            //this messed with builds that contained numbers 
            //ie rowCounts10059 fails on build number "10" since "10059" contains "10"

            buildXmlFilePath += Path.DirectorySeparatorChar + BUILDS_DIR + Path.DirectorySeparatorChar + buildNumber;

            buildXmlFilePath += Path.DirectorySeparatorChar + "build.xml";

            XmlDocument buildXml = null;
            if (File.Exists(buildXmlFilePath))
            {
                buildXml = CreateXml(buildXmlFilePath);
            }

            return buildXml;
        }

        private static XmlDocument CreateXml(string xmlFilePath)
        {
            string xmlString = File.ReadAllText(xmlFilePath);
            XmlDocument xml = new XmlDocument();
            xmlString = FixXmlVersion(xmlString);
            xml.LoadXml(xmlString);

            return xml;
        }

        private static string FixXmlVersion(string xmlAsString)
        {
            string fixedXmlString = xmlAsString.Replace("version='1.1'", "version='1.0'");
            return fixedXmlString;
        }

        public static JobFeed GetFailedJobFeed()
        {
            return GetJobFeed("rssFailed");
        }

        public static JobFeed GetLatestJobFeed()
        {
            return GetJobFeed("rssLatest");
        }

        public static JobFeed GetAllJobFeed()
        {
            return GetJobFeed("rssAll");
        }

        public static JobFeed GetJobFeed(string jobFeedUrl)
        {
            return new JobFeed(GetJobFeedEntries(jobFeedUrl));
        }

        //public static List<JobFeedEntry> GetJobFeedEntries(string jobFeedUrl)
        //{
        //    List<JobFeedEntry> jobFeedEntries = new List<JobFeedEntry>();

        //    XmlDocument jobFeedXml = ApiWorker.GetXmlFromApi(jobFeedUrl);

        //    foreach (XmlNode entry in jobFeedXml.GetElementsByTagName("entry"))
        //    {
        //        jobFeedEntries.Add(new JobFeedEntry(entry));
        //    }

        //    return jobFeedEntries;
        //}

        public static List<JobFeedEntry> GetJobFeedEntries(string jobFeedUrl)
        {
            XmlDocument jobFeedXml = ApiWorker.GetXmlFromApi(jobFeedUrl);
            return jobFeedXml.GetElementsByTagName("entry")
                             .Cast<XmlNode>() // Cast XmlNodeList to IEnumerable<XmlNode>
                             .Select(entry => new JobFeedEntry(entry)) // Map each node to JobFeedEntry
                             .ToList(); // Convert to a List<JobFeedEntry>
        }
    }

    
}
