import React, {FC} from 'react';
import {ProductStyled} from './Styles/ProductStyled';
import {Card, CardActions, CardMedia, IconButton} from '@material-ui/core';
import {RootStore} from '../stores/RootStore';
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

	const handleClick = () => {
		cartStore?.addProduct({id: id, name: name, price: price, image: image})
	}

	return (
		<ProductStyled>
			<Card style={{backgroundColor: '#4d4d4d'}}>
				<CardMedia style={{height: 450}}
					image={image}
					title={name}
				/>

				<CardActions disableSpacing style={{height: '5vh'}}>
						<IconButton aria-label="add to chart" onClick={() => handleClick()}>
							<img height='33px' src='images/plus-sign.svg' alt='store'/>
						</IconButton>
					<p style={{marginLeft: '20vw', fontWeight: 'bold', fontSize: 35, paddingTop: 50, color: '#e0dcdc'}}>{price} PLN</p>
				</CardActions>
					<div >
						<h2 style={{marginLeft: '1vw', fontWeight: 'bold', fontSize: 35, color: '#e0dcdc'}}>
							{name}
						</h2>
					</div>
			</Card>
		</ProductStyled>
	);
}));
