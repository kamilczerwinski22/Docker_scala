import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {ProductProps} from '../components/Product';
import {listProducts} from '../services/product';
import {listStocks} from '../services/stock';
import {listCategories} from '../services/category';
import {listSubcategories} from '../services/subcategory';

interface ProductDb {
	id: number,
	stockId: number,
	categoryId: number,
	subcategoryId: number,
	name: string,
	imageUrl: string,
	description: string
}

interface StockDb {
	id: number,
	unitPrice: number,
	totalPrice: number,
	totalStock: number
}

interface IProductStore {
	products: ProductProps[]
}

export class ProductStore implements IProductStore {
	private rootStore: RootStore | undefined;

	products: ProductProps[] = [];

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this)
		this.rootStore = rootStore;
	}
	
	listProducts = async () => {
		if (this.products.length === 0) {
			const stockList = await listStocks()
			const productList = await listProducts()
			console.log(productList)
			this.products = productList.data.map((product: ProductDb) => {
				const newProduct: ProductProps = {
					id: product.id,
					name: product.name,
					price: stockList.data.filter((stock: StockDb) => stock.id === product.stockId)[0].totalPrice,
					image: product.imageUrl
				}
				return newProduct
			})
		}
		return this.products
	}
}
