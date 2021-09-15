import axios from 'axios';

const HOST = 'http://localhost:9000'

export const listSubcategories = async () => {
	return axios.get(`${HOST}/api/subcategory/list`)
}
