import React, {FC} from 'react';
import {Input} from '@material-ui/core';
import {useHistory} from 'react-router';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import {RegisterStyled} from './Styles/RegisterStyled';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {Button} from 'react-bootstrap';

export const Register: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const history = useHistory();
	const userStore = store?.userStore;

	const onSubmit = async (data: {
		email: string,
		password: string,
		repeatPassword: string,
	}, actions: FormikHelpers<any>) => {

		if (data.password !== data.repeatPassword) {
			alert('Passwords are not the same')
			return
		}

		actions.resetForm();
		try {
			const res = await userStore?.register(data.email, data.password);
			console.log(res)
			history.push('/order');
		} catch (e) {
			console.log(e)
		}
	};

	return (
		<RegisterStyled>
			<div className="registerStyle">
				<Formik initialValues={{
					email: '',
					password: '',
					repeatPassword: ''
				}}
						onSubmit={(values, actions) => onSubmit(values, actions)}>
					{({errors, touched, isSubmitting}) => (
						<Form className="registerForm">
							<p className="inputInfo">Email:</p>
							<Field as={Input} style={{backgroundColor: '#e0dcdc', color: 'black', width: '100%'}}
								   name="email"
								   required
								   error={errors.email && touched.email ? errors.email : null}/>
							<br/><br/>
							<p className="inputInfo">Password:</p>
							<Field as={Input} style={{backgroundColor: '#e0dcdc', color: 'black', width: '100%'}}
								   name="password"
								   type="password"
								   required
								   error={errors.password && touched.password ? errors.password : null}/>
							<br/><br/>
							<p className="inputInfo">Repeat Password:</p>
							<Field as={Input} style={{backgroundColor: '#e0dcdc', color: 'black', width: '100%'}}
								   name="repeatPassword"
								   type="password"
								   required
								   error={errors.repeatPassword && touched.repeatPassword ? errors.repeatPassword : null}/>

							<div className="col">
								<br/>
								<Button variant="danger" type='submit' disabled={isSubmitting}>
									Sign up
								</Button>
							</div>
						</Form>
					)}
				</Formik>
			</div>
		</RegisterStyled>
	);
}));