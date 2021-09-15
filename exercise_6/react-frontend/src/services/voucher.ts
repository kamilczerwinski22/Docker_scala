import axios from 'axios';

const HOST = 'http://localhost:9000'

export const getVoucherById = async (id: number) => {
	return axios.get(`${HOST}/api/voucher/get-by-id/${id}`)
}

export const getVoucherByCode = async (code: string) => {
	return axios.get(`${HOST}/api/voucher/get-by-code/${code}`)
}
