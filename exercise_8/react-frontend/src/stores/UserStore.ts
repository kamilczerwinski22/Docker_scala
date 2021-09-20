import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {getUser, loginUser, registerUser} from '../services/user';
import Cookies from 'js-cookie';

export interface UserDb {
	id: number,
	email: string,
	loginInfo: {
		providerId: string,
		providerKey: string,
	},
}

interface IUserStore {
	user?: UserDb
	password?: string
}

export class UserStore implements IUserStore {
	private rootStore: RootStore | undefined;
	user: UserDb | undefined;
	password: string | undefined;

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this);
		this.rootStore = rootStore;
	}

	fetchUser = async (userId: number) => {
		if (!this.user) {
			const user = await getUser(userId);
			console.log(user)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				loginInfo: {
					providerId: user.data.providerID,
					providerKey: user.data.providerKey,
				}
			};
		}
	}

	register = async (email: string, password: string) => {
		if (!this.user) {
			const user = await registerUser(email, password);
			Cookies.set('userId', user.data.id)
			console.log(user)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				loginInfo: {
					providerId: user.data.providerID,
					providerKey: user.data.providerKey,
				}
			};
			this.password = password
		}
		return this.user;
	};

	login = async (email: string, password: string) => {
		if (!this.user) {
			const user = await loginUser(email, password);
			Cookies.set('userId', user.data.id)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				loginInfo: {
					providerId: user.data.providerID,
					providerKey: user.data.providerKey,
				}
			};
			this.password = password
		}
		return this.user;
	};

	clear = () => {
		this.user = undefined
		this.password = undefined
	}
}
