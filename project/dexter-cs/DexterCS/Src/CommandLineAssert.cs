using CommandLine;
using CommandLine.Text;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
