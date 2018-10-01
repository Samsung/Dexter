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
using Newtonsoft.Json;
using System;

namespace DexterCS
{
    public class Occurence
    {
        public Occurence() { }
        [JsonProperty("code")]
        public string Code { get; set; }
        [JsonProperty("startLine")]
        public int StartLine { get; set; }
        [JsonProperty("endLine")]
        public int EndLine { get; set; }
        [JsonProperty("charStart")]
        public int CharStart { get; set; }
        [JsonProperty("charEnd")]
        public int CharEnd { get; set; }
        [JsonProperty("variableName")]
        public string VariableName { get; set; }
        [JsonProperty("stringValue")]
        public string StringValue { get; set; }
        [JsonProperty("fieldName")]
        public string FieldName { get; set; }
        [JsonProperty("message")]
        public string Message { get; set; }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        public override bool Equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            PreOccurence other = (PreOccurence)obj;

            if (!Object.Equals(StartLine, other.StartLine))
            {
                return false;
            }
            if (!Object.Equals(EndLine, other.EndLine))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(FieldName) && !Object.Equals(FieldName, other.FieldName))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(VariableName) && !Object.Equals(VariableName, other.VariableName))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(StringValue) && !Object.Equals(StringValue, other.StringValue))
            {
                return false;
            }
            if (!string.IsNullOrEmpty(Message) && !Object.Equals(Message, other.Message))
            {
                return false;
            }

            return true;
        }
    }
}
