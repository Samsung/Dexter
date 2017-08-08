using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Diagnostics;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio;

namespace Dexter.Common.Utils
{
    /// <summary>
    /// Implements IVsHiereachyEvents to get all file lists of projects
    /// </summary>
    public class DexterHierarchyEvents : IVsHierarchyEvents
    {
        public DexterHierarchyEvents()
        {

        }

        public int OnInvalidateIcon(IntPtr hicon)
        {
            Debug.WriteLine("OnInvalidateIcon");
            return VSConstants.S_OK;
        }

        public int OnInvalidateItems(uint itemidParent)
        {
            Debug.WriteLine("OnInvalidateItems");
            return VSConstants.S_OK;
        }

        public int OnItemAdded(uint itemidParent, uint itemidSiblingPrev, uint itemidAdded)
        {
            Debug.WriteLine("OnItemAdded");
            return VSConstants.S_OK;
        }

        public int OnItemDeleted(uint itemid)
        {
            Debug.WriteLine("OnItemDeleted");
            return VSConstants.S_OK;
        }

        public int OnItemsAppended(uint itemidParent)
        {
            Debug.WriteLine("OnItemsAppended");
            return VSConstants.S_OK;
        }

        public int OnPropertyChanged(uint itemid, int propid, uint flags)
        {
            Debug.WriteLine("OnPropertyChanged");
            return VSConstants.S_OK;
        }
    }
}
