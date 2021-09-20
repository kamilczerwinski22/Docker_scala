import React, {FC} from 'react';
import { CartPageStyled } from './Styles/CartPageStyled';
import {Basket} from '../components/Basket';
import { v4 as uuidv4 } from "uuid";
import { NavigatePage } from '../components/NavigatePage';

export const CartPage: FC = () => {

	return (
		<NavigatePage>
			<CartPageStyled key={uuidv4()}>
				<Basket/>
			</CartPageStyled>
		</NavigatePage>
	);
};
