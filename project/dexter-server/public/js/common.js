function checkBrowser(){
	var browser = get_browser();
	var browser_version = get_browser_version();
	//console.log("checkBrowser");

	if ("MSIE" == browser && browser_version < 10) {
		var currentLocation = window.location;
		window.location = "/error/NotSupportedBrowser.html?url=" + currentLocation;
	}
}

function get_browser() {
	var N = navigator.appName, ua = navigator.userAgent, tem;
	var M = ua
		.match(/(opera|chrome|safari|firefox|msie|rv)(\/|:)?\s*(\.?\d+(\.\d+)*)/i);
	if (M && (tem = ua.match(/version\/([\.\d]+)/i)) != null)
		M[2] = tem[1];

	if (N == 'Netscape' && M[1] == 'rv') {
		M[1] = 'MSIE';
		M[2] = M[3];
	} else if( N === 'Microsoft Internet Explorer'){
		M[1] = 'MSIE';
		M[2] = M[3];
	}

	M = M ? [ M[1], M[2] ] : [ N, navigator.appVersion, '-?' ];
	return M[0];
}

function get_browser_version() {
	var N = navigator.appName, ua = navigator.userAgent, tem;
	var M = ua
		.match(/(opera|chrome|safari|firefox|msie|rv)(\/|:)?\s*(\.?\d+(\.\d+)*)/i);
	if (M && (tem = ua.match(/version\/([\.\d]+)/i)) != null)
		M[2] = tem[1];

	if (N == 'Netscape' && M[1] == 'rv') {
		M[1] = 'MSIE';
		M[2] = M[3];
	} else if( N === 'Microsoft Internet Explorer'){
		M[1] = 'MSIE';
		M[2] = M[3];
	}

	M = M ? [ M[1], M[2] ] : [ N, navigator.appVersion, '-?' ];

	return M[1];
}


function isHttpResultOK(result) {
	return (result.data && result.data.status && result.data.status == 'ok');
}

