using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Tools
{
    public static class FileTool
    {
        public static bool OutputTextFile(string fileText, string fileName, string outputDirectory, string extension = "")
        {
            string filePath = outputDirectory + Path.DirectorySeparatorChar + fileName + extension;

            if(!Directory.Exists(outputDirectory))
            {
                Directory.CreateDirectory(outputDirectory);
            }

            File.WriteAllText(filePath, fileText);

            return File.Exists(filePath);
        }

        public static string GetTextFileContents(string fileName, string path)
        {
            string fullPath = path + fileName;

            string fileContents = File.ReadAllText(fullPath);

            return fileContents;
        }

        public static string GetTemplateContents(string fileName)
        {
            string templatePath = "." + Path.DirectorySeparatorChar + "Templates" + Path.DirectorySeparatorChar;
            return GetTextFileContents(fileName, templatePath);
        }

        public static bool DirectoryExists(string dirName)
        {
            bool subDirExists = Directory.Exists(dirName);
            return subDirExists;
        }
    }
}
