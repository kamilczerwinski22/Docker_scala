import styled from 'styled-components';

export const CreditCardStyledMain = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 15vw;
  height: 90%;
  box-shadow: 0 0 25px gray;
  background-color: #0f0f0f;
  border-radius: 25px;
  padding: 10px 10px 10px 10px;
  
  .cancel {
    display: flex;
    align-self: flex-end;
  }

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
  
  .content {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
  }
  
  .inputInfo {
    font-size: 18px;
    color: #e0dcdc;
  }
  
  input::placeholder {
    text-indent: 10px;
    font-size: small;
  }
`;
