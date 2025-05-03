import { Box, Card, Stack, Container, Button, TextField, Typography } from "@mui/material"
import { useEffect, useState } from "react";

import ActivityDAO from "../dao/ActivityDAO";
import PlanDAO from "../dao/PlanDAO";
import { useOutletContext, useParams } from "react-router";
import ActivityForm from "./ActivityForm";
import ActivityBox from "./ActivityBox";
import OfflinePage from "./OfflinePage";
import { isOffline } from "../offline-util";

function EditPlanPage() {

    const {user} = useOutletContext();
    const {planId} = useParams()

    const [availableActivities, setAvailableActivities] = useState(new Map());

    const [offline, setOffline] = useState(false);

    const [activities, setActivities] = useState(new Map());

    const [storedActivities, setStoredActivities] = useState(new Map());
    const [addedActivities, setAddedActivities] = useState(new Map());

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

        PlanDAO.getPlan(planId).then(plan => {
            setPlanName(plan.title);

            const activityMap = new Map(plan.activities.map(activity => {
                return [activity.name, activity];
            }));


            setStoredActivities(activityMap);
        }).catch(error => {
            setOffline(isOffline(error));
        });
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

        console.log(storedActivities);
        if(storedActivities.has(activityName)) {
            const updatedActivities = new Map(storedActivities.entries().toArray());
            updatedActivities.set(activity.name, activity);

            setStoredActivities(updatedActivities);
            return;
        }

        const updatedActivities = new Map(addedActivities.entries().toArray());
        updatedActivities.set(activity.name, activity);

        setAddedActivities(updatedActivities);
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

    function handleDelete(name, activityClassMap) {
        activityClassMap.delete(name);
        const activityMap = new Map();

        setActivities(activityMap);
    }

    function handleCreatePlan() {
        const newPlan = {
            title: planName,
            authorId: user.id,
        };

        newPlan.storedActivities = storedActivities
            .values()
            .toArray();

        newPlan.addedActivities = addedActivities
            .values()
            .toArray();

        PlanDAO.editPlan(newPlan).catch(error => {
            setOffline(isOffline(error));
        });
    }
    

  return (
    <>
      <Container>
        <h1 className="pageHeader" variant="h1">Edit an Exercise Plan</h1>

        <Box>

            <Card className="GenericContainer">
                <TextField label="Plan Name" value={planName} onChange={handlePlanNameChange} required disabled />

            </Card>

            <Stack spacing={4}>
                

                <Stack spacing={2}>
                    <Box>
                        <Typography variant="h3">Stored Activities</Typography>
                        <Stack spacing={2}>
                            {
                                ...(storedActivities.values().toArray().map(activity => {
                                return <ActivityBox 
                                    activity={activity} 

                                    setActivities={setStoredActivities}
                                    activities={storedActivities} 

                                    requestEdit={handleEdit}
                                    />
                                }))
                            }
                        </Stack>
                    </Box>

                    <Box>
                        <Typography variant="h3">Added Activities</Typography>

                        <Stack spacing={2}>
                            {
                                ...(addedActivities.values().toArray().map(activity => {
                                return <ActivityBox 
                                    activity={activity} 

                                    setActivities={setAddedActivities}
                                    activities={addedActivities} 

                                    requestEdit={handleEdit}
                                    requestDelete={handleDelete}
                                    />
                                }))
                            }
                        </Stack>
                    </Box>
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

export default EditPlanPage
