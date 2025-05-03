import { createContext, useEffect, useState } from 'react';
import { AppBar } from "@mui/material"
import ProfileMenu from "./ProfileMenu"
import { Outlet, useNavigate } from "react-router"
import AuthDAO from '../dao/AuthDAO'; 
import OfflinePage from './OfflinePage';

// const authContext = createContext({
//     user: undefined
// });

export default function NavBar() {

    let navigate = useNavigate();

    const [currentUser, setCurrentUser] = useState();

    useEffect(() => {
        AuthDAO.getCurrentUser()
            .then(user => {
                console.log("User authenticated:", user);

                if(!currentUser || user.id !== currentUser.id) {
                    setCurrentUser(user);
                }
            })
            .catch(error => {

                if(error.code === 401) {
                    navigate("signin");
                }

                console.error("Error checking user authentication:", error);
                
            });
    }, []);
    
    return (
        <>
            <AppBar>
                <ProfileMenu />
            </AppBar>

            <Outlet context={{
                user: currentUser
            }} />
        </>
    )
}