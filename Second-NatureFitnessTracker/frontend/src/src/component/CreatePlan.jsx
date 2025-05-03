import { Box, Card, Stack, Container, Button, TextField, Typography } from "@mui/material"
import { useContext, useEffect, useState } from "react";

import ActivityDAO from "../dao/ActivityDAO";
import PlanDAO from "../dao/PlanDAO";
import { redirect, useOutletContext } from "react-router";
import ActivityForm from "./ActivityForm";
import ActivityBox from "./ActivityBox";

import { addItemOffline } from "../db.js";
import { isOffline } from "../offline-util.js";
import OfflinePage from "./OfflinePage.jsx";


function CreatePlanPage() {

    const {user} = useOutletContext();

    const [offline, setOffline] = useState(false);

    const [availableActivities, setAvailableActivities] = useState(new Map());
    const [activities, setActivities] = useState(new Map());

    const [planName, setPlanName] = useState("");

    const [activityName, setActivityName] = useState("");
    const [activityType, setActivityType] = useState(undefined);

    const [activityMinutes, setActivityMinutes] = useState(0);
    const [activitySeconds, setActivitySeconds] = useState(0);
    const [activityReps, setActivityReps] = useState(0);


    useEffect(() => {
        ActivityDAO.getActivities().then(response => {
            const iterables = response.activities.map(activity => {
                return [activity.name, activity];
            });
            const activityMap = new Map(iterables);
            setAvailableActivities(activityMap);
        }).catch(error => {
            setOffline(isOffline(error));
        })
    }, []);

    if(offline) {
        return <OfflinePage />
    }

    function handleAddActivity(event) {
        event.preventDefault();

        const inputFields = event.target.querySelectorAll("input");
        let isFormValid = true;

        inputFields.forEach(field => {
            field.setCustomValidity("");
            const isValid = field.checkValidity();

            if(!isValid) {
                isFormValid = false;
            }
        });

        if(!isFormValid) {
            return;
        }
        
        const activity = availableActivities.get(activityName);
        if("TIMED" === activity.type) {
            const MINUTES_TO_SECONDS = 60;
            activity.progressEstimate = activityMinutes * MINUTES_TO_SECONDS + activitySeconds;
            activity.time = activity.progressEstimate
        } else {
            activity.progressEstimate = activityReps;
        }

        if(!activity.progressEstimate || activity.progressEstimate === 0) {
            return;
        }

        activity.numSets = 3;

        const updatedActivities = new Map(activities.entries().toArray());
        updatedActivities.set(activity.name, activity);

        setActivities(updatedActivities);
    }

    function handlePlanNameChange(event) {
        setPlanName(event.target.value);
    }

    function handleEdit(name, progressEstimate, type) {
        setActivityName(name);
        setActivityType(type);

        if("TIMED" === type) {
            const MINUTES_TO_SECONDS = 60;

            const minutes = Math.floor(progressEstimate / MINUTES_TO_SECONDS);
            const seconds = progressEstimate % MINUTES_TO_SECONDS;

            setActivityMinutes(minutes);
            setActivitySeconds(seconds);

            return;
        }

        setActivityReps(progressEstimate);
    }

    function handleDelete(name) {
        activities.delete(name);
        const activityMap = new Map(activities);

        setActivities(activityMap);
    }

    function handleCreatePlan() {
        const newPlan = {
            title: planName,
            authorId: user.id,
        };

        newPlan.activities = activities
            .values()
            .toArray();

        PlanDAO.createPlan(newPlan).then(() => {

        }).catch(error => {
            setOffline(isOffline(error));
        });
    }
    

  return (
    <>
      <Container>
        <h1 className="pageHeader" variant="h1">Create an Exercise Plan</h1>

        <Box>

            <Card className="GenericContainer">
                <TextField label="Plan Name" value={planName} onChange={handlePlanNameChange} required />

            </Card>

            <Stack spacing={4}>
                

                <Stack spacing={2}>
                    {
                        ...(activities.values().toArray().map(activity => {
                        return <ActivityBox 
                            activity={activity} 

                            setActivities={setActivities}
                            activities={activities} 

                            requestEdit={handleEdit}
                            requestDelete={handleDelete}
                            />
                        }))
                    }
                </Stack>

                <ActivityForm 
                    handleAddActivity={handleAddActivity} 
                    availableActivities={availableActivities}
                    
                    activityName={activityName}
                    activityType={activityType}
                    setActivityName={setActivityName}
                    setActivityType={setActivityType}

                    activityMinutes={activityMinutes}
                    activitySeconds={activitySeconds}
                    activityReps={activityReps}
                    setActivityMinutes={setActivityMinutes}
                    setActivitySeconds={setActivitySeconds}
                    setActivityReps={setActivityReps}
                    />

                <Button onClick={handleCreatePlan} variant="contained">Create Plan</Button>
            </Stack>

        </Box>
      </Container>
    </>
  )
}

export default CreatePlanPage
