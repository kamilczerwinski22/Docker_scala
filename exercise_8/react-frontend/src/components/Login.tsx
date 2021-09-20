import React, {FC} from 'react';
import {Input} from '@material-ui/core';
import {useHistory} from 'react-router';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import {LoginStyled} from './Styles/LoginStyled';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {GoogleLoginButton} from 'react-social-login-buttons';
import {GOOGLE_LOGIN_URL} from '../services/user';
import {EmailPassword} from './EmailPassword';
import {Button} from 'react-bootstrap';

export interface LoginProps {
	//
}

export const Login: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const history = useHistory();
	const userStore = store?.userStore;

	const onSubmit = async (data: {
		email: string,
		password: string,
	}, actions: FormikHelpers<any>) => {
		actions.resetForm();
		try {
			const res = await userStore?.login(data.email, data.password);
			console.log(res)
			history.goBack()
		} catch (e) {
			console.log(e)
		}
	};

	const loginWithGoogle = () => {
		window.location.assign(GOOGLE_LOGIN_URL)
	}

	return (
		<LoginStyled>
			<div className="entries">
				<Formik initialValues={{
					email: '',
					password: ''
				}}
						onSubmit={(values, actions) => onSubmit(values, actions)}>
					{({errors, touched, values, isSubmitting}) => (
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
							<div className="row">
								<Button variant="danger" type='submit' disabled={isSubmitting}>
									Sign in
								</Button>
							</div>
							{/*<br/>*/}
							<div className="col">
								<div className="mt-3 mb-3">
									<GoogleLoginButton
										style={{height: '40px', fontSize: '15px', width: '100%', margin: 0}}
										onClick={() => loginWithGoogle()}/>
								</div>
								<br/>
								<div className='row'>
									<p className="inputInfo">Don't have an account?</p>
									<br/><br/>
									<Button variant="danger" onClick={() => history.push('/register')} disabled={isSubmitting}>
										Sign up
									</Button>
								</div>
							</div>
						</Form>
					)}
				</Formik>
			</div>
		</LoginStyled>
	);
}));