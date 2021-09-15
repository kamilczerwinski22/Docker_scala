import React, {FC} from 'react';
import { CartStyled } from './CartStyled';
import {CartItem} from './CartItem/CartItem';
import {Button} from '@material-ui/core';
import {inject, observer} from 'mobx-react';
import {RootStore} from '../../stores/RootStore';
import {v4 as uuidv4} from 'uuid';
import {useHistory} from 'react-router';

export const Cart: FC<{store?: RootStore}> = inject("store")(observer(({store}) => {
	const userStore = store?.userStore
	const cartStore = store?.cartStore
	const history = useHistory()

	const prepareProducts = () => {
		return cartStore?.listProducts().map(product => {
			return (
				<CartItem
					key={uuidv4()} id={product.id} image={product.image} name={product.name} price={product.price}
					quantity={product.quantity}	amount={product.price * product.quantity}
				/>
			)
		});
	}

	const calculateTotal = () => {
		let total = 0
		cartStore?.listProducts().forEach(product => total += product.price * product.quantity)
		return total
	}

	const onCompleteClicked = () => {
		if (!userStore?.user) {
			history.push('/order')
			history.push('/login')
		} else {
			history.push('/order')
		}
	}

	return (
		<CartStyled>
			<div className='mt-5 entries'>
				{prepareProducts()}
				<div className='p-4'>
					<h4 className='summary' style={{marginLeft: '55vw', display: 'inline'}}>Total: {calculateTotal().toFixed(2)} PLN</h4>
					<Button style={{height: '40px', borderRadius: '20px'}} variant="contained" color='primary' onClick={() => onCompleteClicked()}>
						Complete Purchase
					</Button>
				</div>
			</div>
		</CartStyled>
	);
}));
