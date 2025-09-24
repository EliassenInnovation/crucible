using Lightwell_Testing_Dashboard_2.Tools;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Workers.Reports
{
    public class ReportMakerBase
    {
        public const string REPORT_PAGE_TEMPLATE = "reportTemplate.html";
        public const string REPORT_CSS_SHEET = "reportTemplateCSS.css";
        public string REPORT_OUTPUT_DIRECTORY;

        public Dictionary<string, string> _labelDictionary;

        public Dictionary<string, string> LabelDictionary 
        { 
            get
            {
                return _labelDictionary;
            }
        }

        public ReportMakerBase(string[] args)
        {
        }

        public string StringDate
        {
            get
            {
                return DateTime.Now.ToString("yyyy_MM_dd_hh_mm");
            }
        }

        protected string ApplyToTemplate(string[] textToAdd, string templateName)
        {
            string combinedText = "";

            string template = FileTool.GetTemplateContents(REPORT_PAGE_TEMPLATE);

            combinedText = string.Format(template, textToAdd);

            return combinedText;
        }

        public string[] GetReportTemplateAdditions(string reportTable, string date)
        {
            string niceDate = DateTime.Now.ToString();

            string[] reportTemplateAdditions = new string[]
            {
                date,
                niceDate,
                reportTable
            };

            return reportTemplateAdditions;
        }

        protected ReportElement CreatePercentRow(Dictionary<string, int> summaryValues)
        {
            Dictionary<string, string> percentages = GetPercentageDictionary(summaryValues);

            ReportElement percentRow = new ReportElement(ReportElement.TROW, "percentRow");
            ReportElement td;
            
            td = new ReportElement(ReportElement.TD);
            td.Text = "Percentages";
            percentRow.Add(td);

            //Build Date
            td = new ReportElement(ReportElement.TD);
            td.Text = "--";
            percentRow.Add(td);

            //compare to LabelDictionary, this will make sure each column gets some sort of text
            foreach(string key in LabelDictionary.Keys)
            {
                td = new ReportElement(ReportElement.TD);
                if(percentages.ContainsKey(key))
                {
                    td.Text = percentages[key];
                }
                else
                {
                    td.Text = "--";
                }
                percentRow.Add(td);
            }

            return percentRow;
        }

        private Dictionary<string, string> GetPercentageDictionary(Dictionary<string, int> summaryValues)
        {
            Dictionary<string, string> percentages = new Dictionary<string, string>();
            string columnNameRoot;
            string PASSED = "passed", FAILED = "failed", TOTAL = "total", SKIPPED = "skipped", PENDING = "pending", UNDEFINED = "undefined";
            string[] columnTypes = new string[] { PASSED, FAILED, TOTAL, SKIPPED, PENDING, UNDEFINED };

            //double passedFeatures = Convert.ToDouble(summaryValues["passedFeatures"]) / Convert.ToDouble(summaryValues["totalFeatures"]);

            foreach(string key in summaryValues.Keys)
            {
                if(key.ToLower(CultureInfo.InvariantCulture).Contains(PASSED))
                {
                    columnNameRoot = key.Replace(PASSED, "");

                    foreach(string columnType in columnTypes)
                    {
                        if(summaryValues.ContainsKey(columnType + columnNameRoot))
                        {
                            percentages[columnType + columnNameRoot] =
                                GetPercentage(Convert.ToDouble(summaryValues[columnType + columnNameRoot]),
                                    summaryValues[TOTAL + columnNameRoot]);
                        }
                    }
                }
            }

            return percentages;
        }

        private string GetPercentage(double dividend, double divisor)
        {
            double percent = dividend / divisor;
            string percentAsString = string.Format("{0:P1}", percent);

            return percentAsString;
        }

        protected Dictionary<string, int> GetSummaryValues(Dictionary<string, Dictionary<string, string[]>> reports)
        {
            Dictionary<string, int> summaryValues = new Dictionary<string, int>();
            int index;

            foreach (string key in reports.Keys)
            {
                foreach (string subKey in reports[key].Keys)
                {
                    if (!subKey.Equals("buildNumbers") && !subKey.Equals("durations"))
                    {
                        if (!summaryValues.ContainsKey(subKey))
                        {
                            summaryValues.Add(subKey, 0);
                        }

                        //set the last index in the data array
                        index = reports[key][subKey].Length - 1;
                        summaryValues[subKey] += int.Parse(reports[key][subKey][index]);
                    }
                }
            }

            return summaryValues;
        }

        protected ReportElement CreateSummaryRow(Dictionary<string, int> summaryValues)
        {
            ReportElement summaryRow = new ReportElement(ReportElement.TROW, "summaryRow");
            ReportElement td;

            td = new ReportElement(ReportElement.TD);
            td.Text = "Summary";
            summaryRow.Add(td);

            //Build Date
            td = new ReportElement(ReportElement.TD);
            td.Text = "--";
            summaryRow.Add(td);

            //Build Number
            td = new ReportElement(ReportElement.TD);
            td.Text = "--";
            summaryRow.Add(td);

            foreach (string key in summaryValues.Keys)
            {
                td = new ReportElement(ReportElement.TD);
                td.Text = summaryValues[key].ToString();
                summaryRow.Add(td);
            }

            //Duration
            td = new ReportElement(ReportElement.TD);
            td.Text = "--";
            summaryRow.Add(td);

            return summaryRow;
        }

        protected ReportElement CreateHeaderRow(Dictionary<string, string[]> report)
        {
            ReportElement headerRow = new ReportElement(ReportElement.TROW);
            ReportElement th;

            //create a th for Job Name
            th = new ReportElement(ReportElement.TH);
            th.Text = "Job Name";
            headerRow.Add(th);

            //create a th for the Build Date
            th = new ReportElement(ReportElement.TH);
            th.Text = "Build Date";
            headerRow.Add(th);

            foreach (string key in report.Keys)
            {
                th = new ReportElement(ReportElement.TH);
                th.Text = LabelDictionary[key].Replace(" ", @"<br/>");
                headerRow.Add(th);
            }

            return headerRow;
        }

        protected ReportElement CreateHeaderRow(Dictionary<string, string> report)
        {
            ReportElement headerRow = new ReportElement(ReportElement.TROW);
            ReportElement th;

            foreach (string key in report.Keys)
            {
                th = new ReportElement(ReportElement.TH);
                th.Text = LabelDictionary[key].Replace(" ", @"<br/>");
                headerRow.Add(th);
            }

            return headerRow;
        }

        public ReportElement CreateReportRow(Dictionary<string,string[]> report, string jobName)
        {
            ReportElement reportRow = new ReportElement(ReportElement.TROW);
            ReportElement td;

            int index = report[report.Keys.FirstOrDefault()].Length - 1;

            //add Job Name in the first column
            td = new ReportElement(ReportElement.TD);
            td.Text = jobName;
            reportRow.Add(td);

            //add Build Date in the first column
            td = new ReportElement(ReportElement.TD);
            int buildNumber = int.Parse(report["buildNumbers"][report["buildNumbers"].Length - 1]);
            td.Text = DataGrabber.GetBuildRunDate(jobName,buildNumber);
            reportRow.Add(td);

            foreach(string key in report.Keys)
            {
                td = new ReportElement(ReportElement.TD);
                if(key.Equals("durations"))
                {
                    double durationInNanoseconds = double.Parse(report[key][report[key].Length - 1]);
                    td.Text = TimeTools.ConvertJenkinsDurationToHoursAndMinutes(durationInNanoseconds);
                }
                else
                {
                    td.Text = report[key][index];
                }

                reportRow.Add(td);
            }

            return reportRow;
        }

        public ReportElement CreateReportRow(Dictionary<string,string> cellContents, Dictionary<string,string> labelDictionary,
            bool sectionStart, string sectionStartKey)
        {
            ReportElement reportRow = new ReportElement(ReportElement.TROW);
            ReportElement td;

            foreach(string key in labelDictionary.Keys)
            {
                td = new ReportElement(ReportElement.TD);
                //add Teamproject in first column
                if(sectionStart && key.Equals(sectionStartKey))
                {
                    td.Text = cellContents[key];
                }
                else if (!sectionStart && key.Equals(sectionStartKey))
                {
                    td.Text = "";
                }
                else
                {
                    td.Text = cellContents[key];
                }
                reportRow.Add(td);
            }

            return reportRow;
        }

        public bool OutputReport(string reportTable, string reportFileNamePrefix)
        {
            string date = StringDate;
            string[] reportTemplateAdditions = GetReportTemplateAdditions(reportTable, date);

            string reportPage = ApplyToTemplate(reportTemplateAdditions, REPORT_PAGE_TEMPLATE);

            string reportFileName = reportFileNamePrefix + date;

            bool success = FileTool.OutputTextFile(reportPage, reportFileName, REPORT_OUTPUT_DIRECTORY, ".html");

            string cssSheet = FileTool.GetTextFileContents(REPORT_CSS_SHEET, ".\\Templates\\");

            success = FileTool.OutputTextFile(cssSheet, REPORT_CSS_SHEET, REPORT_OUTPUT_DIRECTORY);

            return success;
        }
    }
}
