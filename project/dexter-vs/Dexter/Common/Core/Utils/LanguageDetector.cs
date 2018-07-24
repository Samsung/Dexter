using EnvDTE;
using EnvDTE80;
using Microsoft.VisualStudio.Shell;
using System;

namespace Dexter.Common.Utils
{
    public static class LanguageDetector
    {
        public static bool IsCodeModelLanguageCSharp(string projectFullPath)
        {
            DTE2 dte2 = (DTE2)ServiceProvider.GlobalProvider.GetService(typeof(DTE));

            if (dte2.DTE.ActiveDocument.ProjectItem.ContainingProject.CodeModel.Language 
                == CodeModelLanguageConstants.vsCMLanguageCSharp)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
