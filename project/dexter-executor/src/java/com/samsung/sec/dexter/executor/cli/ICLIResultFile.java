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
package com.samsung.sec.dexter.executor.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.samsung.sec.dexter.core.defect.Defect;

public interface ICLIResultFile {

	void writeXml2ResultFilePostfix(final File file) throws IOException;

	void writeXmlResultFilePostfix(final File file) throws IOException;

	void writeJsonResultFilePostfix(final File file) throws IOException;

	void writeXml2ResultFilePrefix(final File file) throws IOException;

	void writeXmlResultFilePrefix(final File file) throws IOException;

	void writeJsonResultFilePrefix(final File file) throws IOException;

	void writeJsonResultFileBody(final File file, final List<Defect> allDefectList) throws IOException;

	void writeXmlResultFileBody(final File file, final List<Defect> allDefectList, final String sourceFileFullPath) throws IOException;

	void writeXml2ResultFileBody(final File file, final List<Defect> allDefectList, final String sourceFileFullPath) throws IOException;

}
