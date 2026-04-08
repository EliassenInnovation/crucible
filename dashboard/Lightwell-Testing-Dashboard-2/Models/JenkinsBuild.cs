using Lightwell_Testing_Dashboard_2.Workers;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;

namespace Lightwell_Testing_Dashboard_2.Models
{
    public class JenkinsBuild
    {
        public const string _CLASS = "_class";
        public const string NAME = "name";
        public const string URL = "url";
        public const string COLOR = "color";
        public const string BUILDING_INDICATOR = "_anime";
        public const string PROJECT = "Project";
        public const string FOLDER = "Folder";
        public const string MAIN = "main";
        public const string TEST_RESULT_ACTION_CLASS = "hudson.tasks.junit.TestResultAction";

        private readonly List<string> _buildTypes = new List<string>(){ "hudson.model.FreeStyleProject", "org.jenkinsci.plugins.workflow.job.WorkflowJob", "org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" };
        public List<string> BUILD_TYPES 
        { 
            get
            {
                return _buildTypes;
            }
        }

        public const string FOLDER_TYPE = "com.cloudbees.hudson.plugins.folder.Folder";

        [JsonProperty(_CLASS)]
        public string Class { get; set; }
        public string Name { get; set; }
        public string Url { get; set; }
        public string Color { get; set; }
        public string Description { get; set; }
        public int Number { get; set; }
        public JenkinsBuild FirstBuild { get; set; }
        public JenkinsBuild LastBuild { get; set; }
        public bool Buildable { get; set; }
        public BuildAction[] Actions { get; set; }
        public int NextBuildNumber { get; set; }
        public string Result { get; set; }
        public string Duration { get; set; }
        public string Timestamp { get; set; }
        public string FullName { get; set; }


        public bool IsFolder 
        { 
            get
            {
                return Class.Equals(FOLDER_TYPE);
            }
        }

        public bool IsBuild
        {
            get
            {
                return BUILD_TYPES.Contains(Class);
            }
        }

        public Boolean IsBuilding 
        {
            get
            {
                if (string.IsNullOrWhiteSpace(Color))
                {
                    return false;
                }
                else
                {
                    return Color.Contains(BUILDING_INDICATOR);
                }
            } 
        }


        public BuildAction TestResults
        {
            get
            {
                BuildAction testResults = null;
                testResults = Actions?.FirstOrDefault(action => !string.IsNullOrEmpty(action.Class) && action.Class.Equals(TEST_RESULT_ACTION_CLASS));

                return testResults;
            }
        }

        public string Parent
        {
            get
            {
                //String[] urlParts = Url.Split("/job/");
                //if (urlParts.Length == 2)
                //{
                //    return MAIN;
                //}
                //else
                //{
                //    return urlParts[1];
                //}
                if (FullName != null)
                {
                    String[] parentParts = FullName.Split("/");
                    if (parentParts.Length > 1)
                    {
                        return string.Join('.',parentParts.Take(parentParts.Length - 1));
                    }
                }
                return MAIN;
            }
        }

        public string JobPath
        {
            get
            {
                string path = Url.Replace(ApiWorker.Jenkins, "").Replace("job", "jobs").Replace("/","\\").ToLower();
                return path;                
            }
        }
    }
}
