﻿using NUnit.Framework;
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
        public class TestClass 
        {
            public void Test()
            {   
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoMethodCommentRuleId,
                Message = "API Type 'Test' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 14, 25)
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
        public class TestClass 
        {
            public TestClass()
            {   
            }
        }   
    }";
            var expected = new DiagnosticResult
            {
                Id = NoCommentAnalyzer.NoMethodCommentRuleId,
                Message = "API Type 'TestClass' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 14, 20)
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
                Id = NoCommentAnalyzer.NoPropertyCommentRuleId,
                Message = "API Type 'TestProperty' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 14, 24)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        protected override DiagnosticAnalyzer GetCSharpDiagnosticAnalyzer()
        {
            return new NoCommentAnalyzer();
        }
    }

    
}