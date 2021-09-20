import React from "react";
import {MainPageView} from './components/MainPageView';
import {withRouter} from 'react-router';



const App = () => {

	return (
		<div style={{height: '100%', backgroundColor: '#e0dcdc'}}>
			<MainPageView/>
		</div>
	);
};

export default withRouter(App);
