import styled from 'styled-components';

const MainBody = styled.main`
    background-image: linear-gradient(90deg, #C73BA5, #A230BD);
    height:100vh;
`
const DashboardContainer = styled.section`
    display: flex;
    justify-content: center;
    height: 100%;
    // margin-top: 5rem;
`
const ChartStyle = {
    height: '100%',
    width: 'auto',
    border: 'darkgreen',
    borderStyle: 'rigid'
};

const AddAppliance = styled.div`
    display: flex;
    font-family: 'Ubuntu', sans-serif;
`

const AddApplianceContainer = styled.div`
    display: flex;
    margin-left: 7.5rem;
    margin-top: 1rem;
    font-family: 'Ubuntu', sans-serif;
`
const AddButton = styled.button`
    background-image: linear-gradient(-90deg, #853ACA, #A230BD);
    border: none;
    font-family: 'Ubuntu', sans-serif;
    color: #fff;
    border-radius: 0.4rem;
    height: 3.3rem;
    width: 20rem;
    margin-top: 1rem;
    border: solid;
`

const RedIndicator = styled.button`
    background-color: #ff0000;
    color: white;
    padding: 20px;
    height: 3.5rem;
    width: 3.5rem;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
    margin: 4px 2px;
    cursor: pointer;
    border-radius: 50%;
    border: solid;
`

const GreenIndicator = styled.button`
    background-color: #00FF00;
    color: white;
    padding: 20px;
    height: 3.5rem;
    width: 3.5rem;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
    margin: 4px 2px;
    cursor: pointer;
    border-radius: 50%;
    border: solid;
`

export {
    MainBody,
    DashboardContainer,
    ChartStyle,
    AddAppliance,
    AddApplianceContainer,
    AddButton,
    GreenIndicator,
    RedIndicator
}