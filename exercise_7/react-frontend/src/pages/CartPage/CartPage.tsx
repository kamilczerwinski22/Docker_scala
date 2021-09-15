import React, {FC} from 'react';
import { CartPageStyled } from './CartPageStyled';
import {Cart} from '../../components/Cart/Cart';
import { v4 as uuidv4 } from "uuid";
import { Page } from '../../components/Page/Page';

export const CartPage: FC = () => {

	return (
		<Page>
			<CartPageStyled key={uuidv4()}>
				<Cart/>
			</CartPageStyled>
		</Page>
	);
};
