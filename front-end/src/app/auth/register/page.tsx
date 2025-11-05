import React from 'react'
import Link from 'next/link'
import styles from '@/app/auth/auth.module.css'

function page() {
  return (
    <>
        <div className={styles.authPage}>
            <h1>Create your account</h1>
            <form>
                <div>
                    <label htmlFor="email">Email address</label>
                    <input type="email" id="email" name="email" required></input>
                </div>
                <div>
                    <label htmlFor="password">Password</label>
                    <input type="password" id="password" name="password" required></input>
                </div>
                <button type="submit">Register</button>
            </form>
            <p>OR</p>
            <div>
                <button>Continue with Google</button>
            </div>

            <p>Already have an account? <Link href="/auth/login">Log in</Link></p>
            
        </div>
    </>
  )
}

export default page