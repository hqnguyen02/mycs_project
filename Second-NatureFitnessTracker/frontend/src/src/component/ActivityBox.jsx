import { Card, IconButton, Stack, Typography } from "@mui/material"
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';


function ActivityBox(props) {

    const {
        activity,

        activities,
        setActivities,

        requestEdit,
        requestDelete
    } = props
    const {name, progressEstimate, type} = activity;

    function handleDelete() {
        requestDelete(name, activities);
    }

    function handleEdit() {
        requestEdit(name, progressEstimate, type);
    }

    return (
        <Card>
            <Stack className="activity-box" spacing={5} direction="row">
                <Typography variant="h3">{name}</Typography>
                <Typography>{type === "TIMED" ? "Time" : "Reps"}:  {progressEstimate}</Typography>

                <IconButton>
                    <EditIcon onClick={handleEdit} />
                </IconButton>

                {requestDelete && <IconButton onClick={handleDelete}>
                    <DeleteIcon />
                </IconButton>}
            </Stack>
        </Card>
    )
}

export default ActivityBox;
