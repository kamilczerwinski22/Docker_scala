import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {listUserAddresses} from '../services/userAddress';

export interface AddressDb {
	id: number,
	userId: number,
	firstname: string,
	lastname: string,
	address: string,
	zipcode: string,
	city: string,
	country: string
}

interface IAddressStore {
	addresses: AddressDb[]
}

export class AddressStore implements IAddressStore {
	private rootStore: RootStore | undefined;

	addresses: AddressDb[] = [];
	loaded: boolean = false

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this)
		this.rootStore = rootStore;
	}
	
	listAddresses = async (userId: number) => {
		if (!this.loaded) {
			const userAddresses = await listUserAddresses(userId)
			this.loaded = true
			this.addresses = userAddresses.data.map((address: AddressDb) => {
				const newAddress: AddressDb = {
					id: address.id,
					userId: address.userId,
					firstname: address.firstname,
					lastname: address.lastname,
					address: address.address,
					zipcode: address.zipcode,
					city: address.city,
					country: address.country
				}
				return newAddress
			})
		}
		return this.addresses
	}

	addAddress = (address: AddressDb) => {
		this.addresses = [...this.addresses, address]
	}
}
