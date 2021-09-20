import axios from 'axios';

const HOST = 'http://localhost:9000'

export const GOOGLE_LOGIN_URL = 'http://localhost:9000/authenticate/google';



export const registerUser = async (email: string, password: string) => {
	return axios.post(`${HOST}/api/user/sign-up`, {
		email: email,
		password: password
	});
}

export const loginUser = async (email: string, password: string) => {
	return axios.post(`${HOST}/api/user/sign-in`, {
		email: email,
		password: password
	});
}

export const logoutUser = async (email: string, password: string) => {
	return axios.post(`${HOST}/api/user/sign-out`, {
		email: email,
		password: password
	});
};

export const getUser = async (userId: number) => {
	return axios.get(`${HOST}/api/user/get-by-id/${userId}`);
};

