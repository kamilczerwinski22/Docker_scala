import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {getUser} from '../services/user';

export interface UserDb {
	id: number,
	email: string,
	nickname: string,
	password: string
}

interface IUserStore {
	user?: UserDb
}

export class UserStore implements IUserStore {
	private rootStore: RootStore | undefined;
	user: UserDb | undefined

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this)
		this.rootStore = rootStore;
	}
	
	authorize = async () => {
		if (this.user === undefined) {
			const user = await getUser(1)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				nickname: user.data.email,
				password: user.data.password
			}
		}
		return this.user
	}
}
