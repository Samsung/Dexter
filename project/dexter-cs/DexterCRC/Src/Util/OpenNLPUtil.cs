using DexterCS;
using OpenNLP.Tools.PosTagger;
using System;
using System.IO;

namespace DexterCRC.Src.Util
{
    public static class OpenNLPUtil
    {
        // Source of tags https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html

        public static string[] NounTags = { "NN", "NNP", "NNPS", "NNS" };

        public static string[] VerbTags = { "MD", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" };

        public static string[] AdjectiveTags = { "JJ", "JJR", "JJS" };

        public static string[] AdverbTags = { "RB", "RBR", "RBS", "WRB" };

        public static string[] OtherTags = { "CC", "CD", "DT", "EX", "FW", "IN", "LS", "PDT", "POS", "PRP", "PRP$", "RP", "SYM", "TO", "UH", "WDT", "WP", "WP$" };

        private static EnglishMaximumEntropyPosTagger englishMaximumEntropyPosTagger;

        private static bool DoesWordMatchAnyOfTags(string word, string[] tags)
        {
            string wordTag = GetTagger().Tag(new string[] { word })[0];

            foreach (string tag in tags)
            {
                if (wordTag == tag)
                {
                    return true;
                }
            }

            return false;
        }

        public static bool IsNoun(string word)
        {
            return DoesWordMatchAnyOfTags(word, NounTags);
        }

        public static bool AreNouns(string[] words)
        {
            foreach(string word in words)
            {
                if (!IsNoun(word))
                {
                    return false;
                }
            }
            return true;
        }

        private static EnglishMaximumEntropyPosTagger GetTagger()
        {
            if (englishMaximumEntropyPosTagger == null)
            {
                englishMaximumEntropyPosTagger = new EnglishMaximumEntropyPosTagger(
                     DexterConfig.Instance.DexterHome + "\\bin\\dexterCS\\EnglishPOS.nbin");
            }
            return englishMaximumEntropyPosTagger;
        }
    }
}
