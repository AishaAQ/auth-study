'use client'

import React, { useState } from 'react'
import authStyles from '@/app/auth/auth.module.css'
import resetStyles from '@/app/auth/reset-password/reset.module.css'
import styles from '@/components/general.module.css'
import { LuMailCheck} from 'react-icons/lu'
 
function page() {

    const [submitted, setSubmitted] = useState(false);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        setSubmitted(true);
    }

  return (
    <>
        <div className={ authStyles.authPage }>
            <div className={`${authStyles.authCard} ${authStyles.authCardWide}`}>
                
                {
                    !submitted 
                    &&
                    (
                        <>
                            <h1>Forgot your Password?</h1>
                            <p className={ `${styles.secondaryText} ${authStyles.instruction}` }>
                                Enter the email associated with your account, and we'll send you instructions to reset your password.
                            </p>
                            <form className={styles.form} onSubmit={handleSubmit}>
                                <div>
                                    <label htmlFor="email">Email</label>
                                    <input type="email" id="email" name="email" required></input>
                                </div>
                                <button type="submit" className={styles.customButton}>Continue</button>
                            </form>
                        </>
                    )
                }

                {
                    submitted
                    &&
                    (
                        <>
                            <h1>Check your email!</h1>
                            <p className={ `${styles.secondaryText} ${authStyles.instruction}` }>
                                We've sent you an email with instructions to reset your password.
                            </p>
                            <LuMailCheck className={ resetStyles.icon}/>
                        </>
                    )
                }
                

            </div>
        </div>
    </>
  )
}

export default page