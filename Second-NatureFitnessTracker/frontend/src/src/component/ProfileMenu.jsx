import { useEffect, useState } from 'react'
import '../index.css'
import '../ProfileMenu.css'
import { Drawer, IconButton, Menu, MenuItem, Button, Box, Avatar, Typography } from '@mui/material'
import { Link, useNavigate } from 'react-router';
import MenuIcon from '@mui/icons-material/Menu';
import AccountCircleRoundedIcon from '@mui/icons-material/AccountCircleRounded';
import AuthDAO from '../dao/AuthDAO';


export default function ProfileMenu() {
    const navigate = useNavigate();

    const [username, setUsername] = useState("");

    useEffect(() => {
      AuthDAO.getCurrentUser().then(user => {
        if("" === username) {
          setUsername(user.username);
        }
      })
    }, []);

    function MenuButton({to, children, onClick}) {
      if (onClick) {
        return <MenuItem onClick={onClick}>{children}</MenuItem>;
      }
      return (
        <MenuItem><Link to={to}>{children}</Link></MenuItem>
      )
    }

    function SidebarButton({to, children, onClick}) {
      if (onClick) {
        return <Button onClick={onClick}>{children}</Button>;
      }
      return (
        <Button><Link to={to}>{children}</Link></Button>
      )
    }

    const [anchorMenu, setAnchorMenu] = useState(undefined);
  
    function toggleNavMenu(event) {
      const isOpen = undefined !== anchorMenu;
      setAnchorMenu(isOpen ? undefined : event.target);
    }
    
    const handleLogout = async () => {
      toggleNavMenu();
      try {
        await AuthDAO.logOut();
      } catch (error) {
        console.error("Logout failed:", error);
      } finally {
        navigate('/signin', { replace: true });
      }
    };
  
    // Define the profile icon button
    const btnAvatar = 
    <Box className="profileIcon navToggle">
      <IconButton onClick={toggleNavMenu} >
        <AccountCircleRoundedIcon />
      </IconButton>
    </Box>
    const btnAvatarNoNav = 
    <Box className="profileIcon noNavToggle">
      <IconButton>
        <AccountCircleRoundedIcon />
      </IconButton>
    </Box>
  
    return (
      <>
        <Box className="AppbarContent">
          <Typography>{username}</Typography>

          <Box className="navbuttons">
            <Box className="hamburgerIcon" onClick={toggleNavMenu}>
              <IconButton>
                <MenuIcon />
              </IconButton>
            </Box>
            {btnAvatar}
            {btnAvatarNoNav}
          </Box>
        </Box>
        <Menu className="headerNav" open={undefined !== anchorMenu} anchorEl={anchorMenu} onClose={toggleNavMenu}>
          <MenuButton to="/signin">Sign In</MenuButton>
          <MenuButton to="/">Home</MenuButton>
          <MenuButton to="/Plan/Create">Create Exercise Plan</MenuButton>
          <MenuButton to="/log">View Exercise Log</MenuButton>
          <MenuButton onClick={handleLogout}>Log Out</MenuButton>
        </Menu>
        <Drawer className="sideNav" open={undefined !== anchorMenu} onClose={toggleNavMenu}>
          <Avatar>A</Avatar>

          <SidebarButton to="/signin">Sign In</SidebarButton>
          <SidebarButton to="/">Home</SidebarButton>
          <SidebarButton to="/Plan/Create">Create Exercise Plan</SidebarButton>
          <SidebarButton to="/log">View Exercise Log</SidebarButton>
          <SidebarButton onClick={handleLogout}>Log Out</SidebarButton>
        </Drawer>
      </>
    )
  }