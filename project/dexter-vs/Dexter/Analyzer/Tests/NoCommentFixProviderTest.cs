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
    public class NoCommentFixProviderTest : CodeFixVerifier
    {
        [Test]
        public void NoCommentFixProvider_Fix_GivenClassTypeWithoutDoxygenComment()
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
        public void NoCommentFixProvider_Fix_GivenInterfaceTypeWithoutDoxygenComment()
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
        public void NoCommentFixProvider_Fix_GivenStructTypeWithoutDoxygenComment()
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
        public void NoCommentFixProvider_Fix_GivenEnumTypeWithoutDoxygenComment()
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

        [Test]
        public void NoCommentFixProvider_Fix_GivenMethodTypeWithNoParams_WithoutDoxygenComment()
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
        public void Test()
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
        public void Test()
        {   
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentFixProvider_Fix_GivenMethodTypeWithParams_WithoutDoxygenComment()
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
        public void Test(int start, int end)
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
        /// <param name=""start""> </param>
        /// <param name=""end""> </param>
        public void Test(int start, int end)
        {   
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentFixProvider_Fix_GivenMethodTypeWithThrows_WithoutDoxygenComment()
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
        public void Test(int n)
        {   
            if (n == 1) {
                throw new Exception(""test"");
            } else {
                throw new ArgumentException(""test2"");
            }
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
        /// <param name=""n""> </param>
        /// <exception cref=""Exception""> </exception>
        /// <exception cref=""ArgumentException""> </exception>
        public void Test(int n)
        {   
            if (n == 1) {
                throw new Exception(""test"");
            } else {
                throw new ArgumentException(""test2"");
            }
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentFixProvider_Ignore_GivenMethodTypeWithInvalidThrows()
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
        public void Test(int n)
        {   
            if (n == 1) {
                throw;
            } else {
                throw GetException(""test"");
            }
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
        /// <param name=""n""> </param>
        public void Test(int n)
        {   
            if (n == 1) {
                throw;
            } else {
                throw GetException(""test"");
            }
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentFixProvider_Fix_GivenMethodTypeWithValidReturnType()
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
        public int Test()
        {   
            return 1;
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
        /// <returns> </returns>
        public int Test()
        {   
            return 1;
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentFixProvider_Fix_GivenConstructorType()
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
        /// <summary> test </summary>
        public class TestClass
        {
            public TestClass(int n)
            {   
            }
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
        /// <summary> test </summary>
        public class TestClass
        {
            /// <summary>
            /// 
            /// </summary>
            /// <param name=""n""> </param>
            public TestClass(int n)
            {   
            }
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentFixProvider_Fix_GivenPropertyType()
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
        /// <summary> test </summary>
        public class TestClass
        {
            public int TestProperty
            {   
                get; set;
            }
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
        /// <summary> test </summary>
        public class TestClass
        {
            /// <summary>
            /// 
            /// </summary>
            public int TestProperty
            {   
                get; set;
            }
        }
    }";
            VerifyCSharpFix(test, fixtest);
        }

        [Test]
        public void NoCommentFixProvider_Fix_GivenPropertyType_WithExceptionInGetter()
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
        /// <summary> test </summary>
        public class TestClass
        {
            public int TestProperty
            {   
                get {
                    throw new Exception(""test exception"");
                }
                set {
                    throw new ArgumentException(""argument"");
                }
            }
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
        /// <summary> test </summary>
        public class TestClass
        {
            /// <summary>
            /// 
            /// </summary>
            /// <exception cref=""Exception""> </exception>
            /// <exception cref=""ArgumentException""> </exception>
            public int TestProperty
            {   
                get {
                    throw new Exception(""test exception"");
                }
                set {
                    throw new ArgumentException(""argument"");
                }
            }
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
            return new NoCommentFixProvider();
        }
    }


}
