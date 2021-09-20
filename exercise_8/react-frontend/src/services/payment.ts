import axios from 'axios';

const HOST = 'http://localhost:9000'

export const getPayment = async (id: number) => {
	return axios.get(`${HOST}/api/payment/get-by-id/${id}`)
}

export const createPayment = async (userId: number, creditCardId: number, amount: number) => {
	return axios.post(`${HOST}/api/payment/create`, {
		userId: userId,
		creditCardId: creditCardId,
		amount: Math.round(amount * 100),
	})
}
