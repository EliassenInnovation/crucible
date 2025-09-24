using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Helpers
{
    public class DependencyHarness
    {
        private DependencyResolverHelper _serviceProvider;

        public DependencyResolverHelper ServiceProvider
        {
            get
            {
                if(_serviceProvider == null)
                {
                    var webHost = WebHost.CreateDefaultBuilder()
                        .UseStartup<Startup>()
                        .Build();
                    _serviceProvider = new DependencyResolverHelper(webHost);
                }
                return _serviceProvider;
            }
        }
    }
}
