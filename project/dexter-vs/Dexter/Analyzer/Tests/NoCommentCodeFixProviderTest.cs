using NUnit.Framework;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CodeFixes;
using Microsoft.CodeAnalysis.Diagnostics;
using System;
using Dexter.Analyzer.Tests.Helpers;
using Dexter.Analyzer;

namespace Dexter.Analyzer.Tests
{
    [TestFixture]
    public class NoCommentCodeFixProviderTest : CodeFixVerifier
    {
        [Test]
        public void NoCommentCodeFixProvider_Fix_GivenClassTypeWithoutDoxygenComment()
        {
            var test = @"
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        public class TestClass
        {   
        }
    }";
            var fixtest = @"
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        /// <summary>
        /// 
        /// </summary>
        /// <code>
        /// 
        /// </code>
        public class TestClass
        {   
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentCodeFixProvider_Fix_GivenInterfaceTypeWithoutDoxygenComment()
        {
            var test = @"
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        public interface Test
        {   
        }
    }";
            var fixtest = @"
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        /// <summary>
        /// 
        /// </summary>
        public interface Test
        {   
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentCodeFixProvider_Fix_GivenStructTypeWithoutDoxygenComment()
        {
            var test = @"
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        public struct Test
        {   
        }
    }";
            var fixtest = @"
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        /// <summary>
        /// 
        /// </summary>
        public struct Test
        {   
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentCodeFixProvider_Fix_GivenEnumTypeWithoutDoxygenComment()
        {
            var test = @"
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        public enum Test
        {   
            Start,
            Stop
        }
    }";
            var fixtest = @"
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        /// <summary>
        /// 
        /// </summary>
        public enum Test
        {   
            Start,
            Stop
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        protected override DiagnosticAnalyzer GetCSharpDiagnosticAnalyzer()
        {
            return new NoCommentAnalyzer();
        }

        protected override CodeFixProvider GetCSharpCodeFixProvider()
        {
            return new NoCommentCodeFixProvider();
        }
    }


}
