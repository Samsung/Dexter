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
namespace DexterCS
{
    public static class ResultFileConstant
    {
        internal static readonly string PROJECT_NAME = "projectName";
        internal static readonly string PROJECT_FULL_PATH = "projectFullPath";
        internal static readonly string FILE_TYPE = "FILE";
        internal static readonly string FOLDER_TYPE = "FOLDER";
        internal static readonly string PROJECT_TYPE = "PROJECT";
        internal static readonly string SNAPSHOT_TYPE = "SNAPSHOT";
        internal static readonly string FILE_NAME = "fileName";
        internal static readonly string MODULE_PATH = "modulePath";
        internal static readonly string SNAPSHOT_ID = "snapshotId";
        internal static readonly string HEADER_DIR = "headerDir";
        internal static readonly string SOURCE_DIR = "sourceDir";
        internal static readonly string FULL_FILE_PATH = "fullFilePath";
        internal static readonly string GROUP_ID = "groupId";
        internal static readonly string DEFECT_COUNT = "defectCount";
        internal static readonly long DEFAULT_GROUPT_IP = 1;
        internal static readonly string DEFECT_LIST = "defectList";
        internal static readonly string RESULT_FILE_EXTENSION = ".json";
        internal static readonly string RESULT_FILE_PREFIX = "result_";
    }
}