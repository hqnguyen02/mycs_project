import { TextField } from "@mui/material"
import { useState } from "react";


function ProgressMeasure(props) {

    const {
        activity,
        activityMinutes,
        activitySeconds,
        activityReps,   
        setActivityMinutes,
        setActivitySeconds,
        setActivityReps
    } = props

    function updateState(event, setState) {
        const value = Number.parseInt(event.target.value);
        if(Number.isNaN(value)) {
            setState("");
            return
        }

        setState(value);
    }


    if("TIMED" === activity.type) {

        return (
            <>
                <TextField 
                    id="new-activity-minutes"
                    label="Minutes" 
                    type="number"
                    required
                    slotProps={{
                        htmlInput: {
                            min: 0
                        }
                    }}
                    value={activityMinutes}
                    onChange={event => updateState(event, setActivityMinutes)}
                />

                <TextField 
                    id="new-activity-seconds"
                    label="Seconds" 
                    type="number"
                    required
                    slotProps={{
                        htmlInput: {
                            min: 0
                        }
                    }}
                    value={activitySeconds}
                    onChange={event => updateState(event, setActivitySeconds)}
                 />
            </>
        )

    } 

    return (
        <>
            <TextField 
                id="new-activity-reps"
                label="Repetitions" 
                type="number"
                required
                slotProps={{
                    htmlInput: {
                        min: 0
                    }
                }}
                value={activityReps}
                onChange={event => updateState(event, setActivityReps)}
            />
        </>
    )
}

export default ProgressMeasure
