import axios from 'axios';

const HOST = 'http://localhost:9000'

export const listStocks = async () => {
	return axios.get(`${HOST}/api/product-stock/list`)
}
