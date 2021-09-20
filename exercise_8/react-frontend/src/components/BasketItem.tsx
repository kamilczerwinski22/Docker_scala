import React, {FC} from 'react';
import { BasketItemStyled, ImageStyled } from './Styles/BasketItemStyled';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import { Button } from 'react-bootstrap';

export interface CartItemProps {
	id: number
	image: string
	name: string
	price: number
	quantity: number
	amount: number
}

export const BasketItem: FC<{store?: RootStore} & CartItemProps> = inject("store")(observer(({
	store, id, image, name, price, quantity, amount: sumAmount
}) => {
	const cartStore = store?.cartStore

	return (
		<BasketItemStyled>
			<div className='entries-row mb-4'>
				<ImageStyled height={'27vh'} width={'43vh'} image={image}/>
				<h4 className='section' >{name}</h4>
				<h4 className='section' >{price} PLN</h4>
				<h4 className='section' >Quantity: {quantity}</h4>
				<h4 className='section' >{sumAmount} PLN</h4>
				<Button variant="danger" onClick={() => cartStore?.removeProduct(id)}>
					X
				</Button>
			</div>
			<hr style={{backgroundColor: '#ff2800'}}/>
		</BasketItemStyled>
	);
}));
