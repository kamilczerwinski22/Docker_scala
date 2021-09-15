import axios from 'axios';

const HOST = 'http://localhost:9000'

export const addCreditCard = async (userId: number, cardholderName: string, number: string, expDate: string, cvcCode: string) => {
	return axios.post(`${HOST}/api/credit-card/create`, {
		userId: userId,
		cardholderName: cardholderName,
		number: number,
		expDate: expDate,
		cvcCode: cvcCode
	})
}

export const listUserCreditCards = async (userId: number) => {
	return axios.get(`${HOST}/api/credit-card/list-by-user/${userId}`)
}
