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
using DexterCS;
using log4net;
using OpenNLP.Tools.PosTagger;
using System;
using System.IO;

namespace DexterCRC.Src.Util
{
    public static class OpenNLPUtil
    {
        private static ILog CliLog = LogManager.GetLogger(typeof(DexterUtil));

        public static string ENGLISH_POS_DICTIONARY
        {
            get
            {
                return DexterConfig.Instance.DexterHome
                     + DexterUtil.FILE_SEPARATOR + "bin"
                     + DexterUtil.FILE_SEPARATOR + "dexterCS"
                     + DexterUtil.FILE_SEPARATOR + "EnglishPOS.nbin";
            }
        }

        // Source of tags https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html

        public static string[] NounTags = { "NN", "NNP", "NNPS", "NNS" };

        public static string[] VerbTags = { "MD", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" };

        public static string[] AdjectiveTags = { "JJ", "JJR", "JJS" };

        public static string[] AdverbTags = { "RB", "RBR", "RBS", "WRB" };

        public static string[] OtherTags = { "CC", "CD", "DT", "EX", "FW", "IN", "LS", "PDT", "POS", "PRP", "PRP$", "RP", "SYM", "TO", "UH", "WDT", "WP", "WP$" };

        private static EnglishMaximumEntropyPosTagger englishMaximumEntropyPosTagger;

        private static bool DoesWordMatchAnyOfTags(string word, string[] tags)
        {
            string wordTag;
            try
            {
                wordTag = GetTagger().Tag(new string[] { word })[0];

            }
            catch (Exception e)
            {
                CliLog.Error(e.Message);
                return true;
            }

            foreach (string tag in tags)
            {
                if (wordTag == tag)
                {
                    return true;
                }
            }

            return false;
        }

        private static bool IsNoun(string word)
        {
            return DoesWordMatchAnyOfTags(word, NounTags);
        }

        private static bool IsVerb(string word)
        {
            return DoesWordMatchAnyOfTags(word, VerbTags);
        }

        public static bool AreNouns(string[] words)
        {
            foreach (string word in words)
            {
                if (!IsNoun(word))
                {
                    return false;
                }
            }
            return true;
        }

        public static bool IsVerbPhrase(string[] words)
        {
            foreach (string word in words)
            {
                if (IsVerb(word))
                {
                    return true;
                }
            }
            return false;
        }

        private static EnglishMaximumEntropyPosTagger GetTagger()
        {
            if (englishMaximumEntropyPosTagger == null)
            {
                if (!File.Exists(ENGLISH_POS_DICTIONARY))
                {
                    throw new DexterRuntimeException("Cannot perform naming analysis. Dictionary not found in directory: " +
                        ENGLISH_POS_DICTIONARY);
                }
                englishMaximumEntropyPosTagger = new EnglishMaximumEntropyPosTagger(ENGLISH_POS_DICTIONARY);
            }
            return englishMaximumEntropyPosTagger;
        }
    }
}
