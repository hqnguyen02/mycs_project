import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import SignIn from './SignIn.jsx';
import SignUp from './SignUp.jsx';
import MainDashboard from './component/MainDashboard.jsx'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { createTheme, responsiveFontSizes, ThemeProvider } from '@mui/material'
import InfoMenu from './component/InfoMenu.jsx'
import { ActivityGraph } from './component/ActivityGraph.jsx'
import NavBar from './component/NavBar.jsx'
import ShareMenu from './component/ShareMenu.jsx'
import { ActivePlanPage } from './component/ActivePlan.jsx';
import CreatePlanPage from './component/CreatePlan.jsx';
import EditPlanPage from './component/EditPlan.jsx';

let theme = createTheme();
theme = responsiveFontSizes(theme);


createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <Routes>
          <Route path="/signin" element={<SignIn />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/" element={<NavBar />}>
            <Route index element={<MainDashboard />} />
            <Route path="/ActiveSession" element={<ActivePlanPage />} />
            <Route path="log" element={<ActivityGraph />} />

            <Route path="Plan" >
                <Route path="Create" element={<CreatePlanPage />} />

                <Route path=":planId">
                  <Route index element={<InfoMenu />} />
                  <Route path="Share" element={<ShareMenu />} />
                  <Route path="Edit" element={<EditPlanPage />} />
                </Route>
            </Route>
            <Route path="Metrics" element={<ActivityGraph />} />

          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  </StrictMode>,
)

function registerServiceWorker() {
  if (!navigator.serviceWorker) { // Are SWs supported?
    return;
  }
  navigator.serviceWorker.register('/serviceWorker.js')
    .then((registration) => {
      if (registration.installing) {
        console.log('Service worker installing');
      } else if (registration.waiting) {
        console.log('Service worker installed, but waiting');
        newServiceWorkerReady(registration.waiting);
      } else if (registration.active) {
        console.log('Service worker active');
      }
      registration.addEventListener('updatefound', () => { //This is fired whenever registration.installing gets a new worker
        console.log("SW update found", registration, navigator.serviceWorker.controller);
        newServiceWorkerReady(registration.installing);
      });
    })
    .catch(error => {
      console.error(`Registration failed with error: ${error}`);
    });
  navigator.serviceWorker.addEventListener('message', event => {
    console.log('Message from service worker:', event.data);
  });
  // Ensure refresh is only called once.
  // This works around a bug in "force update on reload" in dev tools.
  let refreshing = false;
  navigator.serviceWorker.addEventListener('controllerchange', () => {
    if(refreshing) return;
    window.location.reload();
    refreshing = true;
  });

};

function newServiceWorkerReady(worker) {
  const popup =  document.createElement('div');
  popup.className = "popup";
  popup.innerHTML = '<div>New Version Available</div>';

  const buttonOk = document.createElement('button');
  buttonOk.innerHTML = 'Update';
  buttonOk.addEventListener('click', e => {
    worker.postMessage({action: 'skipWaiting'});
  });
  popup.appendChild(buttonOk);

  const buttonCancel = document.createElement('button');
  buttonCancel.innerHTML = 'Dismiss';
  buttonCancel.addEventListener('click', e => {
    document.body.removeChild(popup);
  });
  popup.appendChild(buttonCancel);

  document.body.appendChild(popup);
}

registerServiceWorker();

function registerPeriodicNewPlanSync() {
  console.log("Attempting to register a periodic sync event...");

  navigator.serviceWorker.ready.then(registration => {
    return registration.periodicSync.register("post-latest-plans", {
      minInterval: 12 * 60 * 60 * 1000,
    });
  }).then(() => {
    console.log('Registered the post-latest-plans tag')
  }).catch(error => {
    console.error("Periodic Sync could not be registered!: ", error);
  });
}

registerPeriodicNewPlanSync();