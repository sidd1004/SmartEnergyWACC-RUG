import React, { Component } from "react";
import { Link, withRouter } from "react-router-dom";
import { Navbar, Nav } from 'react-bootstrap';

import {
    ImageLogo,
    ImageLogoText,
    LogoutLink
}
    from './navbarStyles';
import logo from '../../assets/images/logo-main.png';

class NavBar extends Component {

    constructor(props) {
        super(props);
        this.handleLogout = this.handleLogout.bind(this);
    }

    handleLogout = (e) => {
        e.preventDefault();
        localStorage.removeItem('userName');
        this.props.history.push('/');
    }
    render() {
        return (
            <React.Fragment>
                <Navbar bg="dark" variant="dark">
                    <Navbar.Brand href="/">
                        <ImageLogo>
                            <img src={logo} alt="" style={{ height: "5rem", borderRight: "solid white" }} />
                            <ImageLogoText>
                                Smart Energy System
                        </ImageLogoText>
                        </ImageLogo>
                    </Navbar.Brand>
                    <Nav className="mr-auto">
                        <Nav.Link ><Link to="/dashboard">DASHBOARD</Link></Nav.Link>

                        {/* <Link to="/">Home</Link> */}
                        {/* <Link to="/dashboard">Dashboard</Link> */}
                        {/* <Nav.Link href="#admin">Admin</Nav.Link> */}
                    </Nav>
                    <LogoutLink onClick={e => this.handleLogout(e)}>
                        LOGOUT
                        </LogoutLink>
                </Navbar>
            </React.Fragment>
        );
    }
}

export default withRouter(NavBar);