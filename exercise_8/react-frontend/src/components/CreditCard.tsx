import React, {FC} from 'react';
import {Input} from '@material-ui/core';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import { Container } from '@material-ui/core';
import { CreditCardStyledMain } from './Styles/CreditCardStyled';
import {Button, Row} from 'react-bootstrap';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {addCreditCard} from '../services/creditCard';

interface CreditCardModalProps {
	close: any
}

export const CreditCardModal: FC<{ store?: RootStore } & CreditCardModalProps> = inject('store')(observer(({store, close}) => {
	const userStore = store?.userStore
	const cardStore = store?.creditCardsStore

	const onSubmit = async (data: {
		cardholderName: string,
		number: string,
		expDate: string,
		cvcCode: string,
	}, actions: FormikHelpers<any>) => {

		actions.resetForm();
		if (userStore?.user) {
			try {
				const res = await addCreditCard(userStore.user.id, data.cardholderName, data.number, data.expDate, data.cvcCode);
				cardStore?.addCard(res.data)
				close()
			} catch (error) {
				console.log(error)
			}
		}
	};

	return (
		<CreditCardStyledMain>
			<div className="cancel">
				<Button variant="secondary" onClick={() => close()}>
					X
				</Button>
			</div>
			<Container className="content" >
				<Row>
					<Formik initialValues={{
						cardholderName: '',
						number: '',
						expDate: '',
						cvcCode: ''
					}}
					        onSubmit={(values, actions) => onSubmit(values, actions)}>
						{({errors, touched, values, isSubmitting}) => (
							<Form className="registerForm">
								<p className="inputInfo" >Cardholder Name:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='cardholderName'
								       required
								       error={errors.cardholderName && touched.cardholderName ? errors.cardholderName : null}/>
								<br/><br/>
								<p className="inputInfo">Number:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='number'
								       required
								       error={errors.number && touched.number ? errors.number : null}/>
								<br/><br/>
								<p className="inputInfo">Expiration Date:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='expDate'
								       required
								       error={errors.expDate && touched.expDate ? errors.expDate : null}/>
								<br/><br/>
								<p className="inputInfo">CVC Code:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='cvcCode'
								       required
								       error={errors.cvcCode && touched.cvcCode ? errors.cvcCode : null}/>
								<br/><br/>
								<div className="row">
									<Button variant="danger" type='submit' disabled={isSubmitting}>
										Save Credit Card
									</Button>
								</div>
								<br/>
							</Form>
						)}
					</Formik>
				</Row>
			</Container>
		</CreditCardStyledMain>
	);
}));
