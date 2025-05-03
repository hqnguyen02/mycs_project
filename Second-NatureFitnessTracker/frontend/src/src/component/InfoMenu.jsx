import { Container, Typography, Box } from "@mui/material"
import { useParams } from "react-router";
import { useState, useEffect } from "react";
import PlanDAO from "../dao/PlanDAO";
import { convertTime, formatTime } from "../model/ExercisePlan";
import PlanInfo from "./PlanInfo";
import { isOffline } from "../offline-util";
import OfflinePage from "./OfflinePage";

export default function InfoMenu({retrievePlan}) {

    const [plan, setPlan] = useState(undefined);

    const {planId} = useParams()

    const [offline, setOffline] = useState(false);

    useEffect(() => {

        const promisePlan = PlanDAO.getPlan(planId);
        promisePlan.then(plan => {
            setPlan(plan);
        }).catch(error => {
            setOffline(isOffline(error));
        })

    }, []);

    if(offline) {
        return <OfflinePage />
    }

    function FieldValue({label, value, duration}) {
        const time = convertTime(duration);

        return (
            <>
                <p className="strong">{label}</p>
                <p>{value}</p>
                <p>{formatTime(time)}</p>
            </>
        )
    }

    return (
        <Container>
            <h1 className="pageHeader">Exercise Plan Info</h1>

            <Typography variant="h2" gutterBottom={true}>Plan Summary</Typography>
            {undefined !== plan && <PlanInfo plan={plan} />}
        </Container>
    );
}