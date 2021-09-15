import styled from 'styled-components';

export const CartItemStyled = styled.div`
	padding: 5px;
	
	.entries-row {
		min-width: 10vw;
		display: inline-flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
	}
	
	.section {
		min-width: 15vw;
		display: flex;
		flex-direction: column;
		align-items: center;
	}
`;

export const ImageStyled = styled.div<{image: string, width: string, height: string}>`
	margin-right: 5vw;
	background-image: url(${props => props.image});
	background-size: cover;
	width: ${props => props.width};
	height: ${props => props.height};
	border-radius: 6px;
`
