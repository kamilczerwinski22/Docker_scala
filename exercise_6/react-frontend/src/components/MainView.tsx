import React, {FC, useEffect} from 'react';
import {Redirect, Route, Switch, useHistory, useRouteMatch} from 'react-router';
import {StorePage} from '../pages/StorePage/StorePage';
import {CartPage} from '../pages/CartPage/CartPage';
import {OrderPage} from '../pages/OrderPage/OrderPage';
import {LoginPage} from '../pages/LoginPage/LoginPage';
import {RegisterPage} from '../pages/RegisterPage/RegisterPage';
import {OrderHistoryPage} from '../pages/OrderHistoryPage/OrderHistoryPage';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';

export const MainView = () => {

	return (
		<>
			<Routes/>
		</>
	);
};

export const Routes: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const {path} = useRouteMatch();
	const userStore = store?.userStore
	const history = useHistory()

	useEffect(() => {
		if (!userStore?.user) {
			history.push('/')
		}
	}, [])

	return (
		<Switch>
			<Route path={path} exact component={StorePage}/>
			<Route path={path + 'cart'} component={CartPage}/>
			<Route path={path + 'order'} component={OrderPage}/>
			<Route path={path + 'login'} component={LoginPage}/>
			<Route path={path + 'history'} component={OrderHistoryPage}/>
			<Route path={path + 'register'} component={RegisterPage}/>

			<Route exact path='*'>
				<Redirect to={{pathname: ''}}/>
			</Route>
		</Switch>
	);
}));
