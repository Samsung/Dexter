#region Copyright notice
/**
 * Copyright (c) 2018 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#endregion
using DexterCRC;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace DexterCRC.Tests
{
    [TestClass()]
    public class HeadingRuleTest
    {
        HeadingRule rules;
        public void Init()
        {
            rules = new HeadingRule();
        }


        [TestMethod]
        public void HasDefect_HedingRule_WithHeading_ReturnsFalse()
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

                                            namespace DexterCRC
                                                                            {
                                                 public class SampleClass {";
            //when
            bool result = rules.HasDefect(statement);
            //then
            Assert.IsFalse(result);
        }


        [TestMethod]
        public void HasDefect_HeadingRule_WithoutHeading_ReturnsTrue()
        {
            Init();
            //given
            string statement = @"
                                  using System.Collections.Generic;
                                  using System.Linq;
                                  using System.Text;
                                  using System.Threading.Tasks;

                                            namespace DexterCRC
                                                                            {
                                                 public class SampleClass {";
            //when
            bool result = rules.HasDefect(statement);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_HeadingRule_WithoutCopyrightInHeading_ReturnsTrue()
        {
            Init();
            //given
            string statement = @" // --------- (c) 2016 Samsung Electronics Co., Ltd All Rights Reserved
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

                                            namespace DexterCRC
                                                                            {
                                                 public class SampleClass {";
            //when
            bool result = rules.HasDefect(statement);
            //then
            Assert.IsTrue(result);
        }

        [TestMethod]
        public void HasDefect_HeadingRule_WithoutSamsungInHeading_ReturnsTrue()
        {
            Init();
            //given
            string statement = @" // Copyright (c) 2016 Some Company, Ltd All Rights Reserved
                                    // PROPRIETARY/CONFIDENTIAL 
                                    // This software is the confidential and proprietary
                                    // information of SOME COMPANY (Confidential Information). You shall
                                    // not disclose such Confidential Information and shall use it only in
                                    // accordance with the terms of the license agreement you entered into with
                                    // SOME COMPANY. SOME COMPANY make no representations or warranties about the
                                    // suitability of the software, either express or implied, including but not
                                    // limited to the implied warranties of merchantability, fitness for a
                                    // particular purpose, or non-infringement. SOME COMPANY shall not be liable for any
                                    // damages suffered by licensee as a result of using, modifying or distributing
                                    // this software or its derivatives.

            using System.Collections.Generic;
                                  using System.Linq;
                                  using System.Text;
                                  using System.Threading.Tasks;

                                            namespace DexterCRC
                                                                            {
                                                 public class SampleClass {";
            //when
            bool result = rules.HasDefect(statement);
            //then
            Assert.IsTrue(result);
        }
    }
}