/*
 **********************************************************************************************************************
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
 * -------------------------------------------------------------------------------------------------------------------
 * Version : 0.10.4
 * -------------------------------------------------------------------------------------------------------------------
 * Add Key Assignments :
 *   - markDexterDefectToFalseAlarm() : to mark defect status as a false alarm  (async: 3~5 sec)
 *   - runDexterForCurrentFile() : to execute static anlaysis without eding and saving a file
 **********************************************************************************************************************
*/

// INITIALIZE GLOBAL VARIABLES
macro initDexterGlobalVariables()
{
	if(g_dexterConfig != "g_dexterConfig"){
		stop;
	}

	global g_dexterRunning;
	global g_dexterConfig;			// property for dexter configuration such as dexter home, src/header dirs, etc.
	global g_updateCount;			// current updated count for the statusbar
	global g_dexterInitialized;		// 1: initialized,  0: not initialized 
	global g_maxUpdateCount;		// the count of the statusbar's update that used for checking the result of Dexter
	global g_dexterPrefix;			// Prefix string for tooltip of bookmark
	global g_lastFinished;			// last static analysis time(filename:long value)
	global g_showResultMsgDialog;	// 1: show,  0: hide
	global g_waitDexterResult;		// 1: wait(synchronously),  0: no wait(asynchronously)
	global g_analysisWhenOpen;		// 1: analysis when open, 0: not analysis when open a file(default)
	global g_functionList;

	g_dexterRunning = 1;
	g_maxUpdateCount = 6;
	g_updateCount = 0;
	g_dexterInitialized = 0;
	g_dexterPrefix = "[Dexter] ";
	g_lastFinished = "";
	g_showResultMsgDialog = 0;
	g_waitDexterResult = 0;
	g_analysisWhenOpen = 0;
	g_functionList = "";

	g_dexterConfig.sourceDirRegKey = "sourceDirRegKey"
	g_dexterConfig.headerDirRegKey = "headerDirRegKey"
	g_dexterConfig.dexterHomeRegKey = "dexterHomeRegKey"
	g_dexterConfig.dexterInstallationPathRegKey = "dexterInstallationPathRegKey"
	g_dexterConfig.sourceEncodingRegKey = "sourceEncodingRegKey"

	g_dexterRunning = GetReg("g_dexterRunning");
	if(g_dexterRunning == nil){
		g_dexterRunning = 1;
	}
	
	g_showResultMsgDialog = GetReg("g_showResultMsgDialog");
	if(g_showResultMsgDialog == nil){
		g_showResultMsgDialog = 0;
	}
	
	g_waitDexterResult = GetReg("g_waitDexterResult");
	if(g_waitDexterResult == nil){
		g_waitDexterResult = 0;
	}

	g_analysisWhenOpen = GetReg("g_analysisWhenOpen");
	if(g_analysisWhenOpen == nil){
		g_analysisWhenOpen = 0;
	}
}

//===================================================
// EVENTS
//===================================================
event AppStart()
{
	initDexter(); 
}

event AppShutdown()
{
	if(g_dexterConfig == "g_dexterConfig"){
		stop;
	}
	
	closeDexter();
}

event ProjectOpen(sProject)
{
	initDexter();
	
	g_dexterConfig.projectName = getCurrentProjectName();
	g_dexterConfig.sourceDir = "";
	g_dexterConfig.headerDir = "";

	SetReg(g_dexterConfig.sourceDirRegKey, "");
	SetReg(g_dexterConfig.headerDirRegKey, "")
}

event StatusbarUpdate(sMessage)
{
	if(g_dexterInitialized != 1){
		return;
	}
	if(g_dexterRunning != 1){
		return;
	}

	if(sMessage[0] != "L"){
		return;
	}

	addBookmarkToCurFile();
	g_updateCount = 0;
}
event DocumentOpen(sFile)
{
	if(g_dexterInitialized != 1 || g_dexterRunning != 1){
		stop;
	}

	if(g_analysisWhenOpen == 1){
		runDexter(sFile);
	}
	stop;
}

event DocumentSaveComplete(sFile) // synchronously 
//event DocumentChanged(sFile)  // asynchronously
{

	runDexter(sFile);
	stop;
}


//===================================================
//  MACROS - INITIALIZE
//===================================================
macro initDexter()
{
	initDexterGlobalVariables();

	initDexterHome();
	if(g_dexterConfig.dexterHome == nil){
		error("g_dexterConfig.dexterHomeRegKey is null in initDexter()");
		stop;
	}

	g_dexterConfig.sourceEncoding = GetReg("sourceEncodingRegkey");
	if(g_dexterConfig.sourceEncoding == "nil" || g_dexterConfig.sourceEncoding == ""){
		g_dexterConfig.sourceEncoding = "UTF-8";
		SetReg("sourceEncodingRegkey", "UTF-8");
	}

	g_dexterConfig.sourceEncoding = GetReg(g_dexterConfig.sourceEncodingRegKey)

	dexterPath = GetReg(g_dexterConfig.dexterInstallationPathRegKey);
	if(dexterPath != nil && dexterPath != "dexterPath"){
		g_dexterDaemon = GetReg("g_dexterDaemon");
		if(g_dexterDaemon == nil || g_dexterDaemon == "g_dexterDaemon" || g_dexterDaemon == 0){
			RunCmdLine(dexterPath # "\\dexter.exe", dexterPath, 0);
		}
	} else {
		error("Dexter Initialized failed");
		Msg("You have to run Dexter Daemon first. If you didn't install, please install it first.");
		stop;
	}

	g_dexterConfig.sourceDir = GetReg(g_dexterConfig.sourceDirRegKey)
	g_dexterConfig.headerDir = GetReg(g_dexterConfig.headerDirRegKey)

	g_dexterInitialized = 1;
	info("Dexter Initialized successfully");

	//removeDexterConfigReg()  // use this for debugging	
}

macro initDexterHome()
{
	g_dexterConfig.dexterHome = GetReg(g_dexterConfig.dexterHomeRegKey)
}

macro closeDexter()
{
	SetReg("g_showResultMsgDialog", g_showResultMsgDialog);
	SetReg("g_waitDexterResult", g_waitDexterResult);
	SetReg("g_dexterRunning", g_dexterRunning);
	SetReg("g_analysisWhenOpen", g_analysisWhenOpen);
	closeLogFile();
}

//===================================================
//  MAIN MACROS - for shortcuts
//===================================================
/* Run Static Analysis with using Dexter */
macro runDexterForCurrentFile()
{
	runDexter(getCurrentFilePath());
}

macro runDexter(sFile)
{
	checkAndStopDexterInitialize();

	if(sFile == nil || sFile == "sFile"){
		stop;
	} 

	if(isSupportedFile(sFile) != 1){
		info("can't run dexter because the file is not supporting : " # sFile);
		stop;
	}

	if(sFile == nil || sFile == "sFile" || isSupportedFile(sFile) != 1 || g_dexterInitialized != 1){
		error("can't run dexter because dexter is not initialized : " # sFile);
		stop;
	}
	saveModifiedFunctionList();
	createDexterConfFile();
}


/* Dexter HOME and Source Encoding Setting : User Asking */
macro setupDexter() 
{
	checkAndStopDexterInitialize();
	
	if(g_dexterConfig.dexterHome == nil) {
		g_dexterConfig.dexterHome = Ask ("Enter Dexter Home Path (eg. C:\\dexter_1.0.3)")
		SetReg(g_dexterConfig.dexterHomeRegKey, g_dexterConfig.dexterHome);
	} else {
		while(1) {
			homeMsg = "Current Dexter home path is " # g_dexterConfig.dexterHome # ". Do you want change the path?  (Y:N)"
			StartMsg(homeMsg)
			answer = GetChar();
			EndMsg();

			if("Y" == toupper(answer)){
				g_dexterConfig.dexterHome = Ask("Enter Dexter Home Path (eg. C:\\dexter_v1.0.3). Previous value is " # g_dexterConfig.dexterHome)
				SetReg(g_dexterConfig.dexterHomeRegKey, g_dexterConfig.dexterHome);
				break;
			} else if("N" == toupper(answer)) {
				break;
			}	
		}
	}

	if(g_dexterConfig.sourceEncoding == nil) {
		g_dexterConfig.sourceEncoding = Ask ("Enter source code encoding (eq. UTF-8)")
		// TODO : check encoding pattern
		SetReg(g_dexterConfig.sourceEncodingRegKey, g_dexterConfig.sourceEncoding)
	} else {
		while(1) {
			encodingMsg = "Current source code encoding is " # g_dexterConfig.sourceEncoding # ". Do you want change Encoding? (Y:N)"
			StartMsg(encodingMsg)
			answer = GetChar();
			EndMsg();

			if("Y" == toupper(answer)) {
				g_dexterConfig.sourceEncoding = Ask ("Enter source code encoding (eq. UTF-8)")
				// TODO : check encoding pattern
				SetReg(g_dexterConfig.sourceEncodingRegKey, g_dexterConfig.sourceEncoding)	
				break;
			} else if("N" == toupper(answer)) {
				break;
			}
		}
	}
}


/* Set Source directory */
macro setDexterSourceDir()
{
	checkAndStopDexterInitialize();
	
	g_dexterConfig.sourceDir = GetReg(g_dexterConfig.sourceDirRegKey)
	g_dexterConfig.headerDir = GetReg(g_dexterConfig.headerDirRegKey)

	ynErrorMsg = "Error: You can enter only 'Y' or 'N'. Macro will be terminated."

	enterMsg = "Enter source directory (eq. C:\\Prj\\src)"
	if(g_dexterConfig.sourceDir == nil) {
		g_dexterConfig.sourceDir = Ask (enterMsg)
		SetReg(g_dexterConfig.sourceDirRegKey, g_dexterConfig.sourceDir)
	} else {
		while(1) {
			qMsg = "Current source directory is " # g_dexterConfig.sourceDir # ". Do you want change Directory? (Y:N)"
			StartMsg(qMsg)
			answer = GetChar();
			EndMsg();

			if("Y" == toupper(answer)) {
				g_dexterConfig.sourceDir = Ask (enterMsg)
				SetReg(g_dexterConfig.sourceDirRegKey, g_dexterConfig.sourceDir)	
				break;
			} else if("N" == toupper(answer)) {
				break;
			}
		}
	}
}

/* Set Header directory */
macro setDexterHeaderDir()
{
	checkAndStopDexterInitialize();
	
	g_dexterConfig.headerDir = GetReg(g_dexterConfig.headerDirRegKey)

	ynErrorMsg = "Error: You can enter only 'Y' or 'N'. Macro will be terminated."

	enterMsg = "Enter header file directory, if you have (eq. C:\\Prj\\inc)"
	qMsg = "none";
	if(g_dexterConfig.headerDir == nil) {
		qMsg = "No header file directory is registered. Do you want add Directory? (Y:N)"
	} else {
		qMsg = "Current header directory is " # g_dexterConfig.headerDir # ". Do you want change Directory? (Y:N:D to delete)"
	}

	while(1) {
		StartMsg(qMsg) 
		answer = GetChar();
		EndMsg();

		if("Y" == toupper(answer)) {
			g_dexterConfig.headerDir = Ask (enterMsg)
			SetReg(g_dexterConfig.headerDirRegKey, g_dexterConfig.headerDir)	
			break;
		} else if("D" == toupper(answer)) {
			g_dexterConfig.headerDir = ""
			SetReg(g_dexterConfig.headerDirRegKey, "")
			break;
		} else if("N" == toupper(answer)) {
			break;
		}
	}
}

macro toggleShowDexterMsgDialog()
{
	if(g_showResultMsgDialog == 0){
		g_showResultMsgDialog = 1;
		Msg("Dexter Result Message Dialog : ON");
	} else {
		g_showResultMsgDialog = 0;
		Msg("Dexter Result Message Dialog : OFF");
	}

	stop;
}

macro toggleWaitForDexterResult()
{
	if(g_waitDexterResult == 0){
		g_waitDexterResult = 1;
		Msg("Waiting Dexter Result(Sync) : ON");
	} else {
		g_waitDexterResult = 0;
		Msg("Waiting Dexter Result(Sync) : OFF");
	}

	stop;
}

macro toggleDexterRunning()
{
	if(g_DexterRunning == 0){
		g_DexterRunning = 1;
		Msg("Dexter Static Analysis : ON");
	} else {
		g_DexterRunning = 0;
		Msg("Dexter Static Analysis : OFF");
	}

	stop;
}

macro toggleAnalysisWhenOpen()
{
	if(g_analysisWhenOpen== 0){
		g_analysisWhenOpen= 1;
		Msg("Dexter Static Analysis When opening a file : ON");
	} else {
		g_waitDexterResult = 0;
		Msg("Dexter Static Analysis When opening a file : OFF");
	}

	stop;
}

macro markDexterDefectToFalseAlarm()
{
	checkAndStopDexterInitialize();
	
	if(g_dexterInitialized != 1){
		stop;
	}

	var hResultFile;
	var resultFilePath;
	var lineStr;
	var resultCount;

	resultFilePath = getResultFilePathForCurrentFile();
	if(resultFilePath == nil || resultFilePath == ""){
		stop;
	}

	hResultFile = OpenBuf(resultFilePath);
	if(hResultFile == hNil){
		error("there is no result file to dismiss : " # resultFilePath);
		stop;
	}
	resultCount = GetBufLineCount(hResultFile) - 1;
	if(resultCount <= 0){
		warn("there is no defect info");
		CloseBuf(hResultFile);
		stop;
	}

	var hCurFile;
	hCurFile = GetCurrentBuf();
	if(hCurFile == hNil){
		Msg("there is no open/target file");
		stop;
	}

	var curLine;
	curLine = GetBufLnCur(hCurFile) + 1;
	
	if(curLine <= 0){
		warn("invalid current line number : " # curLine);
		stop;
	}
	var defect;
	var answer;
	while(--resultCount >= 0){				
		lineStr = GetBufLine(hResultFile, resultCount);
		defect = splitDefectMessage(lineStr);

		if(defect.line == curLine){
			while(1){
				StartMsg("Do you want to mark the defect as FALSE ALARM? (Y : N) "
					# defect.checkerCode # defect.line);
				answer = GetChar();
				EndMsg();

				if(answer == "Y" || answer == "y"){
					createDefectFilterFile(defect, hCurFile, curLine);
					removeBookmark(defect);
					DelBufLine(hResultFile, resultCount);
					SetBufDirty(hResultFile, True);
					SaveBuf(hResultFile);
					SetBufDirty(hResultFile, True);
					break;
				} else if(answer == "N" || answer == "n"){
					break;
				}
			}
		}
	}
	CloseBuf(hResultFile);
}

//===================================================
//  SUB MACROS - not for shortcuts
//===================================================
macro createDexterConfFile()
{
	if(g_dexterInitialized != 1){
		stop;
	}

	// project handle
	var hprj;
	var projDir;
	var hFile;

	hprj = GetCurrentProj();
	projDir = GetProjDir(hprj);
	hFile = GetCurrentBuf();
	if(hFile == hNil){
		Msg("There is no open/target file to analyze");
		stop;
	}

	var resultFileFullPath;
	resultFileFullPath = getResultFilePathForCurrentFile();

	if(resultFileFullPath == nil || resultFileFullPath == ""){
		Msg("can't make result file full path in createDexterConfFile()");
		stop;
	}

	var hConfbuf;
	var confFile;
	var requestTime;
	var contents;
	var functionList;
	initDexterHome();
	confFile = g_dexterConfig.dexterHome # "\\bin\\daemon\\dexter_daemon_cfg.json"
	hConfbuf = OpenBuf(confFile);

	if(hConfbuf == hNil){	
		hConfbuf = NewBuf(confFile);
		if(hConfbuf == hNil){
			Msg("Invalid Dexter configuration file(Open Error) : " # confFile);
			stop;
		}
	}
	requestTime = getCurrentTimeString();	

	contents = "{";
	contents = contents # "\t\"requestTime\":\"" # requestTime # "\",";
	contents = contents # "\t\"resultFileFullPath\":\"" # resultFileFullPath # "\",";
	contents = contents # "\t\"projectName\":\"" # getCurrentProjectName() # "\",";
	contents = contents # "\t\"projectFullPath\":\"" # projDir # "\",";
	contents = contents # "\t\"sourceEncoding\":\"" # g_dexterConfig.sourceEncoding # "\",";

	if(g_dexterConfig.sourceDir == nil || g_dexterConfig.sourceDir == ""){
//		contents = contents # "\t\"sourceDir\":[],";
	} else {
		contents = contents # "\t\"sourceDir\":[\"" # g_dexterConfig.sourceDir # "\"],";
	}

	if(g_dexterConfig.headerDir == nil || g_dexterConfig.headerDir == ""){
		contents = contents # "\t\"headerDir\":[],";
	} else {
		contents = contents # "\t\"headerDir\":[\"" # g_dexterConfig.headerDir # "\"],";
	}

	contents = contents # "\t\"fileName\":[\"" # GetBufName(hFile) # "\"],";
	contents = contents # "\t\"type\":\"FILE\",";
	contents = contents # "\t\"functionList\":[" # g_functionList # "]";
	contents = contents # "}";

	emptyFileContent(hConfbuf, 0);
	AppendBufLine(hConfbuf, contents);
	SaveBuf(hConfbuf);
	CloseBuf(hConfbuf);
}
macro saveModifiedFunctionList(){
	var firstRevisionLine;
	var changeCount;
	var functionName;
	var curSymbol;
	
	hwnd = GetCurrentWnd();
	hbuf = GetCurrentBuf();

	currentLine  = 0;

	changeCount = 0;
	firstRevisionLine = 0;

	g_functionList= "";
	currentLn = GetWndSelLnFirst (hwnd);
	currentIch =GetWndSelIchFirst(hwnd);
	vert = GetWndVertScroll (hwnd);
	wndsel = GetWndSel(hwnd);
	//msg();
	while(1){
		Go_To_Next_Change;
		if(changeCount == 0){
			firstRevisionLine = GetBufLnCur(hbuf);
		}else if( isNavigatedLine(hbuf,changeCount,firstRevisionLine) == true){
			break;
		}
		changeCount = changeCount +1;

		curSymbol = GetCurSymbol();
		symbolLocation = GetSymbolLocation(curSymbol);
		if( symbolLocation == "" ){
			
		}else if(symbolLocation.Type == "Function" || symbolLocation.Type == "Method"){
			functionName = SymbolLeafName(symbolLocation);
			
			addFunctionNameIfNotExist(functionName);		
		}
	}
	if(g_functionList !=""){
	g_functionList = strtrunc(g_functionList,strlen(g_functionList)-1);	
	}
	SetBufIns(hbuf,currentLn,currentIch);	
	ScrollWndToLine(hwnd,vert);
	
}
macro isNavigatedLine(hbuf, count,firstRevisionLine){
	
	var currentLine;
	currentLine = GetBufLnCur(hbuf);
	if(count>=1 && currentLine == firstRevisionLine){
		return true;
	}
	return false;

}
macro addFunctionNameIfNotExist(functionName){
	var index;
	var functionListLength;
	var compareFunctionName;
	var frontIndex;
	var backIndex;
	
	index = 0;
	frontIndex = 0;
	backIndex = 0;
	functionListLength = strlen(g_functionList);

	while(index<functionListLength){
		if(g_functionList[index] == ","){
				backIndex = index;
				compareFunctionName = strmid(g_functionList,frontIndex+1,backIndex-1);
				if(compareFunctionName == functionName){
				return;
				}				
			frontIndex = index +1;			
		}
		index = index +1;
	}
	
	g_functionList = g_functionList # "\"" #  functionName # "\"" # ",";

}

macro createPlatzKeywordFile()
{
	if(g_dexterInitialized != 1){
		stop;
	}
	var keyword;
	var hFile;

	hFile = GetCurrentBuf();
	if(hFile == hNil){
		Msg("There is no open/target File to search");
		stop;
	}
	keyword = GetKeywordFromCurrentLine(hFile);
	if(keyword == nil){
		Msg("There is no target keyword to search. Please move your insertion cursor and restart search macro");
		stop;
	}
  
	var hKeywordBuf;
	var keywordFile;
	var requestTime;
	var contents;

	initDexterHome();
	keywordFile = g_dexterConfig.dexterHome # "\\bin\\daemon\\platz_keyword.json"
	hKeywordBuf = OpenBuf(keywordFile);

	if(hKeywordBuf == hNil){	
		hKeywordBuf = NewBuf(keywordFile);
		if(hKeywordBuf == hNil){
			Msg("Invalid Platz Keyword file(Open Error) : " # keywordFile);
			stop;
		}
	}
	requestTime = getCurrentTimeString();	

	contents = "{";
	contents = contents # "\t\"keyword\":\"" # keyword # "\"";
	contents = contents # "}";
	emptyFileContent(hKeywordBuf, 0);
	AppendBufLine(hKeywordBuf, contents);
	SaveBuf(hKeywordBuf);
	CloseBuf(hKeywordBuf);
}

macro GetKeywordFromCurrentLine(hbuf)
{
	var currentCursorIndex;
	var current
	var index;
	var currentLineNumber;
	var currentLineText;
	var firstCharIndex;
	var lastCharIndex;
	var keyword;
	
	hwnd = GetCurrentWnd();
	currentLineNumber = GetWndSelLnFirst(hwnd);
	currentLineText = GetBufLine(hbuf,currentLineNumber);
	currentCursorIndex = GetWndSelIchFirst(hwnd);
	index = currentCursorIndex;


	
	if(checkAllowedChar(currentLineText[index]) == 0){
		msg("[Dexter] You have to move your cursor on a character.");
		stop;
	}
	
	firstCharIndex = index;
	while(index--){
		if(checkAllowedChar(currentLineText[index]) == 0){
			firstCharIndex = index+1;
			break;
		}
	}

	index = currentCursorIndex;
	lastCharIndex = index;
	while(index++){
		if(checkAllowedChar(currentLineText[index]) == 0){
			lastCharIndex = index-1;
			break;
		}
	}

	keyword = strmid(currentLineText,firstCharIndex,lastCharIndex+1);
	return keyword;
		
}

macro checkAllowedChar(char){
	var asciiNum;
	if(char ==Nil){
		return 0;
	}
	asciiNum  =  AsciiFromChar(char);
	if(asciiNum == 95 || asciiNum == 36){
		return 1;
	}
	if(asciiNum >= 65 && asciiNum <=90){ 
		return 1;
	}
	if(asciiNum >=97 && asciiNum <=122){
		return 1;
	}
	if(asciiNum >=48 && asciiNum <=57){
		return 1;
	}
	return 0;
	
	
}


macro createDefectFilterFile(defect, hFile, curLine)
{
	var hFilterBuf;
	var filterFile;

	initDexterHome();
	filterFile = g_dexterConfig.dexterHome # "\\filter\\defect-filter.json"

	hFilterBuf = OpenBuf(filterFile);
	
	if(hFilterBuf == hNil){	
		hFilterBuf = NewBuf(filterFile);
		if(hFilterBuf == hNil){
			debug("Invalid Dexter filter file(Open/Create Error) : " # filterFile);
			stop;
		}
	}

	if(!isBufRW(hFilterBuf)){
		debug("buf cannot read write");
		emptyFileContent(hFilterBuf, 1);
	}
	
	var filterContent;
	filterContent = "{";
	filterContent = filterContent # "\t\"fid\":0,";
	filterContent = filterContent # "\t\"isActive\":true,";
	filterContent = filterContent # "\t\"checkerCode\":\"" # defect.checkerCode # "\",";
	filterContent = filterContent # "\t\"className\":\"" # defect.className # "\",";
	filterContent = filterContent # "\t\"methodName\":\"" # defect.methodName # "\",";
	filterContent = filterContent # "\t\"toolName\":\"" # defect.toolName # "\",";
	filterContent = filterContent # "\t\"language\":\"" # defect.language # "\",";
	filterContent = filterContent # "\t\"modulePath\":\"" # defect.modulePath # "\",";
	filterContent = filterContent # "\t\"fileName\":\"" # getCurrentFileName() # "\",";
	filterContent = filterContent # "\t\"line\":\"" # curLine # "\"";
	filterContent = filterContent # "}";
	
	AppendBufLine(hFilterBuf, filterContent);
	SaveBuf(hFilterBuf);
 	CloseBuf(hFilterBuf);
}

macro isSupportedFile(sFile)
{
	length = strlen(sFile) ;
    bPt = length - 3;
    ePt = length;

    if(bPt < 0 || ePt < 0 || bPt > length || ePt > length){
    	return 0;
    }
	ext1 = tolower(strmid(sFile, bPt, ePt));

	length = strlen(sFile);
   	bPt = length - 1;
   	ePt = length;

    if(bPt < 0 || ePt < 0 || bPt > length || ePt > length){
    	return 0;
    }
   	ext2 = tolower(strmid(sFile, bPt, ePt));

	if(ext1 != "cpp" && ext1 != "hpp" && ext2 != "c" && ext2 != "h")
	{
		return 0;
	}

	return 1;
}


/* for Debugging */
macro removeDexterConfigReg()
{
	g_dexterConfig.dexterHome = ""
	g_dexterConfig.sourceEncoding = ""
	g_dexterConfig.sourceDir = ""
	g_dexterConfig.headerDir = ""
	
	SetReg("dexterHomeRegKey", "")
	SetReg("headerDirRegKey", "")
	SetReg("sourceDirRegKey", "")
	SetReg("sourceEncodingRegKey", "")

	stop;
}


/* Return resultFileFullPath for current file. If error, return nil */
macro getResultFilePathForCurrentFile()
{
	checkAndStopDexterInitialize();
	
	var resultFolder;
	var hprj;
	var prjDir;
	var prjName;
	var hFile;
	var fileName;
	var bP;
	var eP;
	var fLength;
	var filePathFromPrj;
	
	resultFolder = g_dexterConfig.dexterHome # "\\result\\daemon\\";

	hprj = GetCurrentProj ();
	if(hprj == hNil){
		return nil;
	}
	
	prjDir = GetProjDir(hprj);
	prjName = GetCurrentProjectName();
	hFile = GetCurrentBuf();

	if(hFile == hNil){
		Msg("cann't open current file to created result file path");
		return nil;
		Stop;
	}
	
	fileName = GetBufName(hFile);

	// for v1.11
	eP = strlen(fileName);
	if(hasPrefix(fileName, prjDir) == 0 && eP > 3){
		if(fileName[1] == ":"){
			bP = 3;
			filePathFromPrj = strmid(fileName, bP, eP);	
			return resultFolder # filePathFromPrj;
		} else {
			return resultFolder # fileName;
		}
	} else {
		bP = strlen(prjDir);
		fLength = eP;
		if(bP < 0 || eP < 0 || bP > fLength || eP > fLength){
			if(fileName[1] == ":"){
				bP = 3;
				filePathFromPrj = strmid(fileName, bP, eP);	
				return resultFolder # filePathFromPrj;
			} else {
				return resultFolder # fileName;
			}
		} else {
			filePathFromPrj = strmid(fileName, bP, eP);
			filePathFromPrj = prjName # filePathFromPrj;
			return resultFolder # filePathFromPrj;
		}
	}
}


macro addBookmarkToCurFile()
{
	checkAndStopDexterInitialize();
	initDexterHome();

	var resultFilePath;
	var hResultFile;
	var currentFilePath;
	
	resultFilePath = getResultFilePathForCurrentFile();
	
	if(resultFilePath == nil || resultFilePath == ""){
		return;
	}
	
	hResultFile = OpenBuf(resultFilePath);
	if(hResultFile != hNil){
		// 2. check if analysis is over
		var errorCount;
		var endLineStr;
		errorCount = GetBufLineCount(hResultFile) - 1; // errorCount -1 : because the last line is for "E|" message
		
		endLineStr = GetBufLine(hResultFile, errorCount);

		if(endLineStr[0] != "E" || g_lastFinished == endLineStr){
			CloseBuf(hResultFile);
			stop;
		}
		g_lastFinished = endLineStr;

		// 3. remove old bookmarks and add new bookmarks for defects
		var hBuf;
		hBuf = GetCurrentBuf();
		if(hBuf != hNil && IsBufRW(hBuf) == False){ // read only file
			Msg("[Dexter] Cannot add bookmark(s) of defects into the current file due to read-only state.");
			stop;
		}

		removeAllDexterBookmarkForCurrentFile();
		currentFilePath = getCurrentFilePath();
		addDexterBookmarks(hResultFile, errorCount, currentFilePath);
		CloseBuf(hResultFile);
		stop;
	} 
}


macro removeAllDexterBookmarkForCurrentFile()
{
	debug("S:removeAllDexterBookmarksForCurrentFile");
	var count;
	var index;
	var bookmark;
	var fileName;

	fileName = getCurrentFilePath();
	
	count = BookmarksCount();
	index = 0;
	while(index < count){
		bookmark = BookmarksItem(index);

		if(bookmark.File == fileName && hasPrefix(bookmark.Name, g_dexterPrefix) == 1){
			BookmarksDelete(bookmark.Name);
			count = BookmarksCount();
		} else {
			index++;
		}
	}
	debug("E:removeAllDexterBookmarksForCurrentFile");
}

macro removeBookmark(defect)
{
	var defectMsg;
	defectMsg = createDefectMessage(defect);
	BookmarksDelete(defectMsg); 
}


macro addDexterBookmarks(hResultFile, errorCount, currentFileName) 
{
	debug("S:addDexterBookmarks");
	var lineMsg;
	var lineStr;
	var defect;
	var totalErrorCount;
	totalErrorCount = errorCount;
	
	lineMsg = "";
	
	while(--errorCount >= 0){				
		lineStr = GetBufLine(hResultFile, errorCount);
		defect = splitDefectMessage(lineStr);

		if(defect.fail != 1){
			var defectMsg;
			defectMsg = createDefectMessage(defect);

			if(defect.line <= 0){
				BookmarksAdd(defectMsg, currentFileName, 1, 1);	
			} else {
				BookmarksAdd(defectMsg, currentFileName, defect.line-1, 1);	
			}
			
			lineMsg = lineMsg # defect.line # " ";
		}
	}

	if(g_showResultMsgDialog == 1) {
		if(totalErrorCount > 0){
			Msg("[Dexter Result] You have " # totalErrorCount # " defect(s) in " # currentFileName # " file. Line(s): " # lineMsg);
		} else {
			Msg("[Dexter Result] You have no defects in " # currentFileName # " file.";
		}
	}

	debug("E:addDexterBookmarks");
	stop;
}

macro createDefectMessage(defect)
{
	var defectMsg;
	defectMsg = g_dexterPrefix # defect.fileName # ":" # defect.severityCode # ":"# defect.checkerCode # ":" # defect.line # ":" # " " # defect.message;
	
	return defectMsg;
}

macro splitDefectMessage(message)
{
	var defect;
	var size;

	size = strlen(message);
	if(message == nil || size == 0){
		defect.fail = 1;
		return defect;
	}

	var index;
	var foundColon;
	var curColumn;
	
	index = 0;
	foundColon = 0;

	defect.line = "";
	defect.checkerCode = "";
	defect.toolName = "";
	defect.language = "";
	defect.modulePath = "";
	defect.fileName = "";
	defect.className = "";
	defect.methodName = "";
	defect.severityCode = "";
	defect.message = "";

	curColumn = 0;
	while(index < size){
		if(message[index] == ":"){
			curColumn ++;
			foundColon = 1;
			index++;
		}

		if(message[index] == ":"){
			continue;
		}
		
		if(curColumn == 0){
			defect.line = defect.line # message[index];
		} else if(curColumn == 1){
			defect.checkerCode = defect.CheckerCode # message[index];
		} else if(curColumn == 2){
			defect.toolName = defect.toolName # message[index];
		} else if(curColumn == 3){
			defect.language = defect.language # message[index];
		} else if(curColumn == 4){
			defect.modulePath = defect.modulePath # message[index];
		} else if(curColumn == 5){
			defect.fileName = defect.fileName # message[index];
		} else if(curColumn == 6){
			defect.className = defect.className # message[index];
		} else if(curColumn == 7){
			defect.methodName = defect.methodName # message[index];
		} else if(curColumn == 8){
			defect.severityCode = defect.severityCode # message[index];
		} else {
			length = strlen(message);
			if(index > length){
				defect.message = "";
			} else {
				defect.message = strmid(message,index, length);
			}
			break;
		}

		index++;
	}

	if(foundColon == 0){
		defect.fail = 1;
		return defect;
	}

	defect.fail = 0;
	return defect;
}	

macro addDefectBookmarkSync()
{
   	var tryCount;
   	var loopCount;
   	
   	tryCount = 500;
   	
    while(--tryCount > 0)
    {   	
    	addBookmarkToCurFile();

		// waiting...
	    loopCount = 10000;
    	while(loopCount-- > 0){	}
    }  

    stop;
}

macro checkAndStopDexterInitialize()
{
	if(g_dexterConfig == nil || g_dexterConfig == "g_dexterConfig"){
		Msg("You have to enable the feature 'Event Handling', then rerun Source Insight: 'Menu > Options > Preferences > General > Enablie event handlers' option. If you enabled, please just rerun Source Insight, again"); 
		stop;
	}
}


//===================================================
// UTILITIES
//===================================================
macro getCurrentFilePath()
{
	var hBuf;
	
	hBuf = GetCurrentBuf();
	if(hBuf == hNil){
		return "";
	} else {
		return GetBufName(hBuf);
	}
}

/* return only current file name without path information */
macro getCurrentFileName()
{
	var curFilePath;
	var reverseName;
	var length;
	var fileName;

	curFilePath = getCurrentFilePath();
	if(curFilePath == nil || curFilePath == ""){
		return "";
	}
	
	fileName = "";
	reverseName = "";
	length = strlen(curFilePath);
	index = length;

	while(--index >= 0){
		if(curFilePath[index] == "\\"){
			break;
		} else {
			reverseName = reverseName # curFilePath[index];
		}
	}

	length = strlen(reverseName);
	index = length;
	while(--index >= 0){
		fileName = fileName # reverseName[index];
	}

	return fileName;
}


/* Return yyyyMMddhhmmssSSS */
macro getCurrentTimeString()
{
	var now;
	var nowStr;
	
	now = GetSysTime(0);

	nowStr = now.Year;

	nowStr = addZeroForDate(nowStr, now.Month);
	nowStr = addZeroForDate(nowStr, now.Day);
	nowStr = addZeroForDate(nowStr, now.Hour);
	nowStr = addZeroForDate(nowStr, now.Minute);
	nowStr = addZeroForDate(nowStr, now.Second);
	nowStr = addZeroForTreeDigits(nowStr, now.Milliseconds);

	return nowStr;
}

/* Return yyyyMMdd */
macro getCurrentDateString()
{
	var now;
	var nowStr;
	
	now = GetSysTime(0);

	nowStr = now.Year;

	nowStr = addZeroForDate(nowStr, now.Month);
	nowStr = addZeroForDate(nowStr, now.Day);

	return nowStr;
}

macro addZeroForDate(baseStr, newStr)
{
	if(newStr < 10){
		baseStr = baseStr # "0" # newStr;
	} else {
		baseStr = baseStr # newStr;
	}

	return baseStr;
}

macro addZeroForTreeDigits(baseStr, newStr)
{
	if(newStr >= 0 && newStr < 10){
		baseStr = baseStr # "0" # newStr;
	} else if(newStr >= 10 && newStr < 100){
		baseStr = baseStr # "00" # newStr;
	} else {
		baseStr = baseStr # newStr;
	}

	return baseStr;
}

/* this macro exists because built-in ClearBuf() doesn't work well */
macro emptyFileContent(hBuf, isSave)
{
	if(hBuf != hNil){
		line = GetBufLineCount(hBuf);

		while(--line >= 0){
			DelBufLine(hBuf, line);
		}

		ClearBuf(hBuf);

		if(isSave == 1){
			SaveBuf(hBuf);
		}
	}
}

/* If has searchStr, return 1, or return 0 */
macro hasPrefix(baseStr, searchStr)
{
	size = strlen(searchStr);

	index = 0;
	while(index < size){
		if(baseStr[index] != searchStr[index]){
			return 0;
		}

		index++;
	}

	return 1;
}


/* return only current project name without path information */
macro getCurrentProjectName()
{
	var hprj;
	var prjName;
	
	hprj = GetCurrentProj();
	if(hprj == hNil){
		return "";
	}
	
	prjName = GetProjName(hprj);

	var index;
	index = strlen(prjName);

	var reverseName;

	while(--index >= 0)
	{
		if(prjName[index] == "\\"){
			break;
		} else {
			reverseName = reverseName # prjName[index];
		}
	}

	var onlyProjectName;

	index = strlen(reverseName);
	while(--index >= 0){
		onlyProjectName = onlyProjectName # reverseName[index];
	}

	return onlyProjectName;
}

macro error(s) 
{ 
	log("[E]", s);
} 

macro warn(s) 
{ 
	log("[W]", s);
}

macro info(s) 
{ 
	log("[I]", s);
}

macro debug(s) 
{ 
	log("[D]", s);
}

macro log(prefix, s)
{
	/*
	checkAndStopDexterInitialize();
	
	var hLogBuf;
	var sLogFile;
	
	sLogFile = g_dexterConfig.dexterHome # "\\log\\sourceinsight.log";
	hLogBuf = GetBufHandle(sLogFile)

	if(hLogBuf == hNil){
		hLogBuf = OpenBuf(sLogFile);
		if(hLogBuf == hNil){
			return;
		}
	}

	AppendBufLine(hLogBuf, prefix # " " # getCurrentTimeString() # " : " # s);
	SaveBuf(hLogBuf);
	*/
}

macro closeLogFile()
{
	sLogFile = g_dexterConfig.dexterHome # "\\log\\sourceinsight.log";
	hLogBuf = GetBufHandle(sLogFile)

	if(hLogBuf != hNil){
		CloseBuf(hLogBuf);
	}
}
