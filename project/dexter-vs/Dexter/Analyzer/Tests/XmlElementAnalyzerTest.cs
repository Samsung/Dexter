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
    public class XmlElementAnalyzerTest : CodeFixVerifier
    {
        [Test]
        public void NoXmlTextAnalyzer_verfiy_GivenEmptySummary()
        {
            var test = @"using System;
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
            var expected = new DiagnosticResult
            {
                Id = XmlElementAnalyzer.NoXmlTextRuleId,
                Message = "Xml tag 'summary' has no description",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 10, 14)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoXmlTextAnalyzer_Ignore_GivenValidSummary()
        {
            var test = @"using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        /// <summary>
        /// Test summary
        /// </summary>
        public class TestClass
        {   
        }
    }";

            VerifyCSharpDiagnostic(test);
        }

        [Test]
        public void NoXmlTextAnalyzer_verfiy_GivenEmptyCode()
        {
            var test = @"using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Diagnostics;

    namespace ConsoleApplication1
    {
        /// <summary>
        /// Test summary
        /// </summary>
        /// <code>
        /// 
        /// </code>
        public class TestClass
        {   
        }
    }";
            var expected = new DiagnosticResult
            {
                Id = XmlElementAnalyzer.NoXmlTextRuleId,
                Message = "Xml tag 'code' has no description",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 13, 14)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoXmlTextAnalyzer_verfiy_GivenEmptyParam()
        {
            var test = @"using System;
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
            /// test
            /// </summary>
            /// <param name=""n""> </param>
            public TestClass(int n)
            {   
            }
        }
    }";
            var expected = new DiagnosticResult
            {
                Id = XmlElementAnalyzer.NoXmlTextRuleId,
                Message = "Xml tag 'param' has no description",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 16, 18)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoXmlTextAnalyzer_verfiy_GivenEmptyReturns()
        {
            var test = @"using System;
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
            /// test
            /// </summary>
            /// <returns> </returns>
            public TestClass()
            {   
            }
        }
    }";
            var expected = new DiagnosticResult
            {
                Id = XmlElementAnalyzer.NoXmlTextRuleId,
                Message = "Xml tag 'returns' has no description",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 16, 18)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoXmlTextAnalyzer_verfiy_GivenEmptyException()
        {
            var test = @"using System;
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
            /// test
            /// </summary>
            /// <exception cref=""Exception""> </exception>
            public TestClass()
            {   
                throw new Exception(""test"");
            }
        }
    }";
            var expected = new DiagnosticResult
            {
                Id = XmlElementAnalyzer.NoXmlTextRuleId,
                Message = "Xml tag 'exception' has no description",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 16, 18)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        [Test]
        public void NoXmlTextAnalyzer_verfiy_GivenNoEndSummary()
        {
            var test = @"using System;
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
            /// test
            public TestClass()
            {   
            }
        }
    }";
            var expected = new DiagnosticResult
            {
                Id = XmlElementAnalyzer.NoEndTagRuleId,
                Message = "Xml tag 'summary' has no end tag",
                Severity = DiagnosticSeverity.Warning,
                Locations =
                    new[] {
                            new DiagnosticResultLocation("Test0.cs", 13, 18)
                        }
            };

            VerifyCSharpDiagnostic(test, expected);
        }

        protected override DiagnosticAnalyzer GetCSharpDiagnosticAnalyzer()
        {
            return new XmlElementAnalyzer();
        }
    }


}
