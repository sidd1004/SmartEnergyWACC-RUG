import React, { Component, Fragment } from 'react';
import axios from 'axios';
import { toast, ToastContainer } from 'react-toastify';
import settings from '../../config/config';

import logo from '../../assets/images/logo-main.png';

import {
  MainBody, LoginContainer, LoginInnerContainer,
  LoginHeading, LoginTabContainer, ImageLogo,
  ImageLogoText, LoginCredentials, LoginButton, LoginButtonContainer
}
  from './homeStyles';

class Home extends Component {

  constructor(props) {
    super(props);
    this.state = {
      username: '',
      password: '',
      showLogin: true
    }
  }

  renderMainLogo = () => {
    return (
      <ImageLogo>
        <img src={logo} alt="" style={{ height: "8.5rem", borderRight: "solid white" }} />
        <ImageLogoText>
          Smart Energy System
          </ImageLogoText>
      </ImageLogo>
    )
  }

  renderLoginButtons = () => {
    return (
      <LoginTabContainer>
        {/* <LoginOptions>
        </LoginOptions> */}
        {/* <LoginOptions>
        </LoginOptions> */}
      </LoginTabContainer>
    )
  }

  renderLoginText = () => {
    return (
      <LoginHeading>
        Hello there, <br /> Welcome back
            </LoginHeading>
    )
  }

  handleUserName = (e) => {
    this.setState({ username: e.target.value });
  }

  handlePassword = (e) => {
    this.setState({ password: e.target.value });
  }

  refreshCredentials = () => {
    this.setState({ username: '' });
    this.setState({ password: '' });
  }


  renderInputFields = () => {
    return (
      <LoginCredentials>
        <input type="text" placeholder="User Name" onChange={(e) => this.handleUserName(e)} style={{ marginBottom: '15px' }} />
        <input type="password" placeholder="Password" onChange={(e) => this.handlePassword(e)} />
      </LoginCredentials>
    )
  }

  handleSignUpClick = () => {
    this.setState({
      showLogin: !this.state.showLogin
    });
  }

  renderActionButton = () => {
    return (
      <LoginButtonContainer>
        <LoginButton onClick={e => this.loginController(e)}>
          Sign In
      </LoginButton>
      </LoginButtonContainer>
    )
  }

  renderSignUpButton = () => {
    return (
      <LoginButtonContainer>
        <div className="sign-up"
          onClick={(e) => this.handleSignUpClick()}
          style={{ color: '#fff', fontSize: '1.2rem', marginTop: '1rem', cursor: 'pointer' }}
        >
          New User?
        </div>
      </LoginButtonContainer>
    )
  }

  loginController = (e) => {
    e.preventDefault();
    this.makeLoginApiCall();
  }

  addUserController = (e) => {
    e.preventDefault();
    this.addLoginApiCall();
  }

  handleNavigation = () => {
    this.props.history.push('/dashboard');
  }

  makeLoginApiCall = () => {
    const { username, password } = this.state;
    const url = `http://${settings.API_URL}/userAuth/${username}/${password}`
    axios.post(url)
      .then((response) => {
          localStorage.setItem('userName', username);
          localStorage.setItem('password', password);
        this.refreshCredentials();
        this.handleNavigation();

      })
      .catch((error) => {
        console.log(error);
        toast.error("Check your credentials and try again!", {
          position: toast.POSITION.BOTTOM_LEFT
        })
      });
  }

  addLoginApiCall = () => {
    const { username, password } = this.state;
    const url = `http://${settings.API_URL}/addUser/${username}/${password}`
    axios.post(url)
      .then((response) => {
        localStorage.setItem('userName', username);
        localStorage.setItem('password', password);
        this.refreshCredentials();
        this.handleNavigation();
      })
      .catch((error) => {
        console.log(error);
        toast.error("Something went wrong, please try again!", {
          position: toast.POSITION.BOTTOM_LEFT
        })
      });
  }

  renderLogin = () => {
    return (
      <Fragment>
        {this.renderLoginText()}
        {this.renderInputFields()}
      </Fragment>
    )
  }

  renderSignUp = () => {
    return (
      <Fragment>
        {this.renderInputFields()}
      </Fragment>
    )
  }

  back = () => {
    this.refreshCredentials();
    this.handleSignUpClick();
  }

  renderAddButton = () => {
    return (
      <LoginButtonContainer>
        <LoginButton onClick={(e) => this.back()}>
          Back
      </LoginButton>
        <LoginButton onClick={e => this.addUserController(e)} style={{ marginLeft: '15px' }}>
          Add User
      </LoginButton>
      </LoginButtonContainer>
    )
  }

  render() {
    const { showLogin } = this.state;
    return (
      <MainBody>
        <ToastContainer></ToastContainer>
        {this.renderMainLogo()}
        <LoginContainer>
          <LoginInnerContainer>
            {this.renderLoginButtons()}
            {showLogin ? this.renderLogin() : null}
            {showLogin ? this.renderActionButton() : null}
            {showLogin ? this.renderSignUpButton() : null}
            {!showLogin ? this.renderSignUp() : null}
            {!showLogin ? this.renderAddButton() : null}
          </LoginInnerContainer>
        </LoginContainer>
      </MainBody>
    )
  }
}

export default Home;