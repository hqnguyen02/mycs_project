import { Box, Card, Stack, Button, TextField, Typography } from "@mui/material"
import ProgressMeasure from "./ProgressMeasure";



function ActivityForm(props) {

    const {handleAddActivity, availableActivities} = props;

    const {
        activityMinutes,
        activitySeconds,
        activityReps,   
        setActivityMinutes,
        setActivitySeconds,
        setActivityReps
    } = props

    const {
        activityName,
        activityType,
        setActivityName,
        setActivityType
    } = props;

    return (
        <Box component="form" noValidate onSubmit={handleAddActivity}>
            <Card className="GenericContainer add-activity">
                <Typography gutterBottom={true} variant="h3">Add an Activity</Typography>

                <Stack spacing={2}>

                    <TextField 
                        id="new-activity-name"
                        label="Activity Name"
                        select={true}
                        required={true}
                        slotProps={{
                            select: {
                                native: true
                            }
                        }}
                        value={activityName}
                        onChange={event => {
                            setActivityName(event.target.value);
                            setActivityType(availableActivities.get(event.target.value).type);
                        }}
                        >
                            <option value=""></option>
                        {
                            ...(availableActivities.values().toArray().map(activity => {
                                return <option value={activity.name}>{activity.name}</option>
                            }))
                        }
                    </TextField>

                    {activityType && <ProgressMeasure 
                    
                    activityMinutes={activityMinutes}
                    activitySeconds={activitySeconds}
                    activityReps={activityReps}
                    setActivityMinutes={setActivityMinutes}
                    setActivitySeconds={setActivitySeconds}
                    setActivityReps={setActivityReps}
                    
                    activity={{
                        name: activityName,
                        type: activityType
                    }} />}

                    <Button type="submit" variant="contained">Add Activity</Button>

                </Stack>
            </Card>
        </Box>
    )
}

export default ActivityForm