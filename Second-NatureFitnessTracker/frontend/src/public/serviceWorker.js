const STATIC_CACHE_NAME = 'fitness-static-v10';

const CORE_ASSETS = [
  "/signin",
  // "/js/printCache.js",
  // "/manifest.webmanifest",
  "/favicon.ico",
  "/img/manifest-icon-192.maskable.png",
  "/@vite/client",
  "/@react-refresh",
  "/node_modules/vite/dist/client/env.mjs",
  "/src/SignIn.css",
  "/src/db.js",
  "/src/SignUp.css",
  "/src/model/ExercisePlan.js",
  "/src/component/GenericOfflineCard.jsx",
  "/src/ActivityGraph.css",
  "/src/component/PlanInfo.jsx",
  "/src/ActivePlan.css",
  "/src/component/ActivityForm.jsx",
  "/src/component/ActivityBox.jsx",
  "/src/dao/HTTPClient.js",
  "/src/component/ProgressMeasure.jsx",
  "/src/ProfileMenu.css",
  '/src/component/OfflinePage.jsx',
  '/'
]

const frontendRoutes = [
  '/signin',
  '/signup',
  '/',
  '/ActiveSession',
  '/log',
  '/Plan',
  '/Metrics'
];

function log(...data) {
    console.log("SWv2.0", ...data);
}

function fetchAndCache(request) {
    return fetch(request)
    .then(response => {
      if (response.ok && request.method === 'GET') {
        caches.open(STATIC_CACHE_NAME)
          .then(cache => {
            cache.put(request, response.clone())
                .then(() => {
                    log('Successfully cached:', request.url);
                })
                .catch(err => {
                    log('Failed to cache:', request.url, err);
                });
          });
      }
      return response.clone();
    })
}

function determineOfflineResponse(request, caches) {
  const requestURL = new URL(request.url);

  if(requestURL.pathname.startsWith("/api")) {
    log("Constructing offline api response for:", requestURL);
    return constructOfflineAPIResponse();
  }

  if(requestURL.pathname.includes('.jsx')) {
    log('Returning jsx for:', requestURL);
    return caches.match('/src/component/OfflinePage.jsx');
  }

  if(requestURL.pathname.includes('/Plan')) {
    log('Returning text/html for:', requestURL);
    return caches.match('/');
  }

  throw new Error("Could not determine an offline response");
}
  
function cacheFirst(request) {
  const url = request.url;

  return caches.match(request)
    .then(response => {
      if (response !== undefined) {
        return response;
      }

      return fetchAndCache(request);
    })
    .catch(() => {
      return determineOfflineResponse(request, caches);
    });
}

function networkFirst(request) {
  return fetchAndCache(request).catch(err => {
    return caches.match(request);
  }).then(response => {
    log('Fetch has failed. Checking the cache...');

    if(response) {
      return response;
    }

    
    return determineOfflineResponse(request, caches);
  }).catch(error => {
    return determineOfflineResponse(request, caches);
  })
}


log("SW Script executing - adding event listeners");

function constructOfflineAPIResponse() {
  const body = {
    error: "Offline"
  }

  const blob = new Blob([JSON.stringify(body)], {
    type: 'application/json'
  });

  const resp = new Response(blob, {
    status: 404,
    statusText: "Not Found",
    headers: {
      "Content-Type": "application/json"
    }
  });

  return resp;
}

self.addEventListener("install", event => {
    log('install', event);

    event.waitUntil(
      caches.open(STATIC_CACHE_NAME)
      .then(cache => {
        log('Caching static assets');
        return cache.addAll(CORE_ASSETS);
      })
    );
});

self.addEventListener("activate", event => {
    log('activate', event);
    event.waitUntil(
        caches.keys()
        .then(cacheNames => {
          return cacheNames.filter(cacheName => cacheName.startsWith('fitness-static-') && cacheName != STATIC_CACHE_NAME);
        })
        .then(oldCaches => {
          return Promise.all(
            oldCaches.map(cacheName => caches.delete(cacheName))
          );
        })
    );
});

self.addEventListener('fetch', event => {
  const requestURL = new URL(event.request.url);
  if(("http://csc342-519-host.csc.ncsu.edu" === requestURL.origin
      || "https://csc342-519.csc.ncsu.edu" === requestURL.origin)
      && requestURL.pathname.startsWith("/api")
  ) {
    event.respondWith(
      networkFirst(event.request)
    )
    return;
  }

  event.respondWith(
      cacheFirst(event.request)
  );
});

self.addEventListener('message', event => {
    log('message', event.data);
    if(event.data.action === 'skipWaiting') {
      self.skipWaiting();
    }
});


self.addEventListener('push', event => {
  log('push', event);

  const message = event.data.json();

  event.waitUntil(
    self.registration.showNotification(message.title, {
      body: message.data,
      icon: `./img/manifest-icon-192.maskable.png`
    })
  );
})

const periodicSyncCb = new Map();

periodicSyncCb.set('post-latest-plans', () => {
  return new Promise((resolve, _) => {
    log('Checking for unsynced plans...');
    resolve();
  })
})

self.addEventListener('periodicsync', event => {
  log('Received a periodic sync event');

  const cb = periodicSyncCb.get(event.tag);
  if(!cb) {
    log('No callback associated with this tag:', event.tag);
    return;
  }

  log('Executing periodic callback');
  event.waitUntil(cb(event));
})