import React, {FC, useEffect, useState} from 'react';
import {v4 as uuidv4} from 'uuid';
import {NavigatePage} from '../components/NavigatePage';
import {Entry, OrderHistoryPageStyled} from './Styles/OrderHistoryPageStyled';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {toJS} from 'mobx';
import {IOrder, ProductDetails} from '../stores/OrdersStore';
import {Col, Row} from 'react-bootstrap';
import {ImageStyled} from '../components/Styles/BasketItemStyled';

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
					<p className="detailsStyle" style={{textAlign: 'center'}}>Order number {index + 1}</p>
					<Col>
						<div className="text-center mb-4">
						</div>
						{prepareProductsSummary(order.products)}
					</Col>
					<Row>
						<Col>
							<div className="text-center mb-3">
								<br/>
							<p className="detailsStyle">Selected CreditCard</p>
								<br/>
							<div className="infoStyle">Owner: {order.creditCard.cardholderName}</div>
							<div className='mb-2 infoStyle'>•••• •••• •••• {order.creditCard.number}</div>
							</div>
						</Col>
						<Col><div className="text-center mt-3 mb-3">
							<p className="detailsStyle">Delivery Address</p>
							<br/>
							<div className="infoStyle">{order.address.firstname} {order.address.lastname}</div>
							<div className="infoStyle">{order.address.address}</div>
							<div className="infoStyle">{order.address.zipcode} {order.address.city}</div>
							<div className='mb-2 infoStyle'>{order.address.country}</div>

						</div></Col>

						<Col>
						{
							order.voucher &&
								<div className="text-center mb-3">
									<p className="detailsStyle">Coupon</p>
									<br/>
									<div className='mb-2 infoStyle' >{order.voucher.code}: -{order.voucher.amount}%</div>
								</div>
						}
						</Col>
							<Col>
								<div className="text-center mb-3">
								<br/>
								<p className="detailsStyle">Total</p>
									<br/>
								<div className='mb-2' style={{color: '#ffffff', fontWeight: 'bold', fontSize: 30}}>{order.payment.amount} PLN</div>
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
						<div className="order-summary-item mt-4 mb-2" id={uuidv4()}>
							<ImageStyled height={'22vh'} width={'35vh'} image={product.imageUrl}/>
							<div style={{color: '#ffffff', fontWeight: 'bold', fontSize: 25}}>{product.name}</div>
							<div style={{color: '#ffffff', fontWeight: 'bold', fontSize: 25}}>Quantity: {product.quantity}</div>
							<div style={{color: '#ffffff', fontWeight: 'bold', fontSize: 25, paddingRight: 80}}>{product.price * product.quantity} PLN</div>
						</div>
						<hr style={{backgroundColor: '#ff2800', width: '94%', marginLeft: '3%'}}/>
				</Row>
			);
		});
	};

	return (
		<NavigatePage>
			<OrderHistoryPageStyled key={uuidv4()}>
				<div className="entries">
					{prepareOrderSummary()}
				</div>
			</OrderHistoryPageStyled>
		</NavigatePage>
	);
}));
