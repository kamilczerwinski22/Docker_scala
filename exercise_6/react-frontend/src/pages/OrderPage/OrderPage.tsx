import React, {FC, useEffect, useState} from 'react';
import {v4 as uuidv4} from 'uuid';
import {Page} from '../../components/Page/Page';
import {OrderPageStyled} from './OrderPageStyled';
import {UserAddressModal} from '../../components/UserAddress/UserAddress';
import {CreditCardModal} from '../../components/CreditCard/CreditCard';
import Popup from 'reactjs-popup';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import AddIcon from '@material-ui/icons/Add';
import HomeIcon from '@material-ui/icons/Home';
import CreditCardIcon from '@material-ui/icons/CreditCard';
import ShoppingBasketIcon from '@material-ui/icons/ShoppingBasket';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {ImageStyled} from '../../components/Cart/CartItem/CartItemStyled';
import {Col, Container, Row} from 'react-bootstrap';
import {Button, DialogContentText, Divider, Input} from '@material-ui/core';
import {toJS} from 'mobx';
import {AddressDb} from '../../stores/AddressStore';
import {CreditCardDb} from '../../stores/CreditCardStore';
import theme from '../../styles/theme';
import {VoucherDb} from '../../stores/OrdersStore';
import {getVoucherByCode} from '../../services/voucher';
import { createPayment } from '../../services/payment';
import {createOrder, createOrderProduct} from '../../services/order';
import {useHistory} from 'react-router';

const CreditCardPopupModal: FC = () => (

	<Popup closeOnDocumentClick={false} trigger={
		<IconButton style={{color: '#ffffff'}}>
			<Typography variant="button">Add creditcard</Typography>
		</IconButton>}
	       nested modal>
		{(close: any) => (
			<CreditCardModal close={close}/>
		)}
	</Popup>
);

const UserAddressPopupModal: FC = () => (

	<Popup closeOnDocumentClick={false} trigger={
		<IconButton style={{color: '#ffffff'}}>
			<Typography variant="button">Add address</Typography>
		</IconButton>}
	       nested modal>
		{(close: any) => (
			<UserAddressModal close={close}/>
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
				const result = toJS(await creditCardStore?.listCreditCards(userStore.user.id));
				setCreditCards(result);
				result && setSelectedCard(result[0]);
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
					borderRadius: '15px',
					background: address === selectedAddress ? theme.palette.info.light : 'inherit'
				}
				} onClick={() => setSelectedAddress(address)}>
					<div>{address.firstname} {address.lastname}</div>
					<div>{address.address}</div>
					<div>{address.zipcode} {address.city}</div>
					<div>{address.country}</div>

				</Col>
			);
		});
	};

	const prepareCreditCards = () => {
		return creditCards?.map((card: CreditCardDb) => {
			return (
				<Col id={uuidv4()} style={{
					cursor: 'pointer',
					borderRadius: '15px',
					background: card === selectedCard ? theme.palette.info.light : 'inherit'
				}
				} onClick={() => setSelectedCard(card)}>
					<div>Owner: {card.cardholderName}</div>
					<div>{card.number} •••• •••• ••••</div>

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
						<div className="m-4">{product.name}</div>
						<div className="m-4">Quantity: {product.quantity}</div>
						<div className="m-4">SUM:  {product.price * product.quantity} PLN</div>
					</div>
					<Divider style={{backgroundColor: '#ff2800'}}/>
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
		<Page>
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
										<Typography variant="button">Select Delivery Address</Typography>
									</div>
									<Row className="text-center">
										{prepareAddresses()}
									</Row>
									<Row className="text-center">
										<UserAddressPopupModal/>
									</Row>
									<Divider style={{backgroundColor: '#ff2800'}}/>
								</div>
								<div className="order-container p-1 mt-5">
									<div className="text-center mb-3">
										<Typography variant="button">Credit Card</Typography>
									</div>
									<Row className="text-center">
										{prepareCreditCards()}
									</Row>
									<Row className="text-center">
										<CreditCardPopupModal/>
									</Row>
									<Divider style={{backgroundColor: 'ff2800'}}/>
								</div>
							</div>
						</Col>

					</Row>
					<Row>
						<div className="entries mt-4" style={{maxWidth: '80vw'}}>
							<Row>
								<Col className='col-10'>
									<DialogContentText style={{color: '#ffffff'}}>Discount Code:</DialogContentText>
									<Input autoFocus value={code} type='input' onChange={(e) => {
										setCode(e.target.value)
									}}/>
								</Col>
								<Col className='col-2'>
									<Typography>Total: {total.toFixed(2)} PLN</Typography>
									<Button style={{height: '40px', borderRadius: '20px'}} variant="contained" color="primary" onClick={() => onSubmit()}>
										Submit Order
									</Button>
								</Col>
							</Row>
						</div>
					</Row>
				</Container>

			</OrderPageStyled>
		</Page>
	);
}));
