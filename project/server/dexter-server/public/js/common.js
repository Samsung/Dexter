function checkBrowser(){
	var browser = get_browser();
	var browser_version = get_browser_version();
	//alert(navigator.userAgent);
	//console.log("checkBrowser");
	
	if ("MSIE" == browser) {
		window.location = "/error/NotSupportedBrowser.html";
	}
}

function get_browser() {
	var N = navigator.appName, ua = navigator.userAgent, tem;
	var M = ua
			.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
	if (M && (tem = ua.match(/version\/([\.\d]+)/i)) != null)
		M[2] = tem[1];
	M = M ? [ M[1], M[2] ] : [ N, navigator.appVersion, '-?' ];
	return M[0];
}

function get_browser_version() {
	var N = navigator.appName, ua = navigator.userAgent, tem;
	var M = ua
			.match(/(opera|chrome|safari|firefox|msie)\/?\s*(\.?\d+(\.\d+)*)/i);
	if (M && (tem = ua.match(/version\/([\.\d]+)/i)) != null)
		M[2] = tem[1];
	M = M ? [ M[1], M[2] ] : [ N, navigator.appVersion, '-?' ];
	return M[1];
}

