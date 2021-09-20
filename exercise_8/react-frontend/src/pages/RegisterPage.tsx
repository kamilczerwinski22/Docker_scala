import React, {FC} from 'react';
import {v4 as uuidv4} from 'uuid';
import { NavigatePage } from '../components/NavigatePage';
import { Register } from '../components/Register';
import { RegisterPageStyled } from './Styles/RegisterPageStyled';

export const RegisterPage: FC = () => {

	return (
		<NavigatePage>
			<RegisterPageStyled key={uuidv4()}>
				<Register/>
			</RegisterPageStyled>
		</NavigatePage>
	);
};
