import styled from 'styled-components';

export const CreditCardModalStyled = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 15vw;
  height: 90%;
  background-color: #400000;
  border-radius: 4px 4px 4px 4px;
  box-shadow: 0 0 10px grey;
  padding: 10px 10px 10px 10px;
  
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
  
  .inputFieldRow {
    display: flex;
    flex-direction: column;
    padding-top: 1vh;
  }
  
  input::placeholder {
    text-indent: 10px;
    font-size: small;
  }
  
  .field{
    background-color: #495057;
    border-radius: 14px 14px 14px 14px;
  }
  
  .saveChangesBtn{
    display: flex;
    justify-content: flex-end;
    padding-top: 1vh;
  }

`;
