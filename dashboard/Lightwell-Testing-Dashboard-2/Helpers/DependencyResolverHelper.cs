using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Helpers
{
    /// <summary>
    /// Run from DependencyHelper
    /// </summary>
    public class DependencyResolverHelper
    {
        private readonly IWebHost _webHost;

        /// <summary>
        /// <inheritdoc />
        /// </summary>
        /// <param name="WebHost"></param>
        public DependencyResolverHelper(IWebHost WebHost) => _webHost = WebHost;

        public IServiceScope ServiceScope 
        { 
            get
            {
                IServiceScope serviceScope = _webHost.Services.CreateScope();
                return serviceScope;
            }
        }

        /// <summary>
        /// Does the work
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <returns></returns>
        public T GetService<T>()
        {
            using var serviceScope = ServiceScope;
            var services = serviceScope.ServiceProvider;
            try
            {
                var scopedService = services.GetRequiredService<T>();
                return scopedService;
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                throw;
            }
        }
    }
}
