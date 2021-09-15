import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import {BrowserRouter} from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.css';
import { Provider } from "mobx-react";
import {RootStore} from './stores/RootStore';

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <Provider store={new RootStore()}>
        <App/>
      </Provider>
    </BrowserRouter>
  </React.StrictMode>,
  document.getElementById('root')
);
