using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Pages
{
    public class SigninModel : PageModel
    {
        public IEnumerable<AuthenticationScheme> Schemes { get; set; }

        [BindProperty(SupportsGet = true)]
        public string ReturnUrl { get; set; }

        public async Task<IActionResult> OnGet()
        {
            Schemes = await GetExternalProvidersAsync(HttpContext);
            if (Schemes.Count(s => s.DisplayName != null) == 1)
            {
                string provider = Schemes.First(s => s.DisplayName != null).DisplayName;
                return await DoAuthentication(provider);
            }

            return null;
        }

        public async Task<IActionResult> DoAuthentication(string provider)
        {
            if (string.IsNullOrWhiteSpace(provider))
            {
                return BadRequest();
            }

            return await IsProviderSupportedAsync(HttpContext, provider) is false
                ? BadRequest()
                : Challenge(new AuthenticationProperties
                {
                    RedirectUri = Url.IsLocalUrl(ReturnUrl) ? ReturnUrl : "/"
                }, provider);
        }

        public async Task<IActionResult> OnPost([FromForm] string provider)
        {
            return await DoAuthentication(provider);
        }

        public static async Task<AuthenticationScheme[]> GetExternalProvidersAsync(HttpContext context)
        {
            var schemes = context.RequestServices.GetRequiredService<IAuthenticationSchemeProvider>();
            return (await schemes.GetAllSchemesAsync())
                .Where(scheme => !string.IsNullOrEmpty(scheme.DisplayName))
                .ToArray();
        }

        private static async Task<bool> IsProviderSupportedAsync(HttpContext context, string provider) =>
            (await GetExternalProvidersAsync(context))
            .Any(scheme => string.Equals(scheme.Name, provider, StringComparison.OrdinalIgnoreCase));
    }
}
