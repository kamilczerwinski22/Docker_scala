import styled from 'styled-components';

export const OrderHistoryPageStyled = styled.div`
	display: flex;
	align-items: center;
	justify-content: center;

	.entries {
		margin-top: 50px;
		min-width: 70vw;
		padding: 25px;
        box-shadow: 0 0 15px gray;
        background-color: #ff2800;
        border-radius: 35px;
	}

	.order-summary-item {
		min-width: 2vw;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
	}
  
  .detailsStyle {
    color: white;
    font-weight: bold;
    font-size: 20px;
  }
  
  .infoStyle {
    color: white;
  }
  
`;

export const Entry = styled.div`
	margin-top: 20px;
	min-width: 75vw;
	padding: 20px;
    box-shadow: 0 0 10px black;
    background-color: #4d4d4d;
	border-radius: 25px;
`
