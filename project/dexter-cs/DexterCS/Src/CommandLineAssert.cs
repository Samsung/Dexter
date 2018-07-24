using System;

namespace DexterCS
{
    public class CommandLineAssert
    {
        public static void AssertExclusiveOptions(object firstOption, object secondOption)
        {
            if (DexterUtil.HasOption(firstOption) &&
                DexterUtil.HasOption(secondOption)) 
            {
                throw new Exception(@"you can't use option -" + firstOption + " and with -" + secondOption);
            }
        }
    }
}
