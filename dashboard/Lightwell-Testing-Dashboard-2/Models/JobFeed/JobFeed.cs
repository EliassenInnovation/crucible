using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Models.JobFeed
{
    public class JobFeed
    {
        private Dictionary<string, JobData> _jobs;
        public Dictionary<string,JobData> Jobs 
        { 
            get
            {
                if(_jobs == null)
                {
                    _jobs = new Dictionary<string, JobData>();
                }
                return _jobs;
            }
        }

        public List<JobFeedEntry> BuildsThatJustBroke 
        { 
            get
            {
                List<JobFeedEntry> buildsThatJustBroke = new List<JobFeedEntry>();
                foreach(string key in Jobs.Keys)
                {
                    if(Jobs[key].JustBroken)
                    {
                        buildsThatJustBroke.Add(Jobs[key].Latest);
                    }
                }
                return buildsThatJustBroke;
            }
        }

        public List<JobFeedEntry> BuildsBrokenForAnExtendedPeriod
        {
            get
            {
                List<JobFeedEntry> buildsBrokenForAnExtendedPeriod = new List<JobFeedEntry>();
                foreach (string key in Jobs.Keys)
                {
                    if (Jobs[key].BrokenForAnExtendedPeriod)
                    {
                        buildsBrokenForAnExtendedPeriod.Add(Jobs[key].Latest);
                    }
                }
                return buildsBrokenForAnExtendedPeriod;
            }
        }

        public List<JobFeedEntry> BuildsThatWereJustFixed
        {
            get
            {
                List<JobFeedEntry> buildsThatWereJustFixed = new List<JobFeedEntry>();
                foreach (string key in Jobs.Keys)
                {
                    if (Jobs[key].JustFixed)
                    {
                        buildsThatWereJustFixed.Add(Jobs[key].Latest);
                    }
                }
                return buildsThatWereJustFixed;
            }
        }

        public JobFeed() { }
        
        public JobFeed(List<JobFeedEntry> entries)
        {
            foreach(JobFeedEntry entry in entries)
            {
                if(!Jobs.ContainsKey(entry.Name))
                {
                    Jobs.Add(entry.Name, new JobData());
                }
                Jobs[entry.Name].Add(entry.BuildNumber, entry);
            }
        }
    }
}
