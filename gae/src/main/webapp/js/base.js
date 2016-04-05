CLIENT_ID = '396486651427-6oja5nn04gfo35d7e8s2iegu37mnqsjf.apps.googleusercontent.com';
SCOPES = 'https://www.googleapis.com/auth/userinfo.email';
signedIn = false;
var loggedInUserId;

userAuthed = function() {
	var request = gapi.client.oauth2.userinfo.get().execute(function(resp) {
		if (!resp.code) {
			signedIn = true;
			printUsername(resp);
			loggedInUserId = resp.id;
			document.getElementById('signinButton').innerHTML = 'Sign out';
			document.getElementById('searchVideos').disabled = false;
			document.getElementById('search').disabled = false;
			getSearches();
		}
	});
};


signin = function(mode, callback) {
	gapi.auth.authorize({
		client_id : CLIENT_ID,
		scope : SCOPES,
		immediate : mode
	}, callback);
};

auth = function() {
	if (!signedIn) {
		signin(false,
				userAuthed);
	} else {
		signedIn = false;
		document.getElementById('signinButton').innerHTML = 'Sign in';
		clearElement(document.getElementById('username'));
		document.getElementById('searchVideos').disabled = true;
		document.getElementById('search').disabled = true;
	}
};

search = function(query) {
	gapi.client.cloudapi.googleServices.search({
		'query' : query
	}).execute(function(resp) {
		if (!resp.code) {
			resp.items = resp.items || [];
			clearElement(document.getElementById('searchOutput'));
			for (var i = 0; i < resp.items.length; i++) {
				printSearchResult(resp.items[i]);
			}
			getSearches();
		}
	});
};

printSearchResult = function(result) {
	var element = document.createElement('div');
	element.classList.add('row');
	element.innerHTML = '<a href="'+result.url+'">'+result.title+'</a>';
	document.getElementById('searchOutput').appendChild(element);
};

getSearches = function() {
	gapi.client.cloudapi.googleServices.getSearches({}).execute(function(resp) {
		if (!resp.code) {
			resp.items = resp.items || [];
			clearElement(document.getElementById('searches'));
			for (var i = 0; i < resp.items.length; i++) {
				printSearchQuery(resp.items[i]);
			}
		}
	});
};

printSearchQuery = function(result) {
	var tr = document.createElement('tr');
	var td1 = document.createElement('td');
	td1.appendChild(document.createTextNode(result.query));
    tr.appendChild(td1);
    var td2 = document.createElement('td');
	td2.appendChild(document.createTextNode(result.fromCache));
    tr.appendChild(td2);
    var td3 = document.createElement('td');
	td3.appendChild(document.createTextNode(result.duration));
    tr.appendChild(td3);
	document.getElementById('searches').appendChild(tr);
};

printUsername = function(user) {
	var element = document.createElement('div');
	element.innerHTML = user.name;
	clearElement(document.getElementById('username'));
	document.getElementById('username').appendChild(element);
};

searchYoutube = function(query) {
	gapi.client.cloudapi.googleServices.searchYoutube({
		'query' : query
	}).execute(function(resp) {
		if (!resp.code) {
			resp.items = resp.items || [];
			clearElement(document.getElementById('videos'));
			for (var i = 0; i < resp.items.length; i++) {
				printVideo(resp.items[i]);
			}
		}
	});
};

printVideo = function(result) {
	var element = document.createElement('div');
	element.classList.add('video-element');
	element.innerHTML =
		'<a href="'+result.url+'" target="_blank"><img border="0" src="'+result.thumbnailUrl+'" width="100" height="100"></a>'
		+ '<p>' + result.title + '</p>';
	document.getElementById('videos').appendChild(element);
};

enableButtons = function() {
	document.getElementById('signinButton').onclick = function() {
		auth();
	}

	document.getElementById('searchVideos').onclick = function() {
		searchYoutube(document.getElementById('videoQuery').value);
	}

	document.getElementById('search').onclick = function() {
		search(document.getElementById('query').value);
	}
};

clearElement = function(element) {
	while (element.firstChild) {
		element.removeChild(element.firstChild);
	}
}

initApp = function(apiRoot) {
	var apisToLoad;
	document.getElementById('searchVideos').disabled = true;
	document.getElementById('search').disabled = true;
	var callback = function() {
		if (--apisToLoad == 0) {
			enableButtons();
			signin(true,
					userAuthed);			
		}
	}

	apisToLoad = 2; // must match number of calls to gapi.client.load()
	gapi.client.load('cloudapi', 'v1', callback, apiRoot);
	gapi.client.load('oauth2', 'v2', callback);
};
