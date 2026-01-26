import React from 'react'
import authStyles from '@/app/auth/auth.module.css'
import styles from '@/components/general.module.css'

function page() {
  return (
    <>
        <div className={ authStyles.authPage }>
            <div className={ `${authStyles.authCard} ${authStyles.authCardWide}` }>
                <h1>Verify your email address</h1>
                <p className={ `${styles.secondaryText} ${authStyles.instruction}` }>
                  We've emailed a 6-digit code to your email address. Please enter the code below.
                </p>
                <form className={ styles.form }>
                  <div>
                    <input type="text" id="otp" name="otp" required></input>
                  </div>
                  <button type="submit" className={styles.customButton}>Continue</button>
                </form>
            
            </div>
        </div>
    </>
  )
}

export default page