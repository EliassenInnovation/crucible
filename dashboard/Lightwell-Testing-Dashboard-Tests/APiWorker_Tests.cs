using System.Collections.Generic;
using System.Linq;
using Lightwell_Testing_Dashboard_2.Models.Queue;
using Lightwell_Testing_Dashboard_2.Workers;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

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

        [TestMethod]
        public void ICanGetTheQueue()
        {
            JObject queueJson = ApiWorker.GetJsonFromApi(ApiWorker.QUEUE_API);
            Assert.IsNotNull(queueJson);
        }

        [TestMethod]
        public void ICanGetTheQueueAndHydrateJenkinsQueueObject()
        {
            JObject queueJson = ApiWorker.GetJsonFromApi(ApiWorker.QUEUE_API);
            JenkinsQueue jenkinsQueue = JsonConvert.DeserializeObject<JenkinsQueue>(queueJson.ToString());
            Assert.IsNotNull(jenkinsQueue);
        }

        [TestMethod]
        public void ICanVerifyAJobIsQueued()
        {
            JObject queueJson = ApiWorker.GetJsonFromApi(ApiWorker.QUEUE_API);
            JenkinsQueue jenkinsQueue = JsonConvert.DeserializeObject<JenkinsQueue>(queueJson.ToString());
            
            List<string> queuedJobNames = jenkinsQueue.Items.Select(i => i.Task.Name).ToList();

            string jobNameToFind = "Web_Service";
            Assert.IsTrue(queuedJobNames.Contains(jobNameToFind));
        }
    }
}
