'use client'
import Link from 'next/link'
import authStyles from '@/app/auth/auth.module.css'
import styles from '@/components/general.module.css'
import PasswordField from '@/components/PasswordField'
import { FcGoogle } from 'react-icons/fc'
import { useRouter } from 'next/navigation'
import { FormEvent } from 'react'

function page() {

    const router = useRouter();

    async function onSubmit (event: FormEvent<HTMLFormElement>) {
        event.preventDefault()
        const formData = new FormData(event.currentTarget)
        const data = Object.fromEntries(formData)
        const response = await fetch('http://localhost:8080/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: "include",
            body: JSON.stringify(data)
        })
        router.push('/account/verify-email')
    }

  return (
    <>
        <div className={authStyles.authPage}>
            <div className={authStyles.authCard}>
                <h1>Create your account</h1>
                <form className={ styles.form } onSubmit={onSubmit}>
                    <div>
                        <label htmlFor="email">Email address</label>
                        <input type="email" id="email" name="email" required></input>
                    </div>
                    <PasswordField />
                    <button type="submit" className={styles.customButton}>Register</button>
                </form>
                <p className={ styles.dividedText }>or</p>
                <button className={ `${ authStyles.thirdPartyButton} ${authStyles.googleButton}`}>
                    <FcGoogle/>
                    <p>Continue with Google</p>
                </button>

                <p className={ styles.secondaryText }>Already have an account?&nbsp;
                    <Link className={ styles.linkText } href="/auth/login">Log in</Link>
                </p>
            
            </div>
   
        </div>
    </>
  )
}

export default page