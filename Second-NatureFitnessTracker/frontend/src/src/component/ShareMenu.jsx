import { Container, Typography, TextField, Button, Alert } from "@mui/material"
import { useParams } from "react-router";
import { useState, useEffect } from "react";
import PlanDAO from "../dao/PlanDAO";
import UserDao from "../dao/UserDao";
import PlanInfo from "./PlanInfo";
import OfflinePage from "./OfflinePage"
import { isOffline } from "../offline-util"


export default function ShareMenu() {

    const [offline, setOffline] = useState(false);
    console.log(offline);

    const [plan, setPlan] = useState(undefined);
    const [success, setSuccess] = useState(undefined);

    const {planId} = useParams()

    function handleShare(event) {
        event.preventDefault();

        const inputUsername = event.target.querySelector("#unameRecipient");

        UserDao.sharePlan(inputUsername.value, plan)
            .then(response => {
                setSuccess({
                    status: true,
                    msg: "Successfully shared the exercise plan"
                });
            })
            .catch(error => {
                console.log(error.code);
                const off = isOffline(error);

                console.log(off);

                setOffline(off);
                console.log('not run');

                setSuccess({
                    status: false,
                    msg: "Failed to share the exercise plan"
                });
            });
    }

    useEffect(() => {

        if(!offline) {
            const promisePlan = PlanDAO.getPlan(planId);
            promisePlan.then(plan => {
                setPlan(plan);
            }).catch(error => {
                setOffline(isOffline(error));
            })
        }

    }, []);

    if(offline) {
        console.log('is offline');
        return <OfflinePage />
    }

    function ShareForm() {
        return (
            <form className="flex form" onSubmit={handleShare}>
                <TextField id="unameRecipient" className="forminput" label="Recipient Username" required={true} />
                <Button type="submit" variant="contained">Submit</Button>
            </form>
        )
    }

    return (
        <Container>
            <h1 className="pageHeader">Share an Exercise Plan</h1>

            <Typography variant="h2" gutterBottom={true}>Plan Summary</Typography>
            {undefined !== plan && <PlanInfo plan={plan} />}

            <Typography variant="h2" gutterBottom={true}>Recipient</Typography>
            {undefined !== plan && <ShareForm />}

            {undefined !== success && <Alert severity={success.status ? "success" : "error"}>{success.msg}</Alert>}
        </Container>
    );
}