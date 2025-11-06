import React from 'react'
import Link from 'next/link'
import styles from '@/components/general.module.css'
import authStyles from '@/app/auth/auth.module.css'
import PasswordField from '@/components/PasswordField'
import { FcGoogle } from 'react-icons/fc'

function page() {
  return (
        <>
            <div className={authStyles.authPage}>
                <div className={authStyles.authCard}>
                    <h1>Log in</h1>
                    <form className={styles.form}>
                        <div>
                            <label htmlFor="email">Email</label>
                            <input type="email" id="email" name="email" required></input>
                        </div>
                        <PasswordField />
                        <Link className={`${styles.linkText} ${authStyles.linkText}`} href="/">Forgot password?</Link>
                        <button type="submit" className={styles.customButton}>Log in</button>
                    </form>
                    <p className={ styles.dividedText }>or</p>
                    <button className={ `${ authStyles.thirdPartyButton} ${authStyles.googleButton}`}>
                        <FcGoogle/>
                        <p>Continue with Google</p>
                    </button>
                    <p className={ styles.secondaryText }>Don't have an account?&nbsp;
                        <Link className={ styles.linkText } href="/auth/register">Register</Link>
                    </p>
                </div>
            </div>

        </>
    )
}

export default page