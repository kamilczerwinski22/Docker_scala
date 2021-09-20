import styled from 'styled-components';

export const UserAddressStyledMain = styled.div`
	display: flex;
	flex-direction: column;
	justify-content: center;
	width: 15vw;
	height: 90%;
	padding: 10px 10px 10px 10px;
    box-shadow: 0 0 25px gray;
    background-color: #0f0f0f;
    border-radius: 25px;

  .btn-danger {
    background-color: #ff2800;
    color: #e0dcdc;
    font-weight: bold;
    font-size: 20px;
  }

  .btn-secondary {
    background-color: #ff2800;
    color: #e0dcdc;
    font-weight: bold;
    font-size: 10px;
  }
  
	.cancel {
		display: flex;
		align-self: flex-end;
	}

	.content {
		display: flex;
		padding-top: 0.1vh;
		align-items: center;
		justify-content: center;
		flex-direction: column;
	}
  

	input::placeholder {
		text-indent: 10px;
		font-size: medium;
	}

  .inputInfo {
    font-size: 18px;
    color: #e0dcdc;
  }
`;
