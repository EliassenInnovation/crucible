using System;
using System.IO;
using System.Reflection;
using Lightwell_Testing_Dashboard_2.Controllers;
using Lightwell_Testing_Dashboard_2.Workers;
using Lightwell_Testing_Dashboard_2.Workers.Services;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.DataProtection;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

namespace Lightwell_Testing_Dashboard_2
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
            StaticConfig = configuration;
        }

        public IConfiguration Configuration { get; }
        public static IConfiguration StaticConfig { get; private set; }
        static string XmlCommentsFilePath
        {
            get
            {
                var basePath = AppContext.BaseDirectory;
                var fileName = typeof(Startup).GetTypeInfo().Assembly.GetName().Name + ".xml";
                return Path.Combine(basePath, fileName);
            }
        }

        public bool IsSecure
        {
            get
            {
                return !string.IsNullOrWhiteSpace(AuthorizationProviderName);
            }
        }

        public string AuthorizationProviderName 
        { 
            get
            {
                return Configuration["authorization:authProviderName"];
            }
        }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            if (IsSecure)
            {
                services.AddAuthentication(o =>
                    {
                        o.DefaultScheme = CookieAuthenticationDefaults.AuthenticationScheme;
                    })
                    .AddCookie(o =>
                    {
                        o.LoginPath = "/signin";
                        o.LogoutPath = "/signout";
                    })
                    .AddGitHub(o =>
                    {
                        o.ClientId = Configuration["authorization:" + AuthorizationProviderName + ":clientId"];
                        o.ClientSecret = Configuration["authorization:"+ AuthorizationProviderName +":clientSecret"];
                        o.CallbackPath = "/signin-github";
                        o.Scope.Add("read:user");
                    });
            }

            services.AddSingleton<TestResultWorker>();
            services.AddControllersWithViews();
            //services.AddHostedService<BuildTestResultsService>();
            //services.AddHostedService<GetJobFeedService>();
            services.AddHostedService<CombinedWorkerService>();

            // Register the Swagger generator, defining 1 or more Swagger documents
            services.AddSwaggerGen(options =>
            {
                options.IncludeXmlComments(XmlCommentsFilePath);
            });

            services.AddTransient<StatusController>();
            services.AddRazorPages();
            services.AddDataProtection()
                .PersistKeysToFileSystem(new DirectoryInfo(@"./jenkins"))
                .SetApplicationName("Dashboard");
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                app.UseExceptionHandler("/Home/Error");
                // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
                app.UseHsts();
            }
            //app.UseHttpsRedirection();
            app.UseStaticFiles();

            app.UseRouting();

            app.UseAuthentication();
            if (IsSecure)
            {
                app.UseAuthorization();
            }

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapRazorPages();

                endpoints.MapControllerRoute(
                    name: "default",
                    pattern: "{controller=Home}/{action=Index}/{id?}");

                endpoints.MapGet("/signout", async ctx =>
                {
                    await ctx.SignOutAsync(
                        CookieAuthenticationDefaults.AuthenticationScheme,
                        new AuthenticationProperties
                        {
                            RedirectUri = "/"
                        });
                });
                if (IsSecure)
                {
                    endpoints.MapControllers().RequireAuthorization();
                }
            });

            // Enable middleware to serve generated Swagger as a JSON endpoint.
            app.UseSwagger();

            // Enable middleware to serve swagger-ui (HTML, JS, CSS, etc.),
            // specifying the Swagger JSON endpoint.
            app.UseSwaggerUI(c =>
            {
                c.SwaggerEndpoint("./v1/swagger.json", "My API V1");
            });
        }
    }
}
