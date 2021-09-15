import React, {FC} from 'react';
import { CartItemStyled, ImageStyled } from './CartItemStyled';
import {Button, Divider} from '@material-ui/core';
import {RootStore} from '../../../stores/RootStore';
import {inject, observer} from 'mobx-react';

export interface CartItemProps {
	id: number
	image: string
	name: string
	price: number
	quantity: number
	amount: number
}

export const CartItem: FC<{store?: RootStore} & CartItemProps> = inject("store")(observer(({
	store, id, image, name, price, quantity, amount
}) => {
	const cartStore = store?.cartStore

	return (
		<CartItemStyled>
			<div className='entries-row mb-4'>
				<ImageStyled height={'24vh'} width={'36vh'} image={image}/>
				<h4 className='section'>{name}</h4>
				<h4 className='section'>{price} PLN</h4>
				<h4 className='section'>Quantity: {quantity}</h4>
				<h4 className='section'>{amount} PLN</h4>
				<Button style={{marginRight: 'px'}} variant="contained" color='primary' onClick={() => cartStore?.removeProduct(id)}>
					X
				</Button>
			</div>
			<Divider style={{backgroundColor: '#ff2800'}}/>
		</CartItemStyled>
	);
}));
