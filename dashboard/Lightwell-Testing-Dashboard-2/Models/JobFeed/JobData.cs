using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Models.JobFeed
{
    public class JobData : Dictionary<int,JobFeedEntry>
    {
        public const int LATEST = 0;

        public int LatestBuildNumber
        {
            get
            {
                return this[LATEST].BuildNumber;
            }
        }
        public int LastSuccessfulBuildNumber
        {
            get
            {
                return this[LATEST].LastSuccessfulBuildNumber;
            }
        }

        public bool IsBroken
        {
            get
            {
                return this[LATEST].IsBroken;
            }
        }

        public bool BrokenForAnExtendedPeriod 
        { 
            get
            {
                return this[LATEST].BrokenForAnExtendedPeriod;
            }
        }
        public bool JustBroken
        {
            get
            {
                return this[LATEST].JustBroken;
            }
        }
        public bool JustFixed
        {
            get
            {
                return this[LATEST].JustFixed;
            }
        }

        public string Name
        {
            get
            {
                return this[LATEST].Name;
            }
        }
        public string Link
        {
            get
            {
                Regex regex = new Regex(@"\d*\/$");
                return regex.Replace(this[LATEST].Link,"");
            }
        }
        public string Message
        {
            get
            {
                return this[LATEST].Message;
            }
        }

        public JobFeedEntry Latest 
        { 
            get
            {
                return this[LATEST];
            }
        }

        public new void Add(int key, JobFeedEntry value)
        {
            base.Add(key, value);
            if(!this.ContainsKey(LATEST))
            {
                base.Add(LATEST, value);
            }

            if(LatestBuildNumber < key)
            {
                this.Remove(LATEST);
                base.Add(LATEST, value);
            }
        }

        public JobFeedEntry GetBuildNumber(int key)
        {
            return this[key];
        }
    }
}
