/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LocUtil
{
    /** Block Comment Start Flag */
    private transient static boolean isBlockCommentStart;
    
    /** String Start Flag */
    private transient static boolean isStringStart;
    
    /** Line Comment Start String */
    private transient static String sLineCommentStartString;
    
    /** Block Comment Start String */
    private transient static String sBlockCommentStartString;
    
    /** Block Comment End String */
    private transient static String sBlockCommentEndString;
    
    /** String Character */
    private transient static char cStringChar;
    
    /** Constructor */
       
    /**
     * Line CodeÂºÂ° Check
     *
     * @param sLineData
     * @return
     */
    static int getCountLocData(String sLineData)
    {
        // Empty Line
        if (sLineData.trim().isEmpty())
        {
            return 0;
        }      
                        
         return checkCppLineData(sLineData);            
        
    }
    
   
    static int getEmptyLineCount(final String sLineData)
    {    	
    	if (sLineData.trim().isEmpty())
    	{
    		return 1;
    	}
    	return 0;
    }
    
    /**
     * C / C++ / Java LOC Counting
     * 
     * @param sLineData
     * @return
     */
    private static int checkCppLineData(final String sLineData)
    {
        // Comment Mark
        sBlockCommentStartString = "/*";
        sBlockCommentEndString   = "*/";
        sLineCommentStartString  = "//";        
     
        return checkLineData(sLineData, "\"", "'", true, true);
    }
    
    
    
    /**
     * Check Line Data and Return LOC
     * 
     * @param sLineData
     * @param sStringMark1
     * @param sStringMark2
     * @param isStringCheck
     * @param isBlockCommentCheck
     * @return
     */
    private static int checkLineData(String sLineData,	final String sStringMark1,final String sStringMark2,
    		final boolean isStringCheck,
    		final boolean isBlockCommentCheck)
    {
       
        if (isBlockCommentCheck && isBlockCommentStart)
        {
           
            final int idx = sLineData.indexOf(sBlockCommentEndString);
            if (idx != -1)
            {
               
               isBlockCommentStart = false;               
                final int iLen = idx + sBlockCommentEndString.length();
                if (sLineData.length() > iLen)
                {
                    return checkLineData(sLineData.substring(iLen).trim(), sStringMark1, sStringMark2, isStringCheck, isBlockCommentCheck);
                }
            }
            
            return 0;
        }
        
      
        if (isStringCheck && isStringStart)
        {
            
            sLineData = replace(sLineData, "\\\\", "|");    
            sLineData = replace(sLineData, "\\\"", "|");
            sLineData = replace(sLineData, "\\\'", "|");
            
          
            final int idx = sLineData.indexOf(cStringChar);
            if (idx != -1)
            {
              
                isStringStart = false;
               
                final int iLen = idx + 1;
                if (sLineData.length() > iLen)
                {
                    checkLineData(sLineData.substring(iLen).trim(), sStringMark1, sStringMark2, isStringCheck, isBlockCommentCheck);
                }
            }
            
            return 1;
        }
               
        String sPriorString  = "";
        String sCommentCheck = "";
        String sStringCheck  = "";
        
        if (isStringCheck)
        {
            sStringCheck = getPriorityString(sLineData, sStringMark1, sStringMark2);    
        }
        
        if (isBlockCommentCheck)
        {
            sCommentCheck = getPriorityString(sLineData, sLineCommentStartString, sBlockCommentStartString);   
        }
        else
        {
            sCommentCheck = getPriorityString(sLineData, sLineCommentStartString, sLineCommentStartString);   
        }
        
        if (sStringCheck.isEmpty())
        {
          
            sPriorString = sCommentCheck;
        }
        else if (sCommentCheck.isEmpty())
        {
          
            sPriorString = sStringCheck;
        }
        else
        {
            sPriorString = getPriorityString(sLineData, sStringCheck, sCommentCheck);
        }
        
       
        if (isStringCheck && (sPriorString.equals(sStringMark1) || sPriorString.equals(sStringMark2)))
        {
           
            sLineData = replace(sLineData, "\\\\", "|");   
            sLineData = replace(sLineData, "\\\"", "|");
            sLineData = replace(sLineData, "\\\'", "|");
            
           
            final int idx = sLineData.indexOf(sPriorString);
            final int iEndIdx = sLineData.indexOf(sPriorString, idx+1);
            if (iEndIdx == -1)
            {
            	
                isStringStart = true;
                cStringChar = sPriorString.charAt(0);
            }
            else
            {
            	
                final int iLen = iEndIdx + 1;
                if (sLineData.length() > iLen)
                {
                	checkLineData(sLineData.substring(iLen).trim(), sStringMark1, sStringMark2, isStringCheck, isBlockCommentCheck);
                }
            }
            
            return 1;
        }
       
        else if (sPriorString.equals(sLineCommentStartString))
        {
            final int idx = sLineData.indexOf(sLineCommentStartString);
            
           
            return checkAlphaNumeric(sLineData.substring(0, idx));
        }
        
        else if (isBlockCommentCheck && sPriorString.equals(sBlockCommentStartString))
        {
            final int iStartIdx = sLineData.indexOf(sBlockCommentStartString);
           
            final int iRet1 = checkAlphaNumeric(sLineData.substring(0, iStartIdx));
            
            
            final int iEndIdx = sLineData.indexOf(sBlockCommentEndString, iStartIdx+sBlockCommentStartString.length());
            if (iEndIdx == -1)
            {
                
                isBlockCommentStart = true;
            }
            else
            {
            	
                final int iLen = iEndIdx + sBlockCommentEndString.length();
                if (sLineData.length() > iLen)
                {
                    final int iRet2 = checkLineData(sLineData.substring(iLen).trim(), sStringMark1, sStringMark2, isStringCheck, isBlockCommentCheck);
                    
                    return ((iRet1 > iRet2) ? iRet1 : iRet2);
                }
            }
            
            return iRet1;
        }
        
        return checkAlphaNumeric(sLineData);
    }
    
        
    
    
    private static String getPriorityString(final String sLineData,	final String sSearchStr1,final String sSearchStr2)
    {
        if (sSearchStr1.isEmpty() || sSearchStr2.isEmpty())
        {
            return "";
        }
        
        int iFirstSearchIdx  = sLineData.indexOf(sSearchStr1);
        int iSecondSearchIdx = sLineData.indexOf(sSearchStr2);
        
      
        if ((iFirstSearchIdx == -1) && (iSecondSearchIdx == -1))
        {
            return "";
        }
        
        if (iFirstSearchIdx == -1)
        {
            iFirstSearchIdx = Integer.MAX_VALUE;
        }
        
        if (iSecondSearchIdx == -1)
        {
            iSecondSearchIdx = Integer.MAX_VALUE;
        }
        
        if (iFirstSearchIdx < iSecondSearchIdx)
        {
            return sSearchStr1;
        } else {
            return sSearchStr2;
        }
    }
    
    
    private static int checkAlphaNumeric(final String sLineData)
    {
        
        
        for (int i=0; i < sLineData.length(); i++)
        {
            if (Character.isLetterOrDigit(sLineData.charAt(i)))
            {
                return 1;
            }
        }
        return 0;
    }
    
    
    private static String replace(final String sTagetStr,
    		final String sOldStr,
    		final String sNewStr)
    {
    	final StringBuffer sbuf = new StringBuffer();
        
        int begin = 0;
        int idx = sTagetStr.indexOf(sOldStr);
        while (idx != -1)
        {
            sbuf.append(sTagetStr.substring(begin, idx));
            sbuf.append(sNewStr);
            begin = idx + sOldStr.length();
            idx = sTagetStr.indexOf(sOldStr, begin);
        }
        sbuf.append(sTagetStr.substring(begin));
        
        return sbuf.toString();
    }
    
	static int getLineUsingIndex(CharSequence data, int start) {
	    int line = 1;
	    Pattern pattern = Pattern.compile("\n");
	    Matcher matcher = pattern.matcher(data);
	    matcher.region(0, start);
	    while(matcher.find()) {
	        line++;
	    }
	    return(line);
	}
}
