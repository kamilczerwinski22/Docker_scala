import React, {FC, useEffect, useState} from 'react';
import {v4 as uuidv4} from 'uuid';
import {NavigatePage} from '../components/NavigatePage';
import {OrderPageStyled} from './Styles/OrderPageStyled';
import {UserAddressModalMain} from '../components/UserAddress';
import {CreditCardModal} from '../components/CreditCard';
import Popup from 'reactjs-popup';
import IconButton from '@material-ui/core/IconButton';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {ImageStyled} from '../components/Styles/BasketItemStyled';
import {Col, Container, Row, Button} from 'react-bootstrap';
import {Input} from '@material-ui/core';
import {toJS} from 'mobx';
import {AddressDb} from '../stores/AddressStore';
import {CreditCardDb} from '../stores/CreditCardStore';
import {VoucherDb} from '../stores/OrdersStore';
import {getVoucherByCode} from '../services/voucher';
import { createPayment } from '../services/payment';
import {createOrder, createOrderProduct} from '../services/order';
import {useHistory} from 'react-router';


const CreditCardPopupModal: FC = () => (

	<Popup closeOnDocumentClick={false} trigger={
		<IconButton >
			<p  style={{color: '#ffffff', fontSize: 20}}>Add credit Card</p>
		</IconButton>}
	       nested modal>
		{(close: any) => (
			<CreditCardModal close={close}/>
		)}
	</Popup>
);

const UserAddressPopupModal: FC = () => (

	<Popup closeOnDocumentClick={false} trigger={
		<IconButton >
			<p style={{color: '#ffffff', fontSize: 20}}>Add address</p>
		</IconButton>}
	       nested modal>
		{(close: any) => (
			<UserAddressModalMain close={close}/>
		)}
	</Popup>
);

export const OrderPage: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const cartStore = store?.cartStore;
	const userStore = store?.userStore;
	const orderStore = store?.orderStore;
	const addressStore = store?.addressStore;
	const creditCardStore = store?.creditCardsStore;
	const history = useHistory()

	const [code, setCode] = useState('')
	const [voucher, setVoucher] = useState<VoucherDb>()

	const [total, setTotal] = useState(0)
	const [prev, setPrev] = useState(total)

	const [addresses, setAddresses] = useState<AddressDb[]>();
	const [selectedAddress, setSelectedAddress] = useState<AddressDb>();

	const [creditCards, setCreditCards] = useState<CreditCardDb[]>();
	const [selectedCard, setSelectedCard] = useState<CreditCardDb>();

	useEffect(() => {
		(async () => {
			if (userStore?.user) {
				try {
					const result = await getVoucherByCode(code)
					console.log(result);
					setVoucher(result.data);
					if (result.data.amount > 0) {
						setPrev(total)
						setTotal(total - total * result.data.amount * 0.01)
					}
				} catch (e) {
					setVoucher(undefined)
					prev && setTotal(prev)
					console.log(e)
				}
			}
		})();
	}, [code]);

	useEffect(() => {
		(async () => {
			if (userStore?.user) {
				const result = toJS(await addressStore?.listAddresses(userStore.user.id));
				console.log(result);
				setAddresses(result);
				result && setSelectedAddress(result[0]);
			}
		})();
	}, [addressStore?.addresses]);

	useEffect(() => {
		(async () => {
			if (userStore?.user) {
				const res = toJS(await creditCardStore?.listCreditCards(userStore.user.id));
				setCreditCards(res);
				res && setSelectedCard(res[0]);
			}
		})();
	}, [creditCardStore?.card]);

	useEffect(() => {
		(async () => {
			let totalPrice = 0;
			cartStore?.listProducts().forEach(product => totalPrice += product.price * product.quantity);
			setTotal(totalPrice)
		})();
	}, [cartStore?.products]);

	const prepareAddresses = () => {
		return addresses?.map((address: AddressDb) => {
			return (
				<Col id={uuidv4()} style={{
					cursor: 'pointer',
					borderRadius: '25px',
					background: address === selectedAddress ? '#ff2800' : 'inherit'
				}
				} onClick={() => setSelectedAddress(address)}>
					<div className="detailsStyle">{address.firstname} {address.lastname}</div>
					<div className="detailsStyle">{address.address}</div>
					<div className="detailsStyle">{address.zipcode} {address.city}</div>
					<div className="detailsStyle">{address.country}</div>

				</Col>
			);
		});
	};

	const prepareCreditCards = () => {
		return creditCards?.map((card: CreditCardDb) => {
			return (
				<Col id={uuidv4()} style={{
					cursor: 'pointer',
					borderRadius: '25px',
					background: card === selectedCard ? '#ff2800' : 'inherit'
				}
				} onClick={() => setSelectedCard(card)}>
					<div className="detailsStyle">Owner: {card.cardholderName}</div>
					<div className="detailsStyle">{card.number} •••• •••• ••••</div>
				</Col>
			);
		});
	};

	const prepareSummary = () => {
		return cartStore?.listProducts().map(product => {
			return (
				<Row>
					<div className="order-summary-item mt-2 mb-2" id={uuidv4()}>
						<ImageStyled height={'16vh'} width={'24vh'} image={product.image}/>
						<div className="m-4 orderDetails" >{product.name}</div>
						<div className="m-4 orderDetails" >Quantity: {product.quantity}</div>
						<div className="m-4 orderDetails" >{product.price * product.quantity} PLN</div>
					</div>
					<hr style={{backgroundColor: '#ff2800'}}/>
				</Row>
			);
		});
	};

	const onSubmit = async () => {
		if (userStore?.user && selectedCard && selectedAddress) {
			try {
				const payment = await createPayment(userStore.user.id, selectedCard?.id, total);
				const order = await createOrder(userStore.user.id, selectedAddress.id, payment.data.id, voucher?.id)
				await cartStore?.products.forEach(async (product) => {
					const res = await createOrderProduct(order.data.id, product.id, product.quantity)
					console.log(res)
				})
				console.log(order)
				orderStore?.clearOrders()
				orderStore?.refreshOrders(userStore.user.id)
				cartStore?.clearProducts()
				history.push('/history')
			}catch(error){
				console.log(error)
			}
		}
	};

	return (
		<NavigatePage>
			<OrderPageStyled key={uuidv4()}>

				<Container>
					<Row>
						<Col>
							<div className="entries mt-5">
								{prepareSummary()}
							</div>
						</Col>
						<Col className="m-4">
							<div className="entries mt-4">
								<div className="order-container p-1">
									<div className="text-center mb-3">
										<p className="selectStyle">Select Delivery Address</p>
									</div>
									<Row className="text-center">
										{prepareAddresses()}
									</Row>
									<br/>
									<Row className="text-center">
										<UserAddressPopupModal/>
									</Row>
									<hr style={{backgroundColor: '#ff2800'}}/>
								</div>
								<div className="order-container p-1 mt-5">
									<div className="text-center mb-3">
										<p className="selectStyle">Select Credit Card</p>
									</div>
									<Row className="text-center">
										{prepareCreditCards()}
									</Row>
									<br/>
									<Row className="text-center">
										<CreditCardPopupModal/>
									</Row>
								</div>
							</div>
						</Col>

					</Row>
					<Row>
						<div className="entries mt-4" style={{maxWidth: '80vw'}}>
							<Row>
								<Col className='col-10'>
									<p style={{color: '#e0dcdc', fontWeight: 'bold', fontSize: 20}}>Discount Code:</p>
									<Input autoFocus style={{color: 'white'}}  value={code} type='input' onChange={(e) => {
										setCode(e.target.value)
									}}/>
								</Col>
								<Col className='col-2'>
									<h4 style={{color: '#e0dcdc', fontWeight: 'bold', fontSize: 25}}>Total: {total.toFixed(2)} PLN</h4>
									<br/>
									<Button style={{height: '50px', borderRadius: '20px'}} variant="danger" type='submit' onClick={() => onSubmit()}>
										Submit Order
									</Button>
								</Col>
							</Row>
						</div>
					</Row>
				</Container>
			</OrderPageStyled>
		</NavigatePage>
	);
}));
