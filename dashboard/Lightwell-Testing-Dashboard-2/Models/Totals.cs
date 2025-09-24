using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Models
{
    public class Totals
    {
        public int Successes { get; set; }
        public int Fails { get; set; }
        public int Others { get; set; }
        public int Disabled { get; set; }
        public int Total { get; set; }
    }
}
