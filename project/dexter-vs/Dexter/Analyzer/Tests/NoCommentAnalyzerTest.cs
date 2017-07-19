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
                Id = "VD0001",
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
                Id = "VD0001",
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
                Id = "VD0001",
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
                Id = "VD0001",
                Message = "API Type 'Test' has no doxygen comment",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 11, 21)
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
