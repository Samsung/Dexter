using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Runtime.InteropServices;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio;

namespace Dexter.Common.Utils
{
    /// <summary>
    /// Provides util functions to traverse IVsHierarchy
    /// </summary>
    public interface IDexterHierarchyService
    {
        /// <summary>
        /// Gets all source file paths in a project
        /// </summary>
        /// <param name="pHierarchy">hierarchy of a project</param>
        /// <returns>Source file paths</returns>
        IList<string> getAllSourceFilePaths(IVsHierarchy pHierarchy);
    }

    public class DexterHierarchyService : IDexterHierarchyService
    {
        static IDexterHierarchyService instace;

        static public IDexterHierarchyService Instance
        {
            get
            {
                if (instace == null)
                {
                    throw new ArgumentNullException("instance is null");
                }
                return instace;
            }
            set
            {
                instace = value;
            }
        }

        public IList<string> getAllSourceFilePaths(IVsHierarchy pHierarchy)
        {
            string projectPath = getProjectPath(pHierarchy);
            return TraverseSourceFiles(pHierarchy, VSConstants.VSITEMID_ROOT)
                .Select(fileName => projectPath + Path.DirectorySeparatorChar + fileName).ToList();
        }

        private string getProjectPath(IVsHierarchy pHierarchy)
        {
            object projectPath;
            pHierarchy.GetProperty(VSConstants.VSITEMID_ROOT, (int)__VSHPROPID.VSHPROPID_ProjectDir, out projectPath);
            return (string)projectPath;
        }

        private IEnumerable<string> TraverseSourceFiles(IVsHierarchy pHierarchy, uint itemid)
        {
            IntPtr nestedHierarchyObj;
            uint nestedItemId;
            var hierGuid = typeof(IVsHierarchy).GUID;

            var hr = pHierarchy.GetNestedHierarchy(itemid, ref hierGuid, out nestedHierarchyObj, out nestedItemId);
            if (VSConstants.S_OK == hr && IntPtr.Zero != nestedHierarchyObj)
            {
                var nestedHierarchy = Marshal.GetObjectForIUnknown(nestedHierarchyObj) as IVsHierarchy;
                Marshal.Release(nestedHierarchyObj);
                if (nestedHierarchy != null)
                {
                    foreach (var filePath in TraverseSourceFiles(nestedHierarchy, nestedItemId))
                    {
                        yield return filePath;
                    }
                }
            }
            else
            {
                var currentFilePath = GetFilePath(pHierarchy, itemid);
                if (IsSourceFilePath(currentFilePath))
                    yield return currentFilePath;

                object pVar;
                hr = pHierarchy.GetProperty(itemid,
                                           (int)__VSHPROPID.VSHPROPID_FirstVisibleChild,
                                           out pVar);
                ErrorHandler.ThrowOnFailure(hr);
                if (VSConstants.S_OK == hr)
                {
                    var childId = GetItemId(pVar);
                    while (childId != VSConstants.VSITEMID_NIL)
                    {
                        foreach (var filePath in TraverseSourceFiles(pHierarchy, childId))
                        {
                            yield return filePath;
                        }

                        hr = pHierarchy.GetProperty(childId,
                                                   (int)__VSHPROPID.VSHPROPID_NextVisibleSibling,
                                                   out pVar);
                        if (VSConstants.S_OK == hr)
                        {
                            childId = GetItemId(pVar);
                        }
                        else
                        {
                            ErrorHandler.ThrowOnFailure(hr);
                            break;
                        }
                    }
                }
            }
        }

        private bool IsSourceFilePath(string filePath)
        {
            if (filePath != null && filePath.EndsWith(".cs"))
                return true;

            return false;
        }

        private string GetFilePath(IVsHierarchy pHierarchy, uint itemid)
        {
            object filePath;
            pHierarchy.GetProperty(itemid, (int)__VSHPROPID.VSHPROPID_SaveName, out filePath);
            return (string)filePath;
        }

        private static uint GetItemId(object pvar)
        {
            if (pvar == null) return VSConstants.VSITEMID_NIL;
            if (pvar is int) return (uint)(int)pvar;
            if (pvar is uint) return (uint)pvar;
            if (pvar is short) return (uint)(short)pvar;
            if (pvar is ushort) return (ushort)pvar;
            if (pvar is long) return (uint)(long)pvar;
            return VSConstants.VSITEMID_NIL;
        }
    }
}
