'use client'
import { FormEvent } from 'react'
import Link from 'next/link'
import styles from '@/components/general.module.css'
import authStyles from '@/app/auth/auth.module.css'
import PasswordField from '@/components/PasswordField'
import { FcGoogle } from 'react-icons/fc'
import { useRouter } from 'next/navigation'

function page() {

    const router = useRouter()

    async function onSubmit (event: FormEvent<HTMLFormElement>) {
        event.preventDefault()
        const formData = new FormData(event.currentTarget)
        const data = Object.fromEntries(formData)
        const response = await fetch('http://localhost:8080/sessions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })

        if (!response.ok) {
            const errorData = await response.json();
            console.error(errorData.error); 
            alert(errorData.error)
        } 
        else {
            // const data = await response.json();
            // console.log(data); 
            router.push('/logged-in')
        }

    }

  return (
        <>
            <div className={authStyles.authPage}>
                <div className={authStyles.authCard}>
                    <h1>Log in</h1>
                    <form className={styles.form} onSubmit={onSubmit}>
                        <div>
                            <label htmlFor="email">Email</label>
                            <input type="email" id="email" name="email" required></input>
                        </div>
                        <PasswordField />
                        <Link className={`${styles.linkText} ${authStyles.linkText}`} href="/auth/reset-password">Forgot password?</Link>
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