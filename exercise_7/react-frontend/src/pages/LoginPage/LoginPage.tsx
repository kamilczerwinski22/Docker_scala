import React, {FC} from 'react';
import {v4 as uuidv4} from 'uuid';
import { Page } from '../../components/Page/Page';
import {Login} from '../../components/Login/Login';
import { LoginPageStyled } from './LoginPageStyled';

export const LoginPage: FC = () => {

	return (
		<Page>
			<LoginPageStyled key={uuidv4()}>
				<Login/>
			</LoginPageStyled>
		</Page>
	);
};
