using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using DexterCRC;
using DexterCRC.Src.CheckerLogic;

namespace DexterCSTest.Src.CRCLogic
{
    [TestClass]
    class HeadingRuleTest
    {
        HeadingRule rules;
        public void Init()
        {
            rules = new HeadingRule();
        }

        [TestMethod]
        public void HasDefect_SHedingRule_without_Heading()
        {
            Init();
            //given
            string statement = @"
                                  using System.Collections.Generic;
                                  using System.Linq;
                                  using System.Text;
                                  using System.Threading.Tasks;

                                            namespace DexterCRC.Src.CheckerLogic
                                                                            {
                                                 public class SampleClass {";
            //when
            bool result = rules.HasDefect(statement);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_HedingRule_with_Heading()
        {
            Init();
            //given
            string statement = @" // Copyright (c) 2016 Samsung Electronics Co., Ltd All Rights Reserved
                                    // PROPRIETARY/CONFIDENTIAL 
                                    // This software is the confidential and proprietary
                                    // information of SAMSUNG ELECTRONICS (Confidential Information). You shall
                                    // not disclose such Confidential Information and shall use it only in
                                    // accordance with the terms of the license agreement you entered into with
                                    // SAMSUNG ELECTRONICS. SAMSUNG make no representations or warranties about the
                                    // suitability of the software, either express or implied, including but not
                                    // limited to the implied warranties of merchantability, fitness for a
                                    // particular purpose, or non-infringement. SAMSUNG shall not be liable for any
                                    // damages suffered by licensee as a result of using, modifying or distributing
                                    // this software or its derivatives.

            using System.Collections.Generic;
                                  using System.Linq;
                                  using System.Text;
                                  using System.Threading.Tasks;

                                            namespace DexterCRC.Src.CheckerLogic
                                                                            {
                                                 public class SampleClass {";
            //when
            bool result = rules.HasDefect(statement);
            //then
            Assert.IsFalse(result);
        }
    }
}