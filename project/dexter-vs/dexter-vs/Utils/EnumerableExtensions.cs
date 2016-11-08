using System.Collections.Generic;
using System.Linq;

namespace dexter_vs.Utils
{
    /// <summary>
    /// Extension methods for enumerables 
    /// </summary>
    public static class EnumerableExtensions
    {
        /// <summary>
        /// Returns enumerable or its empty equivalent if null
        /// </summary>
        /// <typeparam name="T">Type of element</typeparam>
        /// <param name="source">Enumerable</param>
        /// <returns>this Enumerable of empty Enumerable if is null</returns>
        public static IEnumerable<T> OrEmptyIfNull<T>(this IEnumerable<T> source)
        {
            return source ?? Enumerable.Empty<T>();
        }
    }
}
