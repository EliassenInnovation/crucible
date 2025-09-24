using Microsoft.Extensions.Logging.Abstractions;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Lightwell_Testing_Dashboard_2.Workers
{
    public class LogWorker
    {
        private static BlockingCollection<byte[]> messageQueue = new BlockingCollection<byte[]>();
        private static Thread loggingThread;

        public static string LogDir
        {
            get
            {
                string logDir = WorkerBase.JENKINS_HOME + Path.DirectorySeparatorChar + "logs" +
                    Path.DirectorySeparatorChar;

                if (!Directory.Exists(logDir))
                {
                    Directory.CreateDirectory(logDir);
                }

                return logDir;
            }
        }

        public static string TimeStamp
        {
            get
            {
                return DateTime.Now.ToString("yyyyMMddhh");
            }
        }

        public static string TimeStampString
        {
            get
            {
                return DateTime.Now.ToString("yyyy-MM-dd hh:mm:ss ");
            }
        }

        public static string FilePath
        {
            get
            {
                return LogDir + "log_" + TimeStamp + ".txt";
            }
        }

        public static void LogError(Exception e)
        {
            //CheckLogFileExists();

            //string timeStampString = TimeStampString;
            //using (StreamWriter sw = File.AppendText(FilePath))
            //{
            //    sw.WriteLine(timeStampString + e.Message);
            //    sw.WriteLine(timeStampString + e.StackTrace);
            //    sw.Close();
            //}

            string message = TimeStampString + e.Message + " |\n " + e.StackTrace;
            AddMessageToQueue(message);
        }

        private static void AddMessageToQueue(String messageString)
        {
            byte[] message = Encoding.ASCII.GetBytes(messageString);
            messageQueue.Add(message);
            if(loggingThread == null)
            {
                StartLoggingThread();
            }
        }

        public static void Log(string logEntry)
        {
            string message = TimeStampString + logEntry;
            AddMessageToQueue(message);

            //CheckLogFileExists();

            //string timeStampString = TimeStampString;
            //using (StreamWriter sw = File.AppendText(FilePath))
            //{
            //    sw.WriteLine(timeStampString + logEntry);
            //    sw.Close ();
            //}
        }

        private static void CheckLogFileExists()
        {
            if (!File.Exists(FilePath))
            {
                File.Create(FilePath).Close();
            }
        }

        private static void StartLoggingThread()
        {
            loggingThread = new Thread(ProcessIncomingMessages);
        }

        private static void ProcessIncomingMessages()
        {
            while (true)
            {
                //will block until thread1 Adds a message
                byte[] message = messageQueue.Take();
                CheckLogFileExists();

                string timeStampString = TimeStampString;
                using (StreamWriter sw = File.AppendText(FilePath))
                {
                    sw.WriteLine(Encoding.ASCII.GetString(message));
                    sw.Close();
                }
            }
        }
    }
}
