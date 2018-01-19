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
    public class NoFileCommentFixProviderTest : CodeFixVerifier
    {
        [Test]
        public void NoFileCommentFixProvider_Fix_GivenPublicApiFileWithoutDoxygenComment()
        {
            var test = @"
using System;

namespace ConsoleApplication1
{
    public class TestClass
    {   
    }
}";
            var fixtest = @"/// @file Test0.cs
/// <published> N </published>
/// <privlevel> non-privilege </privlevel>
/// <privilege> none </privilege> 
/// <privacy> N </privacy>
/// <product> TV </product>
/// <version> 3.*.* </version>
/// <SDK_Support> Y </SDK_Support>
/// Copyright (c) 2017 Samsung Electronics Co., Ltd All Rights Reserved
/// PROPRIETARY/CONFIDENTIAL 
/// This software is the confidential and proprietary
/// information of SAMSUNG ELECTRONICS (""Confidential Information""). You shall
/// not disclose such Confidential Information and shall use it only in
/// accordance with the terms of the license agreement you entered into with
/// SAMSUNG ELECTRONICS. SAMSUNG make no representations or warranties about the
/// suitability of the software, either express or implied, including but not
/// limited to the implied warranties of merchantability, fitness for a
/// particular purpose, or non-infringement. SAMSUNG shall not be liable for any
/// damages suffered by licensee as a result of using, modifying or distributing
/// this software or its derivatives.
using System;

namespace ConsoleApplication1
{
    public class TestClass
    {   
    }
}";
            VerifyCSharpFix(test, fixtest);
        }

        protected override DiagnosticAnalyzer GetCSharpDiagnosticAnalyzer()
        {
            return new NoFileCommentAnalyzer();
        }

        protected override CodeFixProvider GetCSharpCodeFixProvider()
        {
            return new NoFileCommentFixProvider();
        }
    }


}
