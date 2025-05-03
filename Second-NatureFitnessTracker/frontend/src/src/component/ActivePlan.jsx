import { Card, Typography, Box, Container, IconButton, Button, TextField, Alert } from "@mui/material"
import { useEffect, useState } from "react";
import { redirect } from "react-router"

import '../ActivePlan.css'

import PlanDAO from "../dao/PlanDAO";
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import LoopIcon from '@mui/icons-material/Loop';
import ArrowLeftIcon from '@mui/icons-material/ArrowLeft';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import SportsScoreIcon from '@mui/icons-material/SportsScore';
import { isOffline } from "../offline-util";
import OfflinePage from "./OfflinePage";


export function ActivePlanPage() {

  const [activePlan, setActivePlan] = useState(undefined);
  const [openedActivity, setOpenedActivity] = useState(undefined);

  const [logProgressMsg, setLogProgressMsg] = useState(undefined);
  const [statusLogProgress, setStatusLogProgress] = useState(undefined);

  const [progressMeasure, setProgressMeasure] = useState(0);

  const [offline, setOffline] = useState(false);

  useEffect(() => {
    
    PlanDAO.getActivePlan().then(plan => {
      setActivePlan(plan.activePlan);
    }).catch(error => {
      setOffline(isOffline(error));
    });
    
  }, []);

  if(offline) {
    return <OfflinePage />
  }

  function handleActivityDropdown(event) {
    let dropdownElement = event.target;
    while(dropdownElement !== null && !dropdownElement.classList.contains('iconButton')) {
      dropdownElement = dropdownElement.parentElement;
    }
    if(null === dropdownElement) {
      return;
    }

    setLogProgressMsg(undefined);
    setStatusLogProgress(undefined);

    if(openedActivity === dropdownElement.id) {
      setOpenedActivity(undefined);
    } else {
      setOpenedActivity(dropdownElement.id);
    }
  }

  function handleEndSession() {
      PlanDAO.endPlan(activePlan).then(() => {
        document.location.href = './'
      }).catch(error => {
        setOffline(isOffline(error));
      })
  }

  function logProgress(event, activity) {
    event.preventDefault();

    const inputField = event.target.querySelector("input");

    setLogProgressMsg(undefined);
    setStatusLogProgress(undefined);


    PlanDAO.logProgress(activePlan, activity.name, Number.parseInt(inputField.value))
      .then(activePlan => {
        setLogProgressMsg("Successfully logged progress");
        setStatusLogProgress("success");

        setActivePlan(activePlan);
      })
      .catch(error => {
        setOffline(isOffline(error));

        if(400 === error.code) {
          setLogProgressMsg(error.error);
        } else {
          setLogProgressMsg("Unable to log progress");
        }

        setStatusLogProgress("error");
    });
  }

  function ActiveActivityCard({activity}) {
    const {name, type, progress, progressEstimate} = activity;

    return (
      <Card className="activityCard">
        <Box className="activityHeader">
          {(progress >= progressEstimate) ? <CheckCircleIcon /> : <LoopIcon />}

          <Typography>{name}</Typography>

          <Box className="arrow-dropdown">
            <IconButton id={name} className="iconButton" onClick={handleActivityDropdown}>
              {openedActivity !== name && <ArrowLeftIcon className="left-icon" />}
              {openedActivity === name && <ArrowDropDownIcon className="down-icon" />}
            </IconButton>
          </Box>
        </Box>

        {openedActivity === name && <form className="activityCardInput" onSubmit={(event) => logProgress(event, {name})}>
          <Box>
            <TextField label={"TIMED" === type ? "Duration" : "Repetitions"} required={true} />
            <Typography>Progress: {progress} of {progressEstimate}</Typography>
          </Box>
          <Button type="submit" variant="contained">Log Progress</Button>
          {statusLogProgress && <Alert severity={statusLogProgress}>{logProgressMsg}</Alert>}
        </form>}
      </Card>
    )
  }

  let activityCardItems = [];
  if(activePlan) {
    activityCardItems = activePlan.activities.map((activity, index) => <ActiveActivityCard activity={activity} />);
  }

  return (
    <>
      {activePlan && 
      <Container>
        <h1 className="pageHeader" variant="h1">Active Plan</h1>

        <Box className="activePlanContainer">
          <Card className="section">
            <Typography className="activeSectionHeader" variant="h2">Plan Name: {activePlan.title}</Typography>
          </Card>

          <Box className="activeSection">
            {...activityCardItems}
          </Box>

          <Button className="sessionEnder" variant="contained" onClick={handleEndSession}>
            <SportsScoreIcon />
            <Typography>End Session</Typography>
          </Button>
        </Box>
      </Container>}
    </>
  )
}