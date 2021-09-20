import React, {FC, useEffect} from 'react';
import {Redirect, Route, Switch, useHistory, useRouteMatch} from 'react-router';
import {StorePage} from '../pages/StorePage';
import {CartPage} from '../pages/CartPage';
import {OrderPage} from '../pages/OrderPage';
import {LoginPage} from '../pages/LoginPage';
import {RegisterPage} from '../pages/RegisterPage';
import {OrderHistoryPage} from '../pages/OrderHistoryPage';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import Cookies from 'js-cookie';

export const MainPageView = () => {

	return (
		<>
			<Routes/>
		</>
	);
};

export const Routes: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const {path} = useRouteMatch();
	const userStore = store?.userStore;
	const history = useHistory();

	useEffect(() => {
		if (!userStore?.user) {

			const userId = Cookies.get('userId');
			if (userId) {
				(async () => {
					tryFetchUser(userId)
				})();
			} else {
				const url = new URL(window.location.href);
				const userIdParam = url.searchParams.get("user-id");
				if (userIdParam) {
					(async () => {
						tryFetchUser(userIdParam)
					})();
				}
			}

			history.push('/');
		}
	}, []);

	const tryFetchUser = (userId: string) => {
		try {
			userStore?.fetchUser(parseInt(userId));
			Cookies.set('userId', userId)
		} catch (e) {
			console.log(e);
		}
	}

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
