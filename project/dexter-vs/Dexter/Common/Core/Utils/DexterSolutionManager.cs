using Microsoft.VisualStudio.Shell.Interop;
using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Diagnostics;
using Microsoft.VisualStudio;

namespace Dexter.Common.Utils
{
    /// <summary>
    /// EventArgs for SourceFileChanged event
    /// </summary>
    public class SourceFileEventArgs : EventArgs
    {
        private bool isAdded;
        private IList<string> filePaths;

        /// <summary>
        /// Creates an instance of SourceFileEventArgs
        /// </summary>
        /// <param name="filePaths">Path list of changed files</param>
        /// <param name="isAdded">Whether files are added or not</param>
        public SourceFileEventArgs(IList<string> filePaths, bool isAdded)
        {
            this.filePaths = filePaths;
            this.isAdded = isAdded;
        }

        /// <summary>
        /// Path list of changed files
        /// </summary>
        public IList<string> FilePaths
        {
            get { return filePaths; }
        }

        /// <summary>
        /// Whether files are added or not
        /// </summary>
        public bool IsAdded
        {
            get { return isAdded; }
        }   
    }

    /// <summary>
    /// Provides interfaces for events of solution and project 
    /// </summary>
    public interface IDexterSolutionManager
    {
        /// <summary>
        /// Provides file list changed event
        /// </summary>
        event EventHandler<SourceFileEventArgs> SourceFilesChanged;
    }

    /// <summary>
    /// Manages VS soultions and packages information/events
    /// </summary>
    public class DexterSolutionManager : IVsSolutionEvents, IDexterSolutionManager
    {
        static DexterSolutionManager instace;
        IDexterHierarchyService hierarchyService;

        public event EventHandler<SourceFileEventArgs> SourceFilesChanged;

        public DexterSolutionManager(IDexterHierarchyService hierarchyService)
        {
            this.hierarchyService = hierarchyService;
        }

        static public DexterSolutionManager Instance
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

        public uint eventCookie { get; set; }


        public int OnAfterCloseSolution(object pUnkReserved)
        {
            Debug.WriteLine("OnAfterCloseSolution");
            return VSConstants.S_OK;
        }

        public int OnAfterLoadProject(IVsHierarchy pStubHierarchy, IVsHierarchy pRealHierarchy)
        {
            Debug.WriteLine("OnAfterLoadProject");
            return VSConstants.S_OK;
        }

        public int OnAfterOpenProject(IVsHierarchy pHierarchy, int fAdded)
        {
            Debug.WriteLine("OnAfterOpenProject");

            IList<string> sourceFilePaths = hierarchyService.getAllSourceFilePaths(pHierarchy);
            if (sourceFilePaths.Count > 0)
            {
                Debug.WriteLine("Start RaiseSourceFilesChanged : " + sourceFilePaths.Count);
                RaiseSourceFilesChanged(new SourceFileEventArgs(sourceFilePaths, true));
            }
            
            //uint cookie;
            //pHierarchy.AdviseHierarchyEvents(new DexterHierarchyEvents(), out cookie);

            return VSConstants.S_OK;
        }

        private void RaiseSourceFilesChanged(SourceFileEventArgs eventArgs)
        {
            SourceFilesChanged?.Invoke(this, eventArgs);
        }

        public int OnAfterOpenSolution(object pUnkReserved, int fNewSolution)
        {
            Debug.WriteLine("OnAfterOpenSolution");
            return VSConstants.S_OK;
        }

        public int OnBeforeCloseProject(IVsHierarchy pHierarchy, int fRemoved)
        {
            Debug.WriteLine("OnBeforeCloseProject");

            IList<string> sourceFilePaths = hierarchyService.getAllSourceFilePaths(pHierarchy);
            if (sourceFilePaths.Count > 0)
            {
                RaiseSourceFilesChanged(new SourceFileEventArgs(sourceFilePaths, false));
            }

            return VSConstants.S_OK;
        }

        public int OnBeforeCloseSolution(object pUnkReserved)
        {
            Debug.WriteLine("OnBeforeCloseSolution");
            return VSConstants.S_OK;
        }

        public int OnBeforeUnloadProject(IVsHierarchy pRealHierarchy, IVsHierarchy pStubHierarchy)
        {
            Debug.WriteLine("OnBeforeUnloadProject");
            return VSConstants.S_OK;
        }

        public int OnQueryCloseProject(IVsHierarchy pHierarchy, int fRemoving, ref int pfCancel)
        {
            Debug.WriteLine("OnQueryCloseProject");

            return VSConstants.S_OK;
        }

        public int OnQueryCloseSolution(object pUnkReserved, ref int pfCancel)
        {
            Debug.WriteLine("OnQueryCloseProject");

            return VSConstants.S_OK;
        }

        public int OnQueryUnloadProject(IVsHierarchy pRealHierarchy, ref int pfCancel)
        {
            Debug.WriteLine("OnQueryUnloadProject");
            return VSConstants.S_OK;
        }
    }
}
