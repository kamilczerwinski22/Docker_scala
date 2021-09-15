import axios from 'axios';

const HOST = 'http://localhost:9000'

export const listOrders = async (userId: number) => {
	return axios.get(`${HOST}/api/order/list-by-user/${userId}`)
}

export const listOrderProducts = async () => {
	return axios.get(`${HOST}/api/order-product/list`)
}

export const createOrder = async (userId: number, addressId: number, paymentId: number, voucherId?: number) => {
	return axios.post(`${HOST}/api/order/create`, {
		userId: userId,
		paymentId: paymentId,
		addressId: addressId,
		voucherId: voucherId,
	})
}

export const createOrderProduct = async (orderId: number, productId: number, amount: number) => {
	return axios.post(`${HOST}/api/order-product/create`, {
		orderId: orderId,
		productId: productId,
		amount: amount,
	})
}
