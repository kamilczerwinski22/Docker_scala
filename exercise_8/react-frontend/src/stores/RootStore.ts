import {BasketStore} from './BasketStore';
import {ProductStore} from './ProductStore';
import {AddressStore} from './AddressStore';
import {CreditCardStore} from './CreditCardStore';
import {UserStore} from './UserStore';
import {OrderStore} from './OrdersStore';

export class RootStore {
	cartStore: BasketStore;
	userStore: UserStore;
	orderStore: OrderStore;
	addressStore: AddressStore;
	productStore: ProductStore
	creditCardsStore: CreditCardStore;

	constructor() {
		this.cartStore = new BasketStore(this);
		this.userStore = new UserStore(this);
		this.orderStore = new OrderStore(this);
		this.addressStore = new AddressStore(this);
		this.productStore = new ProductStore(this);
		this.creditCardsStore = new CreditCardStore(this);
	}
}
