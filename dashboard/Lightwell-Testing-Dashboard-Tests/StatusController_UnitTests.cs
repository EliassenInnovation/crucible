using Lightwell_Testing_Dashboard_2.Controllers;
using Lightwell_Testing_Dashboard_2.Helpers;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_Tests
{
    [TestClass]
    public class StatusController_UnitTests : DependencyHarness
    {
        StatusController statusController;

        [TestInitialize]
        public void Init()
        {
            statusController = ServiceProvider.GetService<StatusController>();
        }

        [TestMethod]
        public void GetNewFailures_ShouldGetResults()
        {
            var results = statusController.GetNewFailures();

            Assert.IsNotNull(results);
        }
    }
}
