import React, {FC} from 'react';
import {v4 as uuidv4} from 'uuid';
import { Page } from '../../components/Page/Page';
import { Register } from '../../components/Register/Register';
import { RegisterPageStyled } from './RegisterPageStyled';

export const RegisterPage: FC = () => {

	return (
		<Page>
			<RegisterPageStyled key={uuidv4()}>
				<Register/>
			</RegisterPageStyled>
		</Page>
	);
};
