using Lightwell_Testing_Dashboard_2.Workers;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lightwell_Testing_Dashboard_Tests
{
    [TestClass]
    public class APiWorker_Tests
    {
        [TestMethod]
        public void IsJenkiinsIdle()
        {
            bool jenkinsIdle = ApiWorker.JenkinsIdle;
            Assert.IsTrue(jenkinsIdle);
        }
    }
}
