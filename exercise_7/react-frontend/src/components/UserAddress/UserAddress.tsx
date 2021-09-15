import React, {FC} from 'react';
import CancelIcon from '@material-ui/icons/Cancel';
import {Button, DialogContentText, Input} from '@material-ui/core';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import Box from "@material-ui/core/Box";
import { Container } from '@material-ui/core';
import { UserAddressModalStyled } from './UserAddressStyled';
import { Row } from 'react-bootstrap';
import {addUserAddress} from '../../services/userAddress';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';

interface UserAddressModalProps {
	close: any
}
export const UserAddressModal: FC<{ store?: RootStore } & UserAddressModalProps> = inject('store')(observer(({store, close}) => {
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
		<UserAddressModalStyled>
			<Box  className="cancel"><CancelIcon style={{color: "#0f0f0f"}} onClick={() => close()}/></Box>
			<Container className="content" >
				<Row style={{alignItems: "center", display:"flex"}}>
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

								<DialogContentText style={{color: "#fff0f0"}}>Firstname:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='firstname'
								       required
								       error={errors.firstname && touched.firstname ? errors.firstname : null}/>

								<DialogContentText style={{color: "#fff0f0"}}>Lastname:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='lastname'
								       required
								       error={errors.lastname && touched.lastname ? errors.lastname : null}/>

								<DialogContentText style={{color: "#fff0f0"}}>Address:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='address'
								       required
								       error={errors.address && touched.address ? errors.address : null}/>

								<DialogContentText style={{color: "#fff0f0"}}>Zipcode:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='zipcode'
								       required
								       error={errors.zipcode && touched.zipcode ? errors.zipcode : null}/>

								<DialogContentText style={{color: "#fff0f0"}}>City:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='city'
								       required
								       error={errors.city && touched.city ? errors.city : null}/>

								<DialogContentText style={{color: "#fff0f0"}}>Country:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff"}} name='country'
								       required
								       error={errors.country && touched.country ? errors.country : null}/>

								<div className="row acceptBtn">
									<Field as={Button} type='submit' disabled={isSubmitting}
									       color="info">Save Address</Field>
								</div>
							</Form>
						)}
					</Formik>
				</Row>
			</Container>
		</UserAddressModalStyled>
	);
}));
