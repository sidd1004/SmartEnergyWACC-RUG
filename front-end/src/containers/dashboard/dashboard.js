import React, { Fragment } from 'react';
import NavBar from '../navbar/navbar';
import { ResponsiveBar } from '@nivo/bar';
import { ResponsivePie } from '@nivo/pie';
// import { ResponsiveLine } from '@nivo/line';
import { Row, Col, ProgressBar } from 'react-bootstrap';
import {
    MainBody, AddAppliance,
    AddApplianceContainer, AddButton, GreenIndicator,RedIndicator
} from './dashboardStyles';
import axios from 'axios';
import GaugeChart from 'react-gauge-chart';
import { toast, ToastContainer } from 'react-toastify';
import settings from '../../config/config';
import DataTable from 'react-data-table-component';
import Slider from 'react-rangeslider'
import 'react-rangeslider/lib/index.css'


class Dashboard extends React.Component {

    constructor(props) {
        super(props);
        const userName = localStorage.userName ? localStorage.userName : 1;
        const password = localStorage.password ? localStorage.password : 1;
        const URL = `ws://${settings.API_URL}/EnergyConsumptionWS/${userName}`;
        const kafkaURL = `ws://${settings.API_URL}/kafka_Actor_stream?topic=testtopic`;
        const tableURL = `ws://${settings.API_URL}/EnergyConsumptionHistoryWS/${userName}/1`;
        this.ws = new WebSocket(URL)
        this.wsKafka = new WebSocket(kafkaURL);
        this.wsTable = new WebSocket(tableURL);
        this.state = {
            energyResult: [],
            socketData: [],
            pieJson: [],
            totalEnergy: [],
            greenEnergyGenerated: [],
            energyIntensity: 0,
            applianceName: '',
            applianceWattage: '',
            tableData: [],
            volume: 20,
            totalEnergyConsumption: 0,
            totalEnergyGeneration: 0,
            excessEnergy: 0,
            userName: userName,
            password: password
        }
        setInterval(function () {
            if (this.ws.onopen == null) {
                this.ws.onopen = () => {
                    this.ws.send("");
                }
            } else {
                if (this.ws.readyState === 1) {
                    this.ws.send("");
                }
            }
        }.bind(this), 2000);

        this.ws.onmessage = evt => {
            // on receiving a message, add it to the list of messages
            const message = JSON.parse(evt.data)
            // this.addMessage(message)
            var pieElement = [{
                "id": "refrigeration",
                "label": "Refrigeration",
                "value": message.refrigeration,
                "color": "hsl(219, 70%, 50%)"
            },
            {
                "id": "plugLoads",
                "label": "Plug Loads",
                "value": message.plugLoads,
                "color": "hsl(100, 70%, 50%)"
            },
            {
                "id": "evCharge",
                "label": "EV Charge",
                "value": message.evCharge,
                "color": "hsl(219, 70%, 50%)"
            },
            {
                "id": "others",
                "label": "Others",
                "value": message.others,
                "color": "hsl(126, 70%, 50%)"
            }];

            this.setState({
                pieJson: pieElement
            });

            this.setState({
                totalEnergyConsumption: parseFloat(message.totalEnergyConsumption) ,
                totalEnergyGeneration: parseFloat(message.totalEnergyGeneration),
                excessEnergy: parseFloat(message.excessEnergy)
            })
        }

        // this.ws.onclose = () => {
        //     console.log('disconnected')
        //     this.setState({
        //         ws: new WebSocket(URL),
        //     });
        // }

        setInterval(function () {
            // console.log(this.wsKafka.readyState)

            if (this.wsKafka.onopen == null) {
                this.wsKafka.onopen = () => {
                    this.wsKafka.send("");
                }
            } else {
                if (this.wsKafka.readyState === 1) {
                    this.wsKafka.send("");
                }
            }
        }.bind(this), 4000);


        this.wsKafka.onmessage = evt => {
            // on receiving a message, add it to the list of messages
            if (!evt.data.startsWith("Consumer")) {
                const message = JSON.parse(evt.data);
                // console.log("mess", message);
                const energyIntensityPercent = message.totalEnergyConsumption / 2000;
                localStorage.setItem('kafkaData', message);
                this.setState({
                    energyIntensity: energyIntensityPercent,
                    greenEnergyGenerated: (message.totalEnergyGeneration / 720) * 100,
                    totalEnergy: (message.totalEnergyConsumption / 720) * 100
                });
            }

            // this.wsKafka.close()

        };

        // this.wsKafka.onclose = () => {
        //     console.log('disconnected')
        //     this.setState({
        //         wsKafka: new WebSocket(URL),
        //     })
        // }

        setInterval(function () {
            if (this.wsTable.onopen == null) {
                this.wsTable.onopen = () => {
                    this.wsTable.send("");
                }
            } else {
                if (this.wsTable.readyState === 1) {
                    this.wsTable.send("");
                }
            }
        }.bind(this), 5000);


        this.wsTable.onmessage = evt => {
            // on receiving a message, add it to the list of messagee
            let convertedJSON = '[' + evt.data + ']';
            convertedJSON = convertedJSON.replace(/\^/g, ",");
            convertedJSON = JSON.parse(convertedJSON);
            this.setState({
                tableData: convertedJSON
            });
        }

        // this.wsTable.onclose = () => {
        //     console.log('disconnected')
        //     this.setState({
        //         wsTable: new WebSocket(URL),
        //     })
        // }
    }

    addMessage = message =>
        this.setState(state => ({ messages: [message, ...state.messages] }))

    submitMessage = messageString => {
        // on submitting the ChatInput form, send the message, add it to the list and reset the input
        const message = { name: this.state.name, message: messageString }
        this.ws.send(JSON.stringify(message))
        this.addMessage(message)
    }

    renderPieChart = () => {
        const { pieJson } = this.state;
        if (pieJson) {
            return (
                <ResponsivePie
                    data={this.state.pieJson}
                    margin={{ top: 40, right: 80, bottom: 80, left: 80 }}
                    innerRadius={0.6}
                    padAngle={0.7}
                    cornerRadius={3}
                    colors={{ scheme: 'category10' }}
                    borderWidth={1}
                    borderColor={{ from: 'color', modifiers: [['darker', 0.2]] }}
                    enableRadialLabels={false}
                    radialLabelsSkipAngle={10}
                    radialLabelsTextXOffset={6}
                    radialLabelsTextColor="#333333"
                    radialLabelsLinkOffset={0}
                    radialLabelsLinkDiagonalLength={16}
                    radialLabelsLinkHorizontalLength={24}
                    radialLabelsLinkStrokeWidth={1}
                    radialLabelsLinkColor={{ from: 'color' }}
                    slicesLabelsSkipAngle={10}
                    slicesLabelsTextColor="#333333"
                    animate={true}
                    motionStiffness={90}
                    motionDamping={15}
                    legends={[
                        {
                            anchor: 'bottom',
                            direction: 'row',
                            translateY: 56,
                            itemWidth: 100,
                            itemHeight: 50,
                            itemTextColor: '#000',
                            symbolSize: 20,
                            symbolShape: 'circle',
                            effects: [
                                {
                                    on: 'hover',
                                    style: {
                                        itemTextColor: '#000'
                                    }
                                }
                            ]
                        }
                    ]}
                />
            )
        }
        else {
            return (
                this.renderErrorMessage()
            )
        }

    }

    renderErrorMessage = () => {
        return (
            <div>
                Loading!
            </div>
        )
    }

    renderBarChart = () => {
        const { energyResult } = this.state;
        if (energyResult.length > 0) {
            return (
                <ResponsiveBar
                    data={this.state.energyResult}
                    keys={['watt']}
                    indexBy="appliance"
                    margin={{ top: 50, right: 50, bottom: 50, left: 100 }}
                    padding={0.2}
                    layout="horizontal"
                    colors={{ scheme: 'category10' }}
                    borderRadius={2}
                    borderWidth={2}
                    borderColor={{ from: 'color', modifiers: [['darker', 1.6]] }}
                    axisTop={null}
                    axisRight={null}
                    axisBottom={null}
                    axisLeft={{
                        tickSize: 0,
                        tickPadding: 4,
                        tickRotation: 0,
                        legendPosition: 'middle',
                    }}
                    labelSkipWidth={12}
                    labelSkipHeight={12}
                    labelTextColor={{ from: 'color', modifiers: [['darker', 5.6]] }}
                    animate={true}
                    motionStiffness={90}
                    motionDamping={15}
                />
            )
        }
        else {
            return (
                this.renderErrorMessage()
            )
        }


    }


    handleApplianceName = (e) => {
        this.setState({ applianceName: e.target.value });
    }

    handleApplianceWattage = (e) => {
        this.setState({ applianceWattage: e.target.value });
    }

    resetValues = () => {
        this.setState({
            applianceWattage: '',
            applianceName: ''
        })
    }

    makeAddApplianceCall = (e) => {
        e.preventDefault();
        const { applianceName, applianceWattage } = this.state;
        const url = `${settings.API_URL}/addAppliance/${applianceName}/${applianceWattage}`
        axios.post(url)
            .then((response) => {
                this.resetValues();
                toast.success("Successfully added Appliance !", {
                    position: toast.POSITION.BOTTOM_LEFT
                })
                // console.log(response);
                this.setInitValue();
            })
            .catch((error) => {
                // console.log("I CAME HERE");
                this.resetValues();
                toast.error("Appliance addition failed !", {
                    position: toast.POSITION.BOTTOM_LEFT
                })
                // console.log(error);
                this.setInitValue();
            });
    }

    tradeEnergyCall = (e) => {
        e.preventDefault();
        const { applianceName, applianceWattage } = this.state;
        const url = `http://${settings.API_URL}/setEnergyToTrade/${this.state.userName}/${this.state.password}/${this.state.volume}`
        axios.post(url)
            .then((response) => {
                this.resetValues();
                toast.success("Successfully traded "+ this.state.volume  + "% of excess energy !", {
                    position: toast.POSITION.BOTTOM_LEFT
                })
                this.state.volume = 20;
            })
            .catch((error) => {
                this.resetValues();
                toast.error("Trading energy failed !", {
                    position: toast.POSITION.BOTTOM_LEFT
                })
            });
    }

    handleOnChange = (value) => {
        this.setState({
            volume: value
        })
    }

    renderAddAppliance = () => {
        return (
            <AddApplianceContainer>
                <div style={{ margin: '10px' }}>
                    Add New Appliance
                </div>
                <AddAppliance>
                    <input type="text" placeholder="Name" style={{ margin: '10px' }} onChange={(e) => this.handleApplianceName(e)} />
                    <input type="text" placeholder="Wattage" style={{ margin: '10px' }} onChange={(e) => this.handleApplianceWattage(e)} />
                </AddAppliance>
                <AddButton onClick={e => this.makeAddApplianceCall(e)} style={{ marginLeft: '15px' }}>
                    Add
                </AddButton>
            </AddApplianceContainer>
        )
    }

    renderEnergyIntensity = () => {
        if (this.state.energyIntensity !== 0) {
            return (
                <GaugeChart id="gauge-chart1"
                    nrOfLevels={10}
                    arcPadding={0.1}
                    cornerRadius={3}
                    percent={this.state.energyIntensity}
                />
            )
        }
        else {
            return (
                this.renderErrorMessage()
            )
        }

    }

    renderIndication = () => {
        if (this.state.excessEnergy > 0) {
            return (
                <GreenIndicator style={{ marginLeft: '15px', verticalAlign: 'middle' }}>
                </GreenIndicator>)
        } else {
            return (
                <RedIndicator style={{ marginLeft: '15px', verticalAlign: 'middle' }}>
                </RedIndicator>)            
        }
    }



    render() {
        const columns = [
            {
                name: 'Prosumer Id',
                selector: 'prosumerId'
            },
            {
                name: 'Refrigeration',
                selector: 'refrigeration'
            },
            {
                name: 'Plug Loads',
                selector: 'plugLoads'
            },
            {
                name: 'evCharge',
                selector: 'evCharge'
            },
            {
                name: 'Others',
                selector: 'others'
            },
        ];



        return (
            <Fragment>
                <NavBar></NavBar>
                <MainBody>
                    {/* <Container> */}

                    <Row style={{ height: '10%', position: 'relative', paddingLeft: '75%' }}>
                        <div className='slider' style={{ width: '30%' }}>
                            <Slider
                                min={0}
                                max={100}
                                value={this.state.volume}
                                orientation="horizontal"
                                onChange={this.handleOnChange}
                            />
                            <div className='value'>{this.state.volume}</div>
                        </div>

                        <AddButton onClick={e => this.tradeEnergyCall(e)} style={{ marginLeft: '15px', verticalAlign: 'middle' }}>
                            Trade Energy
                            </AddButton>
                        {this.renderIndication()}
                    </Row>
                    <Row style={{ height: '40%' }}>
                        <Col style={{ height: '100%', width: 'auto' }} className="block-example border border-dark">
                            <h2 className="card-title">Energy Consumption History</h2>
                            <DataTable
                                style={{ height: '80%', background: 'rgba(255,255,255,0.11)', margin: '1.5rem', width: '97%' }}
                                columns={columns}
                                data={this.state.tableData}
                                noHeader
                                responsive
                            />
                        </Col>
                        <Col className="block-example border border-dark" style={{ height: '100%', width: '100%' }}>
                            <h2 className="card-title">Current Appliance Usage</h2>
                            {this.renderPieChart()}
                        </Col>
                    </Row>
                    <Row style={{ height: '40%' }}>
                        <Col className="block-example border border-dark" style={{ height: '100%', width: 'auto' }}>
                            <h2 className="card-title">Energy Intensity</h2>
                            {this.renderEnergyIntensity()}

                        </Col>
                        <Col className="block-example border border-dark" style={{ height: '100%', width: 'auto' }}>
                            <h2 className="card-title">CARBON FOOTPRINT</h2>
                            <Col style={{ height: '50%', width: 'auto' }}>
                                <h3>Total Energy Consumption</h3>
                                <ProgressBar style={{ height: '20%', width: '80%' }} striped variant="success" now={this.state.totalEnergy} />
                            </Col>
                            <Col style={{ height: '50%', width: 'auto' }}>
                                <h3>Total Energy Generation</h3>
                                <ProgressBar style={{ height: '20%', width: '80%' }} striped variant="success" now={this.state.greenEnergyGenerated} />
                            </Col>
                        </Col>
                    </Row>
                    <div>
                        {/*{this.renderAddAppliance()}*/}
                    </div>
                </MainBody>
                <ToastContainer />
            </Fragment>

        );
    }
}

export default Dashboard