import React from 'react';
import { Route, BrowserRouter } from 'react-router-dom';
import Home from '../home/home';
import Dashboard from '../dashboard/dashboard';

const App = () => (
  <React.Fragment>
    <div>
      <main>
        <BrowserRouter>
          <Route exact path="/" component={Home} />
          <Route exact path="/dashboard" component={Dashboard} />
        </BrowserRouter>
      </main>
    </div>
  </React.Fragment>
)

export default App
