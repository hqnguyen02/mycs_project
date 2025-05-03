import { Line } from "react-chartjs-2"
import { Alert, Box, Card, MenuItem, TextField, InputLabel, Button, Container } from "@mui/material";
import '../ActivityGraph.css'
import {CategoryScale, Chart as ChartJS, LinearScale, LineElement, PointElement} from "chart.js"
import { useEffect, useState } from "react";
import ActivityDAO from "../dao/ActivityDAO";
import OfflinePage from "./OfflinePage";
import { isOffline } from "../offline-util";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement);

export function ActivityGraph() {

    const [offline, setOffline] = useState(false);

    const [points, setPoints] = useState(undefined);
    const [availableActivities, setAvailableActivities] = useState([]);

    useEffect(() => {
        ActivityDAO.getActivities().then(result => {
            setAvailableActivities(result.activities);
        }).catch(error => {
            setOffline(isOffline(error));
        })
    }, []);

    if(offline) {
        return <OfflinePage />
    }

    function handleSubmit(event) {
        updateGraph();
        event.preventDefault();
    }

    function mapActivities(activities) {
        return activities.map(activity => {
            return (<option value={activity.name}>{activity.name}</option>);
        });
    }

    function updateGraph() {
        const selectedActivityElement = document.querySelector('#activitySelector');
        const activityName = selectedActivityElement.value;

        const startDateValue = document.querySelector('#startDateField').value;
        const endDateValue = document.querySelector('#endDateField').value;

        ActivityDAO.getUserMetricsByActivity(activityName).then((points) => {
            return points.filter(point => {
                const startDate = new Date(startDateValue);
                const endDate = new Date(endDateValue);

                const date = new Date(point.x);

                if(startDate <= date && date <= endDate) {
                    return true;
                }

                return false;
            })
        }).then(points => {
            setPoints(points);
        }).catch(error => {
            setOffline(isOffline(error));
        })

    }

    return (
        <>
            <Container>
                <h1 className="pageHeader" variant="h1">Activity Dashboard</h1>

                <form onSubmit={handleSubmit}>
                    <Box className="gridcontainer">
                        <Card className="activityInput container">
                            <Box className="fieldInput">
                                <InputLabel className="inputlabel">Activity</InputLabel>
                                <Box className="activityInputBox">
                                    <TextField
                                        id="activitySelector"
                                        select={true}
                                        slotProps={{
                                            select: {
                                                native: true
                                            }
                                        }}
                                        required={true}
                                        >
                                            <option value="">Please select an activity</option>
                                            {...mapActivities(availableActivities)}
                                    </TextField>
                                </Box>
                            </Box>
                        </Card>

                        <Card className="datepickerContainer">
                            <Box className="inputRow">
                                <InputLabel className="inputlabel">Start of Period</InputLabel>
                                <TextField
                                    id="startDateField"
                                    variant="outlined"
                                    type="date"
                                    required={true}
                                />
                            </Box>
                            <Box className="inputRow">
                                <InputLabel className="inputlabel">End of Period</InputLabel>
                                <TextField
                                    id="endDateField"
                                    variant="outlined"
                                    type="date"
                                    required={true}
                                />
                            </Box>
                        </Card>

                        <Box className="btnSubmitGraph">
                            <Button type="submit" variant="contained">Display Trend Graph</Button>
                        </Box>

                        {points && 0 !== points.length && <Card className="graph background">
                            <Line
                                className="graph area"
                                data={{
                                    datasets: [{
                                        data: points,
                                        backgroundColor: "#000000",
                                    }]
                                }}
                            />
                        </Card>}
                        {
                            points && 0 === points.length && <Alert severity="info">No data available to graph</Alert>
                        }
                    </Box>
                </form>
            </Container>
        </>
    )
}