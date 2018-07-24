using EnvDTE;
using EnvDTE80;
using System;

namespace Dexter.Common.Utils
{
    public static class LanguageDetector
    {
        public static bool IsCodeModelLanguageCSharp(string projectFullPath)
        {
            DTE2 dte2 = GetDTE2Object();

            if (dte2.DTE.ActiveDocument.ProjectItem.ContainingProject
                .CodeModel.Language == CodeModelLanguageConstants.vsCMLanguageCSharp)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// DTE object for Visual Studio 2015 and Visual Studio 2017 or null otherwise
        /// </summary>
        /// <returns>DTE object or null</returns>
        private static DTE2 GetDTE2Object()
        {
            DTE2 dte2 = null;
            try
            {
                // Visual Studio 2017
                dte2 = (DTE2)System.Runtime.InteropServices.Marshal.GetActiveObject("VisualStudio.DTE.15.0");
            }
            catch (Exception e)
            {

            }

            try
            {
                // Visual Studio 2015
                dte2 = (DTE2)System.Runtime.InteropServices.Marshal.GetActiveObject("VisualStudio.DTE.14.0");
            }
            catch (Exception e)
            {

            }

            return dte2;
        }
    }
}
