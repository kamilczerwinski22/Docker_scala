import React, {FC, useState} from 'react';
import {ProductStyled} from './ProductStyled';
import {Card, CardActions, CardContent, CardMedia, IconButton, Tooltip} from '@material-ui/core';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';

export interface ProductProps {
	id: number
	name: string
	price: number
	image: string
}

export const Product: FC<{store?: RootStore} & ProductProps> = inject("store")(observer(({
	store, id, name, price, image
},) => {

	const cartStore = store?.cartStore
	const [opened, setOpened] = useState<boolean>(false)

	const handleClick = () => {
		// tooltip management
		setOpened(true)
		setTimeout(() => {
			setOpened(false)
		}, 1000)

		cartStore?.addProduct({id: id, name: name, price: price, image: image})
	}

	return (
		<ProductStyled>
			<Card style={{backgroundColor: '#4d4d4d'}}>
				<CardMedia style={{height: 280}}
					image={image}
					title={name}
				/>

				<CardActions disableSpacing style={{height: '2vh'}}>
					<Tooltip title="Added to Chart!" open={opened} onClose={() => setOpened(false)}
							 disableFocusListener
							 disableHoverListener
							 disableTouchListener
					>
						<IconButton aria-label="add to chart" onClick={() => handleClick()}>
							<img height='30px' src='images/plus-sign.svg' alt='store'/>
						</IconButton>
					</Tooltip>
					<div> • • • • {price} PLN</div>
				</CardActions>

				<CardActions aria-label="add to chart" onClick={() => handleClick()}>
					<div>
						<h2>
							{name}
						</h2>
					</div>


				</CardActions>


			</Card>
		</ProductStyled>
	);
}));
