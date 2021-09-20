import React, {FC, useEffect, useState} from 'react';
import {ProductListStyled} from './Styles/ProductListStyled';
import {Product, ProductProps} from './Product';
import {GridList, GridListTile} from '@material-ui/core';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {toJS} from 'mobx';
import {v4 as uuidv4} from 'uuid';

export const ProductList: FC<{store?: RootStore}> = inject("store")(observer(({store}) => {
	const productStore = store?.productStore

	const [products, setProducts] = useState<ProductProps[]>()

	useEffect(() => {
		(async () => {
			const result = toJS(await productStore?.listProducts())
			console.log(result)
			setProducts(result)
		})();
	}, [productStore])

	const prepareProducts = () => {
		return products?.map(product => {
			return (
				<GridListTile key={uuidv4()} style={{height: '62vh', minWidth: '38vw'}}>
					<Product
						id={product.id}
						name={product.name}
						price={product.price}
						image={product.image}
					/>
				</GridListTile>
			);
		});
	};

	return (
		<ProductListStyled>
			<div className='mt-5'>
				<GridList spacing={20} cols={2}>
					{prepareProducts()}
				</GridList>
			</div>
		</ProductListStyled>
	);
}));
