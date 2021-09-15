import React, {FC, useEffect, useState} from 'react';
import {ProductListStyled} from './ProductListStyled';
import {Product, ProductProps} from '../Product/Product';
import {GridList, GridListTile} from '@material-ui/core';
import {RootStore} from '../../stores/RootStore';
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
				<GridListTile key={uuidv4()} style={{height: '42vh', minWidth: '25vw'}} cols={1}>
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
				<GridList spacing={10} cols={3}>
					{prepareProducts()}
				</GridList>
			</div>
		</ProductListStyled>
	);
}));
