import { Card, Container } from "@mui/material"
import OfflineCard from "./GenericOfflineCard"


function OfflinePage() {


  return (
    <>
      <Container>
        <h1 className="pageHeader" variant="h1">You are offline</h1>

        <OfflineCard />
      </Container>
    </>
  )
}

export default OfflinePage
