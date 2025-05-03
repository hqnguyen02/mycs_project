import { Paper, Typography, Box } from "@mui/material"
import { convertTime, formatTime } from "../model/ExercisePlan";



function FieldValue({label, value, duration, type}) {
        const time = convertTime(duration);

        return (
            <>
                <p className="strong">{label}</p>
                <p>{value}</p>
                <p>{"TIMED" === type ? formatTime(time) : "N/A"}</p>
            </>
        )
    }

function FieldList({ plan }) {
    const fieldList = [];

    plan.activities.forEach((activity) => {
        fieldList.push(<FieldValue label="Activity" value={activity.name} duration={activity.time} type={activity.type} />);
    });

    return (
        <>
            <>
                <p className="strong"></p>
                <p className="strong">Activity Name</p>
                <p className="strong">Expected Time</p>
            </>
            {...fieldList}
        </>
    );
}

export default function PlanInfo({plan}) {
    return (
        <Box className="container">
            <Paper>
                <Typography variant="h4" gutterBottom={true} textAlign="center">Plan Name: {plan.title}</Typography>
            </Paper>

            <Paper>
                <div className="info">
                    <FieldList plan={plan} />
                </div>
            </Paper>
        </Box>
    )
}