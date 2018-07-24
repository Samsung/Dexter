using System.Collections.Generic;

namespace DexterCS
{
    public interface IDexterPluginInitializer
    {
        void Init(List<IDexterPlugin> list);
    }
}