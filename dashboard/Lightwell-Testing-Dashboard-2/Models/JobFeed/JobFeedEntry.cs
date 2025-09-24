using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Xml;

namespace Lightwell_Testing_Dashboard_2.Models.JobFeed
{
    public class JobFeedEntry
    {
        public string Message { get; set; }
        public string Name { get; set; }
        public String Link { get; set; }

        public int BuildNumber { get; set; }
        public int LastSuccessfulBuildNumber { get; set; }

        public DateTime Published { get; set; }
        public DateTime Updated { get; set; }

        public bool IsBroken { get; set; }
        public bool JustBroken { get; set; }
        public bool JustFixed { get; set; }
        public bool BrokenForAnExtendedPeriod
        {
            get
            {
                return IsBroken && !JustBroken;
            }
        }

        public JobFeedEntry() { }

        public JobFeedEntry(XmlNode entry)
        {
            // Initialize variables
            string id = "", title = "";

            foreach (XmlNode node in entry.ChildNodes)
            {
                switch (node.Name)
                {
                    case "title":
                        title = node.InnerText;
                        ParseTitle(title);
                        break;
                    case "link":
                        Link = node.Attributes["href"]?.Value;
                        break;
                    case "id":
                        id = node.InnerText;
                        ParseId(id);
                        break;
                    case "published":
                        Published = ParseDateTime(node.InnerText);
                        break;
                    case "updated":
                        Updated = ParseDateTime(node.InnerText);
                        break;
                }
            }

            if (string.IsNullOrEmpty(Name))
            {
                ExtractNameAndBuildNumberFromTitle(title);
            }

            DetermineBuildState();
        }

        private void ParseTitle(string title)
        {
            Message = title.Split('(')[1].Replace(")", "");
            IsBroken = Message.Contains("broken", StringComparison.OrdinalIgnoreCase);
        }

        private void ParseId(string id)
        {
            string[] idParts = id.Split(':');
            if (idParts.Length == 4)
            {
                BuildNumber = int.TryParse(idParts[3], out var buildNum) ? buildNum : 0;
                Name = idParts[2];
            }
        }

        private DateTime ParseDateTime(string dateTimeString)
        {
            return DateTime.TryParse(dateTimeString, out var result) ? result : DateTime.MinValue;
        }

        private void ExtractNameAndBuildNumberFromTitle(string title)
        {
            const string buildNumberPattern = @"#\d+";
            Regex regEx = new Regex(buildNumberPattern);

            Match match = regEx.Match(title);
            if (match.Success)
            {
                BuildNumber = int.Parse(match.Value.Replace("#", ""));
                Name = title.Split('#')[0].Trim();
            }
        }

        private void DetermineBuildState()
        {
            if (Message.Contains("stable", StringComparison.OrdinalIgnoreCase))
            {
                LastSuccessfulBuildNumber = BuildNumber;
            }
            else if (Message.Contains("back to normal", StringComparison.OrdinalIgnoreCase))
            {
                LastSuccessfulBuildNumber = BuildNumber;
                JustFixed = true;
            }
            else if (Message.Contains("this build", StringComparison.OrdinalIgnoreCase))
            {
                if (Published > DateTime.Now.AddDays(-1))
                {
                    LastSuccessfulBuildNumber = BuildNumber - 1;
                    JustBroken = true;
                }
                else
                {
                    JustBroken = false;
                }
            }
            else if (Message.Contains("long time", StringComparison.OrdinalIgnoreCase))
            {
                LastSuccessfulBuildNumber = 0;
            }
            else
            {
                const string buildNumberPattern = @"#\d+";
                Regex regEx = new Regex(buildNumberPattern);

                Match match = regEx.Match(Message);
                if (match.Success)
                {
                    LastSuccessfulBuildNumber = int.Parse(match.Value.Replace("#", "")) - 1;
                }
            }
        }

    }
}
