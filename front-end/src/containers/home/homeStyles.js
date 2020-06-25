import styled from 'styled-components';

const MainBody = styled.main`
    background-image: linear-gradient(90deg, #C73BA5, #A230BD);
    height:100vh;
`
const LoginContainer = styled.section`
    display: flex;
    justify-content: center;
    height: 100%;
`

const LoginInnerContainer = styled.div`
    width: 24rem;
    border-radius: 1.2rem;
    height: 40rem;
    margin-top: 3rem;
    background: #262651;
    box-shadow: 0 25px 50px 0 rgba(19, 12, 12, 0.3);
`

const LoginHeading = styled.div`
    font-family: 'Ubuntu', sans-serif;
    letter-spacing: 0.1rem;
    color: #fff;
    margin-top: 5rem;
    margin-left: 2rem;
`

const LoginTabContainer = styled.div`
    border-top-right-radius: 1.2rem;
    border-top-left-radius: 1.2rem;
    font-family: 'Ubuntu', sans-serif;
    letter-spacing: 0.1rem;
    color: #fff;
    height: 4rem;
    display: flex;
    justify-content: space-around;
    align-items: center;
    background-image: linear-gradient(-90deg, #853ACA, #A230BD);
`

const LoginOptions =  styled.div`
`

const ImageLogo = styled.section`
    display: flex;
    padding-top: 10rem;
    justify-content: center;
`

const ImageLogoText = styled.p`
    display: flex;
    margin-left: 2rem;
    font-family: 'Ubuntu', sans-serif;
    color: #fff;
    align-items: center;
`

const LoginCredentials = styled.div`
    display: flex;
    margin: 4rem 2rem 0 2rem;
    flex-direction: column;

`

const LoginButton = styled.button`
    background-image: linear-gradient(-90deg, #853ACA, #A230BD);
    border: none;
    font-family: 'Ubuntu', sans-serif;
    color: #fff;
    border-radius: 0.4rem;
    height: 3.3rem;
    width: 8rem;
    margin-top: 5rem;
`

const LoginButtonContainer = styled.div`
    display: flex;
    justify-content: center;
`

export {
    MainBody,
    LoginContainer,
    LoginInnerContainer,
    LoginHeading,
    LoginTabContainer,
    LoginOptions,
    ImageLogo,
    ImageLogoText,
    LoginCredentials,
    LoginButton,
    LoginButtonContainer
}