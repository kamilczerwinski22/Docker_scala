import {createMuiTheme} from '@material-ui/core';


export const storeTheme = createMuiTheme({
	palette : {
		background : {
			default : "#e0dcdc",
		},
		text : {
			primary: "#ffffff",
		},
		primary : {
			main : "#ff2800",
			contrastText : "#E5E5E5",
		},

		secondary : {
			main : "#0f0f0f",
			contrastText : "#E5E5E5",
		},

		info : {
			light: "#ff2800",
			main : "#0f0f0f",
			contrastText : "#E5E5E5",
		},
	},

	//https://fonts.google.com/specimen/Poppins
	typography : {
		fontFamily : "'Arial', sans-serif",
		h2 : {
			textTransform: "none",
			fontWeight: 400,
		},
		h5 : {
			textTransform: "none",
			fontWeight: 400,
		},
	}

});

export default storeTheme;
