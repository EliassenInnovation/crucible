using System;
using System.Collections.Generic;

namespace Lightwell_Testing_Dashboard_2.Models
{
    public class BuildResultsCollection
    {
        public int TotalTests { get; private set; }
        public int Failed { get; private set; }
        public int Passed { get; private set; }
        public List<BuildResult> BuildResults { get; private set; }

        private Dictionary<string, Totals> _totalsByParent;
        public Dictionary<string, Totals> TotalsByParent
        {
            get
            {
                if (_totalsByParent == null)
                {
                    _totalsByParent = new Dictionary<string, Totals>();
                }
                return _totalsByParent;
            }

        }

        private Dictionary<string, List<BuildResult>> _buildResultsByParent;
        public Dictionary<string, List<BuildResult>> BuildResultsByParent
        {
            get
            {
                if (_buildResultsByParent == null)
                {
                    _buildResultsByParent = new Dictionary<string, List<BuildResult>>();
                }

                return _buildResultsByParent;
            }
        }

        public BuildResultsCollection(List<BuildResult> buildResults)
        {
            BuildResults = buildResults;

            foreach (var buildResult in buildResults)
            {
                if (!TotalsByParent.ContainsKey(buildResult.Parent))
                {
                    TotalsByParent.Add(buildResult.Parent, new Totals());
                }
                if (buildResult.TotalTests > 0)
                {
                    TotalTests += buildResult.TotalTests;
                    Failed += buildResult.FailedTests;
                    Passed += buildResult.PassedTests;
                    TotalsByParent[buildResult.Parent].Successes += (buildResult.TotalTests - buildResult.FailedTests);
                    TotalsByParent[buildResult.Parent].Fails += buildResult.FailedTests;
                    TotalsByParent[buildResult.Parent].Total += buildResult.TotalTests;
                }

                AddToGrandParentTotals(buildResult.Parent, buildResult);

                AddToBuildResultsByParent(buildResult);
            }
        }

        private void AddToBuildResultsByParent(BuildResult buildResult)
        {
            if(!BuildResultsByParent.ContainsKey(buildResult.Parent))
            {
                BuildResultsByParent.Add(buildResult.Parent, new List<BuildResult>());
            }

            BuildResultsByParent[buildResult.Parent].Add(buildResult);
        }

        private void AddToGrandParentTotals(string name, BuildResult buildResult)
        {
            if (name.Contains("."))
            {
                string grandParentName = TrimFromLastDot(name);

                if (!TotalsByParent.ContainsKey(grandParentName))
                {
                    TotalsByParent.Add(grandParentName, new Totals());
                }
                if (buildResult.TotalTests > 0)
                {
                    TotalsByParent[grandParentName].Successes += (buildResult.TotalTests - buildResult.FailedTests);
                    TotalsByParent[grandParentName].Fails += buildResult.FailedTests;
                    TotalsByParent[grandParentName].Total += buildResult.TotalTests;
                }

                AddToGrandParentTotals(grandParentName, buildResult);
            }
        }

        private string TrimFromLastDot(string input)
        {
            if (string.IsNullOrEmpty(input))
                return input;

            int lastDotIndex = input.LastIndexOf('.');
            if (lastDotIndex == -1)
                return input; // No dot found, return the original string

            return input.Substring(0, lastDotIndex);
        }
    }
}
