import React, {FC} from 'react';
import {Input, Container} from '@material-ui/core';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import { UserAddressStyledMain } from './Styles/UserAddressStyled';
import { Row , Button} from 'react-bootstrap';
import {addUserAddress} from '../services/userAddress';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';

interface UserAddressPropsMain {
	close: any
}
export const UserAddressModalMain: FC<{ store?: RootStore } & UserAddressPropsMain> = inject('store')(observer(({store, close}) => {
	const userStore = store?.userStore
	const addressStore = store?.addressStore

	const onSubmit = async (data: {
		firstname: string,
		lastname: string,
		address: string,
		zipcode: string,
		city: string,
		country: string,
	}, actions: FormikHelpers<any>) => {

		actions.resetForm();
		if (userStore?.user) {
			try {
				const res = await addUserAddress(userStore.user.id, data.firstname, data.lastname, data.address, data.zipcode, data.city, data.country);
				addressStore?.addAddress(res.data)
				close()
			}catch(error){
				console.log(error)
			}
		}
	};

	return (
		<UserAddressStyledMain>
			<div className="cancel">
				<Button variant="secondary" onClick={() => close()}>
					X
				</Button>
			</div>
			<Container className="content" >
				<Row>
					<Formik initialValues={{
						firstname: '',
						lastname: '',
						address: '',
						zipcode: '',
						city: '',
						country: '',
					}}
					        onSubmit={(values, actions) => onSubmit(values, actions)}>
						{({errors, touched, values, isSubmitting}) => (
							<Form className="registerForm">

								<p className="inputInfo">Firstname:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='firstname'
								       required
								       error={errors.firstname && touched.firstname ? errors.firstname : null}/>
								<br/><br/>
								<p className="inputInfo" style={{color: "#fff0f0"}}>Lastname:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='lastname'
								       required
								       error={errors.lastname && touched.lastname ? errors.lastname : null}/>
								<br/><br/>
								<p className="inputInfo" style={{color: "#fff0f0"}}>Address:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='address'
								       required
								       error={errors.address && touched.address ? errors.address : null}/>
								<br/><br/>
								<p className="inputInfo" style={{color: "#fff0f0"}}>Zipcode:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='zipcode'
								       required
								       error={errors.zipcode && touched.zipcode ? errors.zipcode : null}/>
								<br/><br/>
								<p className="inputInfo" style={{color: "#fff0f0"}}>City:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='city'
								       required
								       error={errors.city && touched.city ? errors.city : null}/>
								<br/><br/>
								<p className="inputInfo" style={{color: "#fff0f0"}}>Country:</p>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='country'
								       required
								       error={errors.country && touched.country ? errors.country : null}/>
								<br/><br/>
								<div className="row acceptBtn">
									<Button variant="danger" type='submit' disabled={isSubmitting}>
										Save Address
									</Button>
								</div>
								<br/>
							</Form>
						)}
					</Formik>
				</Row>
			</Container>
		</UserAddressStyledMain>
	);
}));
