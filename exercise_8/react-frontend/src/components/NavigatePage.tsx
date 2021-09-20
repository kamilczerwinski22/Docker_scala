import {AppBar, IconButton, Toolbar} from '@material-ui/core';
import React, {FC} from 'react';
import {useHistory} from 'react-router';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {logoutUser} from "../services/user";
import Cookies from "js-cookie";



export const NavigatePage: FC<{store?: RootStore}> = inject("store")(observer(({store, children}) => {
	const userStore = store?.userStore
	const history = useHistory()

	const logout = async () => {
		if (userStore?.user && userStore?.password) {
			try {
				const res = await logoutUser(userStore.user.email, userStore.password)
				console.log(res)
			} catch (e) {
				console.log(e)
			}
		}
		userStore?.clear()
		Cookies.remove('csrfToken')
		Cookies.remove('PLAY_SESSION')
		Cookies.remove('OAuth2State')
		Cookies.remove('authenticator')
		Cookies.remove('userId')
		window.location.reload()
	}

	return (
		<>
			<AppBar position="static" style={{backgroundColor: 'black'}}>
				<Toolbar>
					<IconButton edge="start" onClick={() => history.push('/')}>
						<img height='45px' color='white' src='images/F1logo.svg' alt='store'/>
					</IconButton>
					<p style={{flexGrow: 1, fontWeight: "bold", fontSize: 35, color: '#e0dcdc'}} color='textPrimary'>
						NotSoOffical Store
					</p>
						<div>
							<IconButton color='inherit' onClick={() => history.push('/cart')}>
								<img height='45px' src='images/checkered-flag.svg' alt='store'/>
							</IconButton>
							<IconButton color='inherit' onClick={() => {
								if (!userStore?.user) {
									history.push('/history')
									history.push('/login')
								} else {
									history.push('/history')
								}
							}}>
								<img height='35px' src='images/order-list-white.svg' alt='store'/>
							</IconButton>
							<IconButton color="inherit" onClick={() => logout()}>
								<img height="35px" src="images/logout-svgrepo-com-white2.svg" alt="store"/>
							</IconButton>
						</div>
				</Toolbar>
			</AppBar>
			{children}
		</>
	);
}));
