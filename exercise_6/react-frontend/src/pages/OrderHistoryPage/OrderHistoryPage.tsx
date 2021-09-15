import React, {FC, useEffect, useState} from 'react';
import {v4 as uuidv4} from 'uuid';
import {Page} from '../../components/Page/Page';
import {Entry, OrderHistoryPageStyled} from './OrderHistoryPageStyled';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {toJS} from 'mobx';
import {IOrder, ProductDetails} from '../../stores/OrdersStore';
import {Col, Row} from 'react-bootstrap';
import {ImageStyled} from '../../components/Cart/CartItem/CartItemStyled';
import {Divider, Typography} from '@material-ui/core';
import HomeIcon from '@material-ui/icons/Home';
import CreditCardIcon from '@material-ui/icons/CreditCard';
import ShoppingBasketIcon from '@material-ui/icons/ShoppingBasket';
import LocalOfferIcon from '@material-ui/icons/LocalOffer';
import AttachMoneyIcon from '@material-ui/icons/AttachMoney';

export const OrderHistoryPage: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const userStore = store?.userStore;
	const ordersStore = store?.orderStore;

	const [orders, setOrders] = useState<IOrder[]>();

	useEffect(() => {
		if (userStore?.user) {
			ordersStore?.refreshOrders(userStore.user.id)
		}
	}, [])

	useEffect(() => {
		(async () => {
			if (userStore?.user) {
				const result = toJS(await ordersStore?.listOrders());
				console.log(result);
				if (result && result?.length > 0) {
					setOrders(result);
				}
			}
		})();
	}, [ordersStore?.orders]);

	const prepareOrderSummary = () => {
		return orders?.map((order, index) => {
			return (

				<Entry>
					<Row>
					<Typography>Order number {index + 1}</Typography>
					<Col>
						<div className="text-center mb-4">
						</div>
						{prepareProductsSummary(order.products)}
					</Col>

					<Row>
						<Col>
							<div className="text-center mb-3">
								<br/>
							<Typography variant="button">Selected CreditCard</Typography>
							<div>Owner: {order.creditCard.cardholderName}</div>
							<div className='mb-2'>•••• •••• •••• {order.creditCard.number}</div>

							</div>
						</Col>


						<Col><div className="text-center mt-3 mb-3">
							<Typography variant="button">Delivery Address</Typography>
							<div>{order.address.firstname} {order.address.lastname}</div>
							<div>{order.address.address}</div>
							<div>{order.address.zipcode} {order.address.city}</div>
							<div className='mb-2'>{order.address.country}</div>

						</div></Col>

						<Col>
						{
							order.voucher &&

								<div className="text-center mb-3">
									<Typography variant="button">Coupon</Typography>
									<br/>
									<div className='mb-2'>{order.voucher.code}: -{order.voucher.amount}%</div>

								</div>


						}
						</Col>
							<Col>
								<div className="text-center mb-3">
								<br/>
								<Typography variant="button">Total</Typography>
								<div className='mb-2'>{order.payment.amount} PLN</div>

								</div>
							</Col>

					</Row>


				</Row>
		</Entry>
			);
		});
	};

	const prepareProductsSummary = (products: ProductDetails[]) => {
		return products?.map((product: ProductDetails) => {
			return (
				<Row>
						<div className="order-summary-item mt-2 mb-2" id={uuidv4()}>
							<ImageStyled height={'24vh'} width={'36vh'} image={product.imageUrl}/>
							<div className="m-4">{product.name}</div>
							<div className="m-4">Quantity: {product.quantity}</div>
							<div className="m-4">{product.price * product.quantity} PLN</div>
						</div>
						<Divider style={{backgroundColor: '#ff2800', width: '94%', marginLeft: '3%'}}/>
				</Row>
			);
		});
	};

	return (
		<Page>
			<OrderHistoryPageStyled key={uuidv4()}>
				<div className="entries">
					{prepareOrderSummary()}
				</div>
			</OrderHistoryPageStyled>
		</Page>
	);
}));
