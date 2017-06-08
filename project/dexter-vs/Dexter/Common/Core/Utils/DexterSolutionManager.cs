using Microsoft.VisualStudio.Shell.Interop;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;
using Microsoft.VisualStudio;

namespace Dexter.Common.Utils
{
    /// <summary>
    /// Manages VS soultions and packages information/events
    /// </summary>
    public class DexterSolutionManager : IVsSolutionEvents
    {
        static DexterSolutionManager instace;

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

            //uint cookie;
            //pHierarchy.AdviseHierarchyEvents(new DexterHierarchyEvents(), out cookie);

            return VSConstants.S_OK;
        }

        public int OnAfterOpenSolution(object pUnkReserved, int fNewSolution)
        {
            Debug.WriteLine("OnAfterOpenSolution");
            return VSConstants.S_OK;
        }

        public int OnBeforeCloseProject(IVsHierarchy pHierarchy, int fRemoved)
        {
            Debug.WriteLine("OnBeforeCloseProject");
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
