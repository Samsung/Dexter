using System;
using System.Collections.Generic;
using System.IO;

namespace dexter_vs.Analysis
{
    /// <summary>
    /// Adapter for Dexter application
    /// </summary>
    public class Dexter
    {
        public Dexter()
        {
        }
        
        /// <summary>
        /// Performs analysis of files in given path
        /// </summary>
        /// <param name="path">path to analysed directory</param>
        /// <returns>List of found defects</returns>
        public List<Defect> Analyse(string path = "")
        {
            throw new NotImplementedException();
        }
    }
}