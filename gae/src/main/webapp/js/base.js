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
			document.getElementById('authedGreeting').disabled = false;
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
		document.getElementById('authedGreeting').disabled = true;
	}
};


print = function(greeting) {
	var element = document.createElement('div');
	element.classList.add('row');
	element.innerHTML = greeting.message;
	document.getElementById('outputLog').appendChild(element);
};

getCachedSearches = function() {
	gapi.client.cloudapi.googleServices.getCachedSearches(loggedInUserId).execute(function(resp) {
		if (!resp.code) {
			resp.items = resp.items || [];
			clearElement(document.getElementById('cacheOutput'));
			for (var i = 0; i < resp.items.length; i++) {
				printCache(resp.items[i]);
			}
		}
	});
};

multiplyGreeting = function(greeting, times) {
	gapi.client.cloudapi.googleServices.multiply({
		'message' : greeting,
		'times' : times
	}).execute(function(resp) {
		if (!resp.code) {
			print(resp);
		}
	});
};

authedGreeting = function(id) {
	gapi.client.cloudapi.googleServices.authed().execute(function(resp) {
		print(resp);
	});
};

printCalendarEvent = function(event) {
	var element = document.createElement('div');
	element.classList.add('row');
	element.innerHTML = event;
	document.getElementById('outputLog').appendChild(element);
};

getCalendar = function() {
	gapi.client.cloudapi.googleServices.getCalendar().execute(function(resp) {
		if (!resp.code) {
			resp.items = resp.items || [];
			for (var i = 0; i < resp.items.length; i++) {
				printCalendarEvent(resp.items[i]);
			}
		}
	});
};

printSearchResult = function(result) {
	var element = document.createElement('div');
	element.classList.add('row');
	element.innerHTML = '<a href="'+result.url+'">'+result.title+'</a>';
	document.getElementById('searchOutput').appendChild(element);
};

search = function(query) {
	gapi.client.cloudapi.googleServices.search({
		'query' : query
	}).execute(function(resp) {
		if (!resp.code) {
			resp.items = resp.items || [];
			getCachedSearches();
			clearElement(document.getElementById('searchOutput'));
			for (var i = 0; i < resp.items.length; i++) {
				printSearchResult(resp.items[i]);
			}
		}
	});
};

printCache = function(result) {
	var element = document.createElement('div');
	element.classList.add('row');
	element.innerHTML = result.query;
	document.getElementById('cacheOutput').appendChild(element);
};

printUsername = function(user) {
	var element = document.createElement('div');
	element.innerHTML = user.name;
	document.getElementById('username').appendChild(element);
};

enableButtons = function() {
	document.getElementById('multiplyGreetings').onclick = function() {
		multiplyGreeting(document
				.getElementById('greeting').value, document
				.getElementById('count').value);
	}

	document.getElementById('authedGreeting').onclick = function() {
		authedGreeting();
	}

	document.getElementById('signinButton').onclick = function() {
		auth();
	}

	document.getElementById('getCalendar').onclick = function() {
		getCalendar();
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
	// Loads the OAuth and helloworld APIs asynchronously, and triggers login
	// when they have completed.
	var apisToLoad;
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
