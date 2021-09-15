import React, {FC} from 'react';
import {
	Button,
	DialogContentText,
	Input
} from '@material-ui/core';
import {useHistory} from 'react-router';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import { RegisterStyled } from './RegisterStyled';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';

export const Register: FC<{store?: RootStore}> = inject("store")(observer(({store}) => {
	const history = useHistory();
	const userStore = store?.userStore

	const onSubmit = async (data: {
		email: string,
		password: string,
	}, actions: FormikHelpers<any>) => {
		actions.resetForm();
		await userStore?.authorize()
		history.push('/order')
	};

	return (
		<RegisterStyled>
			<div className='entries'>
				<Formik initialValues={{
					email: '',
					nickname: '',
					password: '',
				}}
				        onSubmit={(values, actions) => onSubmit(values, actions)}>
					{({errors, touched, values, isSubmitting}) => (
						<Form className="registerForm">

							<DialogContentText style={{color: "#fff0f0"}}>Email:</DialogContentText>
							<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#fffff", width: '100%'}} name='email'
							       required
							       error={errors.email && touched.email ? errors.email : null}/>

							<DialogContentText style={{color: "#fff0f0"}}>Nickname:</DialogContentText>
							<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#ffffff", width: '100%'}} name='nickname'
							       required
							       error={errors.nickname && touched.email ? errors.nickname : null}/>

							<DialogContentText style={{color: "#fff0f0"}}>Password:</DialogContentText>
							<Field as={Input} style={{backgroundColor: "#4d4d4d", color: "#fffff", width: '100%'}} name='password'
							       type="password"
							       required
							       error={errors.password && touched.password ? errors.password : null}/>
							<div className="row">
								<Field as={Button} type='submit' disabled={isSubmitting}
								       color="primary">Sign up</Field>
							</div>
						</Form>
					)}
				</Formik>
			</div>
		</RegisterStyled>
	);
}));

