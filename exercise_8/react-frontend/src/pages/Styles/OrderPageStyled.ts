import styled from 'styled-components';

export const OrderPageStyled = styled.div`
	display: flex;
	align-items: center;
	justify-content: center;

	.entries {
		min-width: 30vw;
		padding: 40px;
		box-shadow: 0 0 15px gray;
		background-color: #4d4d4d;
		border-radius: 35px;
	}
	
	.order-summary-item {
		min-width: 10vw;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
	}
  
  .btn-danger {
    background-color: #ff2800;
    color: #e0dcdc;
    font-weight: bold;
    font-size: 20px;
  }

  .selectStyle {
    color: #ffffff;
    //color: #e0dcdc;
    font-size: 20px;
    font-weight: bold;
  }
  
  .detailsStyle {
    color: #ffffff;
  }
  
  .orderDetails {
    font-weight: bold;
    font-size: 25px;
    color: white;
  }

`;
