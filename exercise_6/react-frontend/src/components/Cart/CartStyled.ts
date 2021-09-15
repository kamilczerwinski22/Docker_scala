import styled from 'styled-components';

export const CartStyled = styled.div`
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	.entries {
      min-width: 30vw;
      padding: 40px;
      box-shadow: 0 0 15px gray;
      background-color: #4d4d4d;
      border-radius: 35px;
	}
  

	.section {
		min-width: 15vw;
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 0;
	}
	
	.summary {
		padding: 10px;
		text-align: right;
		margin-right: 5vw;
	}
`;
