import axios from 'axios';

const HOST = 'http://localhost:9000'

export const register = async (email: string, nickname: string, password: string) => {
	return axios.post(`${HOST}/api/user/create`, {
		email: email,
		nickname: nickname,
		password: password
	})
}

export const login = async (email: string, password: string) => {
	return axios.post(`${HOST}/api/user/login`, {
		email: email,
		password: password
	})
}

export const getUser = async (userId: number) => {
	return axios.get(`${HOST}/api/user/get-by-id/${userId}`)
}
