using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Workers
{
    public static class TaskWorker
    {
        public static async Task<bool> CompleteTasks<T>(List<Task<T>> tasks)
        {
            if (tasks == null || tasks.Count == 0)
            {
                return false;
            }

            try
            {
                // Wait for all tasks to complete
                await Task.WhenAll(tasks);
                return true;
            }
            catch
            {
                // Handle exceptions if any of the tasks fail
                return false;
            }
        }
    }
}
