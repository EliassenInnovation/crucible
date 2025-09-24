using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Tools
{
    public class TimeTools
    {
        public static readonly DateTime EPOCH = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

        public static string ConvertJenkinsDurationToHoursAndMinutes(double duration)
        {
            TimeSpan t = TimeSpan.FromMilliseconds(duration);
            string convertedTime = t.Hours.ToString() + ":" + t.Minutes.ToString("00");
            return convertedTime;
        }

        public static string ConvertTimeStampInSecondsToDateTimeString(double timeStamp)
        {
            TimeSpan offset = DateTimeOffset.Now.Offset;
            double hours = offset.TotalHours;
            return EPOCH.AddMilliseconds(timeStamp).AddHours(hours).ToString();
        }

        public static string ConvertSecondsToHoursAndMinutes(double seconds)
        {
            TimeSpan t = TimeSpan.FromSeconds(seconds);
            return t.Hours.ToString() + ":" + t.Minutes.ToString("00");
        }
    }
}
