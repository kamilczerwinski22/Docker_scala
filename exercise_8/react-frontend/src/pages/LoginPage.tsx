import React, {FC} from 'react';
import {v4 as uuidv4} from 'uuid';
import { NavigatePage } from '../components/NavigatePage';
import {Login} from '../components/Login';
import { LoginPageStyled } from './Styles/LoginPageStyled';

export const LoginPage: FC = () => {

	return (
		<NavigatePage>
			<LoginPageStyled key={uuidv4()}>
				<Login/>
			</LoginPageStyled>
		</NavigatePage>
	);
};
