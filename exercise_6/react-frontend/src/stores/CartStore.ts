import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {ProductProps} from '../components/Product/Product';

interface IEntry extends ProductProps {
	quantity: number
}

interface ICartStore {
	products: IEntry[]
}

export class CartStore implements ICartStore {
	private rootStore: RootStore | undefined;

	products: IEntry[] = [];

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this)
		this.rootStore = rootStore;
	}

	addProduct = (product: ProductProps) => {
		const entityIdx = this.products.findIndex(entry => entry.id === product.id)
		if (entityIdx >= 0) {
			this.products = [
				...this.products.slice(0, entityIdx),
				...this.products.slice(entityIdx + 1, this.products.length),
				{...product, quantity: this.products[entityIdx].quantity + 1}
			]
		} else {
			this.products = [...this.products, {...product, quantity: 1}]
		}
	}

	removeProduct = (id: number) => {
		const entityIdx = this.products.findIndex(entry => entry.id === id)
		if (entityIdx >= 0) {
			this.products = [
				...this.products.slice(0, entityIdx),
				...this.products.slice(entityIdx + 1, this.products.length),
			]
		}
	}

	listProducts = () => {
		return this.products
	}

	clearProducts = () => {
		this.products = []
		return this.products
	}
}
