import styled from 'styled-components';

const ImageLogo = styled.section`
    display: flex;
    padding-top: 0rem;
    justify-content: center;
`

const ImageLogoText = styled.p`
    display: flex;
    margin-left: 2rem;
    font-family: 'Ubuntu', sans-serif;
    color: #fff;
    align-items: center;
`
const LogoutLink = styled.div`
    line-height: 2.1;
    color: #007bff;
    padding-right: 0.5rem;
    padding-left: 0.5rem;
    cursor: pointer;
    &:hover{
        text-decoration: underline;
        color: #0056b3;
    }
`

export {
    ImageLogo,
    ImageLogoText,
    LogoutLink
}