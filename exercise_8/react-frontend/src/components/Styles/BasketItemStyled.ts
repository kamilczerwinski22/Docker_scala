import styled from 'styled-components';

export const BasketItemStyled = styled.div`
	padding: 5px;

      .btn-danger {
        background-color: #ff2800;
        color: #e0dcdc;
        font-weight: bold;
        font-size: 20px;
      }
  
	.entries-row {
		min-width: 10vw;
		display: inline-flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
	}
	
	.section {
		min-width: 13vw;
		display: flex;
		flex-direction: column;
		align-items: center;
        font-size: 30px;
        text-align: center;
        font-weight: bold;
        color: #e0dcdc;
        //fontSize: 30;
        //textAlign: 'center';
      //fontWeight: 'bold';
	}
`;

export const ImageStyled = styled.div<{image: string, width: string, height: string}>`
	margin-right: 4vw;
	background-image: url(${props => props.image});
	background-size: cover;
	width: ${props => props.width};
	height: ${props => props.height};
	border-radius: 6px;
`
