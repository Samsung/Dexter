using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Runtime.InteropServices;
using System.Diagnostics;
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
        public IList<string> getAllSourceFilePaths(IVsHierarchy pHierarchy)
        {
            Stack<string> currentPathes = new Stack<string>();
            currentPathes.Push(GetProjectPath(pHierarchy));

            return TraverseSourceFiles(pHierarchy, VSConstants.VSITEMID_ROOT, currentPathes)
                .ToList();
        }

        private string GetProjectPath(IVsHierarchy pHierarchy)
        {
            object projectPath;
            pHierarchy.GetProperty(VSConstants.VSITEMID_ROOT, (int)__VSHPROPID.VSHPROPID_ProjectDir, out projectPath);
            return (string)projectPath;
        }

        private IEnumerable<string> TraverseSourceFiles(IVsHierarchy pHierarchy, uint itemid, Stack<string> currentPathes)
        {
            IntPtr nestedHierarchyObj;
            uint nestedItemId;
            var hierGuid = typeof(IVsHierarchy).GUID;

            PushItemNameToCurrentPathes(pHierarchy, itemid, currentPathes);

            var hr = pHierarchy.GetNestedHierarchy(itemid, ref hierGuid, out nestedHierarchyObj, out nestedItemId);
            if (VSConstants.S_OK == hr && IntPtr.Zero != nestedHierarchyObj)
            {
                var nestedHierarchy = Marshal.GetObjectForIUnknown(nestedHierarchyObj) as IVsHierarchy;
                Marshal.Release(nestedHierarchyObj);
                if (nestedHierarchy != null)
                {
                    foreach (var filePath in TraverseSourceFiles(nestedHierarchy, nestedItemId, currentPathes))
                    {
                        yield return filePath;
                    }
                }
            }
            else
            {
                object pVar;
                hr = pHierarchy.GetProperty(itemid,
                                           (int)__VSHPROPID.VSHPROPID_FirstVisibleChild,
                                           out pVar);
                if (VSConstants.S_OK == hr)
                {
                    var childId = GetItemId(pVar);
                    if (childId == VSConstants.VSITEMID_NIL)
                    {
                        var itemName = GetItemName(pHierarchy, itemid);
                        if (IsSourceFileName(itemName))
                        {
                            yield return GetCurrentFullPath(currentPathes);
                        }
                    }
                    else
                    {
                        while (childId != VSConstants.VSITEMID_NIL)
                        {
                            foreach (var filePath in TraverseSourceFiles(pHierarchy, childId, currentPathes))
                            {
                                yield return filePath;
                            }

                            hr = pHierarchy.GetProperty(childId,
                                                        (int)__VSHPROPID.VSHPROPID_NextVisibleSibling,
                                                        out pVar);

                            if (VSConstants.S_OK != hr)
                            {
                                break;
                            }
                            childId = GetItemId(pVar);
                        }
                    }
                }
            }
            currentPathes.Pop();
        }

        private string GetCurrentFullPath(Stack<string> currentPathes)
        {
            return String.Join(Path.DirectorySeparatorChar.ToString(), currentPathes.Reverse().ToArray());
        }

        private void PushItemNameToCurrentPathes(IVsHierarchy pHierarchy, uint itemid, Stack<string> currentPathes)
        {
            if (itemid == VSConstants.VSITEMID_ROOT)
            {
                return;
            }

            currentPathes.Push(GetItemName(pHierarchy, itemid));
        }

        private bool IsSourceFileName(string fileName)
        {
            if (fileName != null && fileName.EndsWith(".cs"))
                return true;

            return false;
        }

        private string GetItemName(IVsHierarchy pHierarchy, uint itemid)
        {
            object filePath;
            pHierarchy.GetProperty(itemid, (int)__VSHPROPID.VSHPROPID_Name, out filePath);
            return (string)filePath;
        }

        private string GetFilePath(IVsHierarchy pHierarchy, uint itemid)
        {
            object filePath;
            pHierarchy.GetProperty(itemid, (int)__VSHPROPID.VSHPROPID_SaveName, out filePath);
            return (string)filePath;
        }

        private void LogItem(IVsHierarchy pHierarchy, uint itemid)
        {
            object str;

            Debug.WriteLine("================================================");

            pHierarchy.GetProperty(itemid, (int)__VSHPROPID.VSHPROPID_Name, out str);
            Debug.WriteLine("Name: " + (string)str);

            pHierarchy.GetProperty(itemid, (int)__VSHPROPID.VSHPROPID_SaveName, out str);
            Debug.WriteLine("SaveName: " + (string)str);

            pHierarchy.GetProperty(itemid, (int)__VSHPROPID.VSHPROPID_Caption, out str);
            Debug.WriteLine("Caption: " + (string)str);

            pHierarchy.GetProperty(itemid, (int)__VSHPROPID.VSHPROPID_TypeName, out str);
            Debug.WriteLine("TypeName: " + (string)str);

            Debug.WriteLine("================================================");
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
