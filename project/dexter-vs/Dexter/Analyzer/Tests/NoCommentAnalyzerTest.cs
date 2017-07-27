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
    public class NoCommentAnalyzerTest : CodeFixVerifier
    {
        [Test]
        public void NoCommentAnalyzer_verfiy_GivenClassTypeWithoutDoxygenComment()
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
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoCommentRuleId,
                Message = "API Type 'TestClass' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 22)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoCommentAnalyzer_Ignore_GivenNotPublicClassTypeWithoutDoxygenComment()
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
        class TestClass
        {   
        }
    }";

            VerifyCSharpDiagnostic(test);
        }

        [Test]
        public void NoCommentAnalyzer_Ignore_GivenPublicClassTypeWithDoxygenComment()
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
        /// <summary>
        ///
        /// </summary>
        /// <code> </code>
        public class TestClass
        {   
        }
    }";

            VerifyCSharpDiagnostic(test);
        }

        [Test]
        public void NoCommentAnalyzer_verfiy_GivenPublicInterfaceWithoutDoxygenComment()
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
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoCommentRuleId,
                Message = "API Type 'Test' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 26)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoCommentAnalyzer_verfiy_GivenPublicStructWithoutDoxygenComment()
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
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoCommentRuleId,
                Message = "API Type 'Test' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 23)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoCommentAnalyzer_verfiy_GivenPublicEnumWithoutDoxygenComment()
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
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoCommentRuleId,
                Message = "API Type 'Test' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 21)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoCommentAnalyzer_Ignore_GivenPublicEnumWithInternalParent()
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
        internal class TestClass
        {
            public enum Test
            {   
                Start,
                Stop
            }
        }
    }";

            VerifyCSharpDiagnostic(test);
        }

        [Test]
        public void NoCommentAnalyzer_verfiy_GivenPublicMethodWithoutDoxygenComment()
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
        /// <code> test </code>
        public class TestClass 
        {
            public void Test()
            {   
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoCommentMethodRuleId,
                Message = "API Type 'Test' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 15, 25)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoCommentAnalyzer_Ignore_GivenPublicMethodWithInternalParent()
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
        internal class TestClass 
        {
            public void Test()
            {   
            }
        }
    }";

            VerifyCSharpDiagnostic(test);
        }

        [Test]
        public void NoCommentAnalyzer_verfiy_GivenPublicConstructorWithoutDoxygenComment()
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
        /// <code> test </code>
        public class TestClass 
        {
            public TestClass()
            {   
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoCommentMethodRuleId,
                Message = "API Type 'TestClass' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 15, 20)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoCommentAnalyzer_verfiy_GivenPublicPropertyWithoutDoxygenComment()
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
        /// <code> test </code>
        public class TestClass 
        {
            public int TestProperty
            {   
                get; set;
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoCommentPropertyRuleId,
                Message = "API Type 'TestProperty' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 15, 24)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoSummaryRule_verfiy_GivenPublicClassWithoutSummaryComment()
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
        /// <code> test </code>
        public class TestClass 
        {
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoSummaryRuleId,
                Message = "API Type 'TestClass' has no summary comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 12, 22)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoCodeRule_verfiy_GivenPublicClassWithoutCodeComment()
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
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoCodeRuleId,
                Message = "API Type 'TestClass' has no code comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 12, 22)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoReturnsRule_verfiy_GivenPublicMethodWithoutReturnsComment()
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
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public int TestMethod()
            {
                return 0;
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoReturnsRuleId,
                Message = "Method 'TestMethod' has no returns comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 16, 24)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoReturnsRule_Ignore_GivenPublicVoidMethodWithoutReturnsComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public void TestMethod()
            {
            }
        }   
    }";

            VerifyCSharpDiagnostic(test);
        }

        [Test]
        public void NoParamRule_Verify_GivenPublicVoidMethodParamWithoutParamComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public void TestMethod(int first)
            {
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoParamRuleId,
                Message = "Method Parameter 'first' has no param comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 40)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoParamRule_Verify_GivenPublicConstructorParamWithoutParamComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public TestClass(int first)
            {
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoParamRuleId,
                Message = "Method Parameter 'first' has no param comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 34)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoParamRule_Verify_GivenPublicVoidMethodMultiParamWithoutParamComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public void TestMethod(int first, int second)
            {
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoParamRuleId,
                Message = "Method Parameter 'first' has no param comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 40)
                        }
            };
            var expected2 = expected;
            expected2.Message = "Method Parameter 'second' has no param comment";
            expected2.Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 51)
                        };

            VerifyCSharpDiagnostic(test, expected, expected2);
        }

        [Test]
        public void NoParamRule_IgnoreThisParam_GivenPublicVoidMethodMultiParamWithoutParamComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public void TestMethod(this String first, int second)
            {
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoParamRuleId,
                Message = "Method Parameter 'second' has no param comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 59)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoExceptionRule_Verify_GivenPublicMethodThrowsException_WithoutExceptionComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public void TestMethod()
            {
                throw new Exception(""test"");
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoExceptionRuleId,
                Message = "'Exception' throwed in API has no exception comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 13, 27)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoExceptionRule_Verify_GivenPublicMethodThrowsMultiException_WithoutExceptionComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public void TestMethod()
            {
                int n = 0;
                if (n == 0)
                    throw new Exception(""test"");
                else
                    throw new ArgumentException(""test"");
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoExceptionRuleId,
                Message = "'Exception' throwed in API has no exception comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 15, 31)
                        }
            };
            var expected2 = expected;
            expected2.Message = "'ArgumentException' throwed in API has no exception comment";
            expected2.Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 17, 31)
                        };

            VerifyCSharpDiagnostic(test, expected, expected2);
        }

        [Test]
        public void NoExceptionRule_Verify_GivenPublicPropertyThrowsException_WithoutExceptionComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public int TestMethod
            {
                get 
                { 
                    throw new Exception(""test""); 
                }
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoExceptionRuleId,
                Message = "'Exception' throwed in API has no exception comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 15, 31)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoExceptionRule_Verify_GivenPublicPropertyThrowsMultiException_WithoutExceptionComment()
        {
            var test = @"
    using System;

    namespace ConsoleApplication1
    {
        /// <summary> test </summary>
        /// <code> test </code>
        public class TestClass 
        {
            /// <summary> test </summary>
            public int TestMethod
            {
                get 
                { 
                    throw new Exception(""test"");
                }
                set
                {
                    throw new ArgumentException(""test"");
                }
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoExceptionRuleId,
                Message = "'Exception' throwed in API has no exception comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 15, 31)
                        }
            };
            var expected2 = expected;
            expected2.Message = "'ArgumentException' throwed in API has no exception comment";
            expected2.Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 19, 31)
                        };

            VerifyCSharpDiagnostic(test, expected, expected2);
        }

        protected override DiagnosticAnalyzer GetCSharpDiagnosticAnalyzer()
        {
            return new NoCommentAnalyzer();
        }
    }

    
}
