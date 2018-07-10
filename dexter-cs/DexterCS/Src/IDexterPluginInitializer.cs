using System.Collections.Generic;
using DexterCS;

namespace DexterCS
{
    public interface IDexterPluginInitializer
    {
        void Init(List<IDexterPlugin> list);
    }
}