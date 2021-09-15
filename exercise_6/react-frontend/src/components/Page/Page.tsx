import {AppBar, IconButton, Toolbar, Typography} from '@material-ui/core';
import React, {FC} from 'react';
import {useHistory} from 'react-router';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';

export const Page: FC<{store?: RootStore}> = inject("store")(observer(({store, children}) => {
	const userStore = store?.userStore
	const history = useHistory()

	return (
		<>
			<AppBar position="static" color='secondary'>
				<Toolbar>
					<IconButton edge="start" onClick={() => history.push('/')}>
						<img height='50px' color='white' src='images/F1logo.svg' alt='store'/>
					</IconButton>
					<Typography style={{flexGrow: 1, fontWeight: 600, fontSize: 30}} color='textPrimary'>
						NotSoOffical Store
					</Typography>
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
								<img height='35px' src='images/order-list.svg' alt='store'/>
							</IconButton>
						</div>
				</Toolbar>
			</AppBar>
			{children}
		</>
	);
}));
