
import React, {FC} from 'react';
import {Input} from '@material-ui/core';
import {Field} from 'formik';

export const EmailPassword: FC<{errors: any, touched: any}> = ({errors, touched}) => {
    return (
        <>
            <p style={{color: '#e0dcdc'}}>Email:</p>
            <Field as={Input} style={{backgroundColor: '#e0dcdc', color: 'black', width: '100%'}}
                   name="email"
                   required
                   error={errors.email && touched.email ? errors.email : null}/>

            <p style={{color: '#e0dcdc'}}>Password:</p>
            <Field as={Input} style={{backgroundColor: '#e0dcdc', color: 'black', width: '100%'}}
                   name="password"
                   type="password"
                   required
                   error={errors.password && touched.password ? errors.password : null}/>
        </>
    )
}