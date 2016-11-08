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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.sun.jersey.spi.StringReader;

class SourceCodeMatricsHelper
{
	
	/**
	 * getSourceLOCArray(String source) method is responsible
	 * for calculationg total SLOC (Source Line of Code)
	 * 
	 * @param     	[in] String source
	 * @return		[out] int loc
	 * @warning		[None]
	 * @exception	IO exception
	 */
	static int[] getSourceLOCArray(final String source)
	{
		int loc = 0;
		int FileLOC =0;
		int emptyLine=0;
		int[] array=new int[4];
		try
		{

			
			final BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(source)));
			try
			{
				String line = bufferedReader.readLine();
				while (line != null)
				{
					++FileLOC;
					emptyLine +=LocUtil.getEmptyLineCount(line);
					
					if ((line.matches("(\\s*)(\\{)[\\s\\{.]*") ||
							line.matches("(.*)(\\s*)(\\})[\\s\\}]*")) &&
							(LocUtil.getCountLocData(line)==0))
					{
						loc += 1;
					}
					else
					{
						int soc =LocUtil.getCountLocData(line);
						if(soc == 0)
						{
							if(line.equals("};"))
							{
								soc +=1;
							}
							
						}
						loc += soc;
					}

					line = bufferedReader.readLine();
				}
				bufferedReader.close();
				array[0]=loc;
				array[1]=FileLOC;
				array[2] =emptyLine;
				array[3] =FileLOC - (loc + emptyLine);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					bufferedReader.close();
				}
				catch (IOException e1)
				{				
					e1.printStackTrace();
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return array;
	}
	
	/**
	 * getLOCOfAllFiles(String source) method is responsible
	 * for calculationg total SLOC (Source Line of Code)
	 * 
	 * @param     	[in] String source
	 * @return		[out] int loc
	 * @warning		[None]
	 * @exception	IO exception
	 */
	static int getSourceLOC(final String source)
	{
		int loc = 0;
		try
		{

			
			final BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(source)));
			try
			{
				String line = bufferedReader.readLine();
				while (line != null)
				{
					
					if ((line.matches("(\\s*)(\\{)[\\s\\{.]*") ||
							line.matches("(.*)(\\s*)(\\})[\\s\\}]*")) &&
							(LocUtil.getCountLocData(line)==0))
					{
						loc += 1;
					}
					else
					{
						loc += LocUtil.getCountLocData(line);
					}

					line = bufferedReader.readLine();
				}
				bufferedReader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					bufferedReader.close();
				}
				catch (IOException e1)
				{				
					e1.printStackTrace();
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return loc;
	}

	/**
	 * getLOFOfAllFiles(String source) method is responsible
	 * for calculating total SLOC (Source Line of Code)
	 * 
	 * @param     	[in] String source
	 * @return		[out] int loc
	 * @warning		[None]
	 * @exception	IO exception
	 */
	static int getFileTotalLOC(final String source)
	{
		int loc = 0;
		try
		{
			BufferedReader bufferedReader = null;
			try
			{
				bufferedReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(source)));
				while (bufferedReader.readLine() != null)
				{
					loc++;
				}

				bufferedReader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					if (bufferedReader != null)
					{
						bufferedReader.close();
					}
				}
				catch (IOException e1)
				{				
					e1.printStackTrace();
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return loc;
	}

	/**
	 * getLOCOfAllFiles(String source) method is responsible
	 * for calculating total CLOF (comment Line of Code)
	 * 
	 * @param     	[in] String source
	 * @return		[out] int loc
	 * @warning		[None]
	 * @exception	IO exception
	 */
	static int getCommentedLineLOC(final String source)
	{
		int commentCodeLinecount = 0;		
		BufferedReader bufferedReader = null;
		try
		{
			bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(source)));
			String line;
			int TotalLine = 0;
			commentCodeLinecount = 0;
			int emptyLine = 0;
			int loc = 0;
			while ((line = bufferedReader.readLine()) != null)
			{
				TotalLine++;
				//To fix CQ bugs SISC00007055 and SISC00007063
				if ((line.matches("(\\s*)(\\{)[\\s\\{]*(.*)") ||
						line.matches("(.*)(\\s*)(\\})[\\s\\}]*")) &&
						(LocUtil.getCountLocData(line)==0))
				{
					loc += 1;
				}
				else
				{
					loc += LocUtil.getCountLocData(line);
				}
				emptyLine +=LocUtil.getEmptyLineCount(line);
			}
			commentCodeLinecount = (TotalLine - (loc + emptyLine));
			bufferedReader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try
			{
				if (bufferedReader != null)
				{
					bufferedReader.close();
				}
			}
			catch (IOException e1)
			{				
				e1.printStackTrace();
			}
		}


		return commentCodeLinecount;
	}

	/**
	 * getLOCOfAllFiles(String source) method is responsible
	 * for calculating total ELOC (blank Line of Code)
	 * 
	 * @param     	[in] String source
	 * @return		[out] int loc
	 * @warning		[None]
	 * @exception	IO exception
	 */
	static int getEmptyLineLOC(final String source)
	{
		int emptyLine = 0;
		try
		{
			
			BufferedReader bufferedReader = null;
			try
			{
				bufferedReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(source)));
				String line;
				emptyLine = 0;
				while ((line = bufferedReader.readLine()) != null)
				{
					emptyLine +=LocUtil.getEmptyLineCount(line);
				}
				bufferedReader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					if (bufferedReader != null)
					{
						bufferedReader.close();
					}
				}
				catch (IOException e1)
				{				
					e1.printStackTrace();
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return emptyLine;
	}
	

}