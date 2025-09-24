using Lightwell_Testing_Dashboard_2.Models;
using Lightwell_Testing_Dashboard_2.Tools;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace Lightwell_Testing_Dashboard_2.Workers
{
    public class ReportWorker : WorkerBase
    {
        //change day over day
        //total for all tests
        //analyze builds using timestamp
        //only consider day, not hours:minutes, etc
        //highest total for day

        //data by build -> by day
        //by day by build name

        readonly IConfiguration _configuration;
        TestResultWorker _testResultWorker;

        public ReportWorker(IConfiguration config, TestResultWorker testResultWorker) : base(config)
        {
            _configuration = config;
            _testResultWorker = testResultWorker;
        }

        public void CreateScenariosByWeekCSV()
        {
            List<string> buildDirs = GetBuildDirectories(WorkerBase.JENKINS_JOBS_DIR);

            Dictionary<string, Dictionary<string, BuildResult>> buildResultsByNameAndBuildNumber = GetBuildResults(buildDirs);

            Dictionary<DateTime, int[]> scenarioTotalsByDay = GetScenarioCountsByDay(buildResultsByNameAndBuildNumber);

            Dictionary<string, int[]> scenarioCountByWeek = GetScenarioCountByWeek(scenarioTotalsByDay);

            CreateScenarioCountByWeekCSV(scenarioCountByWeek);

            var newScenarioTotalsByDay = GetNewScenarioCountsByDay(buildResultsByNameAndBuildNumber);

            var newScenariosByWeek = GetNewScenarioCountByWeek(newScenarioTotalsByDay);

            CreateNewScenarioCountByWeekCSV(newScenariosByWeek);
        }

        private void CreateScenarioCountByWeekCSV(Dictionary<string, int[]> scenarioCountByWeek)
        {
            string path = "C:\\Jenkins\\userContent\\scenarioByWeekReport_" + DateTime.Now.ToString("yyyyMMdd") + ".csv";

            using (var w = new StreamWriter(path))
            {
                string line = "WEEK,COUNT,TESTRUNS";
                w.WriteLine(line);
                w.Flush();

                foreach (string key in scenarioCountByWeek.Keys)
                {
                    string first = key;
                    int second = scenarioCountByWeek[key][0];
                    int third = scenarioCountByWeek[key][1];
                    line = string.Format("{0},{1},{2}", first, second, third);
                    w.WriteLine(line);
                    w.Flush();
                }
            }
        }

        public List<string> GetBuildDirectories(string dir)
        {
            List<string> buildDirs = new List<string>();
            string[] subDirs = DataGrabber.GetSubDirs(dir);
            foreach (string subDir in subDirs)
            {
                if (_testResultWorker.IsBuildDirectory(subDir))
                {
                    buildDirs.AddRange(GetBuildDirectoryNames(subDir));
                }
                else
                {
                    buildDirs.AddRange(GetBuildDirectories(subDir));
                }

            }

            return buildDirs;
        }

        public List<string> GetBuildDirectoryNames(string dir)
        {
            return Directory.EnumerateDirectories(dir + Path.DirectorySeparatorChar + DataGrabber.BUILDS_DIR).ToList();
        }

        private Dictionary<string, Dictionary<string, BuildResult>> GetBuildResults(List<string> buildDirs)
        {
            BuildResult result;
            Dictionary<string, Dictionary<string, BuildResult>> buildResults = new Dictionary<string, Dictionary<string, BuildResult>>();

            XmlDocument buildXml;

            foreach (string dir in buildDirs)
            {
                result = new BuildResult();
                string buildNumber = "1";//trw.GetLeaf(dir);
                string buildName = GetBuildNameFromBuildDir(dir);

                if (!buildResults.ContainsKey(buildName))
                {
                    buildResults.Add(buildName, new Dictionary<string, BuildResult>());
                }

                int buildNum = Int32.Parse(buildNumber);
                buildXml = DataGrabber.GetSpecificBuildXml(dir, buildNum);
                result = GetTestResult(buildXml, dir, "", buildNum);

                if (!buildResults[buildName].ContainsKey(buildNumber))
                {
                    buildResults[buildName].Add(buildNumber, result);
                }
                else
                {
                    buildResults[buildName][buildNumber] = result;
                }
            }

            return buildResults;

        }

        public BuildResult GetTestResult(XmlDocument buildXml, string buildDir, string parent, int buildNumber)
        {
            BuildResult testResult = new BuildResult();
            testResult.BuildNumber = buildNumber;
            string buildName = "";//trw.GetLeaf(buildDir);

            testResult.BuildName = buildName;

            string buildResult = "", buildDuration = "", buildRunDate = "";

            buildResult = null;// trw.GetNodeValue("result", buildXml);


            if (!buildResult.Equals(TestResultWorker.NOT_FOUND))
            {
                string durationAsString = null;// trw.GetNodeValue("duration", buildXml);
                string timeStampString = null;// trw.GetNodeValue("timestamp", buildXml);
                try
                {
                    double duration = double.Parse(durationAsString);
                    buildDuration = TimeTools.ConvertJenkinsDurationToHoursAndMinutes(duration);
                }
                catch (FormatException)
                {
                    buildDuration = durationAsString;
                }

                try
                {
                    double timeStamp = double.Parse(timeStampString);
                    buildRunDate = TimeTools.ConvertTimeStampInSecondsToDateTimeString(timeStamp);
                }
                catch (FormatException)
                {
                    buildRunDate = timeStampString;
                }
            }
            else
            {
                //don't care for reports
            }

            testResult.Result = buildResult;
            testResult.Duration = buildDuration;
            testResult.RunDate = buildRunDate;

            testResult.Parent = parent;
            testResult.Link = "link coming";

            Dictionary<int, CucumberTrend> trends = DataGrabber.GetCucumberTrends(GetBuildNameFromBuildDir(buildDir));

            if (trends.ContainsKey(buildNumber))
            {
                testResult.PassedTests = trends[buildNumber].PassedScenarios;
                testResult.TotalTests = trends[buildNumber].TotalScenarios;
            }
            else
            {
                testResult.PassedTests = -1;
                testResult.TotalTests = -1;
            }

            return testResult;
        }

        private string GetBuildNameFromBuildDir(string dir)
        {
            const string JUNK = "\\builds\\";
            string buildName = dir.Substring(0, dir.IndexOf(JUNK));

            return buildName;
        }

        private Dictionary<DateTime, int[]> GetScenarioCountsByDay(Dictionary<string, Dictionary<string, BuildResult>> buildResultsByNameAndBuildNumber)
        {
            Dictionary<DateTime, int[]> scenarioCountsByDay = new Dictionary<DateTime, int[]>();
            BuildResult result;
            DateTime dateOfBuild;

            foreach (string build in buildResultsByNameAndBuildNumber.Keys)
            {
                foreach (string buildNumber in buildResultsByNameAndBuildNumber[build].Keys)
                {
                    result = buildResultsByNameAndBuildNumber[build][buildNumber];
                    //get just date
                    //if it's "" skip
                    if (!string.IsNullOrEmpty(result.RunDate))
                    {
                        string dayOfBuild = DateTime.Parse(result.RunDate).ToString("yyyy-MM-dd");

                        dateOfBuild = DateTime.Parse(dayOfBuild);
                        if (!scenarioCountsByDay.ContainsKey(dateOfBuild))
                        {
                            scenarioCountsByDay.Add(dateOfBuild, new int[] { result.TotalTests, 1 });
                        }
                        else
                        {
                            scenarioCountsByDay[dateOfBuild][0] += result.TotalTests;
                            scenarioCountsByDay[dateOfBuild][1]++;
                        }
                    }

                }
            }

            return scenarioCountsByDay;
        }

        private Dictionary<DateTime, int> GetNewScenarioCountsByDay(Dictionary<string, Dictionary<string, BuildResult>> buildResultsByNameAndBuildNumber)
        {
            Dictionary<DateTime, int> newScenarioCountsByDay = new Dictionary<DateTime, int>();
            BuildResult result, previousResult;
            DateTime dateOfBuild;
            int newScenarios = 0;

            foreach (string build in buildResultsByNameAndBuildNumber.Keys)
            {
                foreach (string buildNumber in buildResultsByNameAndBuildNumber[build].Keys)
                {
                    newScenarios = 0;
                    result = buildResultsByNameAndBuildNumber[build][buildNumber];
                    string buildNumberPrevious = (Int32.Parse(buildNumber) - 1).ToString();
                    if (buildResultsByNameAndBuildNumber[build].ContainsKey(buildNumberPrevious))
                    {
                        previousResult = buildResultsByNameAndBuildNumber[build][buildNumberPrevious];
                        int delta = result.TotalTests - previousResult.TotalTests;
                        if (delta > 0)
                        {
                            newScenarios += delta;
                        }
                    }
                    else
                    {
                        newScenarios = result.TotalTests;
                    }

                    //get just date
                    //if it's "" skip
                    if (!string.IsNullOrEmpty(result.RunDate))
                    {
                        string dayOfBuild = DateTime.Parse(result.RunDate).ToString("yyyy-MM-dd");

                        dateOfBuild = DateTime.Parse(dayOfBuild);
                        if (!newScenarioCountsByDay.ContainsKey(dateOfBuild))
                        {
                            newScenarioCountsByDay.Add(dateOfBuild, newScenarios);
                        }
                        else
                        {
                            newScenarioCountsByDay[dateOfBuild] += newScenarios;
                        }
                    }

                }
            }

            return newScenarioCountsByDay;
        }

        private Dictionary<string, int[]> GetScenarioCountByWeek(Dictionary<DateTime, int[]> scenarioTotalsByDay)
        {
            int weekOfTheYear;
            string weekOfYearString = "";
            CultureInfo ci = new CultureInfo("en-US");
            Calendar cal = ci.Calendar;
            CalendarWeekRule cwr = ci.DateTimeFormat.CalendarWeekRule;
            DayOfWeek fdow = ci.DateTimeFormat.FirstDayOfWeek;

            Dictionary<string, int[]> scenariosByWeek = new Dictionary<string, int[]>();

            foreach (DateTime testDay in scenarioTotalsByDay.Keys)
            {
                weekOfTheYear = cal.GetWeekOfYear(testDay, cwr, fdow);
                weekOfYearString = testDay.ToString("yyyy-") + weekOfTheYear;

                if (!scenariosByWeek.ContainsKey(weekOfYearString))
                {
                    scenariosByWeek.Add(weekOfYearString, new int[] { scenarioTotalsByDay[testDay][0], scenarioTotalsByDay[testDay][1] });
                }
                else
                {
                    scenariosByWeek[weekOfYearString][0] += scenarioTotalsByDay[testDay][0];
                    scenariosByWeek[weekOfYearString][1] += scenarioTotalsByDay[testDay][1];
                }
            }

            return scenariosByWeek;
        }

        private Dictionary<string, int> GetNewScenarioCountByWeek(Dictionary<DateTime, int> newScenarioTotalsByDay)
        {
            int weekOfTheYear;
            string weekOfYearString = "";
            CultureInfo ci = new CultureInfo("en-US");
            Calendar cal = ci.Calendar;
            CalendarWeekRule cwr = ci.DateTimeFormat.CalendarWeekRule;
            DayOfWeek fdow = ci.DateTimeFormat.FirstDayOfWeek;

            Dictionary<string, int> newScenariosByWeek = new Dictionary<string, int>();

            foreach (DateTime testDay in newScenarioTotalsByDay.Keys)
            {
                weekOfTheYear = cal.GetWeekOfYear(testDay, cwr, fdow);
                weekOfYearString = testDay.ToString("yyyy-") + weekOfTheYear;

                if (!newScenariosByWeek.ContainsKey(weekOfYearString))
                {
                    newScenariosByWeek.Add(weekOfYearString, newScenarioTotalsByDay[testDay]);
                }
                else
                {
                    newScenariosByWeek[weekOfYearString] += newScenarioTotalsByDay[testDay];
                }
            }

            return newScenariosByWeek;
        }

        private void CreateNewScenarioCountByWeekCSV(Dictionary<string, int> newScenarioCountByWeek)
        {
            string path = "C:\\Jenkins\\userContent\\newScenarioByWeekReport_" + DateTime.Now.ToString("yyyyMMdd") + ".csv";

            using (var w = new StreamWriter(path))
            {
                string line = "WEEK,NEW_SCENARIO_COUNT";
                w.WriteLine(line);
                w.Flush();

                foreach (string key in newScenarioCountByWeek.Keys)
                {
                    string first = key;
                    int second = newScenarioCountByWeek[key];
                    line = string.Format("{0},{1}", first, second);
                    w.WriteLine(line);
                    w.Flush();
                }
            }
        }
    }
}
