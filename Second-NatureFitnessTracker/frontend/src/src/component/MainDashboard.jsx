import { Card, LinearProgress, Typography, Box, Container, IconButton, Button, Snackbar } from "@mui/material"
import { useEffect, useState } from "react";
import { Link } from "react-router";

import EditIcon from '@mui/icons-material/Edit';
import ShareIcon from '@mui/icons-material/Share';
import InfoIcon from '@mui/icons-material/Info';
import StartIcon from '@mui/icons-material/Start';
import PlayCircleFilledIcon from '@mui/icons-material/PlayCircleFilled';
import ExercisePlan from "../model/ExercisePlan";
import PlanDAO from "../dao/PlanDAO";
import OfflineCard from "./GenericOfflineCard";
import { isOffline } from "../offline-util";
import UserDAO from "../dao/UserDao";

function MainDashboard() {
  const [availablePlans, setAvailablePlans] = useState([]);
  const [activePlan, setActivePlan] = useState(undefined);
  const [snackbarOpen, setSnackBarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");

  const [offlineActivePlan, setOfflineActivePlan] = useState(false);
  const [offlinePlans, setOfflinePlans] = useState(false);

  function activateSession(id) {
    console.log(`Starting Session: ${id}...`);
    PlanDAO.startPlan(id).then(plan => {
      setActivePlan(plan);
    }).catch(error => {
      console.log('is off', isOffline(error));
      openSnackbar("Failed to add active exercise plan");
    });
  }

  function openSnackbar(message) {
    setSnackbarMessage(message);
    setSnackBarOpen(true);
  }

  function closeSnackbar() {
    setSnackBarOpen(false);
  }

  function IconLink({className, href, children}) {
    return (
      <IconButton className={className}>
        <Link to={href}>
          {children}
        </Link>
      </IconButton>
    )
  }

  function ActivePlanContainer() {
    return (
      <Card className="active GenericContainer">
        <Typography variant="h3">{activePlan.title}</Typography>
        <p>{activePlan.activities[0].name}</p>
        <Box className="grid">
          <LinearProgress sx={{height: "2em"}} variant="determinate" value="70" />
          <IconLink className="icon centered" href="/ActiveSession">
            <StartIcon />
          </IconLink>
        </Box>
      </Card>
    )
  }

  function AvailablePlanContainer({title, time}) {
    const id = title;

    return (
      <Card className="GenericContainer" >
        <Box className="planHeader">
          <Typography variant="h3">{title}</Typography>
          <p className="timeEstimate">{time}</p>
        </Box>

        <Box className="buttonset">
          <IconLink href={`/Plan/${id}/Edit`}>
            <EditIcon />
          </IconLink>
          <IconLink href={`/Plan/${id}/Share`}>
            <ShareIcon />
          </IconLink>
          <IconLink href={`/Plan/${id}`}>
            <InfoIcon />
          </IconLink>
          {<IconButton onClick={() => activateSession(id)}>
            <PlayCircleFilledIcon />
          </IconButton>}
        </Box>
      </Card>
    )
  }

  function PlanContainerList() {
    if(offlinePlans) {
      return <OfflineCard />
    }

    return (
      <>
        {...availablePlans}
      </>
    )
  }

  function ActivePlanView() {
    if(offlineActivePlan) {
      return <OfflineCard />
    }

    return (
      <>
        {activePlan && <ActivePlanContainer />}
        {!activePlan && <Card className="GenericContainer"> No active exercise plan to display</Card>}
      </>
    )
  }

  function subscribePush() {
    if (!navigator.serviceWorker) { // Are SWs supported?
      return;
    }

    navigator.serviceWorker.ready.then(registration => {
      console.log('Service worker ready');
      
      return registration.pushManager.getSubscription().then(subscription => {
        console.log("Check if subscriptions if found");

        if (subscription) { 
            return subscription;
        } // If a subscription was found, return it.

        const VAPID_PUBLIC_KEY = 'BLR2Tj0jvSefBdZQnp2w9gyrc7F1Wmx1HLdsSDhi_1ESIk02awOFJVMZ8hrTAYsb1p5aIULFjlW5IqbUnVzQXsc'

        return registration.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: VAPID_PUBLIC_KEY
          });
        });
    }).then(subscription => { // Now we have a subscription, let's send it to the server
      // fetch('./api/subscribetopush', {
      //   method: 'POST',
      //   headers: { 'Content-type': 'application/json' },
      //   body: JSON.stringify({
      //       subscription: subscription //The subscription object for this client
      //   }),
      // });

      console.log('Attempting to subscribe...');

      UserDAO.subscribePush(subscription).catch(error => {
        if(isOffline(error)) {
          openSnackbar("Unable to subscribe to push notifications while offline");
        }
      })
    }).catch(error => { 
      console.error('Failed to subscribe the user: ', error); 
    });
  }

  useEffect(() => {
    
    // Fetch the list of available plans
    const plansPromise = PlanDAO.getUserPlans();
    const activePlanPromise = PlanDAO.getActivePlan();

    // Populate a list of available plans
    plansPromise.then(data => {
      const plans = [];

      data.plans.forEach((plan, index) => {
        const planInstance = new ExercisePlan(plan.title);
        planInstance.setActivities(plan.activities);

        plans.push(<AvailablePlanContainer title={planInstance.title} time={planInstance.timeToString()} />);
      })

      setAvailablePlans(plans);
    }).catch(error => {
      setOfflinePlans(isOffline(error));
    });

    // Set the active plan if available
    activePlanPromise.then(activePlan => {
      if(activePlan) {
        setActivePlan(activePlan.activePlan);
      }
    }).catch(error => {
      setOfflineActivePlan(isOffline(error));
    })

  }, [])

  function promptNotification() {
    if("granted" === Notification.permission) {
      subscribePush();
      openSnackbar("Permission for notifications is already granted"); 
      return;
    }

    Notification.requestPermission().then((result) => {
      console.log(result);
      if (result === "granted") {
        openSnackbar("Permission for notifications has been granted"); 
        subscribePush()
      } else {
        openSnackbar("Failed to get permission for notifications");
      }
    });
  }

  return (
    <>
      <Container>
        <h1 className="pageHeader" variant="h1">My Dashboard</h1>

        <Button variant="contained" onClick={promptNotification}>Notify Me</Button>


        <Box className="section">
          <Typography variant="h2">Active Exercise Plan</Typography>
          
          <ActivePlanView />
        </Box>

        <Box className="section">
          <Typography variant="h2">Available Exercise Plans</Typography>

          <PlanContainerList />
        </Box>

        <Snackbar
          open={snackbarOpen}
          autoHideDuration={2000}
          onClose={closeSnackbar}
          message={snackbarMessage}
        />
      </Container>
    </>
  )
}

export default MainDashboard
