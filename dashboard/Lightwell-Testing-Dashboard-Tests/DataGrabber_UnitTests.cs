using Lightwell_Testing_Dashboard_2.Models.JobFeed;
using Lightwell_Testing_Dashboard_2.Tools;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace Lightwell_Testing_Dashboard_Tests
{
    [TestClass]
    public class DataGrabber_UnitTests
    {
        [TestMethod]
        public void GetJobFeed_GetFailedRssFeed()
        {
            JobFeed jobFeed = DataGrabber.GetFailedJobFeed();

            Assert.IsTrue(jobFeed.Jobs.Count > 0);
        }

        //doesn't work when there are only folders at the top level
        //[TestMethod]
        //public void GetJobFeed_GetLatestRssFeed()
        //{
        //    JobFeed jobFeed = DataGrabber.GetLatestJobFeed();

        //    Assert.IsTrue(jobFeed.Jobs.Count > 0);
        //}

        [TestMethod]
        public void GetJobFeed_GetAllRssFeed()
        {
            JobFeed jobFeed = DataGrabber.GetAllJobFeed();

            Assert.IsTrue(jobFeed.Jobs.Count > 0);
        }

        [TestMethod]
        public void GetJobFeed_GetAllRssFeed_CreateJobFeed()
        {
            JobFeed jobFeed = DataGrabber.GetAllJobFeed();

            Assert.IsTrue(jobFeed.Jobs.Count > 0);
        }

        [TestMethod]
        public void GetJobFeed_GetAllRssFeed_CheckThatLatestBuildIsBeingSet()
        {
            List<JobFeedEntry> jobFeedEntries = DataGrabber.GetJobFeedEntries("rssAll");

            JobFeed jobFeed = new JobFeed(jobFeedEntries);

            string sampleJobName = jobFeedEntries[0].Name;

            Assert.AreEqual(sampleJobName,jobFeed.Jobs[sampleJobName].Name);
        }

        [TestMethod]
        public void GetJobFeed_GetAllRssFeed_CheckThatLatestBuildIsBeingSet_usingGetLatestMethod()
        {
            List<JobFeedEntry> jobFeedEntries = DataGrabber.GetJobFeedEntries("rssAll");

            JobFeed jobFeed = new JobFeed(jobFeedEntries);

            string sampleJobName = jobFeedEntries[0].Name;

            Assert.AreEqual(sampleJobName, jobFeed.Jobs[sampleJobName].Latest.Name);
        }

        [TestMethod]
        public void JobFeed_CheckBuildsThatWereJustFixed()
        {
            JobFeed jobFeed = DataGrabber.GetAllJobFeed();

            List<JobFeedEntry> fixedJobs = jobFeed.BuildsThatWereJustFixed;

            foreach (JobFeedEntry entry in fixedJobs)
            {
                Assert.IsTrue(entry.JustFixed);
            }
        }

        [TestMethod]
        public void JobFeed_CheckBuildsThatJustBroke()
        {
            JobFeed jobFeed = DataGrabber.GetAllJobFeed();

            List<JobFeedEntry> justBrokenJobs = jobFeed.BuildsThatJustBroke;

            foreach (JobFeedEntry entry in justBrokenJobs)
            {
                Assert.IsTrue(entry.JustBroken);
            }
        }

        [TestMethod]
        public void JobFeed_CheckBuildsThatHaveBeenBrokenForAnExtendedPeriod()
        {
            JobFeed jobFeed = DataGrabber.GetAllJobFeed();

            List<JobFeedEntry> buildsBrokenForAnExtendedPeriod = jobFeed.BuildsBrokenForAnExtendedPeriod;

            foreach (JobFeedEntry entry in buildsBrokenForAnExtendedPeriod)
            {
                Assert.IsTrue(entry.BrokenForAnExtendedPeriod);
            }
        }
    }
}
