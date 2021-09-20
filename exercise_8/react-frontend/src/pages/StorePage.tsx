import React, {FC} from 'react';
import {StorePageStyled} from './Styles/StorePageStyled';
import { ProductList } from '../components/ProductList';
import {v4 as uuidv4} from 'uuid';
import { NavigatePage } from '../components/NavigatePage';

export const StorePage: FC = () => {

	return (
		<NavigatePage>
			<StorePageStyled key={uuidv4()}>
				<ProductList/>
			</StorePageStyled>
		</NavigatePage>
	);
};
