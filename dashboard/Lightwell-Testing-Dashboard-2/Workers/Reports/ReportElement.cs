using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Workers.Reports
{
    public class ReportElement
    {
        public const string ID = "id";
        public const string DIV = "div";
        public const string TABLE = "table";
        public const string THEAD = "thead";
        public const string TBODY = "tbody";
        public const string TROW = "tr";
        public const string TD = "td";
        public const string TFOOT = "tffot";
        public const string TH = "th";
        public const string END_OF_ROW = "\r\n";
        public const string TAB = "\t";
        public const string TEXT = "text";

        private List<string> selfClosingTagList = new List<string>() { "br" };
        private List<string> noClosingTagList = new List<string>() { "doctype" };

        private bool selfClosingTag = false;
        private bool noClosingTag = false;

        private string _tagName;
        private Dictionary<string, string> _attributes;
        private List<ReportElement> _children;

        #region Constructors
        public ReportElement(string tagName, string _id)
        {
            TagName = tagName;
            noClosingTagList.AddRange(selfClosingTagList);
            Attributes.Add(ID, _id);
        }

        public ReportElement(string tagName)
        {
            TagName = tagName;
            noClosingTagList.AddRange(selfClosingTagList);
        }

        public ReportElement(string tagName, Dictionary<string,string> attributeDict)
        {
            TagName = tagName;
            noClosingTagList.AddRange(selfClosingTagList);
            AddAttributes(attributeDict);
        }
        #endregion

        #region Properties
        public Dictionary<string,string> Attributes 
        {
            get 
            {
                if(_attributes == null)
                {
                    _attributes = new Dictionary<string, string>();
                }
                return _attributes;

            }
        }

        public List<ReportElement> Children 
        {
            get 
            {
                if(_children == null)
                {
                    _children = new List<ReportElement>();
                }
                return _children;
            } 
        }

        public bool  HasChildren 
        {
            get 
            {
                return Children.Count > 0;
            } 
        }

        public string ClosingTag    
        {
            get
            {
                return "</" + TagName + ">";
            }
        }

        public string OpeningTag 
        { 
            get
            {
                string openingTag = "";
                if(selfClosingTag)
                {
                    openingTag = "<" + TagName + "/>";
                }
                openingTag = "<" + TagName;

                foreach(string key in Attributes.Keys)
                {
                    openingTag += " " + key + "='" + Attributes[key] + "'";
                }
                openingTag += ">";

                return openingTag;
            }
        }

        public string  TagName 
        { 
            get
            {
                return _tagName;
            }
            set
            {
                if (selfClosingTagList.Contains(value.ToLower(CultureInfo.InvariantCulture)))
                {
                    selfClosingTag = true;
                }

                if(noClosingTagList.Contains(value.ToLower(CultureInfo.InvariantCulture)))
                {
                    noClosingTag = true;
                }
                _tagName = value;
            }
        }

        public string Text 
        { 
            get
            {
                return Attributes[TEXT];
            }
            set
            {
                Attributes.Add(TEXT, value);
            }
        }
        #endregion

        public void AddAttributes(Dictionary<string,string> attributeDict)
        {
            foreach(string key in attributeDict.Keys)
            {
                Attributes.Add(key, attributeDict[key]);
            }
        }

        public void Add(ReportElement child)
        {
            if(!selfClosingTag && !noClosingTag)
            {
                Children.Add(child);
            }
        }

        public string Print(int tabCount = 0)
        {
            StringBuilder htmlText = new StringBuilder();

            htmlText.Append(OpeningTag);

            if(HasChildren || Attributes.ContainsKey(Text))
            {
                htmlText.Append(END_OF_ROW);
                tabCount++;

                if(Attributes.ContainsKey(Text))
                {
                    htmlText.Append(Text);
                }

                foreach(ReportElement child in Children)
                {
                    htmlText = AddTabs(htmlText, tabCount);
                    htmlText.Append(child.Print(tabCount));
                    htmlText.Append(END_OF_ROW);
                }
                tabCount--;
            }

            //should close everything except DOCTYPE, but some may be self closing (ie <br/>)
            if(!noClosingTag && !selfClosingTag)
            {
                if(HasChildren)
                {
                    htmlText = AddTabs(htmlText, tabCount);
                }

                htmlText.Append(ClosingTag);
            }

            return string.Format("{0}", htmlText.ToString());
        }

        public StringBuilder AddTabs(StringBuilder htmlText, int tabCount)
        {
            for(int x = 0; x < tabCount; x++)
            {
                htmlText.Append(TAB);
            }

            return htmlText;
        }
    }
}
